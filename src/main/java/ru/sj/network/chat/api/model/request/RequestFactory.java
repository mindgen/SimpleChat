package ru.sj.network.chat.api.model.request;

public class RequestFactory {
    public static RequestBase createRequest(RequestType type) {
        switch (type) {
            case Registration:
                return new RegistrationRequest();
            case GetUsers:
                return new GetUsersRequest();
            case GetMessages:
                return new GetMessagesRequest();
            case SendMessage:
                return new SendMsgRequest();
             default:
                 return null;
        }
    }
}
