package ru.sj.network.chat.api.model.request;

/**
 * Created by Eugene Sinitsyn
 */

public abstract class StringModel {

    StringModel(String value) {
        this.value = value;
    }

    private String value;
    public String getValue() { return value; }
}
