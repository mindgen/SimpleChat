package ru.sj.chatApp;

import ru.sj.chatApp.IChatCommand;
import ru.sj.network.chat.client.ChatClient;
import ru.sj.network.chat.client.IChatEvents;
import ru.sj.network.chat.transport.ObjectModelSerializer;
import ru.sj.network.chat.transport.binary.BinaryTransport;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

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

    ChatClient client = new ChatClient(new BinaryTransport(new ObjectModelSerializer()), this);
    ChatClient getClient() { return this.client; }

    public static void main(String[] args) {
        ClientApplication app = new ClientApplication();
        app.doIt();
    }

    public void doIt() {
        try {
            doGreetings();
            while (!client.isConneted()) {
                doEnterServer();
                try {
                    client.connect(new InetSocketAddress(this.srvName, this.port));
                }
                catch (Exception e) {
                    this.writeLine("Incorrect server");
                }
            }

            while (!client.isRegistered()) {
                doEnterName();
                client.registration(this.userName);
            }

            doCommandLoop();
            clearCmds();
            client.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    private void doCommandLoop() throws IOException {
        try {
            while (isAlive()) {
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

    void writeLine(String value) throws  IOException {
        this.getWriter().write(value);
        this.getWriter().newLine();
        this.getWriter().flush();
    }

    void write(String value) throws  IOException {
        this.getWriter().write(value);
        this.getWriter().flush();
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
        if (cmd.isBlank() || cmd.isEmpty()) return false;
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
        }
        catch (Exception e) { }
    }

    @Override
    public void OnRegistration(boolean success) {
        try {
            if (success) {
                writeLine("Registration is done");
                writeLine("Enter command:");
            }
            else {
                writeLine("Registration fail. Enter another name");
            }
        }
        catch (Exception e) { }
    }

    @Override
    public void OnChangeName(boolean success) {
        try {
            if (success) {
                writeLine("Name changed");
            } else {
                writeLine("Name already in use another user");
            }
        }
        catch (Exception e) { }
    }

    @Override
    public void OnSendMessage(boolean success) {

    }
}
