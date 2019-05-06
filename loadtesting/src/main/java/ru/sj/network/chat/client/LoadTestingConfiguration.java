package ru.sj.network.chat.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by Eugene Sinitsyn
 */

@PropertySource(value = "classpath:/loadtesting.properties")
@Configuration
public class LoadTestingConfiguration {
    public LoadTestingConfiguration(@Value("${server.host}") String host,
                                    @Value("${server.port}") int port,
                                    @Value("${test.clients}") int clients,
                                    @Value("${client.buffer}") int clientBuffer) {
        this.host = host;
        this.port = port;
        this.clients = clients;
        this.clientBuffer = clientBuffer;
    }

    private String host;
    public String getHost() { return this.host; }

    private int port;
    public int getPort() { return this.port; }

    private int clients;
    public int getClients() { return this.clients; }

    private int clientBuffer;
    public int getClientBuffer() { return clientBuffer; }
}
