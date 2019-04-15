package ru.sj.network.chat.server.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sj.network.chat.api.model.request.SendMsgRequest;
import ru.sj.network.chat.api.model.response.SendMsgResponse;
import ru.sj.network.chat.server.ChatManager;
import ru.sj.network.chat.server.IHandler;
import ru.sj.network.chat.server.UnauthorizedAccess;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

@Component
public class SendMsgRequestHandler implements IHandler {

    @Autowired
    ChatManager manager;

    private final Logger logger = LoggerFactory.getLogger(SendMsgRequestHandler.class);

    @Override
    public void doRequest(Request request, Response response) {
        SendMsgRequest msgReq = (SendMsgRequest)request.getData();

        logger.info("SendMessage request, session id - '{}'", request.getSession().getId().toString());

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
