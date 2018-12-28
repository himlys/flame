package com.rain.flame.remoting.exchange.server;

import com.rain.flame.Invoker;

public class InvokeRunnable implements Runnable{
    private final ChannelState state;
    private final Object message;
    private final Invoker invoker;
    public InvokeRunnable(Invoker invoker, ChannelState state,Object message) {
        this.state = state;
        this.invoker = invoker;
        this.message = message;

    }

    @Override
    public void run() {
        if (state == ChannelState.RECEIVED) {
            Object object = invoker.invoke(message);
            invoker.getChannel().send(object);
        } else {
            switch (state) {
                case CONNECTED:
                    break;
                case DISCONNECTED:
                    break;
                case SENT:
                    break;
                case CAUGHT:
                default:
            }
        }
    }
    public enum ChannelState {

        /**
         * CONNECTED
         */
        CONNECTED,

        /**
         * DISCONNECTED
         */
        DISCONNECTED,

        /**
         * SENT
         */
        SENT,

        /**
         * RECEIVED
         */
        RECEIVED,

        /**
         * CAUGHT
         */
        CAUGHT
    }
}
