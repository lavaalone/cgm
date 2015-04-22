package com.vng.netty;

import java.util.concurrent.TimeUnit;
import javax.net.ssl.*;

import static org.jboss.netty.channel.Channels.*;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.timeout.*;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.util.HashedWheelTimer;

import com.vng.skygarden._gen_.*;

public class ServerPipelineFactory implements ChannelPipelineFactory 
{
	private final HashedWheelTimer _timer;
	
	ServerPipelineFactory ()
	{
		_timer = new HashedWheelTimer();
	}
	
	public ChannelPipeline getPipeline() throws Exception
	{
		ChannelPipeline pipeline = pipeline();
		
		if (ProjectConfig.USE_SSLSOCKET)
		{
			SSLEngine engine = SslContextFactory.getSslContext().createSSLEngine();
			engine.setUseClientMode(false);
			pipeline.addLast("ssl", new SslHandler(engine));
		}
		
		pipeline.addLast("decoder", new ServerDecoder());
		pipeline.addLast("encoder", new ServerEncoder());
		// pipeline.addLast("timeout_login", new ReadTimeoutHandler(_timer, 30));
		pipeline.addLast("handler", new ServerHandler());
		
		return pipeline;
	}
}