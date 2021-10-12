
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

public class ServerInitializer extends ChannelInitializer<Channel> {

	private static final String WEBSOCKET_PATH = "/websocket";

	private final SslContext sslCtx;

	public ServerInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}

	@Override
	protected void initChannel(Channel ch) {

		ChannelPipeline pipeline = ch.pipeline();

		if (sslCtx != null) {
			
			SslHandler handler = sslCtx.newHandler(ch.alloc());
			handler.engine().setEnabledProtocols(new String[] {"TLSv1.2"});
			//pipeline.addLast(sslCtx.newHandler(ch.alloc()));
			pipeline.addLast(handler);
		}

		pipeline.addLast(new HttpServerCodec());

		pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));

		pipeline.addLast(new WebSocketServerCompressionHandler());

		pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));

		pipeline.addLast(new WebSocketIndexPageHandler(WEBSOCKET_PATH));

		pipeline.addLast(new WebSocketFrameHandler());

	}

}
