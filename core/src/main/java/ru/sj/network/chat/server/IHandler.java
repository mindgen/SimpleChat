package ru.sj.network.chat.server;

import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

/**
 * Created by Eugene Sinitsyn
 */

public interface IHandler {
    void doRequest(Request request, Response response);
    Class<?> getRequestModelClass();
}
