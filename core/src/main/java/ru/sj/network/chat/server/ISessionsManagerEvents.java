package ru.sj.network.chat.server;

/**
 * Created by Eugene Sinitsyn
 */

public interface ISessionsManagerEvents {
    void onOpenSession(ISession session);
    void onCloseSession(ISession session);
}
