package ru.sj.network.chat.server.tcp;

import ru.sj.network.chat.server.ISession;
import ru.sj.network.chat.server.ISessionId;
import ru.sj.network.chat.server.ISessionsManager;

import java.nio.channels.SocketChannel;
import java.util.HashMap;

/**
 * Created by Eugene Sinitsyn
 */

public class SessionsManagerImpl implements ISessionsManager {

    HashMap<ISessionId, ISession> mSessions;
    public SessionsManagerImpl() {
        mSessions = new HashMap<ISessionId, ISession>(1024);
    }

    public ISession openSession() {
        SessionImpl newSession = new SessionImpl(this);
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
}
