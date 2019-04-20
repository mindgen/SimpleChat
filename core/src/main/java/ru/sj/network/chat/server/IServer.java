package ru.sj.network.chat.server;

import java.net.InetSocketAddress;

/**
 * Created by Eugene Sinitsyn
 */

public interface IServer {
    void start() throws Exception;
    void stop() throws Exception;
    boolean isRunning();

    InetSocketAddress getAddress();
    void setAddress(InetSocketAddress address) throws AlreadyStartedException;
}
