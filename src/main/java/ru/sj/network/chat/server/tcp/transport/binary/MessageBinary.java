package ru.sj.network.chat.server.tcp.transport.binary;

import ru.sj.network.chat.server.IMessage;


/**
 * Created by Eugene Sinitsyn
 */

public class MessageBinary implements IMessage {

    private byte[] mPayload;
    public MessageBinary(byte[] payload) {
        mPayload = payload;
    }
    @Override
    public byte[] getPayload() {
        return mPayload;
    }
}
