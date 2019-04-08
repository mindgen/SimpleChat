package ru.sj.network.chat.server.tcp;

import ru.sj.network.chat.server.ISession;
import ru.sj.network.chat.server.ISessionId;
import ru.sj.network.chat.server.ISessionsManager;
import ru.sj.network.chat.transport.INetworkTransport;
import ru.sj.network.chat.transport.MessageBuffer;

import java.util.HashMap;

/**
 * Created by Eugene Sinitsyn
 */

public class SessionsManagerImpl implements ISessionsManager {

    INetworkTransport netTransport;
    ISessionBufferFactory bufFactory;
    HashMap<ISessionId, ISession> mSessions;
    public SessionsManagerImpl(INetworkTransport transport, ISessionBufferFactory bufFactory) {

        mSessions = new HashMap<ISessionId, ISession>(1024);
        netTransport = transport;
        this.bufFactory = bufFactory;
    }

    public ISession openSession() {
        SessionImpl newSession = new SessionImpl(this, this.bufFactory.createRequestBuffer());
        mSessions.put(newSession.getId(), newSession);
        return newSession;
    }

    public void closeSession(ISession session) {
        if (!this.equals(session.getManager())) return;

        ((SessionImpl)session).freeResources();
        mSessions.remove(session.getId());
    }

    public ISession findById(ISessionId id) {
        return mSessions.get(id);
    }

    public INetworkTransport getTransport() { return netTransport; }
}
