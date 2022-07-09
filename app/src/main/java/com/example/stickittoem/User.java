package com.example.stickittoem;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class User {
    public String username;
    public ArrayList<Message> messages;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, ArrayList<Message> messages) {
        this.username = username;
        this.messages = messages;
    }


}
