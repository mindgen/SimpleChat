package ru.sj.network.chat.transport;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * Created by Eugene Sinitsyn
 */

public interface INetworkTransport {
    Collection<Request> decodeRequest(ByteBuffer buffer, IRequestBuffer msgBuffer);
    OutputStream encodeRequest(Request req);

    Response createEmptyResponse();
}
