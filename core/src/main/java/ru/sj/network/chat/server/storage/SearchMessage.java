package ru.sj.network.chat.server.storage;

/**
 * Created by Eugene Sinitsyn
 */

class SearchMessage extends Message {

    SearchMessage(int id) {
        super();
        this.id = id;
    }

    SearchMessage(long timeStamp) {
        super();
        this.timestamp = timeStamp;
    }
}
