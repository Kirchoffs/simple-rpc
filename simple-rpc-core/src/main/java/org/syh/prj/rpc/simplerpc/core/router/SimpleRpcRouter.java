package org.syh.prj.rpc.simplerpc.core.router;

import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFutureWrapper;
import org.syh.prj.rpc.simplerpc.core.registry.URL;

public interface SimpleRpcRouter {
    void refreshRouterArr(Selector selector);

    ChannelFutureWrapper select(Selector selector);
}
