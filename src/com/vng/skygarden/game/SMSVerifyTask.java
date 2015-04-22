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

public class SMSVerifyTask extends Task
{
	private Client		_client;
	private FBEncrypt	_encrypt;
	
	private long _uid = -1;
	private String _phone_number = "";
	private long _verify_code = -1;
	private int _result = -1;
	
	private final String SMS_SERVER_TEST = "http://10.30.81.51:8080/";
	private final String SMS_SERVER_REAL = "http://10.30.81.50:80/";
	private final String SECRECT_KEY = "ffubJKOdxMiyxwh7";
	private final String PRODUCT_NAME = "SGMB";
	private final String MESSAGE = "Chao mung ban den Khu Vuon Tren May Mobile. Vui long nhap _CODE_ de xac nhan tai khoan.";
	
	public SMSVerifyTask(Client client, FBEncrypt encrypt)
	{
		super();
		
		_client = client;
		_encrypt = encrypt;
	}
	
	@Override
	protected void HandleTask() 
	{
		boolean task_result = true;
		
		if (!_encrypt.hasKey(KeyID.KEY_USER_ID) || !_encrypt.hasKey(KeyID.KEY_VERIFY_PHONE))
		{
			LogHelper.Log("SMSTask.. err! invalid client params.");
			task_result = false;
		}
		else
		{
			_uid			= _encrypt.getLong(KeyID.KEY_USER_ID);
			_verify_code	= Long.parseLong(_encrypt.getString(KeyID.KEY_VERIFY_PHONE));
			
			if (_uid <= 0 || _verify_code < 0)
			{
				LogHelper.Log("SMSTask.. err! invalid params");
				
				task_result = false;
			}
			
			boolean add_gift = false;
			if (task_result)
			{
				// send request to server SMS
				try
				{
					StringBuilder parameters = new StringBuilder();
					parameters.append("verify?");
					parameters.append("product=").append(PRODUCT_NAME);
					parameters.append("&user=").append(_uid);
					parameters.append("&code=").append(_verify_code);
					parameters.append("&hash=").append(Misc.Hash(SECRECT_KEY + PRODUCT_NAME + _uid + _verify_code, "SHA-1"));
					LogHelper.Log("parameters: " + parameters.toString());

					java.net.URL url = new java.net.URL(SMS_SERVER_TEST + parameters.toString());
					java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");

					// receive response code
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String line = reader.readLine();
					
					if (line.contains("0") && line.contains("done"))
					{
						// get phone number
						_phone_number = ""; //TODO: get from response
						
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

						if (stored_uid != null && !stored_uid.equals("")) // OLD phone number
						{
							_result = ReturnCode.RESPONSE_PHONE_NUMBER_ALREADY_USED;
						}
						else // NEW phone number
						{
							// save phone number
							DBConnector.GetMembaseServerForGeneralData().Add(_uid + "_" + "phone", _client.GetUserInstance()._phone_number);
							DBConnector.GetMembaseServerForGeneralData().Add("phone" + "_" + _client.GetUserInstance()._phone_number + "_" + "u", Long.toString(_uid));
							
							add_gift = true;
							_result = ReturnCode.RESPONSE_OK;
						}
					}
					connection.disconnect();
				}
				catch (Exception e)
				{
					LogHelper.LogException("SMSTask.OpenHTTPConnection", e);
					_result = -1;
				}
			}
			
			if (add_gift && _result == ReturnCode.RESPONSE_OK && _client.GetUserInstance().GetGiftManager().LoadFromDatabase(KeyID.KEY_GIFT))
			{
				try
				{
					// add gift
					_client.GetUserInstance().GetGiftManager().AddGiftBox(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][5][DatabaseID.GIFT_INFO_NAME]),
																		  Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][5][DatabaseID.GIFT_INFO_DESCRIPTION]),
																		  Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][5][DatabaseID.GIFT_INFO_ITEMS_LIST]));
					_client.GetUserInstance().GetGiftManager().SaveDataToDatabase(KeyID.KEY_GIFT);
					_client.GetUserInstance().MoveGiftBoxToMailBox();
					
					StringBuilder log = new StringBuilder();
					log.append(Misc.getCurrentDateTime());											//  1. log time
					log.append('\t').append(CommandID.CMD_VERIFY_PHONE);							//  2. action name
					log.append('\t').append(_uid);													//  3. account name
					log.append('\t').append(_uid);													//  4. role id
					log.append('\t').append(_client.GetUserInstance().GetUserInfo().getName());		//  5. role name
					log.append('\t').append("0");													//  6. server id
					log.append('\t').append(_client.GetUserInstance().GetUserInfo().getLevel());	//  7. user level
					log.append('\t').append(_client.GetUserInstance().GetUserInfo().GetUserIP());	//  8. user ip
					log.append('\t').append(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][5][DatabaseID.GIFT_INFO_NAME]));						//  9. user gift code
					log.append('\t').append(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][5][DatabaseID.GIFT_INFO_DESCRIPTION]));				//  10. user gift code
					log.append('\t').append(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][5][DatabaseID.GIFT_INFO_ITEMS_LIST]));					//  11. user gift code
					LogHelper.Log(LogHelper.LogType.GIFT_BOX, log.toString());
				}
				catch (Exception e)
				{
					LogHelper.LogException("SMSTask.AddGift", e);
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
		encoder.addString(KeyID.KEY_PHONE_NUMBER, _phone_number);
		
		if (_result == ReturnCode.RESPONSE_OK && _client.GetUserInstance().GetGiftManager()._gifts.size() > 0)
		{
			encoder.addInt(KeyID.KEY_GIFT_ID, _client.GetUserInstance().GetGiftManager()._gifts.getLast().GetId());
			encoder.addString(KeyID.KEY_GIFT_NAME, _client.GetUserInstance().GetGiftManager()._gifts.getLast().GetName());
			encoder.addString(KeyID.KEY_GIFT_DESCRIPTION, _client.GetUserInstance().GetGiftManager()._gifts.getLast().GetDescription());
			encoder.addString(KeyID.KEY_GIFT_ITEM_LIST, _client.GetUserInstance().GetGiftManager()._gifts.getLast().GetItemList());
		}
		
		if (_result == ReturnCode.RESPONSE_PHONE_NUMBER_ALREADY_USED)
		{
			encoder.addBinary(KeyID.KEY_USER_INFOS, ServerHandler.GetUserData(Misc.GetDeviceID(0)));
			encoder.addStringANSI(KeyID.REQUESTED_SESSION_ID, ""); // response password
			encoder.addLong(KeyID.REQUESTED_USER_ID, 0);
		}

		try
		{
			_client.WriteZip(encoder.toByteArray());
			LogHelper.Log("SMSTask.. response to client OK.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("SMSTask.Response", e);
		}
	}
}