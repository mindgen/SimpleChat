package ru.sj.network.chat.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.sj.network.chat.server.ChatManager;
import ru.sj.network.chat.server.ISession;
import ru.sj.network.chat.server.storage.*;
import ru.sj.network.chat.server.tcp.SessionImpl;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by Eugene Sinitsyn
 */

@RunWith(MockitoJUnitRunner.class)
public class ChatManagerTest {
    ChatManager manager;

    ChatRoom chat;

    @Mock
    Users users;

    @Mock
    Messages messages;

    @Mock
    Map<ISession, User> mapping;

    @Mock
    ReadWriteLock stateLock;

    @Mock
    Lock readStateLock;

    @Mock
    Lock writeStateLock;

    @Mock
    ReadWriteLock msgLock;

    @Mock
    Lock msgStubLock;

    @Mock
    ISession session;

    @Mock
    User user;

    @Mock
    TextMessage msg;

    @Before
    public void setup() {
        when(this.stateLock.readLock()).thenReturn(this.readStateLock);
        when(this.stateLock.writeLock()).thenReturn(this.writeStateLock);

        doNothing().when(this.readStateLock).lock();
        doNothing().when(this.readStateLock).unlock();

        doNothing().when(this.writeStateLock).lock();
        doNothing().when(this.writeStateLock).unlock();

        when(this.messages.getLocker()).thenReturn(this.msgLock);
        when(this.msgLock.readLock()).thenReturn(this.msgStubLock);

        this.chat = new ChatRoom(this.users, this.messages);
        this.manager = new ChatManager(this.chat, this.mapping, this.stateLock);
    }

    @Test
    public void registrationNewFreeUser() throws UserExistException, AlreadyRegisteredException {
        when(this.users.addUser(anyString())).thenReturn(user);

        this.manager.registerUser(this.session, "NewUserName");

        verify(this.mapping, times(1)).get(this.session);
        verify(this.mapping, times(1)).put(this.session, user);
        verify(this.messages, times(1)).getLast(100);

        verify(this.writeStateLock, times(1)).lock();
        verify(this.writeStateLock, times(1)).unlock();

        verify(this.readStateLock, times(1)).lock();
        verify(this.readStateLock, times(1)).unlock();

        verify(this.msgStubLock, times(1)).lock();
        verify(this.msgStubLock, times(1)).unlock();
    }

    @Test(expected = UserExistException.class)
    public void registrationExistingUser() throws UserExistException, AlreadyRegisteredException {
        when(this.mapping.get(this.session)).thenReturn(null);
        when(this.users.addUser(anyString())).thenThrow(UserExistException.class);
        try {
            this.manager.registerUser(this.session, "NewUserName");
        }
        finally {
            verify(this.writeStateLock, times(0)).lock();
            verify(this.writeStateLock, times(0)).unlock();

            verify(this.readStateLock, times(1)).lock();
            verify(this.readStateLock, times(1)).unlock();
        }
    }

    @Test(expected = AlreadyRegisteredException.class)
    public void registrationAlreadyRegisteredUser() throws UserExistException, AlreadyRegisteredException {
        when(this.mapping.get(this.session)).thenReturn(this.user);

        try {
            this.manager.registerUser(this.session, "NewUserName");
        }
        finally {
            verify(this.writeStateLock, times(0)).lock();
            verify(this.writeStateLock, times(0)).unlock();

            verify(this.readStateLock, times(1)).lock();
            verify(this.readStateLock, times(1)).unlock();
        }
    }

    @Test
    public void changeNameExistedSessionFreeUserName() throws UserExistException, UnauthorizedAccess {
        when(this.mapping.get(this.session)).thenReturn(this.user);
        doNothing().when(this.users).changeUserName(this.user, "NewName");

        this.manager.changeUserName(this.session, "NewName");

        verify(this.readStateLock, times(1)).lock();
        verify(this.readStateLock, times(1)).unlock();

        verify(this.writeStateLock, times(0)).lock();
        verify(this.writeStateLock, times(0)).unlock();
    }

    @Test(expected = UnauthorizedAccess.class)
    public void changeNameNoneAuthorized() throws UserExistException, UnauthorizedAccess {
        when(this.mapping.get(this.session)).thenReturn(null);

        try {
            this.manager.changeUserName(this.session, "NewName");
        }
        finally {
            verify(this.readStateLock, times(1)).lock();
            verify(this.readStateLock, times(1)).unlock();

            verify(this.writeStateLock, times(0)).lock();
            verify(this.writeStateLock, times(0)).unlock();
        }
    }

    @Test
    public void getUsersCountExistedSession() throws UnauthorizedAccess {
        when(this.mapping.get(this.session)).thenReturn(this.user);

        this.manager.getUsersCount(this.session);

        verify(this.users, times(1)).count();
        verify(this.readStateLock, times(1)).lock();
        verify(this.readStateLock, times(1)).unlock();

        verify(this.writeStateLock, times(0)).lock();
        verify(this.writeStateLock, times(0)).unlock();
    }

    @Test(expected = UnauthorizedAccess.class)
    public void getUsersCountNoneAuthorized() throws UnauthorizedAccess {
        when(this.mapping.get(this.session)).thenReturn(null);

        try {
            this.manager.getUsersCount(this.session);
        }
        finally {
            verify(this.users, times(0)).count();
            verify(this.readStateLock, times(1)).lock();
            verify(this.readStateLock, times(1)).unlock();

            verify(this.writeStateLock, times(0)).lock();
            verify(this.writeStateLock, times(0)).unlock();
        }
    }

    @Test(expected = UnauthorizedAccess.class)
    public void sendMessageNoneAuthorized() throws UnauthorizedAccess {
        when(this.mapping.get(this.session)).thenReturn(null);

        try {
            this.manager.sendMessage(this.session, "Text");
        }
        finally {
            verify(this.messages, times(0)).addTextMessage(anyString(), anyString());
            verify(this.readStateLock, times(1)).lock();
            verify(this.readStateLock, times(1)).unlock();

            verify(this.writeStateLock, times(0)).lock();
            verify(this.writeStateLock, times(0)).unlock();
        }
    }

    @Test
    public void sendMessageExistedSession() throws UnauthorizedAccess {
        when(this.mapping.get(this.session)).thenReturn(this.user);
        when(this.user.getName()).thenReturn("User");
        when(this.messages.addTextMessage(anyString(), anyString())).thenReturn(this.msg);
        when(this.msg.getUser()).thenReturn("User");
        when(this.msg.getMessage()).thenReturn("Text");
        doNothing().when(this.mapping).forEach(any());

        this.manager.sendMessage(this.session, "Text");

        verify(this.mapping, times(1)).forEach(any());
        verify(this.readStateLock, times(2)).lock();
        verify(this.readStateLock, times(2)).unlock();

        verify(this.writeStateLock, times(0)).lock();
        verify(this.writeStateLock, times(0)).unlock();
    }

}
