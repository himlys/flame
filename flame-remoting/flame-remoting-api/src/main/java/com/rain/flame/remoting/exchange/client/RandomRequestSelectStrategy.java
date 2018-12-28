package com.rain.flame.remoting.exchange.client;

import com.rain.flame.Request;
import com.rain.flame.remoting.exchange.ExchangeRequest;
import com.rain.flame.remoting.exchange.RequestSelectStrategy;

import java.util.List;
import java.util.Random;

class RandomRequestSelectStrategy implements RequestSelectStrategy {
    public Request select(List<Request> requests) {
        int i = requests.size();
        int select = new Random().nextInt(i);
        return requests.get(select);
    }
}
