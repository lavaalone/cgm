package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import com.vng.skygarden.DBConnector;
import com.vng.skygarden._gen_.ProjectConfig;

import java.util.concurrent.atomic.*;
import java.util.*;

public class UserInfo
{
	private long id;
	private String device_id;
	private String email = "null";
	private String imei = "null";
	
	// facebook
	private String facebook_id = "null";
	private String facebook_name = "null";
	private String facebook_gender = "null";
	private boolean facebook_like = false;
	private String facebook_long_lived_token = "null";
	private long facebook_issue_token_date;
	private long facebook_expire_token_date;
	private String facebook_birthday = "null";
	
	// zing
	private String zing_id = "null";
	private String zing_name = "null";
	private String zing_display_name = "null";
	private String zing_avatar = "null";
	
	// zalo
	private String zalo_id = "null";
	private String zalo_name = "null";
	private String zalo_display_name = "null";
	private String zalo_avatar = "null";

	// ..
	
	private String name;
	private byte vip;
	private long exp;
	private AtomicLong gold = new AtomicLong(0L);
	private AtomicLong reputation = new AtomicLong(0L);
	private AtomicInteger floorNum = new AtomicInteger(1);
	private AtomicInteger level = new AtomicInteger(1);
	private boolean load_result = true;
	private boolean is_banned = false;
	
	private int last_login;
	private int current_login;
	private int last_time_levelup = -1;
	private int last_server_id;

	private long revenu_date_max = 0;
	private long revenu_date = 0;
	private long revenu_week_max = 0;
	private long revenu_week = 0;
	private long revenu_month_max = 0;
	private long revenu_month = 0;
	
	private short order_num_date_max = 0;
	private short order_num_date = 0;	
	
	final boolean LOG_USER = !true;
	
	// for log payment purpose
	private String current_ip = "0.0.0.0";
	
	private boolean changed = false;
	
	private boolean usedAmulet = false;
	
	private byte[] firstPlant = new byte[Server.s_globalDB[DatabaseID.SHEET_SEED].length];
	
	private boolean received_gift_alpha_test = false;
	private boolean received_gift_8_3 = false;
	private boolean received_gift_downtime_13_03 = false;
	private boolean received_gift_new_version_28_03 = false;
	private boolean received_gift_new_version_04_04 = false;
	private boolean received_gift_new_version_23_04 = false;
	private boolean received_gift_new_version_10_06 = false;
	private boolean received_gift_new_version_23_06 = false;
	private boolean received_gift_new_version_26_06 = false;
	private boolean received_gift_new_version_10_07 = false;
	private boolean received_gift_new_version_23_07 = false;
	private boolean received_gift_new_version_19_08 = false;
	private boolean received_gift_new_version_11_09 = false;
	private boolean received_gift_compensation_11_09 = false;
	private boolean received_gift_update_16_09 = false;
	private boolean received_gift_feature_mailbox = false;
	private boolean received_gift_tom = false;
	
	// gcm
	private boolean registered_gcm_android = false;
	private boolean registered_gcm_ios = false;
	
	// android id & advertising id
	private String android_id = "null";
	private String advertising_id = "null";
	private String pig_id = "null";
	private String device_os = "null";
	private String ref_code = "null";
	private String register_date = "null";
	
	// local ranking
	private long _ranking_accumulation = -1;
	private int _last_time_update_accumulation = 0;
	
	// airship point
	private long _airship_point = 0;
	
	private String _received_gift_cross_install = "";
	private String _event_total_num = ""; // example: event_mid_autumn_festival:20;event_merry_christmast:50
	private int _gift_box_received_event_mid_autumn = -1;
	private int _gift_box_received_event_export = -1;
	private int _gift_box_received_event_halloween = -1;
	private int _gift_box_received_event_20_11 = -1;
	private int _gift_box_received_event_xmas_mini = -1;
	private int _gift_box_received_event_upgrade_pot = -1;
	private int _gift_box_received_event_xmas_2014 = -1;
	private int _gift_box_received_event_xmas_tree = -1;
	private int _gift_box_received_event_order_jan_2015 = -1;
	private int _gift_box_received_event_lunar_year_2015 = -1;
	private int _gift_box_received_event_8_3_2015 = -1;
	private int _gift_box_received_event_birthday_2015 = -1;
	
	public UserInfo(long uid)
	{
		try
		{
			id = uid;
			device_id = "null";
			email = "null";
			imei = "null";
			
			facebook_id = "null";
			facebook_name = "null";
			facebook_gender = "null";
			facebook_long_lived_token = "null";
			facebook_issue_token_date = -1;
			facebook_expire_token_date = -1;
			facebook_birthday = "null";
			
			zing_id = "null";
			zing_name = "null";
			zing_display_name = "null";
			zing_avatar = "null";

			zalo_id = "null";
			zalo_name = "null";
			zalo_display_name = "null";
			zalo_avatar = "null";
			
			name = "SGM" + Misc.SECONDS();
			vip = 0;
			exp = 0;
			gold.set(Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_USER][0][DatabaseID.USER_GOLD]));
			reputation.set(Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_USER][0][DatabaseID.USER_REPUTAION]));
			
			last_login = -1;
			current_login = -1;
			last_time_levelup = -1;
			last_server_id = -1;
			
			revenu_date_max = 0;
			revenu_date = 0;
			revenu_week_max = 0;
			revenu_week = 0;
			revenu_month_max = 0;
			revenu_month = 0;
			
			order_num_date_max = 0;
			order_num_date = 0;
			
			current_ip = "0.0.0.0";
			
			changed = true;
			
			for (int i = 0; i < firstPlant.length; i++) 
				firstPlant[i] = 0;
			
			received_gift_alpha_test = false;
			received_gift_8_3 = false;
			
			registered_gcm_android = false;
			registered_gcm_ios = false;
			android_id = "";
			advertising_id = "";
			pig_id = "";
			device_os = "";
			ref_code = "null";
			
			_ranking_accumulation = -1;
		}
		catch (Exception e)
		{
			LogHelper.LogException("UserInfo", e);
		}
	}
	
	public UserInfo(byte[] bin_db)
	{
		FBEncrypt user = new FBEncrypt();
		user.decode(bin_db, true);
		
		id = user.getLong(KeyID.KEY_USER_ID);
		device_id = user.getString(KeyID.KEY_DEVICE_ID);
		facebook_id = user.getString(KeyID.KEY_FACEBOOK_ID);
		name = user.getString(KeyID.KEY_USER_NAME);
		vip = user.getByte(KeyID.KEY_USER_VIP);
		exp = user.getLong(KeyID.KEY_USER_EXP);
		gold.set(user.getLong(KeyID.KEY_USER_GOLD));
		reputation.set(user.getLong(KeyID.KEY_USER_REPUTATION));
		last_login = user.getInt(KeyID.KEY_USER_LAST_LOGIN);
		current_login = user.getInt(KeyID.KEY_USER_CURRENT_LOGIN);
		last_server_id = user.getInt("last_server_id");
		
		if (!level.compareAndSet(1, user.getShort(KeyID.KEY_USER_LEVEL)))
		{
			LogHelper.Log("UserInfo.. can not load user level.");
			load_result = false;
			return;
		}
		
		if (!floorNum.compareAndSet(1, user.getShort(KeyID.KEY_USER_FLOOR)))
		{
			LogHelper.Log("UserInfo.. can not load user floor number.");
			load_result = false;
			return;
		}
		
		if (user.hasKey(KeyID.KEY_EMAIL))
		{
			email = user.getString(KeyID.KEY_EMAIL);
		}
		
		if (user.hasKey(KeyID.KEY_DEVICE_IMEI))
		{
			imei = user.getString(KeyID.KEY_DEVICE_IMEI);
		}
		
		if (user.hasKey(KeyID.KEY_USER_LAST_LEVELUP))
		{
			last_time_levelup = user.getInt(KeyID.KEY_USER_LAST_LEVELUP);
		}
		
		if (user.hasKey(KeyID.KEY_USER_REVENU_DATE_MAX)) 	revenu_date_max = user.getLong(KeyID.KEY_USER_REVENU_DATE_MAX);
		if (user.hasKey(KeyID.KEY_USER_REVENU_DATE)) 		revenu_date = user.getLong(KeyID.KEY_USER_REVENU_DATE);
		if (user.hasKey(KeyID.KEY_USER_REVENU_WEEK_MAX)) 	revenu_week_max = user.getLong(KeyID.KEY_USER_REVENU_WEEK_MAX);
		if (user.hasKey(KeyID.KEY_USER_REVENU_WEEK)) 		revenu_week = user.getLong(KeyID.KEY_USER_REVENU_WEEK);
		if (user.hasKey(KeyID.KEY_USER_REVENU_MONTH_MAX)) 	revenu_month_max = user.getLong(KeyID.KEY_USER_REVENU_MONTH_MAX);
		if (user.hasKey(KeyID.KEY_USER_REVENU_MONTH)) 		revenu_month = user.getLong(KeyID.KEY_USER_REVENU_MONTH);
		
		if (user.hasKey(KeyID.KEY_USER_ORDER_NUM_DATE_MAX)) order_num_date_max = user.getShort(KeyID.KEY_USER_ORDER_NUM_DATE_MAX);
		if (user.hasKey(KeyID.KEY_USER_ORDER_NUM_DATE)) 	order_num_date = user.getShort(KeyID.KEY_USER_ORDER_NUM_DATE);
		
		if (user.hasKey(KeyID.KEY_ZING_ID))
		{
			zing_id = user.getString(KeyID.KEY_ZING_ID);
		}
		
		if (user.hasKey(KeyID.KEY_ZING_NAME))
		{
			zing_name = user.getString(KeyID.KEY_ZING_NAME);
		}
		
		if (user.hasKey(KeyID.KEY_ZING_DISPLAY_NAME))
		{
			zing_display_name = user.getString(KeyID.KEY_ZING_DISPLAY_NAME);
		}
		
		if (user.hasKey(KeyID.KEY_ZING_AVATAR))
		{
			zing_avatar = user.getString(KeyID.KEY_ZING_AVATAR);
		}
		
		if (user.hasKey(KeyID.KEY_ZALO_ID))
		{
			zalo_id = user.getString(KeyID.KEY_ZALO_ID);
		}
		
		if (user.hasKey(KeyID.KEY_ZALO_NAME))
		{
			zalo_name = user.getString(KeyID.KEY_ZALO_NAME);
		}
		
		if (user.hasKey(KeyID.KEY_ZALO_DISPLAY_NAME))
		{
			zalo_display_name = user.getString(KeyID.KEY_ZALO_DISPLAY_NAME);
		}
		
		if (user.hasKey(KeyID.KEY_ZALO_AVATAR))
		{
			zalo_avatar = user.getString(KeyID.KEY_ZALO_AVATAR);
		}

		if (user.hasKey(KeyID.KEY_FACEBOOK_NAME)) {
			facebook_name = user.getString(KeyID.KEY_FACEBOOK_NAME);
		}
		
		if (user.hasKey(KeyID.KEY_FACEBOOK_GENDER)) {
			facebook_gender = user.getString(KeyID.KEY_FACEBOOK_GENDER);
		}
		
		if (user.hasKey(KeyID.KEY_FACEBOOK_LONG_LIVED_TOKEN)) {
			facebook_long_lived_token = user.getString(KeyID.KEY_FACEBOOK_LONG_LIVED_TOKEN);
		}
		
		if (user.hasKey(KeyID.KEY_FACEBOOK_TOKEN_ISSUE_DATE)) {
			facebook_issue_token_date = user.getLong(KeyID.KEY_FACEBOOK_TOKEN_ISSUE_DATE);
		}
		
		if (user.hasKey(KeyID.KEY_FACEBOOK_TOKEN_EXPIRE_DATE)) {
			facebook_expire_token_date = user.getLong(KeyID.KEY_FACEBOOK_TOKEN_EXPIRE_DATE);
		}
		
		if (user.hasKey(KeyID.KEY_FACEBOOK_BIRTHDAY)) {
			facebook_birthday = user.getString(KeyID.KEY_FACEBOOK_BIRTHDAY);
		}
		
		if (user.hasKey(KeyID.KEY_FACEBOOK_LIKE)) {
			facebook_like = user.getBoolean(KeyID.KEY_FACEBOOK_LIKE);
		}
		
		if (user.hasKey(KeyID.KEY_USER_IP))
		{
			current_ip = user.getString(KeyID.KEY_USER_IP);
		}
		
		if (user.hasKey(KeyID.KEY_USER_USED_AMULET))
		{
			usedAmulet = user.getBoolean(KeyID.KEY_USER_USED_AMULET);
		}
		
		if (user.hasKey(KeyID.KEY_FIRST_PLANT))
		{
			firstPlant = user.getByteArray(KeyID.KEY_FIRST_PLANT);
		}
		else
		{
			for (int i = 0; i < firstPlant.length; i++) 
				firstPlant[i] = 0;
		}
		
		if (user.hasKey("received_gift_alpha_test"))
			received_gift_alpha_test = user.getBoolean("received_gift_alpha_test");
		
		if (user.hasKey("received_gift_8_3"))
			received_gift_8_3 = user.getBoolean("received_gift_8_3");
		
		if (user.hasKey("received_gift_downtime_13_03"))
			received_gift_downtime_13_03 = user.getBoolean("received_gift_downtime_13_03");
		
		if (user.hasKey(KeyID.KEY_REGISTERED_GCM_ANDROID)) {
			registered_gcm_android = user.getBoolean(KeyID.KEY_REGISTERED_GCM_ANDROID);
		}
		
		if (user.hasKey(KeyID.KEY_REGISTER_GCM_IOS)) {
			registered_gcm_ios = user.getBoolean(KeyID.KEY_REGISTER_GCM_IOS);
		}
		
		if (user.hasKey(KeyID.KEY_ANDROID_ID))
		{
			android_id = user.getString(KeyID.KEY_ANDROID_ID);
		}
		
		if (user.hasKey(KeyID.PIG_ID))
		{
			pig_id = user.getString(KeyID.PIG_ID);
		}
		
		if (user.hasKey(KeyID.DEVICE_OS))
		{
			device_os = user.getString(KeyID.DEVICE_OS);
		}
		
		if (user.hasKey(KeyID.KEY_REFERENCE_CODE))
		{
			ref_code = user.getString(KeyID.KEY_REFERENCE_CODE);
		}
		
		if (user.hasKey(KeyID.KEY_ADVERTISING_ID))
		{
			advertising_id = user.getString(KeyID.KEY_ADVERTISING_ID);
		}
		
		if (user.hasKey("received_gift_new_version_28_03")) {
			received_gift_new_version_28_03 = user.getBoolean("received_gift_new_version_28_03");
		}
		
		if (user.hasKey("received_gift_new_version_04_04")) {
			received_gift_new_version_04_04 = user.getBoolean("received_gift_new_version_04_04");
		}
		
		if (user.hasKey("received_gift_new_version_23_04")) {
			received_gift_new_version_23_04 = user.getBoolean("received_gift_new_version_23_04");
		}
		
		if (user.hasKey("received_gift_new_version_10_06")) {
			received_gift_new_version_10_06 = user.getBoolean("received_gift_new_version_10_06");
		}
		
		if (user.hasKey("received_gift_new_version_23_06")) {
			received_gift_new_version_23_06 = user.getBoolean("received_gift_new_version_23_06");
		}
		
		if (user.hasKey("received_gift_new_version_26_06")) {
			received_gift_new_version_26_06 = user.getBoolean("received_gift_new_version_26_06");
		}
		
		if (user.hasKey("received_gift_new_version_10_07")) {
			received_gift_new_version_10_07 = user.getBoolean("received_gift_new_version_10_07");
		}
		
		if (user.hasKey("received_gift_new_version_23_07")) {
			received_gift_new_version_23_07 = user.getBoolean("received_gift_new_version_23_07");
		}
		
		if (user.hasKey("received_gift_new_version_19_08")) {
			received_gift_new_version_19_08 = user.getBoolean("received_gift_new_version_19_08");
		}
		
		if (user.hasKey("received_gift_new_version_11_09")) {
			received_gift_new_version_11_09 = user.getBoolean("received_gift_new_version_11_09");
		}
		
		if (user.hasKey("received_gift_compensation_11_09")) {
			received_gift_compensation_11_09 = user.getBoolean("received_gift_compensation_11_09");
		}
		
		if (user.hasKey("received_gift_update_16_09")) {
			received_gift_update_16_09 = user.getBoolean("received_gift_update_16_09");
		}
		
		if (user.hasKey("received_gift_tom")) {
			received_gift_tom = user.getBoolean("received_gift_tom");
		}
		
		if (user.hasKey("received_gift_feature_mailbox")) {
			received_gift_feature_mailbox = user.getBoolean("received_gift_feature_mailbox");
		}
		
		if (user.hasKey(KeyID.KEY_RANKING_ACCUMULATION)) {
			_ranking_accumulation = user.getLong(KeyID.KEY_RANKING_ACCUMULATION);
		}
		
		if (user.hasKey("last_time_update_accumulation")) {
			_last_time_update_accumulation = user.getInt("last_time_update_accumulation");
		}
		
		if (user.hasKey(KeyID.KEY_RECEIVED_GIFT_INSTALL_APP)) {
			_received_gift_cross_install = user.getString(KeyID.KEY_RECEIVED_GIFT_INSTALL_APP);
		}
		
		if (user.hasKey(KeyID.KEY_EVENT_CURRENT_TOTAL_NUM)) {
			_event_total_num = user.getString(KeyID.KEY_EVENT_CURRENT_TOTAL_NUM);
		}
		
		if (user.hasKey(KeyID.KEY_NUM_GIFT_BOX_EVENT_RECEIVED)) {
			_gift_box_received_event_mid_autumn = user.getInt(KeyID.KEY_NUM_GIFT_BOX_EVENT_RECEIVED);
		}
		
		if (user.hasKey("_gift_box_received_event_20_11")) {
			_gift_box_received_event_20_11 = user.getInt("_gift_box_received_event_20_11");
		}
		
		if (user.hasKey("_gift_box_received_event_xmas_mini")) {
			_gift_box_received_event_xmas_mini = user.getInt("_gift_box_received_event_xmas_mini");
		}
		
		if (user.hasKey("_gift_box_received_event_export")) {
			_gift_box_received_event_export = user.getInt("_gift_box_received_event_export");
		}
		
		if (user.hasKey(KeyID.KEY_NUM_GIFT_BOX_EVENT_HALLOWEEN)) {
			_gift_box_received_event_halloween = user.getInt(KeyID.KEY_NUM_GIFT_BOX_EVENT_HALLOWEEN);
		}
		
		if (user.hasKey(KeyID.KEY_EVENT_XMAS_2014)) {
			_gift_box_received_event_xmas_2014 = user.getInt(KeyID.KEY_EVENT_XMAS_2014);
		}
		
		if (user.hasKey("_gift_box_received_event_upgrade_pot")) {
			_gift_box_received_event_upgrade_pot = user.getInt("_gift_box_received_event_upgrade_pot");
		}
		
		if (user.hasKey("_gift_box_received_event_order_jan_2015")) {
			_gift_box_received_event_order_jan_2015 = user.getInt("_gift_box_received_event_order_jan_2015");
		}
		
		if (user.hasKey("_gift_box_received_event_lunar_year_2015")) {
			_gift_box_received_event_lunar_year_2015 = user.getInt("_gift_box_received_event_lunar_year_2015");
		}
		
		if (user.hasKey("_gift_box_received_event_8_3_2015")) {
			_gift_box_received_event_8_3_2015 = user.getInt("_gift_box_received_event_8_3_2015");
		}
		
		if (user.hasKey(KeyID.KEY_EVENT_XMAS_TREE_2014)) {
			_gift_box_received_event_xmas_tree = user.getInt(KeyID.KEY_EVENT_XMAS_TREE_2014);
		}
		
		if (user.hasKey("_gift_box_received_event_birthday_2015")) {
			_gift_box_received_event_birthday_2015 = user.getInt("_gift_box_received_event_birthday_2015");
		}
		
		if (user.hasKey(KeyID.KEY_REGISTER_DATE)) {
			register_date = user.getString(KeyID.KEY_REGISTER_DATE);
		}
		
		is_banned = ReadBannedStatus();
	}
	
	public boolean isLoadSuccess()
	{
		return load_result;
	}
	
	public byte[] getData(boolean save)
	{
		FBEncrypt user = new FBEncrypt();
		user.addLong(KeyID.KEY_USER_ID, id);
		user.addString(KeyID.KEY_DEVICE_ID, device_id);
		user.addString(KeyID.KEY_FACEBOOK_ID, facebook_id);
		user.addString(KeyID.KEY_USER_NAME, name);
		user.addByte(KeyID.KEY_USER_VIP, vip);
		user.addShort(KeyID.KEY_USER_LEVEL, level.shortValue());
		user.addLong(KeyID.KEY_USER_EXP, exp);
		user.addLong(KeyID.KEY_USER_GOLD, gold.get());
		user.addLong(KeyID.KEY_USER_REPUTATION, reputation.get());
		user.addShort(KeyID.KEY_USER_FLOOR, floorNum.shortValue());
		user.addInt(KeyID.KEY_USER_LAST_LOGIN, last_login);
		user.addInt("last_server_id", last_server_id);
		user.addInt(KeyID.KEY_USER_CURRENT_LOGIN, current_login);
		user.addString(KeyID.KEY_DEVICE_IMEI, imei);
		user.addString(KeyID.KEY_EMAIL, email);
		user.addInt(KeyID.KEY_USER_LAST_LEVELUP, last_time_levelup);
		
		user.addLong(KeyID.KEY_USER_REVENU_DATE_MAX, revenu_date_max);
		user.addLong(KeyID.KEY_USER_REVENU_DATE, revenu_date);
		user.addLong(KeyID.KEY_USER_REVENU_WEEK_MAX, revenu_week_max);
		user.addLong(KeyID.KEY_USER_REVENU_WEEK, revenu_week);
		user.addLong(KeyID.KEY_USER_REVENU_MONTH_MAX, revenu_month_max);
		user.addLong(KeyID.KEY_USER_REVENU_MONTH, revenu_month);
		
		user.addShort(KeyID.KEY_USER_ORDER_NUM_DATE_MAX, order_num_date_max);
		user.addShort(KeyID.KEY_USER_ORDER_NUM_DATE, order_num_date);
		
		// zing info
		user.addString(KeyID.KEY_ZING_ID, zing_id);
		user.addString(KeyID.KEY_ZING_NAME, zing_name);
		user.addString(KeyID.KEY_ZING_DISPLAY_NAME, zing_display_name);
		user.addString(KeyID.KEY_ZING_AVATAR, zing_avatar);

		// zalo info
		user.addString(KeyID.KEY_ZALO_ID, zalo_id);
		user.addString(KeyID.KEY_ZALO_NAME, zalo_name);
		user.addString(KeyID.KEY_ZALO_DISPLAY_NAME, zalo_display_name);
		user.addString(KeyID.KEY_ZALO_AVATAR, zalo_avatar);

		user.addString(KeyID.KEY_FACEBOOK_NAME, facebook_name);
		user.addString(KeyID.KEY_USER_IP, current_ip);
		user.addString(KeyID.KEY_FACEBOOK_GENDER, facebook_gender);
		user.addString(KeyID.KEY_FACEBOOK_LONG_LIVED_TOKEN, facebook_long_lived_token);
		user.addLong(KeyID.KEY_FACEBOOK_TOKEN_ISSUE_DATE, facebook_issue_token_date);
		user.addLong(KeyID.KEY_FACEBOOK_TOKEN_EXPIRE_DATE, facebook_expire_token_date);
		user.addString(KeyID.KEY_FACEBOOK_BIRTHDAY, facebook_birthday);
		user.addBoolean(KeyID.KEY_FACEBOOK_LIKE, facebook_like);
		
//		user.addBoolean("fixed_bug_1901", fixed_bug_1901);
		user.addBoolean(KeyID.KEY_USER_USED_AMULET, usedAmulet);
		try
		{
			// for (int i = 0; i < firstPlant.length; i++)
				// LogHelper.Log("firstPlant[" + i + "] = " + firstPlant[i]);
			
			user.addArray(KeyID.KEY_FIRST_PLANT, firstPlant);
		}
		catch (Exception e)
		{
			LogHelper.LogException("UserInfo.getData", e);
		}
		
		user.addBoolean("received_gift_alpha_test", received_gift_alpha_test);
		user.addBoolean("received_gift_8_3", received_gift_8_3);
		user.addBoolean("received_gift_downtime_13_03", received_gift_downtime_13_03);
		user.addBoolean("received_gift_new_version_28_03", received_gift_new_version_28_03);
		user.addBoolean("received_gift_new_version_04_04", received_gift_new_version_04_04);
		user.addBoolean("received_gift_new_version_23_04", received_gift_new_version_23_04);
		user.addBoolean("received_gift_new_version_10_06", received_gift_new_version_10_06);
		user.addBoolean("received_gift_new_version_23_06", received_gift_new_version_23_06);
		user.addBoolean("received_gift_new_version_26_06", received_gift_new_version_26_06);
		user.addBoolean("received_gift_new_version_10_07", received_gift_new_version_10_07);
		user.addBoolean("received_gift_new_version_23_07", received_gift_new_version_23_07);
		user.addBoolean("received_gift_new_version_19_08", received_gift_new_version_19_08);
		user.addBoolean("received_gift_new_version_11_09", received_gift_new_version_11_09);
		user.addBoolean("received_gift_compensation_11_09", received_gift_compensation_11_09);
		user.addBoolean("received_gift_update_16_09", received_gift_update_16_09);
		user.addBoolean("received_gift_tom", received_gift_tom);
		user.addBoolean("received_gift_feature_mailbox", received_gift_feature_mailbox);
		
		user.addBoolean(KeyID.KEY_REGISTERED_GCM_ANDROID, registered_gcm_android);
		user.addBoolean(KeyID.KEY_REGISTER_GCM_IOS, registered_gcm_ios);
		user.addString(KeyID.KEY_ANDROID_ID, android_id);
		user.addString(KeyID.PIG_ID, pig_id);
		user.addString(KeyID.DEVICE_OS, device_os);
		user.addString(KeyID.KEY_REFERENCE_CODE, ref_code);
		user.addString(KeyID.KEY_ADVERTISING_ID, advertising_id);
		user.addLong(KeyID.KEY_RANKING_ACCUMULATION, _ranking_accumulation);
		user.addInt("last_time_update_accumulation", _last_time_update_accumulation);
		user.addString(KeyID.KEY_RECEIVED_GIFT_INSTALL_APP, _received_gift_cross_install);
		user.addString(KeyID.KEY_EVENT_CURRENT_TOTAL_NUM, _event_total_num);
		if (save)
		{
			user.addInt(KeyID.KEY_NUM_GIFT_BOX_EVENT_RECEIVED, _gift_box_received_event_mid_autumn);
		}
		else
		{
			user.addInt(KeyID.KEY_NUM_GIFT_BOX_EVENT_RECEIVED, _gift_box_received_event_20_11);
		}
		user.addInt("_gift_box_received_event_20_11", _gift_box_received_event_20_11);
		user.addInt("_gift_box_received_event_xmas_mini", _gift_box_received_event_xmas_mini);
		user.addInt("_gift_box_received_event_export", _gift_box_received_event_export);
		user.addInt("_gift_box_received_event_upgrade_pot", _gift_box_received_event_upgrade_pot);
		user.addInt("_gift_box_received_event_order_jan_2015", _gift_box_received_event_order_jan_2015);
		user.addInt("_gift_box_received_event_lunar_year_2015", _gift_box_received_event_lunar_year_2015);
		user.addInt("_gift_box_received_event_8_3_2015", _gift_box_received_event_8_3_2015);
		user.addInt(KeyID.KEY_NUM_GIFT_BOX_EVENT_HALLOWEEN, _gift_box_received_event_halloween);
		user.addInt("_gift_box_received_event_halloween", _gift_box_received_event_halloween);
		user.addInt(KeyID.KEY_EVENT_XMAS_2014, _gift_box_received_event_xmas_2014);
		user.addInt(KeyID.KEY_EVENT_XMAS_TREE_2014, _gift_box_received_event_xmas_tree);
		user.addInt("_gift_box_received_event_birthday_2015", _gift_box_received_event_birthday_2015);
		user.addString(KeyID.KEY_REGISTER_DATE, register_date);
		
		if (save)
		{
			changed = false;
		}
		
		if (LOG_USER)
		{
			LogUser();
		}
		
		return user.toByteArray();
	}
	
	public void LogUser()
	{
		StringBuilder l = new StringBuilder();
		l.append("user_info");
		l.append('\t').append(device_id);
		l.append('\t').append(current_ip);
		l.append('\t').append(id);
		l.append('\t').append(facebook_id);
		l.append('\t').append(facebook_name);
		l.append('\t').append(facebook_gender);
		l.append('\t').append(facebook_long_lived_token);
		l.append('\t').append(facebook_issue_token_date);
		l.append('\t').append(facebook_expire_token_date);
		l.append('\t').append(facebook_birthday);
		l.append('\t').append(facebook_like);
		l.append('\t').append(zing_id);
		l.append('\t').append(zing_name);
		l.append('\t').append(zing_display_name);
		l.append('\t').append(zing_avatar);
		l.append('\t').append(zalo_id);
		l.append('\t').append(zalo_name);
		l.append('\t').append(zalo_display_name);
		l.append('\t').append(zalo_avatar);
		l.append('\t').append(name);
		l.append('\t').append(email);
		l.append('\t').append(imei);
		l.append('\t').append(vip);
		l.append('\t').append(level.shortValue());
		l.append('\t').append(exp);
		l.append('\t').append(gold.get());
		l.append('\t').append(reputation.get());
		l.append('\t').append(floorNum.shortValue());
		l.append('\t').append(last_login);
		l.append('\t').append(last_server_id);
		l.append('\t').append(current_login);
		l.append('\t').append(last_time_levelup);

		l.append('\t').append(revenu_date_max);
		l.append('\t').append(revenu_date);
		l.append('\t').append(revenu_week_max);
		l.append('\t').append(revenu_week);
		l.append('\t').append(revenu_month_max);
		l.append('\t').append(revenu_month);
		
		l.append('\t').append(order_num_date_max);
		l.append('\t').append(order_num_date);
		
		LogHelper.Log(l.toString());
	}

	public long expRequiredToLevelUp()
	{
		long exp = 0;
		try
		{
			int lvl = level.get() + 1;
			
			if (lvl < Server.s_globalDB[DatabaseID.SHEET_USER_LEVEL].length)
			{
				exp = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_USER_LEVEL][lvl][DatabaseID.USER_EXP_LVL]);
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("expRequiredToLevelUp.. can not read data",e);
			
			exp = 0;
		}
		return exp;
	}
	
	public void displayDataPackage()
	{
		FBEncrypt user = new FBEncrypt(getData(false));
		user.displayDataPackage();
	}
	
	public long getID()
	{
		return id;
	}

	public void setID(long id)
	{
		this.id = id;
		changed = true;
	}

	public String getDeviceID()
	{
		return device_id;
	}

	public void setDeviceID(String _device_id)
	{
		this.device_id = _device_id;
		
		// cheat vip
		if (_device_id.equals(KeyID.NPC_NAME))
		{
			byte v = 1;
			setVIP(v);
		}
		
		changed = true;
	}

	public String getFaceBookID()
	{
		return facebook_id;
	}
	
	public void setFaceBookID(String _facebook_id)
	{
		this.facebook_id = _facebook_id;
		changed = true;
	}
	
	public boolean GetFacebookLike()
	{
		return facebook_like;
	}
	
	public void SetFacebookLike(boolean b)
	{
		this.facebook_like = b;
		changed = true;
	}
	
	public String GetFacebookName()
	{
		return facebook_name;
	}
	
	public void SetFacebookName(String _facebook_name)
	{
		this.facebook_name = _facebook_name;
		changed = true;
	}
	
	public String GetFacebookGender()
	{
		return facebook_gender;
	}
	
	public String GetFacebookLongLivedToken()
	{
		return facebook_long_lived_token;
	}
	
	public long GetFacebooTokenkExpiredTime() {
		return this.facebook_expire_token_date;
	}
	
	public void SetFacebookGender(String _facebook_gender)
	{
		this.facebook_gender = _facebook_gender;
		changed = true;
	}
	
	public void SetFacebookBirthday(String s) {
		this.facebook_birthday = s;
		changed = true;
	}
	
	public String GetFacebookBirthday() {
		return this.facebook_birthday;
	}
	
	public void SetFacebookLongLivedToken(String token)
	{
		this.facebook_long_lived_token = token;
		this.facebook_issue_token_date = System.currentTimeMillis();
		this.facebook_expire_token_date = facebook_issue_token_date + (56L * 24 * 60 * 60 * 1000);
		changed = true;
	}
	
	public void SetZingInfo(String _zing_id, String _zing_name, String _zing_display_name, String _zing_avatar)
	{
		this.zing_id = _zing_id;
		this.zing_name = _zing_name;
		this.zing_display_name = _zing_display_name;
		this.zing_avatar = _zing_avatar;
		changed = true;
	}
	
	public String GetZingDisplayName()
	{
		return zing_display_name;
	}
	
	public String GetZingAvatar()
	{
		return zing_avatar;
	}
	
	public String GetZingID()
	{
		return zing_id;
	}

	public void SetZaloInfo(String _zalo_id, String _zalo_name, String _zalo_display_name, String _zalo_avatar)
	{
		this.zalo_id = _zalo_id;
		this.zalo_name = _zalo_name;
		this.zalo_display_name = _zalo_display_name;
		this.zalo_avatar = _zalo_avatar;
		changed = true;
	}
	
	public String GetZaloDisplayName()
	{
		return zalo_display_name;
	}
	
	public String GetZaloAvatar()
	{
		return zalo_avatar;
	}
	
	public String GetZaloID()
	{
		return zalo_id;
	}
	
	public void SetIMEI(String s)
	{
		this.imei = s;
		changed = true;
	}
	
	public String GetIMEI()
	{
		return this.imei;
	}
	
	public void SetEmail(String s)
	{
		this.email = s;
		changed = true;
	}
	
	public String GetEmail()
	{
		return this.email;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
		changed = true;
	}
	
	public byte getVIP()
	{
		return vip;
	}

	public void setVIP(byte _vip)
	{
		this.vip = _vip;
		changed = true;
	}

	public short getLevel()
	{
		return level.shortValue();
	}
	
	public void increaseLevel()
	{
		level.incrementAndGet();
		changed = true;
	}
	
	public void setLevel(int new_level)
	{
		level.getAndSet(new_level);
		changed = true;
	}

	public long getExp()
	{
		return exp;
	}

	public void setExp(long _exp)
	{
		this.exp = _exp;
		changed = true;
	}
	
	/*
	* Receive EXP as gift.
	*/
	public void setExp(long _exp,
							int command_id,
							int item_type, 
							int item_id, 
							String item_name,
							int item_num)
	{
		this.exp = _exp;
		changed = true;
		
		StringBuilder log = new StringBuilder();
		log.append(Misc.getCurrentDateTime());						//  1. thoi gian tieu tien
		log.append('\t').append(Misc.getActionName(command_id));	//  2. hanh dong cua gamer
		log.append('\t').append(id);								//  3. ten tai khoan
		log.append('\t').append(id);								//  4. id cua role nap tien
		log.append('\t').append(getName());							//  5. ten role
		log.append('\t').append(0);									//  6. id cua server
		log.append('\t').append(getLevel());						//  7. level cua gamer
		log.append('\t').append(DatabaseID.IT_MONEY + "_" + DatabaseID.EXP_ID);	//  8. id item nhan
		log.append('\t').append(_exp - this.exp);						//  9. so luong item
		log.append('\t').append(1);									// 10. result
		log.append('\t').append("");								// 11 . description as list
		LogHelper.Log(LogHelper.LogType.RECEIVING_ITEM, log.toString());
	}

	public long getGold()
	{
		return gold.get();
	}

	public boolean setGold(long _gold)
	{
		changed = true;
		return gold.compareAndSet(gold.get(), _gold);
	}

	public boolean setGold(long gold_new, 
							int command_id,
							int item_type, 
							int item_id, 
							String item_name,
							int item_num)
	{
		long old_gold = gold.get();
		boolean result = gold.compareAndSet(gold.get(), gold_new);
		changed = true;
		
		StringBuilder log = new StringBuilder();
		log.append(Misc.getCurrentDateTime());						//  1. thoi gian tieu tien
		log.append('\t').append(Misc.getActionName(command_id));	//  2. hanh dong cua gamer
		log.append('\t').append(id);								//  3. ten tai khoan
		log.append('\t').append(id);								//  4. id cua role nap tien
		log.append('\t').append(getName());							//  5. ten role
		log.append('\t').append(0);									//  6. id cua server
		log.append('\t').append(getLevel());						//  7. level cua gamer
		log.append('\t').append(DatabaseID.IT_MONEY + "_" + DatabaseID.GOLD_ID);	//  8. id item nhan
		
		if (gold.get() > old_gold) // receive gold
		{
			log.append('\t').append(gold.get() - old_gold);				//  9. so luong item
			log.append('\t').append(result == true ? 1 : 0);			// 10. result
			log.append('\t').append("");								// 11. description
			LogHelper.Log(LogHelper.LogType.RECEIVING_ITEM, log.toString());
		}
		else // reduce gold
		{
			log.append('\t').append(old_gold - gold.get());				//  9. so luong item
			if (command_id == CommandID.CMD_BUY_IBSHOP_PACKAGE)
			{
				log.append('\t').append(item_name + "_" + item_type + "_" + item_id);		// 10. id item nhan duoc
			}
			else
			{
				if (item_type != -1 && item_id != -1) // spend gold and receive item
				{
					log.append('\t').append(item_type + "_" + item_id);
				}
				else // spend gold and not receie item
				{
					log.append('\t').append("");
				}
			}
			log.append('\t').append(item_num);							// 11. so luong item nhan duoc
			log.append('\t').append(result == true ? 1 : 0);			// 12. result
			log.append('\t').append("");
			log.append('\t').append(id + "_" + System.currentTimeMillis());
			LogHelper.Log(LogHelper.LogType.SPENT_GOLD, log.toString());
		}

		return result;
	}
	
	public long getReputation()
	{
		return reputation.get();
	}

	public boolean setReputation(long _reputation)
	{
		changed = true;
		return reputation.compareAndSet(reputation.get(), _reputation);
	}

	public boolean setReputation(long reputation_new, 
									int command_id, 
									int item_type, 
									int item_id, 
									String item_name, 
									int item_num)
	{
		long old_reputation = reputation.get();
		boolean result = reputation.compareAndSet(reputation.get(), reputation_new);
		changed = true;
		
		StringBuilder log = new StringBuilder();
		log.append(Misc.getCurrentDateTime());						//  1. thoi gian tieu tien
		log.append('\t').append(Misc.getActionName(command_id));	//  2. hanh dong cua gamer
		log.append('\t').append(id);								//  3. ten tai khoan
		log.append('\t').append(id);								//  4. id cua role nap tien
		log.append('\t').append(getName());							//  5. ten role
		log.append('\t').append(0);									//  6. id cua server
		log.append('\t').append(getLevel());						//  7. level cua gamer
		log.append('\t').append(DatabaseID.IT_MONEY + "_" + DatabaseID.REPUTATION_ID);	//  8. loai item
		
		if (reputation.get() > old_reputation) // receive reputation
		{
			log.append('\t').append(reputation.get() - old_reputation);	//  9. so luong
			log.append('\t').append(result == true ? 1 : 0);			// 10. result
			log.append('\t').append("");								// 11. description
			LogHelper.Log(LogHelper.LogType.RECEIVING_ITEM, log.toString());
		}
		else // consume reputation
		{
			log.append('\t').append(old_reputation - reputation.get());	//  9. so luong
			if (command_id == CommandID.CMD_BUY_IBSHOP_PACKAGE)
			{
				log.append('\t').append(item_name + "_" + item_type + "_" + item_id);		// 10. id item nhan duoc
			}
			else
			{
				if (item_type != -1 && item_id != -1) // spend reputation and receive item
				{
					log.append('\t').append(item_type + "_" + item_id);
				}
				else // spend reputation and not receive item
				{
					log.append('\t').append("");
				}
			}
			log.append('\t').append(item_num);							// 11. so luong item nhan duoc
			log.append('\t').append(result == true ? 1 : 0);			// 12. result
			log.append('\t').append("");
			log.append('\t').append(id + "_" + System.currentTimeMillis());
			LogHelper.Log(LogHelper.LogType.SPENT_REPUTATION, log.toString());
		}

		return result;
	}
	
	public short getTotalFloor()
	{
		return floorNum.shortValue();
	}
	
	public void increaseFloorNumber()
	{
		floorNum.incrementAndGet();
		changed = true;
	}
	
	public void setFloorNumber(int value)
	{
		floorNum.set(value);
		changed = true;
	}
	
	public int GetLastLoginTime()
	{
		return last_login;
	}
	
	public void SetLastLoginTime(int v)
	{
		this.last_login = v;
		changed = true;
	}
	
	public void SetLastServerID(int v)
	{
		this.last_server_id = v;
		changed = true;
	}
	
	public int GetLastServerID()
	{
		return last_server_id;
	}
	
	public int GetCurrentLoginTime()
	{
		return current_login;
	}
	
	public void SetCurrentLoginTime(int v)
	{
		this.current_login = v;
	}
	
	public void SetTimeLevelUp(int v)
	{
		this.last_time_levelup = v;
		changed = true;
	}
	
	public int GetTimeLevelUp()
	{
		return this.last_time_levelup;
	}
	
	public boolean updateRevenu(long itemPrice)
	{
		boolean has_record = false;
		
		revenu_date += itemPrice;
		
		if (revenu_date > revenu_date_max)
		{
			revenu_date_max = revenu_date;
			has_record = true;
		}
		
		changed = true;
		return has_record;
	}
	
	public void updateRevenu()
	{
		boolean reset_revenu_date = GetLastLoginTime() < Misc.GetServerCurrentResetTime();
		if (reset_revenu_date)
		{
			resetRevenuDate();
		}
		
		boolean reset_revenu_week = GetLastLoginTime() < Misc.GetServerWeekCurrentResetTime();
		if (reset_revenu_week)
		{
			resetRevenuWeek();
		}

		boolean reset_revenu_month = GetLastLoginTime() < Misc.GetServerMonthCurrentResetTime();
		if (reset_revenu_month)
		{
			resetRevenuMonth();
		}
		
		changed = true;
	}
	
	public void resetRevenuDate()
	{
		revenu_week += revenu_date;
		if (revenu_week > revenu_week_max) revenu_week_max = revenu_week;
		
		revenu_date = 0;
		
		changed = true;
	}

	public void resetRevenuWeek()
	{
		revenu_month += revenu_week;
		if (revenu_month > revenu_month_max) revenu_month_max = revenu_month;
		
		revenu_week = 0;
		
		changed = true;
	}

	public void resetRevenuMonth()
	{
		revenu_month = 0;
		
		changed = true;
	}

	public boolean increaseDeliveryOrderNum()
	{
		boolean has_record = false;
		
		order_num_date++;
		
		if (order_num_date > order_num_date_max)
		{
			order_num_date_max = order_num_date;
			has_record = true;
		}
		
		changed = true;
		
		return has_record;
	}
	
	public void updateOrderNum()
	{
		boolean reset_order_num = GetLastLoginTime() < Misc.GetServerCurrentResetTime();
		if (reset_order_num)
		{
			resetOrderNumDate();
		}
		changed = true;
	}
	
	public void resetOrderNumDate()
	{
		order_num_date = 0;
		changed = true;
	}
	
	public void SetUserIP(String _ip)
	{
		this.current_ip = _ip;
		changed = true;
	}
	
	public String GetUserIP()
	{
		return this.current_ip;
	}
	
	public boolean isChanged()
	{
		return changed;
	}
	
	public boolean GetUsedAmulet()
	{
		return usedAmulet;
	}
	
	public void SetUsedAmulet(boolean b)
	{
		this.usedAmulet = b;
		changed = true;
	}
	
	public boolean isFirstPlant(int plant_id)
	{
		if (plant_id >= firstPlant.length)
			return false;
		
		return (firstPlant[plant_id] == 0);
	}
	
	public void setUsedFirstPlant(int plant_id)
	{
		if (plant_id >= firstPlant.length)
			return;
		
		firstPlant[plant_id] = 1;
		changed = true;
	}
	
	public boolean isReceivedAlphaGift()
	{
		return received_gift_alpha_test;
	}
	
	public void setReceivedAlphaGift()
	{
		received_gift_alpha_test = true;
		changed = true;
	}
	
	public boolean isReceivedGift83()
	{
		return received_gift_8_3;
	}
	
	public void setReceivedGift83()
	{
		received_gift_8_3 = true;
		changed = true;
	}
	
	public boolean isReceivedGiftDowntime1303()
	{
		return received_gift_downtime_13_03;
	}
	
	public void setReceivedGift1303()
	{
		received_gift_downtime_13_03 = true;
		changed = true;
	}
	
	public boolean isReceivedGiftNewVersion2803() {
		return received_gift_new_version_28_03;
	}
	
	public void setReceviedGiftNewVersion2803() {
		received_gift_new_version_28_03 = true;
		changed = true;
	}
	
	public boolean isReceivedGiftNewVersion0404() {
		return received_gift_new_version_04_04;
	}
	
	public void setReceivedGiftNewVersion0404() {
		received_gift_new_version_04_04 = true;
		changed = true;
	}
	
	public boolean isReceivedGiftNewVersion2304() {
		return received_gift_new_version_23_04;
	}
	
	public void setReceivedGiftNewVersion2304() {
		received_gift_new_version_23_04 = true;
		changed = true;
	}
	
	public boolean isReceivedGiftNewVersion1006() {
		return received_gift_new_version_10_06;
	}
	
	public void setReceivedGiftNewVersion1006() {
		received_gift_new_version_10_06 = true;
		changed = true;
	}
	
	public boolean isReceivedGiftNewVersion2306() {
		return received_gift_new_version_23_06;
	}
	
	public void setReceivedGiftNewVersion2306() {
		received_gift_new_version_23_06 = true;
		changed = true;
	}
	
	public boolean isReceivedGiftNewVersion2606() {
		return received_gift_new_version_26_06;
	}
	
	public void setReceivedGiftNewVersion2606() {
		received_gift_new_version_26_06 = true;
		changed = true;
	}
	
	public boolean isReceivedGiftNewVersion1007() {
		return received_gift_new_version_10_07;
	}
	
	public void setReceivedGiftNewVersion1007() {
		received_gift_new_version_10_07 = true;
		changed = true;
	}
	
	public boolean isReceivedGiftNewVersion2307() {
		return received_gift_new_version_23_07;
	}
	
	public void setReceivedGiftNewVersion2307() {
		received_gift_new_version_23_07 = true;
		changed = true;
	}
	
	public boolean isReceivedGiftNewVersion1908() {
		return received_gift_new_version_19_08;
	}
	
	public void setReceivedGiftNewVersion1908() {
		received_gift_new_version_19_08 = true;
		changed = true;
	}
	
	public boolean isReceivedGiftNewVersion1109() {
		return received_gift_new_version_11_09;
	}
	
	public void setReceivedGiftNewVersion1109() {
		received_gift_new_version_11_09 = true;
		changed = true;
	}
	
	public boolean isReceivedGiftCompensation1109() {
		return received_gift_compensation_11_09;
	}
	
	public void setReceivedGiftCompensation1109() {
		received_gift_compensation_11_09 = true;
		changed = true;
	}
	
	public boolean isReceivedGiftUpdate1609() {
		return received_gift_update_16_09;
	}
	
	public void setReceivedGiftUpdate1609() {
		received_gift_update_16_09 = true;
		changed = true;
	}
	
	public boolean isReceivedGiftTom() {
		return received_gift_tom;
	}
	
	public void setReceivedGiftTom() {
		received_gift_tom = true;
		changed = true;
	}
	
	public boolean isReceivedGiftFeatureMailbox() {
		return received_gift_feature_mailbox;
	}
	
	public void setReceivedGiftFeatureMailbox() {
		received_gift_feature_mailbox = true;
		changed = true;
	}
	
	public void setRegisteredGCM(int os) {
		switch (os) {
			case 0:
				registered_gcm_android = true;
			case 1:
				registered_gcm_ios = true;
		}
		changed = true;
	}
	
	public boolean isRegisteredGCM(int os) {
		switch (os) {
			case 0:
				return registered_gcm_android;
			case 1:
				return registered_gcm_ios;
		}
		
		return false;
	}
	
	public void setAndroidID(String s)
	{
		android_id = s;
	}
	
	public String getAndroidID()
	{
		return android_id;
	}
	
	public void setPigID(String s)
	{
		pig_id = s;
	}
	
	public String getPigID()
	{
		return pig_id;
	}
	
	public void setDeviceOS(String s)
	{
		device_os = s;
		LogHelper.LogHappy("set device_os := " + device_os);
	}
	
	public String getDeviceOS()
	{
		LogHelper.LogHappy("get device_os := " + device_os);
		return device_os;
	}
	
	public void setRefCode(String s)
	{
		ref_code = s;
		LogHelper.LogHappy("set ref code := " + ref_code);
	}
	
	public String getRefCode()
	{
        LogHelper.LogHappy("get ref code := " + ref_code);
		return ref_code;
	}
	
	public void setRegisterDate(String s) {
		register_date = s;
	}
	
	public String getRegisterDate() {
		return register_date;
	}
	
	public void setAdvertisingID(String s)
	{
		advertising_id = s;
	}
	
	public String getAdvertisingID()
	{
		return advertising_id;
	}
	
	public void SetRankingAccumulation(long v) {
		_ranking_accumulation = v; 
		_last_time_update_accumulation = Misc.SECONDS();
		changed = true;
//		LogHelper.Log("UserInfo [" + getID() + "] set ranking accumulation, value = " + v + ", last time update = " + _last_time_update_accumulation);
	}
	public long GetRankingAccumulation() { return _ranking_accumulation; }
	public int GetLastTimeUpdateAccumulation() { return _last_time_update_accumulation; }
	
	public void SetAirshipPoint(long v) {
		_airship_point = v;
		changed = true;
		LogHelper.LogHappy("UserInfo [" + getID() + "] set airship point, value = " + _airship_point);
	}
	
	public long GetAirshipPoint() 
	{ 
		return _airship_point; 
	}
	
	public void SetReceivedGiftInstall(String package_name)
	{
		_received_gift_cross_install = _received_gift_cross_install + ";" + package_name;
	}
	
	public boolean IsReceivedGiftInstall(String package_name)
	{
		return _received_gift_cross_install.contains(package_name);
	}
	
	public String GetReceivedGiftInstall()
	{
		return _received_gift_cross_install;
	}
	
	public int GetEventNum(String key)
	{
//		LogHelper.Log("GetEventNum key := " + key);
		if (_event_total_num.contains(key))
		{
			String[] aos = _event_total_num.split(";");
			for (String s : aos)
			{
				if (s.contains(key))
				{
//					LogHelper.Log("GetEventNum value := " + Integer.parseInt(s.split(":")[1]));
					return Integer.parseInt(s.split(":")[1]);
				}
			}
		}
		
		return  0;
	}
	
	public void IncreaseEventNum(String key, int value)
	{
		if (_event_total_num.contains(key))
		{
			int current_event_num = GetEventNum(key);
			int new_event_num = current_event_num + value;
			
			String old_key = key + ":" + current_event_num;
			String new_key = key + ":" + new_event_num;
			_event_total_num = _event_total_num.replace(old_key, new_key);
			LogHelper.LogHappy("increase key = " + key + ", value = " + value);
		}
		else
		{
			_event_total_num += ";" + key + ":" + value;
		}
		changed = true;
		LogHelper.LogHappy("total event num: " + _event_total_num);
	}
	
	public void SetEventNum(String key, int value)
	{
		if (_event_total_num.contains(key))
		{
			int current_event_num = GetEventNum(key);
			int new_event_num = value;
			
			String old_key = key + ":" + current_event_num;
			String new_key = key + ":" + new_event_num;
			_event_total_num = _event_total_num.replace(old_key, new_key);
			LogHelper.Log("Set key = " + key + ", value = " + value);
		}
		else
		{
			_event_total_num += ";" + key + ":" + value;
		}
		changed = true;
		LogHelper.Log("total event num: " + _event_total_num);
	}
	
	public int GetGiftBoxReceivedEventMidAutumn()
	{
		return _gift_box_received_event_mid_autumn;
	}
	
	public void SetGiftBoxReceivedEventMidAutumn(int value)
	{
		this._gift_box_received_event_mid_autumn = value;
	}
	
	public int GetGiftBoxReceived2011()
	{
		return _gift_box_received_event_20_11;
	}
	
	public void SetGiftBoxReceived2011(int value)
	{
		this._gift_box_received_event_20_11 = value;
	}
	
	public int GetGiftBoxReceivedXmasMini()
	{
		return _gift_box_received_event_xmas_mini;
	}
	
	public void SetGiftBoxReceivedXmasMini(int value)
	{
		this._gift_box_received_event_xmas_mini = value;
	}
	
	public int GetGiftBoxReceivedEventExport()
	{
		return _gift_box_received_event_export;
	}
	
	public void SetGiftBoxReceivedEventExport(int value)
	{
		this._gift_box_received_event_export = value;
	}
	
	public int GetGiftBoxReceivedEventHalloween()
	{
		return _gift_box_received_event_halloween;
	}
	
	public void SetGiftBoxReceivedEventHalloween(int value)
	{
		this._gift_box_received_event_halloween = value;
	}
	
	public int GetGiftBoxReceivedEventXmas2014()
	{
		return _gift_box_received_event_xmas_2014;
	}
	
	public void SetGiftBoxReceivedEventXmas2014(int value)
	{
		this._gift_box_received_event_xmas_2014 = value;
	}
	
	public int GetGiftBoxReceivedEventXmasTree()
	{
		return _gift_box_received_event_xmas_tree;
	}
	
	public void SetGiftBoxReceivedEventXmasTree(int value)
	{
		this._gift_box_received_event_xmas_tree = value;
	}
	
	public int GetGiftBoxReceivedEventBirthday2015()
	{
		return _gift_box_received_event_birthday_2015;
	}
	
	public void SetGiftBoxReceivedEventBirthday2015(int value)
	{
		this._gift_box_received_event_birthday_2015 = value;
	}
	
	public int GetGiftBoxReceivedEventUpgradePot()
	{
		return _gift_box_received_event_upgrade_pot;
	}
	
	public void SetGiftBoxReceivedEventUpgradePot(int value)
	{
		this._gift_box_received_event_upgrade_pot = value;
	}
	
	public int GetGiftBoxReceivedEventOrderJan2015()
	{
		return _gift_box_received_event_order_jan_2015;
	}
	
	public void SetGiftBoxReceivedEventOrderJan2015(int value)
	{
		this._gift_box_received_event_order_jan_2015 = value;
	}
	
	public int GetGiftBoxReceivedEventLunarYear2015()
	{
		return _gift_box_received_event_lunar_year_2015;
	}
	
	public void SetGiftBoxReceivedEventLunarYear2015(int value)
	{
		this._gift_box_received_event_lunar_year_2015 = value;
	}
	
	public int GetGiftBoxReceivedEvent8March2015() {
		return _gift_box_received_event_8_3_2015;
	}
	
	public void SetGiftBoxReceivedEvent8March2015(int value) {
		this._gift_box_received_event_8_3_2015 = value;
	}
	
	public boolean ReadBannedStatus()
	{
		if (id <= 0)
			return false;
		
		String ban = "";
		try
		{
			ban = (String)DBConnector.GetMembaseServer(id).Get(id + "_" + KeyID.BAN);
		}
		catch (Exception e)
		{
			ban = "";
		}
		
		if (ban != null && ban.length() > 0)
		{
			LogHelper.Log("User " + getName() + "( id = " + getID() + ") is banned.");
			return true;
		}
		
		return false;
	}
	
	public boolean IsBan()
	{
		return is_banned;
	}
}