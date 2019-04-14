package ru.sj.network.chat.transport.binary;

import ru.sj.network.chat.api.model.request.RequestBase;
import ru.sj.network.chat.api.model.response.BaseResponse;
import ru.sj.network.chat.server.ISession;
import ru.sj.network.chat.transport.*;

import java.io.*;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

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
    public Request createRequest(RequestBase requestData, ISession session) {
        return new BinaryRequest(requestData, session);
    }

    @Override
    public Request decodeRequest(ByteBuffer buffer, ISession session) throws InvalidProtocolException {
        IRequestBuffer msgBuffer = session.getRequestBuffer();
        msgBuffer.writeToBuffer(buffer);
        msgBuffer.flip();
        Request resultReq = null;
        try {
            while (true) {
                msgBuffer.mark();
                short msgLen = msgBuffer.getShort();
                if (msgBuffer.remaining() >= msgLen) {
                    byte[] msgPayload = new byte[msgLen];
                    msgBuffer.array(msgPayload);
                    Object objectData = SerializerProxy.deserialize(msgPayload, this.serializer);
                    if (null != objectData) {
                        resultReq = this.createRequest((RequestBase)objectData, session);
                    }
                    else {
                        throw new InvalidProtocolException();
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
        return resultReq;
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
        if (modelObject instanceof BaseResponse) {
            Response response = createEmptyResponse();
            response.setData(modelObject);

            return response;
        }
        else throw new IOException();
    }

    public void encodeResponse(Response response, OutputStream stream) throws IOException {
        ByteArrayOutputStream object_stream = new ByteArrayOutputStream();
        SerializerProxy.serialize(response.getData(), object_stream, this.serializer);

        DataOutputStream writer = new DataOutputStream(stream);
        writer.writeShort(object_stream.size());
        writer.flush();
        writer.close();

        object_stream.writeTo(stream);
    }
}
