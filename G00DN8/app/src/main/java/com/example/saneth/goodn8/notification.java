//package com.example.saneth.goodn8;
//
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.support.v4.app.NotificationCompat;
//
//import java.util.Calendar;
//
///**
// * Created by saneth on 5/21/2017.
// */
//
//public class AlarmReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // TODO Auto-generated method stub
//
//        long when = System.currentTimeMillis();
//        NotificationManager notificationManager = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Intent notificationIntent = new Intent(context, EVentsPer form.class);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
//                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
//                context)//.setSmallIcon(R.drawable.applogo)
//                .setContentTitle("Alarm Fired")
//                .setContentText("Events To be PErformed").setSound(alarmSound)
//                .setAutoCancel(true).setWhen(when)
//                .setContentIntent(pendingIntent)
//                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
//        notificationManager.notify(MID, mNotifyBuilder.build());
//        MID++;
//
//    }
//
//}
//    Calendar calendar = Calendar.getInstance();
// calendar.set(Calendar.HOUR_OF_DAY, 18);
//         calendar.set(Calendar.MINUTE, 30);
//         calendar.set(Calendar.SECOND, 0);
//         Intent intent1 = new Intent(MainActivity.this, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(MainActivity.this.ALARM_SERVICE);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);