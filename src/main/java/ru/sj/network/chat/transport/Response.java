package ru.sj.network.chat.transport;

/**
 * Created by Eugene Sinitsyn
 */

public class Response {
    public Response(Object data) {
        this.data = data;
    }

    private Object data;
    public Object getData() { return data; }
}
