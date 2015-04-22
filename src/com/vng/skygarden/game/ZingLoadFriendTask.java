
package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.taskqueue.*;
import com.vng.skygarden.*;
import com.vng.log.LogHelper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class ZingLoadFriendTask extends ZingAuthenticateTask
{
	FBEncrypt		_friend_data;
	private String	_device_id			= "";
	private String	_zing_display_name	= "";
	private String	_zing_avatar		= "";
	private String	_friend_list		= "";
	
	public ZingLoadFriendTask(Client client, FBEncrypt encrypt)
	{
		super(client, encrypt);
		
		_result = ReturnCode.RESPONSE_OK;
		_friend_data = new FBEncrypt();
	}
	
	@Override
	protected void HandleTask() 
	{
		boolean task_result = true;
		
		if (!_encrypt.hasKey(KeyID.KEY_ZING_ID) || 
			!_encrypt.hasKey(KeyID.KEY_ZING_NAME) ||
			!_encrypt.hasKey(KeyID.KEY_ZING_DISPLAY_NAME) ||
			!_encrypt.hasKey(KeyID.KEY_ZING_AVATAR) ||
			!_encrypt.hasKey(KeyID.KEY_ZING_FRIEND_PARAM) ||
			!_encrypt.hasKey(KeyID.KEY_ZING_ACCESS_TOKEN) ||
			!_encrypt.hasKey(KeyID.KEY_USER_ID) || 
			!_encrypt.hasKey(KeyID.KEY_DEVICE_ID))
		{
			LogHelper.Log("ZingLoadFriendTask.. err! not enough client params.");
			task_result = false;
		}
		else
		{
			_zing_id			= _encrypt.getString(KeyID.KEY_ZING_ID);
			_zing_name			= _encrypt.getString(KeyID.KEY_ZING_NAME);
			int[] display_name = _encrypt.getIntArray(KeyID.KEY_ZING_DISPLAY_NAME);
			_zing_display_name	= new String(display_name, 0, display_name.length);
			_zing_avatar		= _encrypt.getString(KeyID.KEY_ZING_AVATAR);
			_access_token		= _encrypt.getString(KeyID.KEY_ZING_ACCESS_TOKEN);
			_friend_list		= _encrypt.getString(KeyID.KEY_ZING_FRIEND_PARAM);
			_uid				= _encrypt.getLong(KeyID.KEY_USER_ID);
			_device_id			= _encrypt.getString(KeyID.KEY_DEVICE_ID);
			
			// double check the params again
			if (_zing_id.equals("") || _zing_name.equals("") || _uid <= 0 || _device_id.equals("") || _friend_list.length() == 0 || _access_token.length() == 0)
			{
				LogHelper.Log("ZingLoadFriendTask.. err! invalid client params.");
				task_result = false;
			}
		}
			
		// verify client access token with zing service.
		if (task_result)
		{
			StringBuilder log = new StringBuilder();
			log.append(Misc.getCurrentDateTime());								//  1. thoi gian tieu tien
			log.append('\t').append("ZingLoadFriend");							//  2. hanh dong cua gamer
			log.append('\t').append(_uid);										//  3. id
			log.append('\t').append(_uid);										//  4. role id
			log.append('\t').append("no_name");									//  5. name
			log.append('\t').append(SkyGarden._server_id);						//  6. server id
			log.append('\t').append(19);										//  7. level
			LogHelper.Log(LogHelper.LogType.TRACKING_ACTION, log.toString());
			
			try
			{
				new ZingAccessTokenVerifier(this, _access_token, _zing_id, _zing_name);
			}
			catch (Exception e)
			{
				LogHelper.LogException("ZingLoadFriendTask.VerifyAccessToken", e);
			}
		}
		else
		{
			ZMCallback(ReturnCode.RESPONSE_ERROR);
		}
	}
	
	public void ZMCallback(int return_code)
	{
		boolean is_new_zing_id		= false;
		boolean update_info			= false;
		boolean load_friend			= false;
		boolean add_gift			= false;
		
		if (return_code == ReturnCode.RESPONSE_OK)
		{
			// check if this zing id is old or new
			String stored_user_id = "";
			try
			{
				stored_user_id = (String)DBConnector.GetMembaseServerForGeneralData().Get("zing" + "_" + _zing_id + "_" + "u");
			}
			catch (Exception e)
			{
				stored_user_id = "";
			}
			
			if (stored_user_id != null && !stored_user_id.equals(""))
			{
				is_new_zing_id = false;
				LogHelper.Log("ZingLoadFriendTask [" + _uid + "].. zing id [" + _zing_id + "] is OLD, linked with uid " + stored_user_id);
			}
			else
			{
				is_new_zing_id = true;
				LogHelper.Log("ZingLoadFriendTask [" + _uid + "].. zing id [" + _zing_id + "] is NEW.");
			}

			if (!is_new_zing_id)
			{
				/* if stored uid is different current uid, return the stored uid info to client. */
				if (Long.parseLong(stored_user_id) != _uid) 
				{
					_result = ReturnCode.RESPONSE_ZING_ID_ALREADY_USED;
					try 
					{
						_device_id = Misc.GetDeviceID(Long.parseLong(stored_user_id));
					} 
					catch (Exception e) 
					{
						LogHelper.LogException("ZingLoadFriendTask.ParseUserId", e);
						_device_id = "error";
					}
					LogHelper.Log("ZingLoadFriendTask [" + _uid + "].. current uid [" + _uid + "] is different with stored uid [" + stored_user_id + "] => return user info.");

					update_info = false;
					load_friend = false;
				}
				else 
				{
					/* if stored uid is the same with current uid: update user info (if have not) and update friends */
					LogHelper.Log("ZingLoadFriendTask [" + _uid + "].. old user id re-log same old zing id => update user info and friends.");
					UserInfo user_info = _client.GetUserInstance().GetUserInfo();
					if (user_info != null && user_info.GetZingID().equals("null"))
					{
						update_info = true;
						LogHelper.Log("ZingLoadFriendTask [" + _uid + "].. this uid is created by zing ID, now update user info.");
					}
					else
					{
						update_info = false;
					}

					load_friend = true;
				}
			}
			else
			{
				String stored_zing_id = "";
				try
				{
					stored_zing_id = (String)DBConnector.GetMembaseServerForGeneralData().Get(_uid + "_" + "zing");
				}
				catch (Exception e)
				{
					stored_zing_id = "";
				}

				if (stored_zing_id != null && !stored_zing_id.equals("")) 
				{
					/* old user id log in new zing id: do nothing in this case */
					_result = ReturnCode.RESPONSE_ERROR;
					update_info = false;
					load_friend = false;
					LogHelper.Log("ZingLoadFriendTask [" + _uid + "].. current uid [" + _uid + "] already linked with [" + stored_zing_id + "], log in NEW id [" + _zing_id + "] => client display errros.");
				} 
				else 
				{
					/* new user id log in new zing id */
					update_info = true;
					load_friend = true;
				}
			}
			
			LogHelper.Log("ZingLoadFriendTask [" + _uid + "].. update info: " + update_info);
			LogHelper.Log("ZingLoadFriendTask [" + _uid + "].. load friend: " + load_friend);
			
			if (update_info)
			{
				boolean result = false;
				result = DBConnector.GetMembaseServerForGeneralData().Add(_uid + "_" + "zing", _zing_id);
				LogHelper.Log("ZingLoadFriendTask [" + _uid + "].. map uid --> zing_name: " + result);

				result = DBConnector.GetMembaseServerForGeneralData().Add("zing" + "_" + _zing_id + "_" + "u", Long.toString(_uid));
				LogHelper.Log("ZingLoadFriendTask [" + _uid + "].. map zing_name --> uid: " + result);

				// set and save zing name in userInfo
				result = _client.GetUserInstance().SetAndSaveZingInfo(_zing_id, _zing_name, _zing_display_name, _zing_avatar);
				LogHelper.Log("ZingLoadFriendTask [" + _uid + "].. save zing info to user info: " + result);

				// give gift
				try
				{
					if (GameUtil.GetUserMisc(_uid).Get("gift_connect_zing").equals("") && _client.GetUserInstance().GetGiftManager().LoadFromDatabase(KeyID.KEY_GIFT))
					{
						result = _client.GetUserInstance().GetGiftManager().AddGiftBox(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][1][DatabaseID.GIFT_INFO_NAME]),
																					   Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][1][DatabaseID.GIFT_INFO_DESCRIPTION]),
																					   Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][1][DatabaseID.GIFT_INFO_ITEMS_LIST]));
						if (result)
						{
							result = _client.GetUserInstance().GetGiftManager().SaveDataToDatabase(KeyID.KEY_GIFT);
							_client.GetUserInstance().MoveGiftBoxToMailBox();

							StringBuilder log = new StringBuilder();
							log.append(Misc.getCurrentDateTime());											//  1. log time
							log.append('\t').append(CommandID.CMD_LOAD_FRIEND_ZING);						//  2. action name
							log.append('\t').append(_uid);													//  3. account name
							log.append('\t').append(_uid);													//  4. role id
							log.append('\t').append(_client.GetUserInstance().GetUserInfo().getName());		//  5. role name
							log.append('\t').append(SkyGarden._server_id);									//  6. server id
							log.append('\t').append(_client.GetUserInstance().GetUserInfo().getLevel());	//  7. user level
							log.append('\t').append(_client.GetUserInstance().GetUserInfo().GetUserIP());	//  8. user ip
							log.append('\t').append(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][1][DatabaseID.GIFT_INFO_NAME]));						//  9. user gift code
							log.append('\t').append(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][1][DatabaseID.GIFT_INFO_DESCRIPTION]));				//  10. user gift code
							log.append('\t').append(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][1][DatabaseID.GIFT_INFO_ITEMS_LIST]));					//  11. user gift code
							LogHelper.Log(LogHelper.LogType.GIFT_BOX, log.toString());
							
							GameUtil.GetUserMisc(_uid).Set("gift_connect_zing", Misc.getCurrentDateTime());
						}
					}

					add_gift = result;
				} 
				catch (Exception e) 
				{
					LogHelper.LogException("ZingLoadFriendTask.AddGift", e);
					add_gift = false;
				}

				LogHelper.Log("ZingLoadFriendTask [" + _uid + "].. add gift box: " + result);

				// Log Social
				StringBuilder log = new StringBuilder();
				log.append(Misc.getCurrentDateTime());
				log.append('\t').append(_uid);
				log.append('\t').append(1); // 1 = zing
				log.append('\t').append(_zing_id + "_" + _zing_name);
				log.append('\t').append("");
				log.append('\t').append("");
				LogHelper.Log(LogHelper.LogType.SOCIAL, log.toString());
				
				// PIG LOG
				StringBuilder piglog = new StringBuilder();
				piglog.append(Misc.getCurrentDateTime());		//  1. thoi gian dang nhap
				piglog.append('\t').append("SocialLogin");
				piglog.append('\t').append(_client.GetUserInstance().GetUserInfo().getPigID());
				piglog.append('\t').append("CGMFBS");
				piglog.append('\t').append(SkyGarden._server_id);
				piglog.append('\t').append(_client.GetUserInstance().GetUserInfo().getDeviceOS());
				piglog.append('\t').append(_uid);
				piglog.append('\t').append("Zing");
				piglog.append('\t').append(_zing_id);
				piglog.append('\t').append("");
				piglog.append('\t').append("");
				piglog.append('\t').append("");
				LogHelper.Log(LogHelper.LogType.PIG_LOG, piglog.toString());
			}
				
			if (load_friend)
			{
				_client.GetUserInstance().GetFriendManager().Clear(FriendManager.TYPE.ZING);
				String[] s = _friend_list.split(":");
				for (int i = 0 ; i < s.length ; i++)
				{
					String friend_uid = "";
					try
					{
						friend_uid = (String)DBConnector.GetMembaseServerForGeneralData().Get("zing" + "_" + s[i] + "_" + "u");
					}
					catch (Exception e)
					{
						LogHelper.LogException("ZingLoadFriendTask", e);
						friend_uid = "";
					}

					if (friend_uid != null && !friend_uid.equals(""))
					{
						byte[] friend_info = null;
						
						try
						{
							friend_info = DBConnector.GetMembaseServer(Long.parseLong(friend_uid)).GetRaw(friend_uid + "_" + KeyID.KEY_USER_INFOS);
						}
						catch (Exception e)
						{
							LogHelper.LogException("ZingLoadFriendTask", e);
							friend_info = null;
						}
						
						if (friend_info == null || friend_info.length == 0)
						{
							LogHelper.Log("ZingLoadFriendTask.. err! can't load user info of zing id: " + s[i]);
						}
						else
						{
							_friend_data.addBinary(s[i] + "_" + i, friend_info);
							LogHelper.Log("ZingLoadFriendTask [" + _uid + "].. loaded user info of zing id: " + s[i]);
							
							// update in-game friend list
							UserInfo ui = new UserInfo(friend_info);
							_client.GetUserInstance().GetFriendManager().AddFriend(ui.getDeviceID(), FriendManager.TYPE.ZING);
						}
					}
				}
				
				// save in-game friend list
				_client.GetUserInstance().GetFriendManager().SaveFriendListToDatabase(KeyID.KEY_FRIENDS);
				
				// set zing friend list
				_client.GetUserInstance().SetZingFriendList(_friend_list);
				
				// only update game name with zing name if user has not had facebook id yet
				if (_client.GetUserInstance().GetUserInfo().getFaceBookID().equals("null")) 
				{
					boolean update_name = _client.GetUserInstance().SetAndSaveUsername("" + _zing_display_name);
					LogHelper.Log("ZingLoadFriendTask [" + _uid + "].. save zing name ["+ _zing_display_name +"] to user info: " + update_name);
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
		
		if (_result == ReturnCode.RESPONSE_ZING_ID_ALREADY_USED)
		{
			encoder.addBinary(KeyID.KEY_USER_INFOS, ServerHandler.GetUserData(_device_id));
		}
		else
		{
			encoder.addBinary(KeyID.KEY_USER_INFOS, _client.GetUserInstance().GetUserData());
		}
		
		if (_friend_data.toByteArray().length > 0)
		{
			encoder.addBinary(KeyID.KEY_FRIEND_ZING_LIST_INFO, _friend_data.toByteArray());
		}
		
		if (_result == ReturnCode.RESPONSE_OK && add_gift && _client.GetUserInstance().GetGiftManager()._gifts.size() > 0)
		{
			encoder.addInt(KeyID.KEY_GIFT_ID, _client.GetUserInstance().GetGiftManager()._gifts.getLast().GetId());
			encoder.addString(KeyID.KEY_GIFT_NAME, _client.GetUserInstance().GetGiftManager()._gifts.getLast().GetName());
			encoder.addString(KeyID.KEY_GIFT_DESCRIPTION, _client.GetUserInstance().GetGiftManager()._gifts.getLast().GetDescription());
			encoder.addString(KeyID.KEY_GIFT_ITEM_LIST, _client.GetUserInstance().GetGiftManager()._gifts.getLast().GetItemList());
		}
		if (add_gift)
		{
			encoder.addBoolean(KeyID.KEY_NEW_MAIL, true);
		}
		
		try
		{
			_client.WriteZip(encoder.toByteArray());
			LogHelper.Log("ZingLoadFriendTask [" + _uid + "].. response to client OK.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("FBLoadFriendTask.Response", e);
		}
	}
}
