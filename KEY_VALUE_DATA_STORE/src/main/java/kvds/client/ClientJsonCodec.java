package kvds.client;

import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import kvds.common.model.Request;
import kvds.common.model.Response;

public class ClientJsonCodec extends ChannelDuplexHandler {

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
		if (message instanceof ByteBuf) {
			Charset charset = Charset.defaultCharset();
			message = this.mapper.readValue(((ByteBuf) message).toString(charset), Response.class);
		}
		super.channelRead(context, message);
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (msg instanceof Request) {
			byte[] buff = mapper.writeValueAsBytes(msg);
			ByteBuf bb = ctx.alloc().buffer(buff.length);
			bb.writeBytes(buff);
			msg = bb;
		}
		super.write(ctx, msg, promise);
	}
}
