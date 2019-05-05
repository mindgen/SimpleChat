package ru.sj.network.chat.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sj.network.chat.api.model.response.NewMessageResponse;
import ru.sj.network.chat.server.storage.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Eugene Sinitsyn
 */

@Component
public class ChatManager implements ISessionsManagerEvents {

    @Autowired
    ChatRoom chat;

    Map<ISession, User> mapping;
    ReadWriteLock stateLock;

    public ChatManager() {
        mapping = new HashMap<>();
        stateLock = new ReentrantReadWriteLock();
    }

    ChatManager(ChatRoom chat, Map<ISession, User> mapping, ReadWriteLock lock) {
        this.chat = chat;
        this.mapping = mapping;
        this.stateLock = lock;
    }

    public List<Message> registerUser(ISession session, String name) throws UserExistException,
                                                                            AlreadyRegisteredException {
        if (null != _find(session)) throw new AlreadyRegisteredException();

        User newUser = chat.getUsers().addUser(name);

        List<Message> lastMessages = null;
        chat.getMessages().getLocker().readLock().lock();
        try {
            _store(session, newUser);
            lastMessages = chat.getMessages().getLast(100);
        }
        finally {
            chat.getMessages().getLocker().readLock().unlock();
        }

        return lastMessages;
    }

    public void changeUserName(ISession session, String newName) throws UserExistException, UnauthorizedAccess {
        User curUser = _checkAccess(session);

        chat.getUsers().changeUserName(curUser, newName);
    }

    public int getUsersCount(ISession session) throws UnauthorizedAccess {
        _checkAccess(session);

        return chat.getUsers().count();
    }

    public void sendMessage(ISession session, String message) throws UnauthorizedAccess {
        User curUser = _checkAccess(session);

        TextMessage chatMessage = chat.getMessages().addTextMessage(curUser.getName(), message);
        sendToAll(chatMessage);
    }

    private void removeSession(ISession session) {
        User user = _find(session);
        if (null == user) return;

        chat.getUsers().removeUser(user.getName());
        _extract(session);
    }

    private void sendToAll(TextMessage msg) {
        NewMessageResponse response = NewMessageResponse.createOK(msg.getUser(), msg.getMessage());

        stateLock.readLock().lock();
        try {
            mapping.forEach((session, user) -> {
                if (msg.getUser().compareTo(user.getName()) != 0) {
                    session.storeRealTimeResponse(response);
                }
            });
        }
        finally {
            stateLock.readLock().unlock();
        }
    }

    // Internal functions
    private void _store(ISession session, User user) {
        stateLock.writeLock().lock();
        try {
            mapping.put(session, user);
        }
        finally {
            stateLock.writeLock().unlock();
        }
    }

    private void _extract(ISession session) {
        stateLock.writeLock().lock();
        try {
            mapping.remove(session);
        }
        finally {
            stateLock.writeLock().unlock();
        }
    }

    private User _find(ISession session) {
        User user = null;
        stateLock.readLock().lock();
        try {
             user = mapping.get(session);
        }
        finally {
            stateLock.readLock().unlock();
        }

        return user;
    }

    private User _checkAccess(ISession session) throws UnauthorizedAccess {
        User curUser = _find(session);
        if (null == curUser) throw new UnauthorizedAccess();

        return curUser;
    }

    // ISessionsManagerEvents
    @Override
    public void onOpenSession(ISession session) {
    }

    @Override
    public void onCloseSession(ISession session) {
        try {
            removeSession(session);
        }
        catch (Exception E) {}
    }
}
