package com.corporation8793.kssterilizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class Login extends AppCompatActivity {
    EditText login_input;
    LinearLayout login_err;
    ImageButton login_auto, login_btn, login_input_init_btn;
    PreferenceUtil preferenceUtil;

    Boolean login_auto_b = false;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        preferenceUtil = PreferenceUtil.getInstance(this);

        login_input = findViewById(R.id.login_input);
        login_input_init_btn = findViewById(R.id.login_input_init_btn);
        login_err = findViewById(R.id.login_err);
        login_auto = findViewById(R.id.login_auto);
        login_btn = findViewById(R.id.login_btn);

        login_auto_b = preferenceUtil.getBooleanExtra("login_auto");

        if (login_auto_b) {
            login_auto_click();
            login_input.setText(preferenceUtil.getStringExtra("machine_num"));
        }

        login_btn.setOnClickListener(v -> {
            // 로그인 유효성
            if (login_input.getText().length() > 0) {
                if (login_auto_b) {
                    preferenceUtil.putStringExtra("machine_num", login_input.getText().toString());
                }

                Intent intent = new Intent(getApplicationContext(), MainControl.class);
                startActivity(intent);
                finish();
            } else {
                login_err.setVisibility(View.VISIBLE);
                vibrator.vibrate(100);
            }
        });

        login_auto.setOnClickListener(v -> {
            login_auto_b = !login_auto_b;

            login_auto_click();
        });

        login_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    login_input_init_btn.setVisibility(View.VISIBLE);
                }
                else {
                    login_input_init_btn.setVisibility(View.INVISIBLE);
                }
            }
        });

        login_input_init_btn.setOnClickListener(v -> login_input.getText().clear());
    }

    public void login_auto_click() {
        if (login_auto_b) {
            login_auto.setBackground(getResources().getDrawable(R.drawable.login_auto_on));
        } else {
            login_auto.setBackground(getResources().getDrawable(R.drawable.login_auto_off));
        }
        preferenceUtil.putBooleanExtra("login_auto", login_auto_b);
    }
}