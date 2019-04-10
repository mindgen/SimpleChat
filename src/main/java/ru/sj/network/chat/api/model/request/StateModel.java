package ru.sj.network.chat.api.model.request;

/**
 * Created by Eugene Sinitsyn
 */

public abstract class StateModel extends RequestBase {
    public StateModel(RequestType type) { super(type); }


    private int lastState = -1;
    public int getLastState() { return lastState; }
    protected void setLastState(int value) { this.lastState = value; }
}
