package ru.sj.network.chat.server.tcp;

import ru.sj.network.chat.server.ISession;
import ru.sj.network.chat.server.ISessionId;
import ru.sj.network.chat.server.ISessionsManager;
import ru.sj.network.chat.transport.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Queue;

/**
 * Created by Eugene Sinitsyn
 */

public class SessionImpl implements ISession {

    SessionIdImp mId;
    ISessionsManager mManager;
    IRequestBuffer sessionBuffer;
    Queue<Response> responseStorage;
    BufferedResponseWriter buffWriter;

    public SessionImpl(ISessionsManager manager, IRequestBuffer buffer, Queue<Response> responseStorage) {
        mId = SessionIdImp.generateNew();
        mManager = manager;
        sessionBuffer = buffer;
        this.responseStorage = responseStorage;
        buffWriter = new BufferedResponseWriter();
    }

    @Override
    public ISessionId getId() {
        return mId;
    }

    @Override
    public ISessionsManager getManager() {
        return mManager;
    }

    @Override
    public void close() {
        getManager().closeSession(this);
    }

    @Override
    public Collection<Request> readData(ByteBuffer buffer) throws InvalidProtocolException {
        Collection<Request> requests = this.getManager().getTransport().decodeRequest(buffer, sessionBuffer);

        return requests;
    }

    @Override
    public void storeResponse(Response response) { responseStorage.add(response); }

    @Override
    public void updateWriteBuffer() throws IOException {
        if (buffWriter.writeResponse(responseStorage.peek(), this.getManager().getTransport())) {
            responseStorage.remove();
        }
    }

    @Override
    public ByteBuffer getWriteBuffer() {
        return buffWriter.getBuffer();
    }

    void freeResources() {
        responseStorage.clear();
    }

    class BufferedResponseWriter {
        BufferedResponseWriter() { stream = new ByteBufferOutputStream(); }

        ByteBuffer buffer;
        ByteBufferOutputStream stream;

        boolean writeResponse(Response response, INetworkTransport transport) throws IOException {
            if (null != buffer && buffer.position() > 0) {
                buffer.flip();
                return false;
            }

            if (null == response) {
                buffer.flip();
                return false;
            }

            transport.encodeResponse(response, stream);
            buffer = ByteBuffer.wrap(stream.getData());

            return true;
        }

        ByteBuffer getBuffer() {
            return this.buffer;
        }

        class ByteBufferOutputStream extends ByteArrayOutputStream {
            public byte[] getData() {
                return this.buf;
            }
        }
    }
}
