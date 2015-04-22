package com.vng.skygarden.game;

import com.vng.log.*;
import com.vng.netty.*;
import com.vng.skygarden.DBConnector;
import com.vng.util.*;
import java.util.*;

public class AchievementManager
{
	List<Achievement> _achievements = new ArrayList<Achievement>();
	HashMap<String, Integer> _command_to_acm_index = new HashMap<String, Integer>();
	UserInfo _user_info = null;
	
	long _save_count = 0;
	int _last_save_time = 0;
	int SAVE_DURATION = 10;
	
	public AchievementManager(UserInfo user_info)
	{
		this._user_info = user_info;
	}
	
	public boolean Load()
	{
		if (_user_info == null)
		{
			return false;
		}
		
		// get achievement index from command id
		for (int i = 0; i < GetTotalAchievements(); i++)
		{
			int idx = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_ACHIEVEMENT][i][DatabaseID.ACHIEVEMENT_INDEX]);
			int cid = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_ACHIEVEMENT][i][DatabaseID.ACHIEVEMENT_COMMAND_ID]);
			int info = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_ACHIEVEMENT][i][DatabaseID.ACHIEVEMENT_EXTRA_COMMAND_INFO]);
			String key = cid + "_" + info;
			int value = idx;
			_command_to_acm_index.put(key, value);
		}
		
		_achievements.clear();
		List<String> all_keys = new ArrayList<String>();
		for (int i = 0; i < GetTotalAchievements(); i++)
		{
			String key = _user_info.getID() + "_" + KeyID.KEY_ACHIEVEMENT_INDEX + i;
			all_keys.add(key);
		}
		
		try
		{
			Map<String, Object> all_data = DBConnector.GetMembaseServer(_user_info.getID()).GetMulti(all_keys);
			for (int i = 0; i < GetTotalAchievements(); i++)
			{
				String key = _user_info.getID() + "_" + KeyID.KEY_ACHIEVEMENT_INDEX + i;
				if (all_data.containsKey(key))
				{
					byte[] data = (byte[])all_data.get(key);
					Achievement acm = new Achievement(data);
					_achievements.add(acm);
				}
				else
				{
					Achievement acm = new Achievement(i);
					acm.setChanged(true);
					_achievements.add(acm);
				}
			}
			
			SaveAll(true);
		}
		catch (Exception e)
		{
			LogHelper.LogException("acm.load", e);
		}
		
		return true;
	}
	
	public void SaveAll(boolean force)
	{
		_save_count++;
		if ((_save_count % 5 == 0) || Misc.SECONDS() - _last_save_time >= SAVE_DURATION || force)
		{
			_last_save_time = Misc.SECONDS();
			for (Achievement acm : _achievements)
			{
				if (acm.isChanged())
				{
					acm.setChanged(false);
					String key = _user_info.getID() + "_" + KeyID.KEY_ACHIEVEMENT_INDEX + acm.getIndex();
					byte[] value = acm.getData();
					DBConnector.GetMembaseServer(_user_info.getID()).SetRaw(key, value);
				}
			}
		}
	}
	
	public int Increase(int command_id, long value)
	{
		return Increase(command_id, -1, value);
	}
	
	public int Increase(int command_id, int extra_info, long value)
	{
		int acm_index = GetAchievementIndex(command_id, extra_info);
		if (acm_index != -1)
		{
			long new_value = value + GetAchievement(acm_index).getTotal();
			return Set(command_id, extra_info, new_value, acm_index);
		}
		
		return acm_index;
	}
	
	public int Set(int command_id, int extra_info, long value, int acm_index)
	{
		if (acm_index == -1)
		{
			acm_index = GetAchievementIndex(command_id, extra_info);
		}
		
		if (acm_index != -1)
		{
			GetAchievement(acm_index).setTotal(value);
			SaveAll(false);
		}
		
		return acm_index;
	}
	
	public int Set(int command_id, int extra_info, long value)
	{
		return Set(command_id, extra_info, value, -1);
	}
	
	public Achievement GetAchievement(int index)
	{
		if (index < _achievements.size())
		{
			return _achievements.get(index);
		}
		return null;
	}
	
	public int GetAchievementIndex(int command_id, int extra_info)
	{
		String key = command_id + "_" + extra_info;
		if (_command_to_acm_index.containsKey(key))
		{
			return _command_to_acm_index.get(key);
		}
		return -1;
	}
	
	public int GetTotalAchievements()
	{
		return Server.s_globalDB[DatabaseID.SHEET_ACHIEVEMENT].length;
	}
	
	public byte[] getDataToClient()
	{
		FBEncrypt encrypt = new FBEncrypt();
		int i = 0;
		encrypt.addInt(KeyID.KEY_ACHIEVEMENT_TOTAL, GetTotalAchievements());
		for (Achievement acm : _achievements)
		{
			encrypt.addBinary(KeyID.KEY_ACHIEVEMENT_INDEX + i, acm.getData());
			i++;
		}
		return encrypt.toByteArray();
	}
}