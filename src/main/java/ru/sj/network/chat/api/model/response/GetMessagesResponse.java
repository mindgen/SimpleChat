package ru.sj.network.chat.api.model.response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GetMessagesResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;

    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
    }
}
