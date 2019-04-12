package ru.sj.network.chat.api.model.response;

import ru.sj.network.chat.api.model.util.CollectionUtil;
import ru.sj.network.chat.server.storage.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GetUsersCountResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;

    GetUsersCountResponse(StatusCode code) { this.setCode(code); }
    GetUsersCountResponse(int value) {
        this.setCode(StatusCode.OK);
        this.count = value;
    }

    private transient int count;

    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        oos.defaultWriteObject();
        if (StatusCode.OK == this.getCode()) {
            oos.writeInt(this.count);
        }
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        if (StatusCode.OK == this.getCode()) {
            this.count = ois.readInt();
        }
    }

    public static GetUsersCountResponse createUnauthorized() { return new GetUsersCountResponse(StatusCode.Unauthorized); }
    public static GetUsersCountResponse createOK(int count) { return new GetUsersCountResponse(count); }
}
