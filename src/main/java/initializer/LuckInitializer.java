package initializer;

import coder.LuckDecoder;
import coder.LuckEncoder;
import handler.LuckInboundHandler;
import handler.LuckOutboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class LuckInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline=ch.pipeline();
        pipeline.addLast(new LuckEncoder());
        pipeline.addLast(new LuckDecoder());
        pipeline.addLast(new LuckInboundHandler());
        pipeline.addLast(new LuckOutboundHandler());
    }
}
