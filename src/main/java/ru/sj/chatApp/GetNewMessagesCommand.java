package ru.sj.chatApp;

/**
 * Created by Eugene Sinitsyn
 */

public class GetNewMessagesCommand implements IChatCommand  {

    public GetNewMessagesCommand(ClientApplication app) { this.app = app;}

    private ClientApplication app;

    @Override
    public String getCommandName() {
        return "checkmsg";
    }

    @Override
    public String getUsageInfo() {
        return "checkmsg -> Check new messages";
    }

    @Override
    public boolean parseArgs(String args) {
        return true;
    }

    @Override
    public void execute() {
        try {
            app.client.readMessages();
        }
        catch (Exception e) {}
    }
}
