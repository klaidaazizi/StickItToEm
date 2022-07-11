package com.example.stickittoem;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.URL;

/**
 * Reference: DemoMessagingService from module
 */
public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = MessagingService.class.getSimpleName();
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String CHANNEL_NAME = "CHANNEL_NAME";
    private static final String CHANNEL_DESCRIPTION = "CHANNEL_DESCRIPTION";


    public MessagingService() {
        super();
        Log.d(TAG,"Service running");
    }

    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);

        Log.d(TAG, "Refreshed token: " + newToken);
    }

    /**
     * Called when message is received.
     * Mainly what you need to implement
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    //
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]


        /* NOTICE!!!
         * Message types are inevitable in recent Android version.
         * remoteMessage.getData() Method will return null for 'topic-subscribed messages' from FCMActivity
         *
         * remoteMessage.getFrom() Method will recognize topic-subscribed messages
         * remoteMessage.getNotification() Method will show the raw-data of topic-subscribed messages
         */

        super.onMessageReceived(remoteMessage);
        Toast.makeText(this, "Notification received",Toast.LENGTH_LONG).show();
        if (remoteMessage.getNotification() != null) {
            Toast.makeText(this, "Message Notification Body: " + remoteMessage.getNotification().getBody(),Toast.LENGTH_SHORT).show();
            showNotification(remoteMessage.getNotification());
        }
        if (remoteMessage.getData().size() > 0){
            Toast.makeText(this, "Message payload: " + remoteMessage.getData(),Toast.LENGTH_SHORT).show();
        }



    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remoteMessage FCM message  received.
     */
    private void showNotification(RemoteMessage.Notification remoteMessage) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification notification;
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel
            notificationChannel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else{
            builder = new NotificationCompat.Builder(this);
        }
        try{
            URL url = new URL(remoteMessage.getImageUrl().toString());
            BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }

        notification = builder.setContentTitle(remoteMessage.getTitle())
                .setContentText(remoteMessage.getBody())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(0, notification);

    }

}