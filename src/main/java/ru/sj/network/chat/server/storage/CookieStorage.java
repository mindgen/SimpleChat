package ru.sj.network.chat.server.storage;

import org.springframework.stereotype.Component;

import java.util.TreeMap;

@Component
public class CookieStorage {

    public synchronized String addUserSession(User user) {
        String cookie = String.valueOf(user.hashCode());
        map.put(cookie, user);

        return cookie;
    }

    public synchronized User getUserSession(String cookie) {
        return map.get(cookie);
    }

    private TreeMap<String, User> map = new TreeMap<>();
}
