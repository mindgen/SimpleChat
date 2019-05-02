package ru.sj.chatApp;

import ru.sj.network.chat.api.model.response.BaseResponse;
import ru.sj.network.chat.api.model.response.ChangeNameResponse;
import ru.sj.network.chat.api.model.response.StatusCode;
import ru.sj.network.chat.client.FutureResponse;

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
            FutureResponse future = app.getClient().changeUserName(this.newName);
            ChangeNameResponse response = utils.checkResponse(future.waitResponse(), ChangeNameResponse.class, app);
            if (null != response) {
                if (StatusCode.OK == response.getCode())
                    app.writeLine("Username changes successfully");
                else
                    app.writeLine("Username already in use");
            }
        }
        catch (Exception e) {}
    }

    private String newName;
}
