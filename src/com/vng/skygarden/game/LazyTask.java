package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.taskqueue.*;

public class LazyTask extends Task
{
	Client		_client;
	
	public LazyTask(Client	client)
	{
		super();
		
		_client = client;
	}
	
	@Override
	protected void HandleTask()
	{
		try
		{
			Thread.sleep(1000);
		}
		catch (Exception ex)
		{
		}
		
		//write response
		FBEncrypt encode = new FBEncrypt();
		
		encode.addByte("code", ReturnCode.RESPONSE_OK);
		encode.addShort("cmd", 100);
		encode.addStringANSI("txt", "Sky Garden");
		encode.addByte("byte", 0);
		encode.addShort("short", 321);
		encode.addInt("int", 654);
		encode.addLong("long", 987);
		encode.addFloat("float", 0.456f);
		
		try
		{
			_client.WriteRaw(encode.toByteArray());
		}
		catch (Exception ex)
		{
		}
	}
}