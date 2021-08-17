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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.corporation8793.kssterilizer.Thread.ConnectedThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainControl extends AppCompatActivity {
    Application application;

    ImageButton mc_power_1, mc_power_2, mc_led_mode, mc_uv_mode, mc_bt_btn, mc_setting_btn;
    LinearLayout mc_timer;
    TextView mc_timer_string;

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

        application = (Application) getApplication();
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
        mc_timer = findViewById(R.id.mc_timer);
        mc_timer_string = findViewById(R.id.mc_timer_string);

        mc_led_mode.setEnabled(false);
        mc_uv_mode.setEnabled(false);

        // 블루투스 버튼 클릭
        mc_bt_btn.setOnClickListener(v -> {
            if (application.btAdapter != null) {
                if(application.connectedThread != null) {
                    application.connectedThread.cancel();
                }
            }
            getPermission();
            application.onBT();
            pairing();
            vibrator.vibrate(100);
        });

        // 세팅 버튼 클릭
        mc_setting_btn.setOnClickListener(v -> {
            if (application.btAdapter != null) {
                if(application.connectedThread != null) {
                    application.uv_ready = false;
                    mc_timer.setVisibility(View.GONE);
                    mc_power_1.setVisibility(View.VISIBLE);

                    if (application.state[0]) {
                        mc_power_1.setBackground(getResources().getDrawable(R.drawable.mc_power_on));
                        mc_power_2.setBackground(getResources().getDrawable(R.drawable.mc_power_off_2));
                        mc_led_mode.setBackground(getResources().getDrawable(R.drawable.mc_led_mode_ready));
                        mc_uv_mode.setBackground(getResources().getDrawable(R.drawable.mc_uv_mode_ready));

                        mc_led_mode.setEnabled(true);
                        mc_uv_mode.setEnabled(true);
                    }
                    vibrator.vibrate(100);
                    Intent intent = new Intent(getApplicationContext(), SettingBranch.class);
                    startActivity(intent);
                } else {
                    vibrator.vibrate(500);
                    Toast.makeText(this, "블루투스 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 파워 버튼 1 클릭
        mc_power_1.setOnClickListener(v -> {
            if (application.btAdapter != null) {
                if(application.connectedThread != null) {
                    application.clickPower();
                    view_clickPower();
                    prevent_duplicateClick();
                } else {
                    vibrator.vibrate(500);
                    Toast.makeText(this, "블루투스 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 파워 버튼 2 클릭
        mc_power_2.setOnClickListener(v -> {
            if (application.btAdapter != null) {
                if(application.connectedThread != null) {
                    if (application.uv_ready) {
                        mc_power_1.setVisibility(View.GONE);
                        mc_timer.setVisibility(View.VISIBLE);
                        vibrator.vibrate(100);
                        application.clickUV();
                        countDown(application.conversionTime);
                    } else {
                        application.clickPower();
                        view_clickPower();
                    }
                    prevent_duplicateClick();
                } else {
                    vibrator.vibrate(500);
                    Toast.makeText(this, "블루투스 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // LED 모드 버튼 클릭
        mc_led_mode.setOnClickListener(v -> {
            application.clickLED();
            view_clickLED();
            application.uv_ready = false;
            prevent_duplicateClick();
        });

        // UV 모드드 버튼 클릭
        mc_uv_mode.setOnClickListener(v -> {
            view_clickUV();
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
        mc_timer.setVisibility(View.GONE);
        mc_power_1.setVisibility(View.VISIBLE);

        if (application.state[0]) {
            mc_power_1.setBackground(getResources().getDrawable(R.drawable.mc_power_on));
            mc_power_2.setBackground(getResources().getDrawable(R.drawable.mc_power_off_2));
            mc_led_mode.setBackground(getResources().getDrawable(R.drawable.mc_led_mode_ready));
            mc_uv_mode.setBackground(getResources().getDrawable(R.drawable.mc_uv_mode_ready));

            mc_led_mode.setEnabled(true);
            mc_uv_mode.setEnabled(true);
        } else {
            mc_power_1.setBackground(getResources().getDrawable(R.drawable.mc_power_off));
            mc_power_2.setBackground(getResources().getDrawable(R.drawable.mc_power_on_2));
            mc_led_mode.setBackground(getResources().getDrawable(R.drawable.mc_led_mode_off));
            mc_uv_mode.setBackground(getResources().getDrawable(R.drawable.mc_uv_mode_off));

            mc_led_mode.setEnabled(false);
            mc_uv_mode.setEnabled(false);
        }
        vibrator.vibrate(500);
    }

    void view_clickLED() {
        Log.e("MainControl", "view_clickLED state: " + application.state[1]);
        Log.e("MainControl", "view_clickLED s1_flag: " + application.s1_flag);
        mc_timer.setVisibility(View.GONE);
        mc_power_1.setVisibility(View.VISIBLE);

        if (application.state[1]) {
            mc_power_1.setEnabled(false);
            mc_power_2.setEnabled(false);

            switch (application.s1_flag) {
                case 1:
                    mc_power_1.setBackground(getResources().getDrawable(R.drawable.mc_power_red));
                    mc_power_2.setBackground(getResources().getDrawable(R.drawable.mc_power_off_2));
                    mc_led_mode.setBackground(getResources().getDrawable(R.drawable.mc_led_mode_red));
                    mc_uv_mode.setBackground(getResources().getDrawable(R.drawable.mc_uv_mode_ready));
                    break;
                case 2:
                    mc_power_1.setBackground(getResources().getDrawable(R.drawable.mc_power_yellow));
                    mc_power_2.setBackground(getResources().getDrawable(R.drawable.mc_power_off_2));
                    mc_led_mode.setBackground(getResources().getDrawable(R.drawable.mc_led_mode_yellow));
                    mc_uv_mode.setBackground(getResources().getDrawable(R.drawable.mc_uv_mode_ready));
                    break;
                case 3:
                    mc_power_1.setBackground(getResources().getDrawable(R.drawable.mc_power_green));
                    mc_power_2.setBackground(getResources().getDrawable(R.drawable.mc_power_off_2));
                    mc_led_mode.setBackground(getResources().getDrawable(R.drawable.mc_led_mode_green));
                    mc_uv_mode.setBackground(getResources().getDrawable(R.drawable.mc_uv_mode_ready));
                    break;
            }
        } else {
            mc_power_1.setEnabled(true);
            mc_power_2.setEnabled(true);

            mc_power_1.setBackground(getResources().getDrawable(R.drawable.mc_power_on));
            mc_power_2.setBackground(getResources().getDrawable(R.drawable.mc_power_off_2));
            mc_led_mode.setBackground(getResources().getDrawable(R.drawable.mc_led_mode_ready));
            mc_uv_mode.setBackground(getResources().getDrawable(R.drawable.mc_uv_mode_ready));
        }
        vibrator.vibrate(100);
    }

    void view_clickUV() {
        if (application.state[0]) {
            initClock();
            mc_power_1.setEnabled(false);
            mc_power_2.setEnabled(true);
            mc_power_1.setBackground(getResources().getDrawable(R.drawable.mc_uv_min_circle));
            mc_power_2.setBackground(getResources().getDrawable(R.drawable.mc_uv_btn_on));
            mc_led_mode.setBackground(getResources().getDrawable(R.drawable.mc_led_mode_ready));
            mc_uv_mode.setBackground(getResources().getDrawable(R.drawable.mc_uv_mode_on));
            Toast.makeText(this, "UV 살균이 준비되었습니다", Toast.LENGTH_SHORT).show();
        }
        vibrator.vibrate(100);
    }

    void initClock() {
        application.uv_ready = true;
        String tempString = application.conversionTime;
        String Min = tempString.substring(2, 4);
        String Second = tempString.substring(4, 6);
        mc_timer_string.setText(Min + ":" + Second);
        application.state[2] = false;
        mc_timer.setVisibility(View.VISIBLE);
        mc_power_1.setVisibility(View.GONE);
        mc_power_2.setBackground(getResources().getDrawable(R.drawable.mc_uv_btn_on));
    }

    void countDown(String time) {
        long conversionTime = 0;
        mc_power_2.setBackground(getResources().getDrawable(R.drawable.mc_power_off_2));
        application.uv_ready = false;

        // 1000 단위가 1초
        // 60000 단위가 1분
        // 60000 * 3600 = 1시간

        String getHour = time.substring(0, 2);
        String getMin = time.substring(2, 4);
        String getSecond = time.substring(4, 6);

        // "00"이 아니고, 첫번째 자리가 0 이면 제거
        if (getHour.substring(0, 1) == "0") {
            getHour = getHour.substring(1, 2);
        }

        if (getMin.substring(0, 1) == "0") {
            getMin = getMin.substring(1, 2);
        }

        if (getSecond.substring(0, 1) == "0") {
            getSecond = getSecond.substring(1, 2);
        }

        // 변환시간
        conversionTime = Long.valueOf(getHour) * 1000 * 3600 + Long.valueOf(getMin) * 60 * 1000 + Long.valueOf(getSecond) * 1000;

        // 첫번쨰 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번쨰 인자 : 주기( 1000 = 1초)
        new CountDownTimer(conversionTime, 1000) {
            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {
                Log.e("uv ready bool", "onTick: " + application.uv_ready);
                if (!application.uv_ready) {
                    // 시간단위
                    String hour = String.valueOf(millisUntilFinished / (60 * 60 * 1000));

                    // 분단위
                    long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000)) ;
                    String min = String.valueOf(getMin / (60 * 1000)); // 몫

                    // 초단위
                    String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지

                    // 밀리세컨드 단위
                    String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // 몫

                    // 시간이 한자리면 0을 붙인다
                    if (hour.length() == 1) {
                        hour = "0" + hour;
                    }

                    // 분이 한자리면 0을 붙인다
                    if (min.length() == 1) {
                        min = "0" + min;
                    }

                    // 초가 한자리면 0을 붙인다
                    if (second.length() == 1) {
                        second = "0" + second;
                    }

                    //mc_timer_string.setText(hour + ":" + min + ":" + second);
                    mc_timer_string.setText(min + ":" + second);
                    application.state[2] = true;
                } else {
                    initClock();
                    onFinish();
                }
            }

            // 제한시간 종료시
            public void onFinish() {
                mc_timer_string.setText("00:00");
                application.state[2] = false;

                new Handler().postDelayed(() -> {
                    if (!application.uv_ready) {
                        initClock();
                    }
                }, 2000);
            }
        }.start();
    }
}