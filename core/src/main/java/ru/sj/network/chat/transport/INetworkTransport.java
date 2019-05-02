package ru.sj.network.chat.transport;

import ru.sj.network.chat.api.model.request.RequestBase;
import ru.sj.network.chat.server.ISession;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by Eugene Sinitsyn
 */

public interface INetworkTransport {
    Request decodeRequest(ByteBuffer buffer, ISession session) throws InvalidProtocolException;
    void encodeRequest(Request req, ByteArrayOutputStream result_stream);

    Response decodeResponse(ByteBuffer buffer, IMessageBuffer msgBuffer) throws InvalidProtocolException;
    void encodeResponse(Response response, OutputStream stream) throws IOException;

    Response createEmptyResponse();
    Request createRequest(RequestBase requestData, ISession session);
}
