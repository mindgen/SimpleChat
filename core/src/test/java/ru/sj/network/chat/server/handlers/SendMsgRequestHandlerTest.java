package ru.sj.network.chat.server.handlers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.sj.network.chat.api.model.request.SendMsgRequest;
import ru.sj.network.chat.api.model.response.GetUsersCountResponse;
import ru.sj.network.chat.api.model.response.SendMsgResponse;
import ru.sj.network.chat.api.model.response.StatusCode;
import ru.sj.network.chat.server.ChatManager;
import ru.sj.network.chat.server.ISession;
import ru.sj.network.chat.server.ISessionId;
import ru.sj.network.chat.server.UnauthorizedAccess;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Eugene Sinitsyn
 */

@RunWith(MockitoJUnitRunner.class)
public class SendMsgRequestHandlerTest {
    SendMsgRequestHandler handler;

    @Mock
    ru.sj.network.chat.transport.Response response;

    @Mock
    ChatManager manager;

    @Mock
    ru.sj.network.chat.transport.Request request;

    @Mock
    SendMsgRequest sendMsgRequest;

    @Mock
    ISession session;

    @Mock
    ISessionId sessionId;

    @Before
    public void setup() {
        this.handler = new SendMsgRequestHandler(this.manager);

        when(this.request.getData()).thenReturn(this.sendMsgRequest);
        when(this.request.getSession()).thenReturn(this.session);
        when(this.session.getId()).thenReturn(this.sessionId);
        when(this.sessionId.toString()).thenReturn("Test Session SendMessage");
        when(this.sendMsgRequest.getMessageText()).thenReturn("Test Message");
    }

    @Test
    public void SendMessageOK() throws UnauthorizedAccess {
        doNothing().when(this.manager).sendMessage(any(), anyString());

        this.handler.doRequest(this.request, this.response);

        ArgumentCaptor<SendMsgResponse> responseArgument = ArgumentCaptor.forClass(SendMsgResponse.class);
        verify(this.response).setData(responseArgument.capture());
        assertEquals(StatusCode.OK, responseArgument.getValue().getCode());
    }

    @Test
    public void SendMessageNoneAuthorized() throws UnauthorizedAccess {
        doThrow(UnauthorizedAccess.class).when(this.manager).sendMessage(any(), anyString());

        this.handler.doRequest(this.request, this.response);

        ArgumentCaptor<SendMsgResponse> responseArgument = ArgumentCaptor.forClass(SendMsgResponse.class);
        verify(this.response).setData(responseArgument.capture());
        assertEquals(StatusCode.Unauthorized, responseArgument.getValue().getCode());
    }
}
