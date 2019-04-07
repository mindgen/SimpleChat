package ru.sj.network.chat.server.storage;

/**
 * Created by Eugene Sinitsyn
 */

public class TextMessage extends Message {

    TextMessage(String chatUser, int id, String msg) {
        super(chatUser, id);
        this.message = msg;
    }

    private String message;
    public String getMessage() {
        return message;
    }
}
