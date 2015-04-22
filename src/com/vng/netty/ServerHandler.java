package com.vng.netty;

import java.util.*;
import java.util.Map.Entry;
import java.net.InetSocketAddress;
import java.net.URLDecoder;


import org.jboss.netty.channel.*;
import org.jboss.netty.buffer.*;

import org.jboss.netty.handler.timeout.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.codec.http.multipart.*;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import org.jboss.netty.handler.ssl.SslHandler;

import org.apache.log4j.Logger;

import com.vng.util.*;
import com.vng.log.*;
import com.vng.skygarden._gen_.*;
import com.vng.skygarden.DBConnector;
import com.vng.db.*;
import com.vng.skygarden.SkyGarden;
import com.vng.skygarden.game.*;

public class ServerHandler extends SimpleChannelUpstreamHandler
{
	private static final HttpDataFactory 	_data_factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed MINSIZE
	
	private long 					_connected_time;
	
	private boolean					_is_closed;
	
	private Client					_client = null;
	protected String 				_address;
	protected String 				_real_address;
	
	
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
//		LogHelper.Log("channelOpen.. concurrent connection = " + Monitor.GetConcurrentConnection());
		_connected_time = System.currentTimeMillis();
		
		Monitor.IncrConnection();
	}	
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
//		LogHelper.Log("channelClosed.. concurrent connection = " + Monitor.GetConcurrentConnection());
		_client = null;
		
		Monitor.DecrConnection((int)(System.currentTimeMillis() - _connected_time));
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
		if (ProjectConfig.USE_SSLSOCKET)
		{
			// Get the SslHandler in the current pipeline.
			// We added it in ServerPipelineFactory.
			final SslHandler ssl_handler = (SslHandler)(ctx.getPipeline().get("ssl"));

			// Get notified when SSL handshake is done.
			ChannelFuture handshakeFuture = ssl_handler.handshake();
			handshakeFuture.addListener(new Greeter());
		}
		
		_address = _real_address = ((InetSocketAddress) e.getChannel().getRemoteAddress()).getAddress().getHostAddress();		
//		LogHelper.Log("channelConnected.. [" + _address + "] concurrent connection = " + Monitor.GetConcurrentConnection());
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
//		LogHelper.Log("channelDisconnected.. concurrent connection = " + Monitor.GetConcurrentConnection());
		
		if (_client != null && _client._user != null)
		{
			if (_client._user.userInfo != null && _client._user._is_login) // avoid throwing exception at running broadcast task or restarting server
			{
				removeUser(_client._user.GetUserDeviceID(), ReturnCode.RESPONSE_FORCE_QUIT);
			}
		}
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
//		LogHelper.Log("\n\n--------------------------- Received message from client ---------------------------");
		
		Object msg = e.getMessage();
		
		if (msg == null)
		{
			return;
		}
		
		Channel channel = e.getChannel();		
		SimpleDataFormat sdf = (SimpleDataFormat)msg;
		
		byte[] data = sdf.GetData();
		byte[] request_content = null;
		
		if (sdf.IsUseGzip())
		{
			request_content = Misc.DecompressGZIP(data);
		}
		else
		{
			request_content = data;
		}
		
		//run game logic
		if (_client == null)
		{
			_client = new Client(channel, _address);

			//handle login cmd
			_client.MessageReceived(request_content);
		}
		else
		{
			_client.MessageReceived(request_content);
		}
		
		_client._data_in += data.length;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
	{
		Monitor.IncrException();
		
		StringBuilder mess = new StringBuilder();
		mess.append("ServerHandler.ExceptionCaught");
		if (_client != null && _client._user != null && _client._user.userInfo != null)
		{
			mess.append("&uid=").append(_client._user.GetUserID());
			mess.append("&device_id=").append(_client._user.GetUserDeviceID());
		}
		
		Throwable cause = e.getCause();		
		if (cause != null)
		{
			boolean should_write_log = true;
			
			if ( 	(cause instanceof java.nio.channels.ClosedChannelException) || 
					(cause instanceof java.io.IOException && cause.getMessage() != null && 
					(cause.getMessage().equals("Connection reset by peer") || cause.getMessage().equals("Connection timed out") || cause.getMessage().equals("Broken pipe") || cause.getMessage().equals("No route to host")))
				)
			{
				should_write_log = false;
			}
			
			if (should_write_log)
			{
				LogHelper.LogException(mess.toString(), cause);
			}
		}
		
		_is_closed = true;
		e.getChannel().close();
	}
	
	public static void addUser(String _device_id, SkyGardenUser _user)
	{
		// to avoid duplicate user in map
		if (isUserOnline(_device_id))
		{
			removeUser(_device_id, ReturnCode.RESPONSE_MULTIPLE_LOGIN);
		}

		Server.s_serverUserOnline.put(_device_id, _user);
		
		Monitor.IncrUser();
	}
	
	public static void removeUser(String _device_id, int return_code)
	{
		try
		{
			if (isUserOnline(_device_id))
			{
				SkyGardenUser u = getUser(_device_id);
				if (u != null)
				{
					// sync
					u.LazySync();
					
					// cache
					if (ProjectConfig.USE_CACHE_OFFLINE)
					{
						if (Server.s_recentOnlineUser.containsKey(u.GetUserID()))
						{
							Server.s_recentOnlineUser.remove(u.GetUserID());
						}
						
						if (u.GetLoadResult()) // only cache users that loaded data succesfully
						{
							if (Server.s_recentOnlineUser.size() <= ProjectConfig.MAX_CACHE)
							{
								LogHelper.Log("PUT USER [" + u.GetUserID() +"] TO CACHED LIST");
								Server.s_recentOnlineUser.put(u.GetUserID(), u);
							}
							else
							{
								LogHelper.Log("WARNING!!! CACHED LIST IS FULL.");
							}
						}
					}
					
					// tell client to force quit
					u.forceQuit(return_code);
					
					// record log out
					LogHelper.logLogOut(u.GetUserInfo(), u.GetMoneyManager(), u.GetIP(), SkyGarden._server_id, u.getSessionID());
					
					// PIG LOG
					StringBuilder piglog = new StringBuilder();
					piglog.append(Misc.getCurrentDateTime());		//  1. thoi gian dang nhap
					piglog.append('\t').append("LogOut");
					piglog.append('\t').append(u._pig_id);
					piglog.append('\t').append("CGMFBS");
					piglog.append('\t').append(SkyGarden._server_id);
					piglog.append('\t').append(u._client_OS);
					piglog.append('\t').append(u.GetUserInfo().getID());
					piglog.append('\t').append(u.getSessionID());
					piglog.append('\t').append(u.GetUserInfo().getLevel());
					piglog.append('\t').append(u.GetMoneyManager().GetRealMoney() + u.GetMoneyManager().GetBonusMoney());
					piglog.append('\t').append(u.GetUserInfo().getExp());
					LogHelper.Log(LogHelper.LogType.PIG_LOG, piglog.toString());
					
					// record data traffic
					int play_time = (int)(System.currentTimeMillis()/1000) - u.GetUserInfo().GetCurrentLoginTime();
					LogHelper.logDataTraffic(u.GetUserInfo().getID(), u.GetUserInfo().getName(), u.GetUserInfo().getLevel(), u.GetIP(), 0, u.getSessionID(), u.getClient().getDataIn(), u.getClient().getDataOut(), u.getClient().getSessionDataTraffic(),play_time);
					
					// delete online key then close channel
					u.base.Delete(_device_id + "_" + KeyID.ONLINE);
					u.base.Delete(u.GetUserID() + "_" + KeyID.ONLINE);
					u.getClient().CloseChannel();
					
					LogHelper.Log("removeUser.. removed user with device id [" + _device_id + "] OK, remain size = " + Server.s_serverUserOnline.size());
					
					// monitor
					Monitor.DecrUser();
				}
			}
			// cause logic bug which allows multiple login.
//			else
//			{
//				LogHelper.Log("removeUser.. err! user " + _device_id + " is not online.");
//				long uid = Misc.GetUserID(_device_id);
//				DBConnector.GetMembaseServerForTemporaryData().Delete(_device_id + "_" + KeyID.ONLINE);
//				DBConnector.GetMembaseServer(uid).Delete(uid + "_" + KeyID.ONLINE);
//				LogHelper.Log("removeUser.. delete online key done.");
//			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("ServerHandler.removeUser", e);
		}
		
		Server.s_serverUserOnline.remove(_device_id);
	}
	
	public static SkyGardenUser getUser(String _device_id)
	{
		if (isUserOnline(_device_id))
		{
			return Server.s_serverUserOnline.get(_device_id);
		}
		
		return null;
	}
	
	public static byte[] GetUserData(String _device_id)
	{
		byte[] user_bin = null;
		try {
			byte[] uid_raw = DBConnector.GetMembaseServerForGeneralData().GetRaw(_device_id + "_" + KeyID.KEY_USER_ID);
			if (uid_raw != null) {
				long uid =	((uid_raw[0]&0xFF)<<56) |
							((uid_raw[1]&0xFF)<<48) |
							((uid_raw[2]&0xFF)<<40) |
							((uid_raw[3]&0xFF)<<32) |
							((uid_raw[4]&0xFF)<<24) |
							((uid_raw[5]&0xFF)<<16) |
							((uid_raw[6]&0xFF)<<8)  |
							(uid_raw[7]&0xFF);

				if (uid > 0) {
					user_bin = DBConnector.GetMembaseServer(uid).GetRaw(uid + "_" + KeyID.KEY_USER_INFOS);
				}
			}
		} catch (Exception e) {
			LogHelper.LogException("GetUserDataFromDeviceID", e);
			return null;
		}
		return user_bin;
	}
	
	public static byte[] GetUserData(long uid) 
	{
		byte[] b = null;
		try 
		{
			b = DBConnector.GetMembaseServer(uid).GetRaw(uid + "_" + KeyID.KEY_USER_INFOS);
		} 
		catch (Exception e) 
		{
			LogHelper.LogException("GetUserDataFromUID", e);
			return null;
		}
		return b;
	}

	public static boolean isUserOnline(String _device_id)
	{
		return Server.s_serverUserOnline.containsKey(_device_id);
	}

	// ------------------------------------------------------------------------------------------------------
	
	private static final class Greeter implements ChannelFutureListener 
	{
		Greeter() 
		{
		}

		public void operationComplete(ChannelFuture future) throws Exception 
		{
			if (future.isSuccess() == false) 
			{
				future.getChannel().close();
			}
		}
	}
}