package ru.sj.network.chat.server.tcp;

import ru.sj.network.chat.transport.IMessageBuffer;
import ru.sj.network.chat.transport.MessageBuffer;

/**
 * Created by Eugene Sinitsyn
 */

public class SessionBufferFactoryImpl implements ISessionBufferFactory {
    @Override
    public IMessageBuffer createRequestBuffer() {
        return new MessageBuffer();
    }
}
