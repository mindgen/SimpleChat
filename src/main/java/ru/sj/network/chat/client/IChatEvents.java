package ru.sj.network.chat.client;

public interface IChatEvents {
    void OnConnect();
    void OnDisconnect();

    void OnRegistration(boolean success);
    void OnChangeName(boolean success);

    void OnSendMessage(boolean success);
}
