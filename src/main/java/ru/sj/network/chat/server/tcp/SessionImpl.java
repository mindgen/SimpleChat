package ru.sj.network.chat.server.tcp;

import ru.sj.network.chat.server.ISession;
import ru.sj.network.chat.server.ISessionId;
import ru.sj.network.chat.server.ISessionsManager;

import java.util.UUID;

/**
 * Created by Eugene Sinitsyn
 */

public class SessionImpl implements ISession {

    SessionIdImp mId;
    ISessionsManager mManager;

    public SessionImpl(ISessionsManager manager) {
        mId = SessionIdImp.generateNew();
        mManager = manager;
    }
    public ISessionId getId() {
        return mId;
    }

    public ISessionsManager getManager() {
        return mManager;
    }

    public void close() {
        getManager().closeSession(this);
    }

    void freeResources() {

    }
}
