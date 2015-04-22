/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vng.skygarden.game;

import com.vng.log.LogHelper;
import com.vng.netty.ServerHandler;
import com.vng.skygarden.DBConnector;
import com.vng.util.Misc;

/**
 *
 * @author thinhnguyen
 */
public class GameUtil {
	public static UserMisc GetUserMisc(long user_id)
	{
		UserMisc misc = null;
		try
		{
			byte[] b = DBConnector.GetMembaseServer(user_id).GetRaw(user_id + "_" + "misc");
			if (b != null && b.length > 0)
			{
				misc = new UserMisc(user_id, b);
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("GetUserMisc", e);
		}

		if (misc == null)
		{
			misc = new UserMisc(user_id);
		}

		return misc;
	}
	
	public static UserInfo GetUserInfo(String device_id)
	{
		byte[] data = ServerHandler.GetUserData(device_id);
		if (data != null)
			return new UserInfo(data);
		
		return null;
	}
	
	public static UserInfo GetUserInfo(long user_id) {
        byte[] userbin = null;
        try {
            userbin = DBConnector.GetMembaseServer(user_id).GetRaw(user_id + "_" + KeyID.KEY_USER_INFOS);
        } catch (Exception e) {
            e.printStackTrace();
            userbin = null;
        }

        if (userbin == null || userbin.length == 0) {
            LogHelper.Log("GetUserInfo [" + user_id + "].. err! null data!");
            return null;
        } else {
            UserInfo userInfo = new UserInfo(userbin);
            return userInfo;
        }
    }
	
	public static boolean AddGiftToMailBox(String start, String end, UserInfo user_info, String unique_key, String gift_title, String gift_desc, String gift_list, String command_name, int server_id)
	{
		long user_id = user_info.getID();
		if (Misc.InEvent(start, end) && GetUserMisc(user_id).Get(unique_key).equals(""))
		{
			// lock mailbox
			StringBuilder lock_info = new StringBuilder();
			lock_info.append(user_id).append('_');
			lock_info.append("AddGift").append('_');
			lock_info.append(Misc.SECONDS());
			if (!DBConnector.GetMembaseServer(user_id).Add(user_id + "_" + "lock_mailbox", lock_info.toString(), 3))
			{
				LogHelper.Log("AddGift.. error: mailbox data is being locked.");
				return false;
			}
			
			MailBox mailbox = new MailBox(user_id);
			mailbox.Load();
			Mail mail = new Mail(0, gift_title, gift_desc, gift_list);
			mailbox.Add(mail);
			mailbox.Save();
			
			StringBuilder log = new StringBuilder();
			log.append(Misc.getCurrentDateTime());						//  1. log time
			log.append('\t').append(command_name);						//  2. action name
			log.append('\t').append(user_id);							//  3. account name
			log.append('\t').append(user_id);							//  4. role id
			log.append('\t').append(user_info.getName());			//  5. role name
			log.append('\t').append(server_id);								//  6. server id
			log.append('\t').append(user_info.getLevel());			//  7. user level
			log.append('\t').append(user_info.GetUserIP());			//  8. user ip
			log.append('\t').append(gift_title + "|" + unique_key);				//  9. user gift name
			log.append('\t').append(gift_desc);		//  10. user gift description
			log.append('\t').append(gift_list);				//  11. user gift items
			LogHelper.Log(LogHelper.LogType.GIFT_BOX, log.toString());
			
			// unlock mailbox
			DBConnector.GetMembaseServer(user_id).Delete(user_id + "_" + "lock_mailbox");
			
			// save unique key
			GetUserMisc(user_id).Set(unique_key, Misc.getCurrentDateTime());
			
			LogHelper.LogHappy("Add gift done, unique_key := " + unique_key);
			return true;
		}
		
		return false;
	}
}
