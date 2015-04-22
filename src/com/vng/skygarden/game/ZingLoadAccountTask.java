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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ZingLoadAccountTask extends ZingAuthenticateTask
{
	private long _stored_uid = -1;
	private String _stored_session = "";
	
	public ZingLoadAccountTask(Client client, FBEncrypt encrypt)
	{
		super(client, encrypt);

	}
	
	@Override
	protected void HandleTask() 
	{
		boolean task_result = true;
		
		if (!_encrypt.hasKey(KeyID.KEY_ZING_ID) || !_encrypt.hasKey(KeyID.KEY_ZING_ACCESS_TOKEN) || !_encrypt.hasKey(KeyID.KEY_ZING_NAME))
		{
			LogHelper.Log("ZingLoadAccountTask.. err! not enough client params.");
			task_result = false;
		}
		else
		{
			// get valid client params
			_zing_id		= _encrypt.getString(KeyID.KEY_ZING_ID);
			_zing_name		= _encrypt.getString(KeyID.KEY_ZING_NAME);
			_access_token	= _encrypt.getString(KeyID.KEY_ZING_ACCESS_TOKEN);
			
			if (_encrypt.hasKey(KeyID.KEY_USER_ID))
			{
				_uid				= _encrypt.getLong(KeyID.KEY_USER_ID);
			}
			else
			{
				_uid = Misc.GetUserID(_encrypt.getString(KeyID.KEY_DEVICE_ID));
			}
			
			// to avoid abuse, i should double check these params again.
			if (_zing_id.equals("") || _access_token.equals("") || _uid < 0 || _zing_name.equals(""))
			{
				LogHelper.Log("ZingLoadAccountTask [" + _uid + "].. err! invalid client params.");
				task_result = false;
			}
			
			if (task_result)
			{
				StringBuilder log = new StringBuilder();
				log.append(Misc.getCurrentDateTime());								//  1. thoi gian tieu tien
				log.append('\t').append("ZingLoadAccount");							//  2. hanh dong cua gamer
				log.append('\t').append(_uid);										//  3. id
				log.append('\t').append(_uid);										//  4. role id
				log.append('\t').append("no_name");									//  5. name
				log.append('\t').append(0);											//  6. server id
				log.append('\t').append(19);										//  7. level
				LogHelper.Log(LogHelper.LogType.TRACKING_ACTION, log.toString());
				
				try
				{
					new ZingAccessTokenVerifier(this, _access_token, _zing_id, _zing_name);
				}
				catch (Exception e)
				{
					LogHelper.LogException("ZingLoadAccountTask.VerifyToken", e);
				}
			}
			else
			{
				ZMCallback(ReturnCode.RESPONSE_ERROR);
			}
		}
	}
	
	public void ZMCallback(int return_code)
	{
		if (return_code == ReturnCode.RESPONSE_OK)
		{
			String stored_user_id = "";

			try
			{
				stored_user_id = (String)DBConnector.GetMembaseServerForGeneralData().Get("zing" + "_" + _zing_id + "_" + "u");
			}
			catch (Exception e)
			{
				LogHelper.LogException("ZingLoadAccountTask.LoadStoredUserID", e);
				stored_user_id = "";
			}

			if (stored_user_id != null && !stored_user_id.equals(""))
			{	
				_stored_uid = Long.parseLong(stored_user_id);
				LogHelper.Log("ZingLoadAccountTask [" + _uid + "].. get stored user id = " + _stored_uid);

				try
				{
					_stored_session = (String)DBConnector.GetMembaseServer(_stored_uid).Get(_stored_uid + "_" + KeyID.KEY_USER_SESSION_ID);
				}
				catch (Exception e)
				{
					LogHelper.LogException("ZingLoadAccountTask.LoadStoredSessionID", e);
					_stored_session = "";
				}

				LogHelper.Log("ZingLoadAccountTask [" + _uid + "].. get stored session id: " + _stored_session);
			}
			else
			{
				LogHelper.Log("ZingLoadAccountTask [" + _uid + "].. err! this zing account does not link with any user id.");
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
			LogHelper.Log("ZingLoadAccountTask [" + _uid + "].. response to client OK.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("ZingAuthenticateTask.Response", e);
		}
	}
}
