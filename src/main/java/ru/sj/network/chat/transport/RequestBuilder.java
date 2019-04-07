package ru.sj.network.chat.transport;

import java.io.ByteArrayInputStream;

/**
 * Created by Eugene Sinitsyn
 */

public class RequestBuilder {
    static public Request build(byte[] payload, IModelSerializer serializer) {
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
}
