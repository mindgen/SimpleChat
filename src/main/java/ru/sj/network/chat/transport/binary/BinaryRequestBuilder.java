package ru.sj.network.chat.transport.binary;

import ru.sj.network.chat.transport.IRequestBuilder;
import ru.sj.network.chat.transport.Request;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

/**
 * Created by Eugene Sinitsyn
 */

public class BinaryRequestBuilder implements IRequestBuilder {
    public Request buildRequest(byte[] payload) {

        ByteArrayInputStream stream = new ByteArrayInputStream(payload);
        DataInputStream reader = new DataInputStream(stream);
        try {
            byte cmdCode = reader.readByte();

        }
        catch (Exception e)
        {
            return new Request(null);
        }

        return new Request(payload);
    }

    //public void register
}
