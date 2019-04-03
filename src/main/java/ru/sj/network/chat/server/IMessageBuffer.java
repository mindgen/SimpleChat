package ru.sj.network.chat.server;

import java.nio.ByteBuffer;

/**
 * Created by Eugene Sinitsyn
 */

public interface IMessageBuffer {
    void writeToBuffer(ByteBuffer buffer);

    int remaining();

    void mark();

    void reset();

    byte getByte();

    int getInt();

    short getShort();

    void array(byte[] dst);

    void flip();

    void compact();
}
