package ru.sj.network.chat.api.model.request;

import java.io.Serializable;

public abstract class AuthorizedRequest extends RequestBase implements Serializable {

    private String cookie;
    public void setCookie(String value) { this.cookie = value; }
    public String getCookie() { return this.cookie; }
}
