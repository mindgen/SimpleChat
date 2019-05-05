package ru.sj.network.chat.server.storage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

/**
 * Created by Eugene Sinitsyn
 */

@RunWith(MockitoJUnitRunner.class)
public class MessagesTest {
    private Messages msgStorage;

    @Mock
    ReadWriteLock lock;

    @Mock
    private Lock stubLock;

    @Mock
    List<Message> msgContainer;

    @Mock
    TextMessage tstMessage;

    @Mock
    TextMessage tstMessage2;

    @Mock
    List<Message> subList;

    String userName;
    String msgText;

    String userName2;
    String msgText2;

    @Before
    public void setup() {
        when(this.lock.readLock()).thenReturn(this.stubLock);
        when(this.lock.writeLock()).thenReturn(this.stubLock);

        doNothing().when(this.stubLock).lock();
        doNothing().when(this.stubLock).unlock();

        when(this.tstMessage.getId()).thenReturn(0);
        when(this.tstMessage2.getId()).thenReturn(1);

        userName = "TestUser";
        msgText = "Test Message";

        userName2 = "TestUser2";
        msgText2 = "Test Message 2";

        msgStorage = new Messages(this.msgContainer, this.lock);
    }

    @Test
    public void addTextMessage() {
        TextMessage msg = this.msgStorage.addTextMessage(userName, msgText);

        assertEquals(1, msg.getId());
        assertEquals(userName, msg.getUser());
        assertEquals(msgText, msg.getMessage());

        msg = this.msgStorage.addTextMessage(userName2, msgText2);

        assertEquals(2, msg.getId());
        assertEquals(userName2, msg.getUser());
        assertEquals(msgText2, msg.getMessage());

        verify(this.msgContainer, times(2)).add(any());
        verify(this.stubLock, times(2)).lock();
        verify(this.stubLock, times(2)).unlock();
    }

    @Test(expected = Exception.class)
    public void addTextMessageWithThrow() {
        when(this.msgContainer.add(any())).thenThrow(new Exception());

        try {
            this.msgStorage.addTextMessage(userName, msgText);
        }
        finally {
            verify(this.stubLock, times(1)).lock();
            verify(this.stubLock, times(1)).unlock();
        }
    }

    @Test
    public void getMessagesCount() {
        when(this.msgContainer.size()).thenReturn(9);
        assertEquals(9, this.msgStorage.count());
        verify(this.msgContainer, times(1)).size();

        verify(this.stubLock, times(1)).lock();
        verify(this.stubLock, times(1)).unlock();
    }

    @Test
    public void findMessageById() {
        when(this.msgContainer.get(0)).thenReturn(tstMessage);
        when(this.msgContainer.get(1)).thenReturn(tstMessage2);
        when(this.msgContainer.size()).thenReturn(3);

        Message msg = this.msgStorage.getById(0);
        assertEquals(tstMessage, msg);

        verify(this.msgContainer, atLeastOnce()).get(anyInt());
        verify(this.stubLock, times(1)).lock();
        verify(this.stubLock, times(1)).unlock();
    }

    @Test(expected = Exception.class)
    public void findMessageWithThrow() {
        when(this.msgContainer.get(anyInt())).thenThrow(new Exception());
        when(this.msgContainer.size()).thenReturn(1);

        try {
            this.msgStorage._findById(0);
        }
        finally {
            verify(this.stubLock, times(1)).lock();
            verify(this.stubLock, times(1)).unlock();
        }
    }

    @Test
    public void getLastMessages() {
        when(this.msgContainer.size()).thenReturn(10);
        when(this.msgContainer.subList(8,10)).thenReturn(subList);

        List<Message> result = this.msgStorage.getLast(2);
        assertEquals(subList, result);

        verify(this.stubLock, times(1)).lock();
        verify(this.stubLock, times(1)).unlock();
    }

    @Test(expected = Exception.class)
    public void getLastMessagesWithThrow() {
        when(this.msgContainer.size()).thenThrow(new Exception());

        try {
            this.msgStorage.getLast(2);
        }
        finally {
            verify(this.stubLock, times(1)).lock();
            verify(this.stubLock, times(1)).unlock();
        }
    }
}
