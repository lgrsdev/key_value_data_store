package kvds.server;

import java.util.concurrent.Executors;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
 
	public static void main(String[] args) throws Exception {
		 
		// Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup(1,Executors.newCachedThreadPool());
		EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), Executors.newCachedThreadPool());

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup) //
					.channel(NioServerSocketChannel.class)
					.localAddress(8322)
					.option(ChannelOption.SO_BACKLOG, 100) 
					.childOption(ChannelOption.TCP_NODELAY, true) 
					.childHandler(new ServerInitializer());
					//.handler(new LoggingHandler(LogLevel.INFO))

			// Start the server.
			ChannelFuture f = b.bind().sync();

			// Wait until the server socket is closed.
			f.channel().closeFuture().sync();
		} finally {
			// Shut down all event loops to terminate all threads.
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();

			// Wait until all threads are terminated.
			bossGroup.terminationFuture().sync();
			workerGroup.terminationFuture().sync();
		}
	}
}
