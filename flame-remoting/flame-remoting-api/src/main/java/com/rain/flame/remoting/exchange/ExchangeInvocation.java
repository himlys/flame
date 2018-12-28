package com.rain.flame.remoting.exchange;

public class ExchangeInvocation {
    private String requestType;
    private Object data;
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public ExchangeInvocation() {
    }

    public Long getId() {
        return id;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
