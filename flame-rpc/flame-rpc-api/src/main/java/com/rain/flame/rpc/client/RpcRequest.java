package com.rain.flame.rpc.client;

import com.rain.flame.Request;
import com.rain.flame.common.URL;
import com.rain.flame.remoting.exchange.ExchangeClient;
import com.rain.flame.rpc.RpcInvocation;

import java.util.List;

public interface RpcRequest extends Request {
    RpcResponse execute(RpcInvocation rpcInvocation);

    List<ExchangeClient> getClients();

    List<RpcRequestInterceptor> getRpcRequestInterceptors();

    void setClients(List<ExchangeClient> clients);

    boolean refreshClients(ExchangeClient client, boolean add);

    boolean refreshClients(List<URL> urls);

    URL getUrl();

}