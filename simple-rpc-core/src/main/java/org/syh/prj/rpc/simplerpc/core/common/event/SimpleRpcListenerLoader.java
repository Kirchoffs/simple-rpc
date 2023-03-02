package org.syh.prj.rpc.simplerpc.core.common.event;

import org.syh.prj.rpc.simplerpc.core.common.event.listener.SimpleRpcServiceUpdateListener;
import org.syh.prj.rpc.simplerpc.core.common.utils.CommonUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleRpcListenerLoader {
    private static List<SimpleRpcListener> simpleRpcListenerList = new ArrayList<>();

    private static ExecutorService eventThreadPool = Executors.newFixedThreadPool(2);

    public static void registerListener(SimpleRpcListener simpleRpcListener) {
        simpleRpcListenerList.add(simpleRpcListener);
    }

    public static void init() {
        registerListener(new SimpleRpcServiceUpdateListener());
    }

    private static Class<?> getInterfaceT(Object o) {
        Type[] types = o.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) types[0];
        Type type = parameterizedType.getActualTypeArguments()[0];
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        return null;
    }

    public static void sendEvent(SimpleRpcEvent simpleRpcEvent) {
        if (CommonUtils.isEmptyList(simpleRpcListenerList)) {
            return;
        }

        for (SimpleRpcListener<?> simpleRpcListener: simpleRpcListenerList) {
            Class<?> type = getInterfaceT(simpleRpcListener);
            if (type.equals(simpleRpcEvent.getClass())) {
                eventThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            simpleRpcListener.callBack(simpleRpcEvent.getData());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    public static void sendSyncEvent(SimpleRpcEvent iRpcEvent) {
        if (CommonUtils.isEmptyList(simpleRpcListenerList)) {
            return;
        }
        for (SimpleRpcListener<?> simpleRpcListener : simpleRpcListenerList) {
            Class<?> type = getInterfaceT(simpleRpcListener);
            if (type.equals(iRpcEvent.getClass())) {
                try {
                    simpleRpcListener.callBack(iRpcEvent.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
