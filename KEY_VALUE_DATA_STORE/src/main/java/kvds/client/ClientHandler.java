package kvds.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kvds.common.model.Response;

public class ClientHandler extends SimpleChannelInboundHandler<Response> {
 
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
		System.out.println(msg.getValue());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
