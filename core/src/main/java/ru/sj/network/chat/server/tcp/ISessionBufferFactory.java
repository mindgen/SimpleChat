package ru.sj.network.chat.server.tcp;

import ru.sj.network.chat.transport.IMessageBuffer;

/**
 * Created by Eugene Sinitsyn
 */

public interface ISessionBufferFactory {
    IMessageBuffer createRequestBuffer();
}
