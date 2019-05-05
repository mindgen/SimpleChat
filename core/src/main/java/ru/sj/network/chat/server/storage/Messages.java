package ru.sj.network.chat.server.storage;

import org.springframework.stereotype.Component;

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

@Component
public class Messages extends LockedEntity{
    private AtomicInteger curId;

    public Messages() {
        super(new ReentrantReadWriteLock());
        this.msgContainer = new ArrayList<Message>();
        curId = new AtomicInteger(0);
    };

    public Messages(List<Message> msgContainer, ReadWriteLock lock) {
        super(lock);
        this.msgContainer = msgContainer;
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

    public TextMessage addTextMessage(String user, String text) {
        TextMessage newMsg = new TextMessage(user, this.getNextId(), text);
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

    public List<Message> getLast(int count) {
        this.getReadLock().lock();
        try {
            return this._getMessages(count);
        }
        finally {
            this.getReadLock().unlock();
        }
    }

    protected int getNextId() {
        return this.curId.incrementAndGet();
    }

    protected int _count() {
        return this.msgContainer.size();
    }

    protected void _add(Message msg) {
        this.msgContainer.add(msg);
    }

    protected Message _findById(int id) {
        int start_idx = Collections.binarySearch(this.msgContainer, new SearchMessage(id), (Message m1, Message m2) ->
        { return Integer.compare(m1.getId(), m2.getId()); } );

        return this.msgContainer.get(start_idx);
    }

    protected List<Message> _getMessages(int count) {
        int lastIdx = this.msgContainer.size() - 1;
        int startIdx = this.msgContainer.size() - count;
        if (startIdx < 0) startIdx = 0;
        return this.msgContainer.subList(startIdx, lastIdx);
    }

    private List<Message> msgContainer;
}
