package com.rain.flame.remoting.exchange.support;

import com.rain.flame.remoting.exchange.ExchangeInterceptor;
import com.rain.flame.remoting.exchange.ExchangeInvoker;
import com.rain.flame.remoting.exchange.ExchangeInvokerFactory;
import com.rain.flame.remoting.exchange.server.InterceptingExchangeInvokerFactory;
import com.rain.flame.remoting.exchange.server.SimpleExchangeInvokerFactory;

import java.util.ArrayList;
import java.util.List;

public class ExchangeInvokerUtils {
    private List<ExchangeInterceptor> exchangeInterceptors = new ArrayList<>();
    private volatile ExchangeInvokerFactory interceptingExchangeInvokerFactory;

    public ExchangeInvokerFactory getInvokerFactory() {
        ExchangeInvokerFactory requestFactory = interceptingExchangeInvokerFactory;
        if (requestFactory == null) {
            requestFactory = doGetExchangeInvokerFactory();
            interceptingExchangeInvokerFactory = requestFactory;
        }
        return interceptingExchangeInvokerFactory;
    }

    private ExchangeInvokerFactory doGetExchangeInvokerFactory() {
        return new InterceptingExchangeInvokerFactory(getDefaultExchangeInvokerFactory(), exchangeInterceptors);
    }

    private ExchangeInvokerFactory getDefaultExchangeInvokerFactory() {
        return new SimpleExchangeInvokerFactory();
    }
}
