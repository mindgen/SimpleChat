package ru.sj.network.chat.server.tcp;

import ru.sj.network.chat.server.ISession;
import ru.sj.network.chat.server.ISessionId;
import ru.sj.network.chat.server.ISessionsManager;
import ru.sj.network.chat.server.ISessionsManagerEvents;
import ru.sj.network.chat.transport.INetworkTransport;
import ru.sj.network.chat.transport.MessageBuffer;
import ru.sj.network.chat.transport.Response;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Eugene Sinitsyn
 */

public class SessionsManagerImpl implements ISessionsManager {

    INetworkTransport netTransport;
    ISessionBufferFactory bufFactory;
    HashMap<ISessionId, ISession> mSessions;
    ISessionsManagerEvents events_handler = null;

    public SessionsManagerImpl(INetworkTransport transport, ISessionBufferFactory bufFactory) {

        mSessions = new HashMap<ISessionId, ISession>(1024);
        netTransport = transport;
        this.bufFactory = bufFactory;
    }

    @Override
    public ISession openSession(SelectionKey sessionKey) {
        SessionImpl newSession = new SessionImpl(this, this.bufFactory.createRequestBuffer(),
                                                    new LinkedList<>(), new LinkedList<>(), sessionKey);
        mSessions.put(newSession.getId(), newSession);
        if (null != this.events_handler) this.events_handler.onOpenSession(newSession);
        return newSession;
    }

    @Override
    public void closeSession(ISession session) {
        if (!this.equals(session.getManager())) return;

        if (null != this.events_handler) this.events_handler.onCloseSession(session);
        ((SessionImpl)session).freeResources();
        mSessions.remove(session.getId());
    }

    @Override
    public ISession findById(ISessionId id) {
        return mSessions.get(id);
    }

    @Override
    public INetworkTransport getTransport() { return netTransport; }

    @Override
    public void setEventsHandler(ISessionsManagerEvents events) {
        this.events_handler = events;
    }
}
