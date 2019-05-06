package ru.sj.network.chat.api.model.request;

/**
 * Created by Eugene Sinitsyn
 */

public abstract class StringModel extends RequestBase {

    private String value = null;
    public String getValue() { return value; }
    protected void setValue(String value) { this.value = value; }
}
