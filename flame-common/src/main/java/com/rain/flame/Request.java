package com.rain.flame;

import com.rain.flame.common.URL;

import java.util.List;

public interface Request {
    Response execute(Invocation invocation);

    long getId();

    void refresh(List<URL> urls);

}
