package ru.sj.network.chat.api.model.request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Eugene Sinitsyn
 */

public class GetUsersCountRequest extends AuthorizedRequest {
    private static final long serialVersionUID = 1L;


    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
    }


    @Override
    public RequestType getType() {
        return RequestType.GetUsersCount;
    }
}
