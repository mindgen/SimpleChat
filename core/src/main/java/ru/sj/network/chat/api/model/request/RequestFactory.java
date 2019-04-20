package ru.sj.network.chat.api.model.request;

public class RequestFactory {
    public static RequestBase createRequest(RequestType type) {
        switch (type) {
            case Registration:
                return new RegistrationRequest();
            case GetUsersCount:
                return new GetUsersCountRequest();
            case SendMessage:
                return new SendMsgRequest();
            case ChangeName:
                return new ChangeNameRequest();
             default:
                 return null;
        }
    }
}
