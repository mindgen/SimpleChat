package ru.sj.network.chat.transport;

import java.nio.ByteBuffer;

/**
 * Created by Eugene Sinitsyn
 */

public interface IRequestBuffer {
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
