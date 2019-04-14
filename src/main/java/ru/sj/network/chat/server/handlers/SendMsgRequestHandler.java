package ru.sj.network.chat.server.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sj.network.chat.api.model.request.SendMsgRequest;
import ru.sj.network.chat.api.model.response.SendMsgResponse;
import ru.sj.network.chat.server.ChatManager;
import ru.sj.network.chat.server.IHandler;
import ru.sj.network.chat.server.UnauthorizedAccess;
import ru.sj.network.chat.server.storage.ChatRoom;
import ru.sj.network.chat.server.storage.User;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

@Component
public class SendMsgRequestHandler implements IHandler {

    @Autowired
    ChatManager manager;

    @Override
    public void doRequest(Request request, Response response) {
        SendMsgRequest msgReq = (SendMsgRequest)request.getData();

        try {
            manager.sendMessage(request.getSession(), msgReq.getMessageText());
        }
        catch (UnauthorizedAccess E) {
            response.setData(SendMsgResponse.createUnauthorized());
            return;
        }

        response.setData(SendMsgResponse.createOK());
    }

    @Override
    public Class<?> getRequestModelClass() {
        return SendMsgRequest.class;
    }
}
