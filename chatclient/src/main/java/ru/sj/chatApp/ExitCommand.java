package ru.sj.chatApp;

/**
 * Created by Eugene Sinitsyn
 */

class ExitCommand implements IChatCommand {

    public ExitCommand(ClientApplication app) { this.app = app;}

    private ClientApplication app;

    @Override
    public String getCommandName() {
        return "exit";
    }

    @Override
    public String getUsageInfo() {
        return "exit -> Close client";
    }

    @Override
    public boolean parseArgs(String args) {
        return true;
    }

    @Override
    public void execute() {
        app.exit();
    }
}
