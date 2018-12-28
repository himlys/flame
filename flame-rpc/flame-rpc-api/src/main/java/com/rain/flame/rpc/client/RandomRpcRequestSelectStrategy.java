package com.rain.flame.rpc.client;

import com.rain.flame.remoting.exchange.ExchangeClient;

import java.util.List;
import java.util.Random;

class RandomRpcRequestSelectStrategy implements RpcRequestSelectStrategy {
    private final List<ExchangeClient> clients;

    public RandomRpcRequestSelectStrategy(List<ExchangeClient> clients) {
        this.clients = clients;
    }

    public ExchangeClient select() {
        int i = clients.size();
        int select = new Random().nextInt(i);
        return clients.get(select);
    }
}
