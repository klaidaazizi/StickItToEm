package database;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.stickittoem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    private final String NOTIFY_CHANNEL_ID = "test";
    private final ArrayList<Message> stickerList = new ArrayList<>();
    private ListView mainList;
    private String currentUser;
    private DatabaseReference databaseReference;
    private DatabaseReference historyDatabaseReference;
    private ValueEventListener historyListener;
    private StickerViewAdapter adapter;
    private Message message;
    final String TAG = "HISTORY_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // need to set up notification channel for over API 26
        // information from android documentation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create notification channel
            NotificationChannel stickerChannel = new
                    NotificationChannel(NOTIFY_CHANNEL_ID, "Stickers",
                    NotificationManager.IMPORTANCE_DEFAULT);
            // now register notification channel with system
            NotificationManager notifyManager = getSystemService(NotificationManager.class);
            notifyManager.createNotificationChannel(stickerChannel);
        }

        setContentView(R.layout.activity_history);
        // get current user from previous activity
        getCurrentUser();
        Log.d("Tag", "Part 1");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // use the below function to fill in the list for the user.
        getUserStickersReceived();
    }

    private void getUserStickersReceived() {
        if (currentUser != null){
            historyDatabaseReference = databaseReference.child("User").child(currentUser).child("received");
            // have listener open for changes to database
            historyListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // 1. need to clear past stickers
                    stickerList.clear();
                    // 2. pull each recieved message: sender, timestamp, stickerID
                    Log.d("Tag", "Inside 1");
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Log.d("Tag", "Inside 2");
                        if (ds != null){
                            Log.d("Tag", "Inside 3");
                            // create a new message and add to array
                            String stickerValue = ds.getValue(Message.class).getStickerID();
                            String newUser = ds.getValue(Message.class).getSender();
                            String timeStamp = ds.getValue(Message.class).getTimeStamp();

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
        }
    }

    /**
     * This adds in the current user to the activity.
     */
    private void getCurrentUser() {
        Intent intent = getIntent();
        if (intent != null){
            currentUser = intent.getStringExtra("currentUser");
            Log.d("TESTactivity() currentUser is: ", currentUser);
        } else {
            Toast.makeText(this, "Intent is null", Toast.LENGTH_SHORT).show();
        }
    }

}