/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.taskqueue.*;
import com.vng.skygarden.*;
import com.vng.log.LogHelper;
import com.vng.zaloSDK.AccessTokenHandler;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ZaloGetTokenTask extends ZaloAuthenticateTask
{
	private long _stored_uid = -1;
	private String _stored_session = "";
	
	public ZaloGetTokenTask(Client client, FBEncrypt encrypt)
	{
		super(client, encrypt);
	}
	
	@Override
	protected void HandleTask() 
	{
		if (!_encrypt.hasKey(KeyID.KEY_ZALO_ID) || !_encrypt.hasKey(KeyID.KEY_ZALO_ACCESS_TOKEN) || !_encrypt.hasKey(KeyID.KEY_ZALO_NAME))
		{
			LogHelper.Log("ZaloGetTokenTask.. err! not enough client params.");
			return;
		}
		else
		{
			// get valid client params
			_zalo_id		= _encrypt.getString(KeyID.KEY_ZALO_ID);
			_zalo_name		= _encrypt.getString(KeyID.KEY_ZALO_NAME);
			_access_token	= _encrypt.getString(KeyID.KEY_ZALO_ACCESS_TOKEN);
			
			if (_encrypt.hasKey(KeyID.KEY_USER_ID))
			{
				_uid				= _encrypt.getLong(KeyID.KEY_USER_ID);
			}
			else
			{
				_uid = Misc.GetUserID(_encrypt.getString(KeyID.KEY_DEVICE_ID));
			}
			
			LogHelper.Log("ZaloGetTokenTask [" + _uid + "].. zalo id: " + _zalo_id);
			LogHelper.Log("ZaloGetTokenTask [" + _uid + "].. zalo access token: " + _access_token);
			LogHelper.Log("ZaloGetTokenTask [" + _uid + "].. user device id: " + _encrypt.getString(KeyID.KEY_DEVICE_ID));
			LogHelper.Log("ZaloGetTokenTask [" + _uid + "].. user id: " + _uid);
			
			// to avoid abuse, i should double check these params again.
			if (_zalo_id.equals("") || _access_token.equals("") || _uid < 0 || _zalo_name.equals(""))
			{
				LogHelper.Log("ZaloGetTokenTask [" + _uid + "].. err! invalid client params.");
				return;
			}
			
			// 2. verify user access token with fb service.
			try
			{
				new AccessTokenHandler(this, _access_token);
			}
			catch (Exception e)
			{
				LogHelper.LogException("ZaloLoadAccountTask.VerifyToken", e);
			}
		}
	}

	public void ZaloCallback(String accessToken)
	{
		if (accessToken != null)
		{
			_client.GetUserInstance()._zalo_token = accessToken;
			LogHelper.Log("zalo call back access token: " + accessToken);
		}
		
		// create response status info
		FBEncrypt responseStatus = new FBEncrypt();
		responseStatus.addLong(KeyID.KEY_USER_ID, _uid);
		responseStatus.addStringANSI(KeyID.KEY_USER_SESSION_ID, _encrypt.getString(KeyID.KEY_USER_SESSION_ID));
		responseStatus.addShort(KeyID.KEY_USER_COMMAND_ID, _encrypt.getShort(KeyID.KEY_USER_COMMAND_ID));
		responseStatus.addLong(KeyID.KEY_USER_REQUEST_ID, _encrypt.getLong(KeyID.KEY_USER_REQUEST_ID) + 2);
		responseStatus.addByte(KeyID.KEY_USER_REQUEST_STATUS, accessToken != null ? ReturnCode.RESPONSE_OK : ReturnCode.RESPONSE_ERROR);

		// reponse to client
		FBEncrypt encoder = new FBEncrypt();
		encoder.addBinary(KeyID.KEY_REQUEST_STATUS, responseStatus.toByteArray());
		if (accessToken != null)
		{
			encoder.addString(KeyID.KEY_ZALO_ACCESS_TOKEN, accessToken);
		}

		try
		{
			_client.WriteZip(encoder.toByteArray());
			LogHelper.Log("ZaloGetTokenTask [" + _uid + "].. response to client OK.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("ZaloGetTokenTask.Response", e);
		}
	}
}
