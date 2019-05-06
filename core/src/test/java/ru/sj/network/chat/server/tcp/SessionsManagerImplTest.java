package ru.sj.network.chat.server.tcp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.sj.network.chat.server.ISession;
import ru.sj.network.chat.server.ISessionId;
import ru.sj.network.chat.server.ISessionsManagerEvents;
import ru.sj.network.chat.transport.IMessageBuffer;
import ru.sj.network.chat.transport.INetworkTransport;

import java.nio.channels.SelectionKey;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

/**
 * Created by Eugene Sinitsyn
 */

@RunWith(MockitoJUnitRunner.class)
public class SessionsManagerImplTest {

    SessionsManagerImpl manager;

    @Mock
    INetworkTransport transport;

    @Mock
    ISessionBufferFactory bufferFactory;

    @Mock
    HashMap<ISessionId, ISession> container;

    @Mock
    ISessionId sessionId;

    @Mock
    ISession session;

    @Mock
    SelectionKey selKey;

    @Mock
    ISessionsManagerEvents events;

    @Mock
    IMessageBuffer msgBuffer;


    @Before
    public void setup() {
        this.manager = new SessionsManagerImpl(this.transport, this.bufferFactory, this.container);
        this.manager.setEventsHandler(events);

        when(this.container.get(sessionId)).thenReturn(session);
        when(this.bufferFactory.createRequestBuffer()).thenReturn(msgBuffer);

        when(this.session.getManager()).thenReturn(this.manager);
        when(this.session.getId()).thenReturn(this.sessionId);
    }

    @Test
    public void findSession() {
        ISession session = this.manager.findById(sessionId);

        assertEquals(this.session, session);
        verify(this.container, times(1)).get(sessionId);
    }

    @Test
    public void openNewSession() {

        ISession newSession = this.manager.openSession(this.selKey);

        verify(this.bufferFactory, times(1)).createRequestBuffer();
        verify(this.events, times(1)).onOpenSession(newSession);
        verify(this.container, times(1)).put(any(), any());
    }

    @Test
    public void closeSession() {
        this.manager.closeSession(this.session);

        verify(this.session, times(1)).getManager();
        verify(this.events, times(1)).onCloseSession(any());
        verify(this.container, times(1)).remove(this.session.getId());
        verify(this.session, times(1)).freeResources();
    }
}
