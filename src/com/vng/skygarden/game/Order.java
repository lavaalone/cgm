package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import com.vng.skygarden.*;
import java.util.*;

public class Order
{
	private byte		type;
	private int			delivery_time;
	private int			new_wait_time;
	
	private short		reward_gold_ratio_from_pot = 0;
	private short		reward_exp_ratio_from_pot = 0;
	private short		reward_gold_ratio_from_machine = 0;
	private short		reward_exp_ratio_from_machine = 0;
	private short		reward_gold_ratio_from_event = 0;
	private short		reward_exp_ratio_from_event = 0;
	
	private long		reward_gold;
	private long		reward_gold_bonus;
	
	private long		reward_exp;
	private long		reward_exp_bonus;
	
	private short		reward_item_count;
	ArrayList<byte[]>	reward_items = null;	// include type, id, num
	
	public boolean		receive_daily_order;
	public int			receive_daily_order_diamond;
	public byte			letter_select_index;
	public byte			letter_select_value;
	
	public boolean[]	letters_enable = null;
	public byte[]		letters_value = null;
	public int			letter_reselect_diamond;
	
	private byte		npc;
	
	private short		product_count;
	ArrayList<byte[]> 	stock = null;			// include type, id, num
	
	private boolean		skipping = false;
	
	public Order(int type)
	{
		this.type = (byte)type;
		this.delivery_time = -1;
		this.new_wait_time = 0;

		this.reward_gold_ratio_from_pot = 0;
		this.reward_exp_ratio_from_pot = 0;
		
		this.reward_gold_ratio_from_machine = 0;
		this.reward_gold_ratio_from_event = 0;
		
		this.reward_exp_ratio_from_machine = 0;
		this.reward_exp_ratio_from_event = 0;
		
		this.reward_gold = 0;
		this.reward_gold_bonus = 0;
		this.reward_exp = 0;
		this.reward_exp_bonus = 0;

		this.reward_item_count = 0;
		this.reward_items = new ArrayList<byte[]>();
		
		this.product_count = 0;
		this.stock = new ArrayList<byte[]>();
		
		do
		{
			this.npc = (byte)Misc.RANDOM_RANGE(0, DatabaseID.NPC_MAX);
		}
		while (this.type != DatabaseID.ORDER_DAILY && (this.npc == DatabaseID.NPC_TINKER_BELL_ID || this.npc == DatabaseID.NPC_SNOW_WHITE_ID));
		
		if (this.type == DatabaseID.ORDER_DAILY)
		{
			this.npc = DatabaseID.NPC_SNOW_WHITE_ID;
			
			this.receive_daily_order = false;
			this.receive_daily_order_diamond = 0;
			this.letter_select_index = -1;
			this.letter_select_value = 0;
			this.letter_reselect_diamond = 0;

			this.letters_enable = new boolean[DatabaseID.ORDER_LETTER_COUNT];
			this.letters_value = new byte[DatabaseID.ORDER_LETTER_COUNT];

			for (int i = 0; i < DatabaseID.ORDER_LETTER_COUNT; i++)
			{
				this.letters_enable[i] = true;
				this.letters_value[i] = 0;
			}
		}
		
		this.skipping = false;
	}
	
	public Order(byte[] order_bin)
	{
		FBEncrypt order = new FBEncrypt(order_bin);
		
		this.type = order.getByte(KeyID.KEY_ORDER_TYPE);
		this.delivery_time = order.getInt(KeyID.KEY_ORDER_DELIVERY_TIME);
		this.new_wait_time = order.getInt(KeyID.KEY_ORDER_NEW_WAIT_TIME);
		
		if (order.hasKey(KeyID.KEY_ORDER_REWARD_GOLD_RATIO_FROM_POT))		this.reward_gold_ratio_from_pot = order.getShort(KeyID.KEY_ORDER_REWARD_GOLD_RATIO_FROM_POT);
		if (order.hasKey(KeyID.KEY_ORDER_REWARD_EXP_RATIO_FROM_POT)) 		this.reward_exp_ratio_from_pot = order.getShort(KeyID.KEY_ORDER_REWARD_EXP_RATIO_FROM_POT);
		if (order.hasKey(KeyID.KEY_ORDER_REWARD_GOLD_RATIO_FROM_MACHINE)) 	this.reward_gold_ratio_from_machine = order.getShort(KeyID.KEY_ORDER_REWARD_GOLD_RATIO_FROM_MACHINE);
		if (order.hasKey(KeyID.KEY_ORDER_REWARD_EXP_RATIO_FROM_MACHINE)) 	this.reward_exp_ratio_from_machine = order.getShort(KeyID.KEY_ORDER_REWARD_EXP_RATIO_FROM_MACHINE);
		
		if (order.hasKey(KeyID.KEY_ORDER_REWARD_GOLD_RATIO_FROM_EVENT)) 	this.reward_gold_ratio_from_event = order.getShort(KeyID.KEY_ORDER_REWARD_GOLD_RATIO_FROM_EVENT);
		if (order.hasKey(KeyID.KEY_ORDER_REWARD_EXP_RATIO_FROM_EVENT)) 	this.reward_exp_ratio_from_event = order.getShort(KeyID.KEY_ORDER_REWARD_EXP_RATIO_FROM_EVENT);
		
		this.reward_gold = order.getLong(KeyID.KEY_ORDER_REWARD_GOLD);
		this.reward_gold_bonus = order.getLong(KeyID.KEY_ORDER_REWARD_GOLD_BONUS);
		this.reward_exp = order.getLong(KeyID.KEY_ORDER_REWARD_EXP);
		this.reward_exp_bonus = order.getLong(KeyID.KEY_ORDER_REWARD_EXP_BONUS);
		this.reward_item_count = order.getShort(KeyID.KEY_ORDER_ITEM_COUNT);
		this.product_count = order.getShort(KeyID.KEY_ORDER_PRODUCT_COUNT);
		this.npc = order.getByte(KeyID.KEY_ORDER_NPC);
		
        reward_items = new ArrayList<byte[]>(this.reward_item_count);
        for (int i = 0; i < this.reward_item_count; i++)
        {
			byte[] item_data = order.getBinary(KeyID.KEY_ORDER_ITEM + i);
			
			if (item_data != null)
			{
				reward_items.add(item_data);
			}
			else
			{
				LogHelper.Log("Cannot load Order " + i);
				return;
			}
        }

        stock = new ArrayList<byte[]>(this.product_count);
		for (int i = 0; i < this.product_count; i++)
		{
			byte[] prod_data = order.getBinary(KeyID.KEY_ORDER_PRODUCT + i);
			
			stock.add(prod_data);
		}

		if (this.type == DatabaseID.ORDER_DAILY)
		{
			this.receive_daily_order = order.getBoolean(KeyID.KEY_ORDER_RECEIVE);
			this.receive_daily_order_diamond = order.getInt(KeyID.KEY_ORDER_RECEIVE_DIAMOND);
			this.letter_select_index = order.getByte(KeyID.KEY_ORDER_LETTER_SELECT_INDEX);
			this.letter_select_value = order.getByte(KeyID.KEY_ORDER_LETTER_SELECT_VALUE);
			this.letter_reselect_diamond = order.getInt(KeyID.KEY_LETTER_RESELECT_DIAMOND);

			this.letters_enable = new boolean[DatabaseID.ORDER_LETTER_COUNT];
			this.letters_value = new byte[DatabaseID.ORDER_LETTER_COUNT];

			for (int i = 0; i < DatabaseID.ORDER_LETTER_COUNT; i++)
			{
				this.letters_enable[i] = order.getBoolean(KeyID.KEY_ORDER_LETTERS_ENABLE + i);
				this.letters_value[i] = order.getByte(KeyID.KEY_ORDER_LETTERS_VALUE + i);
			}
		}
		
		if (order.hasKey(KeyID.KEY_ORDER_SKIPPING)) this.skipping = order.getBoolean(KeyID.KEY_ORDER_SKIPPING);
		
		// LogHelper.Log("Load Order...OK!");
	}
	
	// -------------------------------------------------------------------------------------
	
	public boolean setLettersRandomValue(int letter_index, int user_level)
	{
		try
		{
			if (letter_select_index == letter_index)
			{
				LogHelper.Log("setLettersRandomValue.. Cheating!!! User select previous letter = " + letter_index);
				return false;
			}
			
			int letters_enable_num = letter_select_value + 1;
			
			if (letters_enable_num == 0)
			{
				letters_enable_num = 1;
			}
			
			LinkedList generated = new LinkedList();
			
			for (int i = letters_enable_num; i <= DatabaseID.ORDER_LETTER_COUNT; i++)
			{
				generated.add(i);
			}
			
			// LogHelper.Log("" + generated); 

			// int index = 0;
			
			if (generated.size() == 0)
			{
				LogHelper.Log("setLettersRandomValue.. " + letter_select_value + " " + letter_select_index + " " + letter_index);
				return false;
			}
			
			
			for (int i = 0; i < DatabaseID.ORDER_LETTER_COUNT; i++)
			{
				if (letters_enable[i] == true && generated.size() > 0)
				{
					int index = Misc.RANDOM_RANGE(0, generated.size()-1);
					int value = ((Integer)generated.remove(index)).byteValue();
					letters_value[i] = (byte)value;
					
					// int value = ((Integer)generated.remove(index)).byteValue();
					// letters_value[i] = (byte)value;
				}
			}
			
			if (letter_select_value == 0)
			{
				byte letter_value = Misc.randSelectValue(user_level);
				
				for (int i = 0; i < DatabaseID.ORDER_LETTER_COUNT; i++)
				{
					if (letters_value[i] == letter_value)
					{
						byte temp = letters_value[letter_index];
						letters_value[letter_index] = letter_value;
						letters_value[i] = temp;
						break;
					}
				}
			}
			
			letter_select_index = (byte)letter_index;
			letter_select_value = letters_value[letter_index];
			letter_reselect_diamond = 1; // re-calc if we need
			
			for (int i = 0; i < DatabaseID.ORDER_LETTER_COUNT; i++)
			{
				if (letters_value[i] < letter_select_value)
				{
					letters_enable[i] = false;
				}
			}
			
			return true;
		}
		catch (Exception ex)
		{
			LogHelper.Log("letter_select_index = " + letter_select_index + ", letter_index = " + letter_index);
			LogHelper.LogException("setLettersRandomValue", ex);
		}
		
		return false;
	}
	
	public void addProductToStock(int type, int id, int num)
	{
		FBEncrypt prod = new FBEncrypt();
		prod.addByte(KeyID.KEY_PROD_TYPE, (byte)type);
		prod.addShort(KeyID.KEY_PROD_ID, (short)id);
		prod.addShort(KeyID.KEY_PROD_NUM, (short)num);
		
		stock.add(prod.toByteArray());
		
		setProductCount(this.product_count + 1);
	}
	
	public void addRewardItem(int type, int id, int num)
	{
		FBEncrypt item = new FBEncrypt();
		item.addByte(KeyID.KEY_PROD_TYPE, (byte)type);
		item.addShort(KeyID.KEY_PROD_ID, (short)id);
		item.addShort(KeyID.KEY_PROD_NUM, (short)num);
		
		reward_items.add(item.toByteArray());
		
		setRewardItemCount(this.reward_item_count + 1);
	}
	
	public short getRewardItemCount()
	{
		return this.reward_item_count;
	}
	
	public void setRewardItemCount(int _reward_item_count)
	{
		this.reward_item_count = (short)_reward_item_count;
	}
	
	public short getProductCount()
	{
		return this.product_count;
	}
	
	public void setProductCount(int product_count)
	{
		this.product_count = (short)product_count;
	}
	
	public byte getNPC()
	{
		return this.npc;
	}
	
	public void setNPC(int npc)
	{
		this.npc = (byte)npc;
	}
	
	public byte getType()
	{
		return this.type;
	}
	
	public void setType(int type)
	{
		this.type = (byte)type;
	}
	
	public short getRewardGoldRatioFromPot()
	{
		return this.reward_gold_ratio_from_pot;
	}
	
	public void setRewardGoldRatioFromPot(long reward_gold_ratio_from_pot)
	{
		this.reward_gold_ratio_from_pot = (short)reward_gold_ratio_from_pot;
	}
		
	public short getRewardExpRatioFromPot()
	{
		return this.reward_exp_ratio_from_pot;
	}
	
	public void setRewardExpRatioFromPot(long reward_exp_ratio_from_pot)
	{
		this.reward_exp_ratio_from_pot = (short)reward_exp_ratio_from_pot;
	}

	public short getRewardGoldRatioFromMachine()
	{
		return this.reward_gold_ratio_from_machine;
	}
	
	public void setRewardGoldRatioFromMachine(long reward_gold_ratio_from_machine)
	{
		this.reward_gold_ratio_from_machine = (short)reward_gold_ratio_from_machine;
	}
	
	public void setRewardGoldRatioFromEvent(long reward_gold_ratio_from_event)
	{
		this.reward_gold_ratio_from_event = (short)reward_gold_ratio_from_event;
	}
		
	public short getRewardExpRatioFromMachine()
	{
		return this.reward_exp_ratio_from_machine;
	}
	
	public void setRewardExpRatioFromMachine(long reward_exp_ratio_from_machine)
	{
		this.reward_exp_ratio_from_machine = (short)reward_exp_ratio_from_machine;
	}
	
	public void setRewardExpRatioFromEvent(long reward_exp_ratio_from_event)
	{
		this.reward_exp_ratio_from_event = (short)reward_exp_ratio_from_event;
	}

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

	public short getDeliveryTime()
	{
		return (short)this.delivery_time;
	}
	
	public void setDeliveryTime(int delivery_time)
	{
		this.delivery_time = (short)delivery_time;
	}
	
	public int getNewWaitTime()
	{
		return this.new_wait_time;
	}
	
	public void setNewWaitTime(int new_wait_time)
	{
		this.new_wait_time = new_wait_time;
		this.skipping = new_wait_time > 0 ? true : false;
	}
	
	public ArrayList<byte[]> getStock()
	{
		return stock;
	}
	
	public ArrayList<byte[]> getRewardItems()
	{
		return reward_items;
	}
	
	public boolean getReceiveDailyOrder()
	{
		return this.receive_daily_order;
	}
	
	public void setReceiveDailyOrder(boolean receive_daily_order)
	{
		this.receive_daily_order = receive_daily_order;
	}

	public int getReceiveDailyOrderDiamond()
	{
		return this.receive_daily_order_diamond;
	}
	
	public void setReceiveDailyOrderDiamond(int receive_daily_order_diamond)
	{
		this.receive_daily_order_diamond = receive_daily_order_diamond;
	}
	
	public byte getLetterSelectIndex()
	{
		return this.letter_select_index;
	}
	
	public void setLetterSelectIndex(int letter_select_index)
	{
		this.letter_select_index = (byte)letter_select_index;
	}
	
	public byte getLetterSelectValue()
	{
		return this.letter_select_value;
	}
	
	public void setLetterSelectValue(int letter_select_value)
	{
		this.letter_select_value = (byte)letter_select_value;
	}
	
	public boolean getLettersEnable(int letter_index)
	{
		return this.letters_enable[letter_index];
	}
	
	public void setLettersEnable(int letter_index, boolean value)
	{
		this.letters_enable[letter_index] = value;
	}
	
	public byte getLettersValue(int letter_index)
	{
		return this.letters_value[letter_index];
	}
	
	public void setLettersValue(int letter_index, byte value)
	{
		this.letters_value[letter_index] = value;
	}
	
	public int getLetterReselectDiamond()
	{
		return this.letter_reselect_diamond;
	}
	
	public void setLetterReselectDiamond(int letter_reselect_diamond)
	{
		this.letter_reselect_diamond = letter_reselect_diamond;
	}
	
	public boolean isSkipping()
	{
		return this.skipping;
	}
	
	public void setSkippingStatus(boolean skipping)
	{
		this.skipping = skipping;
	}
	
	public byte[] getData()
	{
		FBEncrypt order = new FBEncrypt();
		
		order.addByte(KeyID.KEY_ORDER_TYPE, this.type);
		order.addInt(KeyID.KEY_ORDER_DELIVERY_TIME, this.delivery_time);
		order.addInt(KeyID.KEY_ORDER_NEW_WAIT_TIME, this.new_wait_time);
		
		order.addShort(KeyID.KEY_ORDER_REWARD_GOLD_RATIO_FROM_POT, this.reward_gold_ratio_from_pot);
		order.addShort(KeyID.KEY_ORDER_REWARD_EXP_RATIO_FROM_POT, this.reward_exp_ratio_from_pot);
		order.addShort(KeyID.KEY_ORDER_REWARD_GOLD_RATIO_FROM_MACHINE, this.reward_gold_ratio_from_machine);
		order.addShort(KeyID.KEY_ORDER_REWARD_EXP_RATIO_FROM_MACHINE, this.reward_exp_ratio_from_machine);
		
		order.addShort(KeyID.KEY_ORDER_REWARD_GOLD_RATIO_FROM_EVENT, this.reward_gold_ratio_from_event);
		order.addShort(KeyID.KEY_ORDER_REWARD_EXP_RATIO_FROM_EVENT, this.reward_exp_ratio_from_event);
		
		order.addLong(KeyID.KEY_ORDER_REWARD_GOLD, this.reward_gold);
		order.addLong(KeyID.KEY_ORDER_REWARD_GOLD_BONUS, this.reward_gold_bonus);		
		
		order.addLong(KeyID.KEY_ORDER_REWARD_EXP, this.reward_exp);
		order.addLong(KeyID.KEY_ORDER_REWARD_EXP_BONUS, this.reward_exp_bonus);
		
		order.addShort(KeyID.KEY_ORDER_ITEM_COUNT, this.reward_item_count);
		order.addShort(KeyID.KEY_ORDER_PRODUCT_COUNT, this.product_count);
		order.addByte(KeyID.KEY_ORDER_NPC, this.npc);
		
        for (int i = 0; i < this.reward_item_count; i++)
        {
			order.addBinary(KeyID.KEY_ORDER_ITEM + i, reward_items.get(i));
        }

        for (int i = 0; i < this.product_count; i++)
        {
			order.addBinary(KeyID.KEY_ORDER_PRODUCT + i, stock.get(i));
        }
		
		if (this.type == DatabaseID.ORDER_DAILY)
		{
			order.addBoolean(KeyID.KEY_ORDER_RECEIVE, this.receive_daily_order);
			order.addInt(KeyID.KEY_ORDER_RECEIVE_DIAMOND, this.receive_daily_order_diamond);
			order.addByte(KeyID.KEY_ORDER_LETTER_SELECT_INDEX, this.letter_select_index);
			order.addByte(KeyID.KEY_ORDER_LETTER_SELECT_VALUE, this.letter_select_value);
			order.addInt(KeyID.KEY_LETTER_RESELECT_DIAMOND, this.letter_reselect_diamond);

			for (int i = 0; i < DatabaseID.ORDER_LETTER_COUNT; i++)
			{
				order.addBoolean(KeyID.KEY_ORDER_LETTERS_ENABLE + i, letters_enable[i]);
				order.addByte(KeyID.KEY_ORDER_LETTERS_VALUE + i, letters_value[i]);
			}
		}
		
		order.addBoolean(KeyID.KEY_ORDER_SKIPPING, this.skipping);
		
		return order.toByteArray();
	}
	
	public void displayDataPackage()
	{
		if (true) return;
		
		LogHelper.Log("\norder type: " + this.type); 
		LogHelper.Log("delivery_time: " + this.delivery_time); 
		LogHelper.Log("new_wait_time: " + this.new_wait_time); 
		
		LogHelper.Log("reward_gold_ratio_from_pot: " + this.reward_gold_ratio_from_pot); 
		LogHelper.Log("reward_exp_ratio_from_pot: " + this.reward_exp_ratio_from_pot); 
		LogHelper.Log("reward_gold_ratio_from_machine: " + this.reward_gold_ratio_from_machine); 
		LogHelper.Log("reward_exp_ratio_from_machine: " + this.reward_exp_ratio_from_machine); 
		

		LogHelper.Log("reward_gold_ratio_from_event: " + this.reward_gold_ratio_from_event); 
		LogHelper.Log("reward_exp_ratio_from_event: " + this.reward_exp_ratio_from_event); 
		
		LogHelper.Log("reward_gold: " + this.reward_gold); 
		LogHelper.Log("reward_gold_bonus: " + this.reward_gold_bonus); 
		LogHelper.Log("reward_exp: " + this.reward_exp); 
		LogHelper.Log("reward_exp_bonus: " + this.reward_exp_bonus); 
		LogHelper.Log("reward_item_count: " + this.reward_item_count); 
		LogHelper.Log("product_count: " + this.product_count); 
		LogHelper.Log("npc: " + this.npc); 
		
        for (int i = 0; i < this.reward_item_count; i++)
        {
			FBEncrypt item = new FBEncrypt(reward_items.get(i));
			item.displayDataPackage();
        }

        for (int i = 0; i < this.product_count; i++)
        {
			FBEncrypt prod = new FBEncrypt(stock.get(i));
			prod.displayDataPackage();
        }
		
		if (this.type == DatabaseID.ORDER_DAILY)
		{
			LogHelper.Log("\nreceive_daily_order: " + this.receive_daily_order); 
			LogHelper.Log("\nreceive_daily_order_diamond: " + this.receive_daily_order_diamond); 
			LogHelper.Log("letter_select_index: " + this.letter_select_index); 
			LogHelper.Log("letter_select_value: " + this.letter_select_value); 
			LogHelper.Log("letter_reselect_diamond: " + this.letter_reselect_diamond); 

			for (int i = 0; i < DatabaseID.ORDER_LETTER_COUNT; i++)
			{
				LogHelper.Log("letter_enable_" + i + ": " + this.letters_enable[i]); 
				LogHelper.Log("letter_value_" + i + ": " + this.letters_value[i]); 
			}
		}
	}
	
	public String LogOrder()
	{
		StringBuilder log = new StringBuilder();
		log.append("ORDER");
		log.append('\t').append(this.type);
		log.append('\t').append(this.delivery_time);
		log.append('\t').append(this.new_wait_time);
		
		log.append('\t').append(this.reward_gold_ratio_from_pot);
		log.append('\t').append(this.reward_exp_ratio_from_pot);
		log.append('\t').append(this.reward_gold_ratio_from_machine);
		log.append('\t').append(this.reward_exp_ratio_from_machine);
		
		log.append('\t').append(this.reward_gold);
		log.append('\t').append(this.reward_gold_bonus);
		log.append('\t').append(this.reward_exp);
		log.append('\t').append(this.reward_exp_bonus);
		log.append('\t').append(this.reward_item_count);
		log.append('\t').append(this.product_count);
		log.append('\t').append(this.npc);
		
        for (int i = 0; i < this.reward_item_count; i++)
        {
			FBEncrypt item = new FBEncrypt(reward_items.get(i));
			log.append("\tITEM" + i);
			
			log.append('\t').append(item.getByte(KeyID.KEY_PROD_TYPE));
			log.append('\t').append(item.getShort(KeyID.KEY_PROD_ID));
			log.append('\t').append(item.getShort(KeyID.KEY_PROD_NUM));
		}

		for (int i = 0; i < this.product_count; i++)
		{
			FBEncrypt prod = new FBEncrypt(stock.get(i));
			
			log.append("\nPROD" + i);
			
			log.append('\t').append(prod.getByte(KeyID.KEY_PROD_TYPE));
			log.append('\t').append(prod.getShort(KeyID.KEY_PROD_ID));
			log.append('\t').append(prod.getShort(KeyID.KEY_PROD_NUM));
		}

		if (this.type == DatabaseID.ORDER_DAILY)
		{
			log.append("\tDAILY_ORDER");
			log.append('\t').append(this.receive_daily_order);
			log.append('\t').append(this.receive_daily_order_diamond);
			log.append('\t').append(this.letter_select_index);
			log.append('\t').append(this.letter_select_value);
			log.append('\t').append(this.letter_reselect_diamond);
			log.append('\t').append(this.letters_enable);
			
			for (int i = 0; i < DatabaseID.ORDER_LETTER_COUNT; i++)
			{
				log.append("\tLETTER" + i);
				log.append('\t').append(this.letters_enable[i]);
				log.append('\t').append(this.letters_value[i]);
			}
		}
		
		return log.toString();
	}
}