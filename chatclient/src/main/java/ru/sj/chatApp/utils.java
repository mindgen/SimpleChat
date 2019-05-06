package ru.sj.chatApp;

import ru.sj.network.chat.api.model.response.BaseResponse;

import static ru.sj.network.chat.client.utils.getFinalResponse;

/**
 * Created by Eugene Sinitsyn
 */

public class utils {
    static <T extends BaseResponse> T checkResponse(BaseResponse response, Class<T> castClass, ClientApplication app) {
        T result = getFinalResponse(response, castClass);
        if (null == result) {
            try {
                app.writeErrorAndExit("Incorrect response");
            }
            catch (Exception E) {}
        }

        return result;
    }
}
