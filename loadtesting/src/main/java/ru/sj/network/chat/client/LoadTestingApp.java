package ru.sj.network.chat.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import ru.sj.network.chat.transport.MessageBuffer;
import ru.sj.network.chat.transport.ObjectModelSerializer;
import ru.sj.network.chat.transport.binary.BinaryTransport;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by Eugene Sinitsyn
 */

@ComponentScan(basePackages = "ru.sj.network.chat.client")
@Service
public final class LoadTestingApp {

    private final Logger logger = LoggerFactory.getLogger(LoadTestingApp.class);

    @Autowired
    private LoadTestingConfiguration configuration;

    public void doTest() {
        logger.info("Do test with {} clients", configuration.getClients());
        Thread[] clients = new Thread[configuration.getClients()];

        for (int i = 0; i < clients.length; ++i) {
            ClientWorker wrk = new ClientWorker(new InetSocketAddress(configuration.getHost(), configuration.getPort()));

            IChatClient client = new ChatClient(
                    new BinaryTransport(new ObjectModelSerializer()),
                    wrk.getEventsHandler(),
                    ByteBuffer.allocate(configuration.getClientBuffer()),
                    new MessageBuffer());

            wrk.setClient(client);

            clients[i] = new Thread(wrk, "ClientWorker_" + String.valueOf(i));
         }

        for (Thread thread : clients) {
            thread.start();
        }
    }

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(LoadTestingApp.class);

        LoadTestingApp app = context.getBean(LoadTestingApp.class);
        app.doTest();
    }

}
