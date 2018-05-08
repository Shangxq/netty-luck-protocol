package test;

import initializer.LuckInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import protocol.LuckHeader;
import protocol.LuckMessage;

public class Client {
    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new LuckInitializer());

            // Start the connection attempt.
            Channel ch = b.connect("127.0.0.1", 8888).sync().channel();

            int version = 1;
//            String sessionId = UUID.randomUUID().toString();
            String content = "I'm the luck protocol!I'm the luck protocol!" +
                    "I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!I'm the luck protocol!";
            String name = "nihao";

            LuckHeader header = new LuckHeader(content.length(), (byte) name.length(), name);
            LuckMessage message = new LuckMessage(header, content.getBytes());
            ch.write(message);
            ch.write(message);
            ch.write(message);
            ch.writeAndFlush(message);

            ch.close();

        } finally {
            group.shutdownGracefully();
        }
    }
}
