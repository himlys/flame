/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.rain.flame.remoting.servers.netty;

import com.rain.flame.common.URL;
import com.rain.flame.remoting.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private NettyClientHandler CLIENT_HANDLER;

    private final SslContext sslCtx;
    private final URL url;
    private NettyCodecAdapter adapter;
    private final ChannelHandler handler;

    public NettyClientInitializer(SslContext sslCtx, URL url, ChannelHandler handler) {
        this.sslCtx = sslCtx;
        this.url = url;
        this.handler = handler;
        CLIENT_HANDLER = new NettyClientHandler(handler, url);
    }

    @Override
    public void initChannel(SocketChannel ch) {
        this.adapter = new NettyCodecAdapter(handler, url);
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
//            pipeline.addLast(sslCtx.newHandler(ch.alloc(), TelnetClient.HOST, TelnetClient.PORT));
        }

        // Add the text line codec combination first,
//        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(adapter.getDecoder());
        pipeline.addLast(adapter.getEncoder());

        // and then business logic.
        pipeline.addLast(CLIENT_HANDLER);
    }
}

