/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;
import com.vng.util.*;
import com.vng.netty.*;
import com.vng.log.*;
import com.vng.skygarden.SkyGarden;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author thinhnn3
 */
public class IBShopPackage
{
    private int id;
	private int pack_type;
	private String pack_name = "";
	private String pack_des = "";
	private String pack_name_en = "";
	private String pack_des_en = "";
	private String pack_name_sc = "";
	private String pack_des_sc = "";
	private String pack_name_tc = "";
	private String pack_des_tc = "";
    
    private int item_id;
    private int item_type;
    private int item_quantity;
    
    private boolean is_active;
    private boolean is_new;
    private boolean is_hot;
    
    private boolean is_sale_off;
    private long sale_off_percent;
    
    private boolean has_promotion;
    private String gift_when_buy;
	private String gift_event;
    
    private long required_gold;
    private long required_diamond;
    private long required_reputation;
    
	private boolean has_time_limit;
    private String sale_duration;
    
	private boolean has_sale_limit;
	private int sale_total_number;
	private int unlock_level;
	
	private int display_idx;

	private AtomicInteger remaining_number = null;
    
    IBShopPackage()
    {
        id = -1;
		pack_type = -1;
        
        item_id = -1;
        item_type = -1;
        item_quantity = -1;
        
        is_active = false;
        is_new = false;
        is_hot = false;
        
        is_sale_off = false;
        sale_off_percent = -1;
        
        has_promotion = false;
        gift_when_buy = null;
		gift_event = null;
        
        required_gold = -1;
        required_diamond = -1;
        required_reputation = -1;
        
		has_time_limit = false;
        sale_duration = null;
		
		has_sale_limit = false;
		sale_total_number = -1;
		unlock_level = 0;
		
		display_idx = -1;
		
		remaining_number = new AtomicInteger(0);
    }
    
    public boolean loadConstantValuesFromExcel(int index)
    {
        try
        {
            id = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_ID]);
			pack_type = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_TYPE]);
			pack_name = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_NAME]);
			pack_des = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_DESCRIPTION]);
			pack_name_en = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_NAME_EN]);
			pack_des_en = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_DESCRIPTION_EN]);
			pack_name_sc = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_NAME_SC]);
			pack_des_sc = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_DESCRIPTION_SC]);
			pack_name_tc = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_NAME_TC]);
			pack_des_tc = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_DESCRIPTION_TC]);
			
            item_id = GetItemID(index);
            item_type = GetItemType(index);
            item_quantity = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_ITEM_QUANTITY]);
			
            is_active = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_IS_ACTIVE]) == 1? true : false;
            is_new = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_IS_NEW]) == 1? true : false;
            is_hot = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_IS_HOT]) == 1? true : false;
			
            is_sale_off = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_IS_SALE_OFF]) == 1? true : false;
            sale_off_percent = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_SALE_OFF_PERCENT]);
			
            has_promotion = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_HAS_PROMOTION]) == 1? true : false;
			
			gift_when_buy = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_GIFT_WHEN_BUY]);
			/* Event 30-4 */
			gift_event = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_EVENT_GIFT]);
            
            required_gold = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_REQUIRED_GOLD]);
            required_diamond = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_REQUIRED_DIAMOND]);
            required_reputation = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_REQUIRED_REPUTATION]);
			
			has_time_limit = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_HAS_TIME_LIMIT]) == 1? true : false;
            sale_duration = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_SALE_DURATION]);
			
			has_sale_limit = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_HAS_SALE_LIMIT]) == 1? true : false;
			sale_total_number = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_SALE_TOTAL_QUANTITY]);
			unlock_level = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_UNLOCK_LEVEL]);
			
			display_idx = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_DISPLAY_INDEX]);
        }
        catch (Exception e)
        {
            LogHelper.LogException("IBShopPackage.LoadConstantValue", e);
            return false;
        }
		
        return true;
    }
	
    public boolean loadConstantValuesFromExcel(String content)
    {
        try
        {
			String[] aos = content.split(";");
            id	= Integer.parseInt(aos[DatabaseID.IBSHOP_ID]);
			pack_type = Integer.parseInt(aos[DatabaseID.IBSHOP_TYPE]);
			pack_name = aos[DatabaseID.IBSHOP_NAME];
			pack_des = aos[DatabaseID.IBSHOP_DESCRIPTION];
			pack_name_en = aos[DatabaseID.IBSHOP_NAME_EN];
			pack_des_en = aos[DatabaseID.IBSHOP_DESCRIPTION_EN];
			pack_name_sc = aos[DatabaseID.IBSHOP_NAME_SC];
			pack_des_sc = aos[DatabaseID.IBSHOP_DESCRIPTION_SC];
			pack_name_tc = aos[DatabaseID.IBSHOP_NAME_TC];
			pack_des_tc = aos[DatabaseID.IBSHOP_DESCRIPTION_TC];
			
			String item_name = aos[DatabaseID.IBSHOP_ITEM_NAME];
			item_type = Integer.parseInt(item_name.split(":")[0]);
            item_id = Integer.parseInt(item_name.split(":")[1]);
            item_quantity = Integer.parseInt(aos[DatabaseID.IBSHOP_ITEM_QUANTITY]);
			
            is_active = Integer.parseInt(aos[DatabaseID.IBSHOP_IS_ACTIVE]) == 1? true : false;
            is_new = Integer.parseInt(aos[DatabaseID.IBSHOP_IS_NEW]) == 1? true : false;
            is_hot = Integer.parseInt(aos[DatabaseID.IBSHOP_IS_HOT]) == 1? true : false;
			
            is_sale_off = Integer.parseInt(aos[DatabaseID.IBSHOP_IS_SALE_OFF]) == 1? true : false;
            sale_off_percent = Long.parseLong(aos[DatabaseID.IBSHOP_SALE_OFF_PERCENT]);
			
            has_promotion = Integer.parseInt(aos[DatabaseID.IBSHOP_HAS_PROMOTION]) == 1? true : false;
			
			gift_when_buy = aos[DatabaseID.IBSHOP_GIFT_WHEN_BUY];
			/* Event 30-4 */
			gift_event = aos[DatabaseID.IBSHOP_EVENT_GIFT];
            
            required_gold = Long.parseLong(aos[DatabaseID.IBSHOP_REQUIRED_GOLD]);
            required_diamond = Long.parseLong(aos[DatabaseID.IBSHOP_REQUIRED_DIAMOND]);
            required_reputation = Long.parseLong(aos[DatabaseID.IBSHOP_REQUIRED_REPUTATION]);
			
			has_time_limit = Integer.parseInt(aos[DatabaseID.IBSHOP_HAS_TIME_LIMIT]) == 1? true : false;
            sale_duration = aos[DatabaseID.IBSHOP_SALE_DURATION];
			
			has_sale_limit = Integer.parseInt(aos[DatabaseID.IBSHOP_HAS_SALE_LIMIT]) == 1? true : false;
			sale_total_number = Integer.parseInt(aos[DatabaseID.IBSHOP_SALE_TOTAL_QUANTITY]);
			unlock_level = Integer.parseInt(aos[DatabaseID.IBSHOP_UNLOCK_LEVEL]);
			
			display_idx = Integer.parseInt(aos[DatabaseID.IBSHOP_DISPLAY_INDEX]);
        }
        catch (Exception e)
        {
//            LogHelper.LogException("IBShopPackage.LoadConstantValue", e);
            return false;
        }
		
        return true;
    }
	
	private int GetItemType(int index)
	{
		String s = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_ITEM_NAME]);
		String[] name = s.split(":");
		
		if (name.length < 2)
		{
			LogHelper.Log("IBShopPakage.. err! invalid item info at pack " + index);
			return -1;
		}
		else
		{
			int type = -1;
			try
			{
				type = Integer.parseInt(name[0]);
			}
			catch (Exception e)
			{
				LogHelper.LogException("IBShopPackage.GetItemType", e);
				type = -1;
			}
			
			return type;
		}
	}
	
	private int GetItemID(int index)
	{
		String s = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][index][DatabaseID.IBSHOP_ITEM_NAME]);
		String[] name = s.split(":");
		if (name.length < 2)
		{
			LogHelper.Log("IBShopPakage.. err! invalid item info at pack " + index);
			return -1;
		}
		else
		{
			int id = -1;
			try
			{
				id = Integer.parseInt(name[1]);
			}
			catch (Exception e)
			{
				LogHelper.LogException("IBShopPackage.GetItemType", e);
				id = -1;
			}
			
			return id;
		}
	}
    
    public int getID()
    {
        return id;
    }
	
	public int getPackType()
	{
		return pack_type;
	}
	
	public String getPackName()
	{
		return pack_name;
	}
	
	public String getPackNameEN()
	{
		return pack_name_en;
	}
	
	public String getPackNameSC()
	{
		return pack_name_sc;
	}
	
	public String getPackNameTC()
	{
		return pack_name_tc;
	}
	
	public String getPackDescription()
	{
		return pack_des;
	}
	
	public String getPackDescriptionEN()
	{
		return pack_des_en;
	}
	
	public String getPackDescriptionSC()
	{
		return pack_des_sc;
	}
	
	public String getPackDescriptionTC()
	{
		return pack_des_tc;
	}
    
    public int getItemID()
    {
        return item_id;
    }
    
    public int getItemType()
    {
        return item_type;
    }
    
    public int getItemQuantity()
    {
        return item_quantity;
    }
    
    public boolean isActive()
    {
        return is_active;
    }
	
	public boolean isNew()
	{
		return is_new;
	}
	
	public boolean isHot()
	{
		return is_hot;
	}
    
    public boolean isSaleOff()
    {
		String start_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][9][DatabaseID.EVENT_GLOBAL_START_DATE]);
		String end_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][9][DatabaseID.EVENT_GLOBAL_END_DATE]);
		if(Misc.InEvent(start_event_time, end_event_time))
		{
			return is_sale_off;
		}
		else
		{
			return false;
		}
    }
    
    public long getSaleOffPercent()
    {
        return sale_off_percent;
    }
    
    public boolean hasPromotion()
    {
        return has_promotion;
    }
    
    public String getGiftWhenBuy()
    {
		if (SkyGarden.InSaleOffEvent() && gift_event != null && !gift_event.equals("")) {
			return gift_event;
		}
		
        return gift_when_buy;
    }
    
    public long getPriceGold()
    {
        return required_gold;
    }
    
    public long getPriceDiamond()
    {
        return required_diamond;
    }
    
    public long getPriceReputation()
    {
        return required_reputation;
    }
	
	public int getSaleToTalNumber()
	{
		return sale_total_number;
	}
	
	public int GetUnlockLevel()
	{
		return unlock_level;
	}
	
	public boolean hasTimeLimit()
	{
		return has_time_limit;
	}
	
	public String getSaleDuration()
	{
		return sale_duration;
	}
	
	public boolean hasSaleLimit()
	{
		return has_sale_limit;
	}
	
	public boolean setRemainingNumber(int value)
	{
		return remaining_number.compareAndSet(remaining_number.get(), value);
	}
	
	public int getRemainingNumber()
	{
		return remaining_number.get();
	}
	
	public int getDisplayIndex()
	{
		return display_idx;
	}
	
	public byte[] getData()
	{
		FBEncrypt data = new FBEncrypt();
		data.addInt(KeyID.KEY_IBS_PACKAGE_INDEX, getID());
		data.addInt(KeyID.KEY_IBS_PACKAGE_TYPE, getPackType());
		data.addString(KeyID.KEY_IBS_PACKAGE_NAME, getPackName());
		data.addString(KeyID.KEY_IBS_PACKAGE_DES, getPackDescription());
		data.addInt(KeyID.KEY_IBS_ITEM_ID, getItemID());
		data.addInt(KeyID.KEY_IBS_ITEM_TYPE, getItemType());
		data.addInt(KeyID.KEY_IBS_ITEM_QUANTIFY, getItemQuantity());
		data.addBoolean(KeyID.KEY_IBS_IS_ACTIVE, isActive());
		data.addBoolean(KeyID.KEY_IBS_IS_NEW, isNew());
		data.addBoolean(KeyID.KEY_IBS_IS_HOT, isHot());
		data.addBoolean(KeyID.KEY_IBS_IS_SALE_OFF, isSaleOff());
		data.addLong(KeyID.KEY_IBS_SALE_OFF_PERCENT, getSaleOffPercent());
		data.addBoolean(KeyID.KEY_IBS_HAS_PROMOTION, hasPromotion());
		data.addString(KeyID.KEY_IBS_GIFT_WHEN_BUY, getGiftWhenBuy());
		data.addLong(KeyID.KEY_IBS_REQUIRED_GOLD, getPriceGold());
		data.addLong(KeyID.KEY_IBS_REQUIRED_DIAMOND, getPriceDiamond());
		data.addLong(KeyID.KEY_IBS_REQUIRED_REPUTATION, getPriceReputation());
		data.addBoolean(KeyID.KEY_IBS_HAS_TIME_LIMIT, hasTimeLimit());
		data.addString(KeyID.KEY_IBS_SALE_DURATION, getSaleDuration());
		data.addBoolean(KeyID.KEY_IBS_HAS_SALE_LIMIT, hasSaleLimit());
		data.addInt(KeyID.KEY_IBS_SALE_TOTAL_QUANTITY, getSaleToTalNumber());
		data.addInt(KeyID.KEY_IBS_UNLOCK_LEVEL, GetUnlockLevel());
		data.addInt(KeyID.KEY_IBS_REMAINING_QUANTITY, getRemainingNumber());
		data.addInt(KeyID.KEY_IBS_DISPLAY_IDX, getDisplayIndex());
		data.addString(KeyID.KEY_IBS_PACKAGE_NAME_EN, getPackNameEN());
		data.addString(KeyID.KEY_IBS_PACKAGE_DES_EN, getPackDescriptionEN());
		data.addString(KeyID.KEY_IBS_PACKAGE_NAME_SC, getPackNameSC());
		data.addString(KeyID.KEY_IBS_PACKAGE_DES_SC, getPackDescriptionSC());
		data.addString(KeyID.KEY_IBS_PACKAGE_NAME_TC, getPackNameTC());
		data.addString(KeyID.KEY_IBS_PACKAGE_DES_TC, getPackDescriptionTC());
		return data.toByteArray();
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("id").append(":=").append(id);
		sb.append(",pack_type").append(":=").append(pack_type);
		sb.append(",pack_name").append(":=").append(pack_name);
		sb.append(",pack_des").append(":=").append(pack_des);
		sb.append(",item_type").append(":=").append(item_type);
		sb.append(",item_id").append(":=").append(item_id);
		sb.append(",item_quantity").append(":=").append(item_quantity);
		sb.append(",is_active").append(":=").append(is_active);
		sb.append(",is_new").append(":=").append(is_new);
		sb.append(",is_hot").append(":=").append(is_hot);
		sb.append(",is_sale_off").append(":=").append(is_sale_off);
		sb.append(",sale_off_percent").append(":=").append(sale_off_percent);
		sb.append(",has_promotion").append(":=").append(has_promotion);
		sb.append(",gift_when_buy").append(":=").append(gift_when_buy);
		sb.append(",gift_event").append(":=").append(gift_event);
		sb.append(",required_gold").append(":=").append(required_gold);
		sb.append(",required_diamond").append(":=").append(required_diamond);
		sb.append(",required_reputation").append(":=").append(required_reputation);
		sb.append(",has_time_limit").append(":=").append(has_time_limit);
		sb.append(",sale_duration").append(":=").append(sale_duration);
		sb.append(",has_sale_limit").append(":=").append(has_sale_limit);
		sb.append(",sale_total_number").append(":=").append(sale_total_number);
		sb.append(",unlock_level").append(":=").append(unlock_level);
		sb.append(",display_idx").append(":=").append(display_idx);
		return sb.toString();
	}
	
	public boolean isMysteryBox()
	{
		try
		{
			String[] s = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][id][DatabaseID.IBSHOP_ITEM_NAME]).split(":");
			
			if (s.length > 2)
			{
				return true;
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("IBShoPackage.isMysteryBox", e);
		}
		
		return false;
	}
	
	public String GetRandomItemInMysteryBox() throws Exception
	{
		if (!isMysteryBox())
		{
			LogHelper.Log("IBShopPackage.. err! pack " + id + " is not a mystery box");
			return "";
		}
		else
		{
			String[] info = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_IBSHOP_ITEM][id][DatabaseID.IBSHOP_ITEM_NAME]).split(":");
			
			if (info.length < 6)
			{
				LogHelper.Log("IBShopPackage.. err! invalid mystery box info");
				return "";
			}
			else
			{
				// type:id:percent:..:..:..
				double current_percent = 0.0;
				double r = Misc.RANDOM_DOUBLE_RANGE(0.0, 100.0);
				
				for (int i = 0; i < info.length - 2; i += 3)
				{
					int type = Integer.parseInt(info[i]);
					int id = Integer.parseInt(info[i+1]);
					double rate = Double.parseDouble(info[i+2]);
					
					current_percent +=rate;
					
					// StringBuilder s = new StringBuilder();
					// s.append("GetRandomItemInMysteryBox:").append(type);
					// s.append(":").append(id);
					// s.append(":").append(Misc.GetItemName(type, id));
					// s.append(":").append(rate);
					// s.append(":").append(current_percent);
					// s.append(":").append(r);
					// LogHelper.Log("IBShopPackage.GetRandomItemInMysteryBox.. ###DEBUG: " + s.toString());
					
					if (current_percent >= r)
					{
						// LogHelper.Log("IBShopPackage.. return item " + Misc.GetItemName(type, id) + " from mystery box");
						
						StringBuilder result = new StringBuilder();
						result.append(type).append(":").append(id);
						return result.toString();
					}
				}
			}
		}
		return "";
	}
}

