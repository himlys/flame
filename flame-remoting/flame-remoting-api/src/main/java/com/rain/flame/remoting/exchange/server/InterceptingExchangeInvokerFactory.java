package com.rain.flame.remoting.exchange.server;

import com.rain.flame.common.URL;
import com.rain.flame.remoting.exchange.ExchangeInterceptor;
import com.rain.flame.remoting.exchange.ExchangeInvoker;
import com.rain.flame.remoting.exchange.ExchangeInvokerFactory;

import java.util.List;

public class InterceptingExchangeInvokerFactory extends AbstractExchangeInvokerFactoryWrapper {
    private final List<ExchangeInterceptor> exchangeInterceptorList;

    public InterceptingExchangeInvokerFactory(ExchangeInvokerFactory exchangeInvokerFactory, List<ExchangeInterceptor> exchangeInterceptorList) {
        super(exchangeInvokerFactory);
        this.exchangeInterceptorList = exchangeInterceptorList;
    }

    public List<ExchangeInterceptor> getInterceptors() {
        return exchangeInterceptorList;
    }

    @Override
    public ExchangeInvoker createExchangeInvoker(URL url) {
        return new InterceptingExchangeInvoker(getExchangeInvokerFactory(), exchangeInterceptorList, url);
    }
}
