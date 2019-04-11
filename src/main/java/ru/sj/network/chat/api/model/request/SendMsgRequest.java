package ru.sj.network.chat.api.model.request;

import ru.sj.network.chat.transport.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Eugene Sinitsyn
 */

public class SendMsgRequest extends StringModel implements Serializable {
    private static final long serialVersionUID = 1L;

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
        return RequestType.SendMessage;
    }
}
