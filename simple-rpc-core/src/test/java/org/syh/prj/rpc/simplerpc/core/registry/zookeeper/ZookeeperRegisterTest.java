package org.syh.prj.rpc.simplerpc.core.registry.zookeeper;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.syh.prj.rpc.simplerpc.interfaces.DataService;

import java.util.List;

@Disabled
public class ZookeeperRegisterTest {
    @Test
    public void testConnectZookeeper() throws Exception {
        ZookeeperRegister zookeeperRegister = new ZookeeperRegister("localhost:2181");
        List<String> urls = zookeeperRegister.getProviderIps(DataService.class.getName());
        System.out.println(urls);
    }
}
