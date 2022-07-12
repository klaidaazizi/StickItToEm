package com.example.stickittoem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String IMAGE_1 = "https://i.imgur.com/V3IvZtL.png";
    private static final String IMAGE_2 = "https://i.imgur.com/UKKs6i8.jpg";
    private static final String IMAGE_3 = "https://i.imgur.com/7zT2o37.png";
    private RadioButton radioButton1, radioButton2, radioButton3;
    private ImageView image1, image2, image3;
    private Map<RadioButton, String> imageToButtonMap;
    private ArrayList<RadioButton> buttons;
    private String checked;

    private String currentUser, receiverUser;
    private Button sendMessage;
    private DatabaseReference usersDatabase;
    private Map<String, User> users;

    private int notiNumber = 0;

    private static final String SERVER_KEY = "key=BMvy9qoBEGhjmlS1Vbk6RFc7hDvzNO4kLhPkuGW-nKoR_aa8b0tjq8k3_bjlM32jt4_h0H9T4j3mxDIqU0AbsLY";
    private static final String TAG = "FCMActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        showImages();
        currentUser = getIntent().getStringExtra("currentUser");
        setTitle(currentUser);

        sendMessage = (Button) findViewById(R.id.sendButton);

        usersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        usersDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Error getting data from database", Toast.LENGTH_SHORT).show();
                } else{
                    updateUsers(task.getResult().getChildren());
                    getUser();
                }
            }
        });
        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateUsers(snapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    private void updateUsers(Iterable<DataSnapshot> children) {
        users = new HashMap<>();
        for (DataSnapshot userSnapshot : children) {
            String user = userSnapshot.getKey();
            String username = (String) userSnapshot.child("username").getValue();
            ArrayList<Message> messages = new ArrayList<>();
            if (userSnapshot.child("messages").exists()) {
                for (DataSnapshot messageSnapshot : userSnapshot.child("messages").getChildren()) {
                    String sender = (String) messageSnapshot.child("sender").getValue();
                    String receiver = (String) messageSnapshot.child("receiver").getValue();
                    String content = (String) messageSnapshot.child("content").getValue();
                    String timestamp = (String) messageSnapshot.child("timestamp").getValue();
                    Message message = new Message(sender, receiver, timestamp, content);
                    messages.add(message);
                }
            }
                users.put(user, new User(username, messages));
            }
        }

    private void getUser() {
        if (users.containsKey(currentUser)) {
            User currUser = users.get(currentUser);
            usersDatabase.child(currentUser).setValue(currUser).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Current user loaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            User user = new User(currentUser);
            usersDatabase.child(currentUser).setValue(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Successfully added new user", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to add user", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    /**
     * Display images on screen
     */

    private void showImages() {
            imageToButtonMap = new HashMap<RadioButton, String>();
            image1 = findViewById(R.id.imageView1);
            Picasso.get().load(IMAGE_1).into(image1);
            image2 = (ImageView) findViewById(R.id.imageView2);
            Picasso.get().load(IMAGE_2).into(image2);
            image3 = (ImageView) findViewById(R.id.imageView3);
            Picasso.get().load(IMAGE_3).into(image3);

            buttons = new ArrayList<>();
            radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
            buttons.add(radioButton1);
            imageToButtonMap.put(radioButton1, IMAGE_1);
            radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
            buttons.add(radioButton2);
            imageToButtonMap.put(radioButton2, IMAGE_2);
            radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
            buttons.add(radioButton3);
            imageToButtonMap.put(radioButton3, IMAGE_3);

        }


    public void selectImage(View view){
        for (RadioButton button: buttons){
            button.setChecked(false);
        }

        RadioButton checkedButton = findViewById(view.getId());
        checkedButton.setChecked(true);
        checked = imageToButtonMap.get(checkedButton);
    }

    /**
     * Button handler; creates a new thread that sends message to friend
     * @param view
     */
    public void sendMessage(View view) {
        receiverUser = ((EditText) findViewById(R.id.friendUsernameTV)).getText().toString();
        if (users.containsKey(receiverUser)) {
            if (checked != null) {
                new Thread(() -> sendMessage(users.get(receiverUser).getUsername(), checked)).start();
                Toast.makeText(MainActivity.this, "Image successfully sent", Toast.LENGTH_SHORT).show();
                updateHistory();
            } else{
                Toast.makeText(MainActivity.this,"Please select an image",Toast.LENGTH_SHORT).show();
            }
        } else{
                Toast.makeText(MainActivity.this,"User does not exist",Toast.LENGTH_SHORT).show();
        }
    }


    // Reference: Firebase demo, FCMActivity
    private void sendMessage(String receiverUser, String checked) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();

        try{
            jNotification.put("title", "Emoji Received!");
            jNotification.put("body", currentUser + "sent you the emoji: " + checked);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");

            // If sending to a single client
            jPayload.put("to", receiverUser);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);

        } catch (JSONException e) {
            Toast.makeText(this,"sendMessage not successful",Toast.LENGTH_SHORT).show();
        }
            //Reference: Utils from Firebase demo
            // HTTP and send the payload
            final String resp = Utils.fcmHttpConnection(SERVER_KEY, jPayload);
            Utils.postToastMessage("Status from Server: " + resp, getApplicationContext());
            sendNotification();

    }

    private void updateHistory() {
        User user = users.get(currentUser);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        user.addHistory(currentUser, receiverUser, dtf.format(now), checked);
        usersDatabase.child(currentUser).setValue(user).addOnCompleteListener(task ->{
            if  (task.isSuccessful()) {
                Toast.makeText(this, "History saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "History could not be saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkHistory(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        intent.putExtra("messages", users.get(currentUser).getMessages());
        startActivity(intent);
    }

    // Reference: Notification demo
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Reference Notification Demo and Android Notification Documentation
    public void sendNotification(){
        int notiImage;
        if (checked == IMAGE_1) {
            notiImage = R.mipmap.stareyes;
        }
        else if (checked == IMAGE_2) {
            notiImage = R.mipmap.raisedeyebrows;
        }
        else {
            notiImage = R.mipmap.happy;
        }
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        PendingIntent callIntent = PendingIntent.getActivity(this, (int)System.currentTimeMillis(),
                new Intent(this, LoginActivity.class), 0);


        // Build notification
        // Actions are just fake
        String channelId = getString(R.string.channel_id);

        Notification noti = new NotificationCompat.Builder(this, channelId)
                .setContentText("You sent a sticker to " + receiverUser + "!")
                .setContentTitle("Sticker")
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        notiImage))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
                ;

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);            // hide the notification after its selected
            noti.flags |= Notification.FLAG_AUTO_CANCEL ;

            notificationManager.notify(notiNumber, noti);
    }

    /*

    public void receiveMessage() {
        usersDatabase.child("users")
                .child("messages")
                .child("receiver")
                .equalTo(currentUser)
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Message message = snapshot.getValue(Message.class);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );
    }

     */

}