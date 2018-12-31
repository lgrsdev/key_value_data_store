package kvds.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

	private static final ServerHandler sharedServerHandler = new ServerHandler();

	@Override
	protected void initChannel(SocketChannel ch) {

		// Enable stream compression (you can remove these two if unnecessary)
		// pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
		// pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

		ch.pipeline().addLast(new JsonObjectDecoder(), new ServerJsonCodec(), sharedServerHandler);
	}
}
