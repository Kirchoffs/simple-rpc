package org.syh.prj.rpc.simplerpc.core.registry;

import org.syh.prj.rpc.simplerpc.core.registry.zookeeper.ProviderNodeInfo;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class URL {
    /**
     * Client's name
     */
    private String applicationName;

    /**
     * Service's name
     * e.g. com.site.services.UserService
     */
    private String serviceName;

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

    public static String buildProviderDataStr(URL url) {
        String host = url.getParameters().get("host");
        String port = url.getParameters().get("port");
        String weight = url.getParameters().get("weight");
        return String.format(
            "%s;%s;%s:%s;%d;%s",
            url.getApplicationName(), url.getServiceName(), host, port, System.currentTimeMillis(), weight
        );
    }

    public static String buildConsumerDataStr(URL url) {
        String host = url.getParameters().get("host");
        return String.format(
                "%s;%s;%s;%d",
                url.getApplicationName(),
                url.getServiceName(),
                host,
                System.currentTimeMillis()
        );
    }

    public static ProviderNodeInfo buildProviderNodeInfoFromDataStr(String providerDataStr) {
        String[] items = providerDataStr.split(";");
        ProviderNodeInfo providerNodeInfo = new ProviderNodeInfo();
        providerNodeInfo.setServiceName(items[1]);
        providerNodeInfo.setAddress(items[2] + ":" + items[3]);
        providerNodeInfo.setWeight(Integer.valueOf(items[5]));
        providerNodeInfo.setRegistryTime(new Date(Long.parseLong(items[4])).toString());
        return providerNodeInfo;
    }
}
