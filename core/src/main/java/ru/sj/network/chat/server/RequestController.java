package ru.sj.network.chat.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by Eugene Sinitsyn
 */

@Service
public class RequestController {

    @Autowired
    public void registerHandler(List<IHandler> handlers) {
        for (IHandler handler : handlers) {
          this.handlers.put(handler.getRequestModelClass().getName(), handler);
        }
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

    private IHandler getHandler(Class<?> model) {
        return handlers.get(model.getName());
    }

    private TreeMap<String, IHandler> handlers = new TreeMap<>();
}
