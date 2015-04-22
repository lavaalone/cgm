package com.vng.skygarden.game;

import com.vng.taskqueue.*;
import com.vng.log.*;
import com.vng.netty.*;
import com.vng.util.*;
import com.vng.skygarden.game.PrivateShopItem;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class NewsBoardSaveLoadTask extends Task
{
	ConcurrentHashMap<String, PrivateShopItem> list;
	
	public NewsBoardSaveLoadTask(ConcurrentHashMap<String, PrivateShopItem> list)
	{
		super();
		
		this.list = list;
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
			LogHelper.LogException("HandleTask", ex);
		}

		try
		{
			FBEncrypt items = new FBEncrypt(1200 * 1024);	// 1.2 Mb ~ 10k items	~ 117 bytes/item
			items.addInt(KeyID.KEY_ADS_NUM, list.size());

			int item_index = 0;
			for (Entry<String, PrivateShopItem> item: list.entrySet())
			{
				String uInfo = item.getKey();
				PrivateShopItem psItem = (PrivateShopItem)item.getValue();
				
				if (item_index < 10000)	// limit 10k items save to log
				{
					items.addString(KeyID.KEY_ADS_USER_ID + item_index, uInfo);
					items.addBinary(KeyID.KEY_ADS_USER_ITEM + item_index, psItem.getData());
					
					item_index++;
				}
			}

			FileOutputStream fos = new FileOutputStream(KeyID.NEWSBOARD_ITEMS_LIST_PATH);
			byte[] data = items.toByteArray();
			fos.write(data, 0, data.length);
			fos.flush();
			fos.close();
		}
		catch (Exception ex)
		{
			LogHelper.LogException("HandleTask", ex);
		}
	}
}
