package ru.sj.network.chat.server.tcp.transport.binary;

import ru.sj.network.chat.server.IMessage;
import ru.sj.network.chat.server.IMessageBuffer;
import ru.sj.network.chat.server.IMessageFactory;
import ru.sj.network.chat.server.IMessageTransport;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Eugene Sinitsyn
 */

public class TransportBinary implements IMessageTransport {

    private IMessageFactory mMsgFactory;
    public TransportBinary(IMessageFactory msgFactory) {
        mMsgFactory = msgFactory;
    }

    @Override
    public Collection<IMessage> decodeMessages(ByteBuffer buffer, IMessageBuffer msgBuffer) {
        List<IMessage> resultMessages = new ArrayList<IMessage>();
        msgBuffer.writeToBuffer(buffer);
        msgBuffer.flip();
        try {
            msgBuffer.mark();
            short msgLen = msgBuffer.getShort();
            byte[] msgPayload = new byte[msgLen];
            msgBuffer.array(msgPayload);
            resultMessages.add(mMsgFactory.createMessage(msgPayload));
        }
        catch (BufferUnderflowException e) {
            msgBuffer.reset();
        }
        msgBuffer.compact();
        return Collections.unmodifiableCollection(resultMessages);
    }

    public ByteBuffer encodeMessages(Collection<IMessage> messages) {
        return null;
    }
}
