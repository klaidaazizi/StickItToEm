package database;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private String stickerID;
    private String timeStamp;
    private String sender;

    public Message() {
    }

    public Message(String stickerID, String sender) {
        this.stickerID = stickerID;
        this.sender = sender;
    }

    public Message(String stickerID, String sender, String timeStamp) {
        this.stickerID = stickerID;
        this.timeStamp = timeStamp;
        this.sender = sender;
    }

    public String getStickerID() {
        return stickerID;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getSender() {
        return sender;
    }

    public static String date(){
        // Formatting outputs as 2022-07-08 23:12:02
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        return formatter.format(dateTime);
    }
}
