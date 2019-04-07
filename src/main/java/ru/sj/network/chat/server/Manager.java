package ru.sj.network.chat.server;

import ru.sj.network.chat.server.storage.Message;
import ru.sj.network.chat.server.storage.Messages;
import ru.sj.network.chat.server.storage.Users;

/**
 * Created by Eugene Sinitsyn
 */

public class Manager {

    private Users users;
    private Messages messages;

    Manager(Users user, Messages messages) {
        this.users = user;
        this.messages = messages;
    }


}
