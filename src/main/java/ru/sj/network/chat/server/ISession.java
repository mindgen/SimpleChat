package ru.sj.network.chat.server;

import java.util.UUID;

/**
 * Created by Eugene Sinitsyn
 */

public interface ISession {
    ISessionId getId();
    ISessionsManager getManager();
    void close();
}
