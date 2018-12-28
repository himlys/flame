package com.rain.flame.config.spring;

import com.rain.flame.common.Constants;
import com.rain.flame.common.URL;
import org.springframework.core.env.Environment;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

public abstract class AbstractConfig implements Serializable {
    private static final long serialVersionUID = 2168833836149892891L;
    private Environment environment;
    protected List<ProtocolConfig> protocols;
    protected List<RegistryConfig> registries;
    protected String id;
    protected Class interfaceClass;
    protected String interfaceName;

    public String getInterfaceName() {
        return interfaceName;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    protected URL getURL(ProtocolConfig config) {
        URL url = new URL(config.getName(), config.getHost(), config.getPort(), getPath());
        Map<String, String> map = new HashMap();
        map.put("interface", interfaceClass.getName());
        map.put("method", getMethod());
        map.put("server", config.getServer());
        map.put("side", "provider");
        url = url.addParameter(map);
        return url;
    }

    protected String getMethod() {
        Set<String> set = new HashSet<>();
        StringBuffer buffer = new StringBuffer();
        for (Method method : interfaceClass.getMethods()) {
            set.add(method.getName());
        }
        String[] methods = new String[set.size()];
        set.toArray(methods);
        for (int i = 0; i < methods.length - 1; i++) {
            buffer.append(methods[i]).append(",");
        }
        buffer.append(methods[methods.length - 1]);
        return buffer.toString();
    }

    protected String getPath() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(interfaceClass.getName());
        return buffer.toString();
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public List<ProtocolConfig> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<ProtocolConfig> protocols) {
        this.protocols = protocols;
    }

    public List<RegistryConfig> getRegistries() {
        return registries;
    }

    public void setRegistries(List<RegistryConfig> registries) {
        this.registries = registries;
    }
}
