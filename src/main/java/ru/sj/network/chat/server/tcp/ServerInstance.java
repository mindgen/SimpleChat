package ru.sj.network.chat.server.tcp;

import ru.sj.network.chat.server.IServer;

import java.io.IOException;
import java.net.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;

/**
 * Created by Eugene Sinitsyn
 */

public class ServerInstance implements IServer {

    private InetSocketAddress mAddress;
    public ServerInstance(InetSocketAddress address)
    {
        this.mAddress = address;
    }

    public void start() {
        for (Integer num = 0; num < mWorkers; ++num) {
            Thread wrkThread = new Thread(new TcpServerWorker(this.getAddress(), new SessionsManagerImpl()),
                                            String.format("WrkThread_%d", num));
            wrkThread.start();
        }
    }

    public InetSocketAddress getAddress() {
        return mAddress;
    }

    private Integer mWorkers;
    public Integer getWorkers() {
        return mWorkers;
    }

    public void setWorkers(Integer value) {
        mWorkers = value;
    }
}

class TcpServerWorker implements Runnable {

    private ServerSocketChannel mServerSocket;
    private InetSocketAddress mAddress;
    private SessionsManagerImpl mManager;

    public TcpServerWorker(InetSocketAddress address, SessionsManagerImpl manager) {
        mAddress = address;
        mManager = manager;
    }

    public void run() {
        try
        {
            configureServer();
            doWork();
        }
        catch (IOException e)
        {
            // TO-DO
            // Inform main thread about this
        }
    }

    private void doWork() throws IOException {
        Selector eventSelector = openSelector();
        if (null == eventSelector) {
            return;
        }

        mServerSocket.register(eventSelector, mServerSocket.validOps());
        while (eventSelector.select() > -1) {
            for (SelectionKey currentEvent : eventSelector.selectedKeys()) {
                if (!currentEvent.isValid()) continue;
                if (currentEvent.isAcceptable()) {
                    acceptConnection(currentEvent);
                }
                else if (currentEvent.isReadable()) {
                    readData(currentEvent);
                }
                else if (currentEvent.isWritable()) {
                    writeData(currentEvent);
                }
            }
            eventSelector.selectedKeys().clear();
        }

    }

    private void acceptConnection(SelectionKey key) throws IOException {
        SocketChannel newChannel = ((ServerSocketChannel)key.channel()).accept();
        configureSocket(newChannel);
        SelectionKey regKey = newChannel.register(key.selector(), SelectionKey.OP_READ);
        regKey.attach(mManager.openSession());
    }

    private void readData(SelectionKey key) {

    }

    private void writeData(SelectionKey key) {

    }

    private Selector openSelector() throws IOException {
        return Selector.open();
    }

    private void configureServer() throws IOException {
        mServerSocket = ServerSocketChannel.open();
        configureServerSocket(mServerSocket);
        mServerSocket.bind(mAddress);
    }

    private void configureServerSocket(ServerSocketChannel sckChannel) throws IOException {
        configureSocket(sckChannel);

        sckChannel.socket().setOption(StandardSocketOptions.SO_REUSEADDR, true);
        sckChannel.socket().setOption(StandardSocketOptions.SO_REUSEPORT, true);
    }

    private void configureSocket(AbstractSelectableChannel sck) throws IOException {
        sck.configureBlocking(false);
    }
}
