package ru.sj.network.chat.server.handlers;

import ru.sj.network.chat.api.model.request.SendMsgRequest;
import ru.sj.network.chat.server.IHandler;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

public class SendMsgRequestHandler implements IHandler {
    @Override
    public void doRequest(Request request, Response response) {

    }

    @Override
    public Class<?> getRequestModelClass() {
        return SendMsgRequest.class;
    }
}
