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

public class FBLoadAccountTask extends FBAuthenticateTask
{
	private long _stored_uid = -1;
	private String _stored_session_id = "";
	
	public FBLoadAccountTask(Client client, FBEncrypt encrypt)
	{
		super(client, encrypt);
	}
	
	@Override
	protected void HandleTask() 
	{
		boolean task_result = true;
		if (!_encrypt.hasKey(KeyID.KEY_FACEBOOK_ID) || !_encrypt.hasKey(KeyID.KEY_FACEBOOK_ACCESS_TOKEN))
		{
			LogHelper.Log("FBLoadAccountTask.. err! invalid client params.");
			task_result = false;
		}
		else
		{
			_facebook_id			= _encrypt.getString(KeyID.KEY_FACEBOOK_ID);
			_user_access_token	= _encrypt.getString(KeyID.KEY_FACEBOOK_ACCESS_TOKEN);
			
			// to avoid abuse, i should double check these params again.
			if (_facebook_id.equals("") || _user_access_token.equals(""))
			{
				LogHelper.Log("FBLoadAccountTask.. err! invalid client params.");
				task_result = false;
			}
		}
			
		// 2. verify user access token with fb service.
		if (task_result)
		{
			StringBuilder log = new StringBuilder();
			log.append(Misc.getCurrentDateTime());								//  1. thoi gian tieu tien
			log.append('\t').append("FBLoadAccount");							//  2. hanh dong cua gamer
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
		if (return_code == ReturnCode.RESPONSE_OK)
		{
			String stored_user_id = "";
			
			try
			{
				stored_user_id = (String)DBConnector.GetMembaseServerForGeneralData().Get("fb" + "_" + _facebook_id + "_" + "u");
			}
			catch (Exception e)
			{
				LogHelper.LogException("FBLoadAccountTask.LoadStoredUserID", e);
				stored_user_id = "";
			}

			if (stored_user_id != null && !stored_user_id.equals(""))
			{
				_stored_uid = Long.parseLong(stored_user_id);
				LogHelper.Log("FBLoadAccountTask [" + _facebook_id + "]..get stored user id = " + _stored_uid);

				// 4. get last stored session id
				try
				{
					_stored_session_id = (String)DBConnector.GetMembaseServer(_stored_uid).Get(_stored_uid + "_" + KeyID.KEY_USER_SESSION_ID);
				}
				catch (Exception e)
				{
					LogHelper.LogException("FBLoadAccountTask.LoadStoredSessionID", e);
					_stored_session_id = "";
				}

				LogHelper.Log("FBLoadAccountTask [" + _facebook_id + "].. get stored session id: " + _stored_session_id);
			}
			else
			{
				LogHelper.Log("FBLoadAccountTask [" + _facebook_id + "].. err! get invalid user id.");
			}
		}
		
		// create response status info
		FBEncrypt responseStatus = new FBEncrypt();
		responseStatus.addLong(KeyID.KEY_USER_ID, _encrypt.getLong(KeyID.KEY_USER_ID));
		responseStatus.addStringANSI(KeyID.KEY_USER_SESSION_ID, _encrypt.getString(KeyID.KEY_USER_SESSION_ID));
		responseStatus.addShort(KeyID.KEY_USER_COMMAND_ID, _encrypt.getShort(KeyID.KEY_USER_COMMAND_ID));
		responseStatus.addLong(KeyID.KEY_USER_REQUEST_ID, _encrypt.getLong(KeyID.KEY_USER_REQUEST_ID) + 2);
		
		if (return_code == ReturnCode.RESPONSE_FB_AUTHENTICATE_FAIL || _stored_uid < 0 || _stored_session_id.equals("") || _stored_session_id.length() == 0)
		{
			responseStatus.addByte(KeyID.KEY_USER_REQUEST_STATUS, ReturnCode.RESPONSE_FB_AUTHENTICATE_FAIL);
		}
		else
		{
			responseStatus.addByte(KeyID.KEY_USER_REQUEST_STATUS, ReturnCode.RESPONSE_OK);
		}

		// reponse to client
		FBEncrypt encoder = new FBEncrypt();
		encoder.addBinary(KeyID.KEY_REQUEST_STATUS, responseStatus.toByteArray());
		
		if (return_code == ReturnCode.RESPONSE_FB_AUTHENTICATE_FAIL)
		{
			encoder.addLong(KeyID.REQUESTED_USER_ID, -1);
			encoder.addStringANSI(KeyID.REQUESTED_SESSION_ID, "");
		}
		else
		{
			encoder.addLong(KeyID.REQUESTED_USER_ID, _stored_uid); // response user name
			encoder.addStringANSI(KeyID.REQUESTED_SESSION_ID, _stored_session_id); // response password
		}
		
		try
		{
			_client.WriteZip(encoder.toByteArray());
			LogHelper.Log("FBLoadAccountTask [" + _facebook_id + "].. response to client OK.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("FBLoadAccountTask.Response", e);
		}
	}
}
