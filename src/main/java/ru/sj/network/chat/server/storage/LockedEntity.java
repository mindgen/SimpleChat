package ru.sj.network.chat.server.storage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Eugene Sinitsyn
 */

public abstract class LockedEntity {
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    protected Lock getReadLock() { return this.lock.readLock(); }
    protected Lock getWriteLock() { return this.lock.writeLock(); }
}
