package org.syh.prj.rpc.simplerpc.core.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.syh.prj.rpc.simplerpc.core.common.config.ClientConfig;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcDecoder;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcEncoder;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcProtocol;
import org.syh.prj.rpc.simplerpc.core.proxy.javassit.JavassitProxyFactory;
import org.syh.prj.rpc.simplerpc.core.proxy.jdk.JDKProxyFactory;
import org.syh.prj.rpc.simplerpc.interfaces.DataService;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.SEND_QUEUE;

public class Client {
    private Logger logger = LogManager.getLogger(Client.class);

    public static EventLoopGroup clientGroup;

    private ClientConfig clientConfig;

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public RpcReference startClientApplication() throws InterruptedException {
        clientGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RpcEncoder());
                ch.pipeline().addLast(new RpcDecoder());
                ch.pipeline().addLast(new ClientHandler());
            }
        });

        ChannelFuture channelFuture = bootstrap.connect(clientConfig.getServerAddr(), clientConfig.getPort()).sync();
        this.startClient(channelFuture);

        RpcReference rpcReference;
        if (System.getProperty("proxy", "jdk").equals("javassit")) {
            logger.info("Use Javassit proxy");
            rpcReference = new RpcReference(new JavassitProxyFactory());
        } else {
            logger.info("Use JDK proxy");
            rpcReference = new RpcReference(new JDKProxyFactory());
        }

        return rpcReference;
    }

    private void startClient(ChannelFuture channelFuture) {
        Thread asyncSendJob = new Thread(new AsyncSendJob(channelFuture));
        asyncSendJob.start();
    }

    class AsyncSendJob implements Runnable {

        private ChannelFuture channelFuture;
        private ObjectMapper mapper = new ObjectMapper();

        public AsyncSendJob(ChannelFuture channelFuture) {
            this.channelFuture = channelFuture;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    RpcInvocation data = SEND_QUEUE.take();
                    String json = mapper.writeValueAsString(data);
                    RpcProtocol rpcProtocol = new RpcProtocol(json.getBytes());

                    channelFuture.channel().writeAndFlush(rpcProtocol);
                } catch (InterruptedException | JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Throwable {
        Client client = new Client();

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setPort(9090);
        clientConfig.setServerAddr("localhost");
        client.setClientConfig(clientConfig);

        RpcReference rpcReference = client.startClientApplication();

        DataService dataService = rpcReference.get(DataService.class);
        for (int i = 0; i < 100; i++) {
            String result = dataService.sendData(String.format("%s-%d", "test", i));
            System.out.println(String.format("%d: %s", i, result));
        }
        System.out.println(dataService.getList());
    }
}
