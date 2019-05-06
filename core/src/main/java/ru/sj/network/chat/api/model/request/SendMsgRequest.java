package ru.sj.network.chat.api.model.request;

import ru.sj.network.chat.transport.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Eugene Sinitsyn
 */

public class SendMsgRequest extends RequestBase {
    private static final long serialVersionUID = 1L;

    private transient String messageText;
    public void setMessageText(String value) { this.messageText = value; }
    public String getMessageText() { return this.messageText; }

    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        oos.defaultWriteObject();
        oos.writeUTF(this.getMessageText());
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
       this.messageText = ois.readUTF();
    }

    @Override
    public RequestType getType() {
        return RequestType.SendMessage;
    }
}
