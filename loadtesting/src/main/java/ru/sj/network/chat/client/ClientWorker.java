package ru.sj.network.chat.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sj.network.chat.api.model.MessageModel;
import ru.sj.network.chat.api.model.response.*;

import java.net.SocketAddress;
import java.util.Random;

import static java.lang.Thread.sleep;

/**
 * Created by Eugene Sinitsyn
 */

class ClientWorker implements Runnable, IChatEvents {
    static RandomString stringGeneration = new RandomString();

    private final Logger logger = LoggerFactory.getLogger("TestLoadLogger");

    Random randomizer;

    private SocketAddress endpoint;
    private int receivedMsg = 0;
    private int cmdCount = 0;
    private int cmdLimit = 0;

    public ClientWorker(SocketAddress endpoint) {
        this.endpoint = endpoint;
        this.randomizer = new Random();
    }

    public void setClient(IChatClient client) { this.client = client; }

    IChatEvents getEventsHandler() { return this; }

    void doWork() {
        (new Thread((Runnable) client)).start();

        while (true) {
            cmdCount = 0;
            this.cmdLimit = this.getRandomizer().nextInt(100) + 10;
            synchronized (this) {
                receivedMsg = 0;
            }
            randomSleep();
            if (!client.connect(this.endpoint)) break;

            doRegistration();
            while(doCmd()) {
                randomSleep();
            };

            client.disconnect();
        }
        client.stop();
    }

    void randomSleep() {

        try {
            sleep(this.getRandomizer().nextInt(30) * 100);
        }
        catch (Exception ex) {}
    }

    boolean doCmd() {
        boolean cmdResult = true;
        int curEvent = this.getRandomizer().nextInt();
        if (0 == (curEvent % 9)) {
            cmdResult = changeUsername();
        }
        else if(0 == (curEvent % 5)) {
            cmdResult = getUsersCount();
        }
        else
            cmdResult = sendMessage();

        ++cmdCount;

        if (!cmdResult) logIncorrectCmd();

        return !needReconnect();
    }

    boolean needReconnect() {
        return cmdCount > cmdLimit;
    }

    void doRegistration() {
        while (true) {
            FutureResponse future = client.registration(generateUsername());
            if (null == future) break;
            RegistrationResponse response = utils.getFinalResponse(future.waitResponse(), RegistrationResponse.class);
            if (null != response && StatusCode.OK == response.getCode()) break;
        }
    }

    boolean changeUsername() {
        FutureResponse future = client.changeUserName(this.generateUsername());
        if (null == future) return false;
        ChangeNameResponse response = utils.getFinalResponse(future.waitResponse(), ChangeNameResponse.class);
        return null != response && StatusCode.OK == response.getCode();
    }

    boolean sendMessage() {
        FutureResponse future = client.sendMessage(this.generateMessage());
        if (null == future) return false;
        SendMsgResponse response = utils.getFinalResponse(future.waitResponse(), SendMsgResponse.class);
        return null != response && StatusCode.OK == response.getCode();
    }

    boolean getUsersCount() {
        FutureResponse future = client.getUsersCount();
        if (null == future) return false;
        GetUsersCountResponse response = utils.getFinalResponse(future.waitResponse(), GetUsersCountResponse.class);
        return null != response && StatusCode.OK == response.getCode();
    }

    Random getRandomizer() { return this.randomizer; }

    String generateUsername() {
        return stringGeneration.getString(getRandomizer().nextInt(7) + 5, this.getRandomizer());
    }

    String generateMessage() {
        return stringGeneration.getString(getRandomizer().nextInt(100) + 5, this.getRandomizer());
    }

    void logIncorrectCmd() {
        logger.error("Incorrect response");

    }

    // Runnable
    @Override
    public void run() {
        doWork();
    }

    private IChatClient client;

    // IChatEvents
    @Override
    public void OnConnect() {
    }

    @Override
    public void OnDisconnect() {

    }

    @Override
    public synchronized void OnNewMessage(MessageModel msg) {
        receivedMsg++;
    }
}
