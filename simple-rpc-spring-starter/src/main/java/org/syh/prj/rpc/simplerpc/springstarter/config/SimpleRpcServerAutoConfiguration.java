package org.syh.prj.rpc.simplerpc.springstarter.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.syh.prj.rpc.simplerpc.core.common.event.SimpleRpcListenerLoader;
import org.syh.prj.rpc.simplerpc.core.server.ApplicationShutdownHook;
import org.syh.prj.rpc.simplerpc.core.server.Server;
import org.syh.prj.rpc.simplerpc.core.server.ServiceWrapper;
import org.syh.prj.rpc.simplerpc.springstarter.common.SimpleRpcService;

import java.util.Map;

public class SimpleRpcServerAutoConfiguration implements InitializingBean, ApplicationContextAware {
    private final Logger logger = LogManager.getLogger(SimpleRpcServerAutoConfiguration.class);

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(SimpleRpcService.class);
        if (beanMap.isEmpty()) {
            return;
        }

        printBanner();

        long begin = System.currentTimeMillis();
        Server server = new Server();
        server.initServerConfig();
        SimpleRpcListenerLoader.init();
        for (String beanName : beanMap.keySet()) {
            Object bean = beanMap.get(beanName);
            SimpleRpcService simpleRpcService = bean.getClass().getAnnotation(SimpleRpcService.class);
            ServiceWrapper dataServiceServiceWrapper = new ServiceWrapper(bean, simpleRpcService.group());
            dataServiceServiceWrapper.setServiceToken(simpleRpcService.serviceToken());
            dataServiceServiceWrapper.setLimit(simpleRpcService.limit());
            server.exportService(dataServiceServiceWrapper);
            logger.info(">>>>>>>>>>>>>>> [simple-rpc] {} export success! >>>>>>>>>>>>>>> ", beanName);
        }
        long end = System.currentTimeMillis();

        ApplicationShutdownHook.registryShutdownHook();
        server.startApplication();
        logger.info(" ================== [{}] started success in {}s ================== ",server.getServerConfig().getApplicationName(),((double)end-(double)begin)/1000);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void printBanner(){
        System.out.println();
        System.out.println("===================================================");
        System.out.println("|||---------- SimpleRpc Starting Now! ----------|||");
        System.out.println("===================================================");
        System.out.println();
    }
}
