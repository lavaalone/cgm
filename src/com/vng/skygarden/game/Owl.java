package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;

import java.util.*;

public class Owl
{
	private short power_limit;
	private short power_current;
	
    private byte slot_max;
	private byte slot_cur;
	private ArrayList<OwlSlot> slot;
	
    private byte total_slot = (byte)Server.s_globalDB[DatabaseID.SHEET_OWL_SLOT_UNLOCK].length;
	private long diamond_unlock_next_slot = 0;
	private long gold_unlock_next_slot = 0;

	private boolean load_result = true;
	private boolean need_save = false;
	
	private final boolean LOG_OWL = !true;
	
	public Owl(int user_level)
	{
		power_limit = (short)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][DatabaseID.CONSTANT_OWL_POWER_LIMIT]);
		power_current = power_limit;
		
        slot_max = 2;
		slot_cur = 0;
		slot = new ArrayList<OwlSlot>();
		
		initDiamondAndGoldUnlockSlot(DatabaseID.OWL_SLOT_UNLOCK);
	}
	
	public Owl(byte[] bin)
	{
		FBEncrypt owl = new FBEncrypt(bin);
		
		power_limit = owl.getShort(KeyID.KEY_OWL_POWER_LIMIT);
		power_current = owl.getShort(KeyID.KEY_OWL_POWER_CURRENT);
        slot_max = owl.getByte(KeyID.KEY_OWL_SLOT_MAX);
		slot_cur = owl.getByte(KeyID.KEY_OWL_SLOT_CUR);
		diamond_unlock_next_slot = owl.getLong(KeyID.KEY_OWL_DIAMOND_UNLOCK_NEXT_SLOT);
		gold_unlock_next_slot = owl.getLong(KeyID.KEY_OWL_GOLD_UNLOCK_NEXT_SLOT);
		
		slot = new ArrayList<OwlSlot>();
		
		for (int i = 0; i < slot_cur; i++)
		{
			byte[] slot_data = owl.getBinary(KeyID.KEY_OWL_SLOT + i);
			
			if (slot_data == null || slot_data.length == 0)
			{
				LogHelper.Log("Cannot load owl slot " + i);
				load_result = false;
				return;
			}

			OwlSlot owl_slot = new OwlSlot(slot_data);
			if (owl_slot.isLoadSuccess() == false)
			{
				load_result = false;
				return;
			}
			
			slot.add(owl_slot);
		}
		
		// check digest_time in slot then increase power_current
		if (this.slot_cur > 0)
		{
			while (this.slot_cur > 0 && getFirstSlotDigestTime() < Misc.SECONDS())
			{
				
				if (slot.size() > 0)
				{
					int food_id = slot.get(0).getFoodID();

					removeFirstSlot();

					increasePower(food_id);
				
					need_save = true;
				}
			}
		}
		
		// for sure we have unlock prices when slots available
		if (isLimitSlot() == false && diamond_unlock_next_slot == -1 && gold_unlock_next_slot == -1)
		{
			initDiamondAndGoldUnlockSlot(DatabaseID.OWL_SLOT_UNLOCK);
		}
	}
	
	private void initDiamondAndGoldUnlockSlot(int unlock_type)
	{
		boolean find_diamond = false;
		boolean find_gold = false;
		
		for (int i = 2; i < total_slot; i++)
		{
			long diamond = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_OWL_SLOT_UNLOCK][i][DatabaseID.OWL_SLOT_UNLOCK_DIAMOND]);
			long gold = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_OWL_SLOT_UNLOCK][i][DatabaseID.OWL_SLOT_UNLOCK_GOLD]);
			
			if (unlock_type == DatabaseID.OWL_SLOT_UNLOCK_DIAMOND_TYPE)
			{
				if (diamond > diamond_unlock_next_slot && !find_diamond)
				{
					diamond_unlock_next_slot = diamond;
					find_diamond = true;
				}
			}
			else if (unlock_type == DatabaseID.OWL_SLOT_UNLOCK_GOLD_TYPE)
			{
				if (gold > gold_unlock_next_slot && !find_gold)
				{
					gold_unlock_next_slot = gold;
					find_gold = true;
				}
			}
			else
			{
				if (diamond > diamond_unlock_next_slot && !find_diamond)
				{
					diamond_unlock_next_slot = diamond;
					find_diamond = true;
				}
				
				if (gold > gold_unlock_next_slot && !find_gold)
				{
					gold_unlock_next_slot = gold;
					find_gold = true;
				}
			}
		}
		
		if (unlock_type == DatabaseID.OWL_SLOT_UNLOCK_GOLD_TYPE && !find_gold) gold_unlock_next_slot = -1;
		if (unlock_type == DatabaseID.OWL_SLOT_UNLOCK_DIAMOND_TYPE && !find_diamond) diamond_unlock_next_slot = -1;
	}
	
	public boolean isLoadSuccess()
	{
		return load_result;
	}
	
	public boolean isNeedSave()
	{
		boolean _need_save = need_save;
		
		if (need_save)
		{
			need_save = false;
		}
		
		return _need_save;
	}
	
    public void increaseSlot(int unlock_type)
    {
        slot_max += 1;
		
		initDiamondAndGoldUnlockSlot(unlock_type);
    }

    public void addFood(int food_id)
    {
        setSlotCur(slot_cur + 1);

        int digest_time = 0;

        if (slot.size() > 0)
        {
            digest_time = slot.get(slot.size() - 1).getDigestTime() + getDigestCompleteTime(food_id);
        }
        else
        {
            digest_time = Misc.SECONDS() + getDigestCompleteTime(food_id);
        }

        OwlSlot owl_slot = new OwlSlot(food_id, digest_time);
        if (owl_slot.isLoadSuccess() == true)
        {
			slot.add(owl_slot);
        }
    }

	public int getDigestCompleteTime(int food_id)
	{
		return (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PRODUCT][food_id][DatabaseID.PRODUCT_PRODUCTION_TIME]);	// PRODUCT_PRODUCTION_TIME = OWL_FOOD_DIGEST_TIME
	}
	
    public boolean isFull()
    {
        return (slot_cur >= slot_max || slot_max >= Server.s_globalDB[DatabaseID.SHEET_OWL_SLOT_UNLOCK].length);
    }
	
    public boolean isLimitSlot()
    {
        return (slot_max >= Server.s_globalDB[DatabaseID.SHEET_OWL_SLOT_UNLOCK].length);
    }
	
	public byte[] getData()
	{
		FBEncrypt owl = new FBEncrypt();
		owl.addShort(KeyID.KEY_OWL_POWER_LIMIT, power_limit);
		owl.addShort(KeyID.KEY_OWL_POWER_CURRENT, power_current);
		owl.addByte(KeyID.KEY_OWL_SLOT_MAX, slot_max);
		owl.addByte(KeyID.KEY_OWL_SLOT_CUR, slot_cur);

        for (int i = 0; i < slot_cur; i++)
        {
            owl.addBinary(KeyID.KEY_OWL_SLOT + i, slot.get(i).getData());
        }
		
		owl.addByte(KeyID.KEY_OWL_TOTAL_SLOT, total_slot);
		owl.addLong(KeyID.KEY_OWL_DIAMOND_UNLOCK_NEXT_SLOT, diamond_unlock_next_slot);
		owl.addLong(KeyID.KEY_OWL_GOLD_UNLOCK_NEXT_SLOT, gold_unlock_next_slot);
		
		if (LOG_OWL)
		{
			LogOwl();
		}
		
		return owl.toByteArray();
	}
	
	public void LogOwl()
	{
		StringBuilder l = new StringBuilder();
		l.append("owl_info");
		l.append('\t').append(power_limit);
		l.append('\t').append(power_current);
		l.append('\t').append(diamond_unlock_next_slot);
		l.append('\t').append(gold_unlock_next_slot);
		
		LogHelper.Log(l.toString());
	}
	
	public void resetPower()
	{
		power_current = power_limit;
	}
	
	public void updatePowerLimit(int user_level)
	{
		power_limit = (short)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][DatabaseID.CONSTANT_OWL_POWER_LIMIT]);
	}
	
	public void decreasePower()
	{
		power_current--;
		if (power_current < 0) power_current = 0;
	}
	
	public void increasePower(int food_id)
	{
		power_current += (short)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PRODUCT][food_id][DatabaseID.PRODUCT_EXP_RECEIVE]);	// PRODUCT_EXP_RECEIVE = OWL_POWER_RECEIVE
		
		if (power_current > power_limit) power_current = power_limit;
	}
	
    public int getFirstSlotDigestTime()
    {
        if (slot.size() <= 0)
        {
            LogHelper.Log("Slot is empty. Server need check!!!");
			return -1;
        }
            
		return slot.get(0).getDigestTime();
    }
	
	public void removeFirstSlot()
	{
		if (this.slot_cur > 0)
		{
			setSlotCur(this.slot_cur - 1);
			slot.remove(0);
		}
	}
	
	public void updateDigestListTime(int digest_time)
	{
		for (int i = 0; i < this.slot_cur; i++)
		{
			slot.get(i).setDigestTime(slot.get(i).getDigestTime() - digest_time);
		}
	}
	
	// ---------------------------------------------------------------------------------------------
	
	public long getDiamondUnlock()
	{
		return this.diamond_unlock_next_slot;
	}
	
	public long getGoldUnlock()
	{
		return this.gold_unlock_next_slot;
	}
	
	public OwlSlot getSlot(int index)
	{
		return slot.get(index);
	}
	
	public byte getSlotMax()
	{
		return this.slot_max;
	}
	
	public void setSlotMax(int slot_max)
	{
		this.slot_max = (byte)slot_max;
	}
	
	public byte getSlotCur()
	{
		return this.slot_cur;
	}
	
	public void setSlotCur(int slot_cur)
	{
		this.slot_cur = (byte)slot_cur;
	}
	
	public Short getPowerLimit()
	{
		return power_limit;
	}
	
	public void setPowerLimit(int power_limit)
	{
		this.power_limit = (short)power_limit;
	}

	public short getPowerCurrent()
	{
		return power_current;
	}
	
	public void setPowerCurrent(int power_current)
	{
		this.power_current = (short)power_current;
	}
}