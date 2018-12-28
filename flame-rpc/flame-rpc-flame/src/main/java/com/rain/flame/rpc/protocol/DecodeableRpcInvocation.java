package com.rain.flame.rpc.protocol;

import com.rain.flame.Channel;
import com.rain.flame.common.URL;
import com.rain.flame.common.utils.ReflectUtils;
import com.rain.flame.common.utils.SystemBeanUtils;
import com.rain.flame.rpc.RpcInvocation;
import com.rain.flame.serialization.ObjectInput;
import com.rain.flame.serialization.Serialization;

import java.io.IOException;
import java.io.InputStream;

public class DecodeableRpcInvocation extends RpcInvocation {
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];
    private final Channel channel;
    private final InputStream inputStream;

    public DecodeableRpcInvocation(Channel channel, InputStream inputStream) {
        this.channel = channel;
        this.inputStream = inputStream;
    }

    public Object decode() throws IOException {
        Serialization serialization = SystemBeanUtils.get(Serialization.class);
        ObjectInput objectInput = serialization.deserialize(channel.getUrl(), inputStream);
        String version = objectInput.readUTF();
        String url = objectInput.readUTF();
        setUrl(URL.valueOf(url));
        String method = objectInput.readUTF();
        setMethodName(method);
        String desc = objectInput.readUTF();
        Object[] args;
        Class<?>[] pts = null;
        if (desc == null || "".equals(desc)) {
            pts = EMPTY_CLASS_ARRAY;
            args = EMPTY_OBJECT_ARRAY;
        } else {
            try {
                pts = ReflectUtils.desc2classArray(desc);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            args = new Object[pts.length];
            for (int i = 0; i < args.length; i++) {
                try {
                    args[i] = objectInput.readObject(pts[i]);
                } catch (Exception e) {
                }
            }
        }
        setParameterTypes(pts);
//        for (int i = 0; i < args.length; i++) {
//            args[i] = decodeInvocationArgument(channel, this, pts, i, args[i]);
//        }
        setArguments(args);
        return this;
    }
}