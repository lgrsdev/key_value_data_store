package kvds.server;

import java.io.File;

import io.netty.channel.ChannelHandler.Sharable;
import kvds.common.model.Request;
import kvds.common.model.Response;
import kvds.server.ds.DataStoreService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class ServerHandler extends SimpleChannelInboundHandler<Request> {

	private DataStoreService service;

	public ServerHandler() {
		service = new DataStoreService();
	}

	@Override 
	protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
		System.out.println("Server got Request from client - " + msg.getCommand());
		String response = "***** " + msg.getCommand().name() + ": Success" + " *****";
		switch (msg.getCommand()) {
		case RIGHT_ADD:
			service.rightAdd(msg.getKey(), msg.getValues().getFirst());
			break;
		case LEFT_ADD:
			service.leftAdd(msg.getKey(), msg.getValues().getFirst());
			break;
		case GET:
			response = response + "\nvalues = " + service.get(msg.getKey()).toString();
			break;
		case SET:
			service.set(msg.getKey(), msg.getValues());
			break;
		case GET_ALL_KEYS:
			response = response + "\nkeys = " + service.getAllKeys(msg.getKey());
			break;
		}

		Response res = new Response();
		res.setResponseValue(response);
		ctx.writeAndFlush(res);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		new File("data").mkdirs(); // create the data store directory in project root folder
	};

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
	}

}
