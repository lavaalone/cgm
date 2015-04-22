package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import com.vng.skygarden._gen_.ProjectConfig;

import java.util.concurrent.atomic.*;
import java.util.*;

public class PrivateInfo
{
	private long		_id;
	private String		_phone_list = "null";
	
	public PrivateInfo(long uid)
	{
		this._id = uid;
	}
	
	public PrivateInfo(byte[] bin_db)
	{
		FBEncrypt user = new FBEncrypt();
		user.decode(bin_db, true);
		
		if (user.hasKey(KeyID.KEY_PHONE_NUMBER))
			_phone_list = user.getString(KeyID.KEY_PHONE_NUMBER);
	}
	
	public byte[] GetData()
	{
		FBEncrypt user = new FBEncrypt();
		user.addLong(KeyID.KEY_USER_ID, _id);
		user.addString(KeyID.KEY_PHONE_NUMBER, _phone_list);
		
		return user.toByteArray();
	}
	
	public void SetPhoneNumber(String s)
	{
		this._phone_list = _phone_list + ";" + s;
	}
	
	public String ToString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("phone_list=").append(_phone_list);
		
		return sb.toString();
	}
}