package com.vng.skygarden.game;

import com.vng.db.DBKeyValue;
import com.vng.log.*;
import com.vng.skygarden.DBConnector;
import com.vng.skygarden._gen_.ProjectConfig;
import com.vng.util.*;
import java.util.*;

public class FriendManager
{
	private String _uid = "";
	
	private StringBuilder _friend_list;
	private DBKeyValue _db;
	
	private final boolean LOG_FRIEND = !true;
	
	private List<String> _fb_friend_list;
	private List<String> _zing_friend_list;
	private List<String> _zalo_friend_list;
	
	public FriendManager()
    {
		_friend_list = new StringBuilder();
		_fb_friend_list = new LinkedList<String>();
		_zing_friend_list = new LinkedList<String>();
		_zalo_friend_list = new LinkedList<String>();
    }
	
	public FriendManager(String uid)
	{
		_uid = uid;
		_friend_list = new StringBuilder();
		_fb_friend_list = new LinkedList<String>();
		_zing_friend_list = new LinkedList<String>();
		_zalo_friend_list = new LinkedList<String>();
		_db = null;
	}
	
	public void SetUserID(String uid)
	{
		this._uid = uid;
	}
	
	public void SetDatabase(DBKeyValue db)
	{
		_db = db;
	}
	
	public boolean LoadFromDatabase(String key)
	{
		_fb_friend_list.clear();
		_zing_friend_list.clear();
		_zalo_friend_list.clear();
		
		long uid = -1;
		try
		{
			uid = Long.parseLong(_uid);
		}
		catch (Exception e)
		{
			return true;
		}
		
		List<String> all_keys = new ArrayList<String>();
		all_keys.add(uid + "_" + key + "_" + "fb");
		all_keys.add(uid + "_" + key + "_" + "zing");
		all_keys.add(uid + "_" + key + "_" + "zalo");
		try
		{
			Map<String, Object> all_data = DBConnector.GetMembaseServer(uid).GetMulti(all_keys);
			
			String inner_key = uid + "_" + key + "_" + "fb";
			if (all_data.containsKey(inner_key))
			{
				String friends = (String)all_data.get(inner_key);
				String[] aos = friends.split(";");
				for (String s : aos)
				{
					if (!_fb_friend_list.contains(s))
					{
						_fb_friend_list.add(s);
					}
				}
				
				if (ProjectConfig.IS_SERVER_FREESTYLE == 1)
				{
					LogHelper.Log("fb friend list: " + _fb_friend_list.toString());
				}
			}
			
			inner_key = uid + "_" + key + "_" + "zing";
			if (all_data.containsKey(inner_key))
			{
				String friends = (String)all_data.get(inner_key);
				String[] aos = friends.split(";");
				for (String s : aos)
				{
					if (!_zing_friend_list.contains(s))
					{
						_zing_friend_list.add(s);
					}
				}
				
				if (ProjectConfig.IS_SERVER_FREESTYLE == 1)
				{
					LogHelper.Log("zing friend list: " + _zing_friend_list.toString());
				}
			}
			
			inner_key = uid + "_" + key + "_" + "zalo";
			if (all_data.containsKey(inner_key))
			{
				String friends = (String)all_data.get(inner_key);
				String[] aos = friends.split(";");
				for (String s : aos)
				{
					if (!_zalo_friend_list.contains(s))
					{
						_zalo_friend_list.add(s);
					}
				}
				
				if (ProjectConfig.IS_SERVER_FREESTYLE == 1)
				{
					LogHelper.Log("zalo friend list: " + _zalo_friend_list.toString());
				}
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("friend.LoadFromDatabase", e);
		}

		return true;
	}
	
	public boolean SaveFriendListToDatabase(String key)
	{
		StringBuilder fb_list = new StringBuilder();
		for (String s : _fb_friend_list)
		{
			if (_fb_friend_list.indexOf(s) == 0)
			{
				fb_list.append(s);
			}
			else
			{
				fb_list.append(";").append(s);
			}
		}
		_db.Set(_uid + "_" + key + "_" + "fb", fb_list.toString());
		
		StringBuilder zing_list = new StringBuilder();
		for (String s : _zing_friend_list)
		{
			if (_zing_friend_list.indexOf(s) == 0)
			{
				zing_list.append(s);
			}
			else
			{
				zing_list.append(";").append(s);
			}
		}
		_db.Set(_uid + "_" + key + "_" + "zing", zing_list.toString());
		
		StringBuilder zalo_list = new StringBuilder();
		for (String s : _zalo_friend_list)
		{
			if (_zalo_friend_list.indexOf(s) == 0)
			{
				zalo_list.append(s);
			}
			else
			{
				zalo_list.append(";").append(s);
			}
		}
		_db.Set(_uid + "_" + key + "_" + "zalo", zalo_list.toString());
		
		return true;
	}
	
	public boolean AddFriend(String friend_id, TYPE type)
	{
		switch (type)
		{
			case FACEBOOK:
				return _fb_friend_list.add(friend_id);
			case ZING:
				return _zing_friend_list.add(friend_id);
			case ZALO:
				return _zalo_friend_list.add(friend_id);
		}
		return false;
	}

	public void Clear(TYPE type)
	{
		switch (type)
		{
			case FACEBOOK:
				_fb_friend_list.clear();
			case ZING:
				_zing_friend_list.clear();
			case ZALO:
				_zalo_friend_list.clear();
		}
	}
	
	public String GetFriendList()
	{
//		if (_friend_list.length() > 0)
//			return _friend_list.toString();
		
		StringBuilder friend_list = new StringBuilder();
		
		List<String> all_friends = new LinkedList<String>();
		for (String s : _fb_friend_list)
		{
			if (!all_friends.contains(s))
				all_friends.add(s);
		}
		
		for (String s : _zing_friend_list)
		{
			if (!all_friends.contains(s))
				all_friends.add(s);
		}
		
		for (String s : _zalo_friend_list)
		{
			if (!all_friends.contains(s))
				all_friends.add(s);
		}
		
		for (String s : all_friends)
		{
			if (friend_list.length() == 0)
				friend_list.append(s);
			else
				friend_list.append(";").append(s);
		}
		
		if (ProjectConfig.IS_SERVER_FREESTYLE == 1)
		{
			LogHelper.Log("friend list size: " + GetSize());
		}
		
		
		return friend_list.toString();
	}
	
	public int GetSize() 
	{
		return _fb_friend_list.size() + _zalo_friend_list.size() + _zing_friend_list.size();
	}
	
	public enum TYPE
	{
		FACEBOOK,
		ZING,
		ZALO
	}
}