package com.rain.flame.rpc;

import com.rain.flame.common.URL;
import com.rain.flame.remoting.Client;
import com.rain.flame.remoting.exchange.ExchangeClient;
import com.rain.flame.rpc.client.RpcRequest;

import java.util.List;
import java.util.Map;

public interface Protocol {
    <T> RpcRequest refer(Class<T> type, URL url);

    void export(URL url);

    Map<String, Client> getClients();

    String getClientKey(URL url);

    void refreshRequest(RpcRequest request, List<URL> urls);

    boolean inactiveClient(ExchangeClient client);
}
