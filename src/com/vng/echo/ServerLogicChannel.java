package com.vng.echo;

import org.jboss.netty.channel.Channel;

import com.vng.util.*;
import com.vng.log.*;
import com.vng.skygarden.game.*;

public class ServerLogicChannel
{
	private Channel channel;
	
	ServerLogicChannel(Channel channel)
	{
		this.channel = channel;
	}

	public void messageExecute(byte[] data)
	{
		// LogHelper.Log(LogHelper.LogType.NEWSBOARD, "begin command");
		
		FBEncrypt encrypt = new FBEncrypt(data);
		// encrypt.displayDataPackage();
		
		
		int _command_id = encrypt.getShort(KeyID.KEY_USER_COMMAND_ID);
		
		switch (_command_id)
		{
			case CommandID.CMD_ADS_ADD:
				// LogHelper.Log(LogHelper.LogType.NEWSBOARD, "CMD_ADS_ADD");
				
				String newsboard_key = encrypt.getString(KeyID.KEY_ADS_KEY);
				byte[] item_bin = encrypt.getBinary(KeyID.KEY_ADS_ITEM);
				PrivateShopItem newsboard_item = new PrivateShopItem(item_bin);
				
				ServerNewsBoard.s_serverNewsBoardManager.add(newsboard_key, newsboard_item);
				break;
				
			case CommandID.CMD_ADS_REMOVE:
				// LogHelper.Log(LogHelper.LogType.NEWSBOARD, "CMD_ADS_REMOVE");
				
				newsboard_key = encrypt.getString(KeyID.KEY_ADS_KEY);
				ServerNewsBoard.s_serverNewsBoardManager.remove(newsboard_key);
				break;
			
			case CommandID.CMD_REFRESH_NEWS_BOARD:
				// LogHelper.Log(LogHelper.LogType.NEWSBOARD, "CMD_REFRESH_NEWS_BOARD");
				
				String _device_id = encrypt.getString(KeyID.KEY_DEVICE_ID);
				Short _user_level = encrypt.getShort(KeyID.KEY_USER_LEVEL);
				
				FBEncrypt response = new FBEncrypt();
				response.addShort(KeyID.KEY_USER_COMMAND_ID, CommandID.CMD_REFRESH_NEWS_BOARD);
				response.addString(KeyID.KEY_DEVICE_ID, _device_id);
				response.addBinary(KeyID.KEY_ADS_LIST, ServerNewsBoard.s_serverNewsBoardManager.getNewsBoardItems(_device_id, _user_level));
				
				this.messageSend(response.toByteArray());
				break;
			
			default:
				break;
		}
		
		// LogHelper.Log(LogHelper.LogType.NEWSBOARD, "end command");
	}

	public void messageSend(byte[] data)
	{
		try
		{
			channel.write(data);
		}
		catch (Exception e)
		{
			LogHelper.Log(LogHelper.LogType.NEWSBOARD, "" + e);
		}
	}
}