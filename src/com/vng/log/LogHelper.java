package com.vng.log;

import java.io.*;
import java.util.*;
import java.text.*;
import org.apache.log4j.Logger;

import com.vng.skygarden._gen_.*;
import com.vng.log.*;
import com.vng.skygarden.SkyGarden;
import com.vng.util.*;
import com.vng.skygarden.game.*;

public class LogHelper
{
	public static final int		MINOR_ERROR = 1;
	public static final int		MEDIUM_ERROR = 2;
	public static final int		SERIOUS_ERROR = 3;
	
	final static boolean USE_LOG_FILE_LOCAL = (ProjectConfig.RUN_LOCAL == 1 || ProjectConfig.IS_SERVER_FREESTYLE == 1 || ProjectConfig.IS_SERVER_RANKING == 1);
	
	final static boolean CONSOLE_OUTPUT = ProjectConfig.RUN_LOCAL == 1;
	
	public static void Log(String content)
	{
		if (CONSOLE_OUTPUT)
		{
			System.out.println(content);
		}
		
		Logger.getLogger("ALL_ACTION").info(content);
	}
	
	public static void LogHappy(String content)
	{
		if (CONSOLE_OUTPUT)
		{
			System.out.println(content);
		}
		
		if (ProjectConfig.IS_SERVER_FREESTYLE == 1)
			Logger.getLogger("ALL_ACTION").info(content);
	}
	
	public static void Log(LogType type, String content)
	{
		switch (type)
		{
			case LOGIN:
				Logger.getLogger("LOGIN").info(content);
				break;
			case LOGOUT:
				Logger.getLogger("LOGOUT").info(content);
				break;
			case DOWNLOAD:
				Logger.getLogger("DOWNLOAD").info(content);
				break;
			case REGISTER:
				Logger.getLogger("REGISTER").info(content);
				break;
			case PAYING:
				if (ProjectConfig.IS_SERVER_LOGIC == 1 || ProjectConfig.IS_SERVER_PAYMENT == 1 || ProjectConfig.IS_SERVER_ZALO_LOGIC == 1 || ProjectConfig.IS_SERVER_ZALO_PAYMENT == 1
						|| ProjectConfig.IS_SERVER_GLOBAL_SING == 1 || ProjectConfig.IS_SERVER_GLOBAL_PAYMENT == 1/* || ProjectConfig.IS_SERVER_PIG == 1*/)
				{
					Logger.getLogger("PAYING").info(content);
				}
				Logger.getLogger("ALL_ACTION").info(content);
				break;
			case SPENT_MONEY:
				Logger.getLogger("SPENT_MONEY").info(content);
				break;
			case SPENT_GOLD:
				Logger.getLogger("SPENT_GOLD").info(content);
				break;
			case SPENT_REPUTATION:
				Logger.getLogger("SPENT_REPUTATION").info(content);
				break;
			case SELLING_ITEM:
				Logger.getLogger("SELLING_ITEM").info(content);
				break;
			case RECEIVING_ITEM:
				Logger.getLogger("RECEIVING_ITEM").info(content);
				break;
			case UPGRADE_ITEM:
				Logger.getLogger("UPGRADE_ITEM").info(content);
				break;
			case NEWSBOARD:
				Logger.getLogger("NEWSBOARD").info(content);
				break;
			case SYSTEM:
				Logger.getLogger("F_SYS").info(content);
				break;
			case IFRS:
				if (ProjectConfig.IS_SERVER_LOGIC == 1 || ProjectConfig.IS_SERVER_PAYMENT == 1 || ProjectConfig.IS_SERVER_ZALO_LOGIC == 1 || ProjectConfig.IS_SERVER_ZALO_PAYMENT == 1
						|| ProjectConfig.IS_SERVER_GLOBAL_SING == 1 || ProjectConfig.IS_SERVER_GLOBAL_PAYMENT == 1/* || ProjectConfig.IS_SERVER_PIG == 1*/)
				{
					Logger.getLogger("IFRS").info(content);
				}
				Logger.getLogger("ALL_ACTION").info(content);
				break;
			case LEVELUP:
				Logger.getLogger("LEVELUP").info(content);
				break;
			case SOCIAL:
				Logger.getLogger("SOCIAL").info(content);
				break;
			case TUTORIAL:
				Logger.getLogger("TUTORIAL").info(content);
				break;
			case SPENT_ITEM:
				Logger.getLogger("SPENT_ITEM").info(content);
				break;
			case SNAPSHOT:
				Logger.getLogger("SNAPSHOT").info(content);
				break;
			case DATA_TRAFFIC:
				Logger.getLogger("DATA_TRAFFIC").info(content);
				break;
			case DROP_ITEM:
				Logger.getLogger("DROP_ITEM").info(content);
				break;
			case MONITOR_SERVER:
				Logger.getLogger("MONITOR_SERVER").info(content);
				break;
			case MONITOR_TASKQUEUE:
				Logger.getLogger("MONITOR_TASKQUEUE").info(content);
				break;
			case GIFT_CODE:
				Logger.getLogger("GIFT_CODE").info(content);
				break;
			case GIFT_BOX:
				Logger.getLogger("GIFT_BOX").info(content);
				break;
			case HACK:
				Logger.getLogger("HACK").info(content);
				break;
			case CRASH:
				Logger.getLogger("CRASH").info(content);
				break;
			case LIKE_GARDENT:
				Logger.getLogger("LIKE_GARDENT").info(content);
				break;
			case ERROR:
				Logger.getLogger("ERROR").info(content);
				break;
			case USER_DATA:
				Logger.getLogger("USER_DATA").info(content);
				break;
			case USER_DATA_FILE:
				Logger.getLogger("USER_DATA_FILE").info(content);
				break;
			case TRACKING_ACTION:
				Logger.getLogger("TRACKING_ACTION").info(content);
				break;
			case OFFER_NEW:
				Logger.getLogger("OFFER_NEW").info(content);
				break;
			case OFFER_ACCEPT:
				Logger.getLogger("OFFER_ACCEPT").info(content);
				break;
			case PIG_LOG:
				Logger.getLogger("PIG_LOG").info(content);
				break;
			default:
				Logger.getLogger("NO_TAG").info(content);
				break;
		}
		
		if (CONSOLE_OUTPUT)
		{
			System.out.println(content);
		}
		
		if (USE_LOG_FILE_LOCAL)
		{
			Logger.getLogger("ALL_ACTION").info(content);
		}
	}
	
//	public static void LogError(int level, String content)
//	{
//		Logger.getLogger("ERROR").info("ERROR LEVEL " + level + ": " + content);
//		
//		if (CONSOLE_OUTPUT)
//		{
//			System.out.println("ERROR LEVEL " + level + ": " + content);
//		}
//		
//		if (USE_LOG_FILE_LOCAL)
//		{
//			Logger.getLogger("ALL_ACTION").info(content);
//		}
//	}
	
	public static void LogException(String content, Throwable ex)
	{
		Logger.getLogger("EXCEPTION").info(content + ":" + ex.toString());
		
		if (CONSOLE_OUTPUT)
		{
			System.out.println(content);
			ex.printStackTrace();
		}
		
		try
		{
			File file = new File("./../log/exception.log");
			FileOutputStream fos = new FileOutputStream(file, true);
			PrintStream ps = new PrintStream(fos);

			StringBuilder s = new StringBuilder();
			s.append('\n').append(Misc.getCurrentDateTime());
			ps.println(s.toString());

			ex.printStackTrace(ps);
			ps.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.getLogger("EXCEPTION_FILE").info(content + ":" + ex.toString());
		}
	}
	
//	public static void LogDecreaseMoney(MoneyType type, String uid, int amount, int after_change)
//	{
//		StringBuilder str = new StringBuilder("DECREASE_");
//		str.append(type);
//		str.append(",").append(uid);
//		str.append(",").append(amount);
//		str.append(",").append(after_change);
//		
//		// Logger.getLogger("CONSUME_MONEY").info(str.toString());
//		Log(str.toString());
//		
//		if (CONSOLE_OUTPUT)
//		{
//			System.out.println(str.toString());
//		}
//		
//		if (USE_LOG_FILE_LOCAL)
//		{
//			Logger.getLogger("ALL_ACTION").info(str.toString());
//		}
//	}
	
	public static void LogDecreaseMoneyException(MoneyType type, String uid, int amount, Throwable ex)
	{
		StringBuilder str = new StringBuilder("DECREASE_");
		str.append(type).append("_EXCEPTION");
		str.append(",").append(uid);
		str.append(",").append(amount);
		str.append(",").append(ex.toString());
		
		// Logger.getLogger("CONSUME_MONEY").info(str.toString());
		Log(str.toString());
		
		if (CONSOLE_OUTPUT)
		{
			System.out.println(str.toString());
		}
		
		Logger.getLogger("ERROR").info(str.toString());
	}
	
//	public static void LogIncreaseMoney(MoneyType type, String uid, int before_change, int amount, int after_change, String reason)
//	{
//		StringBuilder str = new StringBuilder("INCREASE_");
//		str.append(type);
//		str.append(",").append(uid);
//		str.append(",").append(before_change);
//		str.append(",").append(amount);
//		str.append(",").append(after_change);
//		str.append(",").append(reason);
//		
//		Logger.getLogger("ALL_ACTION").info(str.toString());
//		
//		if (CONSOLE_OUTPUT)
//		{
//			System.out.println(str.toString());
//		}
//		
//		if (USE_LOG_FILE_LOCAL)
//		{
//			Logger.getLogger("ALL_ACTION").info(str.toString());
//		}
//	}
	
	public static void LogIncreaseMoneyException(MoneyType type, String uid, int amount, String reason, Throwable ex)
	{
		StringBuilder str = new StringBuilder("DECREASE_");
		str.append(type).append("_EXCEPTION");
		str.append(",").append(uid);
		str.append(",").append(amount);
		str.append(",").append(reason);
		str.append(",").append(ex.toString());
		
		Logger.getLogger("ALL_ACTION").info(str.toString());
		
		if (CONSOLE_OUTPUT)
		{
			System.out.println(str.toString());
		}
		
		Logger.getLogger("ERROR").info(str.toString());
	}
	
//	public static void LogWriteDB(String msg)
//	{
//		Logger.getLogger("WRITE_DB").info(msg);
//	}
	
	public static void logLogIn(UserInfo user, MoneyManager money, String ip, int server_id, String session_id, String device_name, String device_fw, int return_code, boolean useWifi, String carrier_name, String distributor, String version, int friend_num, long process_time)
	{
		long user_id = user.getID();
		String user_name = user.getName();
		int user_level = user.getLevel();
		long user_exp = user.getExp();
		String birthday = user.GetFacebookBirthday();
	
		int money_real = money.GetRealMoney();
		int money_bonus = money.GetBonusMoney();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Misc.getCurrentDateTime());		//  1. thoi gian dang nhap
		sb.append('\t').append(user_id);			//  2. ten tai khoan dang nhap
		sb.append('\t').append(user_id);			//  3. id cua role dang nhap
		sb.append('\t').append(user_name);			//  4. ten role dang nhap		
		sb.append('\t').append(ip);					//  5. ip login v4 cua gamer
		sb.append('\t').append(server_id);			//  6. id cua server dang nhap
		sb.append('\t').append(user_level);			//  7. level cua gamer dang nhap
		sb.append('\t').append(user_exp);			//  8. diem kinh nghiem cua gamer
		sb.append('\t').append(money_real + money_bonus);			//  9. so game coin ton
		sb.append('\t').append(money_real);			//  10. so game coin tu nguon nap xu hien tai
		sb.append('\t').append(money_bonus);		// 11. so game coin tu nguon promotion
		sb.append('\t').append(session_id);			// 12. sessionID cua gamer tren server
		sb.append('\t').append(device_name);			// 13. device id
		sb.append('\t').append(return_code);		// 14. 0: success, <> 0: error code
		sb.append('\t').append(device_fw);			// 15. device fw
		sb.append('\t').append(useWifi ? "Wifi" : "3G");			// 16. wifi or 3g
		sb.append('\t').append(carrier_name);			// 17. carrier name
		sb.append('\t').append(distributor);			// 18. apk distributor
		sb.append('\t').append(user.GetFacebookGender());			// 19. facebook gender
		sb.append('\t').append(version);						// 20. apk version
		sb.append('\t').append(friend_num);						// 21. friend number
		sb.append('\t').append(birthday);						// 22. birthday
		sb.append('\t').append(process_time);					// 23. process time (milliseconds)
		sb.append('\t').append(user.getRefCode());				// 24. reference code
		LogHelper.Log(LogHelper.LogType.LOGIN, sb.toString());
		
		if (USE_LOG_FILE_LOCAL)
		{
			Logger.getLogger("ALL_ACTION").info(sb.toString());
		}
	}
	
	public static void logLogOut(UserInfo user, MoneyManager money, String ip, int server_id, String session_id)
	{
		long user_id = user.getID();
		String user_name = user.getName();
		int user_level = user.getLevel();
		long user_exp = user.getExp();
		long user_gold = user.getGold();

		int money_real = money.GetRealMoney();
		int money_bonus = money.GetBonusMoney();
		
		int play_time = (int)(System.currentTimeMillis()/1000) - user.GetCurrentLoginTime();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Misc.getCurrentDateTime());		//  1. thoi gian dang xuat
		sb.append('\t').append(user_id);			//  2. ten tai khoan dang xuat
		sb.append('\t').append(user_id);			//  3. id cua role dang xuat
		sb.append('\t').append(user_name);			//  4. ten role dang xuat		
		sb.append('\t').append(ip);					//  5. ip login v4 cua gamer
		sb.append('\t').append(server_id);			//  6. id cua server dang xuat
		sb.append('\t').append(user_level);			//  7. level cua gamer dang xuat
		sb.append('\t').append(user_exp);			//  8. diem kinh nghiem cua gamer
		sb.append('\t').append(money_real + money_bonus);			//  9. so game coin ton
		sb.append('\t').append(money_real);			//  9. so game coin tu nguon nap xu hien tai
		sb.append('\t').append(money_bonus);		// 10. so game coin tu nguon promotion
		sb.append('\t').append(session_id);			// 11. sessionID cua gamer tren server
		sb.append('\t').append(play_time);			// 12. thoi gian choi game tu luc dang nhap
		sb.append('\t').append(0);					// 13. 0: success, <> 0: error code
		sb.append('\t').append(user_gold);			// 14. gold
		sb.append('\t').append(SkyGarden._server_id);	// 15. server dang xuat
		
		LogHelper.Log(LogHelper.LogType.LOGOUT, sb.toString());
		
		if (USE_LOG_FILE_LOCAL)
		{
			Logger.getLogger("ALL_ACTION").info(sb.toString());
		}
	}

	public static void logDataTraffic(long user_id, String user_name, int user_level, String ip, int server_id, String session_id, long total_data_in, long total_data_out, long total_data, long play_time)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(Misc.getCurrentDateTime());		//  1. thoi gian dang xuat
		sb.append('\t').append("DataTraffic");		//  2. ten tai khoan dang xuat
		sb.append('\t').append(user_id);			//  3. ten tai khoan dang xuat
		sb.append('\t').append(user_id);			//  4. id cua role dang xuat
		sb.append('\t').append(user_name);			//  5. id cua role dang xuat
		sb.append('\t').append(server_id);			//  6. id cua server dang nhap
		sb.append('\t').append(user_level);			//  7. id cua server dang nhap
		sb.append('\t').append(total_data_in);		//  8. total data in
		sb.append('\t').append(total_data_out);		//  9. total data out
		sb.append('\t').append(total_data);			//  10. data traffic cua session
		sb.append('\t').append(play_time);			//  10. data traffic cua session
		
		LogHelper.Log(LogHelper.LogType.DATA_TRAFFIC, sb.toString());
		
		if (USE_LOG_FILE_LOCAL)
		{
			Logger.getLogger("ALL_ACTION").info(sb.toString());
		}
	}
	
	public static void logCrash(long user_id, 
								String user_name, 
								int user_level, 
								String ip,
								int server_id, 
								String device_name, 
								String device_firmware, 
								String distributor, 
								boolean useWifi,
								String user_action)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(Misc.getCurrentDateTime());		//  1. log time
		sb.append('\t').append("Crash");			//  2. action name
		sb.append('\t').append(user_id);			//  3. account name
		sb.append('\t').append(user_id);			//  4. role id
		sb.append('\t').append(user_name);			//  5. role name
		sb.append('\t').append(server_id);			//  6. server id
		sb.append('\t').append(user_level);			//  7. user level
		
		sb.append('\t').append(ip);					//  8. user ip
		sb.append('\t').append(device_name);		//  9. device name
		sb.append('\t').append(device_firmware);	//  10. device firmware
		sb.append('\t').append(distributor);		//  11. distributor
		sb.append('\t').append(useWifi ? 1 : 0);	//  12. wifi or 3g
		sb.append('\t').append(user_action);		//  13. user last action
		
		LogHelper.Log(LogHelper.LogType.CRASH, sb.toString());
		
		if (USE_LOG_FILE_LOCAL)
		{
			Logger.getLogger("ALL_ACTION").info(sb.toString());
		}
	}
	
	public static void logGiftCode(long user_id, 
								String user_name, 
								int user_level, 
								String ip,
								int server_id, 
								String device_name, 
								String device_firmware, 
								String user_action)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(Misc.getCurrentDateTime());		//  1. log time
		sb.append('\t').append("GiftCode");			//  2. action name
		sb.append('\t').append(user_id);			//  3. account name
		sb.append('\t').append(user_id);			//  4. role id
		sb.append('\t').append(user_name);			//  5. role name
		sb.append('\t').append(server_id);			//  6. server id
		sb.append('\t').append(user_level);			//  7. user level
		
		sb.append('\t').append(ip);					//  8. user ip
		sb.append('\t').append(device_name);		//  9. device name
		sb.append('\t').append(device_firmware);	//  10. device firmware
		sb.append('\t').append(user_action);		//  11. user gift code
		
		LogHelper.Log(LogHelper.LogType.GIFT_CODE, sb.toString());
		
		if (USE_LOG_FILE_LOCAL)
		{
			Logger.getLogger("ALL_ACTION").info(sb.toString());
		}
	}
	
	public static void logLikeGarden(long user_id, 
								String user_name, 
								int user_level, 
								String ip,
								int server_id, 
								long friend_id, 
								long liked_count)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(Misc.getCurrentDateTime());		//  1. log time
		sb.append('\t').append("GiftCode");			//  2. action name
		sb.append('\t').append(user_id);			//  3. account name
		sb.append('\t').append(user_id);			//  4. role id
		sb.append('\t').append(user_name);			//  5. role name
		sb.append('\t').append(server_id);			//  6. server id
		sb.append('\t').append(user_level);			//  7. user level
		
		sb.append('\t').append(ip);					//  8. user ip
		sb.append('\t').append(friend_id);			//  9. friend_id
		sb.append('\t').append(liked_count);		//  10. liked_count
		
		LogHelper.Log(LogHelper.LogType.LIKE_GARDENT, sb.toString());
		
		if (USE_LOG_FILE_LOCAL)
		{
			Logger.getLogger("ALL_ACTION").info(sb.toString());
		}
	}
	
	public enum MoneyType
	{
		XU,
		XU_THUONG
	}
	
	public enum LogType
	{
		LOGIN,
		LOGOUT,
		DOWNLOAD,
		REGISTER,
		PAYING,
		SPENT_MONEY,
//		BUY_GAME_ITEM,
		SPENT_GOLD,
		SPENT_REPUTATION,
		SELLING_ITEM,
		RECEIVING_ITEM,
		UPGRADE_ITEM,
//		USER_ACTION,
		ERROR,
		EXCEPTION,
//		WRITE_DB,
		NEWSBOARD,
		SYSTEM,
		IFRS,
		LEVELUP,
		SOCIAL,
		TUTORIAL,
		SPENT_ITEM,
		SNAPSHOT,
		DATA_TRAFFIC,
		DROP_ITEM,
		NO_TAG,
		GIFT_CODE,
		GIFT_BOX,
		MONITOR_SERVER,
		MONITOR_TASKQUEUE,
		HACK,
		CRASH,
		LIKE_GARDENT,
		USER_DATA,
		USER_DATA_FILE,
		TRACKING_ACTION,
		OFFER_NEW,
		OFFER_ACCEPT,
		PIG_LOG
	}
}