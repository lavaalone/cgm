/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;
import com.vng.util.*;
import com.vng.netty.*;
import com.vng.log.*;
import com.vng.skygarden.DBConnector;
import com.vng.skygarden.SkyGarden;
import com.vng.skygarden._gen_.ProjectConfig;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author thinhnn3
 */
public class Airship
{
	private long _user_id = -1;
	private UserInfo _user_info = null;
	private int		_user_level = 0;
	private String _key = "";
	public List<Stock> _stock = new ArrayList<Stock>();
	public List<Machine> _machine = new ArrayList<Machine>();
	public List<Floor> _floor = new ArrayList<Floor>();
	
    private int _id = -1;
	private List<Cargo> _cargo_list = new LinkedList<Cargo>();
	private int _cargo_count = 0;
	private int _unlock_time = 0;
	private int _unlock_diamond = 0;
	private int _status = DatabaseID.AIRSHIP_LOCKED;
	private int _last_landing_time = 0;
	private int _depart_time = 0; // last_landing_time + stay_duration = depart_time
	private int _next_landing_time = 0; // depart_time + leave_duration = next_landing_time
	private int _last_gen_time = 0;
	private long _exp = 0;
	private long _gold = 0;
	private long _point = 0;
	private long _reputation = 0;
	
	// constant values from excel
	private int _required_level = -1;
	private String _required_items = "";
	private int _unlock_duration = -1;
	private int _stay_duration = -1;
	private int _leave_duration = -1;
	private int _min_point = -1;
	private int _max_point = -1;
	private int _help_time = -1;
	private int _min_bonus_gold_percent = -1;
	private int _max_bonus_gold_percent = -1;
	private int _min_bonus_exp_percent = -1;
	private int _max_bonus_exp_percent = -1;
	private int _point_reduce_per_hour = -1;
	private int _min_reputation = -1;
	private int _max_reputation = -1;
	private int _reputation_reduce_per_hour = -1;
	
	private final int SECONDS_REDUCE_REPUTATION = 3600;
	
	private final int MAX_RETRY = 20;
	private final int NUM_DEFAULT_EASY_REQUEST = 1;
	
	private final boolean USE_ORDER_CALCULATION_METHOD = true;
	private final boolean USE_DYNAMIC_STAY_DURATION = true;
	private final double STAY_DURATION_RATIO = 1.17;
	
	private final boolean USE_NEW_SKIP_TIME_PRICE = true; // remember to set true in the next version
	private final int SECONDS_PER_DIAMOND = 3600;//720;
	
	private boolean _has_key_airship_status = false;
	
	private int _current_airship_num = 0;
	
	private boolean _is_reset_airship_num = false;
	
	// default constructor
    Airship() 
	{
	}
	
	Airship(long user_id, String key, List<Stock> stock)
	{
		this._user_id = user_id;
		this._key = key;
		this._stock = stock;
	}
	
	Airship(UserInfo userinfo, String key, List<Stock> stock, List<Machine> machine, List<Floor> floor) 
	{
		this._user_info = userinfo;
		this._user_id = _user_info.getID(); 
		this._key = key;
		this._stock = stock;
		this._machine = machine;
		this._floor = floor;
		this._user_level = _user_info.getLevel();
	}
	
	/*
	 *Load latest status of Airship
	 */
	public boolean Load() 
	{
		if (_user_id <= 0) 
		{
			LogHelper.Log("Airship.. err! invalid user id");
			return false;
		}
		
		byte[] b = null;
		try 
		{
			b = DBConnector.GetMembaseServer(_user_id).GetRaw(_user_id + "_" + _key);
		}
		catch (Exception e) 
		{
			LogHelper.LogException("Airship.Load", e);
			return false;
		}
		
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(b, true);
		_id = encrypt.getInt(KeyID.KEY_AIRSHIP_ID);
		_unlock_time = encrypt.getInt(KeyID.KEY_AIRSHIP_UNLOCK_TIME);
		_unlock_diamond = GetUnlockDiamond();
		_gold = encrypt.getLong(KeyID.KEY_AIRSHIP_GOLD);
		_exp = encrypt.getLong(KeyID.KEY_AIRSHIP_EXP);
		_point = encrypt.getLong(KeyID.KEY_AIRSHIP_POINT);
		_reputation = encrypt.getLong(KeyID.KEY_AIRSHIP_REPUTATION);
		_cargo_count = encrypt.getInt(KeyID.KEY_AIRSHIP_CARGO_COUNT);
		_cargo_list.clear();
		for (int i = 0; i < _cargo_count; i++) 
		{
			byte[] bc = encrypt.getBinary(KeyID.KEY_AIRSHIP_CARGO + i);
			if (bc != null && bc.length > 0) 
			{
				Cargo c = new Cargo(bc, this);
				_cargo_list.add(c);
			}
		}
		_last_landing_time = encrypt.getInt(KeyID.KEY_AIRSHIP_LAST_LANDING_TIME);
		_next_landing_time = encrypt.getInt(KeyID.KEY_AIRSHIP_NEXT_LANDING_TIME);
		_depart_time = encrypt.getInt(KeyID.KEY_AIRSHIP_DEPART_TIME);
		_last_gen_time = encrypt.getInt("airship_last_gen_time");
		
		if (encrypt.hasKey(KeyID.KEY_AIRSHIP_STATUS))
		{
			_status = encrypt.getInt(KeyID.KEY_AIRSHIP_STATUS);
			_has_key_airship_status = true;
		}
		
		if (encrypt.hasKey(KeyID.KEY_AIRSHIP_CURRENT_NUM))
		{
			_current_airship_num = encrypt.getInt(KeyID.KEY_AIRSHIP_CURRENT_NUM);
		}
		
		if (encrypt.hasKey("_is_reset_airship_num"))
		{
			_is_reset_airship_num = encrypt.getBoolean("_is_reset_airship_num");
		}
		
		if (!_is_reset_airship_num || (_user_info != null && _user_info.GetLastLoginTime() < Misc.GetServerCurrentResetTime()))
		{
			_current_airship_num = 0;
			_is_reset_airship_num = true;
		}
		
		return true;
	}
	
	public void Save() 
	{
		if (_user_id <= 0 ) 
		{
			LogHelper.Log("Airship.Save... err! invalid user id");
			return;
		}
		DBConnector.GetMembaseServer(_user_id).SetRaw(_user_id + "_" + _key, GetDataToBase());
	}
	
	public byte[] GetDataToClient() 
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_AIRSHIP_ID, _id);
		encrypt.addInt(KeyID.KEY_AIRSHIP_CARGO_COUNT, GetCargoCount());
		for (Cargo c : _cargo_list) 
		{
			encrypt.addBinary(KeyID.KEY_AIRSHIP_CARGO + c.getId(), c.GetDataToClient());
		}
		
		encrypt.addInt(KeyID.KEY_AIRSHIP_REQUIRE_LEVEL, GetRequiredLevel(0));
		encrypt.addString(KeyID.KEY_AIRSHIP_REQUIRE_ITEMS, GetRequiredItems(0));
		encrypt.addInt(KeyID.KEY_AIRSHIP_UNLOCK_DURATION, GetUnlockDuration(0));
		encrypt.addInt(KeyID.KEY_AIRSHIP_UNLOCK_TIME, _unlock_time);		
		encrypt.addInt(KeyID.KEY_AIRSHIP_UNLOCK_DIAMOND, GetUnlockDiamond());		
		encrypt.addInt(KeyID.KEY_AIRSHIP_STATUS, GetStatus());		
		encrypt.addInt(KeyID.KEY_AIRSHIP_DIAMOND_PRICE, GetDiamondPriceQuickComplete());
		encrypt.addInt(KeyID.KEY_AIRSHIP_LAST_LANDING_TIME, _last_landing_time);
		encrypt.addInt(KeyID.KEY_AIRSHIP_NEXT_LANDING_TIME, _next_landing_time);
		encrypt.addInt(KeyID.KEY_AIRSHIP_DEPART_TIME, _depart_time);
		encrypt.addLong(KeyID.KEY_AIRSHIP_GOLD, _gold);
		encrypt.addLong(KeyID.KEY_AIRSHIP_EXP, _exp);
		encrypt.addLong(KeyID.KEY_AIRSHIP_POINT, GetPoint());
		encrypt.addLong(KeyID.KEY_AIRSHIP_REPUTATION, GetReputation());
		encrypt.addInt(KeyID.KEY_AIRSHIP_REPUTATION_REDUCE_PER_HOUR, GetReputationReducePerHour(_user_level));
		encrypt.addString(KeyID.KEY_AIRSHIP_PREVIEW_INFO, GetPreviewInfo());
		encrypt.addInt(KeyID.KEY_AIRSHIP_SKIP_DEPART_TIME_PRICE, GetDiamondPriceSkipDepartTime());
		encrypt.addInt(KeyID.KEY_AIRSHIP_CURRENT_NUM, _current_airship_num);
		encrypt.addInt(KeyID.KEY_AIRSHIP_MAX_NUM_PER_DAY, GetMaxAirshipPerDay(_user_level));
		encrypt.addInt(KeyID.KEY_AIRSHIP_SECONDS_REDUCE_REPUTATION, SECONDS_REDUCE_REPUTATION);
		encrypt.addInt(KeyID.KEY_AIRSHIP_SECONDS_PER_DIAMOND, SECONDS_PER_DIAMOND);
		return encrypt.toByteArray();
	}
	
	private byte[] GetDataToBase() {
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_AIRSHIP_ID, _id);
		encrypt.addInt(KeyID.KEY_AIRSHIP_UNLOCK_TIME, _unlock_time);
		encrypt.addInt(KeyID.KEY_AIRSHIP_CARGO_COUNT, _cargo_count);
		for (Cargo c : _cargo_list) {
			encrypt.addBinary(KeyID.KEY_AIRSHIP_CARGO + c.getId(), c.GetDataToBase());
		}
		encrypt.addInt(KeyID.KEY_AIRSHIP_LAST_LANDING_TIME, _last_landing_time);
		encrypt.addInt(KeyID.KEY_AIRSHIP_NEXT_LANDING_TIME, _next_landing_time);
		encrypt.addInt(KeyID.KEY_AIRSHIP_DEPART_TIME, _depart_time);
		encrypt.addLong(KeyID.KEY_AIRSHIP_GOLD, _gold);
		encrypt.addLong(KeyID.KEY_AIRSHIP_EXP, _exp);
		encrypt.addLong(KeyID.KEY_AIRSHIP_POINT, _point);
		encrypt.addLong(KeyID.KEY_AIRSHIP_REPUTATION, _reputation);
		encrypt.addInt("airship_last_gen_time", _last_gen_time);
		encrypt.addInt(KeyID.KEY_AIRSHIP_STATUS, GetStatus());
		encrypt.addInt(KeyID.KEY_AIRSHIP_CURRENT_NUM, _current_airship_num);
		encrypt.addBoolean("_is_reset_airship_num", _is_reset_airship_num);
		return encrypt.toByteArray();
	}
	
	public boolean GenerateRequest(int level, int bonus_exp, int bonus_gold)
	{
		if (level <= 0 || level >= Server.s_globalDB[DatabaseID.SHEET_CONSTANT].length) 
		{
			LogHelper.Log("Airship.GenerateRequest.. err! invalid level");
			return false;
		}
		
		_cargo_list.clear();
		_cargo_count = 0;
		_gold = 0;
		_exp = 0;
		_point = 0;
		_reputation = 0;
		
		// random num of type
		int min = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_MIN_ITEM_TYPE]);
		int max = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_MAX_ITEM_TYPE]);
		int num_of_type = Misc.RANDOM_RANGE(min, max);
		
		// random num of cargo
		min = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_MIN_CARGO_NUM_PER_ITEM_TYPE]);
		max = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_MAX_CARGO_NUM_PER_ITEM_TYPE]);
		int num_of_cargo = Misc.RANDOM_RANGE(min, max);
		
		// create 1 easy request
		int current_percent = 0;
		int retry = 0;
		int count_request = 0;
//		LogHelper.Log("Airship.GenerateRequest.. start gen easy request, num = " + NUM_DEFAULT_EASY_REQUEST);
		String[] easy_list = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_EASY_REQUEST]).split(":");
		while ((retry < MAX_RETRY) && (count_request < NUM_DEFAULT_EASY_REQUEST)) // 1 is default num of easy request
		{
			for (int i = 0; i < easy_list.length - 2; i += 3) {
				int type = Integer.parseInt(easy_list[i]);
				int id = Integer.parseInt(easy_list[i+1]);
				int percent = Integer.parseInt(easy_list[i+2]);
//				LogHelper.Log("Airship.GenerateRequest.. type = " + type + ", id = " + id + ", item name = " + Misc.GetItemName(type, id) + ", percent = " + percent);
				current_percent += percent;
				if (Misc.RANDOM_RANGE(0, 100) <= current_percent && Misc.IsItemUnlock(type, id, level) && !IsItemExist(type, id)) {
					// gen num of items
					min = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_MIN_NUM_REQUIRE_ITEM_EASY]);
					max = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_MAX_NUM_REQUIRE_ITEM_EASY]);
					int num = Misc.RANDOM_RANGE(min, max);
//					LogHelper.Log("Airship.GenerateRequest.. gen num of items, min = " + min + ", max = " + max + ", result = " + num);

					long exp = Misc.GetItemValues(type, id, DatabaseID.ITEM_VALUE_EXP);
					long gold = Misc.GetItemValues(type, id, DatabaseID.ITEM_VALUE_GOLD);
					long rep = 0;//Misc.GetItemValues(type, id, DatabaseID.ITEM_VALUE_REPUTATION);
					// create request of this item type and add to airship
					for (int j = 0; j < num_of_cargo; j++) {
						Cargo c = new Cargo(this);
						c.setId(_cargo_count);
						c.setItemType(type);
						c.setItemId(id);
						c.setItemNum(num);
						if (USE_ORDER_CALCULATION_METHOD)
						{
							c.setGold(GetGold(type, id, num));
							c.setExp(GetExp(type, id, num));
						}
						else
						{
							c.setGold(gold * num);
							c.setExp(exp * num);
						}
						
						c.setRepuration(rep * num);
						c.setAskForHelp(false);
						c.setIsFinished(false);
						_cargo_list.add(c);
						_cargo_count++;
					}
//					LogHelper.Log("Airship.GenerateRequest.. add cargo easy, cargo count = " + _cargo_count);
					count_request++;
					break;
				}
			}
			retry++;
		}
		
		// create 1-2 medium request
		retry = 0;
		count_request = 0;
		int num_of_medium_request = Misc.RANDOM_RANGE(1, 2);
//		LogHelper.Log("Airship.GenerateRequest.. start gen medium request, num = " + num_of_medium_request);
		String[] medium_list = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_MEDIUM_REQUEST]).split(":");
		while ((retry < MAX_RETRY) && (count_request < num_of_medium_request))
		{
			current_percent = 0;
			for (int i = 0; i < medium_list.length - 2; i += 3) {
				int type = Integer.parseInt(medium_list[i]);
				int id = Integer.parseInt(medium_list[i+1]);
				int percent = Integer.parseInt(medium_list[i+2]);
//				LogHelper.Log("Airship.GenerateRequest.. type = " + type + ", id = " + id + ", item name = " + Misc.GetItemName(type, id) + ", percent = " + percent);
				current_percent += percent;
				if (Misc.RANDOM_RANGE(0, 100) <= current_percent && Misc.IsItemUnlock(type, id, level) && !IsItemExist(type, id)) {
					// gen num of items
					min = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_MIN_NUM_REQUIRE_ITEM_MEDIUM]);
					max = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_MAX_NUM_REQUIRE_ITEM_MEDIUM]);
					int num = Misc.RANDOM_RANGE(min, max);
//					LogHelper.Log("Airship.GenerateRequest.. gen num of items, min = " + min + ", max = " + max + ", result = " + num);

					long exp = Misc.GetItemValues(type, id, DatabaseID.ITEM_VALUE_EXP);
					long gold = Misc.GetItemValues(type, id, DatabaseID.ITEM_VALUE_GOLD);
					long rep = 0;//Misc.GetItemValues(type, id, DatabaseID.ITEM_VALUE_REPUTATION);
					// create request of this item type and add to airship
					for (int j = 0; j < num_of_cargo; j++) {
						Cargo c = new Cargo(this);
						c.setId(_cargo_count);
						c.setItemType(type);
						c.setItemId(id);
						c.setItemNum(num);
						if (USE_ORDER_CALCULATION_METHOD)
						{
							c.setGold(GetGold(type, id, num));
							c.setExp(GetExp(type, id, num));
						}
						else
						{
							c.setGold(gold * num);
							c.setExp(exp * num);
						}
						c.setRepuration(rep * num);
						c.setAskForHelp(false);
						c.setIsFinished(false);
						_cargo_list.add(c);
						_cargo_count++;
					}
//					LogHelper.Log("Airship.GenerateRequest.. add cargo medium, cargo count = " + _cargo_count);
					count_request++;
					break;
				}
			}
			retry++;
		}
		
		// choose request hard (if there is)
		retry = 0;
		count_request = 0;
		int num_of_hard_request = num_of_type - 1 - num_of_medium_request;
//		LogHelper.Log("Airship.GenerateRequest.. start gen hard request, num = " + num_of_hard_request);
		String[] hard_list = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_HARD_REQUEST]).split(":");
		while ((retry < MAX_RETRY) && (count_request < num_of_hard_request)) {
			current_percent = 0;
			for (int i = 0; i < hard_list.length - 2; i += 3) {
				int type = Integer.parseInt(hard_list[i]);
				int id = Integer.parseInt(hard_list[i+1]);
				int percent = Integer.parseInt(hard_list[i+2]);
//				LogHelper.Log("Airship.GenerateRequest.. type = " + type + ", id = " + id + ", item name = " + Misc.GetItemName(type, id) + ", percent = " + percent);
				current_percent += percent;
				if (Misc.RANDOM_RANGE(0, 100) <= current_percent && Misc.IsItemUnlock(type, id, level) && !IsItemExist(type, id)) {
					// gen num of items
					min = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_MIN_NUM_REQUIRE_ITEM_HARD]);
					max = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_MAX_NUM_REQUIRE_ITEM_HARD]);
					int num = Misc.RANDOM_RANGE(min, max);
//					LogHelper.Log("Airship.GenerateRequest.. gen num of items, min = " + min + ", max = " + max + ", result = " + num);

					long exp = Misc.GetItemValues(type, id, DatabaseID.ITEM_VALUE_EXP);
					long gold = Misc.GetItemValues(type, id, DatabaseID.ITEM_VALUE_GOLD);
					long rep = 0;//Misc.GetItemValues(type, id, DatabaseID.ITEM_VALUE_REPUTATION);
					// create request of this item type and add to airship
					for (int j = 0; j < num_of_cargo; j++) {
						Cargo c = new Cargo(this);
						c.setId(_cargo_count);
						c.setItemType(type);
						c.setItemId(id);
						c.setItemNum(num);
						if (USE_ORDER_CALCULATION_METHOD)
						{
							c.setGold(GetGold(type, id, num));
							c.setExp(GetExp(type, id, num));
						}
						else
						{
							c.setGold(gold * num);
							c.setExp(exp * num);
						}
						c.setRepuration(rep * num);
						c.setAskForHelp(false);
						c.setIsFinished(false);
						_cargo_list.add(c);
						_cargo_count++;
					}
					count_request++;
					break;
				}
			}
			retry++;
		}
		
		// bonus gold & exp & airship point
		for (Cargo c : _cargo_list) {
			_gold += c.getGold();
			_exp += c.getExp();
		}
		_gold += (long)(_gold * (double)((double)Misc.RANDOM_RANGE(GetMinBonusGoldPercent(level), GetMaxBonusGoldPercent(level)) / 100));
		_exp += (long)(_exp * (double)((double)Misc.RANDOM_RANGE(GetMinBonusExpPercent(level), GetMaxBonusExpPercent(level)) / 100));
		LogHelper.LogHappy("Airship EXP before bonus := " + _exp);
		LogHelper.LogHappy("Airship GOLD before bonus := " + _gold);
		
//		String start_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][8][DatabaseID.EVENT_GLOBAL_START_DATE]);
//		String end_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][8][DatabaseID.EVENT_GLOBAL_END_DATE]);
//		if(Misc.InEvent(start_event_time, end_event_time))
//		{
//			int exp_bonus = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][8][DatabaseID.EVENT_GLOBAL_BONUS_EXP_RATE]);
//			LogHelper.LogHappy("Airship EXP bonus percent := " + exp_bonus);
//			if (exp_bonus > 0)
//			{
//				_exp = _exp + (_exp * (exp_bonus/100));
//			}
//			
//			int gold_bonus = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][8][DatabaseID.EVENT_GLOBAL_BONUS_GOLD_RATE]);
//			LogHelper.LogHappy("Airship GOLD bonus percent := " + gold_bonus);
//			if (gold_bonus > 0)
//			{
//				_gold = _gold + (_gold * (gold_bonus/100));
//			}
//		}
		
		if (SkyGarden._server_config.containsKey(KeyID.KEY_BONUS_AIRSHIP_GOLD))
		{
			int gold_bonus = SkyGarden._server_config.get(KeyID.KEY_BONUS_AIRSHIP_GOLD);
			LogHelper.LogHappy("Airship GOLD bonus percent := " + gold_bonus);
			if (gold_bonus > 0)
			{
				_gold = _gold + (_gold * gold_bonus)/100;
			}
		}
		
		if (SkyGarden._server_config.containsKey(KeyID.KEY_BONUS_AIRSHIP_EXP))
		{
			int exp_bonus = SkyGarden._server_config.get(KeyID.KEY_BONUS_AIRSHIP_EXP);
			LogHelper.LogHappy("Airship EXP bonus percent := " + exp_bonus);
			if (exp_bonus > 0)
			{
				_exp = _exp + (_exp * exp_bonus)/100;
				
			}
		}
		
		_gold = _gold + (_gold * bonus_gold)/100;
		_exp = _exp + (_exp * bonus_exp)/100;
		
		LogHelper.LogHappy("Airship EXP after bonus := " + _exp);
		LogHelper.LogHappy("Airship GOLD after bonus := " + _gold);
		
		_point = Misc.RANDOM_RANGE(GetMinPoint(_id), GetMaxPoint(_id));
		_reputation = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_REPUTATION]);
		_last_gen_time = Misc.SECONDS();
	
		return true;
	}
	
	public int GetDiamondPriceQuickComplete() {
		if (true) return 999;
		// todo: wrong
		int diamond = 0;
		for (Cargo c : _cargo_list) {
			diamond += c.GetDiamondPrice();
		}
		return diamond > 0 ? diamond : 0;
	}
	
	public int GetDiamondPriceSkipDepartTime() 
	{
		if (USE_NEW_SKIP_TIME_PRICE)
		{
			double remain_seconds = (double)_next_landing_time - (double)Misc.SECONDS();
			return (int)Math.ceil((remain_seconds / SECONDS_PER_DIAMOND));
		}
		else
		{
			return Misc.getDiamondEstimate(DatabaseID.DIAMOND_SKIP_TIME_PLANT, _next_landing_time);
		}
	}
	
	public boolean IsItemExist(int type, int id) {
		for (Cargo c : _cargo_list)
		{
			if (c.getItemType() == type && c.getItemId() == id)
			{
				return true;
			}
		}
		return false;
	}
	
	private long GetExp(int item_type, int item_id, int item_num)
	{
		long exp_basic = Misc.getGoldXPCoefficientEstimate(DatabaseID.ORDER_NORMAL, DatabaseID.EXP_ID, item_type, item_id, item_num, false, _user_level);
		if (exp_basic == 0)
		{
			exp_basic = 1;
		}
		
		long machine_bonus_ratio = getMachineBonusRatio(DatabaseID.EXP_ID);
		long exp_bonus_pot = getPotBonus(DatabaseID.EXP_ID, exp_basic, _floor);
		double exp_bonus_machine = ((double)(exp_basic * machine_bonus_ratio) / 100);
		double exp_bonus = exp_bonus_pot + exp_bonus_machine;
		return Math.round(exp_basic + exp_bonus);
	}
	
	private long GetGold(int item_type, int item_id, int item_num)
	{
		long gold_basic = Misc.getGoldXPCoefficientEstimate(DatabaseID.ORDER_NORMAL, DatabaseID.GOLD_ID, item_type, item_id, item_num, false, _user_level);
		if (gold_basic == 0)
		{
			gold_basic = 1;
		}
		
		long machine_bonus_ratio = getMachineBonusRatio(DatabaseID.GOLD_ID);
		double gold_bonus_machine = ((double)(gold_basic * machine_bonus_ratio) / 100);
		long gold_bonus_pot = getPotBonus(DatabaseID.GOLD_ID, gold_basic, _floor);
		double gold_bonus = gold_bonus_pot + gold_bonus_machine;
		return Math.round(gold_basic + gold_bonus);
	}
	
	private long getPotBonus(int type, long order_reward, List<Floor> floor)
	{
		long bonus_value = 0;	// gold, exp
		long bonus_rate = 0;	// %
		
		for (int i = 0; i < floor.size(); i++)
		{
			for (int j = 0; j < DatabaseID.MAX_SLOT_PER_FLOOR; j++)
			{
				int pot_id = floor.get(i).slot[j].pot.getID();
				
				if (pot_id > 0)
				{
					int gold_exp_type = (type == DatabaseID.GOLD_ID ? DatabaseID.POT_ORDER_GOLD_BONUS : DatabaseID.POT_ORDER_XP_BONUS);
					long pot_order_bonus = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_POT][pot_id][gold_exp_type]);
					bonus_rate += pot_order_bonus;
				}
			}
		}
		
		if (bonus_rate > 0)
		{
			double a = (double)(order_reward * bonus_rate) / 100;
			long x = (long)Math.round(a); 	// round
			bonus_value = x;
		}
		
		return bonus_value;
	}
	
	private long getMachineBonusRatio(int type)
	{
		long machine_bonus_ratio = 0;
		for (int i = 0; i < _machine.size(); i++)
		{
			Machine machine = _machine.get(i);
			if (type == DatabaseID.EXP_ID)
			{
				machine_bonus_ratio += (int)Server.s_globalMachineUnlockData[i][machine.getLevel()][DatabaseID.MACHINE_EXP_ORDER];
			}
			else
			{
				machine_bonus_ratio += (int)Server.s_globalMachineUnlockData[i][machine.getLevel()][DatabaseID.MACHINE_GOLD_ORDER];
			}
		}
		return machine_bonus_ratio;
	}

	/**
	 * AUTO GENERATED GETTER & SETTER
	 */
	public int GetId() {
		return _id;
	}

	public void SetId(int _id) {
		this._id = _id;
	}

	public List<Cargo> GetCargoList() {
		return _cargo_list;
	}

	public void SetCargoList(List<Cargo> _cargo_list) {
		this._cargo_list = _cargo_list;
	}

	public int GetCargoCount() {
		return _cargo_count;
	}
	
	public Cargo GetCargo(int cargo_id) {
		try {
			return _cargo_list.get(cargo_id);
		} catch (Exception e) {
			return null;
		}
	}

	public void SetCargoCount(int _cargo_count) {
		this._cargo_count = _cargo_count;
	}

	public int GetRequiredLevel(int id) {
		if (id < 0 || id > Server.s_globalDB[DatabaseID.SHEET_AIRSHIP].length) {
			return 0;
		}
		_required_level = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_AIRSHIP][id][DatabaseID.AIRSHIP_REQUIRE_LEVEL]);
		return _required_level;
	}

	public String GetRequiredItems(int id) {
		if (id < 0 || id > Server.s_globalDB[DatabaseID.SHEET_AIRSHIP].length) {
			return "";
		}
		_required_items = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_AIRSHIP][id][DatabaseID.AIRSHIP_REQUIRE_ITEMS]);
		return _required_items;
	}

	public int GetUnlockDuration(int id) {
		if (id < 0 || id > Server.s_globalDB[DatabaseID.SHEET_AIRSHIP].length) {
			return 0;
		}
		_unlock_duration = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_AIRSHIP][id][DatabaseID.AIRSHIP_UNLOCK_DURATION]);;
		return _unlock_duration;
	}

	public int GetStayDuration(int id) 
	{
		if (USE_DYNAMIC_STAY_DURATION)
		{
			double hour = _cargo_list.size() * STAY_DURATION_RATIO + Misc.RANDOM_RANGE(1, 2);
			_stay_duration = (int)hour*3600;
		}
		else
		{
			if (id < 0 || id > Server.s_globalDB[DatabaseID.SHEET_AIRSHIP].length)
			{
				return 0;
			}
			_stay_duration = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_AIRSHIP][id][DatabaseID.AIRSHIP_STAY_DURATION]);
		}
		
		return _stay_duration;
	}

	public int GetLeaveDuration(int id) {
		if (id < 0 || id > Server.s_globalDB[DatabaseID.SHEET_AIRSHIP].length) {
			return 0;
		}
		_leave_duration = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_AIRSHIP][id][DatabaseID.AIRSHIP_LEAVE_DURATION]);
		return _leave_duration;
	}

	public int GetMinPoint(int id) {
		if (id < 0 || id > Server.s_globalDB[DatabaseID.SHEET_AIRSHIP].length) {
			return 0;
		}
		_min_point = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_AIRSHIP][id][DatabaseID.AIRSHIP_MIN_POINT]);
		return _min_point;
	}

	public int GetMaxPoint(int id) {
		if (id < 0 || id > Server.s_globalDB[DatabaseID.SHEET_AIRSHIP].length) {
			return 0;
		}
		_max_point = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_AIRSHIP][id][DatabaseID.AIRSHIP_MAX_POINT]);
		return _max_point;
	}
	
	public int GetHelpTime(int id) {
		if (id < 0 || id > Server.s_globalDB[DatabaseID.SHEET_AIRSHIP].length) {
			return 0;
		}
		_help_time = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_AIRSHIP][id][DatabaseID.AIRSHIP_HELP_TIME]);
		return _help_time;
	}

	public int GetMinBonusGoldPercent(int level) {
		if (level < 0 || level > Server.s_globalDB[DatabaseID.SHEET_CONSTANT].length) {
			return 0;
		}
		_min_bonus_gold_percent = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_MIN_BONUS_GOLD]);
		return _min_bonus_gold_percent;
	}

	public int GetMaxBonusGoldPercent(int level) {
		if (level < 0 || level > Server.s_globalDB[DatabaseID.SHEET_CONSTANT].length) {
			return 0;
		}
		_max_bonus_gold_percent = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_MAX_BONUS_GOLD]);
		return _max_bonus_gold_percent;
	}

	public int GetMinBonusExpPercent(int level) {
		if (level < 0 || level > Server.s_globalDB[DatabaseID.SHEET_AIRSHIP].length) {
			return 0;
		}
		_min_bonus_exp_percent = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_MIN_BONUS_EXP]);
		return _min_bonus_exp_percent;
	}

	public int GetMaxBonusExpPercent(int level) {
		if (level < 0 || level > Server.s_globalDB[DatabaseID.SHEET_AIRSHIP].length) {
			return 0;
		}
		 _max_bonus_exp_percent = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_MAX_BONUS_EXP]);
		return _max_bonus_exp_percent;
	}

	public int GetPointReducePerHour(int id) 
	{
		if (id < 0 || id > Server.s_globalDB[DatabaseID.SHEET_AIRSHIP].length) {
			return 0;
		}
		_point_reduce_per_hour = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_AIRSHIP][id][DatabaseID.AIRSHIP_POINT_REDUCE]);
		return _point_reduce_per_hour;
	}
	
	public int GetMinReputation(int id) {
		if (id < 0 || id > Server.s_globalDB[DatabaseID.SHEET_AIRSHIP].length) {
			return 0;
		}
		_min_reputation = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_AIRSHIP][id][DatabaseID.AIRSHIP_MIN_REPUTATION]);
		return _min_reputation;
	}

	public int GetMaxReputation(int id) {
		if (id < 0 || id > Server.s_globalDB[DatabaseID.SHEET_AIRSHIP].length) {
			return 0;
		}
		_max_reputation = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_AIRSHIP][id][DatabaseID.AIRSHIP_MAX_REPUTATION]);
		return _max_reputation;
	}
	
	public int GetReputationReducePerHour(int level) {
		if (level < 0 || level > Server.s_globalDB[DatabaseID.SHEET_CONSTANT].length) {
			return 0;
		}
		
		_reputation_reduce_per_hour = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_AIRSHIP_REPUTATION_REDUCE_PER_HOUR]);
		return _reputation_reduce_per_hour;
	}

	public int GetUnlockTime() {
		return _unlock_time;
	}

	public void SetUnlockTime(int _unlock_time) {
		this._unlock_time = _unlock_time;
	}

	public int GetUnlockDiamond() {
		_unlock_diamond = Misc.getDiamondEstimate(DatabaseID.DIAMOND_SKIP_TIME_MACHINE, _unlock_time);
		return _unlock_diamond;
	}
	
	public int GetStatus() 
	{
		if (!_has_key_airship_status)
		{
			_has_key_airship_status = true;
			// check if locked or unlocked or pending
			if (_id == -1) 
			{
				_status = DatabaseID.AIRSHIP_LOCKED;
			} 
			else 
			{
				if ((_unlock_time > Misc.SECONDS())) 
				{
					_status = DatabaseID.AIRSHIP_PENDING;
				} 
				else 
				{
					_status = DatabaseID.AIRSHIP_UNLOCKED;
				}
			}

			// check if landing or departing
			if (_status == DatabaseID.AIRSHIP_UNLOCKED) 
			{
				if (Misc.SECONDS() < _depart_time) 
				{
					_status = DatabaseID.AIRSHIP_LANDING;
				} 
				else if (Misc.SECONDS() >= _depart_time && Misc.SECONDS() <= _next_landing_time) 
				{
					_status = DatabaseID.AIRSHIP_DEPARTING;
				}
				else if (Misc.SECONDS() >= _next_landing_time) 
				{
					_status = DatabaseID.AIRSHIP_LANDING;
				}
			}
		}

		// check if full or not
		if (_status == DatabaseID.AIRSHIP_LANDING && IsFinished()) 
		{
			_status = DatabaseID.AIRSHIP_FULL;
		}

		return _status;
	}
	
	public void SetStatus(int v) {
		_status = v;
	}

	public int GetLastLandingTime() {
		return _last_landing_time;
	}

	public void SetLastLandingTime(int last_landing_time) 
	{
		this._last_landing_time = last_landing_time;
	}

	public int GetDepartTime() {
		return _depart_time;
	}

	public void SetDepartTime(int depart_time) 
	{
		this._depart_time = depart_time;
	}

	public int GetNextLandingTime() {
		return _next_landing_time;
	}

	public void SetNextLandingTime(int next_landing_time) 
	{
		boolean in_event = false;
		String startevent = "04/10/2014 06:00:00";
		String endevent = "11/10/2014 23:59:59";
		try
		{
			SimpleDateFormat datef = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
			long millisec_start_event = datef.parse(startevent).getTime();
			long millisec_end_event = datef.parse(endevent).getTime();
			
			if(System.currentTimeMillis() >= millisec_start_event // in event time
				&& System.currentTimeMillis() <= millisec_end_event // in event time
			)
			{
				in_event = true;
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("ExecutecheckInEvent", e);
			in_event = false;
		}
		
		if (!in_event)
		{
			if (_current_airship_num >= GetMaxAirshipPerDay(_user_level))
			{
				this._next_landing_time = (int)(Misc.GetServerNextResetTime() - Misc.MILLISECONDS_OF_1_1_2010()/1000);
			}
			else
			{
				this._next_landing_time = next_landing_time;
			}
		}
		else
		{
			if (_current_airship_num >= 10)
			{
				this._next_landing_time = Misc.SECONDS() + 60 * 60 * 8;
			}
			else
			{
				this._next_landing_time = next_landing_time;
			}
		}
	}
	
	public long GetGold() 
	{ 
		return _gold; 
	}
	
	public long GetExp() 
	{ 
		return _exp; 
	}
	
	public long GetReputation() 
	{
		int elapsed = Misc.SECONDS() - GetLastLandingTime();
		int reduce = (elapsed / SECONDS_REDUCE_REPUTATION) * GetReputationReducePerHour(_user_level);
		long remain = _reputation - reduce;
		return remain > 0 ? remain : 0; 
	}
	
	public long GetPoint() 
	{
		// calculate dynamic point
		int elapsed = Misc.SECONDS() - GetLastLandingTime();
		int reduce = (elapsed / 3600) * GetPointReducePerHour(_id);
		long remain = _point - reduce;
		return remain; 
	}
	
	public String GetPreviewInfo() 
	{
		StringBuilder sb = new StringBuilder();
		String previous_item = "";
		for (Cargo c : _cargo_list) {
			String item = c.getItemType() + ":" + c.getItemId();
			if (!previous_item.equals(item)) {
				previous_item = item;
				if (sb.length() > 0)
					sb.append(":");
				sb.append(previous_item);
			}
		}
//		LogHelper.Log("Airship.GetPreviewInfo.. value = " + sb.toString());
		return sb.toString();
	}
	
	public boolean IsFinished() {
		boolean finish = true;
		if (_cargo_list.size() > 0) {
			for (Cargo c : _cargo_list) {
				if (!c.isIsFinished()) {
					finish = false;
					break;
				}
			}
		} else {
			finish = false;
		}
		return finish;
	}
	
	public int GetLastGenTime() 
	{
		return _last_gen_time;
	}
	
	public int GetCurrentAirshipNum()
	{
		return _current_airship_num;
	}
	
	public void SetAirshipNum(int v)
	{
		this._current_airship_num = v;
	}
	
	public int GetMaxAirshipPerDay(int level) {
		if (level < 0 || level > Server.s_globalDB[DatabaseID.SHEET_CONSTANT].length) {
			return 0;
		}
		
		String event_start_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][6][DatabaseID.EVENT_GLOBAL_START_DATE]);
		String event_end_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][6][DatabaseID.EVENT_GLOBAL_END_DATE]);
		boolean in_event = Misc.InEvent(event_start_time, event_end_time);
		
		if (!in_event) {
			return (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][level][DatabaseID.CONSTANT_MAX_AIRSHIP_PER_DAY]);
		} else {
			return 20;
		}
	}
}

class Cargo 
{
	private int _id = -1;
	private int _item_type = -1;
	private int _item_id = -1;
	private int _item_num = -1;
	private long _exp = 0;
	private long _gold = 0;
	private long _repuration = 0;
	private boolean _is_finished = false;
	private boolean _ask_for_help = false;
	private long _friend_id = 0;
	private Airship _airship;
	
	Cargo(Airship airship) 
	{
		this._airship = airship;
	}
	
	Cargo(byte[] b, Airship airship) 
	{
		this._airship = airship;
		
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(b, true);
		
		_id				= encrypt.getInt(KeyID.KEY_CARGO_ID);
		_item_type		= encrypt.getInt(KeyID.KEY_CARGO_ITEM_TYPE);
		_item_id		= encrypt.getInt(KeyID.KEY_CARGO_ITEM_ID);
		_item_num		= encrypt.getInt(KeyID.KEY_CARGO_ITEM_NUM);
		_exp			= encrypt.getLong(KeyID.KEY_CARGO_EXP);
		_gold			= encrypt.getLong(KeyID.KEY_CARGO_GOLD);
		_repuration		= encrypt.getLong(KeyID.KEY_CARGO_REPUTATION);
		_is_finished	= encrypt.getBoolean(KeyID.KEY_CARGO_IS_FINISHED);
		_ask_for_help	= encrypt.getBoolean(KeyID.KEY_CARGO_ASK_FOR_HELP);
		_friend_id		= encrypt.getLong(KeyID.KEY_CARGO_FRIEND_ID);
	}
	
	public byte[] GetDataToClient() 
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_CARGO_ID, _id);
		encrypt.addInt(KeyID.KEY_CARGO_ITEM_TYPE, _item_type);
		encrypt.addInt(KeyID.KEY_CARGO_ITEM_ID, _item_id);
		encrypt.addInt(KeyID.KEY_CARGO_ITEM_NUM, _item_num);
		encrypt.addLong(KeyID.KEY_CARGO_EXP, _exp);
		encrypt.addLong(KeyID.KEY_CARGO_GOLD, _gold);
		encrypt.addLong(KeyID.KEY_CARGO_REPUTATION, _repuration);
		encrypt.addBoolean(KeyID.KEY_CARGO_IS_FINISHED, _is_finished);
		encrypt.addBoolean(KeyID.KEY_CARGO_ASK_FOR_HELP, _ask_for_help);
		encrypt.addInt(KeyID.KEY_CARGO_DIAMOND_PRICE, GetDiamondPrice());
		encrypt.addLong(KeyID.KEY_CARGO_FRIEND_ID, _friend_id);
		
		if (_friend_id > 0)
		{
			encrypt.addBinary(KeyID.KEY_USER_INFOS, ServerHandler.GetUserData(_friend_id));
		}
		
		return encrypt.toByteArray();
	}
	
	public byte[] GetDataToBase() 
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_CARGO_ID, _id);
		encrypt.addInt(KeyID.KEY_CARGO_ITEM_TYPE, _item_type);
		encrypt.addInt(KeyID.KEY_CARGO_ITEM_ID, _item_id);
		encrypt.addInt(KeyID.KEY_CARGO_ITEM_NUM, _item_num);
		encrypt.addLong(KeyID.KEY_CARGO_EXP, _exp);
		encrypt.addLong(KeyID.KEY_CARGO_GOLD, _gold);
		encrypt.addLong(KeyID.KEY_CARGO_REPUTATION, _repuration);
		encrypt.addBoolean(KeyID.KEY_CARGO_IS_FINISHED, _is_finished);
		encrypt.addBoolean(KeyID.KEY_CARGO_ASK_FOR_HELP, _ask_for_help);
		encrypt.addInt(KeyID.KEY_CARGO_DIAMOND_PRICE, GetDiamondPrice());
		encrypt.addLong(KeyID.KEY_CARGO_FRIEND_ID, _friend_id);
		return encrypt.toByteArray();
	}
	
	public int GetDiamondPrice() 
	{
		int basic_price = 0;
		int in_stock = 0;
		switch (_item_type) 
		{
			case DatabaseID.IT_PLANT:
				basic_price = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_SEED][_item_id][DatabaseID.SEED_DIAMOND_BUY]);
				in_stock = _airship._stock.get(DatabaseID.STOCK_SILO).getProductNum(_item_type, _item_id);
				break;
			case DatabaseID.IT_PRODUCT:
				basic_price = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PRODUCT][_item_id][DatabaseID.PRODUCT_DIAMOND_BUY]);
				in_stock = _airship._stock.get(DatabaseID.STOCK_BARN).getProductNum(_item_type, _item_id);
				break;
			case DatabaseID.IT_BUG:
				basic_price = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PEST][_item_id][DatabaseID.PEST_DIAMOND_BUY]);
				in_stock = _airship._stock.get(DatabaseID.STOCK_BARN).getProductNum(_item_type, _item_id);
				break;
		}
		int diamond = (_item_num - in_stock) * basic_price;
		diamond = diamond > 0 ? diamond : 0;
		return diamond;
	}
	
	public String ToString() {
		return ("id = " + _id + ", item = " + Misc.GetItemName(_item_type, _item_id) + ", num = " + _item_num + ", exp = " + _exp + ", gold = " + _gold + ", rep = " + _repuration + ", finish = " + _is_finished + ", help = " + _ask_for_help);
	}

	/**
	 * AUTO GENERATED GETTER & SETTER
	 */
	public int getId() {
		return _id;
	}

	public void setId(int _id) {
		this._id = _id;
	}

	public int getItemType() {
		return _item_type;
	}

	public void setItemType(int _item_type) {
		this._item_type = _item_type;
	}

	public int getItemId() {
		return _item_id;
	}

	public void setItemId(int _item_id) {
		this._item_id = _item_id;
	}

	public int getItemNum() {
		return _item_num;
	}

	public void setItemNum(int _item_num) {
		this._item_num = _item_num;
	}

	public boolean isIsFinished() {
		return _is_finished;
	}

	public void setIsFinished(boolean _is_finished) {
		this._is_finished = _is_finished;
	}
	
	public long getExp() {
		return _exp;
	}

	public void setExp(long _exp) {
		this._exp = _exp;
	}

	public long getGold() {
		return _gold;
	}

	public void setGold(long _gold) {
		this._gold = _gold;
	}

	public long getRepuration() {
		return _repuration;
	}

	public void setRepuration(long _repuration) {
		this._repuration = _repuration;
	}

	public boolean isAskForHelp() {
		return _ask_for_help;
	}

	public void setAskForHelp(boolean _ask_for_help) {
		this._ask_for_help = _ask_for_help;
	}
	
	public void setFriendID(long v)
	{
		this._friend_id = v;
	}
	
	public long getFriendID()
	{
		return this._friend_id;
	}
}

