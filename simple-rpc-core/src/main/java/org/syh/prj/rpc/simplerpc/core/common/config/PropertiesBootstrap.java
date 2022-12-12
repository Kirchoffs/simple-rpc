package org.syh.prj.rpc.simplerpc.core.common.config;

import java.io.IOException;

public class PropertiesBootstrap {
    private volatile boolean configIsReady;

    public static final String SERVER_PORT = "simple-rpc.serverPort";
    public static final String REGISTER_ADDRESS = "simple-rpc.registerAddr";
    public static final String APPLICATION_NAME = "simple-rpc.applicationName";
    public static final String PROXY_TYPE = "simple-rpc.proxyType";

    public static ServerConfig loadServerConfigFromLocal() {
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("[loadServerConfigFromLocal] failed, {}", e);
        }

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setServerPort(PropertiesLoader.getPropertiesInteger(SERVER_PORT));
        serverConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        serverConfig.setRegisterAddr(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));

        return serverConfig;
    }

    public static ClientConfig loadClientConfigFromLocal(){
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("[loadClientConfigFromLocal] failed, error: ", e);
        }

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        clientConfig.setRegisterAddr(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));
        clientConfig.setProxyType(PropertiesLoader.getPropertiesStr(PROXY_TYPE));

        return clientConfig;
    }
}
