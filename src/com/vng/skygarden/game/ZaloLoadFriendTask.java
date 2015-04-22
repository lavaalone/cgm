
package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.taskqueue.*;
import com.vng.skygarden.*;
import com.vng.log.LogHelper;
import com.vng.zaloSDK.AccessTokenHandler;
import com.vng.zaloSDK.FriendHandler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class ZaloLoadFriendTask extends ZaloAuthenticateTask
{
	FBEncrypt	_friend_data;
	
	private final String ZALO_APP_NAME = "khuvuontrenmay";
	
	private String _device_id			= "";
	private String _zalo_display_name	= "";
	private String _zalo_avatar			= "";
	boolean _verified_token				= false;
	
	public ZaloLoadFriendTask(Client client, FBEncrypt encrypt)
	{
		super(client, encrypt);
		
		_result = ReturnCode.RESPONSE_OK;
		_friend_data = new FBEncrypt();
	}
	
	@Override
	protected void HandleTask() 
	{
		boolean task_result = true;
		
		if (!_encrypt.hasKey(KeyID.KEY_ZALO_ID) || 
			!_encrypt.hasKey(KeyID.KEY_ZALO_NAME) ||
			!_encrypt.hasKey(KeyID.KEY_ZALO_DISPLAY_NAME) ||
			!_encrypt.hasKey(KeyID.KEY_ZALO_AVATAR) ||
			!_encrypt.hasKey(KeyID.KEY_ZALO_ACCESS_TOKEN) ||
			!_encrypt.hasKey(KeyID.KEY_USER_ID) || 
			!_encrypt.hasKey(KeyID.KEY_DEVICE_ID))
		{
			LogHelper.Log("ZaloLoadFriendTask.. err! not enough client params.");
			task_result = false;
		}
		else
		{
			_zalo_id			= _encrypt.getString(KeyID.KEY_ZALO_ID);
			_zalo_name			= _encrypt.getString(KeyID.KEY_ZALO_NAME);
			int[] display_name = _encrypt.getIntArray(KeyID.KEY_ZALO_DISPLAY_NAME);
			_zalo_display_name	= new String(display_name, 0, display_name.length);
			_zalo_avatar		= _encrypt.getString(KeyID.KEY_ZALO_AVATAR);
			_access_token		= _encrypt.getString(KeyID.KEY_ZALO_ACCESS_TOKEN);
			_uid				= _encrypt.getLong(KeyID.KEY_USER_ID);
			_device_id			= _encrypt.getString(KeyID.KEY_DEVICE_ID);
			
			// double check the params again
			if (_zalo_id.equals("") || _zalo_name.equals("") || _uid <= 0 || _device_id.equals("") || _access_token.length() == 0)
			{
				LogHelper.Log("ZaloLoadFriendTask.. err! invalid client params.");
				task_result = false;
			}
			else
			{
				LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. client params zalo id:		"		+ _zalo_id);
				LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. client params zalo name:	"			+ _zalo_name);
				LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. client params zalo display name:	"	+ _zalo_display_name);
				LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. client params user id:		"		+ _uid);
				LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. client params user access token: "	+ _access_token);
				LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. client params user avatar: "			+ _zalo_avatar);
			}
		}
			
		if (task_result)
		{
			StringBuilder log = new StringBuilder();
			log.append(Misc.getCurrentDateTime());								//  1. thoi gian tieu tien
			log.append('\t').append("ZaloLoadFriend");							//  2. hanh dong cua gamer
			log.append('\t').append(_uid);										//  3. id
			log.append('\t').append(_uid);										//  4. role id
			log.append('\t').append("no_name");									//  5. name
			log.append('\t').append(SkyGarden._server_id);						//  6. server id
			log.append('\t').append(19);										//  7. level
			LogHelper.Log(LogHelper.LogType.TRACKING_ACTION, log.toString());
			
			try
			{
				new AccessTokenHandler(this, _access_token);
				_verified_token = false;
				return;
			}
			catch (Exception e)
			{
				LogHelper.LogException("VerifyAccessToken", e);
			}
		}
		ZaloCallback(null);
	}
	
	public void ZaloCallback(String returned_param)
	{
		if (!_verified_token)
		{
			if (returned_param != null)
			{
				_verified_token = true;
				_access_token = returned_param;
				try
				{
					new FriendHandler(this, _access_token);
					return;
				}
				catch (Exception e)
				{
					LogHelper.LogException("FriendHandler", e);
				}
			}
		}
		else
		{
			UpdateFriends(returned_param);
			return;
		}
	}

	public void UpdateFriends(String _friend_list)
	{
		boolean is_new_zalo_id		= false;
		boolean update_info			= false;
		boolean load_friend			= false;
		boolean add_gift			= false;
			
		if (_friend_list != null)
		{
			String stored_user_id = "";
			try
			{
				stored_user_id = (String)DBConnector.GetMembaseServerForGeneralData().Get("zalo" + "_" + _zalo_id + "_" + "u");
			}
			catch (Exception e)
			{
				stored_user_id = "";
			}
			
			if (stored_user_id != null && !stored_user_id.equals("")) // old zalo id
			{
				is_new_zalo_id = false;
				LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. zalo id [" + _zalo_id + "] is OLD, linked with uid " + stored_user_id);
			}
			else
			{
				is_new_zalo_id = true;
				LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. zalo id [" + _zalo_id + "] is NEW.");
			}

			if (!is_new_zalo_id)
			{
				if (Long.parseLong(stored_user_id) != _uid) 
				{
					_result = ReturnCode.RESPONSE_ZALO_ID_ALREADY_USED;
					try 
					{
						_device_id = Misc.GetDeviceID(Long.parseLong(stored_user_id));
					}
					catch (Exception e) 
					{
						LogHelper.LogException("ZaloLoadFriendTask.ParseUserId", e);
						_device_id = "error";
					}
					
					LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. current uid [" + _uid + "] is different with stored uid [" + stored_user_id + "] => return user info.");
					
					update_info = false;
					load_friend = false;
				}
				else
				{
					/* if stored uid is the same with current uid, just updates friends only. */
					LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. current uid [" + _uid + "] is the same with stored uid [" + stored_user_id + "] => update friends only.");
					UserInfo user_info = _client.GetUserInstance().GetUserInfo();
					if (user_info != null && user_info.GetZaloID().equals("null"))
					{
						update_info = true;
						LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. this uid is created by zalo ID, now update user info.");
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
				// get current zalo id that already linked with uid (if any)
				String stored_zalo_id = "";
				try
				{
					stored_zalo_id = (String)DBConnector.GetMembaseServerForGeneralData().Get(_uid + "_" + "zalo");
				}
				catch (Exception e)
				{
					stored_zalo_id = "";
				}
				
				if (stored_zalo_id != null && !stored_zalo_id.equals("")) 
				{
					/* old user id log in new zing id: do nothing in this case */
					_result = ReturnCode.RESPONSE_ERROR;
					update_info = false;
					load_friend = false;
					LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. current uid [" + _uid + "] already linked with [" + stored_zalo_id + "], log in NEW id [" + _zalo_id + "] => client display errros.");
				} 
				else 
				{
					/* new user id log in new zing id */
					update_info = true;
					load_friend = true;
				}
			}
			
			// load friend uid from zalo id
			LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. update info: " + update_info);
			LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. load friend: " + load_friend);
			
			if (update_info)
			{
				// new user id log in new zalo id
				boolean result = false;
				result = DBConnector.GetMembaseServerForGeneralData().Add(_uid + "_" + "zalo", _zalo_id);
				LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. map uid --> zalo_name: " + result);

				result = DBConnector.GetMembaseServerForGeneralData().Add("zalo" + "_" + _zalo_id + "_" + "u", Long.toString(_uid));
				LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. map zalo_name --> uid: " + result);

				// set and save zalo name in userInfo
				result = _client.GetUserInstance().SetAndSaveZaloInfo(_zalo_id, _zalo_name, _zalo_display_name, _zalo_avatar);
				LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. save zalo info to user info: " + result);

				// give gift
				try
				{
					if (GameUtil.GetUserMisc(_uid).Get("gift_connect_zalo").equals("") && _client.GetUserInstance().GetGiftManager().LoadFromDatabase(KeyID.KEY_GIFT))
					{
						result = _client.GetUserInstance().GetGiftManager().AddGiftBox(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][29][DatabaseID.GIFT_INFO_NAME]),
																					   Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][29][DatabaseID.GIFT_INFO_DESCRIPTION]),
																					   Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][29][DatabaseID.GIFT_INFO_ITEMS_LIST]));
						if (result)
						{
							result = _client.GetUserInstance().GetGiftManager().SaveDataToDatabase(KeyID.KEY_GIFT);
							_client.GetUserInstance().MoveGiftBoxToMailBox();

							StringBuilder log = new StringBuilder();
							log.append(Misc.getCurrentDateTime());											//  1. log time
							log.append('\t').append(CommandID.CMD_LOAD_FRIEND_ZALO);						//  2. action name
							log.append('\t').append(_uid);													//  3. account name
							log.append('\t').append(_uid);													//  4. role id
							log.append('\t').append(_client.GetUserInstance().GetUserInfo().getName());		//  5. role name
							log.append('\t').append(SkyGarden._server_id);									//  6. server id
							log.append('\t').append(_client.GetUserInstance().GetUserInfo().getLevel());	//  7. user level
							log.append('\t').append(_client.GetUserInstance().GetUserInfo().GetUserIP());	//  8. user ip
							log.append('\t').append(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][29][DatabaseID.GIFT_INFO_NAME]));						//  9. user gift code
							log.append('\t').append(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][29][DatabaseID.GIFT_INFO_DESCRIPTION]));				//  10. user gift code
							log.append('\t').append(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][29][DatabaseID.GIFT_INFO_ITEMS_LIST]));					//  11. user gift code
							LogHelper.Log(LogHelper.LogType.GIFT_BOX, log.toString());
							
							GameUtil.GetUserMisc(_uid).Set("gift_connect_zalo", Misc.getCurrentDateTime());
						}
					}

					add_gift = result;
				} 
				catch (Exception e) 
				{
					LogHelper.LogException("ZaloLoadFriendTask.AddGift", e);
					add_gift = false;
				}

				LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. add gift box: " + result);

				// Log Social
				StringBuilder log = new StringBuilder();
				log.append(Misc.getCurrentDateTime());
				log.append('\t').append(_uid);
				log.append('\t').append(0); // 0 = zalo
				log.append('\t').append(_zalo_id + "_" + _zalo_name);
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
				piglog.append('\t').append("Zalo");
				piglog.append('\t').append(_zalo_id);
				piglog.append('\t').append("");
				piglog.append('\t').append("");
				piglog.append('\t').append("");
				LogHelper.Log(LogHelper.LogType.PIG_LOG, piglog.toString());
			}
			
			if (load_friend)
			{
				_client.GetUserInstance().GetFriendManager().Clear(FriendManager.TYPE.ZALO);
				String[] s = _friend_list.split(";");
				for (int i = 0 ; i < s.length ; i++)
				{
					if (s[i].equals("dump_zalo_id") || s[i].equals("") || s[i].length() == 0)
						continue;
					
					String friend_uid = "";
					try
					{
						friend_uid = (String)DBConnector.GetMembaseServerForGeneralData().Get("zalo" + "_" + s[i] + "_" + "u");
					}
					catch (Exception e)
					{
						LogHelper.LogException("ZaloLoadFriendTask", e);
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
							LogHelper.LogException("ZaloLoadFriendTask", e);
							friend_info = null;
						}
						
						if (friend_info == null || friend_info.length == 0)
						{
							LogHelper.Log("ZaloLoadFriendTask.. err! can't load user info of zalo id: " + s[i]);
						}
						else
						{
							_friend_data.addBinary(s[i] + "_" + i, friend_info);
							LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. loaded user info of zalo id: " + s[i]);
							
							// update in-game friend list
							UserInfo ui = new UserInfo(friend_info);
							_client.GetUserInstance().GetFriendManager().AddFriend(ui.getDeviceID(), FriendManager.TYPE.ZALO);
						}
					}
					else
					{
						LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. user id of zalo id [" + s[i] + "] is null");
					}
				}
				
				// save in-game friend list
				_client.GetUserInstance().GetFriendManager().SaveFriendListToDatabase(KeyID.KEY_FRIENDS);
				
				// set zalo friend list
				_client.GetUserInstance().SetZaloFriendList(_friend_list);
				
				// only update game name with zalo name if user has not had facebook id yet
				if (_client.GetUserInstance().GetUserInfo().getFaceBookID().equals("null")) 
				{
					boolean update_name = _client.GetUserInstance().SetAndSaveUsername("" + _zalo_display_name);
					LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. save zalo name ["+ _zalo_display_name +"] to user info: " + update_name);
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
		
		if (_result == ReturnCode.RESPONSE_ZALO_ID_ALREADY_USED)
		{
			encoder.addBinary(KeyID.KEY_USER_INFOS, ServerHandler.GetUserData(_device_id));
		}
		else
		{
			encoder.addBinary(KeyID.KEY_USER_INFOS, _client.GetUserInstance().GetUserData());
		}
		
		if (_friend_data.toByteArray().length > 0)
		{
			encoder.addBinary(KeyID.KEY_FRIEND_ZALO_LIST_INFO, _friend_data.toByteArray());
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
			LogHelper.Log("ZaloLoadFriendTask [" + _uid + "].. response to client OK.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("FBLoadFriendTask.Response", e);
		}
	}
}
