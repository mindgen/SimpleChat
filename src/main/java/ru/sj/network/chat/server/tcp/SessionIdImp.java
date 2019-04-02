package ru.sj.network.chat.server.tcp;

import ru.sj.network.chat.server.ISessionId;

/**
 * Created by Eugene Sinitsyn
 */

public class SessionIdImp implements ISessionId {
    public SessionIdImp() {

    }

    public static SessionIdImp generateNew() {
        return new SessionIdImp();
    }
}
