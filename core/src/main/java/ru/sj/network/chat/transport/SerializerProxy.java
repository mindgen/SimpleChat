package ru.sj.network.chat.transport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Eugene Sinitsyn
 */

public class SerializerProxy {
    static public Object deserialize(byte[] payload, IModelSerializer serializer) {
        Object objectModel = null;
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(payload);
            objectModel = serializer.deserialize(stream);
        }
        catch (Exception e)
        {
        }

        return objectModel;
    }

    static public Object deserialize(InputStream stream, IModelSerializer serializer) {
        return serializer.deserialize(stream);
    }

    static public void serialize(Object requestData, OutputStream stream, IModelSerializer serializer) throws IOException
    {
        serializer.serialize(requestData, stream);
    }
}
