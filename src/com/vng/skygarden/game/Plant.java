package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import com.vng.skygarden.*;
import java.util.Random;
import java.util.concurrent.atomic.*;

public class Plant
{
    private AtomicInteger id = new AtomicInteger(-1);
	
	private int start_time = -1;
    private AtomicInteger grow_time = new AtomicInteger(-1);
	
    private AtomicBoolean hasBug = new AtomicBoolean(false);
	
	private AtomicInteger combo_id = new AtomicInteger(-1);
    
    private boolean load_result = true;
	
	private AtomicInteger fertilizer_id = new AtomicInteger(-1);
	private int time_reduced_by_fertilizer = 0;
	
	private AtomicInteger decor_id = new AtomicInteger(-1);
	
	private boolean changed = true;
  
    public Plant()
    {
    }

    public Plant(byte[] plant_bin) 
    {
        FBEncrypt plant = new FBEncrypt(plant_bin);

        short plant_id = plant.getShort(KeyID.KEY_PLANT_ID);
		start_time = plant.getInt(KeyID.KEY_PLANT_START_TIME);
        int growtime = plant.getInt(KeyID.KEY_PLANT_GROW_TIME);
        boolean has_bug = plant.getBoolean(KeyID.KEY_PLANT_HAS_BUG);
		int combo_id = plant.getInt(KeyID.KEY_PLANT_COMBO_ID);
		
        if (!this.id.compareAndSet(-1, plant_id))
        {
            LogHelper.Log("Plant.. err! can not load plant ID.");
            load_result = false;
            return;
        }
            
        if (!this.grow_time.compareAndSet(-1, growtime))
        {
            LogHelper.Log("Plant.. err! can not load grow time.");
            load_result = false;
            return;
        }
        
        if (!this.hasBug.compareAndSet(false, has_bug))
        {
            LogHelper.Log("Plant.. err! can not load bug id");
            load_result = false;
            return;
        }
		
		if (!this.combo_id.compareAndSet(-1, combo_id))
		{
			LogHelper.Log("Plant.. err! can not load combo id");
			load_result = false;
			return;
		}
		
		if (plant.hasKey(KeyID.KEY_PLANT_FERTILIZER_ID))
		{
			if (!this.fertilizer_id.compareAndSet(-1, plant.getInt(KeyID.KEY_PLANT_FERTILIZER_ID)))
			{
				LogHelper.Log("Plant.. err! can not load fertilizer id");
				load_result = false;
				return;
			}
		}
		
		if (plant.hasKey(KeyID.KEY_PLANT_FERTILIZER_REDUCE_TIME))
		{
			this.time_reduced_by_fertilizer = plant.getInt(KeyID.KEY_PLANT_FERTILIZER_REDUCE_TIME);
		}
    }
    
    public boolean isLoadSuccess()
    {
        return load_result;
    }

    public int getID()
    {
        return this.id.get();
    }

    public void createNewPlant(int plant_id)
    {
        if (!this.id.compareAndSet(-1, plant_id))
        {
            LogHelper.Log("Plant.. can not set new plant.");
            return;
        }
		
		this.start_time = Misc.SECONDS();
        
        // recalculate the grow time
        if (!this.grow_time.compareAndSet(-1, timeWillGrow()))
        {
            LogHelper.Log("Plant.. can not set grow time.");
            return;
        }
		
		// reset the fertilizer id
		this.fertilizer_id.set(-1);
		this.time_reduced_by_fertilizer = 0;
		changed = true;
    }
    
    public boolean hasBugOnPlant()
    {
        if (hasBug.get())
        {
            if (Misc.SECONDS() > grow_time.get())
            {
                hasBug.compareAndSet(true, false);
				changed = true;
            }
        }
        
        return hasBug.get();
    }
	
	public boolean setBugOnPlant(boolean has_bug)
	{
		if (has_bug)
		{
			if (!this.hasBug.compareAndSet(false, true))
			{
				LogHelper.Log("Plant.. can not create bug.");
				return false;
			}
		}
		
		changed = true;
		
		return true;
	}
	
	public boolean SetFertilizerID(int _id)
	{
		changed = true;
		return this.fertilizer_id.compareAndSet(-1, _id);
	}
	
	public int GetFertilizerID()
	{
		return this.fertilizer_id.get();
	}
	
	public void SetFertilizerReduceTime(int v)
	{
		changed = true;
		this.time_reduced_by_fertilizer = v;
	}
	
	public int GetFertilizerReduceTime()
	{
		return this.time_reduced_by_fertilizer;
	}
	
	public boolean SetDecorID(int _id)
	{
		changed = true;
		return this.decor_id.compareAndSet(-1, _id);
	}
	
	public int GetDecorID()
	{
		return this.decor_id.get();
	}
    
    public boolean deleteBug()
    {
		changed = true;
        return this.hasBug.compareAndSet(true,false);
    }
    
    public void deletePlant()
    {
        this.id.set(-1);	
        this.grow_time.set(-1);
        this.hasBug.set(false);
		changed = true;
    }
    
    private int timeWillGrow()
    {
        if (id.get() != -1)
            return (Misc.SECONDS() + (int) Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_SEED][id.get()][DatabaseID.SEED_GROW_TIME]));
        else
            return -1;
    }
    
    public boolean isGrownUp()
    {
        if (id.get() == -1) return false;
        return Misc.SECONDS() >= grow_time.get() ? true : false;
    }
	
    public byte[] getData(boolean save) 
    {
        FBEncrypt plant = new FBEncrypt();
        
        plant.addShort(KeyID.KEY_PLANT_ID, id.shortValue());
        plant.addInt(KeyID.KEY_PLANT_GROW_TIME, grow_time.get());
        plant.addBoolean(KeyID.KEY_PLANT_HAS_BUG, hasBugOnPlant());
		plant.addInt(KeyID.KEY_PLANT_COMBO_ID, combo_id.get());
		plant.addInt(KeyID.KEY_PLANT_START_TIME, this.start_time);
		plant.addInt(KeyID.KEY_PLANT_FERTILIZER_ID, this.fertilizer_id.get());
		plant.addInt(KeyID.KEY_PLANT_FERTILIZER_REDUCE_TIME, this.time_reduced_by_fertilizer);
		
		if (save)
		{
			changed = false;
		}
        
        return plant.toByteArray();
    }
	
	public void displayDataPackage()
	{
		FBEncrypt plant = new FBEncrypt(getData(false));
		plant.displayDataPackage();
	}

    public String getName() 
	{
        if (id.get() < 0 ) return "";
        return Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_SEED][id.get()][DatabaseID.SEED_NAME]);
    }

    public int getGrowTime() 
	{
        return grow_time.get();
    }
    
    public boolean resetGrowTime()
    {
		changed = true;
        return grow_time.compareAndSet(grow_time.get(), 1);
    }
	
	public boolean setGrowTime(int time)
	{
		changed = true;
		return grow_time.compareAndSet(grow_time.get(), time);
	}
	
	public int GetComboID()
	{
		return combo_id.intValue();
	}
	
	public boolean SetComboID(int id)
	{
		changed = true;
		return combo_id.compareAndSet(GetComboID(), id);
	}
    
    public int getHarvestExp() 
	{
        if (id.get() < 0 ) return 0;
        return (int) Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_SEED][id.get()][DatabaseID.SEED_HARVEST_EXP]);
    }

    public short getItemReceiveRatio() 
	{
        if (id.get() < 0 ) return 0;
        return (short) Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_SEED][id.get()][DatabaseID.SEED_ITEM_RECEIVE_RATIO]);
    }

    public short getDiamondSkipTime() 
	{
        if (id.get() < 0 ) return 0;
        return (short) Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_SEED][id.get()][DatabaseID.SEED_DIAMOND_SKIP_TIME]);
    }

    public short getDiamondBuy() 
	{
        if (id.get() < 0 ) return 0;
        return (short) Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_SEED][id.get()][DatabaseID.SEED_DIAMOND_BUY]);
    }

    public long getGoldSellDefault() 
	{
        if (id.get() < 0 ) return 0;
        return Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_SEED][id.get()][DatabaseID.SEED_GOLD_SELL_DEFAULT]);
    }

    public long getGoldSellMin() 
	{
        if (id.get() < 0 ) return 0;
        return Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_SEED][id.get()][DatabaseID.SEED_GOLD_SELL_MIN]);
    }

    public long getGoldSellMax() 
	{
        if (id.get() < 0 ) return 0;
        return Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_SEED][id.get()][DatabaseID.SEED_GOLD_SELL_MAX]);
    }

    public short getBugId() 
	{
        if (id.get() < 0 ) return -1;
        return (short) Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_SEED][id.get()][DatabaseID.SEED_BUG_ID]);
    }

    public short getBugAppearRatio() 
	{
        if (id.get() < 0 ) return 0;
        return (short) Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_SEED][id.get()][DatabaseID.SEED_BUG_APPEAR_RATIO]);
    }
	
	public boolean isChanged()
	{
		return changed;
	}
}
