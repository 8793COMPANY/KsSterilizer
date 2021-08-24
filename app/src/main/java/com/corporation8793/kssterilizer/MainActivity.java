package com.corporation8793.kssterilizer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //인트로
    int count = 0;
    ProgressBar progressBar;
    Handler handler = new Handler();
    Application application;
    Intent intent;
    int code = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        application = (Application) getApplication();
        getPermission();

        progressBar = findViewById(R.id.progress);
        progressBar.setMax(300);
    }

    Runnable progress_start = new Runnable() {
        @Override
        public void run() {
            if (count >= 300){
                handler.removeCallbacksAndMessages(null);
                switch (code) {
                    case 1:
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        getPermission();
                        break;
                }
            } else {
                count++;
                progressBar.setProgress(count);
                handler.postDelayed(progress_start,10);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(progress_start);
    }

    void getPermission() {
        // Get permission
        String[] permission_list = {
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
        };

        ActivityCompat.requestPermissions(this, permission_list,  1);

        intent = new Intent(application.btAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            code = 1;
        }
    }
}