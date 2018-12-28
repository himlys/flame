package com.rain.flame.remoting.exchange.client;

import com.rain.flame.Invocation;
import com.rain.flame.Request;
import com.rain.flame.Response;
import com.rain.flame.remoting.exchange.ExchangeRequest;

import java.util.Collection;
import java.util.List;

public class DefaultExchangeRequest implements ExchangeRequest {
    private List<Request> requests;
    private final RandomRequestSelectStrategy defaultReuqstStrategy = new RandomRequestSelectStrategy();

    public DefaultExchangeRequest(List<Request> requests) {
        this.requests = requests;
    }

    @Override
    public Response execute(Invocation invocation) {
        Request request = defaultReuqstStrategy.select(requests);
        return request.execute(invocation);
    }
}
