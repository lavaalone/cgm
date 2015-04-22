package com.vng.netty;

import java.nio.ByteBuffer;
import java.util.*;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.*;

import org.apache.log4j.Logger;

import com.vng.util.*;
import com.vng.log.*;
import com.vng.skygarden.*;
import com.vng.skygarden._gen_.ProjectConfig;
import com.vng.skygarden.game.*;

public class Client
{
	private Channel 		_channel;
	private String			_address;
	
	SkyGardenUser 			_user;
	
	long					_data_in;
	long					_data_out;
	
	Client(Channel c, String address)
	{	
		_channel = c;
		_address = address;
		
		_user = new SkyGardenUser(this, address);
		
		_data_in = 0;
		_data_out = 0;
	}
	
	public SkyGardenUser GetUserInstance()
	{
		return _user;
	}
	
	public void MessageReceived(byte[] request_content) throws Exception
	{
		if (ProjectConfig.IS_SERVER_FREESTYLE == 1) {
			LogHelper.Log("###Client.MessageReceived.. length = " + request_content.length);
		}
		
		//handle client data
		_user.MessageReceived(request_content);
	}
	
	public long getDataIn()
	{
		return (long)Math.ceil(_data_in/1024);
	}

	public long getDataOut()
	{
		return (long)Math.ceil(_data_out/1024);
	}

	public long getSessionDataTraffic()
	{
		return (long)Math.ceil((_data_in + _data_out) / 1024);
	}
	
	public void WriteZip(byte[] data) throws Exception
	{
		//compress data before respond to client
		byte[] compressed_data = Misc.CompressGZIP(data);
		
		
		_data_out += compressed_data.length;
		
		if (ProjectConfig.IS_SERVER_FREESTYLE == 1) {
			LogHelper.Log("###Client.WriteZip.. length = " + compressed_data.length);
		}
		
		_channel.write(new SimpleDataFormat(compressed_data, true));
	}
	
	public void WriteRaw(byte[] data) throws Exception
	{		
		_data_out += data.length;
		
		_channel.write(new SimpleDataFormat(data, false));
	}
	
	public void CloseChannel()
	{
		// LogHelper.Log("Client.CloseChannel");
		
		_channel.disconnect();
	}
}
