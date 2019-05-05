package ru.sj.network.chat.server.storage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

/**
 * Created by Eugene Sinitsyn
 */

@RunWith(MockitoJUnitRunner.class)
public class UsersTest {
    private Users usersStorage;

    @Mock
    ReadWriteLock lock;

    @Mock
    Lock stubLockWrite;

    @Mock
    Lock stubLockRead;

    @Mock
    Map<String, User> container;

    @Mock
    User tstUser;

    @Mock
    User tstUser2;

    @Before
    public void setup() {
        when(this.lock.readLock()).thenReturn(this.stubLockRead);
        when(this.lock.writeLock()).thenReturn(this.stubLockWrite);

        doNothing().when(this.stubLockWrite).lock();
        doNothing().when(this.stubLockRead).unlock();

        this.usersStorage = new Users(this.container, this.lock);
    }

    @Test
    public void addNewUser() throws UserExistException {
        User newUser = this.usersStorage.addUser("NewUser");
        assertEquals("NewUser", newUser.getName());

        newUser = this.usersStorage.addUser("NewUser2");
        assertEquals("NewUser2", newUser.getName());

        verify(this.container, times(2)).put(anyString(), any());
        verify(this.stubLockWrite, times(2)).lock();
        verify(this.stubLockWrite, times(2)).unlock();

        verify(this.stubLockRead, times(0)).lock();
        verify(this.stubLockRead, times(0)).unlock();
    }

    @Test(expected = UserExistException.class)
    public void addExistingUserWithThrow() throws UserExistException {
        when(this.container.get("User")).thenReturn(tstUser);
        try {
            this.usersStorage.addUser("User");
        }
        finally {
            verify(this.stubLockWrite, times(1)).lock();
            verify(this.stubLockWrite, times(1)).unlock();

            verify(this.stubLockRead, times(0)).lock();
            verify(this.stubLockRead, times(0)).unlock();
        }
    }

    @Test
    public void findUserByName() {
        when(this.container.get("User")).thenReturn(tstUser);

        User resultUser = this.usersStorage.findByName("User");

        assertEquals(tstUser, resultUser);

        verify(this.stubLockRead, times(1)).lock();
        verify(this.stubLockRead, times(1)).unlock();

        verify(this.stubLockWrite, times(0)).lock();
        verify(this.stubLockWrite, times(0)).unlock();
    }

    @Test(expected = Exception.class)
    public void findUserByNameWithThrow() {
        when(this.container.get("User")).thenThrow(new Exception());

        try{
            this.usersStorage.findByName("User");
        }
        finally {
            verify(this.stubLockRead, times(1)).lock();
            verify(this.stubLockRead, times(1)).unlock();

            verify(this.stubLockWrite, times(0)).lock();
            verify(this.stubLockWrite, times(0)).unlock();
        }
    }

    @Test
    public void removeNotNullUser() {
        when(this.container.remove("User")).thenReturn(tstUser);
        when(this.container.remove("User2")).thenReturn(null);

        this.usersStorage.removeUser("User");
        verify(this.container, times(1)).remove("User");

        this.usersStorage.removeUser("User2");
        verify(this.container, times(1)).remove("User2");

        verify(this.stubLockWrite, times(2)).lock();
        verify(this.stubLockWrite, times(2)).unlock();

        verify(this.stubLockRead, times(0)).lock();
        verify(this.stubLockRead, times(0)).unlock();
    }

    @Test(expected = Exception.class)
    public void removeNullUser() {
        when(this.container.remove(null)).thenThrow(new Exception());

        try {
            this.usersStorage.removeUser(null);
        }
        finally {
            verify(this.stubLockWrite, times(1)).lock();
            verify(this.stubLockWrite, times(1)).unlock();

            verify(this.stubLockRead, times(0)).lock();
            verify(this.stubLockRead, times(0)).unlock();
        }
    }

    @Test
    public void getCount() {
        when(this.container.size()).thenReturn(0);

        this.usersStorage.count();

        verify(this.container, times(1)).size();
        verify(this.stubLockWrite, times(0)).lock();
        verify(this.stubLockWrite, times(0)).unlock();

        verify(this.stubLockRead, times(1)).lock();
        verify(this.stubLockRead, times(1)).unlock();
    }

    @Test
    public void changeNameForExistedUserAndFreeNewName() throws UserExistException {
        when(this.container.get("NewName")).thenReturn(null);
        when(this.tstUser.getName()).thenReturn("UserName");

        this.usersStorage.changeUserName(tstUser, "NewName");

        verify(this.container, times(1)).remove("UserName");
        verify(this.container, times(1)).put("NewName", tstUser);
        verify(this.tstUser, times(1)).setName("NewName");

        verify(this.stubLockWrite, times(1)).lock();
        verify(this.stubLockWrite, times(1)).unlock();

        verify(this.stubLockRead, times(0)).lock();
        verify(this.stubLockRead, times(0)).unlock();
    }

    @Test
    public void changeNameToSelfName() throws UserExistException {
        when(this.tstUser.getName()).thenReturn("UserName");

        this.usersStorage.changeUserName(tstUser, "UserName");

        verify(this.container, times(0)).remove(any());
        verify(this.container, times(0)).put(anyString(), any());
        verify(this.tstUser, times(0)).setName(anyString());

        verify(this.stubLockWrite, times(1)).lock();
        verify(this.stubLockWrite, times(1)).unlock();

        verify(this.stubLockRead, times(0)).lock();
        verify(this.stubLockRead, times(0)).unlock();
    }

    @Test(expected = UserExistException.class)
    public void changeNameToExist() throws UserExistException {
        when(this.container.get("UserName2")).thenReturn(tstUser2);
        when(this.tstUser.getName()).thenReturn("UserName");

        try {
            this.usersStorage.changeUserName(tstUser, "UserName2");
        }
        finally {
            verify(this.container, times(0)).remove(any());
            verify(this.container, times(0)).put(anyString(), any());
            verify(this.tstUser, times(0)).setName(anyString());

            verify(this.stubLockWrite, times(1)).lock();
            verify(this.stubLockWrite, times(1)).unlock();

            verify(this.stubLockRead, times(0)).lock();
            verify(this.stubLockRead, times(0)).unlock();
        }
    }
}
