package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.skygarden.DBConnector;
import com.vng.skygarden.SkyGarden;
import com.vng.skygarden._gen_.ProjectConfig;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import java.util.*;

/**
 * Contains the ranking info for client to display
 */
public class RankingInfo {
	private int _id = -1;
	private long _current_floor_value = -1; 
	private int _command = -1;
	private int _extra_command = -1;
	private long _start_show_details_time = -1;
	private long _start_time = -1;
	private long _end_time = -1;
	private String _description = "";
	private String _ranking_category = "";
	private String _text_template = "";
	private long _target = -1;
	private String _unit = "";
	private String _gift_rank_1 = "";
	private String _gift_rank_2 = "";
	private String _gift_rank_3 = "";
	private String _gift_rank_100 = "";
	private String _gift_rank_basic = "";
	private int _rank_size = 100;
	private String _season_img = "null";
	private String _season_img_md5 = "null";
	private String _season_details = "null";
	private String _season_details_md5 = "null";
	private String _temp_text_1 = "temp_text";
	
	public RankingInfo(int id) {
		_id = id;
	}
	
	public byte[] GetData() {
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addLong(KeyID.KEY_RANKING_SHOW_DETAISL_TIME, _start_show_details_time - Misc.MILLISECONDS_OF_1_1_2010());
		encrypt.addLong(KeyID.KEY_RANKING_START_TIME, _start_time - Misc.MILLISECONDS_OF_1_1_2010());
		encrypt.addString(KeyID.KEY_RANKING_START_TIME_STR, Misc.getDateTime(_start_time));
		encrypt.addLong(KeyID.KEY_RANKING_END_TIME, _end_time - Misc.MILLISECONDS_OF_1_1_2010());
//		encrypt.addString(KeyID.KEY_RANKING_END_TIME_STR, Misc.getDateTime(_end_time));
		encrypt.addString(KeyID.KEY_RANKING_END_TIME_STR, Misc.getDateTime(_start_time));
		encrypt.addString(KeyID.KEY_RANKING_DESCRIPTION, _description);
		encrypt.addString(KeyID.KEY_RANKING_CATEGORY, _ranking_category);
		encrypt.addString(KeyID.KEY_RANKING_TEXT_TEMPLATE, _text_template);
		encrypt.addLong(KeyID.KEY_RANKING_TARGET, _target);
		encrypt.addString(KeyID.KEY_RANKING_UNIT, _unit);
		encrypt.addString(KeyID.KEY_RANKING_GIFT_1, _gift_rank_1);
		encrypt.addString(KeyID.KEY_RANKING_GIFT_2, _gift_rank_2);
		encrypt.addString(KeyID.KEY_RANKING_GIFT_3, _gift_rank_3);
		encrypt.addString(KeyID.KEY_RANKING_GIFT_100, _gift_rank_100);
		encrypt.addString(KeyID.KEY_RANKING_GIFT_BASIC, _gift_rank_basic);		
		encrypt.addInt(KeyID.KEY_RANKING_SIZE, _rank_size);		
		encrypt.addInt(KeyID.KEY_RANKING_CLIENT_REFRESH, 15);
		encrypt.addStringANSI(KeyID.KEY_RANKING_IMG, _season_img);
		encrypt.addStringANSI(KeyID.KEY_RANKING_IMG_MD5, _season_img_md5);
		encrypt.addStringANSI(KeyID.KEY_RANKING_DETAILS, _season_details);
		encrypt.addStringANSI(KeyID.KEY_RANKING_DETAILS_MD5, _season_details_md5);
		encrypt.addStringANSI(KeyID.KEY_RANKING_TEMP_TEXT_1, _temp_text_1);
		return encrypt.toByteArray();
	}
	
	public boolean Load() 
	{
		boolean success = false;
		try 
		{
			Object obj = DBConnector.GetMembaseServerForGeneralData().Get("ranking" + "_" + _id);
			if (obj != null)
			{
				String s = (String)obj;
				String[] sa = s.split(";");
				// read command id
				_command = Integer.parseInt(sa[0].split("_")[0]);
				if (_command == CommandID.CMD_MOVE_MACHINE_PRODUCT || _command == CommandID.CMD_MOVE_MACHINE_PRODUCT_OPTIMIZE_DATA_OUT) {
					// extra info ...
					_extra_command = Integer.parseInt(sa[0].split("_")[1]);
				}

				_start_show_details_time = Long.parseLong(sa[1]);
				_start_time = Long.parseLong(sa[2]);
				_end_time = Long.parseLong(sa[3]);
				_description = sa[4];
				_ranking_category = sa[5];
				_text_template = sa[6];
				_target = Long.parseLong(sa[7]);
				_unit = sa[8];
				_gift_rank_1 = sa[9];
				_gift_rank_2 = sa[10];
				_gift_rank_3 = sa[11];
				_gift_rank_100 = sa[12];
				_gift_rank_basic = sa[13];

				if (sa.length >= 14) {
					_rank_size = Integer.parseInt(sa[14]);
				}

				if (sa.length >= 16) {
					_season_img = sa[15];
					_season_img_md5 = sa[16];
				}

				if (sa.length >= 18) {
					_season_details = sa[17];
					_season_details_md5 = sa[18];
				}

				if (sa.length >= 19) {
					_temp_text_1 = sa[19];
				}

				success = true;
			}
			else
			{
				success = false;
			}
			
		} 
		catch (Exception e) 
		{
			success = false;
			LogHelper.LogException("RankingInfo.Load", e);
		}
		
		if (!success) 
		{
			return success;
		}
		else 
		{
			try 
			{
				Object obj = DBConnector.GetMembaseServerForTemporaryData().Get("rannking_floor_value" + "_" + _id);
				if (obj != null)
				{
					_current_floor_value = (long)obj;
				}
				else
				{
					_current_floor_value = 0;
				}
			} 
			catch (Exception e) 
			{
				_current_floor_value = 0;
			}
		}
		
		return success;
	}
	
	public boolean IsActive() {
		if (System.currentTimeMillis() >= _start_show_details_time && System.currentTimeMillis() <= _end_time) {
			return true;
		}
		return false;
	}
	
	public boolean IsComming() {
		if (System.currentTimeMillis() < _start_show_details_time) {
			return true;
		}
		return false;
	}
	
	public boolean IsPast() {
		if (System.currentTimeMillis() > _end_time) {
			return true;
		}
		return false;
	}
	
	public int GetRankingCommand() {
		return _command;
	}
	
	public int GetExtraCommand() {
		return _extra_command;
	}
	
	public int GetID() {
		return _id;
	}
	
	public long GetCurrentFloorValue() {
		return _current_floor_value;
	}
	
	public byte[] GetResult(int top) {
		if (_id == -1 || _command == -1) {
			LogHelper.Log("RankingTarget.. err! invalid ranking result!");
			return null;
		}
		
		if (top > _rank_size) {
			top = _rank_size;
		}
		
		FBEncrypt encrypt = new FBEncrypt();
		String result = "";
		try {
			result = (String)DBConnector.GetMembaseServerForTemporaryData().Get("ranking_result" + "_" + _id);
		} catch (Exception e) {
			LogHelper.LogException("GetResult", e);
			return null;
		}
		
		if (result == null) {
			return null;
		}

		String[] sa = result.split(";");
		int idx = 0;
		for (int i = sa.length - 1; i > 0; i--) {
			String[] inner = sa[i].split(":");
			if (inner.length > 3) {
				int rank = Integer.parseInt(inner[0]);
				if (rank <= top) {
					long user_id = Long.parseLong(inner[1]);
					long value = Long.parseLong(inner[2]);
					int delta_rank = Integer.parseInt(inner[3]);
//					LogHelper.Log("RankingTarget.GetResult: rank = " + rank + ", user id = " + user_id + ", record = " + value + ", delta rank = " + delta_rank);
					
					FBEncrypt inner_encrypt = new FBEncrypt();
					inner_encrypt.addLong(KeyID.KEY_USER_RANK, rank);
					inner_encrypt.addBinary(KeyID.KEY_USER_INFOS, ServerHandler.GetUserData(user_id));
					inner_encrypt.addLong(KeyID.KEY_RANKING_USER_RECORD, value);
					inner_encrypt.addInt(KeyID.KEY_RANKING_DELTA, delta_rank);
					encrypt.addBinary(KeyID.KEY_RANK_RESULT + idx, inner_encrypt.toByteArray());
					idx++;
				}
			} else {
				LogHelper.Log("handleGetPreviousRankingInfo.. err! invalid ranking result!");
				break;
			}
		}
		encrypt.addString(KeyID.KEY_RANKING_LAST_UDPATE, sa[0]);
		encrypt.addInt(KeyID.KEY_RANKING_COUNT, idx);
		return encrypt.toByteArray();
	}
	
	public long GetBasicTarget() {
		return _target;
	}
	
	public String GetGift(int rank) {
		switch (rank) {
			case 1: return _gift_rank_1;
			case 2: return _gift_rank_2;
			case 3: return _gift_rank_3;
			case 100: return _gift_rank_100;
			case 101: return _gift_rank_basic;
			default: return "";
		}
	}
	
	public long GetStartTime() { return (_start_time - Misc.MILLISECONDS_OF_1_1_2010())/1000; }
}

