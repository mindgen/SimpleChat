package ru.sj.network.chat.api.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TextMessageModel extends MessageModel {
    private String message;

    public TextMessageModel(String user, String message) {
        this.userName = user;
        this.message = message;
    }

    public String getText() { return this.message; }
}
