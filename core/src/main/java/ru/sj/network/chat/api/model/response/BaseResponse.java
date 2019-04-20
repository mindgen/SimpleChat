package ru.sj.network.chat.api.model.response;

import java.io.Serializable;

public abstract class BaseResponse implements Serializable {

    private StatusCode code;
    public StatusCode getCode() { return code; }
    protected void setCode(StatusCode code) { this.code = code; }
}
