package ru.sj.network.chat.client;

import ru.sj.network.chat.api.model.MessageModel;

public interface IChatEvents {

    void OnConnect();
    void OnDisconnect();

    void OnNewMessage(MessageModel msg);
}
