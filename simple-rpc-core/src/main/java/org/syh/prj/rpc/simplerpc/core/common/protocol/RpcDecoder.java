package org.syh.prj.rpc.simplerpc.core.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static org.syh.prj.rpc.simplerpc.core.common.constants.RpcConstants.MAGIC_NUMBER;

public class RpcDecoder extends ByteToMessageDecoder {
    // Magic Number (2 bytes) + Content Length (4 bytes)
    public final int BASE_LENGTH = 2 + 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        if (byteBuf.readableBytes() >= BASE_LENGTH) {
            if (byteBuf.readShort() != MAGIC_NUMBER) {
                ctx.close();
                return;
            }

            int length = byteBuf.readInt();
            if (byteBuf.readableBytes() < length) {
                ctx.close();
                return;
            }

            byte[] body = new byte[length];
            byteBuf.readBytes(body);
            RpcProtocol rpcProtocol = new RpcProtocol(body);
            out.add(rpcProtocol);
        }
    }
}
