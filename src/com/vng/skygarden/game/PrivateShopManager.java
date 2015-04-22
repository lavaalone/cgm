package com.vng.skygarden.game;
import com.vng.db.DBKeyValue;
import com.vng.log.*;
import com.vng.netty.*;
import com.vng.util.*;
import java.util.*;

public class PrivateShopManager 
{
	private DBKeyValue _db = null;
	
	private String	_uid = "";
	
	private int current_slot_number;
	
	private int advertise_available_time; // the time point when advertise is available
	
	LinkedList<PrivateShopSlot> slots;
	
	final boolean LOG_SHOP = !true;
	
	PrivateShopManager(String uid)
	{
		_uid = uid;
		current_slot_number = -1;
		advertise_available_time = -1;
		slots = new LinkedList<PrivateShopSlot>();
	}
	
	public void SetDatabase(DBKeyValue db)
	{
		_db = db;
	}
	
	public void SetUserId(String uid)
	{
		_uid = uid;
	}
	
	public boolean initDefaultValues(String key)
	{
		// check again before init new values to avoid overwriting existed data.
		byte[] bin = null;
		try
		{
			 bin = _db.GetRaw(_uid + "_" + key);
		}
		catch (Exception e)
		{
			LogHelper.LogException("PrivateShopManager.initDefaultValues", e);
		}
		
		if (bin != null)
		{
			LogHelper.Log("PrivateShopManager.. can not overwrite existed data.");
			return false;
		}
		
		// start init default values.
		current_slot_number = DatabaseID.PRIVATE_SHOP_DEFAULT_SLOT_NUMBER;
		advertise_available_time = 0;
		
		for (int i=0; i< current_slot_number; i++)
		{
			PrivateShopSlot slot = new PrivateShopSlot(i);
			if (i < current_slot_number)
			{
				slot.setLockStatus(false);
			}
			
			slots.add(slot);
		}
		
		boolean result = saveDataToDatabase(key);
		
//		LogHelper.Log("PrivateShopManager.. init default values: " + result);
		
		return result;
	}
	
	public boolean loadFromDatabase(String key)
	{
		slots.clear();
		
		try
		{
			byte[] bin = _db.GetRaw(_uid + "_" + key);

			if (bin == null || bin.length == 0)
			{
				LogHelper.Log("PrivateShopManager.. err! null private shop data");
				return false;
			}

			FBEncrypt fb = new FBEncrypt();
			fb.decode(bin, true);

			current_slot_number = fb.getShort(KeyID.KEY_PS_CURRENT_SLOT_NUMBER);
			advertise_available_time = fb.getInt(KeyID.KEY_PS_ADVERTISE_AVAILABLE_TIME);

			for (int i = 0; i < current_slot_number; i++)
			{
				// load slot
				byte[] slot_bin = fb.getBinary(KeyID.KEY_PS_SLOT + i);
				if (slot_bin == null || slot_bin.length == 0)
				{
					LogHelper.Log("PrivateShopManager.. err! null slot data");
					return false;
				}

				FBEncrypt slotData = new FBEncrypt();
				slotData.decode(slot_bin, true);
				PrivateShopSlot slot = new PrivateShopSlot(slotData.getShort(KeyID.KEY_PS_SLOT_ID));
				slot.setLockStatus(slotData.getBoolean(KeyID.KEY_PS_SLOT_STATUS));

				// load item in slot
				byte[] item_bin = slotData.getBinary(KeyID.KEY_PS_ITEM);
				if (item_bin == null || item_bin.length == 0)
				{
					LogHelper.Log("PrivateShopManager.. err! null item data");
					return false;
				}

				FBEncrypt itemData = new FBEncrypt();
				itemData.decode(item_bin, true);
				slot.item.setType(itemData.getShort(KeyID.KEY_PS_ITEM_TYPE));
				slot.item.setId(itemData.getShort(KeyID.KEY_PS_ITEM_ID));
				slot.item.setNumber(itemData.getShort(KeyID.KEY_PS_ITEM_NUMBER));
				slot.item.setMoneyType(itemData.getShort(KeyID.KEY_PS_ITEM_MONEY_TYPE));
				slot.item.setPrice(itemData.getLong(KeyID.KEY_PS_ITEM_PRICE));
				slot.item.setStartDate(itemData.getInt(KeyID.KEY_PS_ITEM_START_SELL_DATE));
				slot.item.setEndDate(itemData.getInt(KeyID.KEY_PS_ITEM_END_SELL_DATE));
				slot.item.setStatus(itemData.getShort(KeyID.KEY_PS_ITEM_STATUS));
				slot.item.setCancelPrice(itemData.getShort(KeyID.KEY_PS_ITEM_CANCEL_PRICE));
				slot.item.setAdvertise(itemData.getBoolean(KeyID.KEY_PS_ITEM_ADVERTISE));
				slot.item.setAdvertiseEndTime(itemData.getInt(KeyID.KEY_PS_ITEM_END_ADVERTISE_TIME));
				slot.item.setBuyerID(itemData.getString(KeyID.KEY_PS_ITEM_BUYER_ID));

				// add to ram
				slots.add(slot);
			}
//			LogHelper.Log("PrivateShopManager.. load from database OK.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("PrivateShopManager.loadFromDatabase()", e);
			
			return false;
		}
			
		return true;
	}
	
	public PrivateShopManager(byte[] bin)
	{
		slots = new LinkedList<PrivateShopSlot>();
		
		try
		{
			FBEncrypt fb = new FBEncrypt();
			fb.decode(bin, true);

			current_slot_number = fb.getShort(KeyID.KEY_PS_CURRENT_SLOT_NUMBER);
			advertise_available_time = fb.getInt(KeyID.KEY_PS_ADVERTISE_AVAILABLE_TIME);

			for (int i = 0; i < current_slot_number; i++)
			{
				// load slot
				byte[] slot_bin = fb.getBinary(KeyID.KEY_PS_SLOT + i);
				if (slot_bin == null || slot_bin.length == 0)
				{
					LogHelper.Log("PrivateShopManager.. err! null slot data");
					return;
				}

				FBEncrypt slotData = new FBEncrypt();
				slotData.decode(slot_bin, true);
				PrivateShopSlot slot = new PrivateShopSlot(slotData.getShort(KeyID.KEY_PS_SLOT_ID));
				slot.setLockStatus(slotData.getBoolean(KeyID.KEY_PS_SLOT_STATUS));

				// load item in slot
				byte[] item_bin = slotData.getBinary(KeyID.KEY_PS_ITEM);
				if (item_bin == null || item_bin.length == 0)
				{
					LogHelper.Log("PrivateShopManager.. err! null item data");
					return;
				}

				FBEncrypt itemData = new FBEncrypt();
				itemData.decode(item_bin, true);
				slot.item.setType(itemData.getShort(KeyID.KEY_PS_ITEM_TYPE));
				slot.item.setId(itemData.getShort(KeyID.KEY_PS_ITEM_ID));
				slot.item.setNumber(itemData.getShort(KeyID.KEY_PS_ITEM_NUMBER));
				slot.item.setMoneyType(itemData.getShort(KeyID.KEY_PS_ITEM_MONEY_TYPE));
				slot.item.setPrice(itemData.getLong(KeyID.KEY_PS_ITEM_PRICE));
				slot.item.setStartDate(itemData.getInt(KeyID.KEY_PS_ITEM_START_SELL_DATE));
				slot.item.setEndDate(itemData.getInt(KeyID.KEY_PS_ITEM_END_SELL_DATE));
				slot.item.setStatus(itemData.getShort(KeyID.KEY_PS_ITEM_STATUS));
				slot.item.setCancelPrice(itemData.getShort(KeyID.KEY_PS_ITEM_CANCEL_PRICE));
				slot.item.setAdvertise(itemData.getBoolean(KeyID.KEY_PS_ITEM_ADVERTISE));
				slot.item.setAdvertiseEndTime(itemData.getInt(KeyID.KEY_PS_ITEM_END_ADVERTISE_TIME));
				slot.item.setBuyerID(itemData.getString(KeyID.KEY_PS_ITEM_BUYER_ID));

				// add to ram
				slots.add(slot);
			}
			// LogHelper.Log("PrivateShopManager.. load from database OK.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("PrivateShopManager.loadFromDatabase()", e);
			
			return;
		}
			
		return;
	}

	public byte[] getDataToClient()
	{
		FBEncrypt data = new FBEncrypt();
		
		data.addShort(KeyID.KEY_PS_CURRENT_SLOT_NUMBER, slots.size());
		data.addInt(KeyID.KEY_PS_ADVERTISE_AVAILABLE_TIME, this.advertise_available_time);
		
		data.addInt(KeyID.KEY_PS_NEXT_REQUIRED_DIAMOND, nextRequiredDiamond());
		data.addInt(KeyID.KEY_PS_NEXT_REQUIRED_FRIEND, nextRequiredFriend());
		
		for (PrivateShopSlot slot : slots)
		{
			// slot
			FBEncrypt slot_data = new FBEncrypt();
			slot_data.addShort(KeyID.KEY_PS_SLOT_ID, (short)slot.getId());
			slot_data.addBoolean(KeyID.KEY_PS_SLOT_STATUS, slot.getLockStatus());
			slot_data.addBinary(KeyID.KEY_PS_ITEM, slot.item.getData());
			
			data.addBinary(KeyID.KEY_PS_SLOT + slots.indexOf(slot), slot_data.toByteArray());
		}
		
		return data.toByteArray();
	}
	
	public boolean saveDataToDatabase(String key)
	{
		FBEncrypt data = new FBEncrypt();
		
		data.addShort(KeyID.KEY_PS_CURRENT_SLOT_NUMBER, slots.size());
		data.addInt(KeyID.KEY_PS_ADVERTISE_AVAILABLE_TIME, this.advertise_available_time);
		
		for (PrivateShopSlot slot : slots)
		{
			// get slot data
			FBEncrypt slot_data = new FBEncrypt();
			slot_data.addShort(KeyID.KEY_PS_SLOT_ID, (short)slot.getId());
			slot_data.addBoolean(KeyID.KEY_PS_SLOT_STATUS, slot.getLockStatus());
			slot_data.addBinary(KeyID.KEY_PS_ITEM, slot.item.getData());
			
			data.addBinary(KeyID.KEY_PS_SLOT + slots.indexOf(slot), slot_data.toByteArray());
		}
		
		if (LOG_SHOP)
		{
			LogPrivateShop();
		}
		
		return _db.SetRaw(_uid + "_" + key, data.toByteArray());
	}
	
	public void LogPrivateShop()
	{
		StringBuilder l = new StringBuilder();
		
		l.append(_uid);
		l.append('\t').append("private_shop");
		l.append('\t').append(slots.size());
		l.append('\t').append(this.advertise_available_time);
		
		for (PrivateShopSlot slot : slots)
		{
			l.append('\t').append(slot.getId());
			l.append('\t').append(slot.getLockStatus());
			l.append('\t').append(slot.item.getType());
			l.append('\t').append(slot.item.getId());
			l.append('\t').append(Misc.GetItemName(slot.item.getType(), slot.item.getId()));
			l.append('\t').append(slot.item.getNumber());
			l.append('\t').append(slot.item.getMoneyType());
			l.append('\t').append(slot.item.getPrice());
			l.append('\t').append(slot.item.getStartDate());
			l.append('\t').append(slot.item.getEndDate());
			l.append('\t').append(slot.item.getStatus());
			l.append('\t').append(slot.item.getCancelPrice());
			l.append('\t').append(slot.item.getAdvertiseStatus());
			l.append('\t').append(slot.item.getAdvertiseEndTime());
			l.append('\t').append(slot.item.getBuyerID());
		}
		
		LogHelper.Log(l.toString());
	}
	
	private int nextRequiredFriend()
	{
		int next_slot_id = -1;
		for (int i = DatabaseID.PRIVATE_SHOP_START_SLOT_FRIEND; i < DatabaseID.PRIVATE_SHOP_START_SLOT_FRIEND + DatabaseID.PRIVATE_SHOP_NUMBER_OF_SLOT_FRIEND; i++)
		{
			boolean found = false;
			for (PrivateShopSlot slot : slots)
			{
				if (slot.getId() == i)
				{
					found = true;
					break;
				}
			}
			
			if (found == true)
			{
				continue;
			}
			else
			{
				next_slot_id = i;
				break;
			}
		}
		
		if (next_slot_id == -1)
		{
			return -1;
		}
		else
		{
			return (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PRIVATE_SHOP][next_slot_id][DatabaseID.PS_SHOP_REQUIRED_FRIEND]);
		}
	}
	
	private int nextRequiredDiamond()
	{
		int next_slot_id = -1;
		for (int i = DatabaseID.PRIVATE_SHOP_START_SLOT_DIAMOND; i < DatabaseID.PRIVATE_SHOP_MAX_SLOT; i++)
		{
			boolean found = false;
			for (PrivateShopSlot slot : slots)
			{
				if (slot.getId() == i)
				{
					found = true;
					break;
				}
			}
			
			if (found == true)
			{
				continue;
			}
			else
			{
				next_slot_id = i;
				break;
			}
		}
		
		if (next_slot_id == -1)
		{
			return -1;
		}
		else
		{
			return (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PRIVATE_SHOP][next_slot_id][DatabaseID.PS_SHOP_REQUIRED_DIAMOND]);
		}
	}
	
	public int getAdsAvailableTime()
	{
		return this.advertise_available_time;
	}
	
	public void setAdsAvailableTime(int time_in_second)
	{
		this.advertise_available_time = time_in_second;
	}
}


class PrivateShopSlot 
{
	private int id;

	private boolean is_locked;
	
	PrivateShopItem item;
	
	PrivateShopSlot(int slot_id)
	{
		id = slot_id;
		is_locked = true;
		item = new PrivateShopItem();
	}
	
	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}

	public boolean getLockStatus() 
	{
		return is_locked;
	}
	
	public void setLockStatus(boolean status)
	{
		this.is_locked = status;
	}
}