package com.rain.flame.rpc.client;

import com.rain.flame.Channel;
import com.rain.flame.common.Constants;
import com.rain.flame.rpc.server.RpcInvokeResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultRpcResponse implements RpcResponse {
    private final long id;
    private static final Map<Long, Channel> CHANNELS = new ConcurrentHashMap<>();
    private static final Map<Long, DefaultRpcResponse> FUTURES = new ConcurrentHashMap<>();
    private RpcRequest rpcRequest;
    private int timeout;
    private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();
    private final long start = System.currentTimeMillis();
    private volatile RpcInvokeResult rpcInvokeResult;

    public DefaultRpcResponse(RpcRequest rpcRequest, int timeout) {
        this.rpcRequest = rpcRequest;
        this.id = rpcRequest.getId();
        this.timeout = timeout > 0 ? timeout : Constants.DEFAULT_TIMEOUT;
        // put into waiting map.
        FUTURES.put(id, this);
    }

    @Override
    public long getId() {
        return id;
    }

    public static void received(RpcInvokeResult rpcInvokeResult) {

        try {
            DefaultRpcResponse future = FUTURES.remove(rpcInvokeResult.getId());
            if (future != null) {
                future.doReceived(rpcInvokeResult);
            } else {
            }
        } finally {
            CHANNELS.remove(rpcInvokeResult.getId());
        }
    }

    private void doReceived(RpcInvokeResult rpcInvokeResult) {
        lock.lock();
        try {
            this.rpcInvokeResult = rpcInvokeResult;
            if (done != null) {
                done.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object get() {
        return get(timeout);
    }

    @Override
    public boolean isDone() {
        return rpcInvokeResult != null;
    }

    @Override
    public Object get(int timeoutInMillis) {
        if (timeout <= 0) {
            timeout = Constants.DEFAULT_TIMEOUT;
        }
        if (!isDone()) {
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                while (!isDone()) {
                    done.await(timeout, TimeUnit.MILLISECONDS);
                    if (isDone() || System.currentTimeMillis() - start > timeout) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
            if (!isDone()) {
                throw new RuntimeException("timeout");
            }
        }
        return returnFromResponse();
    }

    private Object returnFromResponse() {
        RpcInvokeResult res = rpcInvokeResult;
        if (res == null) {
            throw new IllegalStateException("response cannot be null");
        }
        for(RpcRequestInterceptor interceptor : rpcRequest.getRpcRequestInterceptors()){
            res = interceptor.interceptAfter(rpcRequest,res);
        }
        return res.getData();
    }
}
