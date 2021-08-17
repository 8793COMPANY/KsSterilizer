package com.corporation8793.kssterilizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class SettingBranch extends AppCompatActivity {
    ImageButton setting_back_btn, setting_machine, setting_uv_delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_branch);

        setting_back_btn = findViewById(R.id.setting_back_btn);
        setting_machine = findViewById(R.id.setting_machine);
        setting_uv_delay = findViewById(R.id.setting_uv_delay);

        setting_back_btn.setOnClickListener(v -> {
            finish();
        });

        setting_machine.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SettingMachineListView.class);
            startActivity(intent);
        });

        setting_uv_delay.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SettingUV_Delay.class);
            startActivity(intent);
        });
    }
}