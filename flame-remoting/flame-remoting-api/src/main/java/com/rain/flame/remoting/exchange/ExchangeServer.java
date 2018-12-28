package com.rain.flame.remoting.exchange;


import com.rain.flame.common.URL;
import com.rain.flame.remoting.Server;

public interface ExchangeServer extends Server {
    void reset(URL url);

}