package com.rain.flame.remoting.exchange.server;


import com.rain.flame.remoting.exchange.ExchangeInvokerFactory;

public abstract class AbstractExchangeInvokerFactoryWrapper implements ExchangeInvokerFactory {
    private final ExchangeInvokerFactory exchangeInvokerFactory;

    protected AbstractExchangeInvokerFactoryWrapper(ExchangeInvokerFactory exchangeInvokerFactory) {
        this.exchangeInvokerFactory = exchangeInvokerFactory;
    }

    public ExchangeInvokerFactory getExchangeInvokerFactory() {
        return exchangeInvokerFactory;
    }
}
