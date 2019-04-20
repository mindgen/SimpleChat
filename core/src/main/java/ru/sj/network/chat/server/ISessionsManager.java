package ru.sj.network.chat.server;

import ru.sj.network.chat.transport.INetworkTransport;

import java.nio.channels.SelectionKey;

/**
 * Created by Eugene Sinitsyn
 */

public interface ISessionsManager {
    ISession openSession(SelectionKey sessionKey);
    void closeSession(ISession session);

    ISession findById(ISessionId id);

    INetworkTransport getTransport();

    void setEventsHandler(ISessionsManagerEvents events);
}
