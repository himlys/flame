package com.rain.flame.remoting.exchange;

import com.rain.flame.Invoker;
import com.rain.flame.common.URL;

public abstract class AbstractExchangeInvoker implements ExchangeInvoker {
    private final URL url;

    public AbstractExchangeInvoker(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    public ExchangeInvocationResult execute(Invoker invoker, Object obj) {
        return executeInternal(invoker,obj);

    }

    protected abstract ExchangeInvocationResult executeInternal(Invoker invoker,Object requestBody);
}
