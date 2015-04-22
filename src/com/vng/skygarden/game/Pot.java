package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import com.vng.skygarden.*;
import java.util.concurrent.atomic.*;

public class Pot
{
    private AtomicInteger id = new AtomicInteger(-1);
    private boolean load_result = true;
    
    Plant plant;
	
	private boolean changed = true;
 
    public Pot()
    {
        plant = new Plant();
    }
    
    public Pot(byte[] pot_bin)
    {
        FBEncrypt pot = new FBEncrypt(pot_bin);
        
        short pot_id = pot.getShort(KeyID.KEY_POT_ID);
		
        // load pot id
        if (!id.compareAndSet(-1, pot_id))
        {
            LogHelper.Log("Pot.. can not load ID.");
            load_result = false;
            return;
        }
        
        // load plant in pot
        byte[] plant_bin = pot.getBinary("plant");
        if (plant_bin == null || plant_bin.length == 0)
        {
            load_result = false;
            LogHelper.Log("Pot.. can not load plant data.");
            return;
        }
		
        plant = new Plant(plant_bin);
        
        if (plant.isLoadSuccess() == false)
        {
            load_result = false;
            return;
        }

    }
	
    public boolean isLoadSuccess()
    {
        return load_result;
    }

    public short getID()
    {
        return id.shortValue();
    }

    // public void setID(int new_id)
    // {
		// id.getAndSet(new_id);
    // }
    
    public boolean createNewPot(int potID)
    {
		changed = true;
        return id.compareAndSet(-1, potID); // in case upgrade or downgrade pot, use increasePotID/decreasePotID
    }
    
    public void increasePotID()
    {
        if (id.get() < Server.s_globalDB[DatabaseID.SHEET_POT].length - 1)
		{
			id.incrementAndGet();
		}
		
		changed = true;
    }
    
    public void decreasePotID()
    {
        id.decrementAndGet();
		
		changed = true;
    }
    
    public boolean deletePot()
    {
		changed = true;
        return id.compareAndSet(getID(), -1);
    }
    
    public byte[] getData(boolean save) 
    {
        FBEncrypt pot = new FBEncrypt();
		
        pot.addShort(KeyID.KEY_POT_ID, id.shortValue());
        pot.addBinary("plant", plant.getData(save));
		
		if (save)
		{
			changed = false;
		}
        
        return pot.toByteArray();
    }
	
    public String getName() 
	{
		if (id.get() < 0) return "";
		
        return Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_POT][id.get()][DatabaseID.POT_NAME]);
    }

    public short getExpIncrease() 
	{
        return (short) Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_POT][id.get()][DatabaseID.POT_EXP_INCREASE]);
    }

    public short getTimeDecrease() 
	{
        return (short) Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_POT][id.get()][DatabaseID.POT_TIME_DECREASE]);
    }

    public long getGoldUpgrade() 
	{
		long v = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_POT][id.get()][DatabaseID.POT_GOLD_UPGRADE]);
        return v > 0 ? v : 0;
    }

    public String getRequiredPearl() 
	{
        return Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_POT][id.get()][DatabaseID.POT_UPGRADE_REQUIREMENT]);
    }

    public short getUpgradeRatio() 
	{
        return (short) Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_POT][id.get()][DatabaseID.POT_UPGRADE_RATIO]);
    }
	
	public double GetBlessingRatio() 
	{
		return Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_POT][id.get()][DatabaseID.POT_BLESSING_RATIO]);
	}
	
	public double GetBadLuckRatio() 
	{
		return Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_POT][id.get()][DatabaseID.POT_BAD_LUCK_RATIO]);
	}

    public long getGoldDefault() 
	{
        return Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_POT][id.get()][DatabaseID.POT_GOLD_DEFAULT]);
    }

    public long getGoldMin() 
	{
        return Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_POT][id.get()][DatabaseID.POT_GOLD_MIN]);
    }

    public long getGoldMax() 
	{
        return Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_POT][id.get()][DatabaseID.POT_GOLD_MAX]);
    }
	
	public int getBugAppearRatio()
	{
		return (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_POT][id.get()][DatabaseID.POT_BUG_APPEAR_RATIO]);
	}
	
	public Plant getPlant()
	{
		return plant;
	}
	
	public boolean isChanged()
	{
		if (changed || plant.isChanged())
		{
			return true;
		}
		return false;
	}
}
