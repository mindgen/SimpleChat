package ru.sj.network.chat.server;


import java.nio.ByteBuffer;

/**
 * Created by Eugene Sinitsyn
 */

public interface ISession {
    ISessionId getId();
    ISessionsManager getManager();
    void close();

    void readData(ByteBuffer buffer);
}
