package ru.sj.network.chat.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.InetSocketAddress;

@PropertySource(value = "classpath:/server.properties")
@Configuration
public class ServerConfiguration {
    public ServerConfiguration(@Value("${server.type}") ServerType type,
                               @Value("${server.port}") int port,
                               @Value("${server.workers}") int workersCnt,
                               @Value("${server.buffer}") int bufferSize) {
        this.serverType = type;
        address = new InetSocketAddress(port);
        this.workersCount = workersCnt;
        this.bufferSize = bufferSize;
    }

    private ServerType serverType;
    public ServerType getServerType() { return serverType; }

    private InetSocketAddress address;
    public InetSocketAddress getAddress() { return address; }

    private int workersCount;
    public int getWorkersCount() { return workersCount; }

    private int bufferSize;
    public int getBufferSize() { return bufferSize; }
}
