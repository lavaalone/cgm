package com.vng.util;

import com.vng.log.LogHelper;
import com.vng.netty.*;
import com.vng.skygarden.*;
import com.vng.skygarden.game.*;
import com.vng.db.*;

import com.vng.db.DBKeyValue;
import com.vng.log.LogHelper;
import com.vng.netty.Server;
import com.vng.skygarden.DBConnector;
import com.vng.skygarden._gen_.ProjectConfig;
import com.vng.skygarden.game.*;
import com.vng.util.*;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.text.SimpleDateFormat;
import java.util.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.*;
import java.util.concurrent.ConcurrentHashMap;

public class GMTools
{
    public static String AddEXPFreestyle(long user_id, long amount)
    {
        // valid param
        if (amount < 0 || amount >= Long.MAX_VALUE)
        {
            return "fail";
        }

		UserInfo userInfo = GetUserInfoFreestyle(user_id);

        if (userInfo == null)
        {
            return "fail";
        }

        // kick before add
        // KickUserFreestyle(device_id);

        long current_exp = userInfo.getExp();
        userInfo.setExp(current_exp + amount);
        long new_exp = userInfo.getExp();

        StringBuilder result = new StringBuilder(40);
        result.append(current_exp);
        result.append(":").append(new_exp);

        // save to db
		DBConnector.GetMembaseServerForFreestyleData().SetRaw(user_id + "_" + KeyID.KEY_USER_INFOS, userInfo.getData(true));

        return result.toString();
    }
    
	public static String AddMoneyFreeStyle(long user_id, long amount, long money_type)
    {
        // valid param
        if (amount < 0 || amount >= (Integer.MAX_VALUE/2))
        {
            return "fail";
        }

        UserInfo userInfo = GetUserInfoFreestyle(user_id);

        if (userInfo == null)
        {
			return "fail";
        }

        StringBuilder result = new StringBuilder(40);

        if (money_type == DatabaseID.DIAMOND_ID)
        {
            MoneyManager moneyManager = null;

            if (user_id >= 0)
            {
                moneyManager = new MoneyManager(Long.toString(user_id));
				
				// if (ProjectConfig.IS_SERVER_FREESTYLE == 1)
					moneyManager.SetDatabase(DBConnector.GetMembaseServerForFreestyleData());
				// else
					// moneyManager.SetDatabase(DBConnector.GetMembaseServer(user_id));
            }

            if (!moneyManager.LoadFromDatabase(KeyID.KEY_MONEY_REAL, KeyID.KEY_MONEY_TOTAL, KeyID.KEY_MONEY_BONUS))
            {
				return "fail";
            }

            int current_money = moneyManager.GetBonusMoney() + moneyManager.GetRealMoney();

            result.append(current_money);

            // add the expect amount back
            // moneyManager.IncreaseBonusMoney((int)(amount - current_money), MoneyManager.Reason.ADD_BY_GM, -1, userInfo.getName(), userInfo.getLevel(), "SERVER.GM", -1, -1, "", (int)(amount - current_money), 1);
            moneyManager.IncreaseBonusMoney((int)(amount), MoneyManager.Reason.ADD_BY_GM, -1, userInfo.getName(), userInfo.getLevel(), "SERVER.GM", -1, -1, "", (int)(amount), 1);

            result.append(":").append(moneyManager.GetBonusMoney() + moneyManager.GetRealMoney());
            result.append(":").append("diamond");

            return result.toString();
        }
        else // add gold or reputation
        {
            if (money_type == DatabaseID.GOLD_ID)
            {
                //read current gold
                result.append(userInfo.getGold());

                // set new gold
                userInfo.setGold(userInfo.getGold() + amount);

                // read new gold
                result.append(":").append(userInfo.getGold());
                result.append(":").append("gold");
            }
            else if (money_type == DatabaseID.REPUTATION_ID)
            {
                // read current reputation
                result.append(userInfo.getReputation());

                // set new reputation
                userInfo.setReputation(userInfo.getReputation() + amount);

                // read new reputation
                result.append(":").append(userInfo.getReputation());
                result.append(":").append("reputation");
            }

            // save to db
			// if (ProjectConfig.IS_SERVER_FREESTYLE == 1)
				DBConnector.GetMembaseServerForFreestyleData().SetRaw(user_id + "_" + KeyID.KEY_USER_INFOS, userInfo.getData(true));
			// else
				// DBConnector.GetMembaseServer(user_id).SetRaw(user_id + "_" + KeyID.KEY_USER_INFOS, userInfo.getData(true));

            // return result
            return result.toString();
        }
    }
	
    public static UserInfo GetUserInfoFreestyle(long user_id)
    {
        try
        {
            byte[] userbin = null;

			// if (ProjectConfig.IS_SERVER_FREESTYLE == 1)
				userbin = DBConnector.GetMembaseServerForFreestyleData().GetRaw(user_id + "_" + KeyID.KEY_USER_INFOS);
			// else
				// userbin = DBConnector.GetMembaseServer(user_id).GetRaw(user_id + "_" + KeyID.KEY_USER_INFOS);

            if (userbin == null || userbin.length == 0)
            {
                return null;
            }
            else
            {
                UserInfo userInfo = new UserInfo(userbin);
                return userInfo;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /*
	public static boolean CopyUserToFreestyle(String device_id)
    {
        return CopyUser("real", "happy", device_id, false);
    }
	
    public static boolean CopyUser(String from_db, String to_db, String device_id, boolean copy_friend)// throws Exception
    {
        DBKeyValue src_db = null;
        DBKeyValue des_db = null;

        long user_id = Misc.GetUserID(device_id);
        String key = "";
        if (user_id < 0)
        {
            LogHelper.Log("err! invalid user id = " + user_id);
            return false;
        }
        key = "" + user_id;

        // determine source db
        if (from_db.equals("real"))
            src_db = DBConnector.GetMembaseServer(user_id);
        else if (from_db.equals("happy"))
            src_db = DBConnector.GetMembaseServerForFreestyleData();

        // determine des db
        if (to_db.equals("real"))
            des_db = null; // not allow to copy inside server real
        else if (to_db.equals("happy"))
            des_db = DBConnector.GetMembaseServerForFreestyleData();

        if (src_db == null || des_db == null)
        {
            LogHelper.Log("err! cant not get DB.");
            return false;
        }

        LogHelper.Log("CopyUserToFreeStyle.. ");
        LogHelper.Log("key = " + key);
        // copy all keys from src_db to membase freestyle;

        // user info
        byte[] userbin = null;
        try
        {
            userbin = src_db.GetRaw(key + "_" + KeyID.KEY_USER_INFOS);
        }
        catch (Exception e){}

        if (userbin != null && userbin.length > 0)
        {
            boolean r = des_db.SetRaw(key + "_" + KeyID.KEY_USER_INFOS, userbin);
            LogHelper.Log("Copy user info: " + r);
        }
        else
        {
            LogHelper.Log("err! can not load user info");
        }

        // money
        MoneyManager mm = GetUserMoneyManager(device_id);
        if (mm != null)
        {
            boolean r = mm.MoveAndOverwriteToDatabase(des_db, Long.toString(user_id), KeyID.KEY_MONEY_REAL, KeyID.KEY_MONEY_TOTAL, KeyID.KEY_MONEY_BONUS, 0);
            LogHelper.Log("Copy money: " + r);
        }
        else
        {
            LogHelper.Log("err! can not load money data");
        }

        // private shop
        PrivateShopManager psm = GetPrivateShopManager(device_id);
        if (psm != null)
        {
            psm.SetDatabase(des_db);
            boolean r = psm.saveDataToDatabase(KeyID.KEY_PRIVATE_SHOP);
            LogHelper.Log("Copy private shop: " + r);
        }
        else
        {
            LogHelper.Log("err! can not load private shop");
        }

        // order
        byte[] order = null;
        try
        {
            order = src_db.GetRaw(key + "_" + KeyID.KEY_ORDER);
        }
        catch (Exception e) {}

        if (order != null && order.length > 0)
        {
            LogHelper.Log("Copy order data: " + des_db.SetRaw(key + "_" + KeyID.KEY_ORDER, order));
        }
        else
        {
            LogHelper.Log("err! can not load order data");
        }

        // friends
        String friend = "";
        try
        {
            friend = (String)src_db.Get(key + "_" + KeyID.KEY_FRIENDS);
        }
        catch (Exception e) {}

        if (friend != null)
        {
            LogHelper.Log("Copy friend data: " + des_db.Set(key + "_" + KeyID.KEY_FRIENDS, friend));

            if (copy_friend)
            {
                LogHelper.Log("friend list: " + friend);
                String[] friends = friend.split(";");

                for (String friend_id : friends)
                {
                    if (!friend_id.equals(device_id))
                        LogHelper.Log("Copy friend data: [" + friend_id + "]: " + CopyUserToFreestyle(friend_id));
                }
            }
        }
        else
        {
            LogHelper.Log("err! can not load friend list");
        }

        // newsboard
        byte[] newsboard = null;
        try
        {
            newsboard = src_db.GetRaw(key + "_" + KeyID.KEY_NEWS_BOARD);
        }
        catch (Exception e) {}

        if (newsboard != null)
        {
            LogHelper.Log("Copy newsboard data: " + des_db.SetRaw(key + "_" + KeyID.KEY_NEWS_BOARD, newsboard));
        }
        else
        {
            LogHelper.Log("err! can not load newsboard data");
        }

        // TODO: achievement

        // TODO: daily gift
		byte[] daily_gift = null;
		try
		{
			daily_gift = src_db.GetRaw(key + "_" + "dailygift");
		} catch (Exception e) {}
		
		if (daily_gift != null) {
			LogHelper.Log("Copy daily gift data: " + des_db.SetRaw(key + "_" + "dailygift", daily_gift));
		} else {
			LogHelper.Log("err! can not load daily gift data");
		}

        // session
        String session = "";
        try
        {
            session = (String)src_db.Get(key + "_" + KeyID.KEY_USER_SESSION_ID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            session = "";
        }
        LogHelper.Log("Copy session: " + des_db.Set(key + "_" + KeyID.KEY_USER_SESSION_ID, session));

        // TODO: online

        // npc private shop
        byte[] npc_pshop = null;
        try
        {
            npc_pshop = src_db.GetRaw(key + "_" + "npc_pshop");
        }
        catch (Exception e) {}

        LogHelper.Log("Copy npc private shop data: " + des_db.SetRaw(key + "_" + "npc_pshop", npc_pshop));

        // npc shop refresh time
        int npcshop_rt = -1;
        try
        {
            npcshop_rt = (int)src_db.Get(key + "_" + "npcshop_rt");
        }
        catch (Exception e) {}

        LogHelper.Log("Copy npc refresh time: " + des_db.Set(key + "_" + "npcshop_rt", npcshop_rt));

        // npc machine daily reset time
        int npc_daily_reset_time = 0;
        try
        {
            npc_daily_reset_time = (int)src_db.Get(key + "_" + "npc_machine_daily_reset_time");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            npc_daily_reset_time = -1;
        }

        if (npc_daily_reset_time > -1)
        {
            boolean r = des_db.Set(key + "_" + "npc_machine_daily_reset_time", npc_daily_reset_time);
            LogHelper.Log("Copy npc daily reset time: " + r);
        }

        // tutorial
        byte[] tutorial = null;
        try
        {
            tutorial = src_db.GetRaw(key + "_" + "tutorial");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            tutorial = null;
        }

        if (tutorial != null)
        {
            boolean r = des_db.SetRaw(key + "_" + "tutorial", tutorial);
            LogHelper.Log("Copy tutorial data: " + r);
        }
        else
        {
            LogHelper.Log("Err! null tutorial data");
        }

        // machine repair limit
        byte[] machine_repair_limit = null;
        try
        {
            machine_repair_limit = src_db.GetRaw(key + "_" + "machine_repair_limit");
        }
        catch (Exception e) {}

        if (machine_repair_limit != null)
        {
            LogHelper.Log("Copy machine repair limit: " + des_db.SetRaw(key + "_" + "machine_repair_limit", machine_repair_limit));
        }
        else
        {
            LogHelper.Log("err! can not load machine repair limit");
        }

        // user_bug_appear_ratio
        String bug_appear_ratio = "";
        try
        {
            bug_appear_ratio = (String)src_db.Get(key + "_" + "user_bug_appear_ratio");
        }
        catch (Exception e) {}

        if (bug_appear_ratio != null)
        {
            LogHelper.Log("Copy bug appear ratio: " + des_db.Set(key + "_" + "user_bug_appear_ratio", bug_appear_ratio));
        }
        else
        {
            LogHelper.Log("err! can not load bug appear ratio");
        }

        // next_time_bug_appear
        int next_time_bug_appear = -1;
        try
        {
            next_time_bug_appear = (int)src_db.Get(key + "_" + "next_time_bug_appear");
        }
        catch (Exception e) {}
        LogHelper.Log("Copy next time bug appear: " + des_db.Set(key + "_" + "next_time_bug_appear", next_time_bug_appear));

        // bug pool
        String bug_pool = "";
        try
        {
            bug_pool = (String)src_db.Get(key + "_" + "bug_pool");
        }
        catch (Exception e) {}
        if (bug_pool != null)
        {
            LogHelper.Log("Copy bug pool: " + des_db.Set(key + "_" + "bug_pool", bug_pool));
        }
        else
        {
            LogHelper.Log("err! empty bug pool");
        }

        // garden_total_appraisal
        int appraisal = -1;
        try
        {
            appraisal = (int)src_db.Get(key + "_" + "garden_total_appraisal");
        }
        catch (Exception e) {}
        LogHelper.Log("Copy garden appraisal: " + des_db.Set(key + "_" + "garden_total_appraisal", appraisal));

        byte[] gift_code = null;
        try
        {
            gift_code = src_db.GetRaw(key + "_" + "gift_code_enter");
        }
        catch (Exception e) {}
        if (gift_code != null)
        {
            LogHelper.Log("Copy gift code: " + des_db.SetRaw(key + "_" + "gift_code_enter", gift_code));
        }
        else
        {
            LogHelper.Log("err! can not load gift code");
        }

        // floor
        for (int i = 0; i < DatabaseID.FLOOR_MAX; i++)
        {
            byte[] floor = null;
            try
            {
                floor = src_db.GetRaw(key + "_" + KeyID.KEY_FLOORS + i);
            }
            catch (Exception e) {}

            if (floor != null && floor.length > 0)
            {
                boolean r = des_db.SetRaw(key + "_" + KeyID.KEY_FLOORS + i, floor);
                LogHelper.Log("Copy data floor " + i + ": " + r);
            }
            else
            {
                break;
            }
        }

        // machine
        for (int i = 0; i < DatabaseID.FLOOR_MAX; i++)
        {
            byte[] machine = null;
            try
            {
                machine = src_db.GetRaw(key + "_" +  KeyID.KEY_MACHINES + i);
            }
            catch (Exception e) {}

            if (machine != null && machine.length > 0)
            {
                boolean r = des_db.SetRaw(key + "_" + KeyID.KEY_MACHINES + i, machine);
                LogHelper.Log("Copy data machine " + i + ": " + r);
            }
            else
            {
                break;
            }
        }

        // machine durability
        for (int i = 0; i < DatabaseID.FLOOR_MAX; i++)
        {
            byte[] machine_dur = null;
            try
            {
                machine_dur = src_db.GetRaw(key + "_" + KeyID.KEY_MACHINES_DURABILITY + i);
            }
            catch (Exception e) {}

            if (machine_dur != null && machine_dur.length > 0)
            {
                boolean r = des_db.SetRaw(key + "_" + KeyID.KEY_MACHINES_DURABILITY + i, machine_dur);
                LogHelper.Log("Copy data machine durability " + i + ": " + r);
            }
            else
            {
                break;
            }
        }

        // npc machine durability
        for (int i = 0; i < DatabaseID.FLOOR_MAX; i++)
        {
            byte[] b = null;
            try
            {
                b = src_db.GetRaw(key + "_" + "npc_machine_durability_" + i);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                b = null;
            }

            if (b != null && b.length > 0)
            {
                boolean r = des_db.SetRaw(key + "_" + "npc_machine_durability_" + i, b);
                LogHelper.Log("Copy npc machine durability " + i + ": " + r);
            }
        }

        // stock
        for (int i = 0; i < DatabaseID.STOCK_MAX; i++)
        {
            byte[] stock = null;
            try
            {
                stock = src_db.GetRaw(key + "_" + KeyID.KEY_STOCKS + i);
            }
            catch (Exception e) {}

            if (stock != null && stock.length > 0)
            {
                boolean r = des_db.SetRaw(key + "_" + KeyID.KEY_STOCKS + i, stock);
                LogHelper.Log("Copy data stock " + i + ": " + r);
            }
        }

        // facebook
        String fbid = "";
        try
        {
            fbid = (String)DBConnector.GetMembaseServerForGeneralData().Get(key + "_" + "fb");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fbid = "";
        }

        if (fbid != null && !fbid.equals(""))
        {
            des_db.Set(key + "_" + "fb", fbid);

            String uid = "";
            try
            {
                uid = (String)DBConnector.GetMembaseServerForGeneralData().Get("fb" + "_" + fbid + "_" + "u");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                uid = "";
            }

            if (uid != null && !uid.equals(""))
            {
                des_db.Set("fb" + "_" + fbid + "_" + "u", uid);
                LogHelper.Log("Copy key facebook id: true");
            }
        }
        else
        {
            LogHelper.Log("can't find linked fbid");
        }

        // key id
        byte[] data = null;

        try
        {
            data = DBConnector.GetMembaseServerForGeneralData().GetRaw(device_id + "_" + KeyID.KEY_USER_ID);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            data = null;
        }

        if (data == null || data.length == 0)
        {
            LogHelper.Log("err! can't find key user id");
        }
        else
        {
            LogHelper.Log("Copy key user id: " + des_db.SetRaw(device_id + "_" + KeyID.KEY_USER_ID, data));
        }

        LogHelper.Log("CopyUser.. done");

        return true;
    }	
	
    public static MoneyManager GetUserMoneyManager(String device_id)
    {
        try
        {
            long user_id = Misc.GetUserID(device_id);

            MoneyManager mm = null;

            if (user_id < 0)
            {
                mm = new MoneyManager(device_id);
                mm.SetDatabase(DBConnector.GetMembaseServerForTemporaryData());
            }
            else
            {
                mm = new MoneyManager(Long.toString(user_id));
                mm.SetDatabase(DBConnector.GetMembaseServer(user_id));
            }

            boolean ls = mm.LoadFromDatabase(KeyID.KEY_MONEY_REAL, KeyID.KEY_MONEY_TOTAL, KeyID.KEY_MONEY_BONUS);

            if (!ls)
            {
                return null;
            }
            else
            {
                return mm;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
	
    public static PrivateShopManager GetPrivateShopManager(String device_id)
    {
        try
        {
            long user_id = Misc.GetUserID(device_id);

            if (user_id < 0)
            {
                LogHelper.Log("err! unstable user does have private shop to load");
                return null;
            }
            else
            {
                PrivateShopManager PShopMgr = new PrivateShopManager(Long.toString(user_id));
                PShopMgr.SetDatabase(DBConnector.GetMembaseServer(user_id));
                boolean r = PShopMgr.loadFromDatabase(KeyID.KEY_PRIVATE_SHOP);

                if (r)
                {
                    return PShopMgr;
                }
                else
                {
                    LogHelper.Log("err! can not load user private shop");
                    return null;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
	*/
}