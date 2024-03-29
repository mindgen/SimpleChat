package ru.sj.network.chat.api.model.response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Eugene Sinitsyn
 */

public class InternalErrorResponse extends BaseResponse {
    public InternalErrorResponse() { this.setCode(StatusCode.Error); }

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
