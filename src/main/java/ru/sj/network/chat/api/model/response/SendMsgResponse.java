package ru.sj.network.chat.api.model.response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SendMsgResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;

    SendMsgResponse(StatusCode code) { this.setCode(code);}

    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
    }

    public static SendMsgResponse createUnauthorized() { return new SendMsgResponse(StatusCode.Unauthorized); }
    public static SendMsgResponse createFail() { return new SendMsgResponse(StatusCode.Error); }
    public static SendMsgResponse createOK() { return new SendMsgResponse(StatusCode.OK); }
}
