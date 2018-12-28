package com.rain.flame.rpc.server;

import com.rain.flame.InvokeResult;

public class RpcInvokeResult implements InvokeResult {
    private Object data;
    private long id;

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
