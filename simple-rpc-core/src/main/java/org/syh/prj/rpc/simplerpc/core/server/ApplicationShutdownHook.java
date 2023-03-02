package org.syh.prj.rpc.simplerpc.core.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.syh.prj.rpc.simplerpc.core.common.event.SimpleRpcListenerLoader;
import org.syh.prj.rpc.simplerpc.core.common.event.event.SimpleRpcDestroyEvent;

public class ApplicationShutdownHook {
    private static final Logger logger = LogManager.getLogger(ApplicationShutdownHook.class);

    public static void registryShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("[registryShutdownHook] ====");
                SimpleRpcListenerLoader.sendSyncEvent(new SimpleRpcDestroyEvent("destroy"));
            }
        }));
    }
}
