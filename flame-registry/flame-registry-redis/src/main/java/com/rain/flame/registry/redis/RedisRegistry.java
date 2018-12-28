package com.rain.flame.registry.redis;

import com.rain.flame.Request;
import com.rain.flame.common.Constants;
import com.rain.flame.common.URL;
import com.rain.flame.registry.api.AbstractRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RedisRegistry extends AbstractRegistry {
    ExecutorService executorService = Executors.newCachedThreadPool();
    private ApplicationContext applicationContext;
    private RedisTemplate redisTemplate;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void deferExpired(URL url) {
        doRegister(url);
        doPublish(url);
    }

    protected void doPublish(URL url) {
        String key = getKey(url);
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.publish(key.getBytes(), Constants.REGISTER.getBytes());
                return null;
            }
        });
    }

    public void doSubscribe(URL url, Request request) {
        String key = getSubscribeKey(url);
        executorService.execute(() -> {
            redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.pSubscribe((message, pattern) -> {
                        if (message.toString().equals(Constants.REGISTER)
                                || message.equals(Constants.UNREGISTER)) {
                            redisTemplate.execute(new RedisCallback() {
                                @Override
                                public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                                    redisConnection.isClosed();
                                    try {
                                        Set<byte[]> sets = redisConnection.keys(key.getBytes());
                                        byte[][] b = new byte[sets.size()][];
                                        int i = 0;
                                        for (Iterator<byte[]> iterator = sets.iterator(); iterator.hasNext(); ) {
                                            byte bb[] = iterator.next();
                                            b[i++] = bb;
                                        }
                                        List<byte[]> r = redisConnection.mGet(b);
                                        List<URL> urls = new ArrayList<>();
                                        for (byte[] bbb : r) {
                                            urls.add(URL.valueOf(new String(bbb)));
                                        }
                                        request.refresh(urls);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        redisConnection.close();
                                    }
                                    return null;
                                }
                            });
                        }
                    }, key.getBytes());
                    return null;
                }
            });
        });
    }

    @Override
    protected void doRegister(URL url) {
        String key = getKey(url);
        String value = getValue(url);
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.set(key.getBytes(), value.getBytes(), Expiration.from(30, TimeUnit.SECONDS), RedisStringCommands.SetOption.SET_IF_ABSENT);
                return null;
            }
        });
    }

    private String getSubscribeKey(URL url) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(Constants.URL_SPLIT).append(url.getProtocol()).append(Constants.URL_SPLIT).append(url.getPath()).append(Constants.URL_SPLIT).append("providers").append("*");
        return buffer.toString();
    }

    private String getKey(URL url) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(Constants.URL_SPLIT).append(url.getProtocol()).append(Constants.URL_SPLIT).append(url.getPath())
                .append(Constants.URL_SPLIT).append("providers").append(Constants.URL_SPLIT)
                .append(url.getIp()).append(Constants.SEPARATOR).append(url.getPort());
        return buffer.toString();
    }

    private String getValue(URL url) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(url.getProtocol()).append(Constants.SEPARATOR).append(Constants.URL_SPLIT).append(Constants.URL_SPLIT)
                .append(url.getHost()).append(Constants.SEPARATOR).append(url.getPort()).append(Constants.URL_SPLIT)
                .append(url.getParameter("interface")).append(Constants.URL_QUESTION_MASK).append("interface=")
                .append(url.getParameter("interface")).append(Constants.URL_PARAMSPLITER).append("method=")
                .append(url.getParameter("method")).append(Constants.URL_PARAMSPLITER).append("server=")
                .append(url.getParameter("server")).append(Constants.URL_PARAMSPLITER).append("side=")
                .append(url.getParameter("side"));
        return buffer.toString();
    }
}
