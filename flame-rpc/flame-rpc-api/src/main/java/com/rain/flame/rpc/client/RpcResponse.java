package com.rain.flame.rpc.client;

import com.rain.flame.rpc.ResponseFuture;

public interface RpcResponse extends ResponseFuture {
    long getId();
    boolean isDone();
}