package ru.sj.network.chat.server.tcp;

import ru.sj.network.chat.server.ISession;
import ru.sj.network.chat.server.ISessionId;
import ru.sj.network.chat.server.ISessionsManager;
import ru.sj.network.chat.transport.IRequestBuffer;
import ru.sj.network.chat.transport.Request;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

/**
 * Created by Eugene Sinitsyn
 */

public class SessionImpl implements ISession {

    SessionIdImp mId;
    ISessionsManager mManager;
    IRequestBuffer sessionBuffer;

    public SessionImpl(ISessionsManager manager, IRequestBuffer buffer) {
        mId = SessionIdImp.generateNew();
        mManager = manager;
        sessionBuffer = buffer;
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

    public Collection<Request> readData(ByteBuffer buffer) {
        Collection<Request> requests = this.getManager().getTransport().decodeRequest(buffer, sessionBuffer);

        return requests;
    }

    void freeResources() {

    }
}
