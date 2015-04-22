package com.vng.echo;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.vng.log.*;

public class ServerLogic
{
	public ServerLogic(String host, int port)
	{
		ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		
		ChannelPipeline pipeline = bootstrap.getPipeline();
		pipeline.addLast("decoder", new EchoDecoder());
		pipeline.addLast("encoder", new EchoEncoder());
		pipeline.addLast("handler", new ServerLogicHandler());
		
		bootstrap.connect(new InetSocketAddress(host, port));
		
		LogHelper.Log("ServerLogic at [" + host + ":" + port + "] ready to use......\n");
	}
}