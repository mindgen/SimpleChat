package ru.sj.network.chat.client;

import ru.sj.network.chat.api.model.response.BaseResponse;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Eugene Sinitsyn
 */

public class FutureResponse {

    private BaseResponse response;
    private Lock lock = new ReentrantLock();
    private Condition readyCondition = lock.newCondition();

    public BaseResponse getResponse() {
        lock.lock();
        try {
            return this.response;
        }
        finally {
            lock.unlock();
        }
    }
    public boolean isReady() {
        return this.getResponse() != null;
    }

    public BaseResponse waitResponse() {
        lock.lock();
        try {
            while (!this.isReady())
                readyCondition.await();

            return this.response;
        }
        catch (InterruptedException E) {}
        finally {
            lock.unlock();
        }

        return null;
    }

    void setResponse(BaseResponse response) {
        lock.lock();
        try {
            this.response = response;
            readyCondition.signalAll();
        }
        finally {
            lock.unlock();
        }
    }
}
