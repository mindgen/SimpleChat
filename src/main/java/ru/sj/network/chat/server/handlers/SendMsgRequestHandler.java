package ru.sj.network.chat.server.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.sj.network.chat.api.model.request.SendMsgRequest;
import ru.sj.network.chat.api.model.response.GetUsersCountResponse;
import ru.sj.network.chat.api.model.response.SendMsgResponse;
import ru.sj.network.chat.server.IHandler;
import ru.sj.network.chat.server.storage.ChatRoom;
import ru.sj.network.chat.server.storage.CookieStorage;
import ru.sj.network.chat.server.storage.User;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

@Component
public class SendMsgRequestHandler implements IHandler {

    @Autowired
    ChatRoom chat;

    @Autowired
    CookieStorage cookies;

    @Override
    public void doRequest(Request request, Response response) {
        SendMsgRequest msgReq = (SendMsgRequest)request.getData();

        User curUser = cookies.getUserSession(msgReq.getCookie());
        if (null == curUser) {
            response.setData(SendMsgResponse.createUnauthorized());
            return;
        }

        chat.getMessages().addTextMessage(curUser.getName(), msgReq.getMessageText());
        response.setData(SendMsgResponse.createOK());
    }

    @Override
    public Class<?> getRequestModelClass() {
        return SendMsgRequest.class;
    }
}
