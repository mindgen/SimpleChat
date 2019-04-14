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

    Map<ISession, User> mapping = new HashMap<>();
    ReadWriteLock stateLock = new ReentrantReadWriteLock();

    public List<Message> registerUser(ISession session, String name) throws UserExistException {
        User newUser = chat.getUsers().addUser(name);

        chat.getMessages().getLocker().readLock().lock();
        _store(session, newUser);

        List<Message> lastMessages = chat.getMessages().getLast(100);
        chat.getMessages().getLocker().readLock().unlock();

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

        chat.getMessages().getLocker().writeLock().lock();
        TextMessage chatMessage = chat.getMessages().addTextMessage(curUser.getName(), message);
        chat.getMessages().getLocker().writeLock().unlock();

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
        mapping.forEach((session, user) -> {
            if (msg.getUser().compareTo(user.getName()) != 0) {
                session.storeRealTimeResponse(response);
            }
        });
        stateLock.readLock().unlock();
    }

    // Internal functions
    private void _store(ISession session, User user) {
        stateLock.writeLock().lock();
        mapping.put(session, user);
        stateLock.writeLock().unlock();
    }

    private void _extract(ISession session) {
        stateLock.writeLock().lock();
        mapping.remove(session);
        stateLock.writeLock().unlock();
    }

    private User _find(ISession session) {
        stateLock.readLock().lock();
        User user = mapping.get(session);
        stateLock.readLock().unlock();

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
        removeSession(session);
    }
}
