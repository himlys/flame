package com.rain.flame;

public interface Invocation {
    public Class<?>[] getParameterTypes();

    public Object[] getArguments();

    public long getId();

    public void setId(long id);
}
