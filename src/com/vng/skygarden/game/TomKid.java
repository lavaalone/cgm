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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author thinhnn3
 */
public class TomKid
{
	private int _status = -1;
	private boolean _first_used = true;
	private List<SuggestItem> _suggest_list = new LinkedList<>();
	private int _last_hire_time = -1;
	private int _expired_hire_time = -1;
	private int _last_working_time = -1;
	private int _next_working_time = -1;
	
	// constant
	private int _hire_price_1_day = -1;
	private int _hire_price_3_day = -1;
	private int _hire_price_7_day = -1;
	private int _long_rest_duration = -1;
	private int _short_rest_duration = -1;
	private int _first_use_duration = -1;
	
	// default constructor
    TomKid() {}
	
	TomKid(byte[] data) {
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(data, true);
		
		_first_used = encrypt.getBoolean(KeyID.KEY_TOM_KID_FIRST_USED);
		_last_hire_time = encrypt.getInt(KeyID.KEY_TOM_KID_LAST_HIRE_TIME);
		_expired_hire_time = encrypt.getInt(KeyID.KEY_TOM_KID_EXPIRE_HIRE_TIME);
		_last_working_time = encrypt.getInt(KeyID.KEY_TOM_KID_LAST_WORKING_TIME);
		_next_working_time = encrypt.getInt(KeyID.KEY_TOM_KID_NEXT_WORKING_TIME);
		_status = GetStatus();
		_suggest_list.clear();
		for (int i = 0; i < 3; i++) {
			byte[] b = encrypt.getBinary(KeyID.KEY_TOM_KID_SUGGEST_ITEM + i);
			if (b != null && b.length > 0) {
				SuggestItem si = new SuggestItem(b);
				_suggest_list.add(si);
				//debug
//				LogHelper.Log("TomKid.Load suggest item " + i + ", content: " + si.ToString());
			}
		}
//		LogHelper.Log("TomKid.Load.. status = " + _status + ", first use = " + _first_used + ", last hire time = " + _last_hire_time + ", expire hire time = " + _expired_hire_time);
	}
	
	public byte[] GetData(boolean save) {
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_TOM_KID_STATUS, GetStatus());
		encrypt.addInt(KeyID.KEY_TOM_KID_LAST_HIRE_TIME, _last_hire_time);
		encrypt.addInt(KeyID.KEY_TOM_KID_EXPIRE_HIRE_TIME, _expired_hire_time);
		encrypt.addBoolean(KeyID.KEY_TOM_KID_FIRST_USED, _first_used);
		encrypt.addInt(KeyID.KEY_TOM_KID_LAST_WORKING_TIME, _last_working_time);
		encrypt.addInt(KeyID.KEY_TOM_KID_NEXT_WORKING_TIME, _next_working_time);
		encrypt.addInt(KeyID.KEY_TOM_KID_UNLOCK_LEVEL, GetUnlockLevel());
		encrypt.addInt(KeyID.KEY_TOM_KID_SUGGEST_COUNT, DatabaseID.TOMKID_DEFAULT_SUGGEST_NUM);
		for (SuggestItem si : _suggest_list) {
			encrypt.addBinary(KeyID.KEY_TOM_KID_SUGGEST_ITEM + _suggest_list.indexOf(si), si.GetData());
		}
		if (!save) {
			encrypt.addInt(KeyID.KEY_TOM_KID_PRICE_HIRE_1_DAY, getHirePrice1Day());
			encrypt.addInt(KeyID.KEY_TOM_KID_PRICE_HIRE_3_DAY, getHirePrice3Day());
			encrypt.addInt(KeyID.KEY_TOM_KID_PRICE_HIRE_7_DAY, getHirePrice7Day());
		}
		return encrypt.toByteArray();
	}
	
	public int GetStatus() {
		int status = -1;
		if (_suggest_list.size() == DatabaseID.TOMKID_DEFAULT_SUGGEST_NUM) 
		{
			status = DatabaseID.TOMKID_PROVIDING_GOODS;
		}
		else
		{
			if (_last_hire_time == -1) 
			{
				status = DatabaseID.TOMKID_LOCKED;
			} 
			else 
			{
				if (Misc.SECONDS() < _expired_hire_time) 
				{
					if (Misc.SECONDS() >= _next_working_time) 
					{
						status = DatabaseID.TOMKID_READY;
					}
					else 
					{
						status = DatabaseID.TOMKID_RESTING;
					}
				} 
				else 
				{
					status = DatabaseID.TOMKID_NOT_HIRED;
				}
			}
		}
		
		return status;
	}
	
	public void GenerateGoods(int type, int id) {
		_suggest_list.clear();
		// pack 1
		int min_num = Misc.GetItemValues(type, id, DatabaseID.ITEM_TOMKID_MIN_NUM_PACK_1);
		int max_num = Misc.GetItemValues(type, id, DatabaseID.ITEM_TOMKID_MAX_NUM_PACK_1);
		int num = Misc.RANDOM_RANGE(min_num, max_num);
		int min_ratio = Misc.GetItemValues(type, id, DatabaseID.ITEM_TOMKID_MIN_RATIO_PACK_1);
		int max_ratio = Misc.GetItemValues(type, id, DatabaseID.ITEM_TOMKID_MAX_RATIO_PACK_1);
		int ratio = Misc.RANDOM_RANGE(min_ratio, max_ratio);
		int basic_gold = Misc.GetItemValues(type, id, DatabaseID.ITEM_TOMKID_GOLD_BASIC);
		int final_gold = (int)(((double)ratio / 100) * basic_gold);
		if (final_gold == 0) final_gold = 1;
//		LogHelper.Log("TomKid.GenerateGoods.. pack 1: num min = " + min_num + ", num max = " + max_num + ", result num = " + num + ", min ratio = " + min_ratio +
//				", max ratio = " + max_ratio + ", result ratio = " + ratio + ", basic gold = " + basic_gold + ", gold per item = " + final_gold + ", final gold = " + final_gold*num);
		SuggestItem sgi = new SuggestItem();
		sgi.setId(0);
		sgi.setType(type);
		sgi.setItemId(id);
		sgi.setNum(num);
		sgi.setGold(final_gold*num);
		_suggest_list.add(sgi);
		
		// pack 2
		min_num = Misc.GetItemValues(type, id, DatabaseID.ITEM_TOMKID_MIN_NUM_PACK_2);
		max_num = Misc.GetItemValues(type, id, DatabaseID.ITEM_TOMKID_MAX_NUM_PACK_2);
		num = Misc.RANDOM_RANGE(min_num, max_num);
		min_ratio = Misc.GetItemValues(type, id, DatabaseID.ITEM_TOMKID_MIN_RATIO_PACK_2);
		max_ratio = Misc.GetItemValues(type, id, DatabaseID.ITEM_TOMKID_MAX_RATIO_PACK_2);
		ratio = Misc.RANDOM_RANGE(min_ratio, max_ratio);
		basic_gold = Misc.GetItemValues(type, id, DatabaseID.ITEM_TOMKID_GOLD_BASIC);
		final_gold = (int)(((double)ratio / 100) * basic_gold);
		if (final_gold == 0) final_gold = 1;
//		LogHelper.Log("TomKid.GenerateGoods.. pack 2: num min = " + min_num + ", num max = " + max_num + ", result num = " + num + ", min ratio = " + min_ratio +
//				", max ratio = " + max_ratio + ", result ratio = " + ratio + ", basic gold = " + basic_gold + ", gold per item = " + final_gold + ", final gold = " + final_gold*num);
		sgi = new SuggestItem();
		sgi.setId(1);
		sgi.setType(type);
		sgi.setItemId(id);
		sgi.setNum(num);
		sgi.setGold(final_gold*num);
		_suggest_list.add(sgi);
		
		// pack 3
		min_num = Misc.GetItemValues(type, id, DatabaseID.ITEM_TOMKID_MIN_NUM_PACK_3);
		max_num = Misc.GetItemValues(type, id, DatabaseID.ITEM_TOMKID_MAX_NUM_PACK_3);
		num = Misc.RANDOM_RANGE(min_num, max_num);
		min_ratio = Misc.GetItemValues(type, id, DatabaseID.ITEM_TOMKID_MIN_RATIO_PACK_3);
		max_ratio = Misc.GetItemValues(type, id, DatabaseID.ITEM_TOMKID_MAX_RATIO_PACK_3);
		ratio = Misc.RANDOM_RANGE(min_ratio, max_ratio);
		basic_gold = Misc.GetItemValues(type, id, DatabaseID.ITEM_TOMKID_GOLD_BASIC);
		final_gold = (int)(((double)ratio / 100) * basic_gold);
		if (final_gold == 0) final_gold = 1;
//		LogHelper.Log("TomKid.GenerateGoods.. pack 3: num min = " + min_num + ", num max = " + max_num + ", result num = " + num + ", min ratio = " + min_ratio +
//				", max ratio = " + max_ratio + ", result ratio = " + ratio + ", basic gold = " + basic_gold + ", gold per item = " + final_gold + ", final gold = " + final_gold*num);
		sgi = new SuggestItem();
		sgi.setId(2);
		sgi.setType(type);
		sgi.setItemId(id);
		sgi.setNum(num);
		sgi.setGold(final_gold*num);
		_suggest_list.add(sgi);
		
//		LogHelper.Log("TomKid.GenerateGoods.. OK! Suggest size = " + _suggest_list.size());
	}

	/**
	 * AUTO GENERATED GETTER & SETTER
	 */
	public int getStatus() {
		return _status;
	}

	public void setStatus(int _status) {
		this._status = _status;
	}

	public boolean isFirstUsed() {
		return _first_used;
	}

	public void setFirstUsed(boolean _first_used) {
		this._first_used = _first_used;
	}

	public List<SuggestItem> getSuggestList() {
		return _suggest_list;
	}

	public int getLastHireTime() {
//		LogHelper.Log("TomKid.getLastHireTime.. value = " + _last_hire_time);
		return _last_hire_time;
	}

	public void setLastHireTime(int _last_hire_time) {
		this._last_hire_time = _last_hire_time;
	}

	public int getExpiredHireTime() {
//		LogHelper.Log("TomKid.getExpiredHireTime.. value = " + _expired_hire_time);
		return _expired_hire_time;
	}

	public void setExpiredHireTime(int _expired_hire_time) {
		this._expired_hire_time = _expired_hire_time;
	}

	public int getHirePrice1Day() {
		_hire_price_1_day = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_TOM_KID][0][DatabaseID.TOMKID_DIAMOND_1_DAY]);
//		LogHelper.Log("TomKid.getHirePrice1Day.. value = " + _hire_price_1_day);
		return _hire_price_1_day;
	}

	public int getHirePrice3Day() {
		_hire_price_3_day = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_TOM_KID][0][DatabaseID.TOMKID_DIAMOND_3_DAY]);
//		LogHelper.Log("TomKid.getHirePrice3Day.. value = " + _hire_price_3_day);
		return _hire_price_3_day;
	}

	public int getHirePrice7Day() {
		_hire_price_7_day = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_TOM_KID][0][DatabaseID.TOMKID_DIAMOND_7_DAY]);
//		LogHelper.Log("TomKid.getHirePrice7Day.. value = " + _hire_price_7_day);
		return _hire_price_7_day;
	}

	public int getLongRestDuration() {
		_long_rest_duration = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_TOM_KID][0][DatabaseID.TOMKID_LONG_REST_DURATION]);
//		LogHelper.Log("TomKid.getLongRestDuration.. value = " + _long_rest_duration);
		return _long_rest_duration;
	}

	public int getShortRestDuration() {
		_short_rest_duration = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_TOM_KID][0][DatabaseID.TOMKID_SHORT_REST_DURATION]);
//		LogHelper.Log("TomKid.getShortRestDuration.. value = " + _short_rest_duration);
		return _short_rest_duration;
	}
	
	public int getFirstUseDuration() {
		_first_use_duration = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_TOM_KID][0][DatabaseID.TOMKID_FIRST_USE_DURATION]);
//		LogHelper.Log("TomKid.getFirstUseDuration.. value = " + _first_use_duration);
		return _first_use_duration;
	}
	
	public int GetUnlockLevel() {
		return (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_TOM_KID][0][DatabaseID.TOMKID_LEVEL_UNLOCK]);
	}
	
	
	public int getLastWorkingTime() {
		return _last_working_time;
	}
	
	public void setLastWorkingTime(int time) {
		this._last_working_time = time;
	}
	
	public int getNextWorkingTime() {
		return _next_working_time;
	}
	
	public void setNextWorkingTime(int time) {
		this._next_working_time = time;
	}
}

class SuggestItem {
	private int _id = -1;
	private int _item_type = -1;
	private int _item_id = -1;
	private int _item_num = -1;
	private long _gold = -1;
	
	// default constructor
	SuggestItem() {}
	
	SuggestItem(byte[] data) {
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(data, true);
		_id = encrypt.getInt(KeyID.KEY_TOM_KID_SUGGEST_ITEM_ID);
		_item_type = encrypt.getInt(KeyID.KEY_TOM_KID_ITEM_TYPE);
		_item_id = encrypt.getInt(KeyID.KEY_TOM_KID_ITEM_ID);
		_item_num = encrypt.getInt(KeyID.KEY_TOM_KID_ITEM_NUM);
		_gold = encrypt.getLong(KeyID.KEY_TOM_KID_ITEM_GOLD_PRICE);
	}
	
	public byte[] GetData() {
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_TOM_KID_SUGGEST_ITEM_ID, _id);
		encrypt.addInt(KeyID.KEY_TOM_KID_ITEM_TYPE, _item_type);
		encrypt.addInt(KeyID.KEY_TOM_KID_ITEM_ID, _item_id);
		encrypt.addInt(KeyID.KEY_TOM_KID_ITEM_NUM, _item_num);
		encrypt.addLong(KeyID.KEY_TOM_KID_ITEM_GOLD_PRICE, _gold);
		return encrypt.toByteArray();
	}
	
	public String ToString() {
		return "id = " + _id + ", item type = " + _item_type + ", item id = " + _item_id + ", item num = " + _item_num + ", gold = " + _gold;
	}
	
	public int getId() {
		return _id;
	}
	
	public void setId(int id) {
		this._id = id;
	}

	public int getType() {
		return _item_type;
	}

	public void setType(int _type) {
		this._item_type = _type;
	}

	public int getItemId() {
		return _item_id;
	}

	public void setItemId(int _id) {
		this._item_id = _id;
	}

	public int getNum() {
		return _item_num;
	}

	public void setNum(int _num) {
		this._item_num = _num;
	}

	public long getGold() {
		return _gold;
	}

	public void setGold(long _gold) {
		this._gold = _gold;
	}
}