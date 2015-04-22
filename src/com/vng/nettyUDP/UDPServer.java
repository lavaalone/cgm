package com.vng.nettyUDP;

import java.net.InetSocketAddress;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.FixedReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.util.CharsetUtil;

import com.vng.log.*;

public class UDPServer
{
	public final String m_ip;
	public final int m_port;

	public UDPServer(String ip, int port)
	{
		m_ip = ip;
		m_port = port;

		DatagramChannelFactory f = new NioDatagramChannelFactory();
		ConnectionlessBootstrap bootstrap = new ConnectionlessBootstrap(f);
		
		// Configure the pipeline factory.
		ChannelPipeline pipeline = bootstrap.getPipeline();
		pipeline.addLast("handler", new UDPServerHandler());
		
		// Enable broadcast
		// bootstrap.setOption("broadcast", "false");
		// bootstrap.setOption("receiveBufferSizePredictorFactory", new FixedReceiveBufferSizePredictorFactory(1024));

		// Bind to the port and start the service.
		bootstrap.bind(new InetSocketAddress(ip, port));
	}
}
