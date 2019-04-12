package ru.sj.network.chat.server.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.sj.network.chat.api.model.request.RegistrationRequest;
import ru.sj.network.chat.api.model.response.RegistrationResponse;
import ru.sj.network.chat.server.IHandler;
import ru.sj.network.chat.server.storage.ChatRoom;
import ru.sj.network.chat.server.storage.CookieStorage;
import ru.sj.network.chat.server.storage.User;
import ru.sj.network.chat.server.storage.UserExistException;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

@Component
public class RegistrationRequestHandler implements IHandler {

    @Autowired
    ChatRoom chat;

    @Autowired
    CookieStorage cookies;

    @Override
    public void doRequest(Request request, Response response) {
        RegistrationRequest regModel = (RegistrationRequest)request.getData();

        User newUser;
        try {
            newUser = chat.getUsers().addUser(regModel.getValue());
        }
        catch (UserExistException E) {
            response.setData(RegistrationResponse.createFail());
            return;
        }

        String cookie = cookies.addUserSession(newUser);
        response.setData(RegistrationResponse.createOK(chat.getMessages().getLast(100), cookie));
    }

    @Override
    public Class<?> getRequestModelClass() {
        return RegistrationRequest.class;
    }
}
