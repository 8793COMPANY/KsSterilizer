package com.corporation8793.kssterilizer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

public class SettingMachineListView extends AppCompatActivity {
    ImageButton setting_done_btn, setting_check_all;
    ListView machine_lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_machine_list_view);

        setting_done_btn = findViewById(R.id.setting_done_btn);
        setting_check_all = findViewById(R.id.setting_check_all);
        machine_lv = findViewById(R.id.machine_lv);
    }
}