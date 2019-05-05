package ru.sj.network.chat.server;


import ru.sj.network.chat.api.model.response.RealTimeResponse;
import ru.sj.network.chat.transport.IMessageBuffer;
import ru.sj.network.chat.transport.InvalidProtocolException;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;

/**
 * Created by Eugene Sinitsyn
 */

public interface ISession {
    ISessionId getId();
    ISessionsManager getManager();
    void close();

    Queue<Request> readData(ByteBuffer buffer) throws InvalidProtocolException;

    void storeResponse(Response response);
    void storeRealTimeResponse(RealTimeResponse responseModel);

    void updateWriteBuffer() throws IOException;
    ByteBuffer getWriteBuffer();

    IMessageBuffer getRequestBuffer();

    public void freeResources();
}
