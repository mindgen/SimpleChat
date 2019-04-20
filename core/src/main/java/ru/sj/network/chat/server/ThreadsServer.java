package ru.sj.network.chat.server;

/**
 * Created by Eugene Sinitsyn
 */

public abstract class ThreadsServer extends BaseServer {

    private int mWorkersCount;
    public int getWorkersCount() { return mWorkersCount; }
    public void setWorkersCount(int value) throws AlreadyStartedException {
        throwIfStarted();
        mWorkersCount = value;
    }

    WorkerThread[] mWorkerThreads = null;
    @Override
    public void doStart() throws Exception {
        mWorkerThreads = new WorkerThread[this.getWorkersCount()];
        try {
            for (int num = 0; num < this.getWorkersCount(); ++num) {
                ServerWorker wrk = createWorker();
                wrk.setServer(this);
                mWorkerThreads[num] = new WorkerThread(wrk, String.format("%s_%d", wrk.getName(), num));
            }
        }
        catch (Exception ex)
        {
            mWorkerThreads = null;
            throw ex;
        }

        for (WorkerThread wrkThread : mWorkerThreads) {
            wrkThread.start();
        }
    }

    @Override
    public void doStop() throws Exception {
        for (WorkerThread wrkThread : mWorkerThreads) {
            if (wrkThread.isAlive())
                wrkThread.interrupt();
        }
        for (WorkerThread wrkThread : mWorkerThreads) {
            if (!wrkThread.isInterrupted())
                wrkThread.join();
            ServerWorker wrk = (ServerWorker)wrkThread.getRunnable();
            if (null != wrk.getStoredException()) {
                wrk.getStoredException().printStackTrace();
                wrk.setStoredException(null);
            }
        }
        mWorkerThreads = null;
    }

    protected abstract ServerWorker createWorker();

    class WorkerThread extends Thread {

        Runnable runTarget;
        public WorkerThread(Runnable target, String name) {
            super(target, name);
            runTarget = target;
        }

        Runnable getRunnable() {return runTarget; }
    }
}
