package handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import protocol.LuckMessage;

public class LuckInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            LuckMessage message=(LuckMessage)msg;
            System.out.println("server接受的信息为"+message.toString());
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
