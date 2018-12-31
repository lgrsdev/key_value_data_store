package kvds.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client { 

	static final String HOST = System.getProperty("host", "127.0.0.1");
	static final int PORT = Integer.parseInt(System.getProperty("port", "8322"));

	public static void main(String[] args) throws Exception {

		EventLoopGroup group = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(),
				Executors.newCachedThreadPool());
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioSocketChannel.class)
			.handler(new ClientInitializer());
			// .handler(new LoggingHandler(LogLevel.INFO));

			// Start the connection attempt.
			Channel ch = b.connect(HOST, PORT).sync().channel();

			// Read commands from the stdin.
			ChannelFuture lastWriteFuture = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			for (;;) {
				String line = in.readLine();
				if (line == null)
					break;

				// Sends the received line to the server.
				lastWriteFuture = ch.writeAndFlush(line);
			}

			// Wait until all messages are flushed before closing the channel.
			if (lastWriteFuture != null)
				lastWriteFuture.sync();
		} finally {
			group.shutdownGracefully();
			group.terminationFuture().sync();
		}
	}

}
