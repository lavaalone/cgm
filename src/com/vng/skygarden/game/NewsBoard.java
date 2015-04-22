package com.vng.skygarden.game;

import com.vng.log.*;
import com.vng.netty.*;
import com.vng.util.*;
import com.vng.skygarden.game.PrivateShopItem;

import java.util.*;
import java.util.Map.Entry;

public class NewsBoard
{
	HashMap<String, PrivateShopItem> news_board;
	int ads_limit_time;
	byte ads_num;
	UserInfo _user_info;
	int _last_update_time = 0;
	
	public NewsBoard(UserInfo user_info)
	{
		news_board = new HashMap<String, PrivateShopItem>();
		ads_num = 0;
		ads_limit_time = Misc.SECONDS();
		this._user_info = user_info;
		_last_update_time = 0;
	}

	public NewsBoard(UserInfo user_info, byte[] news_board_bin)
	{
		this._user_info = user_info;
		FBEncrypt data = new FBEncrypt(news_board_bin);
		
		ads_limit_time = data.getInt(KeyID.KEY_ADS_LIMIT_TIME);
		ads_num = data.getByte(KeyID.KEY_ADS_NUM);

		news_board = new HashMap<String, PrivateShopItem>();
		
		for (int i = 0; i < ads_num; i++)
		{
			String ads_user_id = data.getString(KeyID.KEY_ADS_USER_ID + i);
			byte[] ads_user_item_bin = data.getBinary(KeyID.KEY_ADS_USER_ITEM + i);
			
			PrivateShopItem ads_user_item;
			if (ads_user_item_bin != null)
			{
				ads_user_item = new PrivateShopItem(ads_user_item_bin);
				
				news_board.put(ads_user_id, ads_user_item);
			}
			else
			{
				LogHelper.Log("Cannot load ads " + i);
				return;
			}
		}
		if (data.hasKey("last_update_time"))
		{
			_last_update_time = data.getInt("last_update_time");
		}
	}
	
	public boolean isRefreshNewsBoard()
	{
		return (Misc.SECONDS() > _last_update_time + DatabaseID.NEWS_BOARD_ADS_MAX_TIME);
	}
	
	public void updateNewsBoard(byte[] news_board_bin)
	{
		// update last update time
		if (_user_info == null)
		{
			LogHelper.Log("Newsboard.. err! null user info. Aborted update.");
			return;
		}
		
		_last_update_time = Misc.SECONDS();
		
		FBEncrypt data = new FBEncrypt(news_board_bin);
		
		ads_limit_time = Misc.SECONDS() + DatabaseID.NEWS_BOARD_ADS_MAX_TIME;
		ads_num = data.getByte(KeyID.KEY_ADS_NUM);
		LogHelper.Log("updateNesBoard.. ads num = " + ads_num);

		news_board = new HashMap<String, PrivateShopItem>();
		news_board.clear();
		for (int i = 0; i < ads_num; i++)
		{
			if (news_board.size() >= DatabaseID.NEWS_BOARD_MAX_SLOT)
			{
				return;
			}
			
			String ads_user_id = data.getString(KeyID.KEY_ADS_USER_ID + i);
			if (IsOwner(_user_info.getDeviceID(), ads_user_id))
			{
				continue;
			}
			
			byte[] ads_user_item_bin = data.getBinary(KeyID.KEY_ADS_USER_ITEM + i);
			if (ads_user_item_bin != null)
			{
				PrivateShopItem ads_user_item = new PrivateShopItem(ads_user_item_bin);
				int item_type	= ads_user_item.getType();
				int item_id		= ads_user_item.getId();
				
				// check duplicate
				boolean duplicate = false;
				int count = 0;
				int max_count = 5;
				for (Entry<String, PrivateShopItem> item : news_board.entrySet())
				{
					PrivateShopItem value = item.getValue();
					if (value.getType() == item_type && value.getId() == item_id)
					{
						count++;
						if (count >= 3)
						{
							duplicate = true;
							break;
						}
					}
				}
				
				if (!duplicate)
				{
					news_board.put(ads_user_id, ads_user_item);
				}
			}
			else
			{
				LogHelper.Log("Cannot load ads " + i);
				return;
			}
		}
		
		// check if newsboard is still not enough
		if (news_board.size() < DatabaseID.NEWS_BOARD_MAX_SLOT)
		{
			for (int i = 0; i < ads_num; i++)
			{
				if (news_board.size() >= DatabaseID.NEWS_BOARD_MAX_SLOT)
				{
					return;
				}
				
				String ads_user_id = data.getString(KeyID.KEY_ADS_USER_ID + i);
				if (IsOwner(_user_info.getDeviceID(), ads_user_id) || news_board.containsKey(ads_user_id))
				{
					continue;
				}
				byte[] ads_user_item_bin = data.getBinary(KeyID.KEY_ADS_USER_ITEM + i);
				if (ads_user_item_bin != null)
				{
					PrivateShopItem ads_user_item = new PrivateShopItem(ads_user_item_bin);
					news_board.put(ads_user_id, ads_user_item);
				}
			}
		}
	}
	
	private String parseKey(String key)
	{
		return key.split(KeyID.NEWSBOARD_SLOT_CHAR)[0];
	}
	
	public byte[] getData()
	{
//		StringBuilder sb = new StringBuilder("newsboard db");
		
		FBEncrypt data = new FBEncrypt();
		
		data.addInt(KeyID.KEY_ADS_LIMIT_TIME, ads_limit_time);
		data.addByte(KeyID.KEY_ADS_NUM, ads_num);

//		sb.append('\t').append(ads_limit_time);
//		sb.append('\t').append(ads_num);
		
		int i = 0;
		for (Entry<String, PrivateShopItem> item: news_board.entrySet())
		{
			String user_id = item.getKey();
			data.addString(KeyID.KEY_ADS_USER_ID + i, user_id);
			
			PrivateShopItem psItem = (PrivateShopItem)item.getValue();
			data.addBinary(KeyID.KEY_ADS_USER_ITEM + i, psItem.getData());
			
			i++;
			
//			sb.append('\t').append(user_id);
		}
		data.addInt("last_update_time", _last_update_time);
		
//		LogHelper.Log(sb.toString());
		
		return data.toByteArray();
	}

	public byte[] getDataToClient()
	{
//		StringBuilder sb = new StringBuilder("newsboard_client");
		
		FBEncrypt data = new FBEncrypt();

		int i = 0;
		for (Entry<String, PrivateShopItem> item: news_board.entrySet())
		{
			String user_device_id = parseKey(item.getKey());
			byte[] user_info_bin = ServerHandler.GetUserData(user_device_id);
			
			if (user_info_bin == null)
			{
				// LogHelper.Log(user_device_id + " = NULL");
//				sb.append('\t').append("NULL");
			}
			else
			{
//				sb.append('\t').append(user_device_id);
				
				data.addBinary(KeyID.KEY_ADS_USER_INFO + i, ServerHandler.GetUserData(user_device_id));
				
				PrivateShopItem psItem = (PrivateShopItem)item.getValue();
				data.addBinary(KeyID.KEY_ADS_USER_ITEM + i, psItem.getData());
				
				i++;
			}
		}
		
		data.addInt(KeyID.KEY_ADS_LIMIT_TIME, ads_limit_time);
		data.addByte(KeyID.KEY_ADS_NUM, i);	// ads_num

//		sb.append('\t').append(ads_limit_time);
//		sb.append('\t').append(i);
		
//		LogHelper.Log(sb.toString());
		
		return data.toByteArray();
	}
	
	public void displayDataPackage()
	{
		FBEncrypt data = new FBEncrypt(getData());
		
		LogHelper.Log("ADS LIMIT TIME: " + data.getInt(KeyID.KEY_ADS_LIMIT_TIME));
		
		int num = data.getByte(KeyID.KEY_ADS_NUM);
		LogHelper.Log("ADS NUM: " + num);
		
		LogHelper.Log("");
		for (int i = 0; i < num; i++)
		{
			LogHelper.Log("---------------------------------- ITEM "  + (i+1));
			LogHelper.Log("ADS USER ID: " + data.getString(KeyID.KEY_ADS_USER_ID + i));
			
			FBEncrypt item = new FBEncrypt(data.getBinary(KeyID.KEY_ADS_USER_ITEM + i));
			item.displayDataPackage();
			LogHelper.Log("");
		}
	}
	
	private boolean IsOwner(String device_id, String ads_key)
	{
		String[] keys = ads_key.split(KeyID.NEWSBOARD_SLOT_CHAR);
		return device_id.equals(keys[0]);
	}
}