package com.rain.flame.remoting.exchange.server;

import com.rain.flame.Invoker;
import com.rain.flame.common.URL;
import com.rain.flame.remoting.exchange.AbstractExchangeInvoker;
import com.rain.flame.remoting.exchange.ExchangeInvocationResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleExchangeInvoker extends AbstractExchangeInvoker {
    ExecutorService executorService = Executors.newCachedThreadPool();

    public SimpleExchangeInvoker(URL url) {
        super(url);
    }

    @Override
    protected ExchangeInvocationResult executeInternal(Invoker invoker, Object obj) {

        executorService.execute(new InvokeRunnable(invoker, InvokeRunnable.ChannelState.RECEIVED, obj));
        return null;
    }

}
