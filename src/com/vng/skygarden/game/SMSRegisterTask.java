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
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONObject;

public class SMSRegisterTask extends Task
{
	private Client		_client;
	private FBEncrypt	_encrypt;
	
	private long _uid = -1;
	private String _phone_number = "";
	private int _result = -1;
	
	private final String SMS_SERVER_TEST = "http://10.30.81.51:8080/";
	private final String SMS_SERVER_REAL = "http://10.30.81.50:80/";
	private final String SECRECT_KEY = "ffubJKOdxMiyxwh7";
	private final String PRODUCT_NAME = "SGMB";
	private final String MESSAGE = "KVTM Mobile gui ban ma xac nhan tai khoan _CODE_";
	
	public SMSRegisterTask(Client client, FBEncrypt encrypt)
	{
		super();
		
		_client = client;
		_encrypt = encrypt;
	}
	
	@Override
	protected void HandleTask() 
	{
		boolean task_result = true;
		
		if (!_encrypt.hasKey(KeyID.KEY_USER_ID) || !_encrypt.hasKey(KeyID.KEY_USER_PHONE))
		{
			LogHelper.Log("SMSRegisterTask.. err! invalid client params.");
			task_result = false;
		}
		else
		{
			_uid			= _encrypt.getLong(KeyID.KEY_USER_ID);
			_phone_number	= _encrypt.getString(KeyID.KEY_USER_PHONE);
			
			if (_uid <= 0 || _phone_number == null || _phone_number.length() == 0)
			{
				LogHelper.Log("SMSRegisterTask.. err! invalid params");
				
				task_result = false;
			}
			
			// check if this uid already linked with a phone number
			if (task_result)
			{
				String stored_phone_number = "";
				try
				{
					stored_phone_number = (String)DBConnector.GetMembaseServerForGeneralData().Get(_uid + "_" + "phone");
				}
				catch (Exception e)
				{
					LogHelper.LogException("SMSRegisterTask.GetStoredPhoneNumber", e);
					stored_phone_number = null;
				}

				if (stored_phone_number != null && !stored_phone_number.equals(""))
				{
					task_result = false;
					_result = ReturnCode.RESPONSE_ALREADY_LINKED_PHONE_NUMBER;
					LogHelper.Log(LogHelper.LogType.HACK, "Account already linked with phone number, uid=" + _uid);
				}
			}
			
			// check if this phone number is already used
			if (task_result)
			{
				String stored_uid = "";
				
				try
				{
					stored_uid = (String)DBConnector.GetMembaseServerForGeneralData().Get("phone" + "_" + _phone_number + "_" + "u");
				}
				catch (Exception e)
				{
					LogHelper.LogException("SMSRegisterTask.GetStoredUid", e);
					stored_uid = null;
				}
				
				if (stored_uid != null && !stored_uid.equals(""))
				{
					task_result = false;
					_result = ReturnCode.RESPONSE_PHONE_NUMBER_ALREADY_USED;
					LogHelper.Log("SMSRegisterTask.. Re-use old phone number=" + _phone_number + ", uid=" + _uid);
				}
			}
			
			// send request to server SMS for NEW user
			if (task_result)
			{
				try
				{
					StringBuilder parameters = new StringBuilder();
					parameters.append("register?");
					parameters.append("product=").append(PRODUCT_NAME);
					parameters.append("&user=").append(_uid);
					parameters.append("&phone=").append(_phone_number);
					parameters.append("&msg=").append(MESSAGE);
					parameters.append("&hash=").append(Misc.Hash(SECRECT_KEY + PRODUCT_NAME + _uid + _phone_number + MESSAGE, "SHA-1"));
					LogHelper.Log("parameters: " + parameters.toString());

					java.net.URL url = new java.net.URL(SMS_SERVER_TEST + parameters.toString());
					java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");

					// receive response code
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String line = reader.readLine();
					
					if (line.contains("" + 0) && line.contains("" + _uid) && line.contains(_phone_number))
					{
						_result = ReturnCode.RESPONSE_OK;
						
						// temporary set the user phone number to this current one
						_client.GetUserInstance()._phone_number = this._phone_number;
										
						// Log Social
//						StringBuilder log = new StringBuilder();
//						log.append(Misc.getCurrentDateTime());
//						log.append('\t').append(_uid);
//						log.append('\t').append(4); // 4 = phone number
//						log.append('\t').append(_phone_number);
//						log.append('\t').append("");
//						log.append('\t').append("");
//						LogHelper.Log(LogHelper.LogType.SOCIAL, log.toString());
					}
					connection.disconnect();
				}
				catch (Exception e)
				{
					LogHelper.LogException("SMSRegisterTask.OpenHTTPConnection", e);
					_result = ReturnCode.RESPONSE_ERROR;
				}
			}
			else
			{
				if (_result == ReturnCode.RESPONSE_PHONE_NUMBER_ALREADY_USED) // send sms to OLD user, ask for confirmation
				{
					try
					{
						StringBuilder parameters = new StringBuilder();
						parameters.append("recheck?");
						parameters.append("product=").append(PRODUCT_NAME);
						parameters.append("&user=").append(_uid);
						parameters.append("&phone=").append(_phone_number);
						parameters.append("&msg=").append(MESSAGE);
						parameters.append("&hash=").append(Misc.Hash(SECRECT_KEY + PRODUCT_NAME + _uid + _phone_number + MESSAGE, "SHA-1"));
						LogHelper.Log("parameters: " + parameters.toString());

						java.net.URL url = new java.net.URL(SMS_SERVER_TEST + parameters.toString());
						java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
						connection.setRequestMethod("GET");
						
						// receive response code
						BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String line = reader.readLine();
						
						if (line.contains("" + 0) && line.contains("" + _uid) && line.contains(_phone_number))
						{
							_result = ReturnCode.RESPONSE_OK;
						}
						
						connection.disconnect();
					}
					catch (Exception e)
					{
						LogHelper.LogException("SMSRegisterTask.SendSMSRequestConfirmation", e);
					}
				}
			}
				
		}
		// create response status info
		FBEncrypt responseStatus = new FBEncrypt();
		responseStatus.addShort(KeyID.KEY_USER_COMMAND_ID, _encrypt.getShort(KeyID.KEY_USER_COMMAND_ID));
		responseStatus.addLong(KeyID.KEY_USER_ID, _encrypt.getLong(KeyID.KEY_USER_ID));
		responseStatus.addLong(KeyID.KEY_USER_REQUEST_ID, _encrypt.getLong(KeyID.KEY_USER_REQUEST_ID) + 2);
		responseStatus.addStringANSI(KeyID.KEY_USER_SESSION_ID, _encrypt.getString(KeyID.KEY_USER_SESSION_ID));
		responseStatus.addByte(KeyID.KEY_USER_REQUEST_STATUS, _result);
		
		// reponse to client
		FBEncrypt encoder = new FBEncrypt();
		encoder.addBinary(KeyID.KEY_REQUEST_STATUS, responseStatus.toByteArray());

		try
		{
			_client.WriteZip(encoder.toByteArray());
			LogHelper.Log("SMSRegisterTask.. response to client OK.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("SMSRegisterTask.Response", e);
		}
	}
}