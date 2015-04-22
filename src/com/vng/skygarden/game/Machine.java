package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import java.util.*;

public class Machine
{
	private byte 	floor;
	private byte 	id;
	
	private short 	level;
	private int 	active_time;
	private byte 	status;
	private int 	start_time;
	private byte 	slot_max;
	private byte 	slot_cur;
	private byte 	product_count;
	
	private byte	product_completed_num;
	ArrayList<String>	item_drop_list;		// type:id:num
	
	ArrayList<SlotMachine> slot;
	ArrayList<byte[]> stock;
	
	Decor decor = null;

	private boolean load_result = true;
	private boolean need_save = false;
	
	private boolean LOG_MACHINE = !true;
	
	public Machine(int floor)
	{
		this.floor = (byte)floor;
		this.id = Misc.getMachineID(floor);
		
		this.level = 0;
		this.active_time = 0;
		this.status = DatabaseID.MACHINE_LOCK;
		this.start_time = -1;
		this.slot_max = 2;
		this.slot_cur = 0;
		this.product_count = 0;
		
		product_completed_num = 0;
		item_drop_list = new ArrayList<String>();

		slot = new ArrayList<SlotMachine>();

		stock = new ArrayList<byte[]>();

		decor = new Decor();
		
		// LogHelper.Log("Init machine OK!");
	}
	
	public Machine(byte[] machine_bin)
	{
		FBEncrypt machine = new FBEncrypt(machine_bin);

		this.id = machine.getByte(KeyID.KEY_MACHINE_ID);
		this.floor = machine.getByte(KeyID.KEY_MACHINE_FLOOR);
		this.level = machine.getShort(KeyID.KEY_MACHINE_LEVEL);
		this.active_time = machine.getInt(KeyID.KEY_MACHINE_ACTIVE_TIME);
		this.status = machine.getByte(KeyID.KEY_MACHINE_STATUS);
		this.start_time = machine.getInt(KeyID.KEY_MACHINE_START_TIME);
		this.slot_max = machine.getByte(KeyID.KEY_MACHINE_SLOT_MAX);
		this.slot_cur = machine.getByte(KeyID.KEY_MACHINE_SLOT_CUR);
		this.product_count = machine.getByte(KeyID.KEY_MACHINE_PRODUCT_COUNT);

		this.product_completed_num = 0;
		item_drop_list = new ArrayList<String>();
		
		if (machine.hasKey(KeyID.KEY_MACHINE_ITEM_DROP_LIST))
		{
			String drop_list = machine.getString(KeyID.KEY_MACHINE_ITEM_DROP_LIST);
			
			// LogHelper.Log("load machine " + id + ": _" + drop_list + "_");
			
			if(!drop_list.equals(""))
			{
				if (drop_list.contains(":18:"))
					drop_list = drop_list.replace(":18:", ":1:8:");
				
				String[] items = drop_list.split(":");
				
				if (items.length % 3 == 0)
				{
					for (int d = 0; d < items.length; d += 3)	// type:id:num
					{
						item_drop_list.add(items[d] + ":" + items[d+1] + ":" + items[d+2]);
					}
				}
			}
		}
		
		slot = new ArrayList<SlotMachine>();
		stock = new ArrayList<byte[]>();
		
		for (int i = 0; i < this.slot_cur; i++)
		{
			byte[] slot_data = machine.getBinary(KeyID.KEY_MACHINE_SLOT + i);
			
			if (slot_data == null || slot_data.length == 0)
			{
				LogHelper.Log("Cannot load product slot " + i);
				load_result = false;
				return;
			}

			SlotMachine _slot_machine = new SlotMachine(slot_data);
			if (_slot_machine.isLoadSuccess() == false)
			{
				load_result = false;
				return;
			}
			
			slot.add(_slot_machine);
		}

		for (int i = 0; i < this.product_count; i++)
		{
			byte[] prod_data = machine.getBinary(KeyID.KEY_MACHINE_PRODUCT + i);
			
			if (prod_data == null || prod_data.length == 0)
			{
				LogHelper.Log("Cannot load product data " + i);
				load_result = false;
				return;
			}

			stock.add(prod_data);
		}
		
		// check completed product in slot then move to stock
		if (this.slot_cur > 0)
		{
			while (this.slot_cur > 0 && getFirstSlotTime() < Misc.SECONDS())
			{
				
				if (slot.size() > 0)
				{
					int prod_id = slot.get(0).getID();

					removeFirstSlot();

					addProductToStock(prod_id);
					
					product_completed_num++;
				
					need_save = true;
				}
			}
		}

		//load decor
		byte[] bdecor = machine.getBinary(KeyID.KEY_MACHINE_DECOR);
		if (bdecor == null || bdecor.length == 0)
		{
			LogHelper.Log("Machine.. can not load decor data.");
			load_result = false;
			return;
		}

		decor = new Decor(bdecor);
		if (decor.isLoadSuccess() == false)
		{
			load_result = false;
			return;
		}
		
		// LogHelper.Log("Load machine...OK!");
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
	
	public byte getProductCompleteNum()
	{
		return product_completed_num;
	}
	
	public void resetProductCompleteNum()
	{
		product_completed_num = 0;
	}
	
	public void addDropItem(String item)
	{
		if (product_count > item_drop_list.size())	// make sure item_drop_count always <= product_count
		{
			item_drop_list.add(item);
		}
	}
	
	public void increaseSlot()
	{
		slot_max += 1;
	}
	
	public void addProduct(int product_id)
	{
		setSlotCur(slot_cur + 1);

		int product_time = 0;

		if (slot.size() > 0)
		{
			product_time = slot.get(slot.size() - 1).getProductTime() + getProductCompleteTime(product_id);
		}
		else
		{
			product_time = Misc.SECONDS() + getProductCompleteTime(product_id);
		}

		// byte reduce_time_persent = Byte.parseByte(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_MACHINE_UNLOCK][this.id][getLevel()]).split(":")[DatabaseID.MACHINE_REDUCE_TIME]);
		byte reduce_time_percent = (byte)Server.s_globalMachineUnlockData[this.floor][this.level][DatabaseID.MACHINE_REDUCE_TIME];
		
		if (reduce_time_percent > 0)
		{
			int time_bonus = (getProductCompleteTime(product_id) * reduce_time_percent / 100);
			product_time -= time_bonus;
		}
		
		String start_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][10][DatabaseID.EVENT_GLOBAL_START_DATE]);
		String end_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][10][DatabaseID.EVENT_GLOBAL_END_DATE]);
		if(Misc.InEvent(start_event_time, end_event_time))
		{
			// get machine id
			int machine_id = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][10][DatabaseID.EVENT_GLOBAL_BONUS_GOLD_RATE]);
			LogHelper.LogHappy("Machine id := " + id);
			LogHelper.LogHappy("Event machine id := " + machine_id);
			if (id == machine_id || machine_id == -1)
			{
				// get reduce percent
				int reduce_percent = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][10][DatabaseID.EVENT_GLOBAL_BONUS_EXP_RATE]);
				LogHelper.LogHappy("Reduce time percent := " + reduce_percent);
				LogHelper.LogHappy("Product time before := " + product_time);
				int time_bonus = (getProductCompleteTime(product_id) * reduce_percent / 100);
				product_time -= time_bonus;
				LogHelper.LogHappy("Product time after := " + product_time);
			}
		}
		
		SlotMachine _slot_machine = new SlotMachine(product_id, product_time);
		if (_slot_machine.isLoadSuccess() == false)
		{
			load_result = false;
			LogHelper.Log("Can not add product!!!");
			return;
		}
		
		slot.add(_slot_machine);
	}
	
	public void addProductToStock(int product_id)
	{
		try
		{
			FBEncrypt prod = new FBEncrypt();

			prod.addByte(KeyID.KEY_STOCK_PRODUCT_INDEX, this.product_count);
			prod.addByte(KeyID.KEY_STOCK_PRODUCT_ID, product_id);

			// need re-check how to calc exp for product (before start product or when product complete)
			
			long exp = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PRODUCT][product_id][DatabaseID.PRODUCT_EXP_RECEIVE]);
			// byte exp_bonus = Byte.parseByte(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_MACHINE_UNLOCK][this.id][getLevel()]).split(":")[DatabaseID.MACHINE_EXP_BONUS]);
			byte exp_bonus = (byte)Server.s_globalMachineUnlockData[this.floor][this.level][DatabaseID.MACHINE_EXP_BONUS];
			long exp_receive = exp + (exp * exp_bonus / 100);

			prod.addLong(KeyID.KEY_STOCK_PRODUCT_EXP, exp_receive);

			this.product_count += 1;

			stock.add(prod.toByteArray());
		}
		catch (Exception e)
		{
			LogHelper.LogException("Machine.addProductToStock", e);
		}
	}
	
	public boolean removeFirstProductInStock()
	{
		if (stock.size() <= 0 || this.product_count <= 0)
		{
			LogHelper.Log("Stock is empty. Server need check!!!");
			return false;
		}
		
		stock.remove(0);

		setProductCount(this.product_count - 1);
		
		return true;
	}

	public int removeFirstDropItem()
	{
		int item_id = -1;
		int item_type = -1;
		
		try
		{
			if (item_drop_list != null)
			{
				if (item_drop_list.size() <= 0)
				{
//					LogHelper.Log("item_drop_list is empty. Server need check!!!");
					return -1;
				}		
				item_type = Integer.parseInt(item_drop_list.get(0).split(":")[0]);
				
				if (item_type == DatabaseID.IT_EVENT)
					item_id = -1;
				else
					item_id = Integer.parseInt(item_drop_list.get(0).split(":")[1]);
				
				item_drop_list.remove(0);
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("Machine.removeFirstDropItem", e);
		}
		
		return item_id;
	}
	
	public String GetFirstDropItem()
	{
		StringBuilder sb = new StringBuilder();
		
		int item_id = -1;
		int item_type = -1;
		
		try
		{
			if (item_drop_list != null)
			{
				if (item_drop_list.size() <= 0)
				{
					return "";
				}
				item_type = Integer.parseInt(item_drop_list.get(0).split(":")[0]);
				item_id = Integer.parseInt(item_drop_list.get(0).split(":")[1]);
				
				sb.append(item_type);
				sb.append(":").append(item_id);

				item_drop_list.remove(0);
				return sb.toString();
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("Machine.removeFirstDropItem", e);
		}
		
		return "";
	}
	
	public byte getFirstProductIDInstock()
	{
		if (stock.size() <= 0)
		{
			LogHelper.Log("Stock is empty. Server need check!!!");
			return -1;
		}
			
		byte[] prod_data = stock.get(0);
		
		if (prod_data == null || prod_data.length == 0)
		{
			return -1;
		}
		
		FBEncrypt prod = new FBEncrypt(prod_data);

		return prod.getByte(KeyID.KEY_STOCK_PRODUCT_ID);
	}
	
	public long getFirstProductExpInstock()
	{
		if (stock.size() <= 0)
		{
			LogHelper.Log("Stock is empty. Server need check!!!");
			return -1;
		}
			
		byte[] prod_data = stock.get(0);
		
		if (prod_data == null || prod_data.length == 0)
		{
			return -1;
		}
		
		FBEncrypt prod = new FBEncrypt(prod_data);

		return prod.getLong(KeyID.KEY_STOCK_PRODUCT_EXP);
	}
	
	public byte[] getData()
	{
		FBEncrypt machine = new FBEncrypt();

		machine.addByte(KeyID.KEY_MACHINE_ID, id);
		machine.addByte(KeyID.KEY_MACHINE_FLOOR, floor);
		machine.addShort(KeyID.KEY_MACHINE_LEVEL, level);
		machine.addInt(KeyID.KEY_MACHINE_ACTIVE_TIME, active_time);
		machine.addByte(KeyID.KEY_MACHINE_STATUS, status);
		machine.addInt(KeyID.KEY_MACHINE_START_TIME, start_time);
		machine.addByte(KeyID.KEY_MACHINE_SLOT_MAX, slot_max);
		machine.addByte(KeyID.KEY_MACHINE_SLOT_CUR, slot_cur);
		machine.addByte(KeyID.KEY_MACHINE_PRODUCT_COUNT, product_count);

		int item_drop_count = item_drop_list.size();
		// make sure item_drop_count always <= product_count
		if (product_count < item_drop_count) item_drop_count = product_count;
		
		// LogHelper.Log("machine getData -----------");
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < item_drop_count; i++)
		{
			if (i > 0)
			{
				sb.append(":");
			}
			
			String item = item_drop_list.get(i);
			sb.append(item);
			// LogHelper.Log(item);
		}
		// LogHelper.Log("------ _" + sb.toString() + "_");
		machine.addStringANSI(KeyID.KEY_MACHINE_ITEM_DROP_LIST, sb.toString());
		// LogHelper.Log("--------------------------");
		
		
		for (int i = 0; i < slot_cur; i++)
		{
			machine.addBinary(KeyID.KEY_MACHINE_SLOT + i, slot.get(i).getData());
		}

		for (int i = 0; i < product_count; i++)
		{
			machine.addBinary(KeyID.KEY_MACHINE_PRODUCT + i, stock.get(i));
		}

		machine.addBinary(KeyID.KEY_MACHINE_DECOR, decor.getData(false));
		
		if (LOG_MACHINE)
		{
			LogMachine();
		}
		
		return machine.toByteArray();
	}

	// private String[] toStringArray(ArrayList<String> list)
	// {
		// String[] ret = new String[list.size()];
		// int i = 0;
		// for (String e : list)  
			// ret[i++] = e;
		// return ret;
	// }			
	
	public boolean isFull()
	{
		return (slot_cur >= slot_max) || (product_count >= (slot_max + DatabaseID.MACHINE_DEFAULT_STOCK_CAPACITY));
	}
	
	public boolean isLimit()
	{
		return (slot_max >= DatabaseID.MACHINE_SLOT_MAX);
	}
	
	public int getTimeMax()
	{
		int time = 0;

		for (int i = 0; i < DatabaseID.MACHINE_SLOT_MAX; i++)
		{
			if (slot.get(i).getID() != -1)
			{
				time += slot.get(i).getProductTime();
			}
			else
			{
				break;
			}
		}

		if (time <= 0) time = Misc.SECONDS();

		return time;
	}
	
	public int getFirstSlotTime()
	{
		if (slot.size() <= 0)
		{
			LogHelper.Log("Slot is empty. Server need check!!!");
			return -1;
		}
			
		return slot.get(0).getProductTime();
	}
	
	public void removeFirstSlot()
	{
		if (this.slot_cur > 0)
		{
			setSlotCur(this.slot_cur - 1);
			slot.remove(0);
		}
	}
	
	public void updateProductListTime(int prod_time)
	{
		for (int i = 0; i < this.slot_cur; i++)
		{
			slot.get(i).setProductTime(slot.get(i).getProductTime() - prod_time);
		}
	}
	
	// ------------------------------------------------------------------------------------------------------------------
	
	public byte getSlotCur()
	{
		return this.slot_cur;
	}
	
	public void setSlotCur(int slot_cur)
	{
		this.slot_cur = (byte)slot_cur;
	}
	
	public byte getSlotMax()
	{
		return this.slot_max;
	}
	
	public void setSlotMax(int slot_max)
	{
		this.slot_max = (byte)slot_max;
	}
	
	public byte getProductCount()
	{
		return this.product_count;
	}
	
	public void setProductCount(int product_count)
	{
		this.product_count = (byte)product_count;
	}
	
	public byte getStatus()
	{
		return this.status;
	}
	
	public void setStatus(int status)
	{
		this.status = (byte)status;
	}

	public int getStartTime()
	{
		return this.start_time;
	}
	
	public void setStartTime(int start_time)
	{
		this.start_time = start_time;
	}
	
	public short getLevel()
	{
		return this.level;
	}
	
	public void setLevel(int level)
	{
		this.level = (short)level;
	}
	
	public int getActiveTime()
	{
		return this.active_time;
	}
	
	public void setActiveTime(int active_time)
	{
		// will not increase active time when its already max
		// String[] value = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_MACHINE_UNLOCK][floor][level+1]).split(":");
		// int max_time = Integer.parseInt(value[0]);
		
		if (this.level + 1 < Server.s_globalDB[DatabaseID.SHEET_MACHINE_UNLOCK][floor].length)
		{
			int max_time = (int)Server.s_globalMachineUnlockData[this.floor][this.level + 1][DatabaseID.MACHINE_ACTIVE_TIME];
			if (active_time > max_time) active_time = max_time;
		}
		
		this.active_time = active_time;
	}

	public int getProductCompleteTime(int product_id)
	{
		return (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PRODUCT][product_id][DatabaseID.PRODUCT_PRODUCTION_TIME]);
	}
	
	public int GetID()
	{
		return (int)this.id;
	}
	
	public int getFloor()
	{
		return (int)this.floor;
	}
	
	public ArrayList<SlotMachine> getSlot()
	{
		return slot;
	}
	
	public void displayDataPackage()
	{
		FBEncrypt machine = new FBEncrypt(getData());
	
		LogHelper.Log("");
		LogHelper.Log("id: " + machine.getByte(KeyID.KEY_MACHINE_ID));
		LogHelper.Log("floor: " + machine.getByte(KeyID.KEY_MACHINE_FLOOR));
		LogHelper.Log("level: " + machine.getShort(KeyID.KEY_MACHINE_LEVEL));
		LogHelper.Log("active time: " + machine.getInt(KeyID.KEY_MACHINE_ACTIVE_TIME));
		LogHelper.Log("status: " + machine.getByte(KeyID.KEY_MACHINE_STATUS));
		LogHelper.Log("start time: " + machine.getInt(KeyID.KEY_MACHINE_START_TIME));
		LogHelper.Log("slot max: " + machine.getByte(KeyID.KEY_MACHINE_SLOT_MAX));
		LogHelper.Log("slot cur: " + machine.getByte(KeyID.KEY_MACHINE_SLOT_CUR));
		LogHelper.Log("prod cur: " + machine.getByte(KeyID.KEY_MACHINE_PRODUCT_COUNT));

		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < item_drop_list.size(); i++)
		{
			if (i > 0) sb.append(":");
			sb.append(item_drop_list.get(i));
		}
		// LogHelper.Log("item_drop_list: " + sb.toString());
		
		for (int i = 0; i < machine.getByte(KeyID.KEY_MACHINE_SLOT_CUR); i++)
		{
			LogHelper.Log("\nSLOT " + i +  ":"); 
			FBEncrypt slot_enc = new FBEncrypt(machine.getBinary(KeyID.KEY_MACHINE_SLOT + i));
			slot_enc.displayDataPackage();
		}

		for (int i = 0; i < machine.getByte(KeyID.KEY_MACHINE_PRODUCT_COUNT); i++)
		{
			LogHelper.Log("\nPROD " + i +  ":"); 
			FBEncrypt prod_enc = new FBEncrypt(machine.getBinary(KeyID.KEY_MACHINE_PRODUCT + i));
			prod_enc.displayDataPackage();
		}
		
		Decor _decor = new Decor(machine.getBinary(KeyID.KEY_MACHINE_DECOR));
		_decor.displayDataPackage();
	}
	
	public void LogMachine()
	{
		StringBuilder log = new StringBuilder();
		log.append("machine");
		log.append('\t').append(this.id);
		log.append('\t').append(this.floor);
		log.append('\t').append(this.level);
		log.append('\t').append(this.active_time);
		log.append('\t').append(this.status);
		log.append('\t').append(this.start_time);
		log.append('\t').append(this.slot_max);
		log.append('\t').append(this.slot_cur);
		log.append('\t').append(this.product_count);
		
		for (int i = 0; i < slot.size(); i++)
		{
			SlotMachine slot_machine = slot.get(i);
			
			log.append("\tslot" + i);
			log.append('\t').append(slot_machine.LogSlotMachine());
		}
		
		for (int i = 0; i < stock.size(); i++)
		{
			FBEncrypt prod = new FBEncrypt(stock.get(i));
			
			log.append("\tproduct" + i);
			log.append('\t').append(prod.getByte(KeyID.KEY_STOCK_PRODUCT_INDEX));
			log.append('\t').append(prod.getByte(KeyID.KEY_STOCK_PRODUCT_ID));
			log.append('\t').append(prod.getLong(KeyID.KEY_STOCK_PRODUCT_EXP));
		}
		
		log.append("\tdecor\t").append(decor.getID());
		
		LogHelper.Log(log.toString());
	}
	
	public String LogProperties()
	{
		StringBuilder log = new StringBuilder();
		log.append(this.id);
		log.append(':').append(this.floor);
		log.append(':').append(this.level);
		log.append(':').append(this.active_time);
		log.append(':').append(this.status);
		log.append(':').append(this.start_time);
		log.append(':').append(this.slot_max);
		log.append(':').append(this.slot_cur);
		log.append(':').append(this.product_count);
		
		return log.toString();
	}
}