package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import com.vng.skygarden.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Decor
{
    private AtomicInteger id = new AtomicInteger(-1);
    private boolean load_result = true;
	
	private boolean changed = true;

    public Decor()
    {
    }

    public Decor(byte[] bin_db)
    {
        FBEncrypt decor = new FBEncrypt(bin_db);
		
		int decor_id = -1;
		if (decor.hasKey(KeyID.KEY_DECOR_ID))
		{
			decor_id = decor.getInt(KeyID.KEY_DECOR_ID);
		}
		
		// remove decor tet
		if (decor_id >= 17 && decor_id <= 22)
			return;
		
		// load decor id
		if (!id.compareAndSet(-1, decor_id))
		{
			LogHelper.Log("Decor.. can not load ID.");
			load_result = false;
			return;
		}
    }
    
    public boolean isLoadSuccess()
    {
        return load_result;
    }

    public int getID() 
    {
        return id.get();
    }
	
	public boolean createNewDecor(int _id)
    {
		changed = true;
        return id.compareAndSet(-1, _id);
    }
	
	public boolean deleteDecor()
    {
		changed = true;
        return id.compareAndSet(getID(), -1);
    }
	
	 public void increaseDecorID()
    {
        if (id.get() < Server.s_globalDB[DatabaseID.SHEET_DECOR].length - 1)
		{
			id.incrementAndGet();
		}
		
		changed = true;
    }
    
    public void decreaseDecorID()
    {
        id.decrementAndGet();
		
		changed = true;
    }
	
	public String getName() 
	{
		if (id.get() < 0) return "";
		
        return Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_DECOR][id.get()][DatabaseID.DECOR_NAME]);
    }

    public short getExpIncrease() 
	{
        return (short) Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DECOR][id.get()][DatabaseID.DECOR_EXP_INCREASE]);
    }

    public short getTimeDecrease() 
	{
        return (short) Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DECOR][id.get()][DatabaseID.DECOR_TIME_DECREASE]);
    }

//    public long getGoldUpgrade() 
//	{
//		long v = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DECOR][id.get()][DatabaseID.DECOR_GOLD_UPGRADE]);
//        return v > 0 ? v : 0;
//    }
//
//    public String getRequiredPearl() 
//	{
//        return Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_DECOR][id.get()][DatabaseID.DECOR_PEARL_ID_NUM]);
//    }
//
//    public short getUpgradeRatio() 
//	{
//        return (short) Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DECOR][id.get()][DatabaseID.DECOR_UPGRADE_RATIO]);
//    }
//	
//	public double GetBlessingRatio() 
//	{
//		return Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_DECOR][id.get()][DatabaseID.DECOR_BLESSING_RATIO]);
//	}
//	
//	public double GetBadLuckRatio() 
//	{
//		return Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_DECOR][id.get()][DatabaseID.DECOR_BAD_LUCK_RATIO]);
//	}
//
//    public long getGoldDefault() 
//	{
//        return Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DECOR][id.get()][DatabaseID.DECOR_GOLD_DEFAULT]);
//    }
//
//    public long getGoldMin() 
//	{
//        return Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DECOR][id.get()][DatabaseID.DECOR_GOLD_MIN]);
//    }
//
//    public long getGoldMax() 
//	{
//        return Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DECOR][id.get()][DatabaseID.DECOR_GOLD_MAX]);
//    }
	
	public int getBugAppearRatio()
	{
		if (id.get() < 0) return 0;
		
		return (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DECOR][id.get()][DatabaseID.DECOR_BUG_APPEAR_RATIO]);
	}
	
	public String getUseDuration()
	{
		if (id.get() < 0) return "null";
		
		return Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_DECOR][id.get()][DatabaseID.DECOR_USE_DURATION]);
	}
	
    public byte[] getData(boolean save)
    {
        FBEncrypt decor = new FBEncrypt();
        decor.addInt(KeyID.KEY_DECOR_ID, getID());
		
		if (save)
		{
			changed = false;
		}
        
        return decor.toByteArray();
    }
	
	public void displayDataPackage()
	{
		FBEncrypt decor = new FBEncrypt(getData(false));
		// decor.displayDataPackage();
		LogHelper.Log("decor_id = " + decor.getShort(KeyID.KEY_DECOR_ID));
	}
	
	public boolean isChanged()
	{
		return changed;
	}
}