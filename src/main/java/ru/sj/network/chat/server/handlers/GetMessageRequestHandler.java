package ru.sj.network.chat.server.handlers;

import ru.sj.network.chat.api.model.request.GetMessagesRequest;
import ru.sj.network.chat.server.IHandler;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

public class GetMessageRequestHandler implements IHandler {
    @Override
    public void doRequest(Request request, Response response) {

    }

    @Override
    public Class<?> getRequestModelClass() {
        return GetMessagesRequest.class;
    }
}
