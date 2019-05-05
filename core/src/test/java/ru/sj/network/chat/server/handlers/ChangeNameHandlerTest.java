package ru.sj.network.chat.server.handlers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.sj.network.chat.api.model.request.ChangeNameRequest;
import ru.sj.network.chat.api.model.response.ChangeNameResponse;
import ru.sj.network.chat.api.model.response.StatusCode;
import ru.sj.network.chat.server.ChatManager;
import ru.sj.network.chat.server.ISession;
import ru.sj.network.chat.server.ISessionId;
import ru.sj.network.chat.server.UnauthorizedAccess;
import ru.sj.network.chat.server.storage.UserExistException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Eugene Sinitsyn
 */
@RunWith(MockitoJUnitRunner.class)
public class ChangeNameHandlerTest {
    ChangeNameHandler handler;

    @Mock
    ru.sj.network.chat.transport.Response response;

    @Mock
    ChatManager manager;

    @Mock
    ru.sj.network.chat.transport.Request request;

    @Mock
    ChangeNameRequest changeNameRequest;

    @Mock
    ISession session;

    @Mock
    ISessionId sessionId;

    @Before
    public void setup() {
        this.handler = new ChangeNameHandler(this.manager);

        when(this.request.getData()).thenReturn(this.changeNameRequest);
        when(this.request.getSession()).thenReturn(this.session);
        when(this.session.getId()).thenReturn(this.sessionId);
        when(this.sessionId.toString()).thenReturn("Test Session ChangeName Request");
        when(this.changeNameRequest.getName()).thenReturn("NewName");

    }

    @Test
    public void ChangeNameRequestForFreeUserName() throws UserExistException, UnauthorizedAccess {
        doNothing().when(this.manager).changeUserName(any(), anyString());

        this.handler.doRequest(this.request, this.response);

        ArgumentCaptor<ChangeNameResponse> responseArgument = ArgumentCaptor.forClass(ChangeNameResponse.class);
        verify(this.response).setData(responseArgument.capture());
        assertEquals(StatusCode.OK, responseArgument.getValue().getCode());
    }

    @Test
    public void ChangeNameRequestForExistUserName() throws UserExistException, UnauthorizedAccess {
        doThrow(UserExistException.class).when(this.manager).changeUserName(any(), anyString());

        this.handler.doRequest(this.request, this.response);

        ArgumentCaptor<ChangeNameResponse> responseArgument = ArgumentCaptor.forClass(ChangeNameResponse.class);
        verify(this.response).setData(responseArgument.capture());
        assertEquals(StatusCode.Error, responseArgument.getValue().getCode());
    }

    @Test
    public void ChangeNameRequestNoneAuthorized() throws UserExistException, UnauthorizedAccess {
        doThrow(UnauthorizedAccess.class).when(this.manager).changeUserName(any(), anyString());

        this.handler.doRequest(this.request, this.response);

        ArgumentCaptor<ChangeNameResponse> responseArgument = ArgumentCaptor.forClass(ChangeNameResponse.class);
        verify(this.response).setData(responseArgument.capture());
        assertEquals(StatusCode.Unauthorized, responseArgument.getValue().getCode());
    }
}
