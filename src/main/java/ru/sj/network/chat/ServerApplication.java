package ru.sj.network.chat;

import ru.sj.network.chat.server.*;
import ru.sj.network.chat.server.storage.ChatRoom;
import ru.sj.network.chat.server.storage.Messages;
import ru.sj.network.chat.server.storage.Users;
import ru.sj.network.chat.server.tcp.ServerInstance;

import java.net.InetSocketAddress;

/**
 * Created by Eugene Sinitsyn
 */

public final class ServerApplication {
    public static void main(String[] args) {
        try
        {
            initContext();

            ServerInstance srv = (ServerInstance)ServerFactory.getServer(ServerType.BinaryTcp);
            //srv.setWorkersCount(Runtime.getRuntime().availableProcessors());
            srv.setWorkersCount(1);
            srv.setAddress(new InetSocketAddress(1234));
            srv.setBufferCapacity(1024);
            srv.start();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void initContext() {
        ExecutionContext ctx = ExecutionContext.getInstance();
        ctx.setExecutor(new RequestExecutor());
        ctx.setMainRoom(ChatRoom.createDefault());
        ctx.setRequestController(new RequestController());
    }
}
