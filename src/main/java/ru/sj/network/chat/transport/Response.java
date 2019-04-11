package ru.sj.network.chat.transport;

import ru.sj.network.chat.api.model.response.InternalErrorResponse;
import ru.sj.network.chat.server.IResponseExtension;

/**
 * Created by Eugene Sinitsyn
 */

public class Response implements IResponseExtension {
    public Response() { }

    private Object data = null;
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public void setErrorData() { this.setData(new InternalErrorResponse());}
}
