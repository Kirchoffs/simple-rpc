package org.syh.prj.rpc.simplerpc.core.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

@ChannelHandler.Sharable
public class MaxConnectionLimitHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LogManager.getLogger(MaxConnectionLimitHandler.class);
    private final int maxConnectionNum;
    private final AtomicInteger numConnection = new AtomicInteger(0);
    private final Set<Channel> childChannel = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final LongAdder numDroppedConnections = new LongAdder();
    private final AtomicBoolean loggingScheduled = new AtomicBoolean(false);

    public MaxConnectionLimitHandler(int maxConnectionNum) {
        this.maxConnectionNum = maxConnectionNum;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = (Channel) msg;
        int conn = numConnection.incrementAndGet();
        if (conn <= maxConnectionNum) {
            this.childChannel.add(channel);
            channel.closeFuture().addListener(future -> {
                childChannel.remove(channel);
                numConnection.decrementAndGet();
            });
            super.channelRead(ctx, msg);
        } else {
            numConnection.decrementAndGet();
            channel.config().setOption(ChannelOption.SO_LINGER, 0);
            channel.unsafe().closeForcibly();
            numDroppedConnections.increment();
            if (loggingScheduled.compareAndSet(false, true)) {
                ctx.executor().schedule(this::writeNumDroppedConnectionLog, 1, TimeUnit.SECONDS);
            }
        }
    }

    private void writeNumDroppedConnectionLog() {
        loggingScheduled.set(false);
        final long dropped = numDroppedConnections.sumThenReset();
        if (dropped > 0) {
            logger.error(
                "Dropped {} connection(s) to protect server, maxConnection is {}",
                dropped,
                maxConnectionNum
            );
        }
    }
}
