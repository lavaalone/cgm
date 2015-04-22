package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import com.vng.skygarden._gen_.ProjectConfig;

import java.util.concurrent.atomic.*;
import java.util.*;

public class CrossPromotion
{
	private int _start_time= -1;
	private int _end_time = -1;
	private boolean _is_available = false;
	private String _img = "null";
	private String _img_md5 = "null";
	private String _gift = "null";
	private String _target_link = "null";
	private String _app_info = "null";
	
	public CrossPromotion(byte[] bin_db)
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(bin_db, true);
		_start_time = encrypt.getInt(KeyID.KEY_CROSS_PROMOTION_START_TIME);
		_end_time = encrypt.getInt(KeyID.KEY_CROSS_PROMOTION_END_TIME);
		_img = encrypt.getString(KeyID.KEY_CROSS_PROMOTION_IMAGE);
		_img_md5 = encrypt.getString(KeyID.KEY_CROSS_PROMOTION_IMAGE_MD5);
		_gift = encrypt.getString(KeyID.KEY_CROSS_PROMOTION_GIFT);
		_target_link = encrypt.getString(KeyID.KEY_CROSS_PROMOTION_TARGET_LINK);
		_app_info = encrypt.getString(KeyID.KEY_CROSS_PROMOTION_APP_INFO);
	}
	
	public CrossPromotion(String data)
	{
		// sample:
		// start_time;end_time;img;md5;gift
		// time format: 20/10/2013 22:15:30
		try
		{
			String[] aos = data.split(";");
			_start_time = Misc.SECONDS(aos[0]);
			_end_time = Misc.SECONDS(aos[1]);
			_img = aos[2];
			_img_md5 = aos[3];
			_gift = aos[4];
			_target_link = aos[5];
			_app_info = aos[6];
		}
		catch (Exception e)
		{
			LogHelper.LogException("parse_cross_promotion_info", e);
		}
	}
	
	public byte[] GetData()
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_CROSS_PROMOTION_START_TIME, _start_time);
		encrypt.addInt(KeyID.KEY_CROSS_PROMOTION_END_TIME, _end_time);
		encrypt.addStringANSI(KeyID.KEY_CROSS_PROMOTION_IMAGE, _img);
		encrypt.addStringANSI(KeyID.KEY_CROSS_PROMOTION_IMAGE_MD5, _img_md5);
		encrypt.addString(KeyID.KEY_CROSS_PROMOTION_GIFT, _gift);
		
		if (Misc.SECONDS() >= _start_time && Misc.SECONDS() <= _end_time)
		{
			_is_available = true;
		}
		encrypt.addBoolean(KeyID.KEY_CROSS_PROMOTION_AVAILABLE, _is_available);
		encrypt.addStringANSI(KeyID.KEY_CROSS_PROMOTION_TARGET_LINK, _target_link);
		encrypt.addStringANSI(KeyID.KEY_CROSS_PROMOTION_APP_INFO, _app_info);
		
		return encrypt.toByteArray();
	}
	
	public boolean isAvailable()
	{
		return _is_available;
	}
	
	public String GetGiftList()
	{
		return _gift;
	}
}