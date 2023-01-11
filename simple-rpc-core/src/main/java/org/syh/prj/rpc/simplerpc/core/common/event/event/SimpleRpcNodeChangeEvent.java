package org.syh.prj.rpc.simplerpc.core.common.event.event;

import org.syh.prj.rpc.simplerpc.core.common.event.SimpleRpcEvent;

public class SimpleRpcNodeChangeEvent implements SimpleRpcEvent {

    private Object data;

    public SimpleRpcNodeChangeEvent(Object data) {
        this.data = data;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public SimpleRpcNodeChangeEvent setData(Object data) {
        this.data = data;
        return this;
    }
}
