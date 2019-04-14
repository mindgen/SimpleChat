package ru.sj.network.chat.api.model.request;

import ru.sj.network.chat.transport.Request;

import java.io.Serializable;

public abstract class RequestBase implements Serializable {
    public abstract RequestType getType();
}
