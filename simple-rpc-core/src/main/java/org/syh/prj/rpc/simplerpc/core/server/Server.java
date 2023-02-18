package org.syh.prj.rpc.simplerpc.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.syh.prj.rpc.simplerpc.core.common.config.PropertiesBootstrap;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcDecoder;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcEncoder;
import org.syh.prj.rpc.simplerpc.core.common.config.ServerConfig;
import org.syh.prj.rpc.simplerpc.core.common.utils.CommonUtils;
import org.syh.prj.rpc.simplerpc.core.filter.ServerFilter;
import org.syh.prj.rpc.simplerpc.core.filter.server.ServerFilterChain;
import org.syh.prj.rpc.simplerpc.core.registry.RegistryService;
import org.syh.prj.rpc.simplerpc.core.registry.URL;
import org.syh.prj.rpc.simplerpc.core.registry.zookeeper.AbstractRegister;
import org.syh.prj.rpc.simplerpc.core.serialize.SerializeFactory;
import org.syh.prj.rpc.simplerpc.core.spi.ExtensionLoader;

import java.io.IOException;
import java.util.List;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.PROVIDER_CLASS_MAP;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.PROVIDER_SERVICE_WRAPPER_MAP;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.PROVIDER_URL_SET;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.SERVER_CHANNEL_DISPATCHER;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.SERVER_CONFIG;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.SERVER_FILTER_CHAIN;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.SERVER_SERIALIZE_FACTORY;

public class Server {
    private static EventLoopGroup bossGroup = null;
    private static EventLoopGroup workerGroup = null;

    private ServerConfig serverConfig;

    private RegistryService registryService;

    private ExtensionLoader extensionLoader = new ExtensionLoader();

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void startApplication() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap
                .option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_SNDBUF, 16 * 1024)
                .childOption(ChannelOption.SO_RCVBUF, 16 * 1024);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RpcEncoder());
                ch.pipeline().addLast(new RpcDecoder());
                ch.pipeline().addLast(new ServerHandler());
            }
        });

        this.batchRegisterUrl();
        SERVER_CHANNEL_DISPATCHER.startDataConsume();
        bootstrap.bind(serverConfig.getServerPort()).sync();
    }

    public void initServerConfig()
        throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        serverConfig = PropertiesBootstrap.loadServerConfigFromLocal();
        SERVER_CONFIG = serverConfig;

        SERVER_CHANNEL_DISPATCHER.init(SERVER_CONFIG.getServerQueueSize(), SERVER_CONFIG.getServerBizThreadNums());

        Class<?> serialClass = extensionLoader.getActualClass(SerializeFactory.class, serverConfig.getServerSerialize());
        SERVER_SERIALIZE_FACTORY = (SerializeFactory) serialClass.newInstance();

        ServerFilterChain serverFilterChain = new ServerFilterChain();
        List<Class<?>> serverFilterClassList = extensionLoader.getActualClassList(ServerFilter.class);
        for (Class<?> clazz: serverFilterClassList) {
            serverFilterChain.addServerFilter((ServerFilter) clazz.newInstance());
        }
        SERVER_FILTER_CHAIN = serverFilterChain;
    }

    public void exportService(ServiceWrapper serviceWrapper) {
        Object serviceBean = serviceWrapper.getServiceObj();
        if (serviceBean.getClass().getInterfaces().length == 0) {
            throw new RuntimeException("service should have interfaces!");
        }

        Class[] classes = serviceBean.getClass().getInterfaces();
        if (classes.length > 1) {
            throw new RuntimeException("service should only have one interfaces!");
        }

        if (registryService == null) {
            try {
                Class<?> registerClass = extensionLoader.getActualClass(RegistryService.class, serverConfig.getRegisterType());
                registryService = (AbstractRegister) registerClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Class interfaceClass = classes[0];
        PROVIDER_CLASS_MAP.put(interfaceClass.getName(), serviceBean);
        URL url = new URL();
        url.setServiceName(interfaceClass.getName());
        url.setApplicationName(serverConfig.getApplicationName());
        url.addParameter("host", CommonUtils.getIpAddress());
        url.addParameter("port", String.valueOf(serverConfig.getServerPort()));
        url.addParameter("weight", String.valueOf(serviceWrapper.getWeight()));
        url.addParameter("group", serviceWrapper.getGroup());
        PROVIDER_URL_SET.add(url);
        PROVIDER_SERVICE_WRAPPER_MAP.put(interfaceClass.getName(), serviceWrapper);
    }

    public void batchRegisterUrl() {
        Thread task = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (URL url: PROVIDER_URL_SET) {
                    registryService.register(url);
                }
            }
        });

        task.start();
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.initServerConfig();

        ServiceWrapper dataServiceWrapper = new ServiceWrapper(new DataServiceImpl(), "dev");
        dataServiceWrapper.setServiceToken("token-picea");
        dataServiceWrapper.setLimit(2);
        dataServiceWrapper.setWeight(200);

        ServiceWrapper userServiceWrapper = new ServiceWrapper(new UserServiceImpl(), "dev");
        userServiceWrapper.setServiceToken("token-abies");
        userServiceWrapper.setLimit(2);
        dataServiceWrapper.setWeight(100);

        server.exportService(dataServiceWrapper);
        server.exportService(userServiceWrapper);

        server.startApplication();
    }
}
