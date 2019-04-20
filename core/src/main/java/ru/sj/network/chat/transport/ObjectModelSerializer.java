package ru.sj.network.chat.transport;

import java.io.*;

/**
 * Created by Eugene Sinitsyn
 */

public class ObjectModelSerializer implements IModelSerializer {
    @Override
    public void serialize(Object object, OutputStream stream) throws IOException {
        ObjectOutputStream objectStream = new ObjectOutputStream(stream);
        objectStream.writeObject(object);
        objectStream.flush();
        objectStream.close();
    }

    @Override
    public Object deserialize(InputStream stream) {
        Object objectModel = null;
        try {
            ObjectInputStream objectStream = new ObjectInputStream(stream);
            objectModel = objectStream.readObject();
            objectStream.close();
        }
        catch (Exception ex) { ex.printStackTrace(); }

        return objectModel;
    }
}
