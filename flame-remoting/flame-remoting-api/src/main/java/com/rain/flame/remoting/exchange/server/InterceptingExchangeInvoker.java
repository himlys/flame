package com.rain.flame.remoting.exchange.server;

import com.rain.flame.Invoker;
import com.rain.flame.common.URL;
import com.rain.flame.remoting.exchange.*;

import java.util.Iterator;
import java.util.List;

public class InterceptingExchangeInvoker extends AbstractExchangeInvoker {
    private final ExchangeInvokerFactory exchangeInvokerFactory;

    private final List<ExchangeInterceptor> exchangeInterceptorList;

    private URL url;

    public InterceptingExchangeInvoker(ExchangeInvokerFactory exchangeInvokerFactory, List<ExchangeInterceptor> exchangeInterceptorList, URL url) {
        super(url);
        this.exchangeInvokerFactory = exchangeInvokerFactory;
        this.exchangeInterceptorList = exchangeInterceptorList;
        this.url = url;
    }

    @Override
    protected ExchangeInvocationResult executeInternal(Invoker invoker, Object requestBody) {
        InterceptingExchangeInvokeExecution execution = new InterceptingExchangeInvokeExecution();
        return execution.execute(this, invoker, requestBody);
    }

    private class InterceptingExchangeInvokeExecution implements ExchangeExcution {
        private final Iterator<ExchangeInterceptor> iterator;

        public InterceptingExchangeInvokeExecution() {
            this.iterator = exchangeInterceptorList.iterator();
        }

        @Override
        public ExchangeInvocationResult execute(ExchangeInvoker exchangeInvoker, Invoker invoker, Object requestBody) {
            if (this.iterator.hasNext()) {
                ExchangeInterceptor nextInterceptor = this.iterator.next();
                return nextInterceptor.intercept(exchangeInvoker, invoker, requestBody, this);
            } else {
                ExchangeInvoker delegate = exchangeInvokerFactory.createExchangeInvoker(getUrl());
                return delegate.execute(invoker, requestBody);
            }
        }
    }
}
