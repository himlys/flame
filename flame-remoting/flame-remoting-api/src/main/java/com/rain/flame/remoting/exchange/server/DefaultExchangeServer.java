package com.rain.flame.remoting.exchange.server;

import com.rain.flame.common.URL;
import com.rain.flame.remoting.Server;
import com.rain.flame.remoting.exchange.ExchangeServer;

public class DefaultExchangeServer implements ExchangeServer {
    private final Server server;

    public DefaultExchangeServer(Server server) {
        this.server = server;

    }

    @Override
    public void reset(URL url) {

    }
}