package ru.sj.network.chat.api.model.response;

import ru.sj.network.chat.api.model.MessageModel;
import ru.sj.network.chat.api.model.util.CollectionUtil;
import ru.sj.network.chat.server.storage.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RegistrationResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;

    private transient List<MessageModel> msgList;
    private transient String cookie;

    RegistrationResponse(StatusCode code) { this.setCode(code); }
    RegistrationResponse(List<Message> messages, String cookie) {
        this.cookie = cookie;
        this.setCode(StatusCode.OK);
        this.msgList = new ArrayList<>();
        CollectionUtil.copyStorageMessageToModel(messages, this.msgList);
    }

    public String getCookie() { return this.cookie; }

    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        oos.defaultWriteObject();
        if (StatusCode.OK == this.getCode()) {
            oos.writeObject(this.cookie);
            oos.writeObject(this.msgList);
        }
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        if (StatusCode.OK == this.getCode()) {
            this.cookie = (String)ois.readObject();
            this.msgList = (List<MessageModel>)(ois.readObject());
        }
    }

    public static RegistrationResponse createFail() { return new RegistrationResponse(StatusCode.Error); }
    public static RegistrationResponse createOK(List<Message> messages, String cookie) { return new RegistrationResponse(messages, cookie); }
}
