package com.rain.flame.remoting.exchange;

import com.rain.flame.Channel;
import com.rain.flame.common.URL;
import com.rain.flame.common.utils.Bytes;
import com.rain.flame.common.utils.SystemBeanUtils;
import com.rain.flame.remoting.buffer.ChannelBuffer;
import com.rain.flame.remoting.buffer.ChannelBufferInputStream;
import com.rain.flame.remoting.buffer.ChannelBufferOutputStream;
import com.rain.flame.remoting.codec.AbstractCodec;
import com.rain.flame.serialization.Cleanable;
import com.rain.flame.serialization.ObjectInput;
import com.rain.flame.serialization.ObjectOutput;
import com.rain.flame.serialization.Serialization;

import java.io.IOException;
import java.io.InputStream;

public class ExchangeCodec extends AbstractCodec {
    protected static final int HEADER_LENGTH = 16;
    //    0=operateType 0=connected 1=read
//    1=ObjectType 0=request 1=response
//    2=requestType 0=flame
//    3=responseType 0=正常 1=exception
//    4=id
    protected static final byte FLAG_REQUEST = (byte) 0x80;

    @Override
    public void encode(Channel channel, ChannelBuffer buffer, Object message) throws IOException {
        if (message instanceof ExchangeInvocation) {
            encodeRequest(channel, buffer, (ExchangeInvocation) message);
        } else if (message instanceof ExchangeInvocationResult) {
            encodeResponse(channel, buffer, (ExchangeInvocationResult) message);
        } else {
            super.encode(channel, buffer, message);
        }
    }

    @Override
    public Object decode(Channel channel, ChannelBuffer buffer) throws IOException {
        int readable = buffer.readableBytes();
        byte[] header = new byte[Math.min(readable, HEADER_LENGTH)];
        buffer.readBytes(header);
        return decode(channel, buffer, readable, header);
    }

    protected Object decode(Channel channel, ChannelBuffer buffer, int readable, byte[] header) throws IOException {
        ChannelBufferInputStream is = new ChannelBufferInputStream(buffer, readable - HEADER_LENGTH);
        return decodeBody(channel, is, header);
    }

    protected Object decodeBody(Channel channel, InputStream is, byte[] header) throws IOException {
        return null;
    }

    public Serialization getSerialization(URL url) {
//        这里加入判断可以传配置在url里的Serialization类型
        return SystemBeanUtils.get(Serialization.class);
    }

    protected void encodeRequest(Channel channel, ChannelBuffer buffer, ExchangeInvocation req) throws IOException {
        byte[] header = new byte[HEADER_LENGTH];
        header[0] = (byte) 1;
        header[1] = (byte) 0;
        header[2] = (byte) 0;
        header[3] = (byte) 0;
        Bytes.long2bytes(req.getId(), header, 5);
        buffer.writeBytes(header);
//        暂时用不上，占位用了。
        ChannelBufferOutputStream bos = new ChannelBufferOutputStream(buffer);
        ObjectOutput out = getSerialization(channel.getUrl()).serialize(channel.getUrl(), bos);
        encodeRequestData(channel, out, req.getData());
        out.flushBuffer();
        if (out instanceof Cleanable) {
            ((Cleanable) out).cleanup();
        }
        bos.flush();
        bos.close();
        bos.flush();
        bos.close();
    }

    protected void encodeRequestData(Channel channel, ObjectOutput out, Object data) throws IOException {
        encodeRequestData(out, data);
    }

    protected void encodeRequestData(ObjectOutput out, Object data) throws IOException {
        out.writeObject(data);
    }

    protected void encodeResponse(Channel channel, ChannelBuffer buffer, ExchangeInvocationResult res) throws IOException {
        byte[] header = new byte[HEADER_LENGTH];
        ExchangeInvocationResult.ResponseCode status = res.getResponseCode();
        header[0] = "connected".equals(res.getResponseType()) ? (byte) 0 : (byte) 1;
        header[1] = (byte) 1;
        header[2] = (byte) 0;
        header[3] = (byte) 0;
        // set request id.
        Bytes.long2bytes(res.getId(), header, 5);
        buffer.writeBytes(header);
        ChannelBufferOutputStream bos = new ChannelBufferOutputStream(buffer);
        ObjectOutput out = getSerialization(channel.getUrl()).serialize(channel.getUrl(), bos);
        if (ExchangeInvocationResult.ResponseCode.OK == status) {
            if ("heartBeat".equals(res.getResponseType())) {
                encodeHeartbeatData(channel, out, res.getData());
            } else {
                encodeResponseData(channel, out, res.getData());
            }
        }
        out.flushBuffer();
        if (out instanceof Cleanable) {
            ((Cleanable) out).cleanup();
        }
        bos.flush();
        bos.close();
    }

    protected void encodeHeartbeatData(Channel channel, ObjectOutput out, Object data) throws IOException {

    }

    protected void encodeResponseData(Channel channel, ObjectOutput out, Object data) throws IOException {
        encodeResponseData(out, data);
    }

    protected void encodeResponseData(ObjectOutput out, Object data) throws IOException {
        out.writeObject(data);
    }

    protected Object decodeHeartbeatData(Channel channel, ObjectInput in) throws IOException {
        try {
            return in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    protected Object getRequestData(long id) {
        return null;
    }
}
