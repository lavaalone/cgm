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

/**
 *
 * @author CPU01857-local
 */
public class TreasureTrunk {
	private int _num_bronze;
	private int _num_silver;
	private int _num_gold;
	private int _num_bronze_key;
	private int _num_silver_key;
	private int _num_gold_key;
	private int _count_bronze;
	private int _count_silver;
	private int _count_gold;
	private String _gift_bronze;
	private String _gift_silver;
	private String _gift_gold;
	
	public TreasureTrunk() {
		_count_bronze		= -1;
		_count_silver		= -1;
		_count_gold			= -1;
	}
	
	public TreasureTrunk(byte[] data) {
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(data, true);
		
		_num_bronze			= encrypt.getInt(KeyID.KEY_NUM_BRONZE);
		_num_silver			= encrypt.getInt(KeyID.KEY_NUM_SILVER);
		_num_gold			= encrypt.getInt(KeyID.KEY_NUM_GOLD);
		_num_bronze_key		= encrypt.getInt(KeyID.KEY_NUM_BRONZE_KEY);
		_num_silver_key		= encrypt.getInt(KeyID.KEY_NUM_SILVER_KEY);
		_num_gold_key		= encrypt.getInt(KeyID.KEY_NUM_GOLD_KEY);
		_count_bronze		= encrypt.getInt(KeyID.KEY_COUNT_BRONZE);
		_count_silver		= encrypt.getInt(KeyID.KEY_COUNT_SILVER);
		_count_gold			= encrypt.getInt(KeyID.KEY_COUNT_GOLD);
	}
	
	public byte[] GetData() {
		LogHelper.LogHappy(ToString());
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_NUM_BRONZE, _num_bronze);
		encrypt.addInt(KeyID.KEY_NUM_SILVER, _num_silver);
		encrypt.addInt(KeyID.KEY_NUM_GOLD, _num_gold);
		encrypt.addInt(KeyID.KEY_NUM_BRONZE_KEY, _num_bronze_key);
		encrypt.addInt(KeyID.KEY_NUM_SILVER_KEY, _num_silver_key);
		encrypt.addInt(KeyID.KEY_NUM_GOLD_KEY, _num_gold_key);
		encrypt.addInt(KeyID.KEY_COUNT_BRONZE, _count_bronze);
		encrypt.addInt(KeyID.KEY_COUNT_SILVER, _count_silver);
		encrypt.addInt(KeyID.KEY_COUNT_GOLD, _count_gold);
		encrypt.addString(KeyID.KEY_GIFT_BRONZE, GetGift(DatabaseID.MATERIAL_ITEM_TRUNK_BRONZE));
		encrypt.addString(KeyID.KEY_GIFT_SILVER, GetGift(DatabaseID.MATERIAL_ITEM_TRUNK_SILVER));
		encrypt.addString(KeyID.KEY_GIFT_GOLD, GetGift(DatabaseID.MATERIAL_ITEM_TRUNK_GOLD));
		return encrypt.toByteArray();
	}
	
	public boolean UseTrunk(int id) {
		LogHelper.LogHappy(ToString());
		switch (id) {
			case DatabaseID.MATERIAL_ITEM_TRUNK_BRONZE:
				if (_num_bronze > 0 && _num_bronze_key > 0) {
					_num_bronze--;
					_num_bronze_key--;
					return true;
				}
				break;
			case DatabaseID.MATERIAL_ITEM_TRUNK_SILVER:
				if (_num_silver > 0 && _num_silver_key > 0) {
					_num_silver--;
					_num_silver_key--;
					return true;
				}
				break;
			case DatabaseID.MATERIAL_ITEM_TRUNK_GOLD:
				if (_num_gold > 0 && _num_gold_key > 0) {
					_num_gold--;
					_num_gold_key--;
					return true;
				}
				break;
		}
		return false;
	}
	
	public String ToString() {
		StringBuilder sb = new StringBuilder();
		sb.append("_num_bronze=").append(_num_bronze);
		sb.append("&_num_silver=").append(_num_silver);
		sb.append("&_num_gold=").append(_num_gold);
		sb.append("&_num_bronze_key=").append(_num_bronze_key);
		sb.append("&_num_silver_key=").append(_num_silver_key);
		sb.append("&_num_gold_key=").append(_num_gold_key);
		sb.append("&_count_bronze=").append(_count_bronze);
		sb.append("&_count_silver=").append(_count_silver);
		sb.append("&_count_gold=").append(_count_gold);
		return sb.toString();
	}
	
	public int IncreaseCount(int id) {
		switch (id) {
			case DatabaseID.MATERIAL_ITEM_TRUNK_BRONZE:
				_count_bronze++;
				return _count_bronze;
			case DatabaseID.MATERIAL_ITEM_TRUNK_SILVER:
				_count_silver++;
				return _count_silver;
			case DatabaseID.MATERIAL_ITEM_TRUNK_GOLD:
				_count_gold++;
				return _count_gold;
			default:
				return -1;
		}
	}
	
	public void ResetCount(int id) {
		switch (id) {
			case DatabaseID.MATERIAL_ITEM_TRUNK_BRONZE:
				_count_bronze = 0;
				break;
			case DatabaseID.MATERIAL_ITEM_TRUNK_SILVER:
				_count_silver = 0;
				break;
			case DatabaseID.MATERIAL_ITEM_TRUNK_GOLD:
				_count_gold = 0;
				break;
			default:
				break;
		}
	}
	
	public void IncreaseItem(int id, int num_inc) {
		switch (id) {
			case DatabaseID.MATERIAL_ITEM_TRUNK_BRONZE:
				_num_bronze += num_inc;
				break;
			case DatabaseID.MATERIAL_ITEM_TRUNK_SILVER:
				_num_silver += num_inc;
				break;
			case DatabaseID.MATERIAL_ITEM_TRUNK_GOLD:
				_num_gold += num_inc;
				break;
			case DatabaseID.MATERIAL_ITEM_KEY_BRONZE:
				_num_bronze_key += num_inc;
				break;
			case DatabaseID.MATERIAL_ITEM_KEY_SILVER:
				_num_silver_key += num_inc;
				break;
			case DatabaseID.MATERIAL_ITEM_KEY_GOLD:
				_num_gold_key += num_inc;
				break;
			default:
				break;
		}
	}
	
	public String GetGift(int id) {
		switch (id) {
			case DatabaseID.MATERIAL_ITEM_TRUNK_BRONZE:
				if (Strings.isNullOrEmpty(_gift_bronze)) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i <= 7; i++) {
						String s = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_TREASURE_TRUNK][i][DatabaseID.TREASURE_TRUNK_ITEM]);
						if (!s.equals("null")) {
							if (sb.length() == 0) {
								sb.append(s);
							} else {
								sb.append(";").append(s);
							}
						}
					}
					_gift_bronze = sb.toString();
					LogHelper.LogHappy("_gift_bronze := " + _gift_bronze);
				}
				return _gift_bronze;
			case DatabaseID.MATERIAL_ITEM_TRUNK_SILVER:
				if (Strings.isNullOrEmpty(_gift_silver)) {
					StringBuilder sb = new StringBuilder();
					for (int i = 8; i <= 15; i++) {
						String s = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_TREASURE_TRUNK][i][DatabaseID.TREASURE_TRUNK_ITEM]);
						if (!s.equals("null")) {
							if (sb.length() == 0) {
								sb.append(s);
							} else {
								sb.append(";").append(s);
							}
						}
					}
					_gift_silver = sb.toString();
					LogHelper.LogHappy("_gift_silver := " + _gift_silver);
				}
				return _gift_silver;
			case DatabaseID.MATERIAL_ITEM_TRUNK_GOLD:
				if (Strings.isNullOrEmpty(_gift_gold)) {
					StringBuilder sb = new StringBuilder();
					for (int i = 16; i <= 23; i++) {
						String s = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_TREASURE_TRUNK][i][DatabaseID.TREASURE_TRUNK_ITEM]);
						if (!s.equals("null")) {
							if (sb.length() == 0) {
								sb.append(s);
							} else {
								sb.append(";").append(s);
							}
						}
					}
					_gift_gold = sb.toString();
					LogHelper.LogHappy("_gift_gold := " + _gift_gold);
				}
				return _gift_gold;
			default:
				return "";
		}
	}

	public int getNumBronze() {
		return _num_bronze;
	}

	public void setNumBronze(int _num_bronze) {
		this._num_bronze = _num_bronze;
	}

	public int getNumSilver() {
		return _num_silver;
	}

	public void setNumSilver(int _num_silver) {
		this._num_silver = _num_silver;
	}

	public int getNumGold() {
		return _num_gold;
	}

	public void setNumGold(int _num_gold) {
		this._num_gold = _num_gold;
	}

	public int getNumBronzeKey() {
		return _num_bronze_key;
	}

	public void setNumBronzeKey(int _num_bronze_key) {
		this._num_bronze_key = _num_bronze_key;
	}

	public int getNumSilverKey() {
		return _num_silver_key;
	}

	public void setNumSilverKey(int _num_silver_key) {
		this._num_silver_key = _num_silver_key;
	}

	public int getNumGoldKey() {
		return _num_gold_key;
	}

	public void setNumGoldKey(int _num_gold_key) {
		this._num_gold_key = _num_gold_key;
	}

	public int getCountBronze() {
		return _count_bronze;
	}

	public void setCountBronze(int _count_bronze) {
		this._count_bronze = _count_bronze;
	}

	public int getCountSilver() {
		return _count_silver;
	}

	public void setCountSilver(int _count_silver) {
		this._count_silver = _count_silver;
	}

	public int getCountGold() {
		return _count_gold;
	}

	public void setCountGold(int _count_gold) {
		this._count_gold = _count_gold;
	}
}
