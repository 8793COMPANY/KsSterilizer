package com.corporation8793.kssterilizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {
    //인트로
    int count = 0;
    ProgressBar progressBar;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress);
        progressBar.setMax(300);
    }

    Runnable progress_start = new Runnable() {
        @Override
        public void run() {
            if (count == 300){
                handler.removeCallbacksAndMessages(null);
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }else{
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
}