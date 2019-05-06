package ru.sj.network.chat.client;

import ru.sj.network.chat.api.model.response.BaseResponse;

/**
 * Created by Eugene Sinitsyn
 */

public class utils {
    public static <T extends BaseResponse> T getFinalResponse(BaseResponse response, Class<T> castClass) {
        if (castClass.isInstance(response))
            return (T)response;
        else
            return null;
    }
}
