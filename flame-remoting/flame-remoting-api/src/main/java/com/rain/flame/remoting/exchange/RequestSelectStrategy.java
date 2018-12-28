package com.rain.flame.remoting.exchange;

import com.rain.flame.Request;

import java.util.Collection;
import java.util.List;

public interface RequestSelectStrategy {
    public Request select(List<Request> requests);
}
