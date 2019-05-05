package ru.sj.network.chat.server.storage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by Eugene Sinitsyn
 */

public abstract class LockedEntity {

    public LockedEntity(ReadWriteLock lock) {
        this.lock = lock;
    }
    private ReadWriteLock lock;

    protected Lock getReadLock() { return this.lock.readLock(); }
    protected Lock getWriteLock() { return this.lock.writeLock(); }

    public ReadWriteLock getLocker() { return this.lock; }
}
