package com.example.saneth.goodn8;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Main2Activity extends AppCompatActivity {
    Button reportbtn;
    Button settings;
    Button signout;
    Switch mySwitch;
    Button music;

    TextView heart;
    TextView temp;
    TextView humidityD;
    TextView heartdisp;
    TextView tempdisp;
    TextView user1;

    String heartReport;
    String tempReport;
    String timeValue;
    String timeValueIf;
    String changedTime;
    String timeHigh;

    CheckBox notifi;

    int hour;
    int minute;
    int count1 =0;

    boolean flag = false;
    boolean boo = false;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference hRate, light, tempReading, time, humidity;

    final List<Integer> heartRates = new ArrayList<Integer>();
    MediaPlayer mp1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        reportbtn = (Button) findViewById(R.id.btnReport);
        settings = (Button) findViewById(R.id.btnSettings);
        heart = (TextView) findViewById(R.id.txtHeart);
        temp = (TextView) findViewById(R.id.txtTemp);
        humidityD = (TextView) findViewById(R.id.humiditydisp);

        mySwitch = (Switch) findViewById(R.id.swLight);
        signout = (Button) findViewById(R.id.signout);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        heartdisp = (TextView) findViewById(R.id.heartdisp);
        tempdisp = (TextView) findViewById(R.id.tempdisp);
        user1 = (TextView) findViewById(R.id.textuser);
        music = (Button) this.findViewById(R.id.music);
       notifi = (CheckBox) findViewById(R.id.Notification);
        mp1 = MediaPlayer.create(Main2Activity.this, R.raw.my);





        //Button for sleep time change
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeclicker();
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();

        System.out.println(user.getEmail().toString());
        user1.setText(user.getEmail().toString());

        //taking values from database
        time = db.getReference("GNID01").child("Time").child("fTime");
        hRate = db.getReference("GNID01").child("PulseSensor").child("Heart Rate");
        tempReading = db.getReference("GNID01").child("DHTsensor").child("temperature");
        humidity = db.getReference("GNID01").child("DHTsensor").child("Humidity");
        light = db.getReference().child("GNID01").child("states").child("001");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
            }
        };





        //Getting time from Database
        time.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("========================================================");
                System.out.println("Time : " + dataSnapshot.getValue().toString());
                timeValue = (dataSnapshot.getValue().toString());
                timeValueIf = timeValue;

                if (timeValueIf.equals("0:0")) {

                    timeclicker();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        humidity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Humidity : " + dataSnapshot.getValue().toString());
                humidityD.setText(dataSnapshot.getValue().toString()+" %");
//                tempReport = (dataSnapshot.getValue().toString());
//                tempReport = tempdisp.getText().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        // Music play


       // final MediaPlayer mp1 = MediaPlayer.create(this, R.raw.my);
        music.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mp1.isPlaying()) {
                    mp1.pause();
                } else {
                    mp1.start();
                }

            }
        });


        //Reading Temperature values from firebase
        tempReading.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Temperature : " + dataSnapshot.getValue().toString());
                tempdisp.setText(dataSnapshot.getValue().toString()+" ºC");
                tempReport = (dataSnapshot.getValue().toString());
                tempReport = tempdisp.getText().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Reading Heart rate values form firebase
        hRate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                System.out.println("Heart rate : " + dataSnapshot.getValue().toString());
                heartdisp.setText(dataSnapshot.getValue().toString()+" Bpm");
                heartReport = heartdisp.getText().toString();
        timeValueIf = ("0:0");
                //  check weather he is sleeping

                TimerTask timerTask = new TimerTask() {

                    String sleepH = dataSnapshot.getValue().toString();
                    int shRate = Integer.parseInt(sleepH);

                    @Override

                    public void run() {
                        DateFormat df = new SimpleDateFormat("HH:mm"); //format time
                        String time = df.format(Calendar.getInstance().getTime() );

                        Calendar cal = Calendar.getInstance();

                        String[] parts = timeValueIf.split(":");
                        String part1 = parts[0];
                        String part2 = parts[1];
                        int hours = Integer.parseInt(part1);
                        int minutes = Integer.parseInt(part2);
                        int hourNow = cal.get(Calendar.HOUR_OF_DAY);
                        int minuteNow = cal.get(Calendar.MINUTE);

                        //sleeping time equals to real time
                        if (hours == hourNow && minutes == minuteNow) {
                            light.child("states").setValue(false);

                            if (flag != true) {
                                flag = true;
                                if (mp1.isPlaying()) {
                                    mp1.pause();
                                } else {
                                    mp1.setLooping(true);
                                    mp1.start();
                                }
                            }



                        }
                        //Let the application stops when the wakeup times comes.
                        String[] array = timeValueIf.split(":");
                        String arrayHou = parts[0];
                        String arrayMin = parts[1];
                        int hour = Integer.parseInt(arrayHou);
                        int minute = Integer.parseInt(arrayMin);
                        int totalTime = hours + 7;
                        int hourNNow = cal.get(Calendar.HOUR_OF_DAY);
                        int minuteNNow = cal.get(Calendar.MINUTE);


                        //If time count is greater than 24
                        if (totalTime >= 24) {
                            int gTime = (totalTime - 24);
                            //changedTime = ("0" + gTime + ":" + part2);

                            if (totalTime == hourNNow && minute == minuteNNow) {
                                light.setValue(true);




                                mp1.stop();

                            }
                        }else {
                            if (totalTime <10){//if time is less than 10
                                changedTime = ("0"+totalTime + ":" + part2);

                                if (changedTime.equals(time)) {
                                    light.setValue(true);
                                    mp1.stop();
                                }
                                //if time is greater than 10 and less than 24
                            }else {  timeHigh = (totalTime + ":" + part2);
                                if (timeHigh.equals(time)) {
                                    light.setValue(true);


                                    mp1.stop();
                                }
                            }
                        }


                        //if heart rate is less than 45. He sleeps
                        if ((shRate < 50)) {
                            System.out.println("TimerTask executing counter is: " + shRate);
                            count1 +=1;
                            System.out.print("ggggggggggggggg"+count1);
                            heartRates.add(shRate);
                        }else{
                            heartRates.add(shRate);
                        }
                    }
                };
                //create a new Timer
                Timer timer = new Timer("MyTimer");

                //this line starts the timer at the same time its executed
                timer.scheduleAtFixedRate(timerTask, 30, 3000);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Sign Out Button
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "Signing Out....", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Main2Activity.this, login.class));

            }
        });


        //set the switch to ON
        mySwitch.setSelected(true);

        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    boo = false;
                    DatabaseReference light = FirebaseDatabase.getInstance().getReference().child("GNID01").child("states").child("001");
                    light.setValue(boo);

                } else {
                    boo = true;
                    DatabaseReference light = FirebaseDatabase.getInstance().getReference().child("GNID01").child("states").child("001");
                    light.setValue(boo);
                }

            }
        });
        //Intent for report
        reportbtn.setOnClickListener(new View.OnClickListener() {
            //            @Override
            public void onClick(View v) {
                System.out.println("Clicked report button =============================");
                //reportGen();
                int avg = reportGen();
                Intent intent = new Intent(Main2Activity.this, report.class);
                intent.putExtra("count",count1);
                intent.putExtra("avg", avg);
                intent.putExtra("tReport", tempReport);
                startActivity(intent);
            }
        });




        // notification


        notifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                String[] parts = timeValueIf.split(":");
                String part1 = parts[0];
                String part2 = parts[1];
                int hour = Integer.parseInt(part1);
                int minute = Integer.parseInt(part2);
                int fTime = hour*60 + minute;
                int nTime = fTime-30;
                hour = nTime/60;
                minute = nTime%60;

                System.out.println("dddddddddddddddddddddddddddddddddddddddddddd"+hour);
                System.out.println("dddddddddddddddddddddddddddddddddddddddddddd"+minute);

                Calendar calender = Calendar.getInstance();
                calender.set(Calendar.HOUR_OF_DAY, hour);
                calender.set(Calendar.MINUTE, minute);
                Intent intent = new Intent(getApplicationContext(), Notfication_reciever.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


            }

        });
    }



    //Timer
    private void timeclicker() {
        // TODO Auto-generated method stub
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(Main2Activity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                String testing = (selectedHour + ":" + selectedMinute);
                time.setValue(testing);
                System.out.println("========================================================" + testing);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Sleeping Time");


        mTimePicker.show();

    }
    //Report Generates
    public int reportGen() {

        System.out.println("Came here ==================================report gen==================================");
        int total = 0, avg = 0, count = 0;


        for (int i = 0; i < heartRates.size(); i++) {
            total += heartRates.get(i).intValue();
            count++;
            System.out.println("=====================================" + heartRates.get(i));
        }
        System.out.println("Count = " + count + "total = " + total);
        avg = total / count;
        System.out.println("===================================== avg is " + avg);

        return avg;
    }


    //Exit
    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
        exitDialog.setTitle("Exit");
        exitDialog.setMessage("Are you sure you want to exit?");
        exitDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Main2Activity.this.finish();
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        final AlertDialog alert = exitDialog.create();
        alert.show();

    }


 }






