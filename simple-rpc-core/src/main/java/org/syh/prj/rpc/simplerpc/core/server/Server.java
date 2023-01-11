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
import org.syh.prj.rpc.simplerpc.core.registry.RegistryService;
import org.syh.prj.rpc.simplerpc.core.registry.URL;
import org.syh.prj.rpc.simplerpc.core.registry.zookeeper.ZookeeperRegister;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.PROVIDER_CLASS_MAP;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.PROVIDER_URL_SET;

public class Server {

    private static EventLoopGroup bossGroup = null;
    private static EventLoopGroup workerGroup = null;

    private ServerConfig serverConfig;

    private RegistryService registryService;

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

        this.batchExportUrl();
        bootstrap.bind(serverConfig.getServerPort()).sync();
    }

    public void initServerConfig() {
        ServerConfig serverConfig = PropertiesBootstrap.loadServerConfigFromLocal();
        this.setServerConfig(serverConfig);
    }

    public void exportService(Object serviceBean) {
        exportService(serviceBean, 100);
    }

    public void exportService(Object serviceBean, int weight) {
        if (serviceBean.getClass().getInterfaces().length == 0) {
            throw new RuntimeException("service should have interfaces!");
        }

        Class[] classes = serviceBean.getClass().getInterfaces();
        if (classes.length > 1) {
            throw new RuntimeException("service should only have one interfaces!");
        }

        if (registryService == null) {
            registryService = new ZookeeperRegister(serverConfig.getRegisterAddr());
        }

        Class interfaceClass = classes[0];
        PROVIDER_CLASS_MAP.put(interfaceClass.getName(), serviceBean);
        URL url = new URL();
        url.setServiceName(interfaceClass.getName());
        url.setApplicationName(serverConfig.getApplicationName());
        url.addParameter("host", CommonUtils.getIpAddress());
        url.addParameter("port", String.valueOf(serverConfig.getServerPort()));
        url.addParameter("weight", String.valueOf(weight));
        PROVIDER_URL_SET.add(url);
    }

    public void batchExportUrl() {
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

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        server.initServerConfig();
        server.exportService(new DataServiceImpl());
        server.startApplication();
    }
}
