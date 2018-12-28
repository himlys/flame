package com.rain.flame.remoting.exchange.server;

import com.rain.flame.common.URL;
import com.rain.flame.remoting.exchange.ExchangeInvoker;
import com.rain.flame.remoting.exchange.ExchangeInvokerFactory;

public class SimpleExchangeInvokerFactory implements ExchangeInvokerFactory {
    @Override
    public ExchangeInvoker createExchangeInvoker(URL url) {
        return new SimpleExchangeInvoker(url);
    }
}
