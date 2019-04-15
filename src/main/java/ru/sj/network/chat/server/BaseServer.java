package ru.sj.network.chat.server;

import java.net.InetSocketAddress;

/**
 * Created by Eugene Sinitsyn
 */

public abstract class BaseServer implements IServer {

    boolean mRunning = false;

    @Override
    public synchronized void start() throws Exception
    {
        throwIfStarted();
        try {
            doStart();
            mRunning = true;
        }
        catch (Exception ex)
        {
            mRunning = false;
            throw ex;
        }
    }
    @Override
    public synchronized void stop() throws Exception
    {
        if (!mRunning) return;
        mRunning = false;
        doStop();
    }

    @Override
    public synchronized boolean isRunning() {return mRunning; }

    abstract protected void doStart() throws Exception;
    abstract protected void doStop() throws Exception;


    private InetSocketAddress mAddress;
    @Override
    public synchronized void setAddress(InetSocketAddress address) throws AlreadyStartedException {
        throwIfStarted();
        mAddress = address;
    }
    @Override
    public synchronized InetSocketAddress getAddress() { return mAddress; }

    protected void throwIfStarted() throws AlreadyStartedException {
        if (this.isRunning()) throw new AlreadyStartedException();
    };
}
