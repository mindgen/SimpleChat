package ru.sj.chatApp;

import ru.sj.network.chat.api.model.response.GetUsersCountResponse;
import ru.sj.network.chat.client.FutureResponse;

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
            FutureResponse future = app.getClient().getUsersCount();
            GetUsersCountResponse response = utils.checkResponse(future.waitResponse(), GetUsersCountResponse.class, app);
            if (null != response)
                app.writeLine(String.valueOf(response.getCount()));
        }
        catch (Exception e) {}
    }
}
