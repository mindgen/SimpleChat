package ru.sj.network.chat.server.tcp;

import ru.sj.network.chat.api.model.request.StringModel;
import ru.sj.network.chat.server.ISessionId;

/**
 * Created by Eugene Sinitsyn
 */

public class SessionIdImp implements ISessionId {
    public SessionIdImp() {

    }

    @Override
    public String toString() {
        return Integer.toString(this.hashCode());
    }

    public static SessionIdImp generateNew() {
        return new SessionIdImp();
    }
}
