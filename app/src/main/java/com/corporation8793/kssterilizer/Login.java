package com.corporation8793.kssterilizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class Login extends AppCompatActivity {
    EditText login_input;
    LinearLayout login_err, login_input_init_btn;
    ImageButton login_auto, login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_input = findViewById(R.id.login_input);
        login_input_init_btn = findViewById(R.id.login_input_init_btn);
        login_err = findViewById(R.id.login_err);
        login_auto = findViewById(R.id.login_auto);
        login_btn = findViewById(R.id.login_btn);

        login_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainControl.class);
            startActivity(intent);
            finish();
        });
    }
}