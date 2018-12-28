package com.rain.flame.rpc;

import com.rain.flame.Invocation;
import com.rain.flame.common.URL;

public class RpcInvocation implements Invocation {
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    private URL url;
    private Class<?>[] parameterTypes;
    private Object[] arguments;
    private long id;
    private String methodName;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
}
