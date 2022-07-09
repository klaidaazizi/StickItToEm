package com.example.stickittoem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private static final String SERVER_KEY = "key=BMvy9qoBEGhjmlS1Vbk6RFc7hDvzNO4kLhPkuGW-nKoR_aa8b0tjq8k3_bjlM32jt4_h0H9T4j3mxDIqU0AbsLY";
    private static final String TAG = "FCMActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        for (DataSnapshot child : children) {
            String user = child.getKey();
            String username = (String) child.child("username").getValue();
            ArrayList<Message> messages = new ArrayList<>();
            if (child.child("messages").exists()) {
                for (DataSnapshot messageSnapshot : child.child("messages").getChildren()) {
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
        User currUser = users.get(currentUser);
        usersDatabase.child(currentUser).setValue(currUser).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(this,"Current user loaded successfully",Toast.LENGTH_SHORT);
            } else{
                Toast.makeText(this,"User doesn't exist",Toast.LENGTH_SHORT);
            }
        });
    }


    /**
     * Display images on screen
     */

    private void showImages() {
            imageToButtonMap = new HashMap<RadioButton, String>();
            image1 = (ImageView) findViewById(R.id.imageView1);
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
        if (users.containsKey(receiverUser)){
            if (checked != null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendMessage(users.get(receiverUser).toString(),checked);
                        Toast.makeText(MainActivity.this,"Image successfully sent",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    // Reference: Firebase demo, FCMActivity
    private void sendMessage(String receiverUser, String checked) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();

        try{
            jNotification.put("title", "Emoji Received!");
            jNotification.put("body", currentUser + "sent you the emoji: " + checked);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");

            jData.put("sender", currentUser);
            jData.put("receiver",receiverUser);

            // If sending to a single client
            jPayload.put("to", receiverUser);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);

            //Reference: Utils from Firebase demo
            // HTTP and send the payload
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

        } catch (JSONException | IOException e) {
            Log.e(TAG,"sendMessage not successful",e);
        }
    }

    //Reference: Utils from Firebase demo

    public static String convertStreamToString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String len;
            while ((len = bufferedReader.readLine()) != null) {
                stringBuilder.append(len);
            }
            bufferedReader.close();
            return stringBuilder.toString().replace(",", ",\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}