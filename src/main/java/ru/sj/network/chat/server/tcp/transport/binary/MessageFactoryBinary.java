package ru.sj.network.chat.server.tcp.transport.binary;

import ru.sj.network.chat.server.IMessage;
import ru.sj.network.chat.server.IMessageFactory;

/**
 * Created by Eugene Sinitsyn
 */

public class MessageFactoryBinary implements IMessageFactory {
    public IMessage createMessage(byte[] payload) {
        return new MessageBinary(payload);
    }
}
