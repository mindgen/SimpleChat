package ru.sj.network.chat.server;

import java.net.InetSocketAddress;

/**
 * Created by Eugene Sinitsyn
 */

public interface IServer {
    void start();

    InetSocketAddress getAddress();

    int getWorkersCount();
    void setWorkersCount(int value);

    void setBufferCapacity(int value);
    int getBufferCapacity();
}
