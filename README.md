### 额
dubbo浅尝辄止，后期深入学习还会继续跟进写记事本。因为我司又要自己搞个消息队列中间件。和骨灰级玩家，结对编程啪提呢三人一起组队。初期决定基于netty封装，所以菜鸡的我还是决定笨鸟先飞一手，没错，我学新东西还是喜欢边动手边学，不然一直学理论我真的觉得慌张。领导说离职他不反对，但是做这个中间件是对自己的一个提升，也是简历里漂亮的一笔。我该如何抉择······
和上一篇一样，这仅仅是个叙事文，希望未来我能进步，变成议论文，散文，诗歌。

### Netty简介
基于NIO非阻塞。。。。[Netty介绍很详细的那种](https://www.zhihu.com/question/24322387)

### 第一步：自定义协议
   小白的我第一次听见这样的词语真的是觉得NB，V5，但当你渐渐对网路传输有一点点启蒙你就会发现协议不过是规则，它本身并不神秘，厉害在于它用小小的规则徜徉在网络的海洋里而不出错。HTTP发展至今才发展到2版本，但是它的官方API有成百上千页。消息在网络间传输，用的是二进制，协议本身就是让一堆杂乱无章的01变得有意义。电脑太智障，你必须告诉他0~4这几位是什么意思，5~9这几位是什么意思，这就是协议。而netty简化了我们创建Socket，使用NIO的过程。必开了一些晦涩难懂的底层概念。
  今天要设计的协议叫Luck协议（很多教程上都叫这个）
  因为我们中间件本意是为了传输文件，实现断点续传。所以字段名字hhh
##### header
```java
    /**
    * 消息开头信息
    */
    private int headerData = ConstantValue.HEAD_DATA;

    /**
     * 消息体长度
     */
    private int contentLength;

    /**
     * 文件名长度
     */
    private byte nameLength;

    /**
     * 文件名
     */
    private String fileName;
```
##### 传输体
```java
    /**
     * header
     */
    private LuckHeader luckHeader;
    /**
     * 文件二进制
     */
    private byte[] content;

```
协议就定好了，还是一个可变长的头部呢厉害哦。
### 第二步：定义编解码器
##### 编码器LuckEncoder
这个就简单了按自己定义的协议意义一样一样的write进ByteBuf里
```java
public class LuckEncoder extends MessageToByteEncoder<LuckMessage> {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final LuckMessage msg, final ByteBuf out) throws Exception {
        out.writeInt(msg.getLuckHeader().getHeaderData());
        out.writeInt(msg.getLuckHeader().getContentLength());
        out.writeByte(msg.getLuckHeader().getNameLength());
        if (msg.getLuckHeader().getNameLength() > 0){
            out.writeBytes(msg.getLuckHeader().getFileName().getBytes());
        }
        out.writeBytes(msg.getContent());
    }
}
```
##### 解码器LuckDecoder
这个就比较复杂了，要考虑到粘包断包的问题，其实也不是很复杂，就是严格的根据你定义的协议一点点的去解析，唯一要注意的就是你的每一次read操作都会导致readerIndex的后移，控制好readerIndex就不会有粘包断包的问题
```java
  public class LuckDecoder extends ByteToMessageDecoder {

    public final int BASE_LENGTH = 4 + 4 + 1;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= BASE_LENGTH) {
//            if(in.readableBytes()>2048){
//                in.skipBytes(in.readableBytes());
//            }
            int beginReader;
            while (true) {
                beginReader = in.readerIndex();
                in.markReaderIndex();
                if (in.readInt() == ConstantValue.HEAD_DATA) {
                    break;
                }
                in.resetReaderIndex();
                in.readByte();
                if (in.readableBytes() < BASE_LENGTH) {
                    return;
                }
            }
            int contentLength = in.readInt();

            byte nameLength = in.readByte();

            if(in.readableBytes()<contentLength+nameLength){
                in.resetReaderIndex();
                return;
            }
            byte[] content = new byte[contentLength];

            byte[] fileName = new byte[nameLength];

            if (nameLength > 0) {
                in.readBytes(fileName);
            }
            in.readBytes(content);
            LuckMessage data = nameLength > 0 ?
                    new LuckMessage(new LuckHeader(contentLength, nameLength, new String(fileName)), content)
                    : new LuckMessage(new LuckHeader(contentLength), content);
            out.add(data);
        }
    }
}
```
### 第三步：定义InboundHandler、OutboundHandler
这一部分就算是业务的过滤器了，编解码后做一些业务处理
##### InboundHandler
```java
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
```
##### OutboundHandler
```java
public class LuckOutboundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("client发送消息："+((LuckMessage)msg).toString());
        super.write(ctx,msg,promise);
    }
}
```
### 第四步：创建LuckInitializer
其实就是在channel的pipeline中添加一层层的handler
```java
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
```
### 第五步：就可以创建server和client发消息啦
server
```java
public class Server {

    private static final int PORT = 8888;
    public static void main(String[] args) throws InterruptedException  {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 指定socket的一些属性
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)  // 指定是一个NIO连接通道
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new LuckInitializer());

            // 绑定对应的端口号,并启动开始监听端口上的连接
            Channel ch = serverBootstrap.bind(PORT).sync().channel();

            System.out.printf("luck协议启动地址：127.0.0.1:%d/\n", PORT);

            // 等待关闭,同步端口
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
```
client
```java
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
```

[参考：感谢这位作者让我少走弯路](http://www.cnblogs.com/whthomas/p/netty-custom-protocol.html)