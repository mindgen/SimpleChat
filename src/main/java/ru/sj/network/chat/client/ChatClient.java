package ru.sj.network.chat.client;

import ru.sj.network.chat.api.model.MessageModel;
import ru.sj.network.chat.api.model.request.*;
import ru.sj.network.chat.api.model.response.*;
import ru.sj.network.chat.transport.INetworkTransport;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public final class ChatClient implements Closeable {

    private Socket client_socket;
    private INetworkTransport transport;
    private IChatEvents events;
    private String userName;

    public ChatClient(INetworkTransport transport, IChatEvents events) {
        this.transport = transport;
        this.events = events;
    }
    InetSocketAddress getAddress() { return this.client_socket != null ? (InetSocketAddress)client_socket.getRemoteSocketAddress() :
                                                                        null;}
    public synchronized void connect(InetSocketAddress address) throws IOException {
        if (null != this.client_socket) throw new IOException("Already connected");
        client_socket = new Socket();
        client_socket.connect(address);
        events.OnConnect();
    }

    public synchronized void close() throws IOException {
        this.onClose();
        client_socket.close();
        client_socket = null;
    }

    private String cookie;
    protected void onClose() {
        cookie = null;
        userName = null;
        events.OnDisconnect();
    }

    public boolean isConneted() {
        return this.client_socket == null ? false : this.client_socket.isConnected();
    }

    public boolean isRegistered() { return this.userName != null; }
    public String getUserName() { return this.userName; }

    public List<MessageModel> registration(String userName) throws IOException {
        RegistrationRequest req = (RegistrationRequest)RequestFactory.createRequest(RequestType.Registration);
        req.setName(userName);

        Response response = this.doRequest(this.createRequest(req));
        if (response.getData() instanceof RegistrationResponse) {
            RegistrationResponse regResponse = (RegistrationResponse)response.getData();
            boolean isSuccess = regResponse.getCode() == StatusCode.OK;
            if (isSuccess) {
                this.userName = userName;
            }
            events.OnRegistration(isSuccess);
            return regResponse.getMessages();
        }

        return null;
    }

    public boolean changeName(String newName) throws IOException {
        ChangeNameRequest req = (ChangeNameRequest) RequestFactory.createRequest(RequestType.ChangeName);
        req.setName(newName);

        Response response = this.doRequest(this.createRequest(req));
        if (response.getData() instanceof ChangeNameResponse) {
            ChangeNameResponse nameResponse = (ChangeNameResponse)response.getData();
            boolean isSuccess = nameResponse.getCode() == StatusCode.OK;
            if (isSuccess) {
                this.userName = newName;
            }

            events.OnChangeName(isSuccess);
            return isSuccess;
        }

        return false;
    }

    public int getUsersCount() throws IOException {
        GetUsersCountRequest req = (GetUsersCountRequest)RequestFactory.createRequest(RequestType.GetUsersCount);

        Response response = this.doRequest(this.createRequest(req));
        if (response.getData() instanceof GetUsersCountResponse) {
            GetUsersCountResponse usersCntResponse = (GetUsersCountResponse)response.getData();
            return usersCntResponse.getCount();
        }

        return -1;
    }

    public boolean sendMessage(String text) throws IOException {
        SendMsgRequest req = (SendMsgRequest)RequestFactory.createRequest(RequestType.SendMessage);
        req.setMessageText(text);

        Response response = this.doRequest(this.createRequest(req));
        if (response.getData() instanceof SendMsgResponse) {
            SendMsgResponse msgResponse = (SendMsgResponse)response.getData();

            boolean isSuccess = msgResponse.getCode() == StatusCode.OK;
            events.OnSendMessage(isSuccess);
            return isSuccess;
        }

        return false;
    }

    public void readMessages() throws IOException {
        while (true) {
            Response response = getRealTimeResponse();
            if (null == response) break;
            tryParseNewMessage((BaseResponse) response.getData());
        }
    }

    public boolean tryParseNewMessage(BaseResponse responseModel) {
        if (responseModel instanceof RealTimeResponse) {
            if (responseModel instanceof NewMessageResponse) {
                NewMessageResponse newMsgResponse = (NewMessageResponse)responseModel;
                events.OnNewMessage(newMsgResponse.getMessage());

                return true;
            }
        }

        return false;
    }

    private synchronized Response getRealTimeResponse() throws IOException {
        try {
            if (this.client_socket.getInputStream().available() > 0) {
                return this.transport.decodeResponse(this.client_socket.getInputStream());
            }
        } catch (Exception E) {
            close();
            throw E;
        }

        return null;
    }

    private synchronized Response doRequest(Request request) throws IOException {
        try {
            ByteArrayOutputStream dataStream = this.transport.encodeRequest(request);
            dataStream.writeTo(this.client_socket.getOutputStream());
            while(true) {
                Response response = this.transport.decodeResponse(this.client_socket.getInputStream());
                if (null != response) {
                    if (!tryParseNewMessage((BaseResponse) response.getData()))
                        return response;
                }
            }
        }
        catch (Exception e) {
            close();
            throw e;
        }
    }

    private Request createRequest(RequestBase model) {
        return this.transport.createRequest(model, null);
    }
}
