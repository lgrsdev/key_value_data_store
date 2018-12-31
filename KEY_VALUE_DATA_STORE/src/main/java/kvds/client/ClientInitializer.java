package kvds.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {
 
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(
				new JsonObjectDecoder(), 
				new ClientJsonCodec(), 
				new StringEncoder(),
				new ClientHandler());
	}
}
