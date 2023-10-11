package com.example.mangxahoiute.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.RemoteInput;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.mangxahoiute.ChatActivity;
import com.example.mangxahoiute.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String sent=message.getData().get("sent");
        String user=message.getData().get("user");
        FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
        if (fUser!=null){
            assert sent != null;
            if (sent.equals(fUser.getUid())){
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                    sendOAndAboveNotification(message);
                }else{
                    sendNormalNotification(message);
                }
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void sendNormalNotification(RemoteMessage message) {
        String user= message.getData().get("user");
        String icon= message.getData().get("icon");
        String title= message.getData().get("title");
        String body= message.getData().get("body");
        RemoteMessage.Notification notification=message.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent= new Intent(this, ChatActivity.class);
        Bundle bundle= new Bundle();
        bundle.putString("hisUid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setContentIntent(pIntent);

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if (i>0){
            j=i;
        }
        notificationManager.notify(j,builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void sendOAndAboveNotification(RemoteMessage message) {
        String user= message.getData().get("user");
        String icon= message.getData().get("icon");
        String title= message.getData().get("title");
        String body= message.getData().get("body");
        RemoteMessage.Notification notification=message.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
//        Intent intent= new Intent(this, ChatActivity.class);
        Bundle bundle= new Bundle();
        bundle.putString("hisUid",user);

        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        SharedPreferences.Editor predsefits = sharedPreferences.edit();
        predsefits.putString("hisUid", user);
        predsefits.apply();
//        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAndAboveNotification oreoNotification= new OreoAndAboveNotification(this);
//        Notification.Builder builder= notification1.getONotifications(title,body,pIntent,defSoundUri,icon);
        RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply").setLabel("Your Message...").build();

        Intent replyIntent;
        PendingIntent pIntentreply = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            replyIntent = new Intent(this, NotificationService.class);
            pIntentreply = PendingIntent.getBroadcast(this, 0, replyIntent, 0);


        }

        // todo action

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(R.drawable.reply,
                "Reply", pIntentreply).addRemoteInput(remoteInput).build();

        Intent intent= new Intent(this, ChatActivity.class);
        bundle.putString("hisUid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);


        // todo style


        NotificationCompat.Builder builder = oreoNotification.getNotificationShit( replyAction, title, body,pIntent,
                defSoundUri, icon);








        int j = 0;
        if (i > 0){
            j = i;
        }



        SharedPreferences shf = getSharedPreferences("NEWPREFS", MODE_PRIVATE);
        SharedPreferences.Editor editorSh = shf.edit();
        editorSh.putInt("values", i);
        editorSh.apply();
        oreoNotification.getManager().notify(j,builder.build());
    }
}
