package com.corporation8793.kssterilizer;

import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ListItem> listItems = new ArrayList<ListItem>();

    public ListViewAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return listItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // item.xml 레이아웃을 inflate해서 참조획득
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item, parent, false);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View dialogView = inflater.inflate(R.layout.add_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        ImageButton mad_close_btn, mad_ok_btn;
        EditText mad_input;

        mad_close_btn = dialogView.findViewById(R.id.mad_close_btn);
        mad_ok_btn = dialogView.findViewById(R.id.mad_ok_btn);
        mad_input = dialogView.findViewById(R.id.mad_input);

        mad_close_btn.setOnClickListener(v -> dialog.dismiss());

        // item.xml 의 참조 획득
        TextView machine_id = (TextView)convertView.findViewById(R.id.machine_id);
        ImageButton machine_check = (ImageButton)convertView.findViewById(R.id.machine_check);
        LinearLayout box = (LinearLayout)convertView.findViewById(R.id.box);

        ListItem listItem = listItems.get(position);

        mad_ok_btn.setOnClickListener(v -> {
            listItem.setMachine_id(mad_input.getText().toString());
            listItem.setMachine_check(false);
            listItem.setEmpty(false);
            notifyDataSetChanged();
            dialog.dismiss();
        });

        // 가져온 데이터를 텍스트뷰에 입력
        if (listItem.getEmpty()) {
            box.setBackground(convertView.getResources().getDrawable(R.drawable.add_machin_box_2));
            machine_id.setVisibility(View.INVISIBLE);
            machine_check.setVisibility(View.INVISIBLE);

            box.setOnClickListener(v -> dialog.show());
        } else {
            box.setBackground(convertView.getResources().getDrawable(R.drawable.add_machine_box));
            machine_id.setVisibility(View.VISIBLE);
            machine_check.setVisibility(View.VISIBLE);

            machine_id.setText(listItem.getMachine_id());

            if (listItem.getMachine_check()) {
                machine_check.setBackground(convertView.getResources().getDrawable(R.drawable.setting_check_on));
            } else {
                machine_check.setBackground(convertView.getResources().getDrawable(R.drawable.setting_check_off));
            }

            View finalConvertView = convertView;

            box.setOnClickListener(v -> {
                listItem.setMachine_check(!listItem.getMachine_check());

                if (listItem.getMachine_check()) {
                    machine_check.setBackground(finalConvertView.getResources().getDrawable(R.drawable.setting_check_on));
                } else {
                    machine_check.setBackground(finalConvertView.getResources().getDrawable(R.drawable.setting_check_off));
                }
            });
        }

        return convertView;
    }

    public void addItem(String title, Boolean check, Boolean empty) {
        ListItem listItem = new ListItem();

        listItem.setMachine_id(title);
        listItem.setMachine_check(check);
        listItem.setEmpty(empty);

        listItems.add(listItem);
    }
}