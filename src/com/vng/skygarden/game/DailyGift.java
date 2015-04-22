package com.vng.skygarden.game;

import java.util.*;
import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;

public class DailyGift
{
	private int time_index = -1;
	private String[] time_range_s = new String[DatabaseID.MAX_GIFT_DAILY_NUM];
	private int[] time_range = new int[DatabaseID.MAX_GIFT_DAILY_NUM];
	private byte[] received = new byte[DatabaseID.MAX_GIFT_DAILY_NUM];
	private String[] gifts = new String[DatabaseID.MAX_GIFT_DAILY_NUM];
	
	public DailyGift()
	{
		resetDailyGift();
	}
	
	public DailyGift(byte[] gift_bin)
	{
		FBEncrypt gift_enc = new FBEncrypt(gift_bin);
		received	= gift_enc.getByteArray(KeyID.KEY_DAILY_GIFTS_RECEIVED);
		gifts		= gift_enc.getStringArray(KeyID.KEY_DAILY_GIFTS);
		time_range	= gift_enc.getIntArray(KeyID.KEY_DAILY_GIFTS_TIME_RANGE);

		getTimeIndex();
		
		// for (int i = 0; i < DatabaseID.MAX_GIFT_DAILY_NUM; i++)
		// {
			// LogHelper.Log("###DailyGift received " + i + "= " + received[i]);
			// LogHelper.Log("###DailyGift gifts " + i + "= " + gifts[i]);
			// LogHelper.Log("###DailyGift time_range " + i + "= " + time_range[i]);
		// }

//		gifts[i] = gifts[i].replace("5:3:", "5:2:");	// change KINH NGHIEM to DANH TIENG

		
		for (int i = 0; i < gifts.length; i++)
		{
			if (gifts[i].contains("5:3:"))
			{
				gifts[i] = gifts[i].replace("5:3:", "5:2:");	// change KINH NGHIEM to DANH TIENG
				
				// String[] s_gifts
				// for (int j = 0; j < gifts
				
				// String[] s_gifts = gifts[i].split(":");
				// s_gifts[s_gifts.length-1] = "50";
				// gifts[i] = ParseDB.join(s_gifts, ":");
			}
		}
		

	}

	public byte[] getData()
	{
		try
		{
			FBEncrypt gift_dec = new FBEncrypt();
			gift_dec.addArray(KeyID.KEY_DAILY_GIFTS_RECEIVED, received);
			gift_dec.addArray(KeyID.KEY_DAILY_GIFTS, gifts);
			gift_dec.addArray(KeyID.KEY_DAILY_GIFTS_TIME_RANGE, time_range);
			
			gift_dec.addArray(KeyID.KEY_DAILY_GIFTS_TIME_RANGE_S, time_range_s);
			
			// for (int i = 0; i < DatabaseID.MAX_GIFT_DAILY_NUM; i++)
			// {
				// LogHelper.Log("###getData received " + i + "= " + received[i]);
				// LogHelper.Log("###getData gifts " + i + "= " + gifts[i]);
				// LogHelper.Log("###getData time_range " + i + "= " + time_range[i]);
			// }

			return gift_dec.toByteArray();
		}
		catch (Exception ex)
		{
			LogHelper.LogException("DailyGift getData", ex);
		}
		
		return null;
	}
	
	public void initGifts(int user_level)
	{
		try
		{
			if (user_level >= Server.s_globalDB[DatabaseID.SHEET_DAILY_GIFT].length)
			{
				user_level = Server.s_globalDB[DatabaseID.SHEET_DAILY_GIFT].length - 1;
			}
			
			LinkedList generated = new LinkedList();
			
			// khong lay trung pack items
			int i = 0;
			int count = 0;
			
			while (i < DatabaseID.MAX_GIFT_DAILY_NUM)
			{
				int rnd = Misc.RANDOM_RANGE(0, 100);
				
				int current_percent = 0;
				
				for (int j = DatabaseID.DAILY_GIFT_PACK_1; j < DatabaseID.DAILY_GIFT_PACK_MAX; j++)
				{
					String items_string = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_DAILY_GIFT][user_level][j]);
					String[] items = items_string.split(":");
					
					current_percent += Integer.parseInt(items[0]);
					if (current_percent >= rnd)
					{
						// khong cho phep trung gift pack
						if (!generated.contains(j + "_" + items_string))
						{
							generated.add(j + "_" + items_string);
							
							// remove ratio param in string (first param)
							StringBuilder _item = new StringBuilder();
							
							for (int k = 1; k < items.length; k++)
							{
								_item.append(items[k]);
								if (k < items.length-1) _item.append(":");
							}
							
							gifts[i] = _item.toString();
							i++;
						}
						
						break;
					}
				}
				
				count++;
				
				// for safe
				if (count > 3000)
				{
					LogHelper.Log("Loop. Need recheck!!!"); 
					for (int g = i; g < DatabaseID.MAX_GIFT_DAILY_NUM; g++)
					{
						gifts[i] = "8:26:1";	// vot trang
					}
					break;
				}
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("initGifts", e);
		}
		
//		gifts[i] = gifts[i].replace("5:3:", "5:2:");	// change KINH NGHIEM to DANH TIENG
		for (int i = 0; i < gifts.length; i++)
		{
			if (gifts[i].contains("5:3:"))
			{
				gifts[i] = gifts[i].replace("5:3:", "5:2:");	// change KINH NGHIEM to DANH TIENG
				
				// String[] s_gifts
				// for (int j = 0; j < gifts
				
				// String[] s_gifts = gifts[i].split(":");
				// s_gifts[s_gifts.length-1] = "50";
				// gifts[i] = ParseDB.join(s_gifts, ":");
			}
		}
		
		/*
		for (int i = 0; i < gifts.length; i++)
		{
			gifts[i] = gifts[i].replace("5:3:", "5:2:");	// change KINH NGHIEM to DANH TIENG
			
			String[] s_gifts = gifts[i].split(":");
			s_gifts[s_gifts.length-1] = "50";
			gifts[i] = ParseDB.join(s_gifts, ":");
		}
		*/
		
		// for debug, remove later
		// StringBuilder sb = new StringBuilder();
		// for (int i = 0; i < gifts.length; i++)
		// {
			// sb.append(gifts[i]).append('\t');
		// }
		// LogHelper.Log(sb.toString()); 
	}
	
	private int getTimeIndex()
	{
		time_index = -1;
		
		try
		{
			for (int i = 0; i < DatabaseID.MAX_GIFT_DAILY_NUM; i++)
			{
				String time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_DAILY_GIFT][0][i+1]);
				time_range_s[i] = time;
				
				time_range[i] = Misc.SECONDS1(time);

				// LogHelper.Log("###getTimeIndex time_range " + i + "= " + time_range[i]);

				
				if (Misc.SECONDS() > Misc.SECONDS1(time)) time_index = i;
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("getTimeIndex", e);
		}
		
		if (time_index < 0) LogHelper.Log("NEED CHECK. time_index = " + time_index);
		
		// LogHelper.Log("time_index = " + time_index); 
		
		return time_index;
	}
	
	public boolean canReceivedGift()
	{
		return (getTimeIndex() >= 0);
	}
	
	public String getItems()
	{
		getTimeIndex(); // just to be sure time_index is up to date
		
		// LogHelper.Log("\n\n\n"); 
		// LogHelper.Log("time_index = " + time_index); 
		
		// for (int i = 0; i < received.length; i++)
			// LogHelper.Log(received[i]); 
		
		try
		{
			// chua nhan qua
			if (received[time_index] == 0)
			{
				received[time_index] = 1;

				for (int i = 0; i < time_index; i++)
				{
					received[i] = 1;
				}
				
				
				// LogHelper.Log("saaaaaaaaaaaaaaaaaaaaaaaa  " + gifts[time_index]); 
				return gifts[time_index];
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("getItems", e);
		}
		
		return "";
	}
	
	public boolean cancelGift()
	{
		getTimeIndex(); // just to be sure time_index is up to date
		
		if (received[time_index] == 0)
		{
			received[time_index] = 1;
			return true;
		}
		
		return false;
	}
	
	public void resetDailyGift()
	{
		for (int i = 0; i < DatabaseID.MAX_GIFT_DAILY_NUM; i++)
		{
			received[i] = 0;
			gifts[i] = "";
			time_range_s[i] = "";
			time_range[i] = 0;
		}
		
		getTimeIndex();
	}
	
	public void displayDataPackage()
	{
		for (int i = 0; i < DatabaseID.MAX_GIFT_DAILY_NUM; i++)
		{
			LogHelper.Log("------------------- gift " + i); 
			LogHelper.Log("time_range_s = " + time_range_s[i]); 
			LogHelper.Log("time_range = " + time_range[i]); 
			LogHelper.Log("received = " + received[i]); 
			LogHelper.Log("gifts = " + gifts[i]); 
			LogHelper.Log(""); 
		}
	}
}