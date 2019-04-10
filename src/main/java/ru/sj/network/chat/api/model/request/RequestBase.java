package ru.sj.network.chat.api.model.request;

public abstract class RequestBase {

    public RequestBase(RequestType type) {this.type = type; }

    private transient RequestType type;
    public RequestType getType() { return this.type; }
}
