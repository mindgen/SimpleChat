package ru.sj.network.chat.server.storage;

/**
 * Created by Eugene Sinitsyn
 */

public class User extends LockedEntity {
    User() {
        this.init("");
    }

    User(String name) {
        this.init(name);
    }

    private void init(String name) {
        this.name = name;
    }

    private String name;
    public String getName() {
        this.getReadLock().lock();
        String name = this.name;
        this.getReadLock().unlock();

        return name;
    }
    void setName(String name) {
        this.getWriteLock().lock();
        this.name = name;
        this.getWriteLock().unlock();
    }
}
