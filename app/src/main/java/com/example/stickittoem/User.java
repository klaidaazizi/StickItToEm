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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
    public void addHistory(String sender, String receiver, String timestamp, String image) {
        this.messages.add(new Message(sender, receiver, timestamp, image));
    }
}
