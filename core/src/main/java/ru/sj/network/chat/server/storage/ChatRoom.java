package ru.sj.network.chat.server.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sj.network.chat.server.storage.Message;
import ru.sj.network.chat.server.storage.Messages;
import ru.sj.network.chat.server.storage.Users;

/**
 * Created by Eugene Sinitsyn
 */

@Component
public class ChatRoom {

    public ChatRoom(Users user, Messages messages) {
        this.users = user;
        this.messages = messages;
    }

    @Autowired
    private Users users;
    public Users getUsers() {
        return users;
    }

    @Autowired
    private Messages messages;
    public Messages getMessages() {
        return messages;
    }
}
