package ru.sj.network.chat.api.model.request;

/**
 * Created by Eugene Sinitsyn
 */

public abstract class StateModel {
    public StateModel(int lastState) {
        this.lastState = lastState;
    }

    private int lastState;
    public int getLastState() { return lastState; }
}
