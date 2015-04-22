package com.vng.skygarden.game;

import com.vng.taskqueue.*;
import com.vng.log.*;
import com.vng.netty.*;
import com.vng.util.*;
import com.vng.skygarden.game.PrivateShopItem;
import com.vng.skygarden.DBConnector;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class NewsBoardManager
{
	ConcurrentHashMap<String, PrivateShopItem> list;
	int start_index;
	
	public NewsBoardManager()
	{
		list = new ConcurrentHashMap<String, PrivateShopItem>();
		start_index = 0;
		
		loadNewsboardItemsList();
	}
	
	private void loadNewsboardItemsList()
	{
		try
		{
			File inputFile = new File(KeyID.NEWSBOARD_ITEMS_LIST_PATH);
			
			if (inputFile.exists())
			{
				byte[] data = new byte[(int)inputFile.length()];
				FileInputStream fis = new FileInputStream(inputFile);
				fis.read(data, 0, data.length);
				fis.close();		
				
				FBEncrypt items = new FBEncrypt(data);
				
				int ads_num = items.getInt(KeyID.KEY_ADS_NUM);

				for (int i = 0; i < ads_num; i++)
				{
					String key = items.getString(KeyID.KEY_ADS_USER_ID + i);
					PrivateShopItem value = new PrivateShopItem(items.getBinary(KeyID.KEY_ADS_USER_ITEM + i));
					
					if (value.getAdvertiseEndTime() > Misc.SECONDS())
					{
						add(key, value);
					}
				}
			}
		}
		catch (Exception ex)
		{
			LogHelper.LogException("HandleTask", ex);
		}
	}
	
	public void add(String key, PrivateShopItem value)
	{
		list.put(key, value);
		// LogHelper.Log("Add ADS to list, size: " + list.size());
	}
	
	public void remove(String key)
	{
		list.remove(key);
		// LogHelper.Log("Remove ADS from list, size: " + list.size());
	}

	public long size()
	{
		return list.size();
	}
	
	public boolean containsKey(String key)
	{
		return list.containsKey(key);
	}
	
	public void refresh()
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Misc.getCurrentDateTime());
			sb.append("\trefresh\t").append(list.size());
			
			// LogHelper.Log("\n");
			// LogHelper.Log("Before refresh newsboard, list size is: " + list.size());
			
			for (Entry<String, PrivateShopItem> item: list.entrySet())
			{
				PrivateShopItem psItem = (PrivateShopItem)item.getValue();
				
				if ((psItem.getAdvertiseEndTime() < Misc.SECONDS()) || (psItem.getAdvertiseEndTime() > Misc.SECONDS() + DatabaseID.PRIVATE_SHOP_ADS_MAX_TIME + 900))
				{
					list.remove(item.getKey());
					// LogHelper.Log("remove: " + item.getKey());
				}
			}
			
			// LogHelper.Log("After refresh newsboard, list size is: " + list.size());
			sb.append("\t").append(list.size());
			
			if (Server._task_queue == null)
			{
				sb.append("\n---------------------------------------- _task_queue = null");
			}

			LogHelper.Log(LogHelper.LogType.NEWSBOARD, sb.toString());
			
			
			//-------------------------------------------------------------
			Server._task_queue.AddTask(new NewsBoardSaveLoadTask(list));
		}
		catch (Exception e)
		{
			LogHelper.LogException("refresh", e);
		}
	}

	public byte[] getNewsBoardItems(String device_id, int user_level)
	{
		start_index += DatabaseID.NEWS_BOARD_MAX_SLOT;
		
		HashMap<String, PrivateShopItem> items = new HashMap<String, PrivateShopItem>();
	
		int i = 0;
		int item_num = 0;
		
		// LogHelper.Log("******************************* ALL NEWSBOARD ITEMS LIST: " + list.size());
		
		try
		{
			if (list.size() < DatabaseID.NEWS_BOARD_MAX_SLOT)
			{
				for (Entry<String, PrivateShopItem> item: list.entrySet())
				{
					if (!isOwner(device_id, item.getKey()) && isUnlock(user_level, item.getValue()))
					{
						items.put(item.getKey(), item.getValue());
					}
				}
			}
			else
			{
				for (Entry<String, PrivateShopItem> item: list.entrySet())
				{
					if (i >= (start_index - DatabaseID.NEWS_BOARD_MAX_SLOT) && i < start_index)
					{
						if (items.containsKey(item.getKey()) == false)
						{
							if (!isOwner(device_id, item.getKey()) && isUnlock(user_level, item.getValue()))
							{
								items.put(item.getKey(), item.getValue());
								item_num++;
							}
						}
					}

					i++;
					if (i >= start_index) break;
				}
				
				// not enough items num, re-loop from begining
				if (item_num < DatabaseID.NEWS_BOARD_MAX_SLOT)
				{
					start_index = 0;
					
					for (Entry<String, PrivateShopItem> item: list.entrySet())
					{
						start_index++;
						
						if (items.containsKey(item.getKey()) == false)
						{
							if (!isOwner(device_id, item.getKey()) && isUnlock(user_level, item.getValue()))
							{
								items.put(item.getKey(), item.getValue());
								item_num++;
							}

							if (item_num >= DatabaseID.NEWS_BOARD_MAX_SLOT) break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("getNewsBoardItems", e);
		}
		
		FBEncrypt data = new FBEncrypt();
		data.addByte(KeyID.KEY_ADS_NUM, items.size());
		
		// LogHelper.Log("******************************* NEWSBOARD ITEMS NUM RETURN TO CLIENT: " + items.size());
		
		int item_index = 0;
		for (Entry<String, PrivateShopItem> item: items.entrySet())
		{
			String uInfo = item.getKey();
			PrivateShopItem psItem = (PrivateShopItem)item.getValue();
			
			data.addString(KeyID.KEY_ADS_USER_ID + item_index, uInfo);
			data.addBinary(KeyID.KEY_ADS_USER_ITEM + item_index, psItem.getData());
			
			item_index++;
		}
		
		return data.toByteArray();
	}
	
	private boolean isOwner(String device_id, String ads_key)
	{
		String[] keys = ads_key.split(KeyID.NEWSBOARD_SLOT_CHAR);
		
		// System.out.println("keys.length = " + keys.length); 
		// System.out.println("device_id = " + device_id); 
		// System.out.println("ads_key = " + ads_key); 
		// System.out.println("keys[0] = " + keys[0]); 
		// System.out.println("keys[1] = " + keys[1]); 
		
		return device_id.equals(keys[0]);
	}
	
	private boolean isUnlock(int user_level, PrivateShopItem item)
	{
		int item_type = item.getType();
		int item_id = item.getId();
		
		int item_level = 0;
		
		switch (item_type)
		{
			case DatabaseID.IT_PLANT:
				item_level = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_SEED][item_id][DatabaseID.SEED_LEVEL_UNLOCK]);
				break;
				
			case DatabaseID.IT_POT:
				item_level = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_POT][item_id][DatabaseID.POT_LEVEL_UNLOCK]);
				break;
				
			case DatabaseID.IT_PRODUCT:
				item_level = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PRODUCT][item_id][DatabaseID.PRODUCT_LEVEL_UNLOCK]);
				break;
				
			default:
				return false;
		}
		
		return (item_level <= user_level);
	}
}