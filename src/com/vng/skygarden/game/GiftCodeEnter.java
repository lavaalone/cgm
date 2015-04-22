package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class GiftCodeEnter
{
	private int enter_max;
	private int enter_num;
	
	private int gift_type_total;
	private ConcurrentHashMap<String, Integer> gift_type;	// luot dung cho tung loai code
	
	public GiftCodeEnter()
	{
		enter_max = DatabaseID.GIFT_CODE_ENTER_MAX;
		enter_num = enter_max;
		
		gift_type_total = 0;
		gift_type = new ConcurrentHashMap();
	}

	public GiftCodeEnter(byte[] gift_code_bin)
	{
		FBEncrypt gift_code_enter = new FBEncrypt(gift_code_bin);
		
		enter_max = gift_code_enter.getInt(KeyID.KEY_GIFT_CODE_ENTER_MAX);
		enter_num = gift_code_enter.getInt(KeyID.KEY_GIFT_CODE_ENTER_NUM);
		
		try
		{
			if (gift_code_enter.hasKey(KeyID.KEY_GIFT_TYPE_TOTAL)) gift_type_total = gift_code_enter.getInt(KeyID.KEY_GIFT_TYPE_TOTAL);
			
			gift_type = new ConcurrentHashMap();
			
			for (int i = 0; i < gift_type_total; i++)
			{
				String gift_id		= gift_code_enter.getString(KeyID.KEY_GIFT_TYPE_ID + i);
				int gift_use_time	= gift_code_enter.getInt(KeyID.KEY_GIFT_TYPE_USE_TIME + i);
				
				gift_type.put(gift_id, gift_use_time);
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("GiftCodeEnter", e);
			gift_type = new ConcurrentHashMap();
		}
	}
	
	public byte[] getData()
	{
		FBEncrypt gift_code_enter = new FBEncrypt();
		
		gift_code_enter.addInt(KeyID.KEY_GIFT_CODE_ENTER_MAX, enter_max);
		gift_code_enter.addInt(KeyID.KEY_GIFT_CODE_ENTER_NUM, enter_num);
			
		
		gift_code_enter.addInt(KeyID.KEY_GIFT_TYPE_TOTAL, gift_type.size());
		
		int i = 0;
        for (Entry<String,Integer> item: gift_type.entrySet())
        {
			gift_code_enter.addString(KeyID.KEY_GIFT_TYPE_ID + i, item.getKey());
            gift_code_enter.addInt(KeyID.KEY_GIFT_TYPE_USE_TIME + i, item.getValue());
			
			i++;
        }
		
		return gift_code_enter.toByteArray();
	}
	
	public int getUseTimeOfType(String type)
	{
		if (gift_type.containsKey(type))
		{
			return gift_type.get(type);
		}
		else
		{
			return -1;
		}
	}

	public void decreaseUseTimeOfType(String type)
	{
		if (gift_type.containsKey(type))
		{
			int num = gift_type.get(type);
			if (num > 0) gift_type.replace(type, num - 1);
		}
	}
	
	public boolean hasType(String type)
	{
		return gift_type.containsKey(type);
	}
	
	public void addType(String type, int num)
	{
		if (hasType(type) == false && num > -1)	// chua ton tai
		{
			gift_type.put(type, num);
			
			gift_type_total++;
		}
		else
		{
			LogHelper.Log("can not add type " + type);
		}
	}
	
	public void displayDataPackage()
	{
		LogHelper.Log("enter_max: " + enter_max);
		LogHelper.Log("enter_num: " + enter_num);

		LogHelper.Log("gift_type_total: " + gift_type.size());
		
        for (Entry<String,Integer> item: gift_type.entrySet())
        {
			String gift_id		= item.getKey();
			int gift_use_time	= item.getValue();
			
			LogHelper.Log(gift_id + " : " + gift_use_time);
        }
	}
	
	public void resetEnterNum()
	{
		enter_num = enter_max;
	}
	
	public void decreaseEnterNum()
	{
		if (enter_num > 0) enter_num--;
	}
	
	public int getEnterNum()
	{
		return enter_num;
	}
	
	// private int[] toIntArray(ArrayList<Integer> list)
	// {
		// int[] ret = new int[list.size()];
		// int i = 0;
		// for (Integer e : list)  
			// ret[i++] = e.intValue();
		// return ret;
	// }			
}