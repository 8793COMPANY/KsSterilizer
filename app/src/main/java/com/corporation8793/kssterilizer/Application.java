package com.corporation8793.kssterilizer;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.corporation8793.kssterilizer.Thread.ConnectedThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Application extends android.app.Application {
    private static Context context;

    public BluetoothAdapter btAdapter;

    public ConnectedThread connectedThread = null;

    public boolean[] state = new boolean[] {
            false, false, false
    };

    public int s1_flag = 0;
    public int dm_level = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    void onBT() {
        // Enable bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            btAdapter.enable();
        }
    }

    void clickPower() {
        if (btAdapter != null) {
            if(connectedThread != null) {
                connectedThread.write("0");
                state[0] = !state[0];
            }
        }
    }

    void clickLED() {
        if(state[0]) {
            if (btAdapter != null) {
                if(connectedThread != null) {
                    connectedThread.write("1");

                    if (s1_flag < 3) {
                        state[1] = true;
                        s1_flag += s1_flag;
                    } else {
                        state[1] = false;
                        s1_flag = 0;
                    }
                }
            }
        }
    }

    void clickUV() {
        if(state[0]) {
            if (btAdapter != null) {
                if(connectedThread != null) {
                    connectedThread.write("2");
                    state[2] = !state[2];
                }
            }
        }
    }

    void uv_delay_plus() {
        if(state[0]) {
            if (btAdapter != null) {
                if(connectedThread != null) {
                    connectedThread.write("3");
                    dm_level += dm_level;
                }
            }
        }
    }

    void uv_delay_minus() {
        if(state[0]) {
            if (btAdapter != null) {
                if(connectedThread != null) {
                    connectedThread.write("4");
                    dm_level -= dm_level;
                }
            }
        }
    }
}
