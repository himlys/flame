package com.rain.flame.rpc;

import com.rain.flame.common.Constants;
import com.rain.flame.common.URL;
import com.rain.flame.common.utils.ApplicationContextHelper;
import com.rain.flame.remoting.ChannelHandler;
import com.rain.flame.remoting.Client;
import com.rain.flame.remoting.Server;
import com.rain.flame.remoting.exchange.ExchangeClient;
import com.rain.flame.remoting.exchange.ExchangeServer;
import com.rain.flame.remoting.exchange.server.DefaultExchangeClient;
import com.rain.flame.rpc.client.RpcRequest;
import com.rain.flame.rpc.client.RpcRequestFactory;
import com.rain.flame.rpc.client.RpcRequestInterceptor;
import com.rain.flame.rpc.client.intercepting.InterceptingRpcRequestFactory;
import com.rain.flame.rpc.client.simple.SimpleRpcRequestFactory;
import com.rain.flame.rpc.server.RpcInvokeInterceptor;
import com.rain.flame.rpc.server.RpcInvokerFactory;
import com.rain.flame.rpc.server.intercepting.InterceptingRpcInvokerFactory;
import com.rain.flame.rpc.server.simple.SimpleRpcInvokerFactory;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractProtocol implements Protocol {
    private final Map<String, ExchangeServer> serverMap = new ConcurrentHashMap<String, ExchangeServer>();
    private final Lock connectLock = new ReentrantLock();

    @Override
    public Map<String, Client> getClients() {
        return clients;
    }

    private final List<RpcRequestInterceptor> rpcRequestInterceptors;
    private final List<RpcInvokeInterceptor> rpcInvokeInterceptors;
    private final int tick = 16;
    private Map<URL, RpcRequest> requests = new HashMap<>();
    HashedWheelTimer heartbeatTimer = new HashedWheelTimer(3, TimeUnit.SECONDS, 16);
    private TimerTask heartTimerTask = (timeout) -> {
        doTask();
        if (timeout == null) {
            throw new IllegalArgumentException();
        }

        Timer timer = timeout.timer();
        if (timeout.isCancelled()) {
            return;
        }

        timer.newTimeout(timeout.task(), tick, TimeUnit.MILLISECONDS);
    };

    private void doTask() {
        clients.values().stream().filter(client ->
                !client.isActive()
        ).forEach(client -> {
            inactiveClient(new DefaultExchangeClient(client.getUrl(), client));
            inActiveClients.put(getClientKey(client.getUrl()), client);
            clients.remove(getClientKey(client.getUrl()));
        });
        inActiveClients.values().stream().forEach(client -> {
            if (client.getReconnectCount().get() >= Constants.RECONNECT_COUNT) {
                inActiveClients.remove(getClientKey(client.getUrl()));
            }
        });
        inActiveClients.values().stream().filter(client -> {
                    client.reconnect();
                    return client.isActive();
                }
        ).forEach(client -> {
            activeClient(client);
            clients.put(getClientKey(client.getUrl()), client);
            inActiveClients.remove(getClientKey(client.getUrl()));
        });
    }

    public String getClientKey(URL url) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(Constants.URL_SPLIT).append(url.getProtocol()).append(Constants.URL_SPLIT)
                .append(url.getHost()).append(Constants.URL_SPLIT).append(url.getPort());
        return buffer.toString();
    }

    public boolean activeClient(Client client) {
        connectLock.lock();
        try {
            URL url = client.getUrl();
            getRequests().values().stream().filter(rpcRequest ->
                    URL.isEqualHostPort(rpcRequest.getUrl(), client.getUrl())
            ).forEach(rpcRequest -> {
                rpcRequest.refreshClients(new DefaultExchangeClient(client.getUrl(), client), true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            connectLock.unlock();
        }
        return true;
    }

    public boolean inactiveClient(ExchangeClient client) {
        connectLock.lock();
        try {
            URL url = client.getUrl();
            String key = getClientKey(url);
            clients.remove(key);
            inActiveClients.put(key, client.getClient());
            getRequests().values().stream().filter(rpcRequest ->
                    rpcRequest.getClients().contains(client)
            ).forEach(rpcRequest -> {
                rpcRequest.refreshClients(client, false);
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            connectLock.unlock();
        }
        return true;
    }

    public Map<String, Client> getInActiveClients() {
        return inActiveClients;
    }

    public void setInActiveClients(Map<String, Client> inActiveClients) {
        this.inActiveClients = inActiveClients;
    }

    protected volatile Map<String, Client> inActiveClients = new ConcurrentHashMap<>();
    protected volatile Map<String, Client> clients = new ConcurrentHashMap<>();
    private volatile RpcRequestFactory interceptingRequestFactory;
    private volatile RpcInvokerFactory interceptingInvokerFactory;

    public void refreshRequest(RpcRequest request, List<URL> urls) {
        connectLock.lock();
        try {
            request.refreshClients(urls);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectLock.unlock();
        }
    }

    public Map<URL, RpcRequest> getRequests() {
        return requests;
    }

    protected AbstractProtocol(List<RpcRequestInterceptor> requestInterceptors, List<RpcInvokeInterceptor> invokeInterceptors) {
        this.rpcRequestInterceptors = requestInterceptors;
        this.rpcInvokeInterceptors = invokeInterceptors;
        heartbeatTimer.newTimeout(heartTimerTask, Constants.HEART_BEAT_TIMER, TimeUnit.SECONDS);
    }

    public RpcRequestFactory getRequestFactory() {
        RpcRequestFactory requestFactory = interceptingRequestFactory;
        if (requestFactory == null) {
            requestFactory = doGetRequestFactory();
            interceptingRequestFactory = requestFactory;
        }
        return interceptingRequestFactory;
    }

    protected RpcRequestFactory doGetRequestFactory() {
        return new InterceptingRpcRequestFactory(getDefaultRequestFactory(), rpcRequestInterceptors);
    }

    public RpcRequestFactory getDefaultRequestFactory() {
        return new SimpleRpcRequestFactory();
    }

    public Map<String, ExchangeServer> getServerMap() {
        return serverMap;
    }

    public List<RpcRequestInterceptor> getRpcRequestInterceptors() {
        return rpcRequestInterceptors;
    }

    public List<RpcInvokeInterceptor> getRpcInvokeInterceptors() {
        return rpcInvokeInterceptors;
    }

    public RpcRequestFactory getInterceptingRequestFactory() {
        return interceptingRequestFactory;
    }

    public RpcInvokerFactory getInterceptingInvokerFactory() {
        return interceptingInvokerFactory;
    }

    protected RpcInvokerFactory getInvokerFactory() {
        RpcInvokerFactory invokerFactory = interceptingInvokerFactory;
        if (invokerFactory == null) {
            invokerFactory = doGetInvokerFactory();
            interceptingInvokerFactory = invokerFactory;
        }
        return interceptingInvokerFactory;
    }

    protected RpcInvokerFactory doGetInvokerFactory() {
        return new InterceptingRpcInvokerFactory(getDefaultInvokerFactory(), rpcInvokeInterceptors);
    }

    public RpcInvokerFactory getDefaultInvokerFactory() {
        return new SimpleRpcInvokerFactory();
    }


    public void export(URL url) {
        openServer(url);
    }

    protected void openServer(URL url) {
        String key = url.getAddress();
        ExchangeServer server = serverMap.get(key);
        if (server == null) {
            synchronized (this) {
                server = serverMap.get(key);
                if (server == null) {
                    serverMap.put(key, createServer(url));
                }
            }
        } else {
            // server supports reset, use together with override
            server.reset(url);
        }
    }

    protected ExchangeServer createServer(URL url) {
        ExchangeServer server = doCreateServer(url);
        return server;
    }

    protected abstract ExchangeServer doCreateServer(URL url);

    protected Server initServer(URL url, ChannelHandler handler) {
        try {
            String serverName = url.getParameter("server");
            String serverClass = ApplicationContextHelper.getApplicationContext().getEnvironment().getProperty("flame.server." + serverName);
            Class classz = ClassUtils.forName(serverClass, ClassUtils.getDefaultClassLoader());
            Constructor c = classz.getDeclaredConstructor(new Class[]{ChannelHandler.class, URL.class});
            Server server = (Server) c.newInstance(handler, url);
            return server;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
