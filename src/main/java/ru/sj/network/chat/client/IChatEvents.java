package ru.sj.network.chat.client;

import ru.sj.network.chat.api.model.MessageModel;

public interface IChatEvents {
    void OnConnect();
    void OnDisconnect();

    void OnRegistration(boolean success);
    void OnChangeName(boolean success);

    void OnSendMessage(boolean success);

    void OnNewMessage(MessageModel msg);
}
