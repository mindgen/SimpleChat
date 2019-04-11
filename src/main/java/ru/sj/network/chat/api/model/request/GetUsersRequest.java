package ru.sj.network.chat.api.model.request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Eugene Sinitsyn
 */

public class GetUsersRequest extends StateModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        oos.defaultWriteObject();
        oos.writeInt(this.getLastState());
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.setLastState(ois.readInt());
    }

    @Override
    public RequestType getType() {
        return RequestType.GetUsers;
    }
}
