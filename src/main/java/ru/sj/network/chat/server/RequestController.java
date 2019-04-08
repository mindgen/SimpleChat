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

    public void registerHandler(Class<?> model, IHandler handler) {
        handlers.put(model, handler);
    }

    public void unregisterHandler(Class<?> model) {
        handlers.remove(model);
    }

    private IHandler getHandler(Class<?> model) {
        return handlers.get(model);
    }

    public Object doRequest(Request request, IResponseExtension extension) {
        IHandler handler = getHandler(request.getData().getClass());
        if (null != handler) {
            return handler.doRequest(request, extension);
        }

        return null;
    }

    private TreeMap<Class<?>, IHandler> handlers;
}
