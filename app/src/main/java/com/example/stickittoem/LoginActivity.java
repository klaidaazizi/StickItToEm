package com.example.stickittoem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference database;
    private TextView usernameTV;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = FirebaseDatabase.getInstance().getReference();

        usernameTV = (TextView) findViewById(R.id.loginTV);
        loginButton = (Button) findViewById(R.id.button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if username exists in database open main activity and send username to it
                login(usernameTV.getText().toString());
            }
        });

    }



    public void login(String username) {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("users").hasChild(username)) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("currentUser", username);
                    startActivity(intent);
                }
                else {
                    String msg = username + ": User not found";
                    Toast loginFail = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
                    loginFail.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void writeNewUser(String username) {
        User user = new User(username);

        database.child("users").child(username).setValue(user);
    }
    /*
        // below used once to create these three users
        writeNewUser("laura");
        writeNewUser("klaida");
        writeNewUser("ada");
    */
}