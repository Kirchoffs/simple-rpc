package org.syh.prj.rpc.simplerpc.core.registry.zookeeper;

import org.apache.zookeeper.Watcher;

import java.util.List;

public abstract class AbstractZookeeperClient {
    private String zkAddress;
    private int baseSleepTimes;
    private int maxRetryTimes;

    public AbstractZookeeperClient(String zkAddress) {
        this.zkAddress = zkAddress;
        this.baseSleepTimes = 1000;
        this.maxRetryTimes = 3;
    }

    public AbstractZookeeperClient(String zkAddress, Integer baseSleepTimes, Integer maxRetryTimes) {
        this.zkAddress = zkAddress;

        if (baseSleepTimes == null) {
            this.baseSleepTimes = 1000;
        } else {
            this.baseSleepTimes = baseSleepTimes;
        }

        if (maxRetryTimes == null) {
            this.maxRetryTimes = 3;
        } else {
            this.maxRetryTimes = maxRetryTimes;
        }
    }

    public int getBaseSleepTimes() {
        return baseSleepTimes;
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setBaseSleepTimes(int baseSleepTimes) {
        this.baseSleepTimes = baseSleepTimes;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public abstract void updateNodeData(String address, String data);

    public abstract Object getClient();

    public abstract String getNodeData(String path);

    public abstract List<String> getChildrenList(String path);

    public abstract void createPersistentData(String address, String data);

    public abstract void createTemporaryData(String address, String data);

    public abstract void createPersistentWithSeqData(String address, String data);

    public abstract void createTemporarySeqData(String address, String data);

    public abstract void setTemporaryData(String address, String data);

    public abstract void destroy();

    public abstract List<String> listNode(String address);

    public abstract boolean deleteNode(String address);

    public abstract boolean existNode(String address);

    public abstract void watchNodeData(String path, Watcher watcher);

    public abstract void watchChildNodeData(String path, Watcher watcher);
}
