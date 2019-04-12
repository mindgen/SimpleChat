package ru.sj.network.chat.api.model.request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ChangeNameRequest extends AuthorizedRequest {
    @Override
    public RequestType getType() {
        return RequestType.ChangeName;
    }

    private String newName;
    public void setName(String userName) { this.newName = userName;}
    public String getName() { return this.newName; }

    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
    }
}
