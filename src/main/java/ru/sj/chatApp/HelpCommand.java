package ru.sj.chatApp;

import javax.swing.plaf.ButtonUI;
import java.util.Collection;

/**
 * Created by Eugene Sinitsyn
 */

class HelpCommand implements IChatCommand {

    public HelpCommand(ClientApplication app) { this.app = app;}

    ClientApplication app;

    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public String getUsageInfo() {
        return "help -> Print this info";
    }

    @Override
    public boolean parseArgs(String args) {
        return true;
    }

    @Override
    public void execute() {
        StringBuilder builder = new StringBuilder();
        Collection<IChatCommand> cmds = app.getAllCmds();
        for (IChatCommand cmd : cmds) {
            builder.append(cmd.getUsageInfo());
            builder.append(System.getProperty("line.separator"));
        }

        try {
            app.writeLine(builder.toString());
        }
        catch (Exception e){}
    }
}
