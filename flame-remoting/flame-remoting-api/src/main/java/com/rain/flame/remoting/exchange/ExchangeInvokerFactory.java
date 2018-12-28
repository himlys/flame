package com.rain.flame.remoting.exchange;

import com.rain.flame.common.URL;

public interface ExchangeInvokerFactory {
    ExchangeInvoker createExchangeInvoker(URL url);

}
