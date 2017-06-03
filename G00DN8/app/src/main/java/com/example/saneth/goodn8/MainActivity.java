package com.example.saneth.goodn8;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Button go;
    EditText occ;
    EditText id;
    ToggleButton alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        occ = (EditText) findViewById(R.id.txtOcc);
        id = (EditText) findViewById(R.id.txtId);
        alarm = (ToggleButton) findViewById(R.id.btnAlarm);
        go = (Button) findViewById(R.id.btnGo);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();
                goToMain2();


            }
        });
    }

    private void goToMain2() {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
        finish();
    }
}
