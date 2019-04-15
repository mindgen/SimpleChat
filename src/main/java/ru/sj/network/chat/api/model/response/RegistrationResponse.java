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

    RegistrationResponse(StatusCode code) { this.setCode(code); }
    RegistrationResponse(List<Message> messages) {
        this.setCode(StatusCode.OK);
        this.msgList = new ArrayList<>();
        CollectionUtil.copyStorageMessageToModel(messages, this.msgList);
    }

    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        oos.defaultWriteObject();
        if (StatusCode.OK == this.getCode()) {
            oos.writeObject(this.msgList);
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        if (StatusCode.OK == this.getCode()) {
            Object listObj = ois.readObject();
            if (listObj instanceof ArrayList)
                this.msgList = (ArrayList<MessageModel>)listObj;
        }
    }

    public List<MessageModel> getMessages() { return this.msgList; }

    public static RegistrationResponse createFail() { return new RegistrationResponse(StatusCode.Error); }
    public static RegistrationResponse createOK(List<Message> messages) { return new RegistrationResponse(messages); }
}
