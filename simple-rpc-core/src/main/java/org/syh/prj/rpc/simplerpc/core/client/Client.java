package org.syh.prj.rpc.simplerpc.core.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.syh.prj.rpc.simplerpc.core.common.config.ClientConfig;
import org.syh.prj.rpc.simplerpc.core.common.config.PropertiesBootstrap;
import org.syh.prj.rpc.simplerpc.core.common.event.SimpleRpcListenerLoader;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcDecoder;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcEncoder;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcProtocol;
import org.syh.prj.rpc.simplerpc.core.common.utils.CommonUtils;
import org.syh.prj.rpc.simplerpc.core.filter.ClientFilter;
import org.syh.prj.rpc.simplerpc.core.filter.client.ClientFilterChain;
import org.syh.prj.rpc.simplerpc.core.proxy.ProxyFactory;
import org.syh.prj.rpc.simplerpc.core.registry.RegistryService;
import org.syh.prj.rpc.simplerpc.core.registry.URL;
import org.syh.prj.rpc.simplerpc.core.registry.zookeeper.AbstractRegister;
import org.syh.prj.rpc.simplerpc.core.router.SimpleRpcRouter;
import org.syh.prj.rpc.simplerpc.core.serialize.SerializeFactory;
import org.syh.prj.rpc.simplerpc.core.spi.ExtensionLoader;
import org.syh.prj.rpc.simplerpc.interfaces.OrderService;
import org.syh.prj.rpc.simplerpc.interfaces.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.CLIENT_FILTER_CHAIN;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.SEND_QUEUE;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.SUBSCRIBE_SERVICE_LIST;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.RPC_ROUTER;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.URL_MAP;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.CLIENT_SERIALIZE_FACTORY;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.CLIENT_CONFIG;
import static org.syh.prj.rpc.simplerpc.core.common.constants.RpcConstants.DEFAULT_DELIMITER;

public class Client {
    private final Logger logger = LogManager.getLogger(Client.class);
    public static EventLoopGroup clientGroup;
    private final Bootstrap bootstrap = new Bootstrap();
    private ClientConfig clientConfig;
    private AbstractRegister abstractRegister;
    private SimpleRpcListenerLoader simpleRpcListenerLoader;
    private ExtensionLoader extensionLoader = new ExtensionLoader();

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }
    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public RpcReference initClientApplication()
        throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        clientGroup = new NioEventLoopGroup();

        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ByteBuf delimiter = Unpooled.copiedBuffer(DEFAULT_DELIMITER.getBytes());
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(clientConfig.getMaxServerRespDataSize(), delimiter));
                ch.pipeline().addLast(new RpcEncoder());
                ch.pipeline().addLast(new RpcDecoder());
                ch.pipeline().addLast(new ClientHandler());
            }
        });

        simpleRpcListenerLoader = new SimpleRpcListenerLoader();
        simpleRpcListenerLoader.init();

        clientConfig = PropertiesBootstrap.loadClientConfigFromLocal();
        CLIENT_CONFIG = this.clientConfig;

        initClientConfig();

        Class<?> proxyFactoryClass = extensionLoader.getActualClass(ProxyFactory.class, clientConfig.getProxyType());
        ProxyFactory proxyFactory = (ProxyFactory) proxyFactoryClass.newInstance();

        return new RpcReference(proxyFactory);
    }

    private void initClientConfig()
        throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> routerClass = extensionLoader.getActualClass(SimpleRpcRouter.class, clientConfig.getRouterStrategy());
        RPC_ROUTER = (SimpleRpcRouter) routerClass.newInstance();

        Class<?> serialClass = extensionLoader.getActualClass(SerializeFactory.class, clientConfig.getClientSerialize());
        CLIENT_SERIALIZE_FACTORY = (SerializeFactory) serialClass.newInstance();

        ClientFilterChain clientFilterChain = new ClientFilterChain();
        List<Class<?>> clientFilterClassList = extensionLoader.getActualClassList(ClientFilter.class);
        for (Class<?> clazz: clientFilterClassList) {
            clientFilterChain.addClientFilter((ClientFilter) clazz.newInstance());
        }
        CLIENT_FILTER_CHAIN = clientFilterChain;
    }

    public void doSubscribeService(Class serviceBean) {
        if (abstractRegister == null) {
            try {
                Class<?> registerClass = extensionLoader.getActualClass(RegistryService.class, clientConfig.getRegisterType());
                abstractRegister = (AbstractRegister) registerClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        URL url = new URL();
        url.setApplicationName(clientConfig.getApplicationName());
        url.setServiceName(serviceBean.getName());
        url.addParameter("host", CommonUtils.getIpAddress());

        Map<String, String> result = abstractRegister.getServiceDetailMap(serviceBean.getName());
        URL_MAP.put(serviceBean.getName(), result);

        abstractRegister.subscribe(url);
    }

    public void doConnectServer() {
        for (URL providerUrl: SUBSCRIBE_SERVICE_LIST) {
            List<String> providerIps = abstractRegister.getProviderIps(providerUrl.getServiceName());
            for (String providerIp: providerIps) {
                try {
                    ConnectionHandler.connect(providerUrl.getServiceName(), providerIp);
                } catch (InterruptedException e) {
                    logger.error("[doConnectServer] failed to connect to the provider ", e);
                }
            }
            URL url = new URL();
            url.addParameter("servicePath", providerUrl.getServiceName() + "/provider");
            url.addParameter("providerIps", String.join(",", providerIps));
            abstractRegister.doAfterSubscribe(url);
        }
    }

    public void startClient() {
        Thread asyncSendJob = new Thread(new AsyncSendJob());
        asyncSendJob.start();
    }

    class AsyncSendJob implements Runnable {

        public AsyncSendJob() {}

        @Override
        public void run() {
            while (true) {
                try {
                    RpcInvocation rpcInvocation = SEND_QUEUE.take();
                    ChannelFuture channelFuture = ConnectionHandler.getChannelFuture(rpcInvocation);
                    if (channelFuture != null) {
                        RpcProtocol rpcProtocol = new RpcProtocol(CLIENT_SERIALIZE_FACTORY.serialize(rpcInvocation));
                        channelFuture.channel().writeAndFlush(rpcProtocol);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Throwable {
        Client client = new Client();
        RpcReference rpcReference = client.initClientApplication();

        client.doSubscribeService(OrderService.class);
        client.doSubscribeService(UserService.class);
        ConnectionHandler.setBootstrap(client.getBootstrap());
        client.doConnectServer();
        client.startClient();

        RpcReferenceWrapper<OrderService> rpcReferenceOrderServiceWrapper = new RpcReferenceWrapper<>();
        rpcReferenceOrderServiceWrapper.setAimClass(OrderService.class);
        rpcReferenceOrderServiceWrapper.setGroup("dev");
        rpcReferenceOrderServiceWrapper.setServiceToken("token-picea");
        rpcReferenceOrderServiceWrapper.setRetry(0);
        OrderService orderService = rpcReference.get(rpcReferenceOrderServiceWrapper);

        for (int i = 0; i < 5; i++) {
            try {
                String orderId = orderService.placeOrder("test-order");
                System.out.println(i + ": " + orderId);
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String order = orderService.getOrder("test-order-id");
        System.out.println(order);

        RpcReferenceWrapper<UserService> rpcReferenceUserServiceWrapper = new RpcReferenceWrapper<>();
        rpcReferenceUserServiceWrapper.setAimClass(UserService.class);
        rpcReferenceUserServiceWrapper.setGroup("dev");
        rpcReferenceUserServiceWrapper.setServiceToken("token-abies");
        UserService userService = rpcReference.get(rpcReferenceUserServiceWrapper);

        List<String> users = userService.getUsers();
        System.out.println(users);

        System.out.println("Done");
    }
}
