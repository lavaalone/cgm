package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import com.vng.skygarden.*;

public class Slot
{
    private short index;
    private boolean load_result = true;
    private int exp_receive = 0; // exp user will receive when harvest in this slot
	
    Decor decor = null;
    Pot pot = null;
	
	private boolean changed = true;
	
    public Slot(int slot_index) 
    {
        index = (short)slot_index;
        decor = new Decor();
        pot = new Pot();
    }
    
    public Slot(byte[] bin_db)
    {
        FBEncrypt slot = new FBEncrypt(bin_db);
        
        // load id
        index = slot.getShort(KeyID.KEY_SLOT_INDEX);
        
        //load pot
        byte[] bpot = slot.getBinary("pot");
        if (bpot == null || bpot.length == 0)
        {
            LogHelper.Log("Slot.. can not load pot data.");
            load_result = false;
            return;
        }

        pot = new Pot(bpot);
        if (pot.isLoadSuccess() == false)
        {
            load_result = false;
            return;
        }

//        load decor
        byte[] bdecor = slot.getBinary("decor");
        if (bdecor == null || bdecor.length == 0)
        {
            LogHelper.Log("Slot.. null decor data.");
			decor = new Decor();
        }
		else
		{
			decor = new Decor(bdecor);
			if (decor.isLoadSuccess() == false)
			{
				load_result = false;
				return;
			}
		}
    }
	
    public boolean isLoadSuccess()
    {
        return load_result;
    }

    public short getIndex() 
    {
        return index;
    }
    
    public int getHarvestExp(int bonus_ratio)
    { 
		exp_receive = pot.plant.getHarvestExp();
		
		exp_receive = exp_receive + (exp_receive*bonus_ratio)/100;
        
        return exp_receive;
    }
	
	private boolean just_harvested = false;
	public void harvest()
	{
		just_harvested = true;
		pot.plant.deletePlant();
		changed = true;
	}
    
    public Pot getPot()
	{
		return pot;
	}
	
	public Decor GetDecor()
	{
		return decor;
	}
	
	public byte[] getData(boolean save) 
    {
        FBEncrypt slot = new FBEncrypt();
		
        slot.addShort(KeyID.KEY_SLOT_INDEX, index);
		
		if (just_harvested)
		{
			just_harvested = false;
			slot.addInt(KeyID.KEY_SLOT_HARVEST_EXP, exp_receive);
		}
		else
		{
			slot.addInt(KeyID.KEY_SLOT_HARVEST_EXP, 0);
		}
		
        slot.addBinary("pot", pot.getData(save));
        slot.addBinary("decor", decor.getData(save));
		
		if (save)
		{
			changed = false;
		}

        return slot.toByteArray();
    }
	
	public boolean isChanged()
	{
		if (changed || pot.isChanged() || decor.isChanged())
		{
			return true;
		}
		return false;
	}
}