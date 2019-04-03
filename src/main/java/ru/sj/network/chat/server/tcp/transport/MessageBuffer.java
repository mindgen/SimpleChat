package ru.sj.network.chat.server.tcp.transport;

import ru.sj.network.chat.server.IMessageBuffer;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * Created by Eugene Sinitsyn
 */

public class MessageBuffer implements IMessageBuffer {
    private ByteBuffer mBuffer;

    public void writeToBuffer(ByteBuffer buffer) {
        if (null == mBuffer)
            mBuffer = ByteBuffer.allocate(buffer.limit());
        else
        {
            ByteBuffer newBuffer = ByteBuffer.allocate(mBuffer.capacity() + buffer.limit());
            mBuffer.flip();
            newBuffer.put(mBuffer);
            mBuffer = newBuffer;
        }
        mBuffer.put(buffer);
    }

    public int remaining() {
        return mBuffer.remaining();
    }

    public void mark() {
        mBuffer.mark();
    }

    public void reset() {
        mBuffer.reset();
    }

    public byte getByte() throws BufferUnderflowException {
        return mBuffer.get();
    }

    public int getInt() throws BufferUnderflowException {
        return mBuffer.getInt();
    }

    public short getShort() throws BufferUnderflowException {
        return mBuffer.getShort();
    }

    public void array(byte[] dst) throws BufferUnderflowException {
        mBuffer.get(dst);
    }

    public void flip() {
        mBuffer.flip();
    }

    public void compact() {
        mBuffer.compact();
    }
}
