package coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import protocol.ConstantValue;
import protocol.LuckHeader;
import protocol.LuckMessage;

import java.util.List;

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
