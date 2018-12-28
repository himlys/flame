package com.rain.flame.rpc.client;

import com.rain.flame.Invocation;
import com.rain.flame.Response;
import com.rain.flame.common.URL;
import com.rain.flame.remoting.ChannelHandler;
import com.rain.flame.remoting.Client;
import com.rain.flame.remoting.exchange.ExchangeClient;
import com.rain.flame.remoting.exchange.server.DefaultExchangeClient;
import com.rain.flame.rpc.Protocol;
import com.rain.flame.rpc.RpcInvocation;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public abstract class AbstractRpcRequest implements RpcRequest {
    private List<ExchangeClient> clients = new CopyOnWriteArrayList<>();
    private final URL url;
    private long id;
    private final ChannelHandler handler;
    private RpcRequestSelectStrategy defaultRpcRequestSelectStrategy;
    private final Protocol protocol;

    public Protocol getProtocol() {
        return protocol;
    }

    public AbstractRpcRequest(URL url, ChannelHandler handler, Protocol protocol) {
        this.url = url;
        this.id = new Random().nextLong();
        this.handler = handler;
        this.protocol = protocol;
    }

    public void refresh(List<URL> urls) {
        protocol.refreshRequest(this, urls);
    }

    public boolean refreshClients(List<URL> urls) {
        List<URL> urlList = getUnAddedURL(urls);
        if (urlList.size() > 0) {
            for (int i = 0; i < urlList.size(); i++) {
                clients.add(new DefaultExchangeClient(urlList.get(i), getIfNotExist(urlList.get(i), handler)));
            }
            defaultRpcRequestSelectStrategy = new RoundRpcRequestSelectStrategy(clients);
            return true;
        }
        return false;
    }

    public boolean refreshClients(ExchangeClient client, boolean add) {
        List<ExchangeClient> newClients = null;
        if (add) {
            clients.add(client);
            newClients = clients;
        } else {
            newClients = clients.stream().filter(exchangeClient ->
                    client != exchangeClient
            ).collect(Collectors.toList());
        }
        setClients(newClients);
        defaultRpcRequestSelectStrategy = new RoundRpcRequestSelectStrategy(newClients);
        return true;
    }
    private Client getIfNotExist(URL url, ChannelHandler handler) {
        Map<String, Client> clients = protocol.getClients();
        if (clients.containsKey(protocol.getClientKey(url)))
            return clients.get(protocol.getClientKey(url));
        Client client = initClient(url, handler);
        protocol.getClients().put(protocol.getClientKey(url), client);
        return client;
    }

    private List<URL> getUnAddedURL(List<URL> urls) {
        List<URL> urlList = new ArrayList<>();
        for (URL url : urls) {
            if (!isAddedURL(url)) {
                urlList.add(url);
            }
        }
        return urlList;
    }

    private boolean isAddedURL(URL url) {
        for (ExchangeClient client : clients) {
            if (URL.isEqualURL(client.getUrl(), url)) return true;
        }
        return false;
    }

    public URL getUrl() {
        return url;
    }

    public long getId() {
        return id;
    }

    public ChannelHandler getHandler() {
        return handler;
    }

    @Override
    public List<ExchangeClient> getClients() {
        return clients;
    }

    public void setClients(List<ExchangeClient> clients) {
        defaultRpcRequestSelectStrategy = new RoundRpcRequestSelectStrategy(clients);
        this.clients = clients;
    }

    public RpcResponse execute(RpcInvocation rpcInvocation) {
        return executeInternal(rpcInvocation);

    }

    @Override
    public Response execute(Invocation invocation) {
        if (invocation instanceof RpcInvocation)
            return execute((RpcInvocation) invocation);
        return null;
    }

    public List<RpcRequestInterceptor> getRpcRequestInterceptors() {
        return new ArrayList<>();
    }

    protected abstract RpcResponse executeInternal(RpcInvocation rpcInvocation);

    protected ExchangeClient selectClient() {
        ExchangeClient client = defaultRpcRequestSelectStrategy.select();
        return client;
    }

    private static boolean isPowerOfTwo(int val) {
        return (val & -val) == val;
    }

    public static void main(String args[]) {
//        for (int i = 0; i < 100; i++) {
//            if (isPowerOfTwo(3))
//                System.out.println(i);
//        }
//        for(int i = 0; i < 100 ;i++){
//            int a = i & 3;
//            System.out.println(a);
//        }
    }

    protected abstract Client initClient(URL url, ChannelHandler handler);
}
