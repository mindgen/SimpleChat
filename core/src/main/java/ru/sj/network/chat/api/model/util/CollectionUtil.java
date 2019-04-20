package ru.sj.network.chat.api.model.util;

import org.w3c.dom.Text;
import ru.sj.network.chat.api.model.MessageModel;
import ru.sj.network.chat.api.model.TextMessageModel;
import ru.sj.network.chat.server.storage.Message;
import ru.sj.network.chat.server.storage.TextMessage;
import ru.sj.network.chat.server.storage.User;

import java.util.List;

public abstract class CollectionUtil {
    public static void copyStorageMessageToModel(List<Message> storageMsg, List<MessageModel> result) {
        for (Message msg : storageMsg) {
            if (msg instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage)msg;
                result.add(new TextMessageModel(txtMsg.getUser(), txtMsg.getMessage()));
            }
        }
    }

    public static void copyStorageUsersToModel(List<User> users, List<String> result) {
        for (User usr : users) {
            result.add(usr.getName());
        }
    }
}
