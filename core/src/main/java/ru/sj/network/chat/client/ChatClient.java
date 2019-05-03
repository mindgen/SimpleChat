package ru.sj.network.chat.client;

import ru.sj.network.chat.api.model.request.*;
import ru.sj.network.chat.api.model.response.BaseResponse;
import ru.sj.network.chat.api.model.response.NewMessageResponse;
import ru.sj.network.chat.api.model.response.RealTimeResponse;
import ru.sj.network.chat.transport.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ChatClient implements Runnable, IChatClient {

    private SocketChannel client_socket;
    private Selector socketEventSelector;

    private INetworkTransport transport;
    private IChatEvents events;

    private Object socket_event;
    private volatile boolean stopLoop;

    private Queue<Request> requestQueue;
    private Queue<FutureResponse> responsesQueue;
    private Lock queuesLock;

    private BufferedRequestWriter writer;
    private ByteBuffer readBuffer;
    private IMessageBuffer msgBuffer;

    private SelectionKey clientKey;

    public ChatClient(INetworkTransport transport, IChatEvents events,
                      ByteBuffer readBuffer, IMessageBuffer msgBuffer) {
        this.transport = transport;
        this.events = events;

        this.socket_event = new Object();
        this.stopLoop = false;

        this.requestQueue = new LinkedList<>();
        this.responsesQueue = new LinkedList<>();
        this.queuesLock = new ReentrantLock();

        this.writer = new BufferedRequestWriter();
        this.readBuffer = readBuffer;
        this.msgBuffer = msgBuffer;
    }

    public ChatClient(INetworkTransport transport, IChatEvents events,
                      Queue<Request> requests, Queue<FutureResponse> futureResponses,
                      Lock queuesLock, BufferedRequestWriter writer,
                      ByteBuffer readBuffer, IMessageBuffer msgBuffer) {
        this.transport = transport;
        this.events = events;

        this.socket_event = new Object();
        this.stopLoop = false;

        this.requestQueue = requests;
        this.responsesQueue = futureResponses;
        this.queuesLock = queuesLock;

        this.writer = writer;
        this.readBuffer = readBuffer;
        this.msgBuffer = msgBuffer;
    }

    SocketAddress getAddress() {

        SocketAddress address = null;

        try {
            address = this.client_socket != null ?
                    client_socket.getRemoteAddress() :
                    null;
        }
        catch (Exception e) {}

        return address;

    }

    // Main block loop
    @Override
    public void run() {
        while(!stopLoop) {
            try {
                waitConnect();
                doCommands();
            }
            catch (InterruptedException Ex) { break; }
        }
    }

    private void waitConnect() throws InterruptedException {
        synchronized (this.socket_event) {
            this.socket_event.wait(1000);
        }
    }

    private void doCommands() throws InterruptedException {
        synchronized (this) {
            if (null == client_socket) return;
        }

        try {
            while (socketEventSelector.select() > -1) {
                Set<SelectionKey> selectedKeys = socketEventSelector.selectedKeys();
                for (SelectionKey currentEvent : selectedKeys) {
                    if (!currentEvent.isValid()) continue;

                    if (currentEvent.isReadable()) readData(currentEvent);
                    if (currentEvent.isWritable()) writeData(currentEvent);
                }
                selectedKeys.clear();
            }
        }
        catch (Exception e) {
            closeChannel();
        }
    }

    void readData(SelectionKey key) throws IOException, InvalidProtocolException {
        SocketChannel curSocket = (SocketChannel)key.channel();

        if (!key.isValid()) {
            closeChannel();
            return;
        }

        int bytesRead;
        bytesRead = curSocket.read(this.getReadBuffer());

        this.getReadBuffer().flip();
        Queue<Response> responses = this.transport.decodeResponse(this.getReadBuffer(), this.getMessageBuffer());
        this.getReadBuffer().clear();

        for (Response response : responses) {
            if (null != response) {
                BaseResponse dataResponse = (BaseResponse)response.getData();
                if (dataResponse instanceof RealTimeResponse) {
                    if (dataResponse instanceof NewMessageResponse) {
                        NewMessageResponse newMsgResponse = (NewMessageResponse)dataResponse;
                        this.getEventsHandler().OnNewMessage(newMsgResponse.getMessage());
                    }
                } else {
                    FutureResponse curResponse = this.pollFutureResponse();
                    curResponse.setResponse(dataResponse);
                }
            }
        }

        if (bytesRead < 0) {
            closeChannel();
        }
    }

    void writeData(SelectionKey key) throws IOException {
        SocketChannel curSocket = (SocketChannel)key.channel();

        ByteBuffer curBuff = writer.getBuffer();
        if (null == curBuff || 0 == curBuff.position()) {
            if (writer.writeRequest(this.getRequest(key, false), this.transport)) {
                this.getRequest(key, true);
            }
            curBuff = writer.getBuffer();
        }

        if (null == curBuff) return;

        curSocket.write(curBuff);
        curBuff.compact();
    }

    private void closeChannel() {
        try {
            synchronized (this) {
                if (null != client_socket) {
                    client_socket.close();
                    client_socket = null;
                }

                if (null != socketEventSelector) {
                    socketEventSelector.close();
                    socketEventSelector = null;
                }
                this.queuesLock.lock();
                try {
                    requestQueue.clear();
                    for (FutureResponse future : responsesQueue) {
                        future.setResponse(null);
                    }
                    responsesQueue.clear();
                }
                finally {
                    this.queuesLock.unlock();
                }
                msgBuffer.clear();
                readBuffer.clear();

                notifyAll();
            }

            getEventsHandler().OnDisconnect();

        }
        catch (Exception e) {}
    }

    // IChatClient
    @Override
    public synchronized boolean connect(SocketAddress endpoint) {
        try {
            client_socket = SocketChannel.open(endpoint);
            client_socket.configureBlocking(false);
            socketEventSelector = Selector.open();

            clientKey = client_socket.register(socketEventSelector, SelectionKey.OP_READ);
        }
        catch (Exception Ex) {
            client_socket = null;
            socketEventSelector = null;
            return false;
        }

        if (null == socketEventSelector) {
            return false;
        }

        synchronized (this.socket_event) {
            this.socket_event.notify();
        }
        getEventsHandler().OnConnect();

        return true;
    }

    @Override
    public synchronized void disconnect() {
        if (null == socketEventSelector) return;

        try {
            socketEventSelector.close();
            wait();
        }
        catch (Exception e) {}
    }

    @Override
    public void stop() {
        stopLoop = true;
        disconnect();
    }

    @Override
    public synchronized boolean isConnected() {
        return this.client_socket != null && this.client_socket.isConnected();
    }

    @Override
    public FutureResponse registration(String name) {
        RegistrationRequest req = (RegistrationRequest) RequestFactory.createRequest(RequestType.Registration);
        req.setName(name);

        return this.doRequest(this.createRequest(req));
    }

    @Override
    public FutureResponse changeUserName(String name) {
        ChangeNameRequest req = (ChangeNameRequest) RequestFactory.createRequest(RequestType.ChangeName);
        req.setName(name);

        return this.doRequest(this.createRequest(req));
    }

    @Override
    public FutureResponse getUsersCount() {
        GetUsersCountRequest req = (GetUsersCountRequest)RequestFactory.createRequest(RequestType.GetUsersCount);

        return this.doRequest(this.createRequest(req));
    }


    @Override
    public FutureResponse sendMessage(String text) {
        SendMsgRequest req = (SendMsgRequest)RequestFactory.createRequest(RequestType.SendMessage);
        req.setMessageText(text);

        return this.doRequest(this.createRequest(req));
    }

    @Override
    public IChatEvents getEventsHandler() {
        return this.events;
    }

    // Internal helpers

    private Request createRequest(RequestBase model) {
        return this.transport.createRequest(model, null);
    }

    private FutureResponse doRequest(Request req) {
        return addRequest(req);
    }

    FutureResponse addRequest(Request req) {
        FutureResponse futureResponse = new FutureResponse();

        this.queuesLock.lock();
        try {
            requestQueue.add(req);
            responsesQueue.add(futureResponse);
            clientKey.interestOpsOr(SelectionKey.OP_WRITE);

            socketEventSelector.wakeup();
        }
        catch (Exception Ex) { return null;}
        finally {
            this.queuesLock.unlock();
        }

        return futureResponse;
    }

    Request getRequest(SelectionKey curKey, boolean remove) {
        this.queuesLock.lock();
        try {
            Request req = remove ? requestQueue.poll() : requestQueue.peek();
            if (null == req) {
                curKey.interestOpsAnd(~SelectionKey.OP_WRITE);
            }

            return req;
        }
        finally {
            this.queuesLock.unlock();
        }
    }

    FutureResponse pollFutureResponse() {
        this.queuesLock.lock();
        try {
            return responsesQueue.poll();
        }
        finally {
            this.queuesLock.unlock();
        }
    }

    ByteBuffer getReadBuffer() { return this.readBuffer; }
    IMessageBuffer getMessageBuffer() { return this.msgBuffer; }

    class BufferedRequestWriter {
        BufferedRequestWriter() { stream = new ByteBufferOutputStream(); }

        ByteBuffer buffer;
        ByteBufferOutputStream stream;

        boolean writeRequest(Request req, INetworkTransport transport) throws IOException {
            if (null != buffer && buffer.position() > 0) {
                buffer.flip();
                return false;
            }

            if (null == req) {
                buffer.flip();
                return false;
            }

            stream.reset();
            transport.encodeRequest(req, stream);
            buffer = ByteBuffer.wrap(stream.getData(), 0, stream.size());

            return true;
        }

        ByteBuffer getBuffer() { return this.buffer; }

        boolean isEmpty() { return null == this.buffer || !this.buffer.hasRemaining(); }

        class ByteBufferOutputStream extends ByteArrayOutputStream {
            public byte[] getData() {
                return this.buf;
            }
        }

        void clear() {
            buffer = null;
            stream.reset();
        }
    }

}
