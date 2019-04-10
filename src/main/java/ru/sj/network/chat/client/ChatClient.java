package ru.sj.network.chat.client;

import ru.sj.network.chat.api.model.request.RegistrationRequest;
import ru.sj.network.chat.api.model.request.RequestFactory;
import ru.sj.network.chat.api.model.request.RequestType;
import ru.sj.network.chat.transport.INetworkTransport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ChatClient {

    private Socket client_socket;
    private INetworkTransport transport;

    public ChatClient(INetworkTransport transport) {
        client_socket = new Socket();
        this.transport = transport;
    }
    InetSocketAddress getAddress() { return this.client_socket != null ? (InetSocketAddress)client_socket.getRemoteSocketAddress() :
                                                                        null;}
    public void connect(InetSocketAddress address) throws IOException {
        client_socket.connect(address);
    }

    public void close() throws IOException {
        client_socket.close();
        this.onClose();
    }

    protected void onClose() {
        userName = null;
    }

    public boolean isConneted() {
        return this.client_socket.isConnected();
    }

    private String userName;
    boolean registration(String userName) {
        RegistrationRequest req = (RegistrationRequest)RequestFactory.createRequest(RequestType.Registration);
        req.setName(userName);

        transport.
    }
}
