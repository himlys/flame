package com.rain.flame.rpc.protocol;

import com.rain.flame.common.URL;
import com.rain.flame.common.utils.ApplicationContextHelper;
import com.rain.flame.remoting.ChannelHandler;
import com.rain.flame.remoting.Client;
import com.rain.flame.rpc.Protocol;
import com.rain.flame.rpc.client.RpcRequestFactory;
import com.rain.flame.rpc.client.RpcRequestInterceptor;
import com.rain.flame.rpc.client.intercepting.InterceptingRpcRequest;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class FlameRequest<T> extends InterceptingRpcRequest {

    public FlameRequest(RpcRequestFactory requestFactory, List<RpcRequestInterceptor> rpcRequestInterceptors, URL url, ChannelHandler handler, Protocol protocol) {
        super(requestFactory, rpcRequestInterceptors, url,handler,protocol);
    }
    protected Client initClient(URL url, ChannelHandler handler) {
        try {
            String server = url.getParameter("server");
            String serverClass = ApplicationContextHelper.getApplicationContext().getEnvironment().getProperty("flame.client." + server);
            Class classz = ClassUtils.forName(serverClass, ClassUtils.getDefaultClassLoader());
            Constructor c = classz.getDeclaredConstructor(new Class[]{URL.class, ChannelHandler.class});
            Client client = (Client) c.newInstance(url, handler);
            return client;
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
