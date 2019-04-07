package ru.sj.network.chat.server;

import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

import java.util.TreeMap;

/**
 * Created by Eugene Sinitsyn
 */

public class Controller {

    Controller() {
        this.handlers = new TreeMap<>();
    }

    public void registerHandler(Class<?> model, IHandler handler) {
        handlers.put(model, handler);
    }

    public void unregisterHandler(Class<?> model) {
        handlers.remove(model);
    }

    public IHandler getHandler(Class<?> model) {
        return handlers.get(model);
    }

    public Response doRequest(Request req) {
        Response result = null;
        IHandler handler = getHandler(req.getData().getClass());
        if (null != handler) {
            result = new Response(handler.doRequest(req));
        }
        else {

        }

        return result;

    }

    private TreeMap<Class<?>, IHandler> handlers;
}
