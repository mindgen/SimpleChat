package ru.sj.network.chat.server;


import ru.sj.network.chat.transport.Request;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * Created by Eugene Sinitsyn
 */

public interface ISession {
    ISessionId getId();
    ISessionsManager getManager();
    void close();

    Collection<Request> readData(ByteBuffer buffer);
}
