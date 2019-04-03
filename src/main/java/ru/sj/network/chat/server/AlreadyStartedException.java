package ru.sj.network.chat.server;

/**
 * Created by Eugene Sinitsyn
 */

public class AlreadyStartedException extends Exception {
    public AlreadyStartedException(){};
    public AlreadyStartedException(String message) {
        super(message);
    }
}
