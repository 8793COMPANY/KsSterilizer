package com.corporation8793.kssterilizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SettingUV_Delay extends AppCompatActivity {
    Application application;

    ImageButton setting_done_btn, uv_delay_minus_btn, uv_delay_plus_btn;
    View uv_delay;
    TextView uv_delay_time;

    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_uv_delay);

        application = (Application) getApplication();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        setting_done_btn = findViewById(R.id.setting_done_btn);
        uv_delay_minus_btn = findViewById(R.id.uv_delay_minus_btn);
        uv_delay_plus_btn = findViewById(R.id.uv_delay_plus_btn);
        uv_delay = findViewById(R.id.uv_delay);
        uv_delay_time = findViewById(R.id.uv_delay_time);

        updateDelayView();

        setting_done_btn.setOnClickListener(v -> {
            finish();
        });

        uv_delay_minus_btn.setOnClickListener(v -> {
            if (application.uv_delay_minus()) {
                //
            } else {
                Toast.makeText(this, "1단 밑으로 내릴 수 없습니다", Toast.LENGTH_SHORT).show();
            }
            prevent_duplicateClick();
            updateDelayView();
        });

        uv_delay_plus_btn.setOnClickListener(v -> {
            if (application.uv_delay_plus()) {
                //
            } else {
                Toast.makeText(this, "6단을 초과하여 단계가 초기화됩니다", Toast.LENGTH_SHORT).show();
            }
            prevent_duplicateClick();
            updateDelayView();
        });
    }

    void updateDelayView() {
        switch (application.dm_level) {
            case 0:
                uv_delay.setBackground(getResources().getDrawable(R.drawable.uv_delay_1));
                uv_delay_time.setText("1초");
                application.conversionTime = "000001";
                break;
            case 1:
                uv_delay.setBackground(getResources().getDrawable(R.drawable.uv_delay_2));
                uv_delay_time.setText("3초");
                application.conversionTime = "000003";
                break;
            case 2:
                uv_delay.setBackground(getResources().getDrawable(R.drawable.uv_delay_3));
                uv_delay_time.setText("5초");
                application.conversionTime = "000005";
                break;
            case 3:
                uv_delay.setBackground(getResources().getDrawable(R.drawable.uv_delay_4));
                uv_delay_time.setText("10초");
                application.conversionTime = "000010";
                break;
            case 4:
                uv_delay.setBackground(getResources().getDrawable(R.drawable.uv_delay_5));
                uv_delay_time.setText("15초");
                application.conversionTime = "000015";
                break;
            case 5:
                uv_delay.setBackground(getResources().getDrawable(R.drawable.uv_delay_6));
                uv_delay_time.setText("30초");
                application.conversionTime = "000030";
                break;
        }
        vibrator.vibrate(100);
    }

    void prevent_duplicateClick() {
        uv_delay_minus_btn.setEnabled(false);
        uv_delay_plus_btn.setEnabled(false);

        new Handler().postDelayed(() -> {
            uv_delay_minus_btn.setEnabled(true);
            uv_delay_plus_btn.setEnabled(true);
        }, 4000);
    }
}