package com.example.saneth.goodn8;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Main2Activity extends AppCompatActivity {
    Button report;
     Switch mySwitch;
    Button heart;
    TextView temp;
    FirebaseDatabase database;
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        report = (Button)findViewById(R.id.btnReport);
        heart = (Button)findViewById(R.id.btnHeart);
        temp = (TextView)findViewById(R.id.txtTemp);
        mySwitch = (Switch) findViewById(R.id.swLight);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.child("Value here").setValue("Hello, World!");

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this, report.class));
            }
        });

        //set the switch to ON
        mySwitch.setSelected(true);

        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    Toast.makeText(getApplicationContext(), "ON", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();

                    myRef.child("Value = ").setValue("Hello, World!");

                }else{
                    Toast.makeText(getApplicationContext(), "Off", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //check the current state before we display the screen

        if(mySwitch.isChecked()){
            Toast.makeText(getApplicationContext(), "ON", Toast.LENGTH_SHORT).show();

            myRef.child("Value here").child("roww").setValue("Hello, World!");
        }
        else {
            Toast.makeText(getApplicationContext(), "Off", Toast.LENGTH_SHORT).show();
        }
    }


}