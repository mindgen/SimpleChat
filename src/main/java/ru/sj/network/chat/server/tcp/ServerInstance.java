package ru.sj.network.chat.server.tcp;

import ru.sj.network.chat.server.AlreadyStartedException;
import ru.sj.network.chat.server.ISession;
import ru.sj.network.chat.server.ServerWorker;
import ru.sj.network.chat.server.ThreadsServer;

import javax.xml.crypto.KeySelector;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;

/**
 * Created by Eugene Sinitsyn
 */

public class ServerInstance extends ThreadsServer {

    private int mBufferCapacity = 32;
    public void setBufferCapacity(int value) throws AlreadyStartedException {
        throwIfStarted();
        mBufferCapacity = value;
    }
    public int getBufferCapacity() { return mBufferCapacity; }

    @Override
    protected ServerWorker createWorker()
    {
        return new TcpServerWorker(this.getAddress(),
                new SessionsManagerImpl(),
                ByteBuffer.allocate(this.getBufferCapacity()));
    }
}

class TcpServerWorker extends ServerWorker {

    private ServerSocketChannel mServerSocket;
    private InetSocketAddress mAddress;
    private SessionsManagerImpl mManager;
    private ByteBuffer mBufer;

    public TcpServerWorker(InetSocketAddress address, SessionsManagerImpl manager, ByteBuffer buffer) {
        mAddress = address;
        mManager = manager;
        mBufer = buffer;
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
        while (eventSelector.select() > -1 && !this.IsTerminated()) {
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
        ISession curSession = (ISession)key.attachment();
        SocketChannel curSocket = (SocketChannel)key.channel();

        int bytesRead;
        try {
            do {
                bytesRead = curSocket.read(this.getBuffer());
            } while (bytesRead > 0);
        }
        catch (IOException e)
        {
            closeChannel(key);
            return;
        }

        this.getBuffer().flip();
        curSession.readData(this.getBuffer());
        this.getBuffer().clear();

        if (bytesRead < 0) closeChannel(key);
    }

    private ByteBuffer getBuffer() { return mBufer; }

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

    private void closeChannel(SelectionKey key)  {
        ISession curSession = (ISession)key.attachment();
        curSession.close();
        key.cancel();
        try {
            key.channel().close();
        }
        catch(IOException E) {}
    }

    @Override
    public String getName()
    {
        return "NoneBlockWrk";
    }
}
