package ru.sj.network.chat.server;

import java.net.InetSocketAddress;

/**
 * Created by Eugene Sinitsyn
 */

public interface IServer {
    void start();

    InetSocketAddress getAddress();

    Integer getWorkers();
    void setWorkers(Integer value);
}
