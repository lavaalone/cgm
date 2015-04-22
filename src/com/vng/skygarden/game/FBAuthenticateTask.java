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
import com.vng.skygarden._gen_.ProjectConfig;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FBAuthenticateTask extends Task
{
	Client		_client;
	FBEncrypt	_encrypt;
	
	int _result;
	long _uid = -1;
	
	String _facebook_id = "";
	String _user_access_token = "";

	public FBAuthenticateTask(Client client, FBEncrypt encrypt)
	{
		super();
		
		_client = client;
		_encrypt = encrypt;
		_result = -1;
	}
	
	@Override
	protected void HandleTask() 
	{
		boolean task_result = true;
		if (!_encrypt.hasKey(KeyID.KEY_FACEBOOK_ID) || !_encrypt.hasKey(KeyID.KEY_FACEBOOK_ACCESS_TOKEN)/* || !_encrypt.hasKey(KeyID.KEY_USER_ID)*/)
		{
			LogHelper.Log("FBAuthenticateTask.. err! not enough client params.");
			task_result = false;
		}
		else
		{
			// get valid client params
			_facebook_id		= _encrypt.getString(KeyID.KEY_FACEBOOK_ID);
			_user_access_token	= _encrypt.getString(KeyID.KEY_FACEBOOK_ACCESS_TOKEN);
			
			if (_encrypt.hasKey(KeyID.KEY_USER_ID))
			{
				_uid				= _encrypt.getLong(KeyID.KEY_USER_ID);
			}
			else
			{
				_uid = Misc.GetUserID(_encrypt.getString(KeyID.KEY_DEVICE_ID));
			}
			
			// to avoid abuse, i should double check these params again.
			if (_facebook_id.equals("") || _user_access_token.equals("")/* || _uid < 0*/)
			{
				LogHelper.Log("FBAuthenticateTask [" + _uid + "].. err! invalid client params.");
				task_result = false;
			}
		}

		if (task_result)
		{
			StringBuilder log = new StringBuilder();
			log.append(Misc.getCurrentDateTime());								//  1. thoi gian tieu tien
			log.append('\t').append("FBAuthenticate");							//  2. hanh dong cua gamer
			log.append('\t').append(_uid);										//  3. id
			log.append('\t').append(_uid);										//  4. role id
			log.append('\t').append("no_name");									//  5. name
			log.append('\t').append(0);											//  6. server id
			log.append('\t').append(19);										//  7. level
			LogHelper.Log(LogHelper.LogType.TRACKING_ACTION, log.toString());
			
			try
			{
				new FBAccessTokenVerifier(this, _user_access_token, _facebook_id);
			}
			catch (Exception e)
			{
				LogHelper.LogException("FBAuthenticateTask.FBAccessTokenVerifier", e);
			}
		}
	}
	
	public void FBCallback(int return_code)
	{
		// 3. reset user's session id to default_session
		boolean return_new_session = false;
		if (return_code == ReturnCode.RESPONSE_OK)
		{
			String stored_user_id = "";
			
			try
			{
				stored_user_id = (String)DBConnector.GetMembaseServerForGeneralData().Get("fb" + "_" + _facebook_id + "_" + "u");
			}
			catch (Exception e)
			{
				LogHelper.LogException("FBAuthenticateTask.LoadStoredUserID", e);
				stored_user_id = "";
			}

			if (stored_user_id != null && !stored_user_id.equals(""))
			{	
				if (Long.parseLong(stored_user_id) == _uid)
				{
					// reset session to default_session
					boolean reset_result = DBConnector.GetMembaseServer(_uid).Set(_uid + "_" + KeyID.KEY_USER_SESSION_ID, "default_session");
					LogHelper.Log("FBAuthenticateTask [" + _uid + "].. [1] reset session of user id " + _uid + ": " + reset_result);
					
					return_new_session = reset_result;
				}
				else {
					_uid = Long.parseLong(stored_user_id);
					boolean reset_result = DBConnector.GetMembaseServer(_uid).Set(_uid + "_" + KeyID.KEY_USER_SESSION_ID, "default_session");
					LogHelper.Log("FBAuthenticateTask [" + _uid + "].. [2] reset session of user id " + _uid + ": " + reset_result);
					
					return_new_session = reset_result;
				}
			}
			else
			{
				LogHelper.Log("FBAuthenticateTask [" + _uid + "].. err! this facebook account does not link with any user id.");
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
		
		if (return_code == ReturnCode.RESPONSE_FB_AUTHENTICATE_FAIL || return_code == ReturnCode.RESPONSE_MISMATCH_UID)
		{
			responseStatus.addByte(KeyID.KEY_USER_REQUEST_STATUS, return_code);
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
			LogHelper.Log("FBAuthenticateTask [" + _uid + "].. response to client OK.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("FBAuthenticateTask.Response", e);
		}
	}
}
