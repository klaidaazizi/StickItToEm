package com.example.stickittoem;

/**
 * Message model
 */
public class Message {
    private String sender;
    private String receiver;
    private String timestamp;
    private String content;

    public Message(String sender, String receiver, String timestamp, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
