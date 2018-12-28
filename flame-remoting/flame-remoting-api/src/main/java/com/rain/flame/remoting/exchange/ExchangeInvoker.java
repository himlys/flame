package com.rain.flame.remoting.exchange;

import com.rain.flame.Invoker;
import com.rain.flame.remoting.ChannelHandler;

public interface ExchangeInvoker {
    ExchangeInvocationResult execute(Invoker invoker, Object obj);
}
