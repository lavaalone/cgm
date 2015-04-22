package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;

import java.util.*;

public class GiftCode
{
	private String id;
	private String server_code;
	private String client_code;
	private String gift_code;
	private String name;
	private String description;
	private String gift;
	private int start_time;
	private int end_time;
	private int use_time;
	private boolean received;
	
	public GiftCode(String _id, String _server_code, String _client_code, String _gift_code, String _name, String _description, String _gift, int _start_time, int _end_time, int _use_time)
	{
		id = _id;
		server_code = _server_code;
		client_code = _client_code;
		gift_code = _gift_code;
		name = _name;
		description = _description;
		gift = _gift;
		start_time = _start_time;
		end_time = _end_time;
		use_time = _use_time;
		
		received = false;
	}

	public GiftCode(byte[] gift_code_bin)
	{
		FBEncrypt gift_code_enc = new FBEncrypt(gift_code_bin);
		
		id = gift_code_enc.getString(KeyID.KEY_GIFT_CODE_ID);
		server_code = gift_code_enc.getString(KeyID.KEY_GIFT_CODE_SERVER_CODE);
		client_code = gift_code_enc.getString(KeyID.KEY_GIFT_CODE_CLIENT_CODE);
		gift_code = gift_code_enc.getString(KeyID.KEY_GIFT_CODE_GIFT_CODE);
		name = gift_code_enc.getString(KeyID.KEY_GIFT_CODE_NAME);
		description = gift_code_enc.getString(KeyID.KEY_GIFT_CODE_DESCRIPTION);
		gift = gift_code_enc.getString(KeyID.KEY_GIFT_CODE_GIFT);
		start_time = gift_code_enc.getInt(KeyID.KEY_GIFT_CODE_START_TIME);
		end_time = gift_code_enc.getInt(KeyID.KEY_GIFT_CODE_END_TIME);
		use_time = gift_code_enc.getInt(KeyID.KEY_GIFT_CODE_USE_TIME);
		
		if (gift_code_enc.hasKey(KeyID.KEY_GIFT_CODE_RECEIVED)) received = gift_code_enc.getBoolean(KeyID.KEY_GIFT_CODE_RECEIVED);
	}
	
	public byte[] getData()
	{
		FBEncrypt gift_code_enc = new FBEncrypt();
		
		gift_code_enc.addString(KeyID.KEY_GIFT_CODE_ID, id);
		gift_code_enc.addString(KeyID.KEY_GIFT_CODE_SERVER_CODE, server_code);
		gift_code_enc.addString(KeyID.KEY_GIFT_CODE_CLIENT_CODE, client_code);
		gift_code_enc.addString(KeyID.KEY_GIFT_CODE_GIFT_CODE, gift_code);
		gift_code_enc.addStringUNICODE(KeyID.KEY_GIFT_CODE_NAME, name);
		gift_code_enc.addStringUNICODE(KeyID.KEY_GIFT_CODE_DESCRIPTION, description);
		gift_code_enc.addString(KeyID.KEY_GIFT_CODE_GIFT, gift);
		gift_code_enc.addInt(KeyID.KEY_GIFT_CODE_START_TIME, start_time);
		gift_code_enc.addInt(KeyID.KEY_GIFT_CODE_END_TIME, end_time);
		gift_code_enc.addInt(KeyID.KEY_GIFT_CODE_USE_TIME, use_time);
		
		gift_code_enc.addBoolean(KeyID.KEY_GIFT_CODE_RECEIVED, received);
		
		return gift_code_enc.toByteArray();
	}
	
	public void displayDataPackage()
	{
		// LogHelper.Log("id: " + id);
		// LogHelper.Log("server_code: " + server_code);
		// LogHelper.Log("client_code: " + client_code);
		// LogHelper.Log("gift_code: " + gift_code);
		// LogHelper.Log("name: " + name);
		// LogHelper.Log("description: " + description);
		// LogHelper.Log("gift: " + gift);
		// LogHelper.Log("start_time: " + start_time);
		// LogHelper.Log("end_time: " + end_time);
		// LogHelper.Log("use_time: " + use_time);
		// LogHelper.Log("received: " + received);

		System.out.println("id: " + id);
		System.out.println("server_code: " + server_code);
		System.out.println("client_code: " + client_code);
		System.out.println("gift_code: " + gift_code);
		System.out.println("name: " + name);
		System.out.println("description: " + description);
		System.out.println("gift: " + gift);
		System.out.println("start_time: " + start_time);
		System.out.println("end_time: " + end_time);
		System.out.println("use_time: " + use_time);
		System.out.println("received: " + received);
	}
	
	public int checkValidCode(String _client_code)
	{
		int valid = ReturnCode.RESPONSE_AUTHENTICATE_GIFT_CODE_FAILED;
		
		if (Misc.SECONDS() > start_time && Misc.SECONDS() < end_time)
		{
			String merge_code = Misc.mergeCodes(server_code, _client_code);
			
			if (client_code.equals(_client_code) && gift_code.equals(merge_code) && use_time > 0)
			{
				valid = ReturnCode.RESPONSE_AUTHENTICATE_GIFT_CODE_OK;
				received = true;
			}
			else
			{
				if (client_code.equals(_client_code) == false || gift_code.equals(merge_code) == false)
				{
					valid = ReturnCode.RESPONSE_AUTHENTICATE_GIFT_CODE_FAILED;
					// LogHelper.Log("checkValidCode: Wrong code");
				}
				else if (use_time <= 0)
				{
					valid = ReturnCode.RESPONSE_GIFT_CODE_HAD_BEEN_RECEIVED;
					// LogHelper.Log("checkValidCode: use_time = " + use_time);
				}
			}
		}
		else
		{
			valid = ReturnCode.RESPONSE_GIFT_CODE_OUT_OF_DATE;
			// LogHelper.Log("checkValidCode: The gift code is out of date");
		}
		
		return valid;
	}
	
	// not use
	// public void decreaseUseTime()
	// {
		// if (use_time > 0) use_time--;
	// }
	
	public String getGift()
	{
		return gift;
	}
	
	public String getID()
	{
		return id;
	}
	
	public int getExpireTime()
	{
		return (end_time - Misc.SECONDS());
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public int getUseTime()
	{
		return use_time;
	}
	
	public boolean getReceived()
	{
		return received;
	}
}