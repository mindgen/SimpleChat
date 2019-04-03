package ru.sj.network.chat.server;

import ru.sj.network.chat.server.tcp.ServerInstance;

/**
 * Created by Eugene Sinitsyn
 */

public class ServerFactory {
    public static IServer getServer(ServerType type) {
        switch (type) {
            case BinaryTcp: return new ServerInstance();
            default: return null;
        }
    }
}
