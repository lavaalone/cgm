package com.vng.echo;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.ChannelStateEvent;

import com.vng.log.*;
import com.vng.util.*;
import com.vng.skygarden.game.*;
import com.vng.netty.*;

public class ServerLogicHandler extends SimpleChannelUpstreamHandler
{
	private static Channel channel = null;

	@Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
	{
		channel = e.getChannel();
		LogHelper.Log(LogHelper.LogType.NEWSBOARD, "connected to ServerNewsBoard"); 
    }
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
		LogHelper.Log(LogHelper.LogType.NEWSBOARD, "-------------------------------------- ServerLogicHandler channelDisconnected");
	}
	
	@Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
	{
		// LogHelper.Log(LogHelper.LogType.NEWSBOARD, "\n\n---------------------- messageReceived ServerLogicHandler ---------------------------");
		
		try
		{
			Object msg = e.getMessage();
			byte[] data = (byte[])msg;

			if (data == null || data.length == 0)
			{
				LogHelper.Log(LogHelper.LogType.NEWSBOARD, "\n\ndata = null");
			}
			
			FBEncrypt encrypt = new FBEncrypt(data);
			// encrypt.displayDataPackage();
			
			messageExecute(encrypt);
		}
		catch (Exception ex)
		{
			LogHelper.Log(LogHelper.LogType.NEWSBOARD, "\n\n---------------------- messageReceived ServerLogicHandler ERROR: " + ex);
		}
    }

	public void messageExecute(FBEncrypt encrypt)
	{
		int _command_id = encrypt.getShort(KeyID.KEY_USER_COMMAND_ID);
		
		switch (_command_id)
		{
			case CommandID.CMD_REFRESH_NEWS_BOARD:
				String _device_id = encrypt.getString(KeyID.KEY_DEVICE_ID);
				
				byte[] ads_list = encrypt.getBinary(KeyID.KEY_ADS_LIST);
				
				SkyGardenUser me = ServerHandler.getUser(_device_id);
				
				me.refreshNewsBoardToClient(ads_list);
				
				break;
			
			default:

				break;
		}
	}
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
	{
		Throwable cause = e.getCause();		
		if (cause != null)
		{
			LogHelper.LogException("ServerLogicHandler exceptionCaught", cause);
			LogHelper.Log("*** " + cause.getMessage() + " ***");
			LogHelper.Log(LogHelper.LogType.NEWSBOARD, "*** " + cause.getMessage() + " ***");
		}
		
        e.getChannel().close();
    }

	public static void messageSend(byte[] data)
	{
		try
		{
			channel.write(data);
		}
		catch (Exception e)
		{
			LogHelper.Log(LogHelper.LogType.NEWSBOARD, "*** messageSend *** " + e);
		}
	}
}