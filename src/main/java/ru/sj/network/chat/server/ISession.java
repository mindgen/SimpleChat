package ru.sj.network.chat.server;


import ru.sj.network.chat.transport.InvalidProtocolException;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * Created by Eugene Sinitsyn
 */

public interface ISession {
    ISessionId getId();
    ISessionsManager getManager();
    void close();

    Collection<Request> readData(ByteBuffer buffer) throws InvalidProtocolException;

    void storeResponse(Response response);

    void updateWriteBuffer() throws IOException;
    ByteBuffer getWriteBuffer();
}
