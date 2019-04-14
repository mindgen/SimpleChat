package ru.sj.network.chat.server.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sj.network.chat.api.model.request.GetUsersCountRequest;
import ru.sj.network.chat.api.model.response.GetUsersCountResponse;
import ru.sj.network.chat.server.ChatManager;
import ru.sj.network.chat.server.IHandler;
import ru.sj.network.chat.server.UnauthorizedAccess;
import ru.sj.network.chat.server.storage.ChatRoom;
import ru.sj.network.chat.server.storage.User;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

@Component
public class GetUsersCountRequestHandler implements IHandler {

    @Autowired
    ChatManager manager;

    @Override
    public void doRequest(Request request, Response response) {
        GetUsersCountRequest UsersReq = (GetUsersCountRequest)request.getData();

        int usersCount = 0;
        try {
            usersCount = manager.getUsersCount(request.getSession());
        }
        catch (UnauthorizedAccess E) {
            response.setData(GetUsersCountResponse.createUnauthorized());
            return;
        }

        response.setData(GetUsersCountResponse.createOK(usersCount));
    }

    @Override
    public Class<?> getRequestModelClass() {
        return GetUsersCountRequest.class;
    }
}
