package ru.sj.network.chat.server.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sj.network.chat.api.model.request.GetMessagesRequest;
import ru.sj.network.chat.server.IHandler;
import ru.sj.network.chat.server.storage.ChatRoom;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;


@Component
public class GetMessageRequestHandler implements IHandler {

    @Autowired
    ChatRoom chat;

    @Override
    public void doRequest(Request request, Response response) {

    }

    @Override
    public Class<?> getRequestModelClass() {
        return GetMessagesRequest.class;
    }
}
