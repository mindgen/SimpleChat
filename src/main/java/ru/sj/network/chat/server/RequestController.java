package ru.sj.network.chat.server;

import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

import java.util.TreeMap;

/**
 * Created by Eugene Sinitsyn
 */

public class RequestController {

    public RequestController() {
        this.handlers = new TreeMap<>();
    }

    public void registerHandler(IHandler handler) {
        handlers.put(handler.getRequestModelClass().getName(), handler);
    }

    public void unregisterHandler(IHandler handler) {
        handlers.remove(handler.getRequestModelClass());
    }

    private IHandler getHandler(Class<?> model) {
        return handlers.get(model.getName());
    }

    public void doRequest(Request request, Response response) {
        IHandler handler = getHandler(request.getData().getClass());
        if (null != handler) {
            handler.doRequest(request, response);
        }
        else {
            response.setErrorData();
        }
    }

    private TreeMap<String, IHandler> handlers;
}
