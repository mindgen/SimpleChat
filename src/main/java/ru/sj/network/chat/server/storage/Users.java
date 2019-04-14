package ru.sj.network.chat.server.storage;

import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by Eugene Sinitsyn
 */

@Component
public class Users extends LockedEntity  {
    public Users(){
        this.users = new HashMap<String, User>();
    }

    public int count() {
        this.getReadLock().lock();
        try
        {
            return this._count();
        }
        finally {
            this.getReadLock().unlock();
        }
    }

    public User addUser(String name) throws UserExistException {
        this.getWriteLock().lock();
        try
        {
            User user = this._findByName(name);
            if (null != user)
                throw new UserExistException();

            user = new User(name);
            this.users.put(name, user);

            return user;
        }
        finally {
            this.getWriteLock().unlock();
        }
    }

    public User findByName(String name) {
        this.getReadLock().lock();
        try
        {
            return this._findByName(name);
        }
        finally {
            this.getReadLock().unlock();
        }
    }

    public void removeUser(String name) {
        this.getWriteLock().lock();
        try
        {
           this._removeUser(name);
        }
        finally {
            this.getWriteLock().unlock();
        }
    }

    public void changeUserName(User user, String newName) throws UserExistException {
        this.getWriteLock().lock();
        try
        {
            if (null != this._findByName(newName)) throw new UserExistException();

            this.users.remove(user.getName());
            user.setName(newName);
            this.users.put(newName, user);
        }
        finally {
            this.getWriteLock().unlock();
        }
    }

    protected int _count() { return users.size(); }
    protected User _findByName(String name) { return this.users.get(name); }
    protected void _removeUser(String name) { this.users.remove(name); }

    private HashMap<String, User> users;
}
