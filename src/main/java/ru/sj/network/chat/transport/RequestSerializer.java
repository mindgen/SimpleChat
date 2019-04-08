package ru.sj.network.chat.transport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Eugene Sinitsyn
 */

public class RequestSerializer {
    static public Request deserialize(byte[] payload, IModelSerializer serializer) {
        Object objectModel = null;
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(payload);
            objectModel = serializer.deserialize(stream);
        }
        catch (Exception e)
        {
        }

        return new Request(objectModel);
    }

    static public void serialize(Request request, OutputStream stream, IModelSerializer serializer) throws IOException
    {
        serializer.serialize(request, stream);
    }
}
