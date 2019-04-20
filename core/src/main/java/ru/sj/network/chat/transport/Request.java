package ru.sj.network.chat.transport;

import ru.sj.network.chat.server.ISession;

/**
 * Created by Eugene Sinitsyn
 */

public class Request {
    public Request(Object data) {
        this.data = data;
    }

    private Object data;
    public Object getData() { return data; }
    protected void setData(Object data) { this.data = data; }

    private ISession session;
    protected void setSession(ISession session) { this.session = session; }
    public ISession getSession() { return this.session; }
}
