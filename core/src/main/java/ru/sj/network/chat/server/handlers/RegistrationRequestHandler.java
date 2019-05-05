package ru.sj.network.chat.server.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sj.network.chat.api.model.request.RegistrationRequest;
import ru.sj.network.chat.api.model.response.RegistrationResponse;
import ru.sj.network.chat.server.AlreadyRegisteredException;
import ru.sj.network.chat.server.ChatManager;
import ru.sj.network.chat.server.IHandler;
import ru.sj.network.chat.server.storage.Message;
import ru.sj.network.chat.server.storage.UserExistException;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

import java.util.List;

@Component
public class RegistrationRequestHandler implements IHandler {

    @Autowired
    ChatManager manager;

    RegistrationRequestHandler(ChatManager manager) {
        this.manager = manager;
    }

    private final Logger logger = LoggerFactory.getLogger(RegistrationRequestHandler.class);

    @Override
    public void doRequest(Request request, Response response) {
        RegistrationRequest regModel = (RegistrationRequest)request.getData();

        logger.info("Registration request with User '{}', session id - '{}'",
                regModel.getValue(),
                request.getSession().getId().toString());

        List<Message> lastMsgs;
        try {
            lastMsgs = manager.registerUser(request.getSession(), regModel.getValue());
        }
        catch (UserExistException e) {
            response.setData(RegistrationResponse.createFail());
            return;
        }
        catch (AlreadyRegisteredException e) {
            response.setData(RegistrationResponse.createFail());
            return;
        }

        response.setData(RegistrationResponse.createOK(lastMsgs));
    }

    @Override
    public Class<?> getRequestModelClass() {
        return RegistrationRequest.class;
    }
}
