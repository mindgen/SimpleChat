package ru.sj.network.chat.transport.binary;

import ru.sj.network.chat.api.model.request.RequestBase;
import ru.sj.network.chat.transport.*;

import java.io.*;
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
    public Response createEmptyResponse() {
        return new BinaryResponse();
    }

    @Override
    public Request createRequest(RequestBase requestData) {
        return new BinaryRequest(requestData);
    }

    @Override
    public Collection<Request> decodeRequest(ByteBuffer buffer, IRequestBuffer msgBuffer) {
        List<Request> resultMessages = new ArrayList<Request>();
        msgBuffer.writeToBuffer(buffer);
        msgBuffer.flip();
        try {
            while (true) {
                msgBuffer.mark();
                short msgLen = msgBuffer.getShort();
                if (msgBuffer.remaining() >= msgLen) {
                    byte[] msgPayload = new byte[msgLen];
                    msgBuffer.array(msgPayload);
                    Object objectData = SerializerProxy.deserialize(msgPayload, this.serializer);
                    if (null != objectData) {
                        resultMessages.add(this.createRequest((RequestBase)objectData));
                    }
                }
                else {
                    msgBuffer.reset();
                    break;
                }
            }
        }
        catch (BufferUnderflowException e) {
            msgBuffer.reset();
        }
        msgBuffer.compact();
        return Collections.unmodifiableCollection(resultMessages);
    }

    @Override
    public ByteArrayOutputStream encodeRequest(Request req) {
        ByteArrayOutputStream result_stream = new ByteArrayOutputStream();
        try {
            ByteArrayOutputStream object_stream = new ByteArrayOutputStream();
            SerializerProxy.serialize(req.getData(), object_stream, this.serializer);

            DataOutputStream writer = new DataOutputStream(result_stream);
            writer.writeShort(object_stream.size());
            writer.flush();
            writer.close();

            object_stream.writeTo(result_stream);
        }
        catch (IOException e)
        {
            return null;
        }

        return result_stream;
    }

    public Response decodeResponse(InputStream inStream) throws IOException {
        DataInputStream reader = new DataInputStream(inStream);
        short size = reader.readShort();
        ByteArrayInputStream objectStream = new ByteArrayInputStream(reader.readNBytes(size));
        Object modelObject = SerializerProxy.deserialize(objectStream, this.serializer);
        if (modelObject instanceof Response)
            return (Response)modelObject;

        return null;
    }

    public void encodeResponse(Response response, OutputStream stream) throws IOException {
        SerializerProxy.serialize(response, stream, this.serializer);
    }
}
