package com.vng.skygarden.game;

import com.vng.log.*;
import com.vng.netty.*;
import com.vng.skygarden.DBConnector;
import com.vng.util.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class RotaFortunaeManager
{
	UserInfo _user_info = null;
	List<RotaFortunae> _rota_fortunate_list = new LinkedList<RotaFortunae>();
	int _start_time = 0;
	int _end_time = 0;
	int _total_star = 0;
	String _gift_fake = "";
	String _gold_list = "";
	
	private final int MAX_ITEM = 13;
	
	private final int RESULT_INDEX_PLANT	= 0;
	private final int RESULT_INDEX_PRODUCT	= 1;
	private final int RESULT_INDEX_MATERIAL = 2;
	private final int RESULT_INDEX_DECOR	= 3;
	private final int RESULT_INDEX_STAR		= 4;
	private final int RESULT_INDEX_RETRY	= 5;
	private final int RESULT_INDEX_DIAMOND	= 6;
	private final int RESULT_INDEX_POT		= 7;
	
	private final int MAX_FORTUNE_RETRY		= 3;
	private final int MAX_GEN_RETRY			= 16;
	
	private int _last_gen_time = 0;
	int _total_diamond = 0;
	int _total_pot = 0;
	
	public RotaFortunaeManager(UserInfo user_info)
	{
		this._user_info = user_info;
		_start_time = 0;
		_end_time = 0;
		_gift_fake = "";
	}
	
	public RotaFortunaeManager(UserInfo user_info, byte[] b)
	{
		this._user_info = user_info;
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(b, true);
		
		_start_time = encrypt.getInt(KeyID.KEY_ROTA_FORTUNAE_START_TIME);
		_end_time = encrypt.getInt(KeyID.KEY_ROTA_FORTUNAE_END_TIME);
		_gift_fake = encrypt.getString(KeyID.KEY_ROTA_FORTUNAE_GIFT_FAKE);
		_gold_list = encrypt.getString(KeyID.KEY_ROTA_FORTUNAE_GOLD_LIST);
		_total_star = encrypt.getInt(KeyID.KEY_ROTA_FORTUNAE_TOTAL_STAR);
		_last_gen_time = encrypt.getInt("_last_gen_time");
	}
	
	public boolean Load()
	{
		if (_user_info == null)
		{
			return false;
		}
		
		_rota_fortunate_list.clear();
		List<String> all_keys = new ArrayList<String>();
		for (int i = 0; i < GetTotalRotaFortunaes(); i++)
		{
			String key = _user_info.getID() + "_" + KeyID.KEY_ROTA_FORTUNAE_INDEX + i;
			all_keys.add(key);
		}
		
		try
		{
			Map<String, Object> all_data = DBConnector.GetMembaseServer(_user_info.getID()).GetMulti(all_keys);
			for (int i = 0; i < GetTotalRotaFortunaes(); i++)
			{
				String key = _user_info.getID() + "_" + KeyID.KEY_ROTA_FORTUNAE_INDEX + i;
				if (all_data.containsKey(key))
				{
					byte[] data = (byte[])all_data.get(key);
					RotaFortunae rf = new RotaFortunae(data);
					_rota_fortunate_list.add(rf);
				}
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("load_fortunate", e);
		}
		
		return true;
	}
	
	/**
	 * Gen a list of rota fortunate
	 */
	public boolean Gen()
	{
		if (_user_info == null)
		{
			return false;
		}
		
		_total_star = 0;
		_start_time = Misc.SECONDS();
		_end_time = Misc.SECONDS() + 1800;
		_gift_fake = "";
		_gold_list = GenGoldList();

		_rota_fortunate_list.clear();
		int gen_retry = 0;
		while (_rota_fortunate_list.size() < GetTotalRotaFortunaes() && gen_retry < MAX_GEN_RETRY)
		{
			int rf_infex = _rota_fortunate_list.size();
			RotaFortunae rf = Gen(null, _total_star, rf_infex);
			
			boolean duplicate = false;
			for (RotaFortunae inner_rf : _rota_fortunate_list)
			{
				if (!rf.getGiftReal().contains(DatabaseID.IT_MONEY + ":" + DatabaseID.GOLD_ID) && rf.getGiftReal().equals(inner_rf.getGiftReal()))
				{
					duplicate = true;
					break;
				}
			}
			if (!duplicate)
			{
				_rota_fortunate_list.add(rf);
				gen_retry = 0;
			}
			else
			{
				gen_retry++;
			}
		}
		
		// handle duplicated
		for (int i = 0; i < MAX_GEN_RETRY/2; i++)
		{
			int rf_duplicate_index = -1;
			for (RotaFortunae rf : _rota_fortunate_list)
			{
				if (rf.getRetryTotal() > 0 && rf.getGiftReal().equals(""))
				{
					rf_duplicate_index = rf.getIndex();
					break;
				}
			}
			
			if (rf_duplicate_index != -1)
			{
				RotaFortunae dupe_rf = GetRotaFortunae(rf_duplicate_index);
				RotaFortunae new_rf =  Gen(dupe_rf, _total_star, rf_duplicate_index);
				boolean duplicated_again = false;
				for (RotaFortunae rf : _rota_fortunate_list)
				{
					String gift_real = rf.getGiftReal();
					String new_gift = new_rf.getGiftReal();
					if (gift_real.equals(new_gift))
					{
						duplicated_again = true;
						break;
					}
				}
				
				if (duplicated_again)
				{
					dupe_rf.setGiftReal("");
				}
				else
				{
					_rota_fortunate_list.remove(rf_duplicate_index);
					_rota_fortunate_list.add(rf_duplicate_index, new_rf);
				}
			}
			else
			{
				break;
			}
		}
		
		boolean use_default = false;
		for (RotaFortunae rf : _rota_fortunate_list)
		{
			if (rf.getGiftReal().equals(""))
			{
				use_default = true;
				break;
			}
		}
		
		if (use_default)
		{
			int num = 1;
			String start_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][0][DatabaseID.EVENT_GLOBAL_START_DATE]);
			String end_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][0][DatabaseID.EVENT_GLOBAL_END_DATE]);
			if(Misc.InEvent(start_event_time, end_event_time))
			{
				num = 2;
			}
			else
			{
				num = 1;
			}
			
			_rota_fortunate_list.clear();
			RotaFortunae default_0 = new RotaFortunae(0);
			default_0.setGiftReal(DatabaseID.IT_MATERIAL + ":" + 9 + ":" + num);
			_rota_fortunate_list.add(default_0);
			
			RotaFortunae default_1 = new RotaFortunae(1);
			default_1.setGiftReal(DatabaseID.IT_MONEY + ":" + DatabaseID.GOLD_ID + ":" + num);
			_rota_fortunate_list.add(default_1);
			
			RotaFortunae default_2 = new RotaFortunae(2);
			default_2.setGiftReal(DatabaseID.IT_PRODUCT + ":" + 36 + ":" + num);
			_rota_fortunate_list.add(default_2);
			
			RotaFortunae default_3 = new RotaFortunae(3);
			default_3.setGiftReal(DatabaseID.IT_DECOR + ":" + 4 + ":" + num);
			_rota_fortunate_list.add(default_3);
			
			RotaFortunae default_4 = new RotaFortunae(4);
			default_4.setGiftReal(DatabaseID.IT_MATERIAL + ":" + 6 + ":" + num);
			_rota_fortunate_list.add(default_4);
			
			RotaFortunae default_5 = new RotaFortunae(5);
			default_5.setGiftReal(DatabaseID.IT_MONEY + ":" + DatabaseID.GOLD_ID + ":" + num);
			_rota_fortunate_list.add(default_5);
			
			RotaFortunae default_6 = new RotaFortunae(6);
			default_6.setGiftReal(DatabaseID.IT_BUG + ":" + 5 + ":" + num);
			_rota_fortunate_list.add(default_6);
			
			LogHelper.Log("generated default gift for usser " + _user_info.getID());
		}
		
		_gift_fake = GenFakeGiftList();
		_last_gen_time = (int)(System.currentTimeMillis()/1000);
		
		return true;
	}
	
	public RotaFortunae Gen(RotaFortunae rf, int star, int index)
	{
		RotaFortunae result = null;
		if (rf != null)
		{
			byte[] b = rf.getData();
			result = new RotaFortunae(b);
		}
		else
		{
			result = new RotaFortunae(index);
		}
		boolean is_retry = result.getRetryTotal() > 0;
		boolean paid = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_ROTA_FORTUNAE][index][DatabaseID.ROTA_FORTUNAE_PAID_OR_FREE]) == 1;
		if (is_retry)
		{
//			LogHelper.Log("RotaFortunae.Gen.. retry: " + result.ToString());
		}
		else
		{
			result.setIsPaid(paid);
			result.setUsed(false);

			// set diamond
			double ratio = Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_ROTA_FORTUNAE][index][DatabaseID.ROTA_FORTUNAE_DIAMOND_RATIO]);
			int basic_diamond = 0;
			boolean last_paid = (index == GetLastPaidIndex()); // todo: calculate if it's the last paid time
			if (last_paid)
			{
				basic_diamond = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][_user_info.getLevel()][DatabaseID.CONSTANT_FORTUNE_WHEEL_LAST_BASIC_PRICE]);
			}
			else
			{
				basic_diamond = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][_user_info.getLevel()][DatabaseID.CONSTANT_FORTUNE_WHEEL_NORMAL_BASIC_PRICE]);
			}
			int diamond = (int)(basic_diamond * ratio);
			result.setDiamond(diamond);
			
			// control sale off
			try
			{
				String start_sale_off_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][6][DatabaseID.EVENT_GLOBAL_START_DATE]);
				String end_sale_off_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][6][DatabaseID.EVENT_GLOBAL_END_DATE]);

				if (Misc.InEvent(start_sale_off_time, end_sale_off_time))
				{
					result.setSaleOff(true);
					int sale_off_percent = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][6][DatabaseID.EVENT_GLOBAL_BONUS_EXP_RATE]);
					int sale_off_price = result.getDiamond() - ((sale_off_percent * result.getDiamond()) / 100);
					result.setDiamondSaleOff(sale_off_price < 0 ? 0 : sale_off_price);
					
					LogHelper.LogHappy("sale_off_percent := " + sale_off_percent);
					LogHelper.LogHappy("sale_off_price := " + sale_off_price);
				}
				else
				{
					result.setSaleOff(false);
					result.setDiamondSaleOff(result.getDiamond());
				}
			}
			catch (Exception e)
			{
				LogHelper.LogException("Execute2011Time", e);
			}
		}
			
			
		// choose gift real
		int column_index_of_ratio = 0;
		if (paid)
		{
			column_index_of_ratio = GetColumnIndexOfRatioForPaidWheel(_total_star, GetLatestPaidFortuneIndex());
		}
		else
		{
			column_index_of_ratio = GetColumnIndexOfRatioForFreeWheel(_total_star, GetLatestFreeFortuneIndex());
		}
		
		// get item type & item id
		int item_type = -1;
		int item_id = -1;
			
		int random = Misc.RANDOM_RANGE(0, 100);
		int current_percent = 0;
		int result_index = -1;
		int total_items = Server.s_globalDB[DatabaseID.SHEET_ROTA_FORTUNAE_ITEMS].length;
		INNER_1: for (int row = 0; row < total_items; row++)
		{
			int percent_of_item = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_ROTA_FORTUNAE_ITEMS][row][column_index_of_ratio]);
			current_percent += percent_of_item;
			if (current_percent >= random)
			{
				result_index = row;
				INNER_2: switch (result_index)
				{
					case RESULT_INDEX_PLANT:
					{
						item_type = DatabaseID.IT_PLANT;
						break INNER_2;
					}
					case RESULT_INDEX_PRODUCT:
					{
						item_type = DatabaseID.IT_PRODUCT;
						break INNER_2;
					}
					case RESULT_INDEX_MATERIAL:
					{
						item_type = DatabaseID.IT_MATERIAL;
						break INNER_2;
					}
					case RESULT_INDEX_DECOR:
					{
						item_type = DatabaseID.IT_DECOR;
						break INNER_2;
					}
					case RESULT_INDEX_STAR:
					{
						_total_star += 1;
						if (_total_star > 3)
						{
							_total_star = 3;
						}
						item_type = DatabaseID.IT_MONEY;
						item_id = DatabaseID.GOLD_ID;
						break INNER_2;
					}
					case RESULT_INDEX_RETRY:
					{
						result.setRetryTotal(result.getRetryTotal() + 1);
						break INNER_2;
					}
					case RESULT_INDEX_DIAMOND:
					{
						_total_diamond++;
						if (_total_diamond > 2)
						{
							item_type = DatabaseID.IT_PRODUCT;
						}
						else
						{
							item_type = DatabaseID.IT_MONEY;
							item_id = DatabaseID.DIAMOND_ID;
						}
						
						break INNER_2;
					}
					case RESULT_INDEX_POT:
					{
						_total_pot++;
						if (_total_pot > 1)
						{
							item_type = DatabaseID.IT_DECOR;
						}
						else
						{
							item_type = DatabaseID.IT_POT;
						}
						
						break INNER_2;
					}
				}
				
				// this should never happen, but trap bug for safety
				if (result_index == RESULT_INDEX_STAR && _total_star > 3)
				{
					result_index = RESULT_INDEX_PRODUCT;
					item_type = DatabaseID.IT_PRODUCT;
				}
				else if (result_index == RESULT_INDEX_RETRY && result.getRetryTotal() > 2)
				{
					result_index = RESULT_INDEX_MATERIAL;
					item_type = DatabaseID.IT_MATERIAL;
				}
				
				break INNER_1;
			}
		}
			
		// get item id
		if (result_index > -1 && (result_index != RESULT_INDEX_RETRY))
		{
			// get unlocked list
			ArrayList<String> unlocked = new ArrayList<>();
			switch (item_type)
			{
				case DatabaseID.IT_PLANT:
					unlocked = Misc.getItemsUnlockWithItemType(_user_info.getLevel(), DatabaseID.USER_SEED_ID_UNLOCK);
					break;
				case DatabaseID.IT_PRODUCT:
					unlocked = Misc.getItemsUnlockWithItemType(_user_info.getLevel(), DatabaseID.USER_PROD_ID_UNLOCK);
					unlocked.addAll(Misc.getItemsUnlockWithItemType(_user_info.getLevel(), DatabaseID.USER_BUG_UNLOCK));
					break;
				case DatabaseID.IT_MATERIAL:
					unlocked = Misc.getItemsUnlockWithItemType(_user_info.getLevel(), DatabaseID.USER_MATERIAL_UNLOCK);
					break;
				case DatabaseID.IT_DECOR:
					unlocked = Misc.getItemsUnlockWithItemType(_user_info.getLevel(), DatabaseID.USER_DECOR_UNLOCK);
					break;
				case DatabaseID.IT_POT:
					unlocked = Misc.getItemsUnlockWithItemType(_user_info.getLevel(), DatabaseID.USER_POT_ID_UNLOCK);
					break;
				case DatabaseID.IT_MONEY:
					break;
			}

			// get total ratio & random
			if (item_id == DatabaseID.GOLD_ID)
			{
				// Do nothing, as get num of gold from constant
			}
			else
			{
				int total_ratio = 0;
				for (String s : unlocked)
				{
					int temp_item_type = Integer.parseInt(s.split(":")[0]);
					int temp_item_id = Integer.parseInt(s.split(":")[1]);
					int item_ratio = Misc.GetItemValues(temp_item_type, temp_item_id, DatabaseID.ITEM_FORTUNE_WHEEL_REAL_RATIO);
					total_ratio += item_ratio;
				}

				int random_item_id = Misc.RANDOM_RANGE(0, total_ratio);
				int current_ratio = 0;
				for (String s : unlocked)
				{
					int temp_item_type = Integer.parseInt(s.split(":")[0]);
					int temp_item_id = Integer.parseInt(s.split(":")[1]);
					int item_ratio = Misc.GetItemValues(temp_item_type, temp_item_id, DatabaseID.ITEM_FORTUNE_WHEEL_REAL_RATIO);
					current_ratio += item_ratio;
					if (current_ratio >= random_item_id)
					{
						item_type = temp_item_type;
						item_id = temp_item_id;
						break;
					}
				}	
			}
		}
		
		if (item_type != -1 && item_id != -1)
		{
			String start_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][0][DatabaseID.EVENT_GLOBAL_START_DATE]);
			String end_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][0][DatabaseID.EVENT_GLOBAL_END_DATE]);
			if(Misc.InEvent(start_event_time, end_event_time))
			{
				result.setGiftReal(item_type + ":" + item_id + ":" + 2); // finish choose gift real
			}
			else
			{
				if (item_type == DatabaseID.IT_MONEY && item_id == DatabaseID.DIAMOND_ID)
				{
					int tmp = result.getDiamond() * 2;
					result.setGiftReal(item_type + ":" + item_id + ":" + (tmp <= 0 ? 1 : tmp));
				}
				else
				{
					result.setGiftReal(item_type + ":" + item_id + ":" + 1); // finish choose gift real
				} 
			}
		}
		else
		{
			result.setGiftReal("");
		}

		return result;
	}
	
	public boolean SaveRotaFortunae(int index)
	{
		String key = _user_info.getID() + "_" + KeyID.KEY_ROTA_FORTUNAE_INDEX + index;
		byte[] value = GetRotaFortunae(index).getData();
		return DBConnector.GetMembaseServer(_user_info.getID()).SetRaw(key, value);
	}
	
	public void Save()
	{
		for (RotaFortunae rf : _rota_fortunate_list)
		{
			SaveRotaFortunae(rf.getIndex());
		}
	}
	
	public RotaFortunae GetRotaFortunae(int idx)
	{
		for (RotaFortunae rf : _rota_fortunate_list)
		{
			if (rf.getIndex() == idx)
			{
				return rf;
			}
		}
		return null;
	}
	
	public byte[] GetDataToClient()
	{
		FBEncrypt encrypt = new FBEncrypt();
		int i = 0;
		encrypt.addInt(KeyID.KEY_ROTA_FORTUNAE_TOTAL, GetTotalRotaFortunaes());
		for (RotaFortunae rf : _rota_fortunate_list)
		{
			encrypt.addBinary(KeyID.KEY_ROTA_FORTUNAE_INDEX + i, rf.getData());
			i++;
		}
		encrypt.addInt(KeyID.KEY_ROTA_FORTUNAE_START_TIME, _start_time);
		encrypt.addInt(KeyID.KEY_ROTA_FORTUNAE_END_TIME, _end_time);
		encrypt.addString(KeyID.KEY_ROTA_FORTUNAE_GIFT_FAKE, _gift_fake);
		encrypt.addString(KeyID.KEY_ROTA_FORTUNAE_GOLD_LIST, _gold_list);
		encrypt.addInt(KeyID.KEY_ROTA_FORTUNAE_TOTAL_STAR, _total_star);
		return encrypt.toByteArray();
	}
	
	public byte[] GetDataToBase()
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_ROTA_FORTUNAE_START_TIME, _start_time);
		encrypt.addInt(KeyID.KEY_ROTA_FORTUNAE_END_TIME, _end_time);
		encrypt.addString(KeyID.KEY_ROTA_FORTUNAE_GIFT_FAKE, _gift_fake);
		encrypt.addString(KeyID.KEY_ROTA_FORTUNAE_GOLD_LIST, _gold_list);
		encrypt.addInt(KeyID.KEY_ROTA_FORTUNAE_TOTAL_STAR, _total_star);
		encrypt.addInt("_last_gen_time", _last_gen_time);
		return encrypt.toByteArray();
	}
	
	public int GetTotalRotaFortunaes()
	{
		return Server.s_globalDB[DatabaseID.SHEET_ROTA_FORTUNAE].length;
	}
	
	private int GetLastPaidIndex()
	{
		for (int i = GetTotalRotaFortunaes() - 1; i > 0; i--)
		{
			boolean paid = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_ROTA_FORTUNAE][i][DatabaseID.ROTA_FORTUNAE_PAID_OR_FREE]) == 1;
			if (paid)
			{
				return i;
			}
		}
		return GetTotalRotaFortunaes() - 1;
	}
	
	private int GetColumnIndexOfRatioForFreeWheel(int star, int fortune_times)
	{
		switch (star)
		{
			case 0:
			{
				switch (fortune_times)
				{
					case 0: return DatabaseID.ROTA_FORTUNAE_FREE_1;
					default: return DatabaseID.ROTA_FORTUNAE_FREE_2;
				}
			}
			case 1:
				switch (fortune_times)
				{
					case 0: return DatabaseID.ROTA_FORTUNAE_FREE_1_1ST_RETRY;
					default: return DatabaseID.ROTA_FORTUNAE_FREE_2_1ST_RETRY;
				}
			case 2:
			{
				switch (fortune_times)
				{
					case 0: return DatabaseID.ROTA_FORTUNAE_FREE_1_2ND_RETRY;
					default: return DatabaseID.ROTA_FORTUNAE_FREE_2_2ND_RETRY;
				}
			}
			case 3:
			{
				switch (fortune_times)
				{
					case 0: return DatabaseID.ROTA_FORTUNAE_FREE_1_3RD_RETRY;
					default: return DatabaseID.ROTA_FORTUNAE_FREE_2_3RD_RETRY;
				}
			}
			default:
				return 0;
		}
	}
	
	private int GetColumnIndexOfRatioForPaidWheel(int star, int fortune_times)
	{
		switch (star)
		{
			case 0:
			{
				switch (fortune_times)
				{
					case 0: return DatabaseID.ROTA_FORTUNAE_PAID_1;
					case 1: return DatabaseID.ROTA_FORTUNAE_PAID_2;
					case 2: return DatabaseID.ROTA_FORTUNAE_PAID_3;
					case 3: return DatabaseID.ROTA_FORTUNAE_PAID_4;
					default: return DatabaseID.ROTA_FORTUNAE_PAID_5;
				}
			}
			case 1:
				switch (fortune_times)
				{
					case 0: return DatabaseID.ROTA_FORTUNAE_PAID_1_1ST_RETRY;
					case 1: return DatabaseID.ROTA_FORTUNAE_PAID_2_1ST_RETRY;
					case 2: return DatabaseID.ROTA_FORTUNAE_PAID_3_1ST_RETRY;
					case 3: return DatabaseID.ROTA_FORTUNAE_PAID_4_1ST_RETRY;
					default: return DatabaseID.ROTA_FORTUNAE_PAID_5_1ST_RETRY;
				}
			case 2:
			{
				switch (fortune_times)
				{
					case 0: return DatabaseID.ROTA_FORTUNAE_PAID_1_2ND_RETRY;
					case 1: return DatabaseID.ROTA_FORTUNAE_PAID_2_2ND_RETRY;
					case 2: return DatabaseID.ROTA_FORTUNAE_PAID_3_2ND_RETRY;
					case 3: return DatabaseID.ROTA_FORTUNAE_PAID_4_2ND_RETRY;
					default: return DatabaseID.ROTA_FORTUNAE_PAID_5_2ND_RETRY;
				}
			}
			case 3:
			{
				switch (fortune_times)
				{
					case 0: return DatabaseID.ROTA_FORTUNAE_PAID_1_3RD_RETRY;
					case 1: return DatabaseID.ROTA_FORTUNAE_PAID_2_3RD_RETRY;
					case 2: return DatabaseID.ROTA_FORTUNAE_PAID_3_3RD_RETRY;
					case 3: return DatabaseID.ROTA_FORTUNAE_PAID_4_3RD_RETRY;
					default: return DatabaseID.ROTA_FORTUNAE_PAID_5_3RD_RETRY;
				}
			}
			default:
				return 0;
		}
	}
	
	public boolean isAvailable()
	{
		if (_rota_fortunate_list.size() == 0)
		{
			return true;
		}
		else
		{
			for (RotaFortunae rf : _rota_fortunate_list)
			{
				if (!rf.isUsed())
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public List<RotaFortunae> GetFortuneList()
	{
		return this._rota_fortunate_list;
	}
	
	public String GetGoldList()
	{
		return this._gold_list;
	}
	
	public int GetTotalStar()
	{
		return this._total_star;
	}
	
	/**
	 * Browse the fortune list and return the last index of FREE fortune
	 */
	private int GetLatestFreeFortuneIndex()
	{
		int r = 0;
		for (RotaFortunae rf : _rota_fortunate_list)
		{
			if (!rf.IsPaid())
			{
				r++;
			}
		}
		return r;
	}
	
	/**
	 * Browse the fortune list and return the last index of PAID fortune
	 */
	private int GetLatestPaidFortuneIndex()
	{
		int r = 0;
		for (RotaFortunae rf : _rota_fortunate_list)
		{
			if (rf.IsPaid())
			{
				r++;
			}
		}
		return r;
	}
	
	public int GetStartTime() {
		return this._start_time;
	}
	
	public void SetStartTime(int v) {
		this._start_time = v;
	}
	
	public int GetDepartTime() {
		return this._end_time;
	}
	
	public void SetDepartTime(int v) {
		this._end_time = v;
	}
	
	public String GenGoldList()
	{
		String start_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][0][DatabaseID.EVENT_GLOBAL_START_DATE]);
		String end_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][0][DatabaseID.EVENT_GLOBAL_END_DATE]);
		if(Misc.InEvent(start_event_time, end_event_time))
		{
			String[] basic_gold_list = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][_user_info.getLevel()][DatabaseID.CONSTANT_FORTUNE_WHEEL_GOLD]).split(";");
			StringBuilder new_gold_list = new StringBuilder();
			for (String s : basic_gold_list)
			{
				if (new_gold_list.length() == 0)
				{
					new_gold_list.append(Integer.parseInt(s) * 2);
				}
				else
				{
					new_gold_list.append(";").append(Integer.parseInt(s) * 2);
				}
			}
			return new_gold_list.toString();
		}
		else
		{
			return Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][_user_info.getLevel()][DatabaseID.CONSTANT_FORTUNE_WHEEL_GOLD]);
		}
	}
	
	public String GenFakeGiftList()
	{
		int remain_num = MAX_ITEM - _rota_fortunate_list.size();
		List<String> fake_list = new ArrayList<String>();
		int retry = 0;
		while (fake_list.size() < remain_num && retry < MAX_GEN_RETRY)
		{
			for (int i = 0; i <= remain_num; i++)
			{
				ArrayList<String> unlocked = new ArrayList<>();
				int item_type = 0;
				int item_id = 0;
				unlocked = Misc.getItemsUnlockWithItemType(_user_info.getLevel(), DatabaseID.USER_PROD_ID_UNLOCK);
				unlocked.addAll(Misc.getItemsUnlockWithItemType(_user_info.getLevel(), DatabaseID.USER_BUG_UNLOCK));
				unlocked.addAll(Misc.getItemsUnlockWithItemType(_user_info.getLevel(), DatabaseID.USER_MATERIAL_UNLOCK));
				unlocked.addAll(Misc.getItemsUnlockWithItemType(_user_info.getLevel(), DatabaseID.USER_DECOR_UNLOCK));
				unlocked.addAll(Misc.getItemsUnlockWithItemType(_user_info.getLevel(), DatabaseID.USER_POT_ID_UNLOCK));
				unlocked.add(DatabaseID.IT_MONEY + ":" + DatabaseID.DIAMOND_ID);

				int total_ratio = 0;
				for (String s : unlocked)
				{
					int temp_item_type = Integer.parseInt(s.split(":")[0]);
					int temp_item_id = Integer.parseInt(s.split(":")[1]);
					int item_ratio = Misc.GetItemValues(temp_item_type, temp_item_id, DatabaseID.ITEM_FORTUNE_WHEEL_FAKE_RATIO);
					total_ratio += item_ratio;
				}
				int random_item_ratio = Misc.RANDOM_RANGE(0, total_ratio - 1);
				int current_ratio = 0;
				for (String s : unlocked)
				{
					int temp_item_type = Integer.parseInt(s.split(":")[0]);
					int temp_item_id = Integer.parseInt(s.split(":")[1]);
					int item_ratio = Misc.GetItemValues(temp_item_type, temp_item_id, DatabaseID.ITEM_FORTUNE_WHEEL_FAKE_RATIO);
					current_ratio += item_ratio;
					if (current_ratio >= random_item_ratio)
					{
						item_type = temp_item_type;
						item_id = temp_item_id;
						break;
					}
				}
				
				boolean is_duplicate_real_list = false;
				for (RotaFortunae rf : _rota_fortunate_list)
				{
					if (rf.getGiftReal().contains(item_type + ":" + item_id))
					{
						is_duplicate_real_list = true;
						break;
					}
				}
				
				if (!fake_list.contains(item_type + ":" + item_id + ":" +  1) && !is_duplicate_real_list)
				{
					if (item_type == DatabaseID.IT_MONEY && item_id == DatabaseID.DIAMOND_ID)
					{
						String fake_diamond = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][_user_info.getLevel()][DatabaseID.CONSTANT_FORTUNE_FAKE_DIAMOND]);
						String[] aos = fake_diamond.split(":");
						int random_percent = Misc.RANDOM_RANGE(0, 100);
						int current_percent = 0;
						for (int j = 0; j < aos.length - 1; j+=2)
						{
							int num_diamond = Integer.parseInt(aos[j]);
							int percent = Integer.parseInt(aos[j+1]);
							
							current_percent += percent;
							if (current_percent >= random_percent)
							{
								fake_list.add(item_type + ":" + item_id + ":" +  num_diamond);
								break;
							}
						}
					}
					else
					{
						fake_list.add(item_type + ":" + item_id + ":" +  1);
					}
					
					retry = 0;
					if (fake_list.size() >= remain_num)
					{
						break;
					}
				}
				else
				{
					retry++;
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for (String s : fake_list)
		{
			String start_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][0][DatabaseID.EVENT_GLOBAL_START_DATE]);
			String end_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][0][DatabaseID.EVENT_GLOBAL_END_DATE]);
			if(Misc.InEvent(start_event_time, end_event_time))
			{
				String[] aos = s.split(":");
				s = aos[0] + ":" + aos[1] + ":" + 2;
			}
			
			if (sb.length() == 0)
			{
				sb.append(s);
			}
			else
			{
				sb.append(":");
				sb.append(s);
			}
		}
	
		return sb.toString();
	}
	
	public void SetLastGenTime(int v)
	{
		this._last_gen_time = v;
	}
	
	public int GetLastGenTime()
	{
		return this._last_gen_time;
	}
}

class RotaFortunae
{
	private int _index = -1;
	private boolean _is_paid = false;
	private int _diamond = 0;
	private String _gift_real;
	private String _gift_fake;
	private boolean _is_used = false;
	private int _retry_total = 0;
	private boolean _is_bought = false;
	private boolean _is_sale_off = false;
	private int _diamond_sale_off = 0;
	
	public RotaFortunae(int idx)
	{
		_index = idx;
		_is_paid = false;
		_diamond = 0;
		_gift_real = "";
		_gift_fake = "";
		_is_used = false;
		 _retry_total = 0;
		 _is_bought = false;
		 _is_sale_off = false;
		 _diamond_sale_off = 0;
	}
	
	public RotaFortunae(byte[] b)
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(b, true);
		
		_index = encrypt.getInt(KeyID.KEY_ROTA_FORTUNAE_ID);
		_is_paid = encrypt.getBoolean(KeyID.KEY_ROTA_FORTUNAE_IS_PAID);
		_diamond = encrypt.getInt(KeyID.KEY_ROTA_FORTUNAE_DIAMOND_PRICE);
		_gift_real = encrypt.getString(KeyID.KEY_ROTA_FORTUNAE_GIFT_REAL);
		_gift_fake = encrypt.getString(KeyID.KEY_ROTA_FORTUNAE_GIFT_FAKE);
		_is_used = encrypt.getBoolean(KeyID.KEY_ROTA_FORTUNAE_IS_USED);
		_retry_total = encrypt.getInt(KeyID.KEY_ROTA_FORTUNAE_TOTAL_RETRY);
		_is_bought = encrypt.getBoolean(KeyID.KEY_ROTA_FORTUNAE_IS_BOUGHT);
		_is_sale_off = encrypt.getBoolean(KeyID.KEY_ROTA_FORTUNAE_IS_SALE_OFF);
		_diamond_sale_off = encrypt.getInt(KeyID.KEY_ROTA_FORTUNAE_DIAMOND_SALE_OFF);
	}
	
	public byte[] getData()
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_ROTA_FORTUNAE_ID, _index);
		encrypt.addBoolean(KeyID.KEY_ROTA_FORTUNAE_IS_PAID, _is_paid);
		encrypt.addInt(KeyID.KEY_ROTA_FORTUNAE_DIAMOND_PRICE, _diamond);
		encrypt.addString(KeyID.KEY_ROTA_FORTUNAE_GIFT_REAL, _gift_real);
		encrypt.addString(KeyID.KEY_ROTA_FORTUNAE_GIFT_FAKE, _gift_fake);
		encrypt.addBoolean(KeyID.KEY_ROTA_FORTUNAE_IS_USED, _is_used);
		encrypt.addInt(KeyID.KEY_ROTA_FORTUNAE_TOTAL_RETRY, _retry_total);
		encrypt.addBoolean(KeyID.KEY_ROTA_FORTUNAE_IS_BOUGHT, _is_bought);
		encrypt.addBoolean(KeyID.KEY_ROTA_FORTUNAE_IS_SALE_OFF, _is_sale_off);
		encrypt.addInt(KeyID.KEY_ROTA_FORTUNAE_DIAMOND_SALE_OFF, _diamond_sale_off);
		
		LogHelper.LogHappy(ToString());
		
		return encrypt.toByteArray();
	}
	
	public String ToString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("index = ").append(_index);
		sb.append(", is_paid = ").append(_is_paid);
		sb.append(", diamond_price = ").append(_diamond);
		sb.append(", gift_real = ").append(GetGiftName());
//		sb.append(", gift_fake = ").append(_gift_fake);
		sb.append(", is_used = ").append(_is_used);
		sb.append(", retry_total = ").append(_retry_total);
		sb.append(", is_bought = ").append(_is_bought);
		sb.append(", _is_sale_off = ").append(_is_sale_off);
		sb.append(", _diamond_sale_off = ").append(_diamond_sale_off);
		return sb.toString();
	}
	
	private String GetGiftName()
	{
		if (_gift_real.length() > 0)
		{
			String[] aos = _gift_real.split(":");
			int type = Integer.parseInt(aos[0]);
			int id = Integer.parseInt(aos[1]);
			return Misc.GetItemName(type, id);
		}
		return "empty_gift";
	}

	/**
	 * AUTO GENERATED GETTERs & SETTERS
	 */
	public int getIndex() {
		return _index;
	}

	public void setIndex(int _index) {
		this._index = _index;
	}

	public boolean IsPaid() {
		return _is_paid;
	}

	public void setIsPaid(boolean _is_paid) {
		this._is_paid = _is_paid;
	}

	public int getDiamond() {
		return _diamond;
	}

	public void setDiamond(int _diamond) {
		this._diamond = _diamond;
	}

	public String getGiftReal() {
		return _gift_real;
	}

	public void setGiftReal(String _gift_real) {
		this._gift_real = _gift_real;
	}

	public String getGiftFake() {
		return _gift_fake;
	}

	public void setGiftFake(String _gift_fake) {
		this._gift_fake = _gift_fake;
	}

	public boolean isUsed() {
		return _is_used;
	}

	public void setUsed(boolean _is_used) {
		this._is_used = _is_used;
	}
	
	public int getRetryTotal()
	{
		return _retry_total;
	}
	
	public void setRetryTotal(int value)
	{
		this._retry_total = value;
	}
	
	public boolean isBought()
	{
		return _is_bought;
	}
	
	public void setBought(boolean b)
	{
		this._is_bought = b;
	}
	
	public boolean isSaleOff()
	{
		return _is_sale_off;
	}
	
	public void setSaleOff(boolean b)
	{
		this._is_sale_off = b;
	}
	
	public int getDiamondSaleOff()
	{
		return _diamond_sale_off;
	}
	
	public void setDiamondSaleOff(int v)
	{
		this._diamond_sale_off = v;
	}
}