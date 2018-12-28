package com.rain.flame.remoting.exchange;

import com.rain.flame.Channel;
import com.rain.flame.remoting.exchange.interceptors.server.LoggingExchangeInterceptor;
import com.rain.flame.remoting.exchange.server.InterceptingExchangeInvokerFactory;
import com.rain.flame.remoting.exchange.server.SimpleExchangeInvokerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class ExchangeHandlerAdapter implements ExchangeHandler {
    private List<ExchangeInterceptor> exchangeInterceptors = new ArrayList<>();
    private ExchangeInvokerFactory interceptingInvokerFactory;

    protected ExchangeInvokerFactory getExchangeInvokerFactory() {
        ExchangeInvokerFactory invokerFactory = interceptingInvokerFactory;
        if (invokerFactory == null) {
            invokerFactory = doGetExchangeInvokerFactory();
            interceptingInvokerFactory = invokerFactory;
        }
        return interceptingInvokerFactory;
    }

    private ExchangeInvokerFactory doGetExchangeInvokerFactory() {
        exchangeInterceptors.add(new LoggingExchangeInterceptor());
        return new InterceptingExchangeInvokerFactory(getDefaultExchangeInvokerFactory(), exchangeInterceptors);
    }

    private ExchangeInvokerFactory getDefaultExchangeInvokerFactory() {
        return new SimpleExchangeInvokerFactory();
    }

    @Override
    public void connected(Channel channel) {

    }

    @Override
    public void disconnected(Channel channel) {

    }

    @Override
    public void sent(Channel channel, Object message) {

    }

    @Override
    public void received(Channel channel, Object message) {

    }

    @Override
    public void caught(Channel channel, Throwable exception) {

    }

}
