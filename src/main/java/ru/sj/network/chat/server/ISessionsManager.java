package ru.sj.network.chat.server;

import ru.sj.network.chat.transport.INetworkTransport;

/**
 * Created by Eugene Sinitsyn
 */

public interface ISessionsManager {
    ISession openSession();
    void closeSession(ISession session);

    ISession findById(ISessionId id);

    INetworkTransport getTransport();
}
