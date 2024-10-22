package com.example.doanngon.Model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.doanngon.Fragmet.ThongBao;
import com.example.doanngon.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.NotificationParams;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        RemoteMessage.Notification notification = message.getNotification();
        if(notification == null){
            return;
        }
        String strTitle = notification.getTitle();
        String strBody = notification.getBody();
        sendNotification(strTitle,strBody);
    }

    private void sendNotification(String strTitle, String strBody) {

        Intent intent = new Intent(this, ThongBao.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this,channelNotification.CHANNEL_ID)
                .setContentTitle(strTitle)
                .setContentText(strBody)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);
        Notification notification = noBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null){
            notificationManager.notify(1,notification);
        }
    }
}
