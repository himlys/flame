package com.rain.flame.common;

public final class Constants {
    public static final int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);
    public static final String DEFAULT_KEY_PREFIX = "default";
    public static final String COMMA = ".";
    public static final String VERSION_KEY = "version";
    public static final int DEFAULT_TIMEOUT = 1000;
    public static final String DEFAULT_PROTOCOL_HOST = "127.0.0.1";
    public static final int DEFAULT_PROTOCOL_PORT = 10820;
    public static final String DEFAULT_PROTOCOL_SERVER = "netty";
    public static final String SEPARATOR = ":";
    public static final String URL_SPLIT = "/";
    public static final String URL_PARAMSPLITER = "&";
    public static final String URL_QUESTION_MASK = "?";
    public static final String SERVICE_BEAN_NAME = "com.rain.flame.config.spring.ServiceBean";
    public static final String SIDE_KEY = "side";
    public static final String CONSUMER_SIDE = "consumer";
    public static final String PROVIDER_SIDE = "provider";
    public static final String INTERFACE_KEY = "interface";
    public static final String GROUP_KEY = "group";
    //    默认为秒
    public static final Integer DEFAULT_REGISTRY_EXPIRED = 30;
    public static final String REGISTER = "register";
    public static final String UNREGISTER = "unregister";
    public static final int TICKS_PER_WHEEL = 16;
    public static final int HEART_BEAT_TIMER = 30;
    public static final int RECONNECT_COUNT = 3;
}
