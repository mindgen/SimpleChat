package ru.sj.network.chat.server.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sj.network.chat.api.model.request.ChangeNameRequest;
import ru.sj.network.chat.api.model.response.ChangeNameResponse;
import ru.sj.network.chat.server.IHandler;
import ru.sj.network.chat.server.storage.ChatRoom;
import ru.sj.network.chat.server.storage.CookieStorage;
import ru.sj.network.chat.server.storage.User;
import ru.sj.network.chat.server.storage.UserExistException;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

@Component
public class ChangeNameHandler implements IHandler {
    @Autowired
    ChatRoom chat;

    @Autowired
    CookieStorage cookies;

    @Override
    public void doRequest(Request request, Response response) {
        ChangeNameRequest req = (ChangeNameRequest)request.getData();

        User curUser = cookies.getUserSession(req.getCookie());
        if (null == curUser) {
            response.setData(ChangeNameResponse.createUnauthorized());
            return;
        }

        try {
            chat.getUsers().changeUserName(curUser, req.getName());
        }
        catch (UserExistException E)
        {
            response.setData(ChangeNameResponse.createFail());
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
