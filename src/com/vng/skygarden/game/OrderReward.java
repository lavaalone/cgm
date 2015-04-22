package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import com.vng.skygarden.*;
import java.util.*;

public class OrderReward
{
	private long				reward_gold;
	private long				reward_gold_bonus;
	private long				reward_exp;
	private long				reward_exp_bonus;
	private int					reward_diamond;

	private short				reward_item_count;
	private ArrayList<byte[]>	reward_items;		// include: type, id, num
	
	public OrderReward()
	{
		this.reward_gold = 0;
		this.reward_gold_bonus = 0;
		this.reward_exp = 0;
		this.reward_exp_bonus = 0;
		this.reward_diamond = 0;

		this.reward_item_count = 0;
		this.reward_items = new ArrayList<byte[]>();

		// LogHelper.Log("Init Order reward: OK!");
	}
	
	public OrderReward(byte[] reward_bin)
	{
		FBEncrypt reward = new FBEncrypt(reward_bin);
		
		this.reward_gold = reward.getLong(KeyID.KEY_ORDER_REWARD_GOLD);
		this.reward_gold_bonus = reward.getLong(KeyID.KEY_ORDER_REWARD_GOLD_BONUS);
		this.reward_exp = reward.getLong(KeyID.KEY_ORDER_REWARD_EXP);
		this.reward_exp_bonus = reward.getLong(KeyID.KEY_ORDER_REWARD_EXP_BONUS);
		this.reward_diamond = reward.getInt(KeyID.KEY_ORDER_REWARD_DIAMOND);

		this.reward_item_count = reward.getShort(KeyID.KEY_ORDER_ITEM_COUNT);
		this.reward_items = new ArrayList<byte[]>(this.reward_item_count);
		
		for (int i = 0; i < this.reward_item_count; i++)
		{
			byte[] item_data = reward.getBinary(KeyID.KEY_ORDER_ITEM + i);
			
			if (item_data != null)
			{
				reward_items.add(item_data);
			}
			else
			{
				LogHelper.Log("Cannot load reward " + i);
				return;
			}
		}
		
		// LogHelper.Log("Load reward...OK!");
	}
	
	// -------------------------------------------------------------------------------------
	
	public long getRewardGold()
	{
		return this.reward_gold;
	}
	
	public void setRewardGold(long reward_gold)
	{
		this.reward_gold = reward_gold;
	}
	
	public long getRewardGoldBonus()
	{
		return this.reward_gold_bonus;
	}
	
	public void setRewardGoldBonus(long reward_gold_bonus)
	{
		this.reward_gold_bonus = reward_gold_bonus;
	}
	
	public long getRewardExp()
	{
		return this.reward_exp;
	}
	
	public void setRewardExp(long reward_exp)
	{
		this.reward_exp = reward_exp;
	}

	public long getRewardExpBonus()
	{
		return this.reward_exp_bonus;
	}
	
	public void setRewardExpBonus(long reward_exp_bonus)
	{
		this.reward_exp_bonus = reward_exp_bonus;
	}

	public int getRewardDiamond()
	{
		return this.reward_diamond;
	}
	
	public void setRewardDiamond(int reward_diamond)
	{
		this.reward_diamond = reward_diamond;
	}

	public ArrayList<byte[]> getItems()
	{
		return reward_items;
	}
	
	public void setItems(ArrayList<byte[]> _reward_items)
	{
		this.reward_items = _reward_items;
		
		this.reward_item_count = (short)_reward_items.size();
	}
	
	public byte[] getData()
	{
		FBEncrypt reward = new FBEncrypt();
		
		reward.addLong(KeyID.KEY_ORDER_REWARD_GOLD, this.reward_gold);
		reward.addLong(KeyID.KEY_ORDER_REWARD_GOLD_BONUS, this.reward_gold_bonus);
		reward.addLong(KeyID.KEY_ORDER_REWARD_EXP, this.reward_exp);
		reward.addLong(KeyID.KEY_ORDER_REWARD_EXP_BONUS, this.reward_exp_bonus);
		reward.addInt(KeyID.KEY_ORDER_REWARD_DIAMOND, this.reward_diamond);
		reward.addShort(KeyID.KEY_ORDER_ITEM_COUNT, this.reward_item_count);
		
		for (int i = 0; i < this.reward_item_count; i++)
		{
			reward.addBinary(KeyID.KEY_ORDER_ITEM + i, reward_items.get(i));
		}

		return reward.toByteArray();
	}
	
	public void displayDataPackage()
	{
		FBEncrypt reward = new FBEncrypt(getData());
		
		long gold = reward.getLong(KeyID.KEY_ORDER_REWARD_GOLD);
		long gold_bonus = reward.getLong(KeyID.KEY_ORDER_REWARD_GOLD_BONUS);
		long exp = reward.getLong(KeyID.KEY_ORDER_REWARD_EXP);
		long exp_bonus = reward.getLong(KeyID.KEY_ORDER_REWARD_EXP_BONUS);
		int diamond = reward.getInt(KeyID.KEY_ORDER_REWARD_DIAMOND);
		short item_count = reward.getShort(KeyID.KEY_ORDER_ITEM_COUNT);
		
		LogHelper.Log("\nREWARD GOLD: " + gold);
		LogHelper.Log("REWARD GOLD BONUS: " + gold_bonus);
		LogHelper.Log("REWARD EXP: " + exp);
		LogHelper.Log("REWARD EXP BONUS: " + exp_bonus);
		LogHelper.Log("REWARD DIAMOND: " + diamond);
		LogHelper.Log("ITEM COUNT: " + item_count);
		
		for (int i = 0; i < item_count; i++)
		{
			LogHelper.Log("\n--- ITEM " + i + ":");
			FBEncrypt item = new FBEncrypt(reward.getBinary(KeyID.KEY_ORDER_ITEM + i));
			item.displayDataPackage();
		}
	}
	
	public String LogOrderReward()
	{
		StringBuilder log = new StringBuilder();
		log.append("order_reward");
		log.append('\t').append(this.reward_gold);
		log.append('\t').append(this.reward_gold_bonus);
		log.append('\t').append(this.reward_exp);
		log.append('\t').append(this.reward_exp_bonus);
		log.append('\t').append(this.reward_diamond);
		log.append('\t').append(this.reward_item_count);

		for (int i = 0; i < this.reward_item_count; i++)
		{
			FBEncrypt item = new FBEncrypt(reward_items.get(i));
			
			log.append("\treward" + i);
			log.append('\t').append(item.getByte(KeyID.KEY_PROD_TYPE));
			log.append('\t').append(item.getShort(KeyID.KEY_PROD_ID));
			log.append('\t').append(item.getShort(KeyID.KEY_PROD_NUM));
		}
		
		return log.toString();
	}	
}