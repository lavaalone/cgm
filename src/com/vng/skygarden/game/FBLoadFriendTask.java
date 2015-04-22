
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

public class FBLoadFriendTask extends FBAuthenticateTask
{
	FBEncrypt	_friend_data;
	private String _device_id = "";
	private String _friend_id_list = "";
	
	public FBLoadFriendTask(Client client, FBEncrypt encrypt)
	{
		super(client, encrypt);
		_friend_data = new FBEncrypt();
	}
	
	@Override
	protected void HandleTask() 
	{
		boolean task_result = true;
		if (!_encrypt.hasKey(KeyID.KEY_FACEBOOK_ID) || 
				!_encrypt.hasKey(KeyID.KEY_USER_ID) || 
				!_encrypt.hasKey(KeyID.KEY_FACEBOOK_FRIEND_PARAM) ||
				!_encrypt.hasKey(KeyID.KEY_FACEBOOK_ACCESS_TOKEN) ||
				!_encrypt.hasKey(KeyID.KEY_DEVICE_ID))
		{
			task_result = false;
		}
		else
		{
			_uid					= _encrypt.getLong(KeyID.KEY_USER_ID);
			_user_access_token		= _encrypt.getString(KeyID.KEY_FACEBOOK_ACCESS_TOKEN);
			_facebook_id			= _encrypt.getString(KeyID.KEY_FACEBOOK_ID);
			_device_id				= _encrypt.getString(KeyID.KEY_DEVICE_ID);
			_friend_id_list			= _encrypt.getString(KeyID.KEY_FACEBOOK_FRIEND_PARAM);

			// double check the params again
			if (_facebook_id.equals("") || _uid <= 0 || _device_id.equals("") || _friend_id_list.length() == 0 || _user_access_token.length() == 0)
			{
				task_result = false;
			}
			else
			{
				task_result = true;
			}
		}
		
		if (task_result)
		{
			StringBuilder log = new StringBuilder();
			log.append(Misc.getCurrentDateTime());								//  1. thoi gian tieu tien
			log.append('\t').append("FBLoadFriend");							//  2. hanh dong cua gamer
			log.append('\t').append(_uid);										//  3. id
			log.append('\t').append(_uid);										//  4. role id
			log.append('\t').append("no_name");									//  5. name
			log.append('\t').append(SkyGarden._server_id);						//  6. server id
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
		boolean is_new_fb_id		= false;
		boolean update_info			= false;
		boolean load_friend			= false;
		boolean add_gift			= false;
		boolean update_fb_name		= false;
		
		if (return_code == ReturnCode.RESPONSE_OK)
		{
			// get uid that already linked with this facebbook id (if any)
			String stored_user_id = "";
			try
			{
				stored_user_id = (String)DBConnector.GetMembaseServerForGeneralData().Get("fb" + "_" + _facebook_id + "_" + "u");
			}
			catch (Exception e)
			{
				stored_user_id = "";
			}
			
			if (stored_user_id != null && !stored_user_id.equals(""))
			{
				is_new_fb_id = false;
				LogHelper.Log("FBLoadFriendTask [" + _uid + "].. fb id [" + _facebook_id + "] is OLD, linked with uid " + stored_user_id);
			}
			else
			{
				is_new_fb_id = true;
				LogHelper.Log("FBLoadFriendTask [" + _uid + "].. fb id [" + _facebook_id + "] is NEW.");
			}

			if (!is_new_fb_id)
			{
				/* if stored uid is different current uid, return the stored uid info to client. */
				if (Long.parseLong(stored_user_id) != _uid) 
				{
					_result = ReturnCode.RESPONSE_FB_ID_ALREADY_USED;
					try 
					{
						_device_id = Misc.GetDeviceID(Long.parseLong(stored_user_id));
					} catch (Exception e) 
					{
						LogHelper.LogException("FBLoadFriendTask.ParseUserId", e);
						_device_id = "error";
					}
					LogHelper.Log("FBLoadFriendTask [" + _uid + "].. current uid [" + _uid + "] is different with stored uid [" + stored_user_id + "] => return user info.");
					
					update_info = false;
					load_friend = false;
				}
				else 
				{
					//* if stored uid is the same with current uid: update user info (if have not) and update friends */
					LogHelper.Log("FBLoadFriendTask [" + _uid + "].. old user id re-log same old zing id => update user info and friends.");
					UserInfo user_info = _client.GetUserInstance().GetUserInfo();
					if (user_info != null && user_info.getFaceBookID().equals("null"))
					{
						update_info = true;
						LogHelper.Log("FBLoadFriendTask [" + _uid + "].. this uid is created by FB ID, now update user info.");
					}
					else
					{
						update_info = false;
						update_fb_name = false;
					}

					load_friend = true;
				}
			}
			else
			{
				String stored_fb_id = "";
				try
				{
					stored_fb_id = (String)DBConnector.GetMembaseServerForGeneralData().Get(_uid + "_" + "fb");
				}
				catch (Exception e)
				{
					stored_fb_id = "";
				}
			
				if (stored_fb_id != null && !stored_fb_id.equals("")) 
				{
					/* old user id log in new fbid, what should i do here: display notice ? update friends ? */
					_result = ReturnCode.RESPONSE_ERROR;
					update_info = false;
					load_friend = false;
					LogHelper.Log("FBLoadFriendTask [" + _uid + "].. current uid [" + _uid + "] already linked with [" + stored_fb_id + "], log in NEW id [" + _facebook_id + "] => client display errros.");
				}
				else 
				{
					/* new user id log in new zing id */
					update_info = true;
					load_friend = true;
				}
			}

			LogHelper.Log("FBLoadFriendTask [" + _uid + "].. update info: " + update_info);
			LogHelper.Log("FBLoadFriendTask [" + _uid + "].. load friend: " + load_friend);
			LogHelper.Log("FBLoadFriendTask [" + _uid + "].. update facebook name:" + update_fb_name);
			
			if (update_info)
			{
				boolean result = false;
				result = DBConnector.GetMembaseServerForGeneralData().Add(_uid + "_" + "fb", _facebook_id);
				LogHelper.Log("FBLoadFriendTask [" + _uid + "].. map uid --> fbid: " + result);

				result = DBConnector.GetMembaseServerForGeneralData().Add("fb" + "_" + _facebook_id + "_" + "u", Long.toString(_uid));
				LogHelper.Log("FBLoadFriendTask [" + _uid + "].. map fbid --> uid: " + result);

				// set and save facebook id in userInfo
				result = _client.GetUserInstance().SetAndSaveFacebookID(_facebook_id);
				LogHelper.Log("FBLoadFriendTask [" + _uid + "].. save facebook id to user info: " + result);

				update_fb_name = true;

				// give gift
				try
				{
					if (GameUtil.GetUserMisc(_uid).Get("gift_connect_facebook").equals("") && _client.GetUserInstance().GetGiftManager().LoadFromDatabase(KeyID.KEY_GIFT))
					{
						// give first time login facebook
						result = _client.GetUserInstance().GetGiftManager().AddGiftBox(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][0][DatabaseID.GIFT_INFO_NAME]),
																					   Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][0][DatabaseID.GIFT_INFO_DESCRIPTION]),
																					   Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][0][DatabaseID.GIFT_INFO_ITEMS_LIST]));
						if (result)
						{
							result = _client.GetUserInstance().GetGiftManager().SaveDataToDatabase(KeyID.KEY_GIFT);
							_client.GetUserInstance().MoveGiftBoxToMailBox();

							StringBuilder log = new StringBuilder();
							log.append(Misc.getCurrentDateTime());											//  1. log time
							log.append('\t').append(CommandID.CMD_LOAD_FRIEND_FB);							//  2. action name
							log.append('\t').append(_uid);													//  3. account name
							log.append('\t').append(_uid);													//  4. role id
							log.append('\t').append(_client.GetUserInstance().GetUserInfo().getName());		//  5. role name
							log.append('\t').append(SkyGarden._server_id);									//  6. server id
							log.append('\t').append(_client.GetUserInstance().GetUserInfo().getLevel());	//  7. user level
							log.append('\t').append(_client.GetUserInstance().GetUserInfo().GetUserIP());	//  8. user ip
							log.append('\t').append(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][0][DatabaseID.GIFT_INFO_NAME]));						//  9. user gift code
							log.append('\t').append(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][0][DatabaseID.GIFT_INFO_DESCRIPTION]));				//  10. user gift code
							log.append('\t').append(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][0][DatabaseID.GIFT_INFO_ITEMS_LIST]));					//  11. user gift code
							LogHelper.Log(LogHelper.LogType.GIFT_BOX, log.toString());
							
							GameUtil.GetUserMisc(_uid).Set("gift_connect_facebook", Misc.getCurrentDateTime());
						}

						add_gift = result;
					}
				} 
				catch (Exception e) 
				{
					LogHelper.LogException("FBLoadFriendTask.AddGift", e);
					add_gift = false;
				}

				LogHelper.Log("FBLoadFriendTask [" + _uid + "].. add gift box: " + result);

				// Log Social
				StringBuilder log = new StringBuilder();
				log.append(Misc.getCurrentDateTime());
				log.append('\t').append(_uid);
				log.append('\t').append(2); // 2 = facebook
				log.append('\t').append(_facebook_id);
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
				piglog.append('\t').append("Facebook");
				piglog.append('\t').append(_facebook_id);
				piglog.append('\t').append("");
				piglog.append('\t').append("");
				piglog.append('\t').append("");
				LogHelper.Log(LogHelper.LogType.PIG_LOG, piglog.toString());
			}
			
			if (load_friend)
			{
				_client.GetUserInstance().GetFriendManager().Clear(FriendManager.TYPE.FACEBOOK);
				String[] s = _friend_id_list.split(":");
				for (int i = 0 ; i < s.length ; i++)
				{
					String friend_uid = "";
					try
					{
						friend_uid = (String)DBConnector.GetMembaseServerForGeneralData().Get("fb" + "_" + s[i] + "_" + "u");
					}
					catch (Exception e)
					{
						LogHelper.LogException("FBLoadFriendTask", e);
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
							LogHelper.LogException("FBLoadFriendTask", e);
							friend_info = null;
						}
						
						if (friend_info == null || friend_info.length == 0)
						{
							LogHelper.Log("FBLoadFriendTask [" + _uid + "].. err! can't load user info of facebook id: " + s[i]);
						}
						else
						{
							_friend_data.addBinary(s[i] + "_" + i, friend_info);
							LogHelper.Log("FBLoadFriendTask [" + _uid + "].. loaded user info of facebook id: " + s[i]);
							
							// update in-game friend list
							UserInfo ui = new UserInfo(friend_info);
							_client.GetUserInstance().GetFriendManager().AddFriend(ui.getDeviceID(), FriendManager.TYPE.FACEBOOK);
						}
					}
					else
					{
						LogHelper.Log("FBLoadFriendTask [" + _uid + "].. err! user id of facebook id [" + s[i] + "] is null");
					}
				}
				
				// save in-game friend list
				_client.GetUserInstance().GetFriendManager().SaveFriendListToDatabase(KeyID.KEY_FRIENDS);
				
				// set fb friend list
				_client.GetUserInstance().SetFBFriendList(_friend_id_list);
			}
			
			
			if (update_fb_name) 
			{
				// save fb info
				String facebook_name = "";
				String facebook_gender = "";
				try
				{
					String[] facebook_info = Misc.GetFacebookInfo(_facebook_id).split(":");
					facebook_name = facebook_info[0];
					facebook_gender = facebook_info[1];
					LogHelper.Log("FBLoadFriendTask [" + _uid + "].. retrieve facebook name: " + facebook_name);
					LogHelper.Log("FBLoadFriendTask [" + _uid + "].. retrieve facebook gender: " + facebook_gender);
				}
				catch (Exception e)
				{
					LogHelper.LogException("FBLoadFriendTask.GetFacebookName", e);
				}
				
				_client.GetUserInstance().SetAndSaveFacebookInfo(facebook_name, facebook_gender);
				
				// only update user name if user has not had zing id
				if (_client.GetUserInstance().GetUserInfo().GetZingID().equals("null"))
				{
					boolean update_name = _client.GetUserInstance().SetAndSaveUsername(facebook_name);
					LogHelper.Log("FBLoadFriendTask [" + _uid + "].. save facebook name ["+ facebook_name +"] to user info: " + update_name);
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
		
		if (_result == ReturnCode.RESPONSE_FB_ID_ALREADY_USED)
		{
			encoder.addBinary(KeyID.KEY_USER_INFOS, ServerHandler.GetUserData(_device_id));
		}
		else
		{
			encoder.addBinary(KeyID.KEY_USER_INFOS, _client.GetUserInstance().GetUserData());
		}
		
		if (_friend_data.toByteArray().length > 0)
		{
			encoder.addBinary(KeyID.KEY_FRIEND_FB_LIST_INFO, _friend_data.toByteArray());
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
			LogHelper.Log("FBLoadFriendTask [" + _uid + "].. response to client OK.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("FBLoadFriendTask.Response", e);
		}
	}
}
