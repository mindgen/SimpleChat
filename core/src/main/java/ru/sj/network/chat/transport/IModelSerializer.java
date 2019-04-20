package ru.sj.network.chat.transport;

import java.io.*;

/**
 * Created by Eugene Sinitsyn
 */

public interface IModelSerializer {
    void serialize(Object object, OutputStream stream) throws IOException;
    Object deserialize(InputStream stream);
}
