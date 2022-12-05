package org.syh.prj.rpc.simplerpc.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.syh.prj.rpc.simplerpc.core.client.Client;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcDecoder;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcEncoder;
import org.syh.prj.rpc.simplerpc.core.common.config.ServerConfig;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.PROVIDER_CLASS_MAP;

public class Server {
    private Logger logger = LogManager.getLogger(Client.class);

    private static EventLoopGroup bossGroup = null;
    private static EventLoopGroup workerGroup = null;

    private ServerConfig serverConfig;

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

        bootstrap.bind(serverConfig.getPort()).sync();
        logger.info("server started");
    }

    public void registerService(Object serviceBean) {
        if (serviceBean.getClass().getInterfaces().length == 0) {
            throw new RuntimeException("service should have interfaces!");
        }

        Class[] classes = serviceBean.getClass().getInterfaces();
        if (classes.length > 1) {
            throw new RuntimeException("service should only have one interfaces!");
        }

        Class interfaceClass = classes[0];
        PROVIDER_CLASS_MAP.put(interfaceClass.getName(), serviceBean);
    }

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(9090);
        server.setServerConfig(serverConfig);

        server.registerService(new DataServiceImpl());

        server.startApplication();
    }
}
