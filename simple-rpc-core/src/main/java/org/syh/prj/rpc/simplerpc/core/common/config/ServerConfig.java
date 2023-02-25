package org.syh.prj.rpc.simplerpc.core.common.config;

public class ServerConfig {
    private Integer serverPort;

    private String registerAddr;

    private String registerType;

    private String applicationName;

    private String serverSerialize;

    private Integer serverBizThreadNums;

    private Integer serverQueueSize;

    private Integer maxServerRequestDataSize;

    private Integer maxConnections;

    public String getRegisterAddr() {
        return registerAddr;
    }

    public void setRegisterAddr(String registerAddr) {
        this.registerAddr = registerAddr;
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getServerSerialize() {
        return serverSerialize;
    }

    public void setServerSerialize(String serverSerialize) {
        this.serverSerialize = serverSerialize;
    }

    public Integer getServerBizThreadNums() {
        return serverBizThreadNums;
    }

    public void setServerBizThreadNums(Integer serverBizThreadNums) {
        this.serverBizThreadNums = serverBizThreadNums;
    }

    public Integer getServerQueueSize() {
        return serverQueueSize;
    }

    public void setServerQueueSize(Integer serverQueueSize) {
        this.serverQueueSize = serverQueueSize;
    }

    public Integer getMaxServerRequestDataSize() {
        return maxServerRequestDataSize;
    }

    public void setMaxServerRequestDataSize(Integer maxServerRequestDataSize) {
        this.maxServerRequestDataSize = maxServerRequestDataSize;
    }

    public Integer getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }
}
