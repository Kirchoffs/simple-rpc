package org.syh.prj.rpc.simplerpc.core.common.config;

import java.io.IOException;

import static org.syh.prj.rpc.simplerpc.core.common.constants.RpcConstants.DEFAULT_QUEUE_SIZE;
import static org.syh.prj.rpc.simplerpc.core.common.constants.RpcConstants.DEFAULT_THREAD_NUMS;

public class PropertiesBootstrap {
    private volatile boolean configIsReady;

    public static final String SERVER_PORT = "simple-rpc.server-port";
    public static final String REGISTER_ADDRESS = "simple-rpc.register-addr";
    public static final String REGISTER_TYPE = "simple-rpc.register-type";
    public static final String APPLICATION_NAME = "simple-rpc.application-name";
    public static final String PROXY_TYPE = "simple-rpc.proxy-type";
    public static final String ROUTER_STRATEGY = "simple-rpc.router-strategy";
    public static final String CLIENT_SERIALIZE = "simple-rpc.client-serialize";
    public static final String SERVER_SERIALIZE = "simple-rpc.server-serialize";
    public static final String SERVER_QUEUE_SIZE = "simple-rpc.server-queue-size";
    public static final String SERVER_BIZ_THREAD_NUMS = "simple-rpc.server-biz-thread-nums";

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
        serverConfig.setRegisterType(PropertiesLoader.getPropertiesStr(REGISTER_TYPE));
        serverConfig.setServerSerialize(PropertiesLoader.getPropertiesStr(SERVER_SERIALIZE));
        serverConfig.setServerQueueSize(PropertiesLoader.getPropertiesInteger(SERVER_QUEUE_SIZE, DEFAULT_QUEUE_SIZE));
        serverConfig.setServerBizThreadNums(PropertiesLoader.getPropertiesInteger(SERVER_BIZ_THREAD_NUMS, DEFAULT_THREAD_NUMS));

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
        clientConfig.setRegisterType(PropertiesLoader.getPropertiesStr(REGISTER_TYPE));
        clientConfig.setProxyType(PropertiesLoader.getPropertiesStr(PROXY_TYPE));
        clientConfig.setRouterStrategy(PropertiesLoader.getPropertiesStr(ROUTER_STRATEGY));
        clientConfig.setClientSerialize(PropertiesLoader.getPropertiesStr(CLIENT_SERIALIZE));

        return clientConfig;
    }
}
