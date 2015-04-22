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

public class ZaloLoadAccountTask extends ZaloAuthenticateTask
{
	private long _stored_uid = -1;
	private String _stored_session = "";
	
	public ZaloLoadAccountTask(Client client, FBEncrypt encrypt)
	{
		super(client, encrypt);
	}
	
	@Override
	protected void HandleTask() 
	{
		if (!_encrypt.hasKey(KeyID.KEY_ZALO_ID) || !_encrypt.hasKey(KeyID.KEY_ZALO_ACCESS_TOKEN) || !_encrypt.hasKey(KeyID.KEY_ZALO_NAME))
		{
			LogHelper.Log("ZaloLoadAccountTask.. err! not enough client params.");
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
			
			LogHelper.Log("ZaloLoadAccountTask [" + _uid + "].. zalo id: " + _zalo_id);
			LogHelper.Log("ZaloLoadAccountTask [" + _uid + "].. zalo access token: " + _access_token);
			LogHelper.Log("ZaloLoadAccountTask [" + _uid + "].. user device id: " + _encrypt.getString(KeyID.KEY_DEVICE_ID));
			LogHelper.Log("ZaloLoadAccountTask [" + _uid + "].. user id: " + _uid);
			
			// to avoid abuse, i should double check these params again.
			if (_zalo_id.equals("") || _access_token.equals("") || _uid < 0 || _zalo_name.equals(""))
			{
				LogHelper.Log("ZaloLoadAccountTask [" + _uid + "].. err! invalid client params.");
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
			String stored_user_id = "";

			try
			{
				stored_user_id = (String)DBConnector.GetMembaseServerForGeneralData().Get("zalo" + "_" + _zalo_id + "_" + "u");
			}
			catch (Exception e)
			{
				LogHelper.LogException("ZaloLoadAccountTask.LoadStoredUserID", e);
				stored_user_id = "";
			}

			if (stored_user_id != null && !stored_user_id.equals(""))
			{
				_stored_uid = Long.parseLong(stored_user_id);
				LogHelper.Log("ZaloLoadAccountTask [" + _uid + "].. get stored user id = " + _stored_uid);
				
				try
				{
					_stored_session = (String)DBConnector.GetMembaseServer(_stored_uid).Get(_stored_uid + "_" + KeyID.KEY_USER_SESSION_ID);
				}
				catch (Exception e)
				{
					LogHelper.LogException("ZaloLoadAccountTask.LoadStoredSessionID", e);
					_stored_session = "";
				}

				LogHelper.Log("ZaloLoadAccountTask [" + _uid + "].. get stored session id: " + _stored_session);
			}
			else
			{
				LogHelper.Log("ZaloLoadAccountTask [" + _uid + "].. err! this zalo account does not link with any user id.");
			}
		}
		
		// create response status info
		FBEncrypt responseStatus = new FBEncrypt();
		responseStatus.addLong(KeyID.KEY_USER_ID, _encrypt.getLong(KeyID.KEY_USER_ID));
		responseStatus.addStringANSI(KeyID.KEY_USER_SESSION_ID, _encrypt.getString(KeyID.KEY_USER_SESSION_ID));
		responseStatus.addShort(KeyID.KEY_USER_COMMAND_ID, _encrypt.getShort(KeyID.KEY_USER_COMMAND_ID));
		responseStatus.addLong(KeyID.KEY_USER_REQUEST_ID, _encrypt.getLong(KeyID.KEY_USER_REQUEST_ID) + 2);

		if (_result == ReturnCode.RESPONSE_ZING_AUTHENTICATE_FAIL || _stored_uid < 0 || _stored_session.equals("") || _stored_session.length() == 0)
		{
			responseStatus.addByte(KeyID.KEY_USER_REQUEST_STATUS, ReturnCode.RESPONSE_ZING_AUTHENTICATE_FAIL);
		}
		else
		{
			responseStatus.addByte(KeyID.KEY_USER_REQUEST_STATUS, ReturnCode.RESPONSE_OK);
		}

		// reponse to client
		FBEncrypt encoder = new FBEncrypt();
		encoder.addBinary(KeyID.KEY_REQUEST_STATUS, responseStatus.toByteArray());

		if (_result == ReturnCode.RESPONSE_ZING_AUTHENTICATE_FAIL)
		{
			encoder.addLong(KeyID.REQUESTED_USER_ID, -1);
			encoder.addStringANSI(KeyID.REQUESTED_SESSION_ID, "");
		}
		else
		{
			encoder.addLong(KeyID.REQUESTED_USER_ID, _stored_uid); // response user name
			encoder.addStringANSI(KeyID.REQUESTED_SESSION_ID, _stored_session); // response password
		}

		try
		{
			_client.WriteZip(encoder.toByteArray());
			LogHelper.Log("ZaloLoadAccountTask [" + _uid + "].. response to client OK.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("ZaloAuthenticateTask.Response", e);
		}
	}
}
