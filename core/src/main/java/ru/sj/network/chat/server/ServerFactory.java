package ru.sj.network.chat.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.sj.network.chat.server.tcp.ServerInstance;

/**
 * Created by Eugene Sinitsyn
 */

@Component
public class ServerFactory {

    @Autowired
    RequestExecutor executor;

    @Bean(name="server")
    public IServer getServer(ServerConfiguration configuration) throws AlreadyStartedException {
        switch (configuration.getServerType()) {
            case BinaryTcp: {
                ServerInstance srv = new ServerInstance(this.executor);
                srv.setBufferCapacity(configuration.getBufferSize());
                srv.setWorkersCount(configuration.getWorkersCount());
                srv.setAddress(configuration.getAddress());

                return srv;
            }
            default: return null;
        }
    }
}
