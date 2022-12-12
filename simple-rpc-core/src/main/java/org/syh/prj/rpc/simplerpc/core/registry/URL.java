package org.syh.prj.rpc.simplerpc.core.registry;

import org.syh.prj.rpc.simplerpc.core.registry.zookeeper.ProviderNodeInfo;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class URL {
    private String applicationName;

    private String serviceName; // e.g. com.site.services.UserService

    private Map<String, String> parameters = new HashMap<>();

    public void addParameter(String key, String value) {
        this.parameters.putIfAbsent(key, value);
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public static String buildProviderUrlStr(URL url) {
        String host = url.getParameters().get("host");
        String port = url.getParameters().get("port");
        return String.format(
            "%s;%s;%s:%s;%d",
            url.getApplicationName(), url.getServiceName(), host, port, System.currentTimeMillis()
        );
    }

    public static String buildConsumerUrlStr(URL url) {
        String host = url.getParameters().get("host");
        return String.format(
                "%s;%s;%s;%d",
                url.getApplicationName(),
                url.getServiceName(),
                host,
                System.currentTimeMillis()
        );
    }

    public static ProviderNodeInfo buildURLFromUrlStr(String providerNodeStr) {
        String[] items = providerNodeStr.replaceFirst("^/", "").split("/");
        ProviderNodeInfo providerNodeInfo = new ProviderNodeInfo();
        providerNodeInfo.setServiceName(items[1]);
        providerNodeInfo.setAddress(items[3]);
        return providerNodeInfo;
    }
}
