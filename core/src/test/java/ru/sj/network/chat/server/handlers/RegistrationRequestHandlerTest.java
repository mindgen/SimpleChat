package ru.sj.network.chat.server.handlers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.sj.network.chat.api.model.request.RegistrationRequest;
import ru.sj.network.chat.api.model.response.RegistrationResponse;
import ru.sj.network.chat.api.model.response.StatusCode;
import ru.sj.network.chat.server.*;
import ru.sj.network.chat.server.storage.Message;
import ru.sj.network.chat.server.storage.TextMessage;
import ru.sj.network.chat.server.storage.UserExistException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Eugene Sinitsyn
 */

@RunWith(MockitoJUnitRunner.class)
public class RegistrationRequestHandlerTest {
    RegistrationRequestHandler handler;

    @Mock
    ru.sj.network.chat.transport.Response response;

    @Mock
    ChatManager manager;

    @Mock
    ru.sj.network.chat.transport.Request request;

    @Mock
    RegistrationRequest registrationRequest;

    @Mock
    ISession session;

    @Mock
    ISessionId sessionId;

    List<Message> resultList;

    @Mock
    TextMessage lastMessage;

    @Before
    public void setup() {
        when(this.request.getData()).thenReturn(this.registrationRequest);
        when(this.request.getSession()).thenReturn(this.session);
        when(this.session.getId()).thenReturn(this.sessionId);
        when(this.registrationRequest.getValue()).thenReturn("UserName");
        when(this.sessionId.toString()).thenReturn("Test Session RegistrationRequest");


        this.handler = new RegistrationRequestHandler(this.manager);
        this.resultList = new ArrayList<>();
        this.resultList.add(lastMessage);
    }

    @Test
    public void RegistrationRequestForValidParams() throws UserExistException, AlreadyRegisteredException {
        when(this.manager.registerUser(any(), anyString())).thenReturn(this.resultList);

        this.handler.doRequest(this.request, this.response);

        ArgumentCaptor<RegistrationResponse> responseArgument = ArgumentCaptor.forClass(RegistrationResponse.class);
        verify(this.response).setData(responseArgument.capture());
        assertEquals(StatusCode.OK, responseArgument.getValue().getCode());
        assertEquals(1, responseArgument.getValue().getMessages().size());
    }

    @Test
    public void RegistrationRequestForAlreadyRegistered() throws UserExistException, AlreadyRegisteredException {
        doThrow(AlreadyRegisteredException.class).when(this.manager).registerUser(any(), anyString());

        this.handler.doRequest(this.request, this.response);

        ArgumentCaptor<RegistrationResponse> responseArgument = ArgumentCaptor.forClass(RegistrationResponse.class);
        verify(this.response).setData(responseArgument.capture());
        assertEquals(StatusCode.Error, responseArgument.getValue().getCode());
    }

    @Test
    public void RegistrationRequestForExistUser() throws UserExistException, AlreadyRegisteredException {
        doThrow(UserExistException.class).when(this.manager).registerUser(any(), anyString());

        this.handler.doRequest(this.request, this.response);

        ArgumentCaptor<RegistrationResponse> responseArgument = ArgumentCaptor.forClass(RegistrationResponse.class);
        verify(this.response).setData(responseArgument.capture());
        assertEquals(StatusCode.Error, responseArgument.getValue().getCode());
    }
}
