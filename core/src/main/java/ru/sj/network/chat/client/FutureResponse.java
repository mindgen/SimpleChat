package ru.sj.network.chat.client;

import ru.sj.network.chat.api.model.response.BaseResponse;

/**
 * Created by Eugene Sinitsyn
 */

public class FutureResponse {

    private BaseResponse response;

    public synchronized BaseResponse getResponse() {
        return this.response;
    }

    boolean isReady() {
        return this.getResponse() != null;
    }

    synchronized void setResponse(BaseResponse response) {
        this.response = response;
    }
}
