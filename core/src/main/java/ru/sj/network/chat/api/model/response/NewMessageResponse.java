package ru.sj.network.chat.api.model.response;

import ru.sj.network.chat.api.model.MessageModel;
import ru.sj.network.chat.api.model.TextMessageModel;

/**
 * Created by Eugene Sinitsyn
 */

public class NewMessageResponse extends RealTimeResponse {
    private static final long serialVersionUID = 1L;

    NewMessageResponse(StatusCode code) { this.setCode(code); }
    NewMessageResponse(String userName, String msg) {
        this.setCode(StatusCode.OK);
        this.msgModel = new TextMessageModel(userName, msg);
    }

    private MessageModel msgModel;

    public MessageModel getMessage() { return this.msgModel; }

    public static NewMessageResponse createOK(String userName, String msg) { return new NewMessageResponse(userName, msg); }
}
