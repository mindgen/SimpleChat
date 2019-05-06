package ru.sj.network.chat.transport.binary;

import ru.sj.network.chat.server.ISession;
import ru.sj.network.chat.server.tcp.SessionsManagerImpl;
import ru.sj.network.chat.transport.Request;

public class BinaryRequest extends Request {
    public BinaryRequest(Object data, ISession session) { super(data); this.setSession(session); }
}
