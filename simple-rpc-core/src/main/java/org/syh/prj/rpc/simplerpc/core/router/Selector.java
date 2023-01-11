package org.syh.prj.rpc.simplerpc.core.router;

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

        public Selector build() {
            return selector;
        }
    }

    private Selector() {}

    private String providerServiceName;

    public String getProviderServiceName() {
        return providerServiceName;
    }

    public void setProviderServiceName(String providerServiceName) {
        this.providerServiceName = providerServiceName;
    }
}
