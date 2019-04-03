package ru.sj.network.chat.server;

/**
 * Created by Eugene Sinitsyn
 */

public abstract class ServerWorker implements Runnable {
    public abstract String getName();

    public boolean IsTerminated() { return Thread.currentThread().isInterrupted(); }
}
