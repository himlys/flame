package com.rain.flame.remoting.exchange.server;

import com.rain.flame.Request;
import com.rain.flame.common.URL;
import com.rain.flame.remoting.Client;
import com.rain.flame.remoting.exchange.ExchangeClient;
import com.rain.flame.remoting.exchange.ExchangeInvocation;
import com.rain.flame.remoting.exchange.ExchangeInvocationResult;

public class DefaultExchangeClient implements ExchangeClient {
    private final Client client;
    private final URL url;

    public DefaultExchangeClient(URL url, Client client) {
        this.url = url;
        this.client = client;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isActive() {
        return client.isActive();
    }

    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public ExchangeInvocationResult send(Request request, Object param) {
        ExchangeInvocation exchangeInvocation = new ExchangeInvocation();
        exchangeInvocation.setData(param);
        exchangeInvocation.setRequestType("flame");
        exchangeInvocation.setId(request.getId());
        Object object = client.send(exchangeInvocation);
        ExchangeInvocationResult result = new ExchangeInvocationResult();
        result.setData(object);
        result.setResponseCode(ExchangeInvocationResult.ResponseCode.OK);
        result.setId(exchangeInvocation.getId());
        return result;
    }
}
