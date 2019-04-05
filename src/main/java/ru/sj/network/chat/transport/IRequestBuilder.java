package ru.sj.network.chat.transport;

/**
 * Created by Eugene Sinitsyn
 */

public interface IRequestBuilder {
    public Request buildRequest(byte[] payload);
}
