package com.vng.nettyhttp;

import java.util.concurrent.TimeUnit;
import javax.net.ssl.*;

import static org.jboss.netty.channel.Channels.*;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.util.HashedWheelTimer;

import com.vng.skygarden._gen_.*;

public class ServerPipelineFactory implements ChannelPipelineFactory 
{
	private static final int	_MAX_HTTP_SIZE = 32000;
	private static final int	_SERVER_TIMEOUT_HTTP_READ = 4000;
	private static final int	_SERVER_TIMEOUT_HTTP_WRITE = 6000;
	
	private final HashedWheelTimer _timer;
	
	ServerPipelineFactory ()
	{
		_timer = new HashedWheelTimer();
	}
	
	public ChannelPipeline getPipeline() throws Exception
	{
		ChannelPipeline pipeline = pipeline();
		
		/*
                if (ProjectConfig.USE_HTTPS)
		{
			SSLEngine engine = SslContextFactory.getSslContext().createSSLEngine();
			engine.setUseClientMode(false);
			pipeline.addLast("ssl", new SslHandler(engine));
		}
                      */
		
		pipeline.addLast("decoder", new HttpRequestDecoder(_MAX_HTTP_SIZE, _MAX_HTTP_SIZE, _MAX_HTTP_SIZE));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("timeout", new IdleStateHandler(_timer, _SERVER_TIMEOUT_HTTP_READ, _SERVER_TIMEOUT_HTTP_WRITE, 0, TimeUnit.MILLISECONDS));
		pipeline.addLast("handler", new ServerHandler());
		
		return pipeline;
	}
}

