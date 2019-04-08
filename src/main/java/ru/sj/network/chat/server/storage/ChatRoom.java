package ru.sj.network.chat.server.storage;

import ru.sj.network.chat.server.storage.Message;
import ru.sj.network.chat.server.storage.Messages;
import ru.sj.network.chat.server.storage.Users;

/**
 * Created by Eugene Sinitsyn
 */

public class ChatRoom {

    public ChatRoom(Users user, Messages messages) {
        this.users = user;
        this.messages = messages;
    }

    private Users users;
    public Users getUsers() {
        return users;
    }

    private Messages messages;
    public Messages getMessages() {
        return messages;
    }

    public static ChatRoom createDefault() {
        return new ChatRoom(new Users(), new Messages());
    }
}
