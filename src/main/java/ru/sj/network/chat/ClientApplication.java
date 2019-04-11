package ru.sj.network.chat;

import ru.sj.network.chat.client.ChatClient;
import ru.sj.network.chat.transport.ObjectModelSerializer;
import ru.sj.network.chat.transport.binary.BinaryTransport;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

public final class ClientApplication {

    public static void main(String[] args) {
        try {
            ChatClient client = new ChatClient(new BinaryTransport(new ObjectModelSerializer()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Server hostname");
            String hostname = reader.readLine();

            System.out.println("Server port");
            Integer port = Integer.parseInt(reader.readLine());
            client.connect(new InetSocketAddress(hostname, port));

            boolean result = client.registration("User1");

            if (!result) client.close();
        }
        catch (Exception e)
        {

        }
    }
}
