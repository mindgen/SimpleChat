package ru.sj.network.chat.server;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * Created by Eugene Sinitsyn
 */

public interface IMessageTransport {
    Collection<IMessage> decodeMessages(ByteBuffer buffer, IMessageBuffer msgBuffer);
    ByteBuffer encodeMessages(Collection<IMessage> messages);
}
