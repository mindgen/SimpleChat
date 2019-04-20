package ru.sj.network.chat.server.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sj.network.chat.api.model.request.ChangeNameRequest;
import ru.sj.network.chat.api.model.response.ChangeNameResponse;
import ru.sj.network.chat.server.ChatManager;
import ru.sj.network.chat.server.IHandler;
import ru.sj.network.chat.server.UnauthorizedAccess;
import ru.sj.network.chat.server.storage.UserExistException;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

@Component
public class ChangeNameHandler implements IHandler {
    @Autowired
    ChatManager manager;

    private final Logger logger = LoggerFactory.getLogger(ChangeNameHandler.class);

    @Override
    public void doRequest(Request request, Response response) {
        ChangeNameRequest req = (ChangeNameRequest)request.getData();

        logger.info("ChangeName request, session id - '{}'", request.getSession().getId().toString());
        try {
            manager.changeUserName(request.getSession(), req.getName());
        }
        catch (UserExistException E)
        {
            response.setData(ChangeNameResponse.createFail());
            return;
        }
        catch (UnauthorizedAccess E)
        {
            response.setData(ChangeNameResponse.createUnauthorized());
            return;
        }

        response.setData(ChangeNameResponse.createOK());
        return;
    }

    @Override
    public Class<?> getRequestModelClass() {
        return ChangeNameRequest.class;
    }
}
