package ru.sj.chatApp;

import ru.sj.network.chat.client.ChatClient;

/**
 * Created by Eugene Sinitsyn
 */

class ChangeNameCommand implements IChatCommand {

    public ChangeNameCommand(ClientApplication app) { this.app = app; }

    private ClientApplication app;

    @Override
    public String getCommandName() {
        return "changename";
    }

    @Override
    public String getUsageInfo() {
        return "changename <New Name> -> Change current User name to New name";
    }

    @Override
    public boolean parseArgs(String args) {
        if (null == args) return false;
        String[] vars = args.split("\\p{javaSpaceChar}+");
        if (vars.length == 0) return false;
        this.newName = vars[0];

        return true;
    }

    @Override
    public void execute() {
        try {
            app.getClient().changeName(this.newName);
        }
        catch (Exception e) {}
    }

    private String newName;
}
