package ru.sj.network.chat.server.handlers;

import ru.sj.network.chat.api.model.request.RegistrationRequest;
import ru.sj.network.chat.server.IHandler;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

public class RegistrationRequestHandler implements IHandler {
    @Override
    public void doRequest(Request request, Response response) {

    }

    @Override
    public Class<?> getRequestModelClass() {
        return RegistrationRequest.class;
    }
}
