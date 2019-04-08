package ru.sj.network.chat.server;

import ru.sj.network.chat.server.storage.ChatRoom;

/**
 * Created by Eugene Sinitsyn
 */

public final class ExecutionContext {

    ExecutionContext(){}

    private ChatRoom mainRoom;
    public ChatRoom getMainRoom() {
        return mainRoom;
    }

    public void setMainRoom(ChatRoom mainRoom) {
        this.mainRoom = mainRoom;
    }

    private RequestController requestController;
    public RequestController getRequestController() {
        return requestController;
    }

    public void setRequestController(RequestController requestController) {
        this.requestController = requestController;
    }

    private RequestExecutor executor;
    public RequestExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(RequestExecutor executor) {
        this.executor = executor;
    }

    private static ExecutionContext ctx;
    public synchronized static ExecutionContext getInstance()
    {
        if (null == ctx) {
            ctx = new ExecutionContext();
        }

        return ctx;
    }
}
