package ru.sj.network.chat.client;

import java.util.Random;

/**
 * Created by Eugene Sinitsyn
 */

class RandomString {
    static private final String symbols = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String getString(int length, Random randomizer) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; ++i)
            builder.append(symbols.charAt(randomizer.nextInt(symbols.length())));

        return builder.toString();
    }
}
