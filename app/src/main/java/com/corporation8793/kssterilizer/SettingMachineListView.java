package com.corporation8793.kssterilizer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

public class SettingMachineListView extends AppCompatActivity {
    ListViewAdapter listViewAdapter;

    ImageButton setting_done_btn, setting_check_all;
    Boolean setting_check_all_b = false;
    ListView machine_lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_machine_list_view);

        setting_done_btn = findViewById(R.id.setting_done_btn);
        setting_check_all = findViewById(R.id.setting_check_all);
        machine_lv = findViewById(R.id.machine_lv);

        listViewAdapter = new ListViewAdapter(this);
        machine_lv.setAdapter(listViewAdapter);

        listViewAdapter.addItem("1", false, true);
        listViewAdapter.addItem("2", false, true);
        listViewAdapter.addItem("3", false, true);
        listViewAdapter.addItem("4", false, true);
        listViewAdapter.addItem("5", false, true);

        listViewAdapter.notifyDataSetChanged();

        setting_done_btn.setOnClickListener(v -> {
            // 설정값 저장 //단 OFF 되면 초기화
            finish();
        });

        setting_check_all.setOnClickListener(v -> {
            setting_check_all_b = !setting_check_all_b;

            if(setting_check_all_b) {
                setting_check_all.setBackground(getResources().getDrawable(R.drawable.setting_check_all_on));
                for(int i = 0; i < 5; i++) {
                    ListItem listItem = (ListItem)listViewAdapter.getItem(i);

                    if(!listItem.getEmpty()) {
                        listItem.setMachine_check(true);
                    }
                }
            } else {
                setting_check_all.setBackground(getResources().getDrawable(R.drawable.setting_check_all_off));
                for(int i = 0; i < 5; i++) {
                    ListItem listItem = (ListItem)listViewAdapter.getItem(i);

                    if(!listItem.getEmpty()) {
                        listItem.setMachine_check(false);
                    }
                }
            }

            listViewAdapter.notifyDataSetChanged();
        });
    }
}