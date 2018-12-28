package com.rain.flame.example.server.service;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

public abstract class AbstractService implements WebServerFactoryCustomizer {
    protected int port;
    @Override
    public void customize(WebServerFactory factory) {
        if (factory instanceof TomcatServletWebServerFactory) {
            factory = (TomcatServletWebServerFactory) factory;
            port = ((TomcatServletWebServerFactory) factory).getPort();
        }
    }
}
