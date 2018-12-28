package com.rain.flame.config.spring;

import java.io.Serializable;

public class ProtocolConfig implements Serializable {
    private static final long serialVersionUID = -9004747384796668026L;
    private String host;
    private Integer port;
    private String server;
    private String client;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}
