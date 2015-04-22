package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import com.vng.skygarden.*;
import com.vng.skygarden._gen_.ProjectConfig;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Stock
{
    private int 	id;
    private short 	level;
    private short	capacity_max;
    private short	capacity_cur;
    private short	capacity_add;
    private short	capacity_plus;
    private short	vip_plus;
    private ConcurrentHashMap<String, Integer> items; // <"type_id", quantity>
    
    private boolean load_result = true;
	
	private int total_item;
	
	final boolean LOG_STORAGE = !true;
	
	private UserInfo _userInfo = null;
	
    public Stock(int id, UserInfo userInfo)
    {
        this.id = id;

        level = 0;
		
		if (id == DatabaseID.STOCK_EVENT)
		{
			capacity_max = 9999;
		}
		else
		{
			capacity_max = (short)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_STOCK][id][DatabaseID.STOCK_CAPACITY_MAX]);
		}
		
        capacity_cur = 0;
		total_item = 0;

        items = new ConcurrentHashMap(capacity_max);
		
		_userInfo = userInfo;
    }

    public Stock(int id, byte[] stock_data, UserInfo userInfo)
    {
        this.id = id;

        FBEncrypt stock = new FBEncrypt(stock_data);

        level = stock.getShort(KeyID.KEY_STOCK_LEVEL);
        capacity_max = stock.getShort(KeyID.KEY_STOCK_CAPACITY_MAX);
		total_item = stock.getInt(KeyID.KEY_STOCK_TOTAL_ITEM);
        if (level < 0 || capacity_max < 0 || total_item < 0)
        {
            load_result = false;
			return;
        }
		
		items = new ConcurrentHashMap(capacity_max);
		for (int i = 0; i < total_item; i++)
		{
			int item_type	= stock.getInt(KeyID.KEY_STOCK_ITEM_TYPE + i);
			int item_id		= stock.getInt(KeyID.KEY_STOCK_ITEM_ID + i);
			int item_val	= stock.getInt(KeyID.KEY_STOCK_ITEM_QUANTITY + i);
			
			if (!IsStockEvent() && item_type == DatabaseID.IT_EVENT)
				continue;
			
			// remove decor tet
			if (item_type == DatabaseID.IT_DECOR) {
				if (item_id >= 17 && item_id <= 22)
					continue;
			}
			
			items.put(item_type + "_" + item_id, item_val);
		}
		
		capacity_cur = getCapacityCurrent();
		
		_userInfo = userInfo;
    }
    
    public boolean isLoadSuccess()
    {
        return load_result;
    }
	
	public ConcurrentHashMap<String, Integer> getItems()
	{
		return this.items;
	}
	
    public byte[] getDataToDatabase()
    {
        FBEncrypt stock = new FBEncrypt();

        stock.addShort(KeyID.KEY_STOCK_LEVEL, level);
        stock.addShort(KeyID.KEY_STOCK_CAPACITY_MAX, capacity_max);
        stock.addShort(KeyID.KEY_STOCK_CAPACITY_CUR, capacity_cur);
		stock.addInt(KeyID.KEY_STOCK_TOTAL_ITEM, items.size());
		
		int idx = 0;
        for (Entry<String,Integer> item: items.entrySet())
        {
			String[] tmpString = item.getKey().split("_");
			stock.addInt(KeyID.KEY_STOCK_ITEM_TYPE + idx, Integer.parseInt(tmpString[0]));
			stock.addInt(KeyID.KEY_STOCK_ITEM_ID + idx, Integer.parseInt(tmpString[1]));
            stock.addInt(KeyID.KEY_STOCK_ITEM_QUANTITY + idx, item.getValue());
			
			idx++;
        }
		
		if (LOG_STORAGE)
		{
			LogStorage();
		}

        return stock.toByteArray();
    }
	
	public void LogStorage()
	{
		StringBuilder log = new StringBuilder();
		log.append("stock");
		log.append('\t').append(this.id);
		log.append('\t').append(level);
		log.append('\t').append(capacity_max);
		log.append('\t').append(capacity_cur);
		log.append('\t').append(items.size());
		
        for (Entry<String,Integer> item: items.entrySet())
        {
			String[] tmpString = item.getKey().split("_");
			log.append('\t').append(Misc.GetItemName(Integer.parseInt(tmpString[0]), Integer.parseInt(tmpString[1])));
			log.append(":").append(item.getValue());
        }
		
		LogHelper.Log(log.toString());
	}

	public String LogProperties()
	{
		StringBuilder log = new StringBuilder();
		log.append(this.id);
		log.append(':').append(level);
		log.append(':').append(capacity_max);
		log.append(':').append(capacity_cur);
		log.append(':').append(items.size());

		return log.toString();
	}
	
    public byte[] getDataToClient()
    {
		if (ProjectConfig.IS_SERVER_FREESTYLE == 1)
			LogStorage();
		
        FBEncrypt stock = new FBEncrypt();

        stock.addShort(KeyID.KEY_STOCK_LEVEL, level);
        stock.addShort(KeyID.KEY_STOCK_CAPACITY_MAX, capacity_max);
        stock.addShort(KeyID.KEY_STOCK_CAPACITY_CUR, capacity_cur);
		stock.addInt(KeyID.KEY_STOCK_TOTAL_ITEM, items.size());
        
        int idx = 0;
        for (Entry<String,Integer> item: items.entrySet())
        {
			String[] tmpString = item.getKey().split("_");
			stock.addInt(KeyID.KEY_STOCK_ITEM_TYPE		+ idx, Integer.parseInt(tmpString[0]));
			stock.addInt(KeyID.KEY_STOCK_ITEM_ID		+ idx, Integer.parseInt(tmpString[1]));
			stock.addInt(KeyID.KEY_STOCK_ITEM_QUANTITY	+ idx, item.getValue());
			
			idx++;
        }

        return stock.toByteArray();
    }
	
    public short getLevel()
    {
        return level;
    }

    public int GetID()
    {
        return this.id;
    }

    public void setLevel(int level)
    {
        this.level = (short)level;
    }

	public short getCapacityMax()
    {
        return capacity_max;
    }

    public void setCapacityMax(int capacity_max)
    {
        this.capacity_max = (short)capacity_max;
    }

    public short getCapacityCurrent()
    {
		short val = 0;
		for (Entry<String,Integer> item: items.entrySet())
        {
			val += item.getValue();
        }
		
        return val;
    }

    public boolean isFull()
    {
        return (capacity_cur >= capacity_max);
    }

    public boolean checkFull(int new_capacity)
    {
        return (new_capacity > capacity_max);
    }

	public String updateProducts_Log(int command_id, String[] products_need, int item_type)
    {
        if (products_need.length % 2 == 1)
        {
			return null;
        }
		
		StringBuilder log = new StringBuilder();
        
		for (int i = 0; i < products_need.length; i += 2)
        {
			short product_id_need = Short.parseShort(products_need[i]);
			short product_num_need = Short.parseShort(products_need[i+1]);

			String product_key = item_type + "_" + product_id_need;
			short product_stock_num = (short)getProductNum(item_type, product_id_need);
			short new_product_num = (short)(product_stock_num - product_num_need);
			
			if (i != 0) log.append(':');
			log.append(item_type);									// 12. id cua loai item su dung
			log.append(':').append(product_id_need);							// 13. id cua item su dung
			log.append(':').append(product_num_need);							// 14. so luong vat pham su dung
			log.append(':').append(getProductNum(item_type, product_id_need));	// 15. so item co truoc khi su dung
			
			if (new_product_num < 0)
			{
				LogHelper.Log("Stock.. err! invalid item nums (negative value)");
				return null;
			}

			if (new_product_num == 0)
			{
				items.remove(product_key);
				capacity_cur -= product_num_need;
			}
			else
			{
				boolean result = updateValue(command_id, item_type, product_id_need, new_product_num);
				
				if (result == false)
				{
					//TODO: should discuss again that if it's neccesary to reload data from base here.
					return null;
				}
			}
			
			log.append(':').append(getProductNum(item_type, product_id_need));		// 16. so item con sau khi su dung
        }
		
		return log.toString();
    }
	
	public boolean updateProducts(int command_id, String[] products_need, int const_item_type)
    {
        if (products_need.length % 2 == 1)
        {
			return false;
        }
		
        for (int i = 0; i < products_need.length; i += 2)
        {
			short product_id_need = Short.parseShort(products_need[i]);
			short product_num_need = Short.parseShort(products_need[i+1]);

			String product_key = const_item_type + "_" + product_id_need;
			short product_stock_num = (short)getProductNum(const_item_type, product_id_need);
			short new_product_num = (short)(product_stock_num - product_num_need);
			
			if (new_product_num < 0)
			{
				LogHelper.Log("Stock.. err! invalid item nums (negative value)");
				return false;
			}

			if (new_product_num == 0)
			{
				items.remove(product_key);
				capacity_cur -= product_num_need;
			}
			else
			{
				boolean result = updateValue(command_id, const_item_type, product_id_need, new_product_num);
				
				if (result == false)
				{
					//TODO: should discuss again that if it's neccesary to reload data from base here.
					return false;
				}
			}
        }
		
		return true;
    }
	
	public boolean checkProducts(String[] products_need, int item_type)
    {
		boolean product_enough = true;

		// dont need this to create product
		if (products_need.length % 2 == 1)
		{
			return true;
		}

		for (int i = 0; i < products_need.length; i += 2)
		{
			short product_id_need = Short.parseShort(products_need[i]);
			short product_num_need = Short.parseShort(products_need[i+1]);

			short product_stock_num = (short)getProductNum(item_type, product_id_need);

			// LogHelper.Log(product_id_need + "   " + product_num_need + "   " + product_stock_num); 

			// check current product number in stock with condition to create new production
			if (product_stock_num < product_num_need)
			{
				product_enough = false;
				break;
			}
		}

		return product_enough;
    }
	
	public boolean checkEnoughProductInStock(int type, int id, int num)
	{
		int product_stock_num = getProductNum(type, id);
		
		return (product_stock_num < num ? false : true);
	}
	
	public void decreaseProductInStock(int command_id, int type, int id, int num)
	{
		int product_stock_num = getProductNum(type, id);
		int new_product_stock_num = product_stock_num - num;

		if (new_product_stock_num <= 0)
		{
			items.remove(type + "_" + id);
			capacity_cur -= num;
			
			if (capacity_cur < 0) capacity_cur = 0;
		}
		else
		{
			updateValue(command_id, type, id, new_product_stock_num);
		}
	}
	
	public int getProductNum(int item_type, int item_id)
	{
		String key = item_type + "_" + item_id;
		if (items.get(key) == null) return 0;

		return items.get(key);
	}
	
	public short getCapacityAdd()
	{
		return (short)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_STOCK][id][DatabaseID.STOCK_CAPACITY_ADD]);
	}

	public short getCapacityPlus()
	{
		return (short)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_STOCK][id][DatabaseID.STOCK_CAPACITY_PLUS]);
	}
	
	public short getVipPlus()
	{
		return (short)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_STOCK][id][DatabaseID.STOCK_VIP_PLUS]);
	}
	
	public void displayDataPackage()
	{
		FBEncrypt stock = new FBEncrypt(getDataToDatabase());
		stock.displayDataPackage();
	}
        
	public boolean add(int item_type, int item_id, int value, int command_id)
	{
		if (!IsStockEvent() && item_type == DatabaseID.IT_EVENT || (item_type == DatabaseID.IT_MATERIAL && item_id == DatabaseID.MATERIAL_MOON_CAKE))
			return false;
		
		String key = item_type + "_" + item_id;

		if (capacity_cur + value > capacity_max)
		{
			LogHelper.Log("Stock.. err! stock is full, can not add item " + key);
			return false;
		}

		if (items.containsKey(key))
		{
			if (items.replace(key, items.get(key), items.get(key) + value))
			{
//				LogHelper.Log("Stock.. add item to stock: " + Misc.GetItemName(item_type, item_id) + ":" + value);
			}
			else
			{
				LogHelper.Log("Stock.. err! can not add item " + key);
				return false;
			}
		}
		else
		{
			items.put(key, value);
//			LogHelper.Log("Stock.. add item to stock: " + Misc.GetItemName(item_type, item_id) + ":" + value);
		}

		capacity_cur += value;
		
		StringBuilder log = new StringBuilder();
		log.append(Misc.getCurrentDateTime());								//  1. thoi gian tieu tien
		log.append('\t').append(Misc.getActionName(command_id));			//  2. hanh dong cua gamer
		log.append('\t').append(_userInfo.getID());							//  3. id
		log.append('\t').append(_userInfo.getID());							//  4. role id
		log.append('\t').append(_userInfo.getName());						//  5. name
		log.append('\t').append(0);											//  6. server id
		log.append('\t').append(_userInfo.getLevel());						//  7. level
		log.append('\t').append(item_type + "_" + item_id);					//  8. id item nhan
		log.append('\t').append(value);										//  9. so luong item nhan
		log.append('\t').append(1);											// 10. result
		log.append('\t').append("");										// 11 . description as list
		LogHelper.Log(LogHelper.LogType.RECEIVING_ITEM, log.toString());
		
		return true;
	}
	
	/*
	 * addGift(itemType, itemID, itemNum) is used to add gifts to storage.
	 * Gifts are added even when storage is full.
	 */
	public boolean addGift(int item_type, int item_id, int value)
	{		
		if (!IsStockEvent() && item_type == DatabaseID.IT_EVENT || (item_type == DatabaseID.IT_MATERIAL && item_id == DatabaseID.MATERIAL_MOON_CAKE))
			return false;
	
		String key = item_type + "_" + item_id;

		if (items.containsKey(key))
		{
			if (!items.replace(key, items.get(key), items.get(key) + value))
			{
				LogHelper.Log("Stock.. can not add item: " + Misc.GetItemName(item_type, item_id));
				return false;
			}
		}
		else
		{
			items.put(key, value);
		}

		capacity_cur += value;
		
		return true;
	}
	
	public boolean addGift(int item_type, int item_id, int value, int command_id)
	{
		if (!IsStockEvent() && item_type == DatabaseID.IT_EVENT || (item_type == DatabaseID.IT_MATERIAL && item_id == DatabaseID.MATERIAL_MOON_CAKE))
			return false;
		
		String key = item_type + "_" + item_id;

		if (items.containsKey(key))
		{
			if (!items.replace(key, items.get(key), items.get(key) + value))
			{
				LogHelper.Log("Stock.. can not add item: " + Misc.GetItemName(item_type, item_id));
				return false;
			}
		}
		else
		{
			items.put(key, value);
		}

		capacity_cur += value;
	
		StringBuilder log = new StringBuilder();
		log.append(Misc.getCurrentDateTime());																//  1. thoi gian
		log.append('\t').append(Misc.getActionName(command_id));											//  2. hanh dong
		log.append('\t').append(_userInfo.getID());															//  3. ten tai khoan
		log.append('\t').append(_userInfo.getID());															//  4. id cua role nap tien
		log.append('\t').append(_userInfo.getName());														//  5. ten role
		log.append('\t').append(0);																			//  6. id cua server
		log.append('\t').append(_userInfo.getLevel());														//  7. level cua gamer
		log.append('\t').append(item_type + "_" + item_id);													//  8. id cua loai item
		log.append('\t').append(value);																		//  9. so luong vat pham
		log.append('\t').append(1);																			// 10. 1: success, <>1: error code
		log.append('\t').append("");																		// 11 . description as list
		LogHelper.Log(LogHelper.LogType.RECEIVING_ITEM, log.toString());
	
		return true;
	}
	
	public boolean increaseObject(int command_id, int item_type, int item_id, int server_id)
	{
		if (!IsStockEvent() && item_type == DatabaseID.IT_EVENT || (item_type == DatabaseID.IT_MATERIAL && item_id == DatabaseID.MATERIAL_MOON_CAKE))
			return false;
		
		boolean result = true;
		String key = item_type + "_" + item_id;
		if (!isFull())
		{
			if (items.containsKey(key))
			{
				if (!items.replace(key, items.get(key), items.get(key) + 1))
				{
					LogHelper.Log("Stock.. can not increase item: " + Misc.GetItemName(item_type, item_id));
					result = false;
				}
			}
			else
			{
				items.put(key, 1);
			}
			
			capacity_cur++;
		}
		else
		{
			LogHelper.Log("Stock.. stock is full, can not increase item: " + Misc.GetItemName(item_type, item_id));
			result = false;
		}
		
		StringBuilder log = new StringBuilder();
		log.append(Misc.getCurrentDateTime());								//  1. thoi gian tieu tien
		log.append('\t').append(Misc.getActionName(command_id));			//  2. hanh dong cua gamer
		log.append('\t').append(_userInfo.getID());							//  3. id
		log.append('\t').append(_userInfo.getID());							//  4. role id
		log.append('\t').append(_userInfo.getName());						//  5. name
		log.append('\t').append(0);											//  6. server id
		log.append('\t').append(_userInfo.getLevel());						//  7. level
		log.append('\t').append(item_type + "_" + item_id);					//  8. id item nhan
		log.append('\t').append(1);											//  9. so luong item nhan
		log.append('\t').append(result ? 1 : 0);							// 10. result
		log.append('\t').append(server_id);									// 11 . description as list
		LogHelper.Log(LogHelper.LogType.RECEIVING_ITEM, log.toString());
		
		return result;
	}
	
	public String decreaseObject_Log(int item_type, int item_id, int command_id)
	{
		String key = item_type + "_" + item_id;
		
		StringBuilder log = new StringBuilder();
		log.append(item_type).append('_').append(item_id);											// 12. id cua loai item su dung
		log.append(':').append(1);										// 14. so luong vat pham su dung
		log.append(':').append(getProductNum(item_type, item_id));		// 15. so item co truoc khi su dung
		
		if (items.containsKey(key))
		{
			if (!items.replace(key, items.get(key), items.get(key)-1))
			{
				LogHelper.Log("Stock.. can not decrease item: " + Misc.GetItemName(item_type, item_id));
				return null;
			}
			
			if (items.get(key) == 0)
			{
				if (items.remove(key, 0))
				{
//					LogHelper.Log("Stock.. remove empty item: " + Misc.GetItemName(item_type, item_id));
				}
				else
				{
					LogHelper.Log("Stock.. can not remove emtpy item: " + Misc.GetItemName(item_type, item_id));
				}
			}
			
			capacity_cur--;
		}
		else
		{
			LogHelper.Log("Stock.. decrease item fail.Can not find item: " + Misc.GetItemName(item_type, item_id) );
			return null;
		}
		
		log.append(':').append(getProductNum(item_type, item_id));		// 16. so item con sau khi su dung
		
		StringBuilder log_spent = new StringBuilder();
		log_spent.append(Misc.getCurrentDateTime());							//  1. thoi gian tieu tien
		log_spent.append('\t').append(Misc.getActionName(command_id));			//  2. hanh dong cua gamer
		log_spent.append('\t').append(_userInfo.getID());						//  3. id
		log_spent.append('\t').append(_userInfo.getID());						//  4. role id
		log_spent.append('\t').append(_userInfo.getName());						//  5. name
		log_spent.append('\t').append(0);										//  6. server id
		log_spent.append('\t').append(_userInfo.getLevel());					//  7. level
		log_spent.append('\t').append(item_type + "_" + item_id);				//  8. id item su dung
		log_spent.append('\t').append(1);										//  9. so luong item su dung
		log_spent.append('\t').append("");										//  10. id item nhan
		log_spent.append('\t').append(0);										//  11. so luong item nhan
		log_spent.append('\t').append(1);										//  12. result
		log_spent.append('\t').append("");										//  13. ten tai khoan giao dich
		log_spent.append('\t').append(_userInfo.getID() + "_" + System.currentTimeMillis());	//  14. transaction id
		LogHelper.Log(LogHelper.LogType.SPENT_ITEM, log_spent.toString());
		
		return log.toString();
	}
	
	public boolean decreaseObject(int command_id, int const_item_type, int item_id)
	{
		boolean result = true;
		String key = const_item_type + "_" + item_id;
		if (items.containsKey(key))
		{
			if (!items.replace(key, items.get(key), items.get(key)-1))
			{
				LogHelper.Log("Stock.. can not decrease item: " + Misc.GetItemName(const_item_type, item_id));
				result = false;
			}
			
			if (items.get(key) == 0)
			{
				if (items.remove(key, 0))
				{
//					LogHelper.Log("Stock.. remove empty item: " + Misc.GetItemName(const_item_type, item_id));
				}
				else
				{
					LogHelper.Log("Stock.. can not remove emtpy item: " + Misc.GetItemName(const_item_type, item_id));
				}
			}
			
			capacity_cur--;
		}
		else
		{
			LogHelper.Log("Stock.. decrease item fail.Can not find item: " + Misc.GetItemName(const_item_type, item_id) );
			result = false;
		}
		
		StringBuilder log = new StringBuilder();
		log.append(Misc.getCurrentDateTime());								//  1. thoi gian tieu tien
		log.append('\t').append(Misc.getActionName(command_id));			//  2. hanh dong cua gamer
		log.append('\t').append(_userInfo.getID());							//  3. id
		log.append('\t').append(_userInfo.getID());							//  4. role id
		log.append('\t').append(_userInfo.getName());						//  5. name
		log.append('\t').append(0);											//  6. server id
		log.append('\t').append(_userInfo.getLevel());						//  7. level
		log.append('\t').append(const_item_type + "_" + item_id);			//  8. id item su dung
		log.append('\t').append(1);											//  9. so luong item su dung
		log.append('\t').append("");										//  10. id item nhan
		log.append('\t').append(0);											//  11. so luong item nhan
		log.append('\t').append(result ? 1 : 0);							//  12. result
		log.append('\t').append("");										//  13. ten tai khoan giao dich
		log.append('\t').append(_userInfo.getID() + "_" + System.currentTimeMillis());	//  14. transaction id
		LogHelper.Log(LogHelper.LogType.SPENT_ITEM, log.toString());
		
		return result;
	}
	
	public boolean updateValue(int command_id, int item_type, int item_id, int new_val)
	{
		if (new_val < 0) return false;
		
		String key = item_type + "_" + item_id;
		
		if (items.containsKey(key))
		{
			int old_val = getProductNum(item_type, item_id);
			int delta = old_val - new_val;
			
			if (new_val == 0)
			{
				items.remove(key);
				capacity_cur -= delta;
//				LogHelper.Log("Stock: remove item " + Misc.GetItemName(item_type, item_id) + " from stock.");
				
				if (_userInfo != null)
				{
					StringBuilder log = new StringBuilder();
					log.append(Misc.getCurrentDateTime());								//  1. thoi gian tieu tien
					log.append('\t').append(Misc.getActionName(command_id));			//  2. hanh dong cua gamer
					log.append('\t').append(_userInfo.getID());							//  3. id
					log.append('\t').append(_userInfo.getID());							//  4. role id
					log.append('\t').append(_userInfo.getName());						//  5. name
					log.append('\t').append(0);											//  6. server id
					log.append('\t').append(_userInfo.getLevel());						//  7. level
					log.append('\t').append(item_type + "_" + item_id);					//  8. id item su dung
					
					if (new_val > old_val) // receive items
					{
						log.append('\t').append(new_val - old_val);						//  9. so luong item su dung
						log.append('\t').append(1);									// 10. result
						log.append('\t').append("");								// 11 . description as list
						LogHelper.Log(LogHelper.LogType.RECEIVING_ITEM, log.toString());
					}
					else // use item
					{
						log.append('\t').append(old_val - new_val);							//  9. so luong item su dung
						log.append('\t').append("");										//  10. id item nhan
						log.append('\t').append(0);											//  11. so luong item nhan
						log.append('\t').append(1);											//  12. result
						log.append('\t').append("");										//  13. ten tai khoan giao dich
						log.append('\t').append(_userInfo.getID() + "_" + System.currentTimeMillis());	//  14. transaction id
						LogHelper.Log(LogHelper.LogType.SPENT_ITEM, log.toString());
					}
				}
				
				return true;
			}
			else
			{
				boolean result = items.replace(key, items.get(key), new_val);
				
				if (_userInfo != null)
				{
					StringBuilder log = new StringBuilder();
					log.append(Misc.getCurrentDateTime());								//  1. thoi gian tieu tien
					log.append('\t').append(Misc.getActionName(command_id));			//  2. hanh dong cua gamer
					log.append('\t').append(_userInfo.getID());							//  3. id
					log.append('\t').append(_userInfo.getID());							//  4. role id
					log.append('\t').append(_userInfo.getName());						//  5. name
					log.append('\t').append(0);											//  6. server id
					log.append('\t').append(_userInfo.getLevel());						//  7. level
					log.append('\t').append(item_type + "_" + item_id);					//  8. id item su dung
					
					if (new_val > old_val) // receive items
					{
						log.append('\t').append(new_val - old_val);						//  9. so luong item su dung
						log.append('\t').append(result ? 1 : 0);									// 10. result
						log.append('\t').append("");								// 11 . description as list
						LogHelper.Log(LogHelper.LogType.RECEIVING_ITEM, log.toString());
					}
					else // use item
					{
						log.append('\t').append(old_val - new_val);							//  9. so luong item su dung
						log.append('\t').append("");										//  10. id item nhan
						log.append('\t').append(0);											//  11. so luong item nhan
						log.append('\t').append(result ? 1 : 0);											//  12. result
						log.append('\t').append("");										//  13. ten tai khoan giao dich
						log.append('\t').append(_userInfo.getID() + "_" + System.currentTimeMillis());	//  14. transaction id
						LogHelper.Log(LogHelper.LogType.SPENT_ITEM, log.toString());
					}
				}
				
				if (result == true)
				{
					capacity_cur -= delta;
					return true;
				}
				else
				{
					LogHelper.Log("Stock: can not update value for key: " + Misc.GetItemName(item_type, item_id));
					return false;
				}
			}
		}
		else
		{
			// LogHelper.Log("\nStock: " + Misc.GetItemName(item_type, item_id) + " is not exist in stock");
			return false;
		}
	}
	
	public boolean IsStockEvent()
	{
		return id == DatabaseID.STOCK_EVENT;
	}
}