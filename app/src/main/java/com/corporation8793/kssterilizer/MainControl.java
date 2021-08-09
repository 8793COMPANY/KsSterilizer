package com.corporation8793.kssterilizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.corporation8793.kssterilizer.Thread.ConnectedThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainControl extends AppCompatActivity {
    Application application = new Application();

    ImageButton mc_power_1, mc_power_2, mc_led_mode, mc_uv_mode, mc_bt_btn, mc_setting_btn;

    AlertDialog.Builder builder;

    ListView pair_lv;

    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;


    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_control);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        getPermission();
        application.onBT();
        pairing();

        mc_bt_btn = findViewById(R.id.mc_bt_btn);
        mc_setting_btn = findViewById(R.id.mc_setting_btn);
        mc_power_1 = findViewById(R.id.mc_power_1);
        mc_power_2 = findViewById(R.id.mc_power_2);
        mc_led_mode = findViewById(R.id.mc_led_mode);
        mc_uv_mode = findViewById(R.id.mc_uv_mode);

        // 블루투스 버튼 클릭
        mc_bt_btn.setOnClickListener(v -> {
            application.connectedThread.cancel();
            getPermission();
            application.onBT();
            pairing();
            vibrator.vibrate(100);
        });

        // 세팅 버튼 클릭
        mc_setting_btn.setOnClickListener(v -> {
            vibrator.vibrate(100);
            Toast.makeText(this, "세팅", Toast.LENGTH_SHORT).show();
        });

        // 파워 버튼 1 클릭
        mc_power_1.setOnClickListener(v -> {
            application.clickPower();
            prevent_duplicateClick();
            view_clickPower();
        });

        // 파워 버튼 2 클릭
        mc_power_2.setOnClickListener(v -> {
            application.clickPower();
            prevent_duplicateClick();
            view_clickPower();
        });

        // LED 모드 버튼 클릭
        mc_led_mode.setOnClickListener(v -> {
            vibrator.vibrate(100);
            Toast.makeText(this, "LED 모드", Toast.LENGTH_SHORT).show();
        });

        // UV 모드드 버튼 클릭
        mc_uv_mode.setOnClickListener(v -> {
            vibrator.vibrate(100);
            Toast.makeText(this, "UV 모드", Toast.LENGTH_SHORT).show();
        });
    }

    void pairing() {
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        //LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View dialogView = inflater.inflate(R.layout.pairing, null);
        builder.setView(dialogView);

        pair_lv = dialogView.findViewById(R.id.pair_lv);

        // show paired devices
        btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        deviceAddressArray = new ArrayList<>();

        btArrayAdapter.clear();
        if(deviceAddressArray!=null && !deviceAddressArray.isEmpty()){ deviceAddressArray.clear(); }
        pairedDevices = application.btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                btArrayAdapter.add(deviceName);
                deviceAddressArray.add(deviceHardwareAddress);
            }
        }

        pair_lv.setAdapter(btArrayAdapter);

        AlertDialog dialog = builder.create();
        dialog.show();

        pair_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"연결중입니다...",Toast.LENGTH_SHORT).show();
                dialog.cancel();

                final String name = btArrayAdapter.getItem(position); // get name
                final String address = deviceAddressArray.get(position); // get address
                boolean flag = true;

                BluetoothDevice device = application.btAdapter.getRemoteDevice(address);
                BluetoothSocket btSocket = null;

                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

                // create & connect socket
                try {
                    btSocket = device.createRfcommSocketToServiceRecord(uuid);
                    btSocket.connect();
                } catch (IOException e) {
                    flag = false;
                    Toast.makeText(getApplicationContext(), "connection failed!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                if(flag){
                    application.connectedThread = new ConnectedThread(btSocket);
                    application.connectedThread.start();

                    new Handler().postDelayed(() -> {
                        //딜레이 후 시작할 코드 작성
                        Toast.makeText(getApplicationContext(), "connected to " + name, Toast.LENGTH_SHORT).show();
                        //connectedThread.write("A");
//                        dialog.cancel();
                    }, 1000);
                }
            }
        });
    }

    void getPermission() {
        // Get permission
        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        ActivityCompat.requestPermissions(this, permission_list,  1);

        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 1);
    }

    void prevent_duplicateClick() {
        mc_power_1.setEnabled(false);
        mc_power_2.setEnabled(false);
        mc_led_mode.setEnabled(false);
        mc_uv_mode.setEnabled(false);

        new Handler().postDelayed(() -> {
            mc_power_1.setEnabled(true);
            mc_power_2.setEnabled(true);
            mc_led_mode.setEnabled(true);
            mc_uv_mode.setEnabled(true);
        }, 2000);
    }

    void view_clickPower() {
        if (application.state[0]) {
            mc_power_1.setBackground(getResources().getDrawable(R.drawable.mc_power_on));
            mc_power_2.setBackground(getResources().getDrawable(R.drawable.mc_power_off_2));
            mc_led_mode.setBackground(getResources().getDrawable(R.drawable.mc_led_mode_ready));
            mc_uv_mode.setBackground(getResources().getDrawable(R.drawable.mc_uv_mode_ready));
        } else {
            mc_power_1.setBackground(getResources().getDrawable(R.drawable.mc_power_off));
            mc_power_2.setBackground(getResources().getDrawable(R.drawable.mc_power_on_2));
            mc_led_mode.setBackground(getResources().getDrawable(R.drawable.mc_led_mode_off));
            mc_uv_mode.setBackground(getResources().getDrawable(R.drawable.mc_uv_mode_off));
        }
        vibrator.vibrate(500);
    }
}