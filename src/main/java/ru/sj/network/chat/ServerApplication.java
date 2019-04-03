package ru.sj.network.chat;

import ru.sj.network.chat.server.ServerFactory;
import ru.sj.network.chat.server.ServerType;
import ru.sj.network.chat.server.tcp.ServerInstance;

/**
 * Created by Eugene Sinitsyn
 */

public class ServerApplication {
    public static void main(String[] args) {
        try
        {
            ServerInstance srv = (ServerInstance)ServerFactory.getServer(ServerType.BinaryTcp);
            srv.setWorkersCount(Runtime.getRuntime().availableProcessors());
            srv.setBufferCapacity(32); //get protocol message size * 8
            srv.start();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
