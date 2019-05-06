package ru.sj.chatApp;

import ru.sj.network.chat.api.model.response.SendMsgResponse;
import ru.sj.network.chat.client.FutureResponse;

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
            FutureResponse future = app.getClient().sendMessage(message);
            SendMsgResponse response = utils.checkResponse(future.waitResponse(), SendMsgResponse.class, app);
            if (null != response)
                app.writeLine("OK");
        }
        catch (Exception e) {}
    }

    private String message;
}
