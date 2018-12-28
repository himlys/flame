package com.rain.flame.remoting.exchange;

import com.rain.flame.Invocation;
import com.rain.flame.Response;

public interface ExchangeRequest {
    Response execute(Invocation invocation);
}
