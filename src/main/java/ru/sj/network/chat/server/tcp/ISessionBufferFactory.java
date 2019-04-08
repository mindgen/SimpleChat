package ru.sj.network.chat.server.tcp;

import ru.sj.network.chat.transport.IRequestBuffer;

/**
 * Created by Eugene Sinitsyn
 */

public interface ISessionBufferFactory {
    IRequestBuffer createRequestBuffer();
}
