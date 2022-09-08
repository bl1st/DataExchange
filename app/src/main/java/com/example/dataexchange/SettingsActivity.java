package com.example.dataexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    //493 balanin
    EditText ip,send_port,recieve_port,name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ip = findViewById(R.id.et_ip);
        send_port = findViewById(R.id.et_sendPort);
        recieve_port = findViewById(R.id.et_recievePort);
        name = findViewById(R.id.et_name);

        Intent i = getIntent();
        name.setText(i.getStringExtra("name"));
        ip.setText(i.getStringExtra("ip"));
        send_port.setText(i.getStringExtra("sendPort"));
        recieve_port.setText(i.getStringExtra("recievePort"));

    }

    public void onButtonApply_Click(View v){

        Intent i = new Intent();
        i.putExtra("ip", ip.getText().toString());
        i.putExtra("sendPort", send_port.getText().toString());
        i.putExtra("recievePort",recieve_port.getText().toString());
        i.putExtra("name",name.getText().toString());
        Log.e("TEST","FINISHING SETTINGS ACTIVITY");
        setResult(RESULT_OK,i);
        finish();
    }

    public void onButtonClearHistory_Click(View v){

       g.messages.CLEARALLTABLE();
        Toast.makeText(this, "Successfully cleared history", Toast.LENGTH_SHORT).show();
    }

    public void onButtonExit_Click(View v){

        setResult(RESULT_CANCELED);
        finish();
    }
}