package com.rain.flame.rpc.protocol;

import com.rain.flame.Channel;
import com.rain.flame.common.URL;
import com.rain.flame.common.utils.Bytes;
import com.rain.flame.common.utils.ReflectUtils;
import com.rain.flame.common.utils.SystemBeanUtils;
import com.rain.flame.remoting.exchange.ExchangeCodec;
import com.rain.flame.remoting.exchange.ExchangeInvocation;
import com.rain.flame.remoting.exchange.ExchangeInvocationResult;
import com.rain.flame.rpc.RpcInvocation;
import com.rain.flame.serialization.ObjectInput;
import com.rain.flame.serialization.ObjectOutput;
import com.rain.flame.serialization.Serialization;

import java.io.IOException;
import java.io.InputStream;

public class FlameCodec extends ExchangeCodec {
    protected void encodeRequestData(Channel channel, ObjectOutput out, Object data) throws IOException {
        encodeRequestData(channel, out, data, "1.0.0");
    }

    protected void encodeRequestData(Channel channel, ObjectOutput out, Object data, String version) throws IOException {
        RpcInvocation inv = (RpcInvocation) data;

        out.writeUTF(version);
        out.writeUTF(inv.getUrl().toString());
        out.writeUTF(inv.getMethodName());
        out.writeUTF(ReflectUtils.getDesc(inv.getParameterTypes()));
        Object[] args = inv.getArguments();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                out.writeObject(args[i]);
            }
        }
    }

    protected Object decodeBody(Channel channel, InputStream is, byte[] header) throws IOException {
        byte messageType = header[1];
        byte requestType = header[2];
        long id = Bytes.bytes2long(header, 5);
        if (messageType == 1) {
            byte status = header[3];
            ExchangeInvocationResult res = new ExchangeInvocationResult();
            res.setId(id);
            res.setResponseCode(res.getResponseCodeByByte(status));
            if (status == ExchangeInvocationResult.ResponseCode.OK.getValue()) {
                try {
                    Object result = null;
                    switch (requestType) {
                        case 0: {
                            ObjectInput objectInput = deserialize(SystemBeanUtils.get(Serialization.class), channel.getUrl(), is);
                            result = objectInput.readObject();
                            break;
                        }
                        case 1: {
                            result = deserialize(SystemBeanUtils.get(Serialization.class), channel.getUrl(), is).readObject(String.class);
                            break;
                        }
                    }
                    res.setData(result);
                } catch (Throwable t) {
                }
                return res;
            }
        } else {
            ExchangeInvocation req = new ExchangeInvocation();
            req.setId(id);
            try {
                Object result = null;
                switch (requestType) {
                    case 0: {
                        DecodeableRpcInvocation inv;
                        inv = new DecodeableRpcInvocation(channel, is);
                        inv.decode();
                        inv.setId(id);

                        result = inv;
                        break;
                    }
                    case 1: {
                        result = deserialize(SystemBeanUtils.get(Serialization.class), channel.getUrl(), is).readObject(String.class);
                        break;
                    }
                }
                req.setData(result);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return req;
        }
        return null;
    }

    private ObjectInput deserialize(Serialization serialization, URL url, InputStream is)
            throws IOException {
        return serialization.deserialize(url, is);
    }
}
