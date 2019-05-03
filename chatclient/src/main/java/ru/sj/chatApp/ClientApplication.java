package ru.sj.chatApp;

import ru.sj.network.chat.api.model.MessageModel;
import ru.sj.network.chat.api.model.TextMessageModel;
import ru.sj.network.chat.api.model.response.BaseResponse;
import ru.sj.network.chat.api.model.response.RegistrationResponse;
import ru.sj.network.chat.api.model.response.StatusCode;
import ru.sj.network.chat.client.*;
import ru.sj.network.chat.transport.MessageBuffer;
import ru.sj.network.chat.transport.ObjectModelSerializer;
import ru.sj.network.chat.transport.binary.BinaryTransport;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;

public final class ClientApplication implements IChatEvents {

    private BufferedReader inStream = new BufferedReader(new InputStreamReader(System.in));
    private BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(System.out));

    private String srvName;
    private int port;

    private String userName;
    private volatile boolean work = true;

    private TreeMap<String, IChatCommand> cmds = new TreeMap<>();

    public ClientApplication() {
        addCmd(new ChangeNameCommand(this));
        addCmd(new GetUsersCountCommand(this));
        addCmd(new SendMessageCommand(this));
        addCmd(new HelpCommand(this));
        addCmd(new ExitCommand(this));
    }

    private void addCmd(IChatCommand cmd) {
        cmds.put(cmd.getCommandName(), cmd);
    }

    private IChatCommand getCmd(String cmd) {
        return cmds.get(cmd);
    }

    private void clearCmds() {
        cmds.clear();
    }

    Collection<IChatCommand> getAllCmds() {
        return Collections.unmodifiableCollection(cmds.values());
    }

    IChatClient client = new ChatClient(new BinaryTransport(new ObjectModelSerializer()), this,
            ByteBuffer.allocate(1024), new MessageBuffer());
    IChatClient getClient() { return this.client; }

    public static void main(String[] args) {
        ClientApplication app = new ClientApplication();
        app.doIt();
    }

    public void doIt() {
        (new Thread((Runnable) client)).start();

        try {
            doGreetings();
            while (!client.isConnected()) {
                doEnterServer();
                try {
                    client.connect(new InetSocketAddress(this.srvName, this.port));
                }
                catch (Exception e) {
                    this.writeLine("Incorrect server");
                }
            }

            doRegistration();

            doCommandLoop();
            clearCmds();
            client.stop();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    private void doCommandLoop() throws IOException {
        try {
            while (isAlive()) {
                write("Enter command:");
                String cmdValue = readLine();
                if (!doExecuteCommand(cmdValue)) doIncorectCommand();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private BufferedReader getReader() { return this.inStream; }
    private BufferedWriter getWriter() { return this.outStream; }

    synchronized void writeLine(String value) throws  IOException {
        this.getWriter().write(value);
        this.getWriter().newLine();
        this.getWriter().flush();
    }

    synchronized void write(String value) throws  IOException {
        this.getWriter().write(value);
        this.getWriter().flush();
    }

    synchronized void writeErrorAndExit(String val) throws IOException {
        this.getWriter().write("Error: ");
        this.getWriter().write(val);
        this.getWriter().newLine();
        this.getWriter().flush();
        exit();
    }

    private void writeMessage(MessageModel msg) throws IOException {
        if (null != msg && (msg instanceof TextMessageModel)) {
            TextMessageModel txtMsg = (TextMessageModel)msg;
            writeLine(String.format("'%s': %s", txtMsg.getUserName(), txtMsg.getText()));
        }
    }

    private String readLine() throws IOException {
        return this.getReader().readLine();
    }

    private void doGreetings() throws IOException {
        writeLine("Welcome to console chat");
    }

    private void doEnterServer() throws IOException {
        writeLine("Enter server name:");
        this.srvName = readLine();

        writeLine("Enter server port:");
        this.port = Integer.parseInt(readLine());
    }

    private void doEnterName() throws IOException {
        writeLine("Enter user name:");
        this.userName = readLine();
    }

    private void doIncorectCommand() throws IOException {
        writeLine("Incorrect command. Use 'help' to showing available commands.");
    }

    public void exit() { this.work = false; }
    private boolean isAlive() { return this.work; }

    private boolean doExecuteCommand(String cmd) {
        if (null == cmd || cmd.isEmpty() || cmd.isBlank()) return false;
        int cmd_last = cmd.indexOf(" ");

        String cmdName = ((-1 == cmd_last) ? cmd : cmd.substring(0, cmd_last)).toLowerCase();
        String cmdArgs = ((-1 == cmd_last) ? "" : cmd.substring(cmd_last, cmd.length())).trim().toLowerCase();

        IChatCommand cmdExec = this.getCmd(cmdName);
        if (null != cmdExec && cmdExec.parseArgs(cmdArgs)) {
            cmdExec.execute();
            return true;
        }

        return false;
    }

    void doRegistration() throws IOException {
        while (true) {
            doEnterName();
            FutureResponse future = client.registration(this.userName);
            BaseResponse response = future.waitResponse();
            RegistrationResponse regResp = utils.checkResponse(response, RegistrationResponse.class, this);
            if (null != regResp) {
                List<MessageModel> messages = regResp.getMessages();
                if (null != messages) {
                    messages.forEach((msg) -> {
                        try {
                            writeMessage(msg);
                        } catch (Exception E){}
                    });
                }
                if (StatusCode.OK == regResp.getCode())
                    break;
                else
                    this.writeLine("Name already in use");
            }
        }
    }

    // IChatEvents implementation

    @Override
    public void OnConnect() {
        try {
            writeLine("Connected successfully");
        }
        catch (Exception e) { }
    }

    @Override
    public void OnDisconnect() {
        try {
            writeLine("Disconnected");
            this.exit();
        }
        catch (Exception e) { }
    }

    @Override
    public void OnNewMessage(MessageModel msg) {
        try {
            writeMessage(msg);
        }
        catch (Exception E) {}
    }
}
