package com.rain.flame.remoting.exchange.interceptors.server;

import com.rain.flame.Invoker;
import com.rain.flame.remoting.exchange.ExchangeExcution;
import com.rain.flame.remoting.exchange.ExchangeInterceptor;
import com.rain.flame.remoting.exchange.ExchangeInvocationResult;
import com.rain.flame.remoting.exchange.ExchangeInvoker;

public class LoggingExchangeInterceptor implements ExchangeInterceptor {
    @Override
    public ExchangeInvocationResult intercept(ExchangeInvoker exchangeInvoker, Invoker invoker, Object requestBody, ExchangeExcution execution) {
//        System.out.println("LoggingExchangeInterceptor start");
        ExchangeInvocationResult result = execution.execute(exchangeInvoker, invoker, requestBody);
//        System.out.println("LoggingExchangeInterceptor end");
        return result;
    }
}
