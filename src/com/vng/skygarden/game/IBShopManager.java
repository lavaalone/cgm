/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;
import com.vng.db.DBKeyValue;
import com.vng.log.*;
import com.vng.netty.*;
import com.vng.skygarden.DBConnector;
import com.vng.util.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author thinhnn3
 */
public class IBShopManager 
{
    private ConcurrentHashMap<Integer, IBShopPackage> _normal_packs; // static
	private ConcurrentHashMap<String, IBShopPackage> _gold_packs; // dynamic base on user level
	private ConcurrentHashMap<Integer, IBShopPackage> _all_packs;
	private DBKeyValue _db = null;
	
    public IBShopManager()
    {
        _normal_packs = new ConcurrentHashMap<Integer, IBShopPackage>();
		_gold_packs = new ConcurrentHashMap<String, IBShopPackage>();
		_all_packs = new ConcurrentHashMap<Integer, IBShopPackage>();
    }
	
	public void SetDatabase(DBKeyValue db)
	{
		_db = db;
	}
    
    public void loadIBShopPackages()
    {
		scheduleLoadIBShopPackages();
    }
	
	public int getSaleRemainingNumberFromDatabase(IBShopPackage pack)
	{
		try
		{
			String str_value = "";
			
			str_value = (String) _db.Get(KeyID.KEY_IBS_PACKAGE_ID + pack.getID());
			
			int num = Integer.parseInt(str_value);
			
			if (num < 0 )
			{
				pack.setRemainingNumber(0);
				return 0;	
			}
			
			pack.setRemainingNumber(num);
			
			return num;
		}
		catch (Exception ex)
		{
			LogHelper.LogException("IBShopManager.getSaleRemainingNumberFromDatabase()", ex);
			
			return 0;
		}
	}
	
	public void decreaseSaleTotalQuantity(IBShopPackage pack, int amount) throws Exception
	{
		if (pack.setRemainingNumber(pack.getRemainingNumber() - amount))
		{
			int new_number = 0;
			
			new_number = (int)_db.Decrease(KeyID.KEY_IBS_PACKAGE_ID + pack.getID(), amount);
			
			pack.setRemainingNumber(new_number);
		}		
	}
	
	public byte[] getItemListSendToClient(int level, String banned_list)
	{
		int max_pack_id = 0;
		FBEncrypt list = new FBEncrypt();
		
		for (Map.Entry<Integer, IBShopPackage> e : _normal_packs.entrySet())
		{
			if (!isPackBanned(banned_list, e.getValue().getID()))
			{
				list.addBinary(KeyID.KEY_IBS_PACKAGE_ID + e.getValue().getID(), e.getValue().getData());
			}
			
			max_pack_id = e.getValue().getID() > max_pack_id ? e.getValue().getID() : max_pack_id;
		}
		
		for (int gold_pack = 0; gold_pack < 6; gold_pack++)
		{
			String key = "ibshop_gold_pack" + "_" + level + "_" + gold_pack;
			if (_gold_packs.containsKey(key))
			{
				IBShopPackage pack = _gold_packs.get(key);
				if (!isPackBanned(banned_list, pack.getID()))
				{
					list.addBinary(KeyID.KEY_IBS_PACKAGE_ID + pack.getID(), pack.getData());
				}
				
				max_pack_id = pack.getID() > max_pack_id ? pack.getID() : max_pack_id;
			}
		}
		
		max_pack_id++;
		list.addInt(KeyID.KEY_IBS_MAX_PACKAGE_NUMBER, max_pack_id);
        
        return list.toByteArray();
	}
    
	public void scheduleLoadIBShopPackages()
	{		
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		ScheduledFuture<?> schedule_future = scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() 
			{
				try
				{
					for (int level = 0; level < DatabaseID.MAX_LEVEL; level++)
					{
						for (int pack = 0; pack < 6; pack++)
						{
							String key = "ibshop_gold_pack" + "_" + level + "_" + pack;
							Object obj = DBConnector.GetMembaseServerForTemporaryData().Get(key);
							if (obj != null)
							{
								IBShopPackage ibsp = new IBShopPackage();
								if (ibsp.loadConstantValuesFromExcel((String)obj))
								{
									_gold_packs.put(key, ibsp);
									_all_packs.put(ibsp.getID(), ibsp);
//									LogHelper.LogHappy("key := " + key + ", content := " + ibsp.toString());
								}
							}
						}
					}
				}
				catch (Exception e)
				{
					LogHelper.LogException("loadGoldPack", e);
				}
				
				try
				{
					for (int i = 0; i < DatabaseID.MAX_IBSHOP_PACK; i++)
					{
						String key = "ibshop_normal_pack" + "_" + i;
						Object obj = DBConnector.GetMembaseServerForTemporaryData().Get(key);
						if (obj != null)
						{
							IBShopPackage ibsp = new IBShopPackage();
							if (ibsp.loadConstantValuesFromExcel((String)obj))
							{
								_normal_packs.put(ibsp.getID(), ibsp);
								_all_packs.put(ibsp.getID(), ibsp);
//								LogHelper.LogHappy("key := " + key + ", content := " + ibsp.toString());
							}
						}
						else
							break;
					}
				}
				catch (Exception e)
				{
					LogHelper.LogException("loadNormalPack", e);
				}
			}
		}, 0, 60, TimeUnit.SECONDS);
	}
	
	public ConcurrentHashMap<String, IBShopPackage> goldPacks()
	{
		return _gold_packs;
	}
	
	public IBShopPackage getPack(int pack_id)
	{
		if (_all_packs.containsKey(pack_id))
		{
			return _all_packs.get(pack_id);
		}
		
		return null;
	}
	
	private boolean isPackBanned(String ban_list, int pack_id)
	{
		String[] aos = ban_list.split(";");
		for (String s : aos)
		{
			if (s.length() > 0 && Integer.parseInt(s) == pack_id)
				return true;
		}
		
		return false;
	}
}
