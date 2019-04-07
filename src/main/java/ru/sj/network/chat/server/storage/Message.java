package ru.sj.network.chat.server.storage;

/**
 * Created by Eugene Sinitsyn
 */

public abstract class Message {
    String chatUser;
    int id;
    long timestamp;

    Message() {
        this.init(null, 0);
    }

    Message(String chatuser, int id) {
        this.init(chatUser, id);
    }

    protected void init(String chatUser, int id) {
        this.chatUser = chatUser;
        this.id = id;
        this.timestamp = System.currentTimeMillis();
    }

    public String getUser() {
        return chatUser;
    }

    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
