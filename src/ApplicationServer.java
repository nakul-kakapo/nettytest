import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class ApplicationServer {

	private static final int HTTP_PORT = 8080;
	static final boolean SSL = System.getProperty("ssl") != null;
	static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8080"));

	public void run() throws Exception {

		final SslContext sslCtx;
		if (SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
//			sslCtx = SslContextBuilder.forServer(kmf).trustManager(tmf).clientAuth(ClientAuth.REQUIRE)
//					.protocols(new String[] { "TLSv1.3", "TLSv1.2" }).build();
		} else {
			sslCtx = null;
		}

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {

			// A helper class that simplifies server configuration

			ServerBootstrap httpBootstrap = new ServerBootstrap();

			// Configure the server

			httpBootstrap.group(bossGroup, workerGroup);

			httpBootstrap.channel(NioServerSocketChannel.class);

			httpBootstrap.handler(new LoggingHandler(LogLevel.INFO));

			httpBootstrap.childHandler(new ServerInitializer(sslCtx));

			httpBootstrap.option(ChannelOption.SO_BACKLOG, 128);

			httpBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

			// Bind and start to accept incoming connections.

			// ChannelFuture httpChannel = httpBootstrap.bind(HTTP_PORT).sync();

			Channel channel = httpBootstrap.bind(PORT).sync().channel();

			System.out.println(
					"Open your web browser and navigate to " + (SSL ? "https" : "http") + "://127.0.0.1:" + PORT + '/');

			// Wait until server socket is closed

			channel.closeFuture().sync();

		}

		finally {

			workerGroup.shutdownGracefully();

			bossGroup.shutdownGracefully();

		}

	}

	public static void main(String[] args) throws Exception {

		new ApplicationServer().run();

	}

}
