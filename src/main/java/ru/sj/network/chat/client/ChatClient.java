package ru.sj.network.chat.client;

import ru.sj.network.chat.api.model.request.RegistrationRequest;
import ru.sj.network.chat.api.model.request.RequestBase;
import ru.sj.network.chat.api.model.request.RequestFactory;
import ru.sj.network.chat.api.model.request.RequestType;
import ru.sj.network.chat.api.model.response.RegistrationResponse;
import ru.sj.network.chat.transport.INetworkTransport;
import ru.sj.network.chat.transport.Request;
import ru.sj.network.chat.transport.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class ChatClient {

    private Socket client_socket;
    private INetworkTransport transport;

    public ChatClient(INetworkTransport transport) {
        client_socket = new Socket();
        this.transport = transport;
    }
    InetSocketAddress getAddress() { return this.client_socket != null ? (InetSocketAddress)client_socket.getRemoteSocketAddress() :
                                                                        null;}
    public synchronized void connect(InetSocketAddress address) throws IOException {
        client_socket.connect(address);
    }

    public synchronized void close() throws IOException {
        this.onClose();
        client_socket.close();
    }

    protected void onClose() {
        userName = null;
    }

    public boolean isConneted() {
        return this.client_socket.isConnected();
    }

    private String userName;
    public boolean registration(String userName) throws IOException {
        RegistrationRequest req = (RegistrationRequest)RequestFactory.createRequest(RequestType.Registration);
        req.setName(userName);

        Response response = this.doRequest(this.createRequest(req));
        if (response.getData() instanceof RegistrationResponse) {
            RegistrationResponse regResponse = (RegistrationResponse)response.getData();
            return true;
        }

        return false;
    }

    private synchronized Response doRequest(Request request) throws IOException {
        ByteArrayOutputStream dataStream = this.transport.encodeRequest(request);
        dataStream.writeTo(this.client_socket.getOutputStream());
        return this.transport.decodeResponse(this.client_socket.getInputStream());
    }

    private Request createRequest(RequestBase model) {
        return this.transport.createRequest(model);
    }
}
