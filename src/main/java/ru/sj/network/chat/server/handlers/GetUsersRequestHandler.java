package ru.sj.network.chat.server.handlers;

import ru.sj.network.chat.api.model.request.GetUsersRequest;
import ru.sj.network.chat.server.IHandler;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

public class GetUsersRequestHandler implements IHandler {
    @Override
    public void doRequest(Request request, Response response) {

    }

    @Override
    public Class<?> getRequestModelClass() {
        return GetUsersRequest.class;
    }
}
