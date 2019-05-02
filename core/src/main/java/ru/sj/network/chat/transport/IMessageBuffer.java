package ru.sj.network.chat.transport;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * Created by Eugene Sinitsyn
 */

public interface IMessageBuffer {
    void writeToBuffer(ByteBuffer buffer);

    int remaining();

    void mark();

    void reset();

    byte getByte() throws BufferUnderflowException;

    int getInt() throws BufferUnderflowException;

    short getShort() throws BufferUnderflowException;

    void array(byte[] dst) throws BufferUnderflowException;

    void flip();

    void compact();

    void clear();
}
