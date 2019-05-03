package ru.sj.network.chat.server.tcp;

import ru.sj.network.chat.api.model.response.RealTimeResponse;
import ru.sj.network.chat.server.ISession;
import ru.sj.network.chat.server.ISessionId;
import ru.sj.network.chat.server.ISessionsManager;
import ru.sj.network.chat.transport.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Eugene Sinitsyn
 */

public class SessionImpl implements ISession {

    SessionIdImp mId;
    ISessionsManager mManager;
    IMessageBuffer reqBuffer;
    Queue<Response> responseStorage;
    BufferedResponseWriter buffWriter;

    Queue<Response> realTimeResponseStorage;
    ReentrantLock realTimeStorageLock;

    SelectionKey selKey;

    public SessionImpl(ISessionsManager manager, IMessageBuffer buffer,
                       Queue<Response> responseStorage, Queue<Response> realTimeResponseStorage,
                       SelectionKey selKey) {
        this.mId = SessionIdImp.generateNew();
        this.mManager = manager;
        this.reqBuffer = buffer;
        this.responseStorage = responseStorage;
        this.selKey = selKey;
        this.buffWriter = new BufferedResponseWriter();

        this.realTimeResponseStorage = realTimeResponseStorage;
        realTimeStorageLock = new ReentrantLock();
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
    public Queue<Request> readData(ByteBuffer buffer) throws InvalidProtocolException {
        return this.getManager().getTransport().decodeRequest(buffer, this);
    }

    @Override
    public void storeResponse(Response response) { responseStorage.add(response); setNeedWriteToSocket(true); }

    @Override
    public void storeRealTimeResponse(RealTimeResponse responseModel) {
        realTimeStorageLock.lock();
        try {
            Response newResponse = getManager().getTransport().createEmptyResponse();
            newResponse.setData(responseModel);
            this.realTimeResponseStorage.add(newResponse);
            setNeedWriteToSocket(true);
        }
        finally {
            realTimeStorageLock.unlock();
        }
    }

    @Override
    public void updateWriteBuffer() throws IOException {
        if (buffWriter.writeResponse(responseStorage.peek(), this.getManager().getTransport())) {
            responseStorage.remove();
        }
        else {
            realTimeStorageLock.lock();
            if (buffWriter.writeResponse(realTimeResponseStorage.peek(), this.getManager().getTransport())) {
                realTimeResponseStorage.remove();
            } else if (buffWriter.isEmpty()) {
                setNeedWriteToSocket(false);
            }
            realTimeStorageLock.unlock();
        }
    }

    @Override
    public ByteBuffer getWriteBuffer() {
        return buffWriter.getBuffer();
    }

    @Override
    public IMessageBuffer getRequestBuffer() {
        return this.reqBuffer;
    }

    void freeResources() {
        responseStorage.clear();
        realTimeStorageLock.lock();
        this.selKey = null;
        realTimeStorageLock.unlock();
    }

    private void setNeedWriteToSocket(boolean needWrite) {
        try {
            if (null != this.selKey) {
                if (needWrite)
                    this.selKey.interestOpsOr(SelectionKey.OP_WRITE);
                else
                    this.selKey.interestOpsAnd(~SelectionKey.OP_WRITE);
            }
        }
        catch (Exception E) {}
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

            stream.reset();
            transport.encodeResponse(response, stream);
            buffer = ByteBuffer.wrap(stream.getData(), 0, stream.size());

            return true;
        }

        ByteBuffer getBuffer() {
            return this.buffer;
        }

        boolean isEmpty() { return null == this.buffer || !this.buffer.hasRemaining(); }

        class ByteBufferOutputStream extends ByteArrayOutputStream {
            public byte[] getData() {
                return this.buf;
            }
        }
    }
}
