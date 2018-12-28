package com.rain.flame.registry.api;

import com.rain.flame.Request;
import com.rain.flame.common.Constants;
import com.rain.flame.common.URL;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public abstract class AbstractRegistry implements Registry {
    private final Map<URL, Object> registered = new ConcurrentHashMap();
    private final ScheduledExecutorService expireExecutor = Executors.newScheduledThreadPool(1);
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private ScheduledFuture<?> expireFuture;
    private int expirePeriod = Constants.DEFAULT_REGISTRY_EXPIRED;

    @Override
    public void unregister(URL url) {

    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public void register(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("register url == null");
        }
        doRegister(url);
        registered.put(url, new Object());
        this.expireFuture = expireExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    deferExpired(url);
                } catch (Throwable t) {
                }
            }
        }, expirePeriod / 2, expirePeriod / 2, TimeUnit.SECONDS);
    }

    protected abstract void deferExpired(URL url);

    protected abstract void doRegister(URL url);

    @Override
    public void subscribe(URL url, Request request) {
        doSubscribe(url,request);
    }

    protected abstract void doSubscribe(URL url, Request request);

    @Override
    public void unsubscribe(URL url) {

    }
}
