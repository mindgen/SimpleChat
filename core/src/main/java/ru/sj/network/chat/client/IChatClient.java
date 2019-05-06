package ru.sj.network.chat.client;

import java.net.SocketAddress;

/**
 * Created by Eugene Sinitsyn
 */

public interface IChatClient {
    boolean connect(SocketAddress endpoint);
    void disconnect();
    boolean isConnected();

    void stop();

    FutureResponse registration(String name);

    FutureResponse changeUserName(String name);
    FutureResponse getUsersCount();
    FutureResponse sendMessage(String text);

    IChatEvents getEventsHandler();
}
