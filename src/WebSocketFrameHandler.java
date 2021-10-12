import java.util.Locale;
import java.util.logging.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

	private final static Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
		// TODO Auto-generated method stub

		if (msg instanceof TextWebSocketFrame) {
			// Send the upper case string back.

			String request = ((TextWebSocketFrame) msg).text();
			log.info("{} received {}" + ctx.channel() + request);

			ctx.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase(Locale.US)));

		} else {
			String message = "unsupported frame type: " + msg.getClass().getName();
			throw new UnsupportedOperationException(message);
		}
	}

}
