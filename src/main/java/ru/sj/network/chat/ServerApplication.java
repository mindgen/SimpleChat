package ru.sj.network.chat;

import ru.sj.network.chat.server.*;
import ru.sj.network.chat.server.storage.ChatRoom;
import ru.sj.network.chat.server.storage.Messages;
import ru.sj.network.chat.server.storage.Users;
import ru.sj.network.chat.server.tcp.ServerInstance;

/**
 * Created by Eugene Sinitsyn
 */

public final class ServerApplication {
    public static void main(String[] args) {
        try
        {
            initContext();

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

    private static void initContext() {
        ExecutionContext ctx = ExecutionContext.getInstance();
        ctx.setExecutor(new RequestExecutor());
        ctx.setMainRoom(ChatRoom.createDefault());
        ctx.setRequestController(new RequestController());
    }
}
