package ru.sj.network.chat.server;

import java.lang.ref.WeakReference;

/**
 * Created by Eugene Sinitsyn
 */

public abstract class ServerWorker implements Runnable {
    public abstract String getName();

    public boolean IsTerminated() { return Thread.currentThread().isInterrupted(); }

    private Exception storedException = null;
    Exception getStoredException() { return this.storedException; }
    protected void setStoredException(Exception Ex) { this.storedException = Ex; }

    private WeakReference<IServer> server;
    void setServer(IServer srv) { this.server = new WeakReference<IServer>(srv); }
    protected IServer getServer() { return this.server.get(); }
}
