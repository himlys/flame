package com.rain.flame;

public interface Invoker {
    Object invoke(Object message);
    Channel getChannel();
}
