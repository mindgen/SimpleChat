package ru.sj.network.chat.server;

import ru.sj.network.chat.transport.INetworkTransport;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

/**
 * Created by Eugene Sinitsyn
 */

public class RequestExecutor {
    public Response executeCmds(Request request,
                                     INetworkTransport transport,
                                     RequestController controller) {
        Response response = transport.createEmptyResponse();
        controller.doRequest(request, response);

        return response;
    }
}
