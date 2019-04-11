package ru.sj.network.chat.server;

import ru.sj.network.chat.transport.INetworkTransport;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

import java.util.Collection;

/**
 * Created by Eugene Sinitsyn
 */

public class RequestExecutor {
    public Response executeCmds(Request request,
                                     INetworkTransport transport,
                                     RequestController controller) {
        return null;
    }
}
