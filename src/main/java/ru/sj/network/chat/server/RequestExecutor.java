package ru.sj.network.chat.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sj.network.chat.transport.INetworkTransport;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

/**
 * Created by Eugene Sinitsyn
 */

@Component
public class RequestExecutor {

    @Autowired
    RequestController controller;

    public Response executeCmds(Request request,
                                     INetworkTransport transport) {
        Response response = transport.createEmptyResponse();
        controller.doRequest(request, response);

        return response;
    }
}
