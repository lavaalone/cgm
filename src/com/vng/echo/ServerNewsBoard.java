package com.vng.echo;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.vng.log.*;
import com.vng.taskqueue.*;
import com.vng.skygarden.game.NewsBoardManager;

public class ServerNewsBoard
{
	public static NewsBoardManager s_serverNewsBoardManager;
	
	public ServerNewsBoard(String host, int port)
	{
		ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		
		ChannelPipeline pipeline = bootstrap.getPipeline();
		pipeline.addLast("decoder", new EchoDecoder());
		pipeline.addLast("encoder", new EchoEncoder());
		pipeline.addLast("handler", new ServerNewsBoardHandler());

		bootstrap.bind(new InetSocketAddress(host, port));
		
		LogHelper.Log("ServerNewsBoard at [" + host + ":" + port + "] ready to use......\n");
		
		// -----------------------------------------------------------------------------------------------------
		s_serverNewsBoardManager = new NewsBoardManager();

		new TaskControl().executeTaskFor3Hour();
	}
}