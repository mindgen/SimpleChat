package ru.sj.network.chat.server.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Integer.min;

/**
 * Created by Eugene Sinitsyn
 */

public class Messages extends LockedEntity{
    private AtomicInteger curId;

    public Messages(){
        this.msgContainer = new ArrayList<Message>();
        curId = new AtomicInteger(0);
    };

    public int count() {
        this.getReadLock().lock();
        try {
            return this._count();
        }
        finally {
            this.getReadLock().unlock();
        }
    }

    Message addTextMessage(String user, String text) {
        TextMessage newMsg = new TextMessage(user, this.curId.incrementAndGet(), text);
        this.getWriteLock().lock();
        try {
            this._add(newMsg);
            newMsg.timestamp = System.currentTimeMillis();
        }
        finally {
            this.getWriteLock().unlock();
        }

        return newMsg;
    }

    public Message getById(int id) {
        this.getReadLock().lock();
        try {
            return this._findById(id);
        }
        finally {
            this.getReadLock().unlock();
        }
    }

    public List<Message> getRange(long startTime, int count) {
        this.getReadLock().lock();
        try {
            return this._getMessage(startTime, count);
        }
        finally {
            this.getReadLock().unlock();
        }
    }

    protected int _count() {
        return this.msgContainer.size();
    }

    protected void _add(Message msg) {
        this.msgContainer.add(msg);
    }

    protected Message _findById(int id) {
        int start_idx = Collections.binarySearch(this.msgContainer, new SearchMessage(id), (Message m1, Message m2) ->
        { return m2.id == m1.id ? 0 : -1; } );

        return this.msgContainer.get(start_idx);
    }

    protected List<Message> _getMessage(long startTime, int count) {
        int start_idx = Collections.binarySearch(this.msgContainer, new SearchMessage(startTime), (Message m1, Message m2) ->
        {return m2.timestamp > m1.timestamp ? 0 : -1; } );

        return this.msgContainer.subList(start_idx, min(start_idx + count, this._count() - 1));
    }

    private ArrayList<Message> msgContainer;
}
