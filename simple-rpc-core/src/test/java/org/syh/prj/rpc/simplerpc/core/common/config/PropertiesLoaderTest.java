package org.syh.prj.rpc.simplerpc.core.common.config;

import org.junit.jupiter.api.Test;

public class PropertiesLoaderTest {
    @Test
    public void testGetPropertiesStr() throws Exception {
        PropertiesLoader.loadConfiguration();
        System.out.println(PropertiesLoader.getPropertiesStr("simple-rpc.proxyType"));
    }
}
