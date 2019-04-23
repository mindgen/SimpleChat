package ru.sj.network.chat.client;

import ru.sj.network.chat.api.model.request.*;
import ru.sj.network.chat.transport.INetworkTransport;
import ru.sj.network.chat.transport.Request;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;
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

    public ChatClient(INetworkTransport transport, IChatEvents events) {
        this.transport = transport;
        this.events = events;

        this.socket_event = new Object();
        this.stopLoop = false;

        this.requestQueue = new LinkedList<>();
        this.responsesQueue = new LinkedList<>();
        this.queuesLock = new ReentrantLock();
    }

    public ChatClient(INetworkTransport transport, IChatEvents events,
                      Queue<Request> requests, Queue<FutureResponse> futureResponses,
                      Lock queuesLock) {
        this.transport = transport;
        this.events = events;

        this.socket_event = new Object();
        this.stopLoop = false;

        this.requestQueue = requests;
        this.responsesQueue = futureResponses;
        this.queuesLock = queuesLock;
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
        this.socket_event.wait(1000);
    }

    private void doCommands() throws InterruptedException {
        synchronized (this) {
            if (null == client_socket) return;
        }

        try {
            client_socket.register(socketEventSelector, SelectionKey.OP_READ);

            while (socketEventSelector.select() > -1) {
                for (SelectionKey currentEvent : socketEventSelector.selectedKeys()) {
                    if (!currentEvent.isValid()) continue;

                    if (currentEvent.isReadable()) readData(currentEvent);
                    if (currentEvent.isWritable()) writeData(currentEvent);
                }
                socketEventSelector.selectedKeys().clear();
            }
        }
        catch (Exception e) {
            closeChannel();
        }
    }

    void readData(SelectionKey key) {

    }

    void writeData(SelectionKey key) {
        ByteBuffer buff = (ByteBuffer)key.attachment();
        if (buff != null) {
            SocketChannel channel = (SocketChannel)key.channel();
            channel.write(buff.flip());
            if (!buff.compact().hasRemaining()) {
                key.attach(null);
            } else return;
        }

        this.queuesLock.lock();
        try {
            Request req = this.requestQueue.poll();
            if (null == req) {
                key.interestOpsAnd(~SelectionKey.OP_WRITE);
                return;
            }

            ByteArrayOutputStream this.transport.encodeRequest(req);
        }
        finally {
            this.queuesLock.unlock();
        }

    }

    private void closeChannel() {
        try {
            synchronized (this) {
                client_socket.close();
                client_socket = null;

                socketEventSelector.close();
                socketEventSelector = null;
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
            socketEventSelector = Selector.open();
        }
        catch (Exception Ex) {
            client_socket = null;
            socketEventSelector = null;
            return false;
        }

        if (null == socketEventSelector) {
            return false;
        }

        getEventsHandler().OnConnect();
        this.socket_event.notify();

        return true;
    }

    @Override
    public synchronized void disconnect() {
        if (null == socketEventSelector) return;

        try {
            socketEventSelector.close();
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
        FutureResponse futureResponse = new FutureResponse();

        this.queuesLock.lock();
        try {
            requestQueue.add(req);
            responsesQueue.add(futureResponse);
            client_socket.register(socketEventSelector, SelectionKey.OP_WRITE);
        }
        catch (ClosedChannelException e) { futureResponse = null; }
        finally {
            this.queuesLock.unlock();
        }

        return futureResponse;
    }

}
