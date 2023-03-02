package org.syh.prj.rpc.simplerpc.springstarter.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.syh.prj.rpc.simplerpc.core.client.Client;
import org.syh.prj.rpc.simplerpc.core.client.ConnectionHandler;
import org.syh.prj.rpc.simplerpc.core.client.RpcReference;
import org.syh.prj.rpc.simplerpc.core.client.RpcReferenceWrapper;
import org.syh.prj.rpc.simplerpc.springstarter.common.SimpleRpcReference;

import java.lang.reflect.Field;

public class SimpleRpcClientAutoConfiguration implements BeanPostProcessor, ApplicationListener<ApplicationReadyEvent> {
    private static RpcReference rpcReference = null;
    private static Client client = null;
    private volatile boolean needInitClient = false;
    private volatile boolean hasInitClientConfig = false;

    private static final Logger logger = LogManager.getLogger(SimpleRpcClientAutoConfiguration.class);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(SimpleRpcReference.class)) {
                if (!hasInitClientConfig) {
                    client = new Client();
                    try {
                        rpcReference = client.initClientApplication();
                    } catch (Exception e) {
                        logger.error("[SimpleRpcClientAutoConfiguration] postProcessAfterInitialization has error ", e);
                        throw new RuntimeException(e);
                    }
                    hasInitClientConfig = true;
                }
                needInitClient = true;
                SimpleRpcReference simpleRpcReference = field.getAnnotation(SimpleRpcReference.class);
                try {
                    field.setAccessible(true);
                    RpcReferenceWrapper rpcReferenceWrapper = new RpcReferenceWrapper();
                    rpcReferenceWrapper.setAimClass(field.getType());
                    rpcReferenceWrapper.setGroup(simpleRpcReference.group());
                    rpcReferenceWrapper.setServiceToken(simpleRpcReference.serviceToken());
                    rpcReferenceWrapper.setUrl(simpleRpcReference.url());
                    rpcReferenceWrapper.setTimeOut(simpleRpcReference.timeOut());
                    rpcReferenceWrapper.setRetry(simpleRpcReference.retry());
                    Object refObj = rpcReference.get(rpcReferenceWrapper);
                    field.set(bean, refObj);
                    client.doSubscribeService(field.getType());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
        return bean;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        if (needInitClient && client != null) {
            logger.info(" ================== [{}] started success ================== ", client.getClientConfig().getApplicationName());
            ConnectionHandler.setBootstrap(client.getBootstrap());
            client.doConnectServer();
            client.startClient();
        }
    }
}
