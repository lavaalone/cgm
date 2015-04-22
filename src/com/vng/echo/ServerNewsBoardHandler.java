package com.vng.echo;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import org.jboss.netty.channel.ChannelStateEvent;
import java.net.InetSocketAddress;

import com.vng.log.*;

public class ServerNewsBoardHandler extends SimpleChannelUpstreamHandler
{
	private ServerLogicChannel client = null;
	
	@Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
	{
		LogHelper.Log(LogHelper.LogType.NEWSBOARD, "connected to ServerLogic");
    }
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
		LogHelper.Log(LogHelper.LogType.NEWSBOARD, "-------------------------------------- ServerNewsBoardHandler channelDisconnected");
	}
	
	@Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
	{
		// LogHelper.Log(LogHelper.LogType.NEWSBOARD, "\n\n---------------------- messageReceived ServerNewsBoardHandler ---------------------------");
		
		try
		{
			Object msg = e.getMessage();
			byte[] data = (byte[])msg;

			if (client == null)
			{
				client = new ServerLogicChannel(e.getChannel());
			}
			
			client.messageExecute(data);
		}
		catch (Exception ex)
		{
			LogHelper.Log(LogHelper.LogType.NEWSBOARD, "\n\n---------------------- messageReceived ServerNewsBoardHandler ERROR: " + ex);
		}
    }
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
	{
		Throwable cause = e.getCause();		
		if (cause != null)
		{
			LogHelper.LogException("ServerNewsBoardHandler exceptionCaught", cause);
			LogHelper.Log("*** " + cause.getMessage() + " ***");
			LogHelper.Log(LogHelper.LogType.NEWSBOARD, "*** " + cause.getMessage() + " ***");
		}
		
        e.getChannel().close();
    }
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
		client = null;
		LogHelper.Log(LogHelper.LogType.NEWSBOARD, "ServerNewsBoardHandler channelClosed");
	}
}