package ru.sj.network.chat.server.tcp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.sj.network.chat.api.model.response.RealTimeResponse;
import ru.sj.network.chat.server.ISessionsManager;
import ru.sj.network.chat.transport.IMessageBuffer;
import ru.sj.network.chat.transport.INetworkTransport;
import ru.sj.network.chat.transport.Response;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

/**
 * Created by Eugene Sinitsyn
 */

@RunWith(MockitoJUnitRunner.class)
public class SessionImplTest {
    SessionImpl session;

    @Mock
    ISessionsManager manager;

    @Mock
    IMessageBuffer buffer;

    @Mock
    Queue<Response> responseStorage;

    @Mock
    Queue<Response> realTimeResponseStorage;

    @Mock
    SelectionKey selKey;

    @Mock
    Selector selector;

    @Mock
    Response response;

    @Mock
    Response emptRresponse;

    @Mock
    SessionImpl.BufferedResponseWriter writer;


    @Mock
    Lock storageLock;

    @Mock
    INetworkTransport transport;

    @Before
    public void setup() {
        session = new SessionImpl(this.manager,
                                        this.buffer,
                                        this.responseStorage,
                                        this.realTimeResponseStorage,
                                        this.selKey,
                                        this.storageLock,
                                        this.writer);

        when(this.selKey.selector()).thenReturn(this.selector);
        when(this.manager.getTransport()).thenReturn(this.transport);
        when(this.transport.createEmptyResponse()).thenReturn(emptRresponse);
        when(this.selKey.interestOpsOr(anyInt())).thenReturn(SelectionKey.OP_READ);
        when(this.selector.wakeup()).thenReturn(selector);

        doNothing().when(this.emptRresponse).setData(any());
        doNothing().when(this.storageLock).lock();
        doNothing().when(this.storageLock).unlock();
    }

    @Test
    public void closeSession() {
        this.session.close();

        verify(this.manager, times(1)).closeSession(this.session);
    }

    @Test
    public void storeResponse() {
        this.session.storeResponse(response);

        verify(this.responseStorage, times(1)).add(this.response);
        verify(this.selKey, times(1)).interestOpsOr(SelectionKey.OP_WRITE);
        verify(this.selector, times(1)).wakeup();
    }

    @Test
    public void storeRealTimeResponse() {

        this.session.storeRealTimeResponse(Mockito.mock(RealTimeResponse.class));

        verify(this.storageLock, times(1)).lock();
        verify(this.storageLock, times(1)).unlock();
        verify(this.transport, times(1)).createEmptyResponse();
        verify(this.realTimeResponseStorage, times(1)).add(emptRresponse);
        verify(this.selKey, times(1)).interestOpsOr(SelectionKey.OP_WRITE);
        verify(this.selector, times(1)).wakeup();
    }

    @Test
    public void updateBufferFromRequestStorage() throws IOException {
        when(this.writer.writeResponse(this.response, this.transport)).thenReturn(true);
        when(this.responseStorage.peek()).thenReturn(this.response);

        this.session.updateWriteBuffer();

        verify(this.responseStorage, times(1)).peek();
        verify(this.responseStorage, times(1)).poll();
        verify(this.storageLock, times(0)).lock();
        verify(this.storageLock, times(0)).unlock();
    }

    @Test
    public void updateBufferFromRealtimeStorage() throws IOException {
        when(this.writer.writeResponse(this.response, this.transport)).thenReturn(false);
        when(this.responseStorage.peek()).thenReturn(this.response);
        when(this.writer.isEmpty()).thenReturn(true);

        when(this.realTimeResponseStorage.peek()).thenReturn(this.response);

        this.session.updateWriteBuffer();

        verify(this.responseStorage, times(1)).peek();
        verify(this.responseStorage, times(0)).poll();

        verify(this.realTimeResponseStorage, times(1)).peek();
        verify(this.realTimeResponseStorage, times(0)).poll();

        verify(this.storageLock, times(1)).lock();
        verify(this.storageLock, times(1)).unlock();

        verify(this.selKey, times(1)).interestOpsAnd(~SelectionKey.OP_WRITE);
    }
}
