package com.maroreminder.app;

import android.app.*;
import android.content.*;
import android.os.Build;

public class ReminderReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c, Intent i){
        String title=i.getStringExtra("title"), note=i.getStringExtra("note");
        NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(new NotificationChannel("reminders","Reminders",NotificationManager.IMPORTANCE_HIGH));
        Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(c,"reminders"):new Notification.Builder(c);
        b.setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle(title==null?"રિમાઇન્ડર":title)
         .setContentText(note==null||note.isEmpty()?"તમારો રિમાઇન્ડર":note).setAutoCancel(true);
        nm.notify(i.getIntExtra("id",1),b.build());
    }
}
