package org.syh.prj.rpc.simplerpc.core.common.event.event;

import org.syh.prj.rpc.simplerpc.core.common.event.SimpleRpcEvent;

public class SimpleRpcServiceUpdateEvent implements SimpleRpcEvent {
    private Object data;

    public SimpleRpcServiceUpdateEvent(Object data) {
        this.data = data;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public SimpleRpcEvent setData(Object data) {
        this.data = data;
        return this;
    }

}
