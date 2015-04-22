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

public class ZaloAuthenticateTask extends Task
{
	Client		_client;
	FBEncrypt	_encrypt;

	int _result;

	long _uid = -1;

	String _zalo_id = "";
	String _zalo_name = "";
	String _access_token = "";

	private final String ZALO_APP_NAME = "khuvuontrenmay";
	
	public ZaloAuthenticateTask(Client client, FBEncrypt encrypt)
	{
		super();
		
		_client = client;
		_encrypt = encrypt;
		_result = -1;
	}
	
	@Override
	protected void HandleTask()
	{	
		if (!_encrypt.hasKey(KeyID.KEY_ZALO_ID) || !_encrypt.hasKey(KeyID.KEY_ZALO_ACCESS_TOKEN) || !_encrypt.hasKey(KeyID.KEY_ZALO_NAME))
		{
			LogHelper.Log("ZaloAuthenticateTask.. err! not enough client params.");
			return;
		}
		
		// get valid client params
		_zalo_id		= _encrypt.getString(KeyID.KEY_ZALO_ID);
		_zalo_name		= _encrypt.getString(KeyID.KEY_ZALO_NAME);
		_access_token	= _encrypt.getString(KeyID.KEY_ZALO_ACCESS_TOKEN);
		
		if (_encrypt.hasKey(KeyID.KEY_USER_ID))
		{
			_uid = _encrypt.getLong(KeyID.KEY_USER_ID);
		}
		
		LogHelper.Log("ZaloAuthenticateTask [" + _uid + "].. zalo id: " + _zalo_id);
		LogHelper.Log("ZaloAuthenticateTask [" + _uid + "].. zalo access token: " + _access_token);
		LogHelper.Log("ZaloAuthenticateTask [" + _uid + "].. user device id: " + _encrypt.getString(KeyID.KEY_DEVICE_ID));
		LogHelper.Log("ZaloAuthenticateTask [" + _uid + "].. user id: " + _uid);
		
		// to avoid abuse, i should double check these params again.
		if (_zalo_id.equals("") || _access_token.equals("") || _zalo_name.equals(""))
		{
			LogHelper.Log("ZaloAuthenticateTask [" + _uid + "].. err! invalid client params.");
			return;
		}
		
		// 2. verify user access token with fb service.
		try
		{
			new AccessTokenHandler(this, _access_token);
		}
		catch (Exception e)
		{
			LogHelper.LogException("ZaloAuthenticateTask.VerifyToken", e);
		}
	}

	public void ZaloCallback(String accessToken)
	{
		boolean return_new_session = false;

		// 3. reset user's session id to default_session
		if (accessToken != null)
		{
			_client.GetUserInstance()._zalo_token = accessToken;
			LogHelper.Log("zalo call back access token: " + accessToken);
		
			String stored_user_id = "";

			try
			{
				stored_user_id = (String)DBConnector.GetMembaseServerForGeneralData().Get("zalo" + "_" + _zalo_id + "_" + "u");
			}
			catch (Exception e)
			{
				LogHelper.LogException("ZaloAuthenticateTask.LoadStoredUserID", e);
				stored_user_id = "";
			}

			if (stored_user_id != null && !stored_user_id.equals(""))
			{	
				if (Long.parseLong(stored_user_id) == _uid)
				{
					// reset session to default_session
					return_new_session = DBConnector.GetMembaseServer(_uid).Set(_uid + "_" + KeyID.KEY_USER_SESSION_ID, "default_session");
					LogHelper.Log("ZaloAuthenticateTask [" + _uid + "].. [1] reset session of user id " + _uid + ": " + return_new_session);
				}
				else // allow user uses any facebook to login
				{
					_uid = Long.parseLong(stored_user_id);
					return_new_session = DBConnector.GetMembaseServer(_uid).Set(_uid + "_" + KeyID.KEY_USER_SESSION_ID, "default_session");
					LogHelper.Log("ZaloAuthenticateTask [" + _uid + "].. [2] reset session of user id " + _uid + ": " + return_new_session);
				}
			}
			else
			{
				LogHelper.Log("ZaloAuthenticateTask.. err! this zalo account does not link with any user id.");
				_result = ReturnCode.RESPONSE_MISMATCH_UID;
			}
		}
		
		// create response status info
		FBEncrypt responseStatus = new FBEncrypt();
		responseStatus.addLong(KeyID.KEY_USER_ID, _uid);
		responseStatus.addStringANSI(KeyID.KEY_USER_SESSION_ID, _encrypt.getString(KeyID.KEY_USER_SESSION_ID));
		if (return_new_session)
		{
			responseStatus.addStringANSI(KeyID.KEY_USER_NEW_SESSION_ID, "default_session");
		}

		responseStatus.addShort(KeyID.KEY_USER_COMMAND_ID, _encrypt.getShort(KeyID.KEY_USER_COMMAND_ID));
		responseStatus.addLong(KeyID.KEY_USER_REQUEST_ID, _encrypt.getLong(KeyID.KEY_USER_REQUEST_ID) + 2);

		if (_result == ReturnCode.RESPONSE_ZALO_AUTHENTICATE_FAIL || _result == ReturnCode.RESPONSE_MISMATCH_UID)
		{
			responseStatus.addByte(KeyID.KEY_USER_REQUEST_STATUS, _result);
		}
		else
		{
			responseStatus.addByte(KeyID.KEY_USER_REQUEST_STATUS, ReturnCode.RESPONSE_RETRY_LOGIN);
		}

		// reponse to client
		FBEncrypt encoder = new FBEncrypt();
		encoder.addBinary(KeyID.KEY_REQUEST_STATUS, responseStatus.toByteArray());

		try
		{
			_client.WriteZip(encoder.toByteArray());
			LogHelper.Log("ZaloAuthenticateTask [" + _uid + "].. response to client OK.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("ZaloAuthenticateTask.Response", e);
		}
	}
}
