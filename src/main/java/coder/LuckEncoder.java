package coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import protocol.LuckMessage;

public class LuckEncoder extends MessageToByteEncoder<LuckMessage> {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final LuckMessage msg, final ByteBuf out) throws Exception {
        out.writeInt(msg.getLuckHeader().getHeaderData());
        out.writeInt(msg.getLuckHeader().getContentLength());
//        out.writeBytes(msg.getLuckHeader().getSessionId().getBytes());
        out.writeByte(msg.getLuckHeader().getNameLength());
        if (msg.getLuckHeader().getNameLength() > 0){
            out.writeBytes(msg.getLuckHeader().getFileName().getBytes());
        }
        out.writeBytes(msg.getContent());
    }
}
