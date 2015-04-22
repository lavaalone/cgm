/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vng.skygarden.game;

import com.vng.db.DBKeyValue;
import com.vng.log.LogHelper;
import com.vng.util.FBEncrypt;
import com.vng.util.Misc;
import java.util.LinkedList;

/**
 *
 * @author thinhnn3
 */
public class MerchantManager {
	private DBKeyValue _db = null;
	
	private String	_uid = "";
	
	public LinkedList<Merchant> _merchants;

	public MerchantManager(String uid) {
		_uid = uid;
		_merchants = new LinkedList<Merchant>();
	}
	
	public void SetDatabase(DBKeyValue db) {
		_db = db;
	}
	
	public void SetUserId(String uid) {
		_uid = uid;
	}
	
	public boolean AddNewMerchant() { 
		if (_merchants.size() > DatabaseID.MERCHANT_MAX_CONCURRENT) {
			LogHelper.Log("MerchantManager.. AddNewMerchant failed! Reach max concurrent merchant");
			return false;
		}
		
		// determine merchant type
		int merchant_id = DatabaseID.MERCHANT_RED_HOOD;
		if (_merchants.size() > 0) {
			switch (_merchants.getLast().GetMerchantID()) {
				case DatabaseID.MERCHANT_RED_HOOD:
					merchant_id = DatabaseID.MERCHANT_PUSS_IN_BOOST;
//					LogHelper.Log("MerchantManager.. new merchant: MERCHANT_PUSS_IN_BOOST");
					break;
				case DatabaseID.MERCHANT_PUSS_IN_BOOST:
					merchant_id = DatabaseID.MERCHANT_GROUCHY;
//					LogHelper.Log("MerchantManager.. new merchant: MERCHANT_GROUCHY");
					break;
				case DatabaseID.MERCHANT_GROUCHY:
					merchant_id = DatabaseID.MERCHANT_RED_HOOD;
//					LogHelper.Log("MerchantManager.. new merchant: MERCHANT_RED_HOOD");
					break;
			}
		} else {
//			LogHelper.Log("MerchantManager.. default new merchant: MERCHANT_RED_HOOD");
		}
		
		Merchant m = new Merchant(merchant_id);
		return _merchants.add(m);
	}
	
	public boolean SetMerchantDetail(int merchant_id, int item_type, int item_id, int item_num, int money_type, int money_id, long money_num, int appear_time) {
		if (item_num <= 0) {
			LogHelper.Log("SetMerchantDetail.. SetMerchantDetail failed! requested item num should be > 0");
			return false;
		}
		
		for (Merchant m : _merchants) {
			if (m.GetMerchantID() == merchant_id) {
				m.SetRequestItem(item_type, item_id, item_num);
				m.SetPrice(money_type, money_id, money_num);
				m.SetAppearTime(appear_time);
//				LogHelper.Log("SetMerchantDetail.. SetMerchantDetail done!");
				return true;
			}
		}
		
		return false;
	}
	
	public boolean LoadFromDatabase(String key) {
		_merchants.clear();
		
		try {
			byte[] bin = _db.GetRaw(_uid + "_" + key);

			if (bin == null || bin.length == 0) {
				LogHelper.Log("MerchantManager.. merchant list is empty.");
				return true;
			}
			
			FBEncrypt fb = new FBEncrypt();
			fb.decode(bin, true);
			
			int i = 0;
			while (fb.hasKey(KeyID.KEY_MERCHANT_LIST + i)) {
				String[] merchant_info = fb.getString(KeyID.KEY_MERCHANT_LIST + i).split(";");
				if (merchant_info.length != 9) {
//					LogHelper.Log("MerchantManager.. err! invalid merchant info");
					return false;
				}
				
				Merchant m = new Merchant(Integer.parseInt(merchant_info[0]));
				m.SetState(Integer.parseInt(merchant_info[1]) == 1 ? true : false);
				m.SetAppearTime(Integer.parseInt(merchant_info[2]));
				m.SetRequestItem(Integer.parseInt(merchant_info[3]), Integer.parseInt(merchant_info[4]), Integer.parseInt(merchant_info[5]));
				m.SetPrice(Integer.parseInt(merchant_info[6]), Integer.parseInt(merchant_info[7]), Integer.parseInt(merchant_info[8]));
				_merchants.add(m);
				
				i++;
			}
			
//			LogHelper.Log("MerchantManager.. load from database OK with size = " + _merchants.size());
			
			return true;
		} catch (Exception e) {
			LogHelper.LogException("MerchantManager.LoadFromDatabase", e);
		}
		
		return false;
	}
	
	public boolean SaveDataToDatabase(String key) {
		if (_merchants.size() == 0) {
//			LogHelper.Log("MerchantManager.. merchant list contains zero object");
			_db.Delete(_uid + "_" + key);
			return true;
		}
		
		FBEncrypt data = new FBEncrypt();
		for (Merchant m : _merchants) {
			data.addString(KeyID.KEY_MERCHANT_LIST + _merchants.indexOf(m), m.GetInfo());
		}
		
		return _db.SetRaw(_uid + "_" + key, data.toByteArray());
	}
	
	public LinkedList<Merchant> GetMerchantList() {
		return _merchants;
	}
	
	public int GetMerchantSize() {
		return _merchants.size();
	}
	
	public Merchant GetFirstMerchant() {
		return _merchants.getFirst();
	}
	
	public Merchant GetLastMerchant() {
		return _merchants.getLast();
	}
	
	public void RemoveMerchant(int merchant_id) {
		for (Merchant m : _merchants) {
			if (m.GetMerchantID() == merchant_id) {
				_merchants.remove(m);
				LogHelper.Log("MerchantManager.. removed merchant done!");
				break;
			}
		}
	}
}

class Merchant {
	int _merchant_id;
	
	int _request_item_type;
	int _request_item_id;
	int _request_item_num;
	
	int _money_type;
	int _money_id;
	long _money_num;
	
	int _appear_time;
	boolean _state;

	public Merchant(int id) {
		_merchant_id = id;
		
		_request_item_type = -1;
		_request_item_id = -1;
		_request_item_num = -1;
		
		_money_type = DatabaseID.IT_MONEY;
		_money_id = DatabaseID.GOLD_ID;
		_money_num = 0;
		
		_appear_time = Misc.SECONDS();
	}
	
	public int GetMerchantID() {
		return _merchant_id;
	}
	
	public void SetRequestItem(int type, int id, int num) {
		_request_item_type = type;
		_request_item_id = id;
		_request_item_num = num;
	}
	
	public int GetRequestItemType() {
		return _request_item_type;
	}
	
	public int GetRequestItemID() {
		return _request_item_id;
	}
	
	public int GetRequestItemNum() {
		return _request_item_num;
	}
	
	public void SetPrice(int money_type, int money_id, long money_num) {
		_money_type = money_type;
		_money_id = money_id;
		_money_num = money_num;
	}
	
	public int GetMoneyType() {
		return _money_type;
	}
	
	public int GetMoneyID() {
		return _money_id;
	}
	
	public long GetMoneyNum() {
		return _money_num;
	}
	
	public void SetAppearTime(int second) {
		_appear_time = second;
	}
	
	public int GetAppearTime() {
		return _appear_time;
	}
	
	public void SetState(boolean b) {
		_state = b;
	}
	
	public boolean GetState() {
		return _state;
	}
	
	public String GetInfo()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(_merchant_id);
		sb.append(';').append(_state ? 1 : 0);
		sb.append(';').append(_appear_time);
		sb.append(';').append(_request_item_type);
		sb.append(';').append(_request_item_id);
		sb.append(';').append(_request_item_num);
		sb.append(';').append(_money_type);
		sb.append(';').append(_money_id);
		sb.append(';').append(_money_num);
		
		return sb.toString();
	}
}
