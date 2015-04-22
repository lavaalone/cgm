package com.vng.skygarden.game;

import com.vng.log.*;
import com.vng.netty.*;
import com.vng.util.*;
import java.util.*;

public class Achievement
{
	private int		_index = -1;
	private long	_total = 0;
	private boolean _received_gift_1 = false;
	private boolean _received_gift_2 = false;
	private boolean _received_gift_3 = false;
	private boolean _is_changed		 = false;
	
	public Achievement(int index)
	{
		_index = index;
		_total = 0;
		_received_gift_1 = false;
		_received_gift_2 = false;
		_received_gift_3 = false;
	}

	public Achievement(byte[] bin)
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(bin, true);
		_index = encrypt.getInt(KeyID.KEY_ACHIEVEMENT_ID);
		_total = encrypt.getLong(KeyID.KEY_ACHIEVEMENT_TOTAL);
		_received_gift_1 = encrypt.getBoolean(KeyID.KEY_ACHIEVEMENT_RECEIVED_GIFT + DatabaseID.ACHIEVEMENT_GIFT_INDEX_1);
		_received_gift_2 = encrypt.getBoolean(KeyID.KEY_ACHIEVEMENT_RECEIVED_GIFT + DatabaseID.ACHIEVEMENT_GIFT_INDEX_2);
		_received_gift_3 = encrypt.getBoolean(KeyID.KEY_ACHIEVEMENT_RECEIVED_GIFT + DatabaseID.ACHIEVEMENT_GIFT_INDEX_3);
	}
	
	public byte[] getData()
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_ACHIEVEMENT_ID, _index);
		encrypt.addLong(KeyID.KEY_ACHIEVEMENT_TOTAL, _total);
		encrypt.addBoolean(KeyID.KEY_ACHIEVEMENT_RECEIVED_GIFT + DatabaseID.ACHIEVEMENT_GIFT_INDEX_1, _received_gift_1);
		encrypt.addBoolean(KeyID.KEY_ACHIEVEMENT_RECEIVED_GIFT + DatabaseID.ACHIEVEMENT_GIFT_INDEX_2, _received_gift_2);
		encrypt.addBoolean(KeyID.KEY_ACHIEVEMENT_RECEIVED_GIFT + DatabaseID.ACHIEVEMENT_GIFT_INDEX_3, _received_gift_3);
		return encrypt.toByteArray();
	}
	
	public String ToString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("index = ").append(_index);
		sb.append(", total = ").append(_total);
		sb.append(", recieved_gift_1 = ").append(_received_gift_1);
		sb.append(", recieved_gift_1 = ").append(_received_gift_2);
		sb.append(", recieved_gift_1 = ").append(_received_gift_3);
//		sb.append(", size = ").append(getData().length);
		return sb.toString();
	}
	
	public boolean IsReceivedGift(int gift_index)
	{
		switch (gift_index)
		{
			case DatabaseID.ACHIEVEMENT_GIFT_INDEX_1:
				return _received_gift_1;
			case DatabaseID.ACHIEVEMENT_GIFT_INDEX_2:
				return _received_gift_2;
			case DatabaseID.ACHIEVEMENT_GIFT_INDEX_3:
				return _received_gift_3;
			default:
				return false;
		}
	}
	
	public void SetReceivedGift(int gift_index)
	{
		_is_changed = true;
		switch (gift_index)
		{
			case DatabaseID.ACHIEVEMENT_GIFT_INDEX_1:
				_received_gift_1 = true;
				break;
			case DatabaseID.ACHIEVEMENT_GIFT_INDEX_2:
				_received_gift_2 = true;
				break;
			case DatabaseID.ACHIEVEMENT_GIFT_INDEX_3:
				_received_gift_3 = true;
				break;
		}
	}
	
	public String GetGiftList(int acm_index, int gift_index)
	{
		switch (gift_index)
		{
			case DatabaseID.ACHIEVEMENT_GIFT_INDEX_1:
				return Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_ACHIEVEMENT][acm_index][DatabaseID.ACHIEVEMENT_GIFT_1]);
			case DatabaseID.ACHIEVEMENT_GIFT_INDEX_2:
				return Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_ACHIEVEMENT][acm_index][DatabaseID.ACHIEVEMENT_GIFT_2]);
			case DatabaseID.ACHIEVEMENT_GIFT_INDEX_3:
				return Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_ACHIEVEMENT][acm_index][DatabaseID.ACHIEVEMENT_GIFT_3]);
			default:
				return "";
		}
	}
	
	public long GetTarget(int target_index)
	{
		switch (target_index)
		{
			case DatabaseID.ACHIEVEMENT_GIFT_INDEX_1:
				return Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_ACHIEVEMENT][_index][DatabaseID.ACHIEVEMENT_TARGET_1]);
			case DatabaseID.ACHIEVEMENT_GIFT_INDEX_2:
				return Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_ACHIEVEMENT][_index][DatabaseID.ACHIEVEMENT_TARGET_2]);
			case DatabaseID.ACHIEVEMENT_GIFT_INDEX_3:
				return Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_ACHIEVEMENT][_index][DatabaseID.ACHIEVEMENT_TARGET_3]);
			default:
				return -1;
		}
	}

	public int getIndex() {
		return _index;
	}

	public void setIndex(int _index) 
	{
		_is_changed = true;
		this._index = _index;
	}

	public long getTotal() 
	{
		return _total;
	}

	public void setTotal(long _total) 
	{
		_is_changed = true;
		this._total = _total;
	}

	public boolean isReceivedGift1() 
	{
		return _received_gift_1;
	}

	public void setReceivedGift1(boolean _recieved_gift_1) 
	{
		_is_changed = true;
		this._received_gift_1 = _recieved_gift_1;
	}

	public boolean isReceivedGift2() 
	{
		return _received_gift_2;
	}

	public void setReceivedGift2(boolean _recieved_gift_2) 
	{
		_is_changed = true;
		this._received_gift_2 = _recieved_gift_2;
	}

	public boolean isReceivedGift3() 
	{
		return _received_gift_3;
	}

	public void setReceivedGift3(boolean _recieved_gift_3) 
	{
		_is_changed = true;
		this._received_gift_3 = _recieved_gift_3;
	}
	
	public boolean isChanged()
	{
		return this._is_changed;
	}
	
	public void setChanged(boolean b)
	{
		this._is_changed = b;
	}
}