package org.syh.prj.rpc.simplerpc.core.registry.zookeeper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.syh.prj.rpc.simplerpc.core.client.Client;
import org.syh.prj.rpc.simplerpc.core.common.event.SimpleRpcListenerLoader;
import org.syh.prj.rpc.simplerpc.core.common.event.event.SimpleRpcNodeChangeEvent;
import org.syh.prj.rpc.simplerpc.core.registry.RegistryService;
import org.syh.prj.rpc.simplerpc.core.registry.URL;
import org.syh.prj.rpc.simplerpc.core.common.event.data.URLChangeWrapper;
import org.syh.prj.rpc.simplerpc.core.common.event.SimpleRpcEvent;
import org.syh.prj.rpc.simplerpc.core.common.event.event.SimpleRpcServiceUpdateEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.CLIENT_CONFIG;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.SERVER_CONFIG;

public class ZookeeperRegister extends AbstractRegister implements RegistryService {
    private final Logger logger = LogManager.getLogger(ZookeeperRegister.class);

    private AbstractZookeeperClient zkClient;

    private String ROOT = "/simple-rpc";

    private String getProviderPath(URL url) {
        return ROOT + "/" + url.getServiceName() + "/provider/" + url.getParameters().get("host") + ":" + url.getParameters().get("port");
    }

    private String getConsumerPath(URL url) {
        return ROOT + "/" + url.getServiceName() + "/consumer/" + url.getApplicationName() + ":" + url.getParameters().get("host");
    }

    public ZookeeperRegister() {
        String registryAddr = CLIENT_CONFIG != null ? CLIENT_CONFIG.getRegisterAddr() : SERVER_CONFIG.getRegisterAddr();
        this.zkClient = new CuratorZookeeperClient(registryAddr);
    }

    public ZookeeperRegister(String address) {
        this.zkClient = new CuratorZookeeperClient(address);
    }


    @Override
    public List<String> getProviderIps(String serviceName) {
        List<String> nodeDataList = this.zkClient.getChildrenList(ROOT + "/" + serviceName + "/provider");
        return nodeDataList;
    }

    @Override
    public Map<String, String> getServiceDetailMap(String serviceName) {
        List<String> nodeDataList = this.zkClient.getChildrenList(ROOT + "/" + serviceName + "/provider");
        Map<String, String> result = new HashMap<>();
        for (String ipAndHost: nodeDataList) {
            logger.info("Address {}", ipAndHost);
            String childData = this.zkClient.getNodeData(ROOT + "/" + serviceName + "/provider/" + ipAndHost);
            result.put(ipAndHost, childData);
        }
        return result;
    }

    @Override
    public void  register(URL url) {
        if (!this.zkClient.existNode(ROOT)) {
            zkClient.createPersistentData(ROOT, "");
        }

        String urlStr = URL.buildProviderDataStr(url);
        String providerPath = getProviderPath(url);
        if (zkClient.existNode(providerPath)) {
            zkClient.deleteNode(providerPath);
        }
        zkClient.createTemporaryData(providerPath, urlStr);

        super.register(url);
    }

    @Override
    public void unRegister(URL url) {
        zkClient.deleteNode(getProviderPath(url));
        super.unRegister(url);
    }

    @Override
    public void subscribe(URL url) {
        if (!this.zkClient.existNode(ROOT)) {
            zkClient.createPersistentData(ROOT, "");
        }

        String urlStr = URL.buildConsumerDataStr(url);
        if (zkClient.existNode(getConsumerPath(url))) {
            zkClient.deleteNode(getConsumerPath(url));
        }
        zkClient.createTemporarySeqData(getConsumerPath(url), urlStr);

        super.subscribe(url);
    }

    @Override
    public void unSubscribe(URL url) {
        zkClient.deleteNode(getConsumerPath(url));
        super.unSubscribe(url);
    }

    @Override
    public void doAfterSubscribe(URL url) {
        String servicePath = ROOT + "/" + url.getParameters().get("servicePath");
        watchChildNodeData(servicePath);
        String providerIpsStr = url.getParameters().get("providerIps");
        String[] providerIpsList = providerIpsStr.split(",");
        for (String providerIp: providerIpsList) {
            watchNodeDataChange(servicePath + "/" + providerIp);
        }
    }

    public void watchNodeDataChange(String newServerNodePath) {
        zkClient.watchNodeData(newServerNodePath, new Watcher() {

            @Override
            public void process(WatchedEvent watchedEvent) {
                String path = watchedEvent.getPath();
                String nodeData = zkClient.getNodeData(path);
                ProviderNodeInfo providerNodeInfo = URL.buildProviderNodeInfoFromDataStr(nodeData);
                SimpleRpcEvent event = new SimpleRpcNodeChangeEvent(providerNodeInfo);
                SimpleRpcListenerLoader.sendEvent(event);
                watchNodeDataChange(newServerNodePath);
            }
        });
    }

    public void watchChildNodeData(String newServerNodePath){
        zkClient.watchChildNodeData(newServerNodePath, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent);
                String path = watchedEvent.getPath();
                List<String> childrenDataList = zkClient.getChildrenList(path);

                URLChangeWrapper urlChangeWrapper = new URLChangeWrapper();
                urlChangeWrapper.setProviderUrl(childrenDataList);
                urlChangeWrapper.setServiceName(path.replaceFirst("^/", "").split("/")[1]);

                SimpleRpcEvent simpleRpcEvent = new SimpleRpcServiceUpdateEvent(urlChangeWrapper);
                SimpleRpcListenerLoader.sendEvent(simpleRpcEvent);

                watchChildNodeData(path);
            }
        });
    }

    @Override
    public void doBeforeSubscribe(URL url) {

    }
}
