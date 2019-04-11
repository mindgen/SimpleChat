package ru.sj.network.chat.server.tcp;

import ru.sj.network.chat.server.*;
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
                new SessionsManagerImpl(new BinaryTransport(new ObjectModelSerializer()),
                                        new SessionBufferFactoryImpl()),
                ByteBuffer.allocate(this.getBufferCapacity()));
    }

    private class TcpServerWorker extends ServerWorker {

        private ServerSocketChannel mServerSocket;
        private InetSocketAddress mAddress;
        private SessionsManagerImpl mManager;
        private ByteBuffer mBuffer;

        public TcpServerWorker(InetSocketAddress address, SessionsManagerImpl manager,
                               ByteBuffer Buffer) {
            mAddress = address;
            mManager = manager;
            mBuffer = Buffer;
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
                    else {
                        if (currentEvent.isReadable()) readData(currentEvent);
                        if (currentEvent.isWritable()) writeData(currentEvent);
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

            if (!key.isValid()) {
                closeChannel(key);
                return;
            }

            int bytesRead;
            try {
                bytesRead = curSocket.read(this.getBuffer());
            }
            catch (IOException e)
            {
                closeChannel(key);
                return;
            }

            this.getBuffer().flip();
            Collection<Request> requests = curSession.readData(this.getBuffer());
            this.getBuffer().clear();

            for (Request req: requests) {
                Response response = ExecutionContext.getInstance().getExecutor().executeCmds(req,
                        curSession.getManager().getTransport(), ExecutionContext.getInstance().getRequestController());

                curSession.storeResponse(response);
                key.interestOpsAnd(SelectionKey.OP_WRITE);
            }

            if (bytesRead < 0) {
                closeChannel(key);
            }
        }

        private ByteBuffer getBuffer() { return mBuffer; }

        private void writeData(SelectionKey key) {
            ISession curSession = (ISession)key.attachment();
            SocketChannel curSocket = (SocketChannel)key.channel();

            if (!key.isValid()) {
                closeChannel(key);
                return;
            }

            key.interestOpsAnd(~SelectionKey.OP_WRITE);
            while (true)
            {
                try {
                    curSession.updateWriteBuffer();
                    curSocket.write(curSession.getWriteBuffer());
                }
                catch (Exception E)
                {
                    closeChannel(key);
                    return;
                }
                curSession.getWriteBuffer().compact();
                if (curSession.getWriteBuffer().position() > 0) {
                    key.interestOpsAnd(SelectionKey.OP_WRITE);
                    break;
                }
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

            sckChannel.socket().setOption(StandardSocketOptions.SO_REUSEADDR, true);
            sckChannel.socket().setOption(StandardSocketOptions.SO_REUSEPORT, true);
        }

        private void configureSocket(AbstractSelectableChannel sck) throws IOException {
            sck.configureBlocking(false);
        }

        private void cancelSelectedKey(SelectionKey key) {
            key.cancel();
        }

        private void closeChannel(SelectionKey key)  {
            ISession curSession = (ISession)key.attachment();
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
