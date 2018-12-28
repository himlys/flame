package com.rain.flame;

public interface Response {
    Object get();

    Object get(int timeoutInMillis);
}
