package org.syh.prj.rpc.simplerpc.core.common.event.data;

import java.util.List;

public class URLChangeWrapper {
    private String serviceName;

    private List<String> providerUrls;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<String> getProviderUrls() {
        return providerUrls;
    }

    public void setProviderUrl(List<String> providerUrls) {
        this.providerUrls = providerUrls;
    }

    @Override
    public String toString() {
        return
                "URLChangeWrapper{" +
                "serviceName='" + serviceName + '\'' +
                ", providerUrls=" + providerUrls +
                "}";
    }
}
