package ru.sj.network.chat.transport.binary;

import ru.sj.network.chat.transport.*;

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

    private IModelSerializer serializer;
    public BinaryTransport(IModelSerializer serializer) {
        this.serializer = serializer;
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
            Request req = RequestSerializer.deserialize(msgPayload, this.serializer);
            if (null != req)
                resultMessages.add(req);
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

    @Override
    public Response createEmptyResponse() {
        return new BinaryResponse();
    }
}
