package com.rain.flame.remoting.exchange;

import com.rain.flame.Invoker;

public interface ExchangeInterceptor {
    ExchangeInvocationResult intercept(ExchangeInvoker exchangeInvoker, Invoker invoker, Object requestBody, ExchangeExcution execution);
}
