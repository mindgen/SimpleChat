package ru.sj.chatApp;

import ru.sj.network.chat.client.ChatClient;

/**
 * Created by Eugene Sinitsyn
 */

class GetUsersCountCommand implements IChatCommand {

    public GetUsersCountCommand(ClientApplication app) { this.app = app;}

    private ClientApplication app;

    @Override
    public String getCommandName() {
        return "getuserscount";
    }

    @Override
    public String getUsageInfo() {
        return "getuserscount -> Show count of users in chat room";
    }

    @Override
    public boolean parseArgs(String args) {
        return true;
    }

    @Override
    public void execute() {
        try {
            app.writeLine(String.valueOf(app.getClient().getUsersCount()));
        }
        catch (Exception e) {}
    }
}
