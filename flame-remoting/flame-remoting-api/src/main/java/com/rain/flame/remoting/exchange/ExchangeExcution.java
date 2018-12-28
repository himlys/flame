package com.rain.flame.remoting.exchange;

import com.rain.flame.Invoker;

public interface ExchangeExcution {
    ExchangeInvocationResult execute(ExchangeInvoker exchangeInvoker, Invoker invoker, Object body);
}
