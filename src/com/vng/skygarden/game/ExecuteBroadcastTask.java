/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.taskqueue.*;
import com.vng.log.LogHelper;
import com.vng.skygarden.SkyGarden;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ExecuteBroadcastTask extends Task
{
	private final int MAX_SIZE = 15;
	public ExecuteBroadcastTask()
	{
		super();
	}
	
	@Override
	protected void HandleTask() 
	{
		LogHelper.LogHappy("Execute Broadcast Task.. content list size := " + SkyGarden.BroadcastList().size());
		List<String> contents = new LinkedList<String>();
		for (Map.Entry<Integer, String> content : SkyGarden.BroadcastList().entrySet()) {
			if (contents.size() < MAX_SIZE) {
				contents.add(content.getValue().split(":")[0]);
				Server.s_broadcast_list.remove(content.getKey());
			} else {
				break;
			}
		}
		
		// push to online user
		for (Map.Entry<String,SkyGardenUser> e : Server.s_serverUserOnline.entrySet()) {
			SkyGardenUser u = e.getValue();
			if (u != null && u.getClient() != null) {
				FBEncrypt header = new FBEncrypt();
				header.addShort(KeyID.KEY_USER_COMMAND_ID, CommandID.CMD_BROADCAST);
				header.addByte(KeyID.KEY_USER_REQUEST_STATUS, ReturnCode.RESPONSE_OK);
				header.addLong(KeyID.KEY_USER_ID, u.GetUserID());
				header.addLong(KeyID.KEY_USER_REQUEST_ID, u.getRequestID());
				header.addStringANSI(KeyID.KEY_USER_SESSION_ID, u.getSessionID());
				
				FBEncrypt res = new FBEncrypt();
				res.addBinary(KeyID.KEY_REQUEST_STATUS, header.toByteArray());
				res.addStringArray(KeyID.KEY_BROADCAST_CONTENT_LIST, contents);
				
				try {
					u.getClient().WriteZip(res.toByteArray());
				} catch (Exception ex) {
					LogHelper.LogException("ServerBroadcast", ex);
				}
			}
		}
		
		LogHelper.LogHappy("Execute Broadcast Task.. done.");
	}
}
