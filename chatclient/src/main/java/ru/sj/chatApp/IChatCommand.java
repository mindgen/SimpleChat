package ru.sj.chatApp;

/**
 * Created by Eugene Sinitsyn
 */

interface IChatCommand {

    String getCommandName();
    String getUsageInfo();

    boolean parseArgs(String args);
    void execute();
}
