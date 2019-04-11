package ru.sj.network.chat.api.model.request;

import ru.sj.network.chat.transport.Request;

public abstract class RequestBase {
    public abstract RequestType getType();
}
