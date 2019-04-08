package ru.sj.network.chat.transport;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * Created by Eugene Sinitsyn
 */

public interface INetworkTransport {
    Collection<Request> decodeRequest(ByteBuffer buffer, IRequestBuffer msgBuffer);
    ByteBuffer encodeRequest(Collection<Request> messages);

    Response createEmptyResponse();
}
