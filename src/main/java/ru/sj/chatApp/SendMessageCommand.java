package ru.sj.chatApp;

import ru.sj.network.chat.client.ChatClient;

/**
 * Created by Eugene Sinitsyn
 */

public class SendMessageCommand implements IChatCommand {

    public SendMessageCommand(ClientApplication app) { this.app = app;}

    private ClientApplication app;

    @Override
    public String getCommandName() {
        return "send";
    }

    @Override
    public String getUsageInfo() {
        return "send <Message> -> Send new message to chat";
    }

    @Override
    public boolean parseArgs(String args) {
        if (null == args) return false;

        this.message = args;

        return true;
    }

    @Override
    public void execute() {
        try {
            app.getClient().sendMessage(message);
        }
        catch (Exception e) {}
    }

    private String message;
}
