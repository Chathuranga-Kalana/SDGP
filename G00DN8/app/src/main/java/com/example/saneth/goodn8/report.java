package com.example.saneth.goodn8;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class report extends AppCompatActivity {
TextView time;
    TextView heart;
    TextView temp;
    int hCount;
    int sleepingHTime = 0;
    int sleepingMTime =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        time = (TextView)findViewById(R.id.txtTime);
        heart = (TextView) findViewById(R.id.txtHeart);
        temp = (TextView) findViewById(R.id.txtTemp);



        Intent l = getIntent();
        int avg = l.getIntExtra("avg", 0);
        //String avg = l.getStringExtra("avg");
        String tReport = l.getStringExtra("tReport");
        hCount =l.getIntExtra("count",0);


        sleepingMTime = (hCount*3)/60;
        if(sleepingMTime <=60){time.setText(sleepingHTime+":"+sleepingMTime);}
        else{
            sleepingHTime= sleepingMTime/60;
            sleepingMTime =sleepingMTime%60;
            time.setText(sleepingHTime+":"+sleepingMTime);
        }
        time.setText(sleepingHTime+":"+sleepingMTime);
        heart.setText(avg+"");
        temp.setText(tReport);
        }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }
}


