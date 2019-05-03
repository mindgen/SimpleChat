package ru.sj.network.chat.server.tcp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sj.network.chat.server.*;
import ru.sj.network.chat.transport.InvalidProtocolException;
import ru.sj.network.chat.transport.ObjectModelSerializer;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;
import ru.sj.network.chat.transport.binary.BinaryTransport;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Collection;
import java.util.Queue;
import java.util.Set;

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

    private RequestExecutor executor;
    public ServerInstance(RequestExecutor executor) {
        this.executor = executor;
    }

    @Override
    protected ServerWorker createWorker()
    {
        return new TcpServerWorker(this.executor, this.getAddress(),
                new SessionsManagerImpl(new BinaryTransport(new ObjectModelSerializer()),
                                        new SessionBufferFactoryImpl()),
                ByteBuffer.allocate(this.getBufferCapacity()));
    }

    private class TcpServerWorker extends ServerWorker {

        private RequestExecutor executor;

        private ServerSocketChannel mServerSocket;
        private InetSocketAddress mAddress;
        private SessionsManagerImpl mManager;
        private ByteBuffer mBuffer;

        public TcpServerWorker(RequestExecutor executor, InetSocketAddress address, SessionsManagerImpl manager,
                               ByteBuffer Buffer) {
            mAddress = address;
            mManager = manager;
            mBuffer = Buffer;
            this.executor = executor;
            this.mManager.setEventsHandler(this.executor.getManager());
        }

        public void run() {
            try
            {
                configureServer();
                doWork();
            }
            catch (IOException e)
            {
               this.setStoredException(e);
               try {
                this.getServer().stop();
               }
               catch (InterruptedException Ex) {}
               catch (Exception Ex) { System.exit(100); }
            }
        }

        private void doWork() throws IOException {
            Selector eventSelector = openSelector();
            if (null == eventSelector) {
                return;
            }

            mServerSocket.register(eventSelector, mServerSocket.validOps());
            while (eventSelector.select() > -1 && !this.IsTerminated()) {
                Set<SelectionKey> selectedKeys = eventSelector.selectedKeys();
                for (SelectionKey currentEvent : selectedKeys) {
                    if (!currentEvent.isValid()) continue;
                    try {
                        if (currentEvent.isAcceptable()) {
                            acceptConnection(currentEvent);
                        }
                        else {
                            if (currentEvent.isReadable()) readData(currentEvent);
                            if (currentEvent.isWritable()) writeData(currentEvent);
                        }
                    }
                    catch (Exception e)
                    {
                        closeChannel(currentEvent);
                    }
                }
                selectedKeys.clear();
            }

        }

        private void acceptConnection(SelectionKey key) throws IOException {
            SocketChannel newChannel = ((ServerSocketChannel)key.channel()).accept();
            configureSocket(newChannel);
            SelectionKey regKey = newChannel.register(key.selector(), SelectionKey.OP_READ);
            regKey.attach(mManager.openSession(regKey));
        }

        private void readData(SelectionKey key) throws IOException, InvalidProtocolException {
            ISession curSession = (ISession)key.attachment();
            SocketChannel curSocket = (SocketChannel)key.channel();

            if (!key.isValid()) {
                closeChannel(key);
                return;
            }

            int bytesRead;
            bytesRead = curSocket.read(this.getBuffer());

            this.getBuffer().flip();
            Queue<Request> requests = curSession.readData(this.getBuffer());
            this.getBuffer().clear();

            for (Request request : requests) {
                Response response = executor.executeCmds(request,
                        curSession.getManager().getTransport());

                curSession.storeResponse(response);
            }

            if (bytesRead < 0) {
                closeChannel(key);
            }
        }

        private ByteBuffer getBuffer() { return mBuffer; }

        private void writeData(SelectionKey key) throws IOException {
            ISession curSession = (ISession)key.attachment();
            SocketChannel curSocket = (SocketChannel)key.channel();

            if (!key.isValid()) {
                closeChannel(key);
                return;
            }

            while (true)
            {
                curSession.updateWriteBuffer();
                ByteBuffer curBuffer = curSession.getWriteBuffer();
                if (null == curBuffer) break;

                int wCnt = curSocket.write(curBuffer);

                curBuffer.compact();
                if (curBuffer.position() > 0) {
                    break;
                } else if (0 == wCnt) break;
            }
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

            sckChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            if (sckChannel.supportedOptions().contains(StandardSocketOptions.SO_REUSEPORT))
                sckChannel.setOption(StandardSocketOptions.SO_REUSEPORT, true);
        }

        private void configureSocket(AbstractSelectableChannel sck) throws IOException {
            sck.configureBlocking(false);
        }

        private void cancelSelectedKey(SelectionKey key) {
            key.cancel();
        }

        private void closeChannel(SelectionKey key)  {
            ISession curSession = (ISession)key.attachment();
            key.attach(null);
            if (null != curSession)
                curSession.close();
            cancelSelectedKey(key);
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
}
