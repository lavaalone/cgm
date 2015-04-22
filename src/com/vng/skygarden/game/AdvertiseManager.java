package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.skygarden.DBConnector;
import com.vng.skygarden._gen_.ProjectConfig;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class AdvertiseManager
{
	private final int MAX_CHANNEL = 20;
	private final int CLEAN_INTERVAL = 120;
	private final int GEN_INTERVAL = 120;
	private final int ADS_DURATION = 60 * 60;
	private final int MAX_LEVEL = 100; // result = 100 * 200
	private final int MAX_RETRY = DatabaseID.NEWS_BOARD_MAX_SLOT * 3;
	
	List<ConcurrentHashMap<String, ArrayList<byte[]>>> _ads_channel = new ArrayList<ConcurrentHashMap<String, ArrayList<byte[]>>>(MAX_CHANNEL);
	
	private int _channel_index_add = 0; // no need to use atomic here
	private int _channel_index_get = 0; // no need to use atomic here
	public AdvertiseManager()
	{
		if (ProjectConfig.IS_SERVER_NEWSBOARD != 1 && ProjectConfig.IS_SERVER_NEWSBOARD_FREESTYLE !=1)
		{
			LogHelper.Log("AdvertiseManager.. err! not server newsboard");
			return;
		}
		
		// initialize channels
		for (int i = 0; i < MAX_CHANNEL; i++)
		{
			LogHelper.Log("AdvertiseManager.. init ads channel, size = " + _ads_channel.size());
			ConcurrentHashMap<String, ArrayList<byte[]>> channel = new ConcurrentHashMap<String, ArrayList<byte[]>>();
			_ads_channel.add(channel);
		}
		
		// clean schedule
		Timer tc = new Timer("Clean");
		tc.schedule(new TimerTask() 
		{
			@Override
			public void run()
			{
				try
				{
					Clean();
				}
				catch (Exception e)
				{
					LogHelper.LogException("Clean", e);
				}
			}
		}, CLEAN_INTERVAL * 1000, CLEAN_INTERVAL * 1000);
		
		// generate ads schedule
		Timer gen = new Timer("Generate");
		tc.schedule(new TimerTask() 
		{
			@Override
			public void run()
			{
				try
				{
					Gen();
				}
				catch (Exception e)
				{
					LogHelper.LogException("Generate", e);
				}
			}
		}, GEN_INTERVAL * 1000, GEN_INTERVAL * 1000);
	}
	
	public void Add(String key, PrivateShopItem item) throws Exception
	{
		long current_time = Misc.SECONDS();
		byte[] time = new byte[8];
		time[0] = (byte)((current_time>>56)&0xFF);
        time[1] = (byte)((current_time>>48)&0xFF);
        time[2] = (byte)((current_time>>40)&0xFF);
        time[3] = (byte)((current_time>>32)&0xFF);
        time[4] = (byte)((current_time>>24)&0xFF);
        time[5] = (byte)((current_time>>16)&0xFF);
        time[6] = (byte)((current_time>>8)&0xFF);
        time[7] = (byte)((current_time&0xFF));
		
		ArrayList<byte[]> value = new ArrayList<>(2);
		value.add(0, item.getData());
		value.add(1, time);
		GetChannel(true).put(key, value);
//		LogHelper.Log("AdvertiseManager.Add.. key = " + key + ", time long = " + current_time + ", time string = " + Misc.getDateTime((int)current_time) + ", item: " + item.ToString());
	}
	
	public void Remove(String key) throws Exception
	{
		for (ConcurrentHashMap<String, ArrayList<byte[]>> channel : _ads_channel)
		{
			if (channel.containsKey(key))
			{
				channel.remove(key);
				return;
			}
		}
	}
	
	/**
	 * Get a number of (DatabaseID.NEWS_BOARD_MAX_SLOT*3) PrivateShopItem from ads channel, base on user level
	 * @param level
	 * @return 
	 */
	public HashMap<String, PrivateShopItem> Get(int level)
	{
		
		HashMap<String, PrivateShopItem> result = new HashMap<>(DatabaseID.NEWS_BOARD_MAX_SLOT * 3);
		int retry = 0;
		while (result.size() < DatabaseID.NEWS_BOARD_MAX_SLOT && (retry < MAX_RETRY))
		{
			// raw random here to save develope time
			ConcurrentHashMap<String, ArrayList<byte[]>> ads_channel = GetChannel(false);
			
			int max_size = ads_channel.size() - 1;
			if (max_size < 0)
			{
				max_size = 0;
			}
			int random_position = Misc.RANDOM_RANGE(0, max_size);
			int current_position = 0;
			for (Entry<String, ArrayList<byte[]>> ads : ads_channel.entrySet())
			{
				if (current_position == random_position)
				{
					ArrayList<byte[]> value = ads.getValue();
					PrivateShopItem item = new PrivateShopItem(value.get(0));
					if (IsItemUnlocked(level, item))
					{
						String key = ads.getKey();
						result.put(key, item);
					}
				}
				current_position++;
			}
			retry++;
		}
		return result;
	}
	
	/**
	 * Gen a number of (MAX_LEVEL*RESULT_PER_LEVEL) news list and write to database
	 */
	public void Gen()
	{
		for (int level = 5; level < MAX_LEVEL; level++)
		{
			for (int result_index = 0; result_index < DatabaseID.RESULT_PER_LEVEL; result_index++)
			{
				long start = System.currentTimeMillis();
				HashMap<String, PrivateShopItem> news_list = Get(level);
				long end = System.currentTimeMillis();
				long elapsed = end - start;
//				LogHelper.Log("Generate news list, level = " + level + ", news index = " + result_index + ", size = " + news_list.size() + ", time elapsed = " + elapsed);
				
				FBEncrypt data = new FBEncrypt();
				data.addByte(KeyID.KEY_ADS_NUM, news_list.size());
				int index = 0;
				for (Entry<String, PrivateShopItem> item: news_list.entrySet())
				{
					data.addString(KeyID.KEY_ADS_USER_ID + index, item.getKey());
					data.addBinary(KeyID.KEY_ADS_USER_ITEM + index, item.getValue().getData());
					index++;
				}
				DBConnector.GetMembaseServerForTemporaryData().SetRaw("newslist" + "_" + level + "_" + result_index, data.toByteArray());
			}
		}
	}
	
	public void Clean() throws Exception
	{
		LogHelper.Log("AdvertiseManager.Clean.. start clean at: " + Misc.getCurrentDateTime());
		for (ConcurrentHashMap<String, ArrayList<byte[]>> channel : _ads_channel)
		{
			LogHelper.Log("AdvertiseManager.Clean.. channel [" + _ads_channel.indexOf(channel) + "], before clean size = " + channel.size());
			for (Map.Entry<String, ArrayList<byte[]>> ad : channel.entrySet())
			{
				String key = ad.getKey();
				ArrayList<byte[]> value = ad.getValue();
				byte[] data = value.get(1);
				long add_time = ((data[0]&0xFF)<<56) |
								((data[1]&0xFF)<<48) |
								((data[2]&0xFF)<<40) |
								((data[3]&0xFF)<<32) |
								((data[4]&0xFF)<<24) |
								((data[5]&0xFF)<<16) |
								((data[6]&0xFF)<<8)  |
								(data[7]&0xFF);
//				LogHelper.Log("AdvertiseManager.Clean.. key = " + key + ", time long = " + add_time + ", time string= " + Misc.getDateTime((int)add_time) + ", current date time = " + Misc.getCurrentDateTime());
				if (Misc.SECONDS() > add_time + ADS_DURATION)
				{
					// ads expired
					channel.remove(key);
//					LogHelper.Log("AdvertiseManager.Clean.. removed key = " + key);
				}
			}
			LogHelper.Log("AdvertiseManager.Clean.. channel [" + _ads_channel.indexOf(channel) + "], after clean size = " + channel.size());
		}
		LogHelper.Log("AdvertiseManager.Clean.. end clean at: " + Misc.getCurrentDateTime());
	}
	
	public ConcurrentHashMap<String, ArrayList<byte[]>> GetChannel(boolean add)
	{
		if (add)
		{
			_channel_index_add++;
			if (_channel_index_add >= MAX_CHANNEL)
			{
				_channel_index_add = 0;
			}
			return _ads_channel.get(_channel_index_add);
		}
		else
		{
			_channel_index_get++;
			if (_channel_index_get >= MAX_CHANNEL)
			{
				_channel_index_get = 0;
			}
			return _ads_channel.get(_channel_index_get);
		}
	}
	
	private boolean IsItemUnlocked(int level, PrivateShopItem item)
	{
		try
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
					return true;
			}
			return (item_level <= level);
		}
		catch (Exception e)
		{
			return false;
		}
	}
}