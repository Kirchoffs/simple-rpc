package org.syh.prj.rpc.simplerpc.core.common.event.listener;

import org.syh.prj.rpc.simplerpc.core.common.event.SimpleRpcListener;
import org.syh.prj.rpc.simplerpc.core.common.event.event.SimpleRpcDestroyEvent;
import org.syh.prj.rpc.simplerpc.core.registry.URL;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.PROVIDER_URL_SET;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.REGISTRY_SERVICE;

public class SimpleRpcDestroyEventListener implements SimpleRpcListener<SimpleRpcDestroyEvent> {
    @Override
    public void callBack(Object t) {
        for (URL url : PROVIDER_URL_SET) {
            REGISTRY_SERVICE.unRegister(url);
        }
    }
}
