package ru.sj.network.chat;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import ru.sj.network.chat.server.*;
import ru.sj.network.chat.server.storage.ChatRoom;
import ru.sj.network.chat.server.storage.Messages;
import ru.sj.network.chat.server.storage.Users;
import ru.sj.network.chat.server.tcp.ServerInstance;

import java.net.InetSocketAddress;

/**
 * Created by Eugene Sinitsyn
 */

@ComponentScan(basePackages = "ru.sj.network.chat.server")
public final class ServerApplication {
    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(ServerApplication.class);
        IServer srv = (IServer)context.getBean("server");
        try {
            srv.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}
