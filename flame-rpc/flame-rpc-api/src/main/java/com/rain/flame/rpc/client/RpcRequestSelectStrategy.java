package com.rain.flame.rpc.client;

import com.rain.flame.remoting.exchange.ExchangeClient;

import java.util.List;

public interface RpcRequestSelectStrategy {
    ExchangeClient select();
}
