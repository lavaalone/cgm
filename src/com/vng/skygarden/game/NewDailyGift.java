/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;

import com.google.common.base.Strings;
import com.vng.log.LogHelper;
import com.vng.skygarden.DBConnector;
import com.vng.util.FBEncrypt;
import com.vng.util.Misc;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author CPU01857-local
 */
public class NewDailyGift {
	private int _max = 24;
	private HashMap<Integer, Day> _days = new HashMap<>();
	private int _attend_count = 0;
	private int _next_reset_time = 0;
	
	public NewDailyGift() {
	}
	
	public boolean Load(byte[] data)
	{
		_days.clear();
		
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(data, true);
		
		_max			= encrypt.getInt(KeyID.KEY_MAX);
		_attend_count	= encrypt.getInt(KeyID.KEY_ATTENT_COUNT);
		_next_reset_time = encrypt.getInt(KeyID.KEY_NEXT_RESET_TIME);
		
		for (int i = 0; i < _max; i++)
		{
			byte[] ba = encrypt.getBinary(KeyID.KEY_DAY + i);
			if (ba == null) {
				LogHelper.LogHappy("Can not load day " + i);
				return false;
			}
			
			Day d = new Day(i);
			if (!d.Load(ba)) {
				return false;
			} else {
				_days.put(i, d);
				LogHelper.LogHappy("Loaded day := " + d.ToString());
			}
		}
		
//		if (_attend_count >= _max) {
//			_attend_count = _max - 1;
//		}
		
		return true;
	}
	
	public byte[] GetData() {
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_MAX, _max);
		encrypt.addInt(KeyID.KEY_ATTENT_COUNT, _attend_count);
		encrypt.addInt(KeyID.KEY_NEXT_RESET_TIME, _next_reset_time);
		
		for (Map.Entry<Integer, Day> e : _days.entrySet()) {
			encrypt.addBinary(KeyID.KEY_DAY + e.getKey(), e.getValue().GetData());
		}
		
		return encrypt.toByteArray();
	}
	
	public boolean init() {
		_days.clear();
		_attend_count = -1;
		
		try {
			List<String> keys = new ArrayList<String>();
			for (int i = 0; i <_max; i++) {
				String key = "daily_gift" + "_" + i;
				keys.add(key);
			}
			
			Map<String, Object> data = DBConnector.GetMembaseServerForTemporaryData().GetMulti(keys);
			for (int i = 0; i < _max; i++) {
				String key = "daily_gift" + "_" + i;
				if (data.containsKey(key)) {
					Day d = new Day(i);
					String value = (String)data.get(key);
					if (d.Init(value)) {
						_days.put(i, d);
						LogHelper.LogHappy("Init day := " + d.ToString());
					}
				}
			}
			
			// set next reset time
			_next_reset_time = Misc.SECONDS() + 30*24*60*60; // reset in the next 30 days.
			
			return true;
		} catch (Exception e) {
			LogHelper.LogException("initNewDailyGift", e);
			return false;
		}
	}
	
	public HashMap<Integer, Day> Days() {
		return _days;
	}
	
	public Day GetDay(int i) {
		if (_days.containsKey(i)) {
			return _days.get(i);
		}
		return null;
	}
	
	public int getAttendCount() {
		return _attend_count;
	}
	
	public void increaseAttendCount() {
		if (_attend_count < _max)
			this._attend_count++;
	}

	public int getNextResetTime() {
		return _next_reset_time;
	}

	public void setNextResetTime(int _next_reset_time) {
		this._next_reset_time = _next_reset_time;
	}
	
	public int getAttendMax() {
		return this._max;
	}
}

class Day {
	private int		_id;
	private int		_diamond;
	private String	_gift;
	private boolean	_attend;
	private boolean _is_special;
	
	public Day(int id)
	{
		this._id = id;
	}
	
	public boolean Load(byte[] ba)
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(ba, true);
		_id			= encrypt.getInt(KeyID.KEY_ID);
		_diamond	= encrypt.getInt(KeyID.KEY_DIAMOND);
		_gift		= encrypt.getString(KeyID.KEY_GIFT);
		_attend		= encrypt.getBoolean(KeyID.KEY_ATTEND);
		_is_special	= encrypt.getBoolean(KeyID.KEY_IS_SPECIAL);
		return true;
	}
	
	public boolean Init(String s)
	{
		String[] sa = s.split(";");
		_diamond	= Integer.parseInt(sa[0]);
		_gift		= sa[1];		
		_attend		= false;
		
		if (_id > 0 && (_id + 1) % 6 == 0)
			_is_special = true;
		else
			_is_special = false;
		
		return true;
	}

	public int getId() {
		return _id;
	}

	public void setId(int _id) {
		this._id = _id;
	}

	public int getDiamond() {
		return _diamond;
	}

	public void setDiamond(int _diamond) {
		this._diamond = _diamond;
	}

	public boolean isAttend() {
		return _attend;
	}

	public void setAttend(boolean _attend) {
		this._attend = _attend;
	}
	public String getGift() {
		return _gift;
	}

	public void setGift(String _gift) {
		this._gift = _gift;
	}

	public boolean isIsSpecial() {
		return _is_special;
	}

	public void setIsSpecial(boolean _is_special) {
		this._is_special = _is_special;
	}
	public byte[] GetData()
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_ID, _id);
		encrypt.addString(KeyID.KEY_GIFT, _gift);
		encrypt.addInt(KeyID.KEY_DIAMOND, _diamond);
		encrypt.addBoolean(KeyID.KEY_ATTEND, _attend);
		encrypt.addBoolean(KeyID.KEY_IS_SPECIAL, _is_special);
		return encrypt.toByteArray();
	}
	
	public String ToString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("_id=").append(_id);
		sb.append("&_diamond=").append(_diamond);
		sb.append("&_gift=").append(_gift);
		sb.append("&_attend=").append(_attend);
		sb.append("&_is_special=").append(_is_special);
		return sb.toString();
	}
}
