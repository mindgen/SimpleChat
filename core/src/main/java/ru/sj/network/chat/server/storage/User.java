package ru.sj.network.chat.server.storage;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Eugene Sinitsyn
 */

public class User extends LockedEntity {
    User() {
        super(new ReentrantReadWriteLock());
        this.init("");
    }

    User(String name) {
        super(new ReentrantReadWriteLock());
        this.init(name);
    }

    private void init(String name) {
        this.name = name;
    }

    private String name;
    public String getName() {
        String name;
        this.getReadLock().lock();
        try {
            name = this.name;
        }
        finally {
            this.getReadLock().unlock();
        }

        return name;
    }
    void setName(String name) {
        this.getWriteLock().lock();
        try {
            this.name = name;
        }
        finally {
            this.getWriteLock().unlock();
        }
    }
}
