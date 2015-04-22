/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.taskqueue.*;
import com.vng.log.LogHelper;
import java.util.Map;

public class BroadcastTask extends Task
{
	private Client		_client;
	private FBEncrypt	_encrypt;
	
	private int _result;
	
	private String content = "";
	private int repeat_times = -1;
	private int duration = -1;
	
	public BroadcastTask(Client client, FBEncrypt encrypt)
	{
		super();
		
		_client = client;
		_encrypt = encrypt;
		_result = -1;
	}
	
	@Override
	protected void HandleTask() 
	{
		content = _encrypt.getString(KeyID.KEY_BROADCAST_CONTENT);
		repeat_times = _encrypt.getInt(KeyID.KEY_BROADCAST_REPEAT_TIMES);
		duration = _encrypt.getInt(KeyID.KEY_BROADCAST_DURATION);
		
		int start_broadcast_time = Misc.SECONDS();
		int end_broadcast_time = start_broadcast_time + duration * 60;
		
		if (content.equals("") || content.length() == 0)
		{
			LogHelper.Log("ServerBroadcast.. err! invalid content");
			return;
		}
		
		if (repeat_times < 0 || repeat_times > 200)
		{
			LogHelper.Log("ServerBroadcast.. err! invalid repeat times");
			return;
		}
		
		if (duration < 0 || duration > 10800)
		{
			LogHelper.Log("ServerBroadcast.. err! invalid duration");
			return;
		}
		
		// add to current broadcast list
		StringBuilder s = new StringBuilder();
		s.append(content);
		s.append(":").append(repeat_times);
		s.append(":").append(start_broadcast_time);
		s.append(":").append(end_broadcast_time);
		Server.s_broadcast_list.put(Server.s_broadcast_list.size(), s.toString());
		
		// log
		LogHelper.Log("BroadcastTask.. add new content: " + s.toString());
		
		// push to online user
		for (Map.Entry<String,SkyGardenUser> e : Server.s_serverUserOnline.entrySet())
		{
			SkyGardenUser u = e.getValue();
			if (u != null && u.getClient() != null)
			{
				FBEncrypt header = new FBEncrypt();
				header.addShort(KeyID.KEY_USER_COMMAND_ID, CommandID.CMD_BROADCAST);
				header.addByte(KeyID.KEY_USER_REQUEST_STATUS, ReturnCode.RESPONSE_OK);
				header.addLong(KeyID.KEY_USER_ID, u.GetUserID());
				header.addLong(KeyID.KEY_USER_REQUEST_ID, u.getRequestID());
				header.addStringANSI(KeyID.KEY_USER_SESSION_ID, u.getSessionID());
				
				FBEncrypt res = new FBEncrypt();
				res.addBinary(KeyID.KEY_REQUEST_STATUS, header.toByteArray());
				res.addString(KeyID.KEY_BROADCAST_CONTENT_LIST, /*(s.toString()*/content);
				
				try
				{
					u.getClient().WriteZip(res.toByteArray());
					LogHelper.Log("ServerBroadcast.. push content OK.");
				}
				catch (Exception ex) 
				{
					LogHelper.LogException("ServerBroadcast", ex);
				}
			}
		}
	}
}
