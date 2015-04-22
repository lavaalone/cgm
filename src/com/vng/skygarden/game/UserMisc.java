package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import com.vng.skygarden.DBConnector;
import com.vng.skygarden._gen_.ProjectConfig;

import java.util.concurrent.atomic.*;
import java.util.*;
import java.util.Map.Entry;

public class UserMisc
{
	private long _user_id;
	private FBEncrypt _encrypt;
	
	public UserMisc(long user_id)
	{
		this._user_id = user_id;
		_encrypt = new FBEncrypt();
	}
	
	public UserMisc(long user_id, byte[] data)
	{
		this._user_id = user_id;
		_encrypt = new FBEncrypt();
		_encrypt.decode(data, true);
	}
	
	public void Set(String key, String value)
	{
		if (_encrypt.hasKey(key))
		{
			_encrypt.updateValue(key, value, true);
		}
		else
		{
			_encrypt.addString(key, value);
		}
		
		DBConnector.GetMembaseServer(_user_id).Set(_user_id + "_" + "misc", _encrypt.toByteArray());
	}
	
	public String Get(String key)
	{
		if (_encrypt.hasKey(key))
			return _encrypt.getString(key);
		
		return "";
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		HashMap<Integer, Object> hm = _encrypt.getHashMap();
		for (Entry<Integer, Object> e : hm.entrySet())
		{
			sb.append("key := " ).append(e.getKey()).append(", value := ").append(e.getValue()).append("\n");
		}
		return sb.toString();
	}
}