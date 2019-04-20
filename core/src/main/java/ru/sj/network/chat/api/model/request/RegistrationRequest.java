package ru.sj.network.chat.api.model.request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Eugene Sinitsyn
 */

public class RegistrationRequest extends StringModel {
    private static final long serialVersionUID = 1L;

    public void setName(String userName) { this.setValue(userName);}

    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        oos.defaultWriteObject();
        oos.writeUTF(this.getValue());
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        String val = ois.readUTF();
        this.setValue(val);
    }

    @Override
    public RequestType getType() {
        return RequestType.Registration;
    }
}
