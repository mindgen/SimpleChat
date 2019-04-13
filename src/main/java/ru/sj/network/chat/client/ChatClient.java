package ru.sj.network.chat.client;

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

    public boolean registration(String userName) throws IOException {
        RegistrationRequest req = (RegistrationRequest)RequestFactory.createRequest(RequestType.Registration);
        req.setName(userName);

        Response response = this.doRequest(this.createRequest(req));
        if (response.getData() instanceof RegistrationResponse) {
            RegistrationResponse regResponse = (RegistrationResponse)response.getData();
            boolean isSuccess = regResponse.getCode() == StatusCode.OK;
            if (isSuccess) {
                this.cookie = regResponse.getCookie();
                this.userName = userName;
            }
            events.OnRegistration(isSuccess);
            return isSuccess;
        }

        return false;
    }

    public boolean changeName(String newName) throws IOException {
        ChangeNameRequest req = (ChangeNameRequest) RequestFactory.createRequest(RequestType.ChangeName);
        req.setName(newName);
        req.setCookie(cookie);

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
        req.setCookie(cookie);

        Response response = this.doRequest(this.createRequest(req));
        if (response.getData() instanceof GetUsersCountResponse) {
            GetUsersCountResponse usersCntResponse = (GetUsersCountResponse)response.getData();
            return usersCntResponse.getCount();
        }

        return -1;
    }

    public boolean sendMessage(String text) throws IOException {
        SendMsgRequest req = (SendMsgRequest)RequestFactory.createRequest(RequestType.SendMessage);
        req.setCookie(cookie);
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

    private synchronized Response doRequest(Request request) throws IOException {
        try {
            ByteArrayOutputStream dataStream = this.transport.encodeRequest(request);
            dataStream.writeTo(this.client_socket.getOutputStream());
            return this.transport.decodeResponse(this.client_socket.getInputStream());
        }
        catch (Exception e) {
            close();
            throw e;
        }
    }

    private Request createRequest(RequestBase model) {
        return this.transport.createRequest(model);
    }
}
