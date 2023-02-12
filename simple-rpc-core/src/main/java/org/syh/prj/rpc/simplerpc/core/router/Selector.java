package org.syh.prj.rpc.simplerpc.core.router;

import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFutureWrapper;

import java.util.List;

public class Selector {
    public static class SelectorBuilder {
        private Selector selector;
        public SelectorBuilder() {
            selector = new Selector();
        }

        public SelectorBuilder setProviderServiceName(String providerServiceName) {
            selector.setProviderServiceName(providerServiceName);
            return this;
        }

        public SelectorBuilder setChannelFutureWrappers(List<ChannelFutureWrapper> channelFutureWrappers) {
            selector.setChannelFutureWrappers(channelFutureWrappers);
            return this;
        }

        public Selector build() {
            return selector;
        }
    }

    private Selector() {}

    private String providerServiceName;
    private List<ChannelFutureWrapper> channelFutureWrappers;

    public String getProviderServiceName() {
        return providerServiceName;
    }

    public void setProviderServiceName(String providerServiceName) {
        this.providerServiceName = providerServiceName;
    }

    public List<ChannelFutureWrapper> getChannelFutureWrappers() {
        return channelFutureWrappers;
    }

    public void setChannelFutureWrappers(List<ChannelFutureWrapper> channelFutureWrappers) {
        this.channelFutureWrappers = channelFutureWrappers;
    }
}
