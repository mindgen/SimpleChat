package ru.sj.network.chat.server;

import java.nio.channels.SocketChannel;

/**
 * Created by Eugene Sinitsyn
 */

public interface ISessionsManager {
    ISession openSession();
    void closeSession(ISession session);

    ISession findById(ISessionId id);
}
