package com.example.whatsappclone.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.whatsappclone.R;

public class CallLogs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_logs);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CallLogs.this, MainActivity.class));
        finish();
    }
}