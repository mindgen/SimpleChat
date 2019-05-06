package ru.sj.network.chat.api.model.response;

import ru.sj.network.chat.api.model.MessageModel;
import ru.sj.network.chat.api.model.request.StringModel;
import ru.sj.network.chat.api.model.util.CollectionUtil;
import ru.sj.network.chat.server.storage.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ChangeNameResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    ChangeNameResponse(StatusCode code) { this.setCode(code); }

    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
    }

    public static ChangeNameResponse createUnauthorized() { return new ChangeNameResponse(StatusCode.Unauthorized); }
    public static ChangeNameResponse createFail() { return new ChangeNameResponse(StatusCode.Error); }
    public static ChangeNameResponse createOK() { return new ChangeNameResponse(StatusCode.OK); }
}
