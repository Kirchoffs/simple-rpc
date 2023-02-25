package org.syh.prj.rpc.simplerpc.core.common.config;

import java.io.IOException;

import static org.syh.prj.rpc.simplerpc.core.common.constants.RpcConstants.CLIENT_DEFAULT_MSG_LENGTH;
import static org.syh.prj.rpc.simplerpc.core.common.constants.RpcConstants.DEFAULT_MAX_CONNECTIONS;
import static org.syh.prj.rpc.simplerpc.core.common.constants.RpcConstants.DEFAULT_QUEUE_SIZE;
import static org.syh.prj.rpc.simplerpc.core.common.constants.RpcConstants.DEFAULT_THREAD_NUMS;
import static org.syh.prj.rpc.simplerpc.core.common.constants.RpcConstants.DEFAULT_TIMEOUT;
import static org.syh.prj.rpc.simplerpc.core.common.constants.RpcConstants.SERVER_DEFAULT_MSG_LENGTH;

public class PropertiesBootstrap {
    private volatile boolean configIsReady;

    public static final String SERVER_PORT = "simple-rpc.server-port";
    public static final String REGISTER_ADDRESS = "simple-rpc.register-addr";
    public static final String REGISTER_TYPE = "simple-rpc.register-type";
    public static final String APPLICATION_NAME = "simple-rpc.application-name";
    public static final String PROXY_TYPE = "simple-rpc.proxy-type";
    public static final String ROUTER_STRATEGY = "simple-rpc.router-strategy";
    public static final String CLIENT_SERIALIZE = "simple-rpc.client-serialize";
    public static final String CLIENT_DEFAULT_TIME_OUT = "simple-rpc.client.default.timeout";
    public static final String SERVER_SERIALIZE = "simple-rpc.server-serialize";
    public static final String SERVER_QUEUE_SIZE = "simple-rpc.server-queue-size";
    public static final String SERVER_BIZ_THREAD_NUMS = "simple-rpc.server-biz-thread-nums";
    public static final String SERVER_MAX_CONNECTIONS = "simple-rpc.server.max.connectionS";
    public static final String SERVER_MAX_DATA_SIZE = "simple-rpc.server.max.data.size";
    public static final String CLIENT_MAX_DATA_SIZE = "simple-rpc.client.max.data.size";

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
        serverConfig.setMaxConnections(PropertiesLoader.getPropertiesInteger(SERVER_MAX_CONNECTIONS, DEFAULT_MAX_CONNECTIONS));
        serverConfig.setMaxServerRequestDataSize(PropertiesLoader.getPropertiesInteger(SERVER_MAX_DATA_SIZE, SERVER_DEFAULT_MSG_LENGTH));

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
        clientConfig.setTimeOut(PropertiesLoader.getPropertiesInteger(CLIENT_DEFAULT_TIME_OUT, DEFAULT_TIMEOUT));
        clientConfig.setMaxServerRespDataSize(PropertiesLoader.getPropertiesInteger(CLIENT_MAX_DATA_SIZE, CLIENT_DEFAULT_MSG_LENGTH));

        return clientConfig;
    }
}
