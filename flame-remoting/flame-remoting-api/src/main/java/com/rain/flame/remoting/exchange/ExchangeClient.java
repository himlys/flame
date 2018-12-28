package com.rain.flame.remoting.exchange;

import com.rain.flame.Request;
import com.rain.flame.common.URL;
import com.rain.flame.remoting.Client;

public interface ExchangeClient {
    ExchangeInvocationResult send(Request request, Object param);

    URL getUrl();

    boolean isActive();

    Client getClient();
}
