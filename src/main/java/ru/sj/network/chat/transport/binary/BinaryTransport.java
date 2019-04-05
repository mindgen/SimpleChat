package ru.sj.network.chat.transport.binary;

import ru.sj.network.chat.transport.IRequestBuilder;
import ru.sj.network.chat.transport.INetworkTransport;
import ru.sj.network.chat.transport.IRequestBuffer;
import ru.sj.network.chat.transport.Request;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Eugene Sinitsyn
 */

public class BinaryTransport implements INetworkTransport {

    private IRequestBuilder mMsgFactory;
    public BinaryTransport(IRequestBuilder msgFactory) {
        mMsgFactory = msgFactory;
    }

    @Override
    public Collection<Request> decodeRequest(ByteBuffer buffer, IRequestBuffer msgBuffer) {
        List<Request> resultMessages = new ArrayList<Request>();
        msgBuffer.writeToBuffer(buffer);
        msgBuffer.flip();
        try {
            msgBuffer.mark();
            short msgLen = msgBuffer.getShort();
            byte[] msgPayload = new byte[msgLen];
            msgBuffer.array(msgPayload);
            resultMessages.add(mMsgFactory.buildRequest(msgPayload));
        }
        catch (BufferUnderflowException e) {
            msgBuffer.reset();
        }
        msgBuffer.compact();
        return Collections.unmodifiableCollection(resultMessages);
    }

    @Override
    public ByteBuffer encodeRequest(Collection<Request> messages) {
        return null;
    }
}
