package ru.sj.network.chat.server.storage;

/**
 * Created by Eugene Sinitsyn
 */

public class UserExistException extends Exception {
    UserExistException() {
        super("User already created");
    }
}
