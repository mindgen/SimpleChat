package ru.sj.network.chat.server;

/**
 * Created by Eugene Sinitsyn
 */

public interface IMessageFactory {
    public IMessage createMessage(byte[] payload);
}
