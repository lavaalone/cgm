/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vng.skygarden.game;

import com.google.common.base.Strings;
import com.vng.log.LogHelper;
import com.vng.netty.Server;
import com.vng.util.FBEncrypt;
import com.vng.util.Misc;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author MacBook
 */
public class ComboManager {
	// next task: 1. save & load this instance, 2. integrate with others features
	private ConcurrentHashMap<Integer, String> _floors_combo = new ConcurrentHashMap<>(); // floor id, combo id as string, 1;2;3...
	private boolean _order_bonus = false;
	private int _order_bonus_percent = 0;
	
	public ComboManager()
	{
	}
	
	public ComboManager(byte[] data)
	{
		_floors_combo.clear();
		
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(data, true);
		
		String[] sa = encrypt.getString(KeyID.KEY_COMBO).split(";");
		for (String s : sa)
		{
			if (!Strings.isNullOrEmpty(s)) {
				_floors_combo.put(Integer.parseInt(s), encrypt.getString(KeyID.KEY_COMBO + "_" + s));
				LogHelper.LogHappy("Load combo, floor := " + s + ", combo := " + encrypt.getString(KeyID.KEY_COMBO + "_" + s));
			}
		}
	}
	
	public byte[] GetData()
	{
		FBEncrypt encrypt = new FBEncrypt();
		for (Map.Entry<Integer, String> e : _floors_combo.entrySet())
		{
			encrypt.addString(KeyID.KEY_COMBO + "_" + e.getKey(), e.getValue());
		}
		
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Integer, String> e : _floors_combo.entrySet())
		{
			if (sb.length() > 0) {
				sb.append(";").append(e.getKey());
			} else {
				sb.append(e.getKey());
			}
		}
		encrypt.addString(KeyID.KEY_COMBO, sb.toString());
		
		return encrypt.toByteArray();
	}
	
	public boolean computeCombo(Floor floor)
	{
		StringBuilder combo = new StringBuilder();
		LinkedList<Integer> decors = new LinkedList<>();
		// get total decor on floors
		for (int i = 0; i < DatabaseID.MAX_SLOT_PER_FLOOR; i++)
		{
			if (floor.slot[i].GetDecor().getID() > -1) {
				decors.add(floor.slot[i].GetDecor().getID());
			}
		}
		LinkedList<Integer> clone = (LinkedList)decors.clone();
		LogHelper.LogHappy("decors floor " + floor.getID() + " := " + decors);
		
		for (int k = 0; k < DatabaseID.MAX_SLOT_PER_FLOOR; k++)
		{
			for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_COMBO].length; i++)
			{
				int cb_id = (int) Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_COMBO][i][DatabaseID.COMBO_ID]);
				String[] cb_require = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_COMBO][i][DatabaseID.COMBO_REQUIRE]).split(":");
				
				// compute combo 6 later
				if (cb_require.length == 12)
					break;
				
				LogHelper.LogHappy("combo require: " + Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_COMBO][i][DatabaseID.COMBO_REQUIRE]));

				// check if floor decors meet the combo require
				boolean meet = true;
				for (int j = 0; j < cb_require.length - 1 && meet; j+=2)
				{
					if (!decors.contains(Integer.parseInt(cb_require[j + 1]))) {
						meet = false;
					}
				}

				if (meet)
				{
					// remove from decors list
					for (int j = 0; j < cb_require.length - 1; j+=2) {
						decors.remove(decors.indexOf(Integer.parseInt(cb_require[j + 1])));
					}

					if (combo.length() > 0) {
						combo.append(";").append(cb_id);
					} else {
						combo.append(cb_id);
					}
				}
			}
			
			if (decors.size() <= 1) {
				break;
			}
		}
		
		// compute combo 6
		LogHelper.LogHappy("clone decors floor " + floor.getID() + " := " + clone);
		for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_COMBO].length; i++)
		{
			int cb_id = (int) Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_COMBO][i][DatabaseID.COMBO_ID]);
			String[] cb_require = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_COMBO][i][DatabaseID.COMBO_REQUIRE]).split(":");

			// compute combo 6 later
			if (cb_require.length == 12)
			{
				LogHelper.LogHappy("combo 6 require: " + Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_COMBO][i][DatabaseID.COMBO_REQUIRE]));
				
				// check if floor decors meet the combo require
				boolean meet = true;
				for (int j = 0; j < cb_require.length - 1 && meet; j+=2)
				{
					if (!clone.contains(Integer.parseInt(cb_require[j + 1]))) {
						meet = false;
					}
				}

				if (meet)
				{
					if (combo.length() > 0) {
						combo.append(";").append(cb_id);
					} else {
						combo.append(cb_id);
					}
				}
			}
		}
		
		_floors_combo.put((int)floor.getID(), combo.toString());
		LogHelper.LogHappy("combo floor " + floor.getID() + " := " + combo.toString());
		return combo.length() > 0;
	}
	
	public String GetFloorCombo(int floor_id)
	{
		if (_floors_combo.containsKey(floor_id)) {
			return _floors_combo.get(floor_id);
		}
		return "";
	}
	
	public int GetComboBonus(int combo_id, int bonus_type)
	{
		return (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_COMBO][combo_id][bonus_type]);
	}
	
	public int GetAirshipBonusGold()
	{
		int result = 0;
		for (Map.Entry<Integer, String> e : _floors_combo.entrySet())
		{
			String[] sa = e.getValue().split(";");
			for (String s : sa){
				if (!Strings.isNullOrEmpty(s))
				{
					result += GetComboBonus(Integer.parseInt(s), DatabaseID.COMBO_BONUS_AIRSHIP_GOLD);
				}
			}
		}
		LogHelper.LogHappy("Airship Bonus Gold := " + result);
		return result;
	}
	
	public int GetAirshipBonusExp()
	{
		int result = 0;
		for (Map.Entry<Integer, String> e : _floors_combo.entrySet())
		{
			String[] sa = e.getValue().split(";");
			for (String s : sa){
				if (!Strings.isNullOrEmpty(s))
				{
					result += GetComboBonus(Integer.parseInt(s), DatabaseID.COMBO_BONUS_AIRSHIP_EXP);
				}
			}
		}
		LogHelper.LogHappy("Airship Bonus Exp := " + result);
		return result;
	}
	
	public int GetBonusDailyOrder()
	{
		int result = 0;
		for (Map.Entry<Integer, String> e : _floors_combo.entrySet())
		{
			String[] sa = e.getValue().split(";");
			for (String s : sa){
				if (!Strings.isNullOrEmpty(s))
				{
					result += GetComboBonus(Integer.parseInt(s), DatabaseID.COMBO_BONUS_ORDER_DAILY);
				}
			}
		}
		LogHelper.LogHappy("Order Daily Bonus Exp := " + result);
		
		if (_order_bonus) {
			result += _order_bonus_percent;
			LogHelper.LogHappy("Order Normal Bonus Exp := " + result);
		}
		
		return result;
	}
	
	public int GetBonusNormalOrder()
	{
		int result = 0;
		for (Map.Entry<Integer, String> e : _floors_combo.entrySet())
		{
			String[] sa = e.getValue().split(";");
			for (String s : sa){
				if (!Strings.isNullOrEmpty(s))
				{
					result += GetComboBonus(Integer.parseInt(s), DatabaseID.COMBO_BONUS_ORDER_NORMAL);
				}
			}
		}
		LogHelper.LogHappy("Order Normal Bonus Exp := " + result);
		
		if (_order_bonus) {
			result += _order_bonus_percent;
			LogHelper.LogHappy("Order Normal Bonus Exp := " + result);
		}
		
		return result;
	}

	public boolean orderBonus() {
		return _order_bonus;
	}

	public void setOrderBonus(boolean _has_order_bonus) {
		this._order_bonus = _has_order_bonus;
	}

	public int getOrderBonusPercent() {
		return _order_bonus_percent;
	}

	public void setOrderBonusPercent(int _order_bonus_percent) {
		this._order_bonus_percent = _order_bonus_percent;
		LogHelper.LogHappy("set _order_bonus_percent := " + _order_bonus_percent);
	}
}

class Combo {
	private int _id;
	private int _bonus_plant_exp;
	private int _bonus_plant_time;
	private int _bonus_bug;
	private int _bonus_airship_gold;
	private int _bonus_airship_exp;
	private int _bonus_order_normal;
	private int _bonus_order_daily;
	
	public Combo()
	{	
	}
	
	public Combo(byte[] data)
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(data, true);
		
		_id = encrypt.getInt(KeyID.KEY_ID);
		_bonus_plant_exp = encrypt.getInt(KeyID.KEY_BONUS_PLANT_EXP);
		_bonus_plant_time = encrypt.getInt(KeyID.KEY_BONUS_PLANT_TIME);
		_bonus_bug = encrypt.getInt(KeyID.KEY_BONUS_BUG);
		_bonus_airship_gold = encrypt.getInt(KeyID.KEY_BONUS_AIRSHIP_GOLD);
		_bonus_airship_exp = encrypt.getInt(KeyID.KEY_BONUS_AIRSHIP_EXP);
		_bonus_order_normal = encrypt.getInt(KeyID.KEY_BONUS_ORDER_NORMAL);
		_bonus_order_daily = encrypt.getInt(KeyID.KEY_BONUS_ORDER_DAILY);
	}
	
	public byte[] getData()
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_ID, _id);
		encrypt.addInt(KeyID.KEY_BONUS_PLANT_EXP, _bonus_plant_exp);
		encrypt.addInt(KeyID.KEY_BONUS_PLANT_TIME, _bonus_plant_time);
		encrypt.addInt(KeyID.KEY_BONUS_BUG, _bonus_bug);
		encrypt.addInt(KeyID.KEY_BONUS_AIRSHIP_GOLD, _bonus_airship_gold);
		encrypt.addInt(KeyID.KEY_BONUS_AIRSHIP_EXP, _bonus_airship_exp);
		encrypt.addInt(KeyID.KEY_BONUS_ORDER_NORMAL, _bonus_order_normal);
		encrypt.addInt(KeyID.KEY_BONUS_ORDER_DAILY, _bonus_order_daily);
		return encrypt.toByteArray();
	}

	public int getId() {
		return _id;
	}

	public void setId(int _id) {
		this._id = _id;
	}

	public int getBonusPlantExp() {
		return _bonus_plant_exp;
	}

	public void setBonusPlantExp(int _bonus_plant_exp) {
		this._bonus_plant_exp = _bonus_plant_exp;
	}

	public int getBonusPlantTime() {
		return _bonus_plant_time;
	}

	public void setBonusPlantTime(int _bonus_plant_time) {
		this._bonus_plant_time = _bonus_plant_time;
	}

	public int getBonusBug() {
		return _bonus_bug;
	}

	public void setBonusBug(int _bonus_bug) {
		this._bonus_bug = _bonus_bug;
	}

	public int getBonusAirshipGold() {
		return _bonus_airship_gold;
	}

	public void setBonusAirshipGold(int _bonus_airship) {
		this._bonus_airship_gold = _bonus_airship;
	}

	public int getBonusOrderNormal() {
		return _bonus_order_normal;
	}

	public void setBonusOrderNormal(int _bonus_order) {
		this._bonus_order_normal = _bonus_order;
	}

	public int getBonusAirshipExp() {
		return _bonus_airship_exp;
	}

	public void setBonusAirshipExp(int _bonus_airship_exp) {
		this._bonus_airship_exp = _bonus_airship_exp;
	}

	public int getBonusOrderDaily() {
		return _bonus_order_daily;
	}

	public void setBonusOrderDaily(int bonus) {
		this._bonus_order_daily = bonus;
	}
}
