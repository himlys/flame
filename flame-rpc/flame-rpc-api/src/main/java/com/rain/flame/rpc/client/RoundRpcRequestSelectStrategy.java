package com.rain.flame.rpc.client;

import com.rain.flame.remoting.exchange.ExchangeClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class RoundRpcRequestSelectStrategy implements RpcRequestSelectStrategy {
    private final RpcRequestSelectStrategy rpcRequestSelectStrategy;
    private final List<ExchangeClient> clients;

    public RoundRpcRequestSelectStrategy(List<ExchangeClient> param) {
        this.clients = param;
        if (isPowerOfTwo(clients.size())) {
            this.rpcRequestSelectStrategy = new PowerofTwoRpcRequestSelectStrategy();
        } else {
            this.rpcRequestSelectStrategy = new NoRpcRequestSelectStrategy();
        }
    }

    @Override
    public ExchangeClient select() {
        return rpcRequestSelectStrategy.select();
    }

    private static boolean isPowerOfTwo(int val) {
        return (val & -val) == val;
    }

    private class PowerofTwoRpcRequestSelectStrategy implements RpcRequestSelectStrategy {
        private final AtomicInteger idx = new AtomicInteger();

        @Override
        public ExchangeClient select() {
            int i = idx.getAndIncrement() & clients.size() - 1;
            return clients.get(i);
        }
    }

    private class NoRpcRequestSelectStrategy implements RpcRequestSelectStrategy {
        private final AtomicInteger idx = new AtomicInteger();

        @Override
        public ExchangeClient select() {
            int i = Math.abs(idx.getAndIncrement() % clients.size());
            return clients.get(i);
        }
    }
}
