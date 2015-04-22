package com.vng.skygarden;

import java.util.*;
import java.util.concurrent.*;

import com.vng.log.*;
import com.vng.db.*;
import com.vng.skygarden._gen_.*;

public class DBConnector
{
	//database server ip address
	
	//LIVE
	private static final DataBucket[]	_membase_servers_live = {	
																	new DataBucket("10.30.60.247:11225", "temp", 0, 0),						//temp bucket
																	new DataBucket("10.30.60.247:11226", "general", 0, 0),					//general bucket
																	new DataBucket("10.30.60.248:11227", "usergroup_1", 1, 1591075L),	//user's data bucket 1
																	new DataBucket("10.30.60.249:11227", "usergroup_2", 1591076L, 3000000L),	//user's data bucket 2
																	new DataBucket("10.30.60.250:11227", "usergroup_3", 3000001L, Long.MAX_VALUE),	//user's data bucket 3
																	new DataBucket("10.30.60.247:11224", "freestyle", 1 , Long.MAX_VALUE) // happy
																};
	
	//	DEV
	private static final DataBucket[]	_membase_servers_dev = 	{
																	new DataBucket("10.30.62.23:11215", "temp", 0, 0),						//temp bucket
																	new DataBucket("10.30.62.23:11216", "general", 0, 0),					//general bucket
																	new DataBucket("10.30.62.23:11217", "usergroup_1", 1, Long.MAX_VALUE),	//user's data bucket
																	new DataBucket("10.30.62.23:11214", "freestyle", 1 , Long.MAX_VALUE)
																};
	
	// ZALO LIVE
	private static final DataBucket[]	_membase_servers_zalo_live = {
																	new DataBucket("10.30.60.250:11220", "temp", 0, 0),						//temp bucket
																	new DataBucket("10.30.60.249:11222", "general", 0, 0),					//general bucket
																	new DataBucket("10.30.60.250:11222", "usergroup_1", 1, Long.MAX_VALUE),	//user's data bucket
																	new DataBucket("10.30.60.250:11221", "freestyle", 1 , Long.MAX_VALUE)
																};
	// GLOBAL SING LIVE
	private static final DataBucket[]	_membase_servers_global_live = {
																	new DataBucket("172.31.16.201:11220", "temp", 0, 0),						//temp bucket
																	new DataBucket("172.31.16.201:11222", "general", 0, 0),					//general bucket
																	new DataBucket("172.31.16.200:11223", "usergroup_1", 1, Long.MAX_VALUE),	//user's data bucket
																	new DataBucket("172.31.16.201:11221", "freestyle", 1 , Long.MAX_VALUE)
																};

	//database
	private static DBKeyValue[] 		_membase_servers;
	
	//init db connection
	public static boolean InitDBConnection()
	{
		//Init connection to membase server
		HashMap<String, Object> MBServerConfig = new HashMap<String, Object>();
		MBServerConfig.put("TimeOut", new Integer(5000));
		
		if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_LIVE)
		{
			if (ProjectConfig.IS_SERVER_GLOBAL_SING == 1 || ProjectConfig.IS_SERVER_GLOBAL_PAYMENT == 1)
			{
				_membase_servers = new DBKeyValue[_membase_servers_global_live.length];
				int index = 0;
				DBKeyValue db = null;

				for (DataBucket dbk : _membase_servers_global_live)
				{
					db = new DBSpyMemcache();
					db.SetConfigs(MBServerConfig);

					if (db.Connect(dbk._address) == false)
					{
						return false;
					}

					_membase_servers[index] = db;
					index++;
				}
			}
			else
			{
				_membase_servers = new DBKeyValue[_membase_servers_live.length];
				int index = 0;
				DBKeyValue db = null;

				for (DataBucket dbk : _membase_servers_live)
				{
					db = new DBSpyMemcache();
					db.SetConfigs(MBServerConfig);

					if (db.Connect(dbk._address) == false)
					{
						return false;
					}

					_membase_servers[index] = db;
					index++;
					System.out.println("Connected to database [" + dbk._name + "] at [" + dbk._address + "], min/max user id := " + dbk._min_userid + "/" + dbk._max_userid + "\n");
				}
			}
		}
		else if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_DEV)
		{
			_membase_servers = new DBKeyValue[_membase_servers_dev.length];
			int index = 0;
			DBKeyValue db = null;
			
			for (DataBucket dbk : _membase_servers_dev)
			{
				db = new DBSpyMemcache();
				db.SetConfigs(MBServerConfig);
				
				if (db.Connect(dbk._address) == false)
				{
					return false;
				}
				
				_membase_servers[index] = db;
				index++;
			}
		}
		else if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_FILE)
		{
			MBServerConfig.put("folder", "./database_mb/");
			
			_membase_servers = new DBKeyValue[1];
			DBKeyValue db = new DBFileStore();
			db.SetConfigs(MBServerConfig);
			db.Connect();
			
			_membase_servers[0] = db;
		}
		
		return true;
	}
	
	public static DBKeyValue GetMembaseServer(long user_id)
	{
		if (ProjectConfig.IS_SERVER_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_RANKING_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_NEWSBOARD_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_PIG == 1)
		{
			if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_FILE)
			{
				return _membase_servers[0];
			}
			
			return _membase_servers[_membase_servers.length - 1];
		}
		
		if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_LIVE)
		{
			if (ProjectConfig.IS_SERVER_GLOBAL_SING == 1 || ProjectConfig.IS_SERVER_GLOBAL_PAYMENT == 1)
			{
				int i = 0;
				for (DataBucket dbk : _membase_servers_global_live)
				{
					if (user_id >= dbk._min_userid && user_id <= dbk._max_userid)
					{
//						LogHelper.Log("Membase server of user id " + user_id + " is := " + dbk._name);
						return _membase_servers[i];
					}

					i++;
				}
			}
			else
			{
				int i = 0;
				for (DataBucket dbk : _membase_servers_live)
				{
					if (user_id >= dbk._min_userid && user_id <= dbk._max_userid)
					{
//						LogHelper.Log("Membase server of user id " + user_id + " is := " + dbk._name);
						return _membase_servers[i];
					}

					i++;
				}
			}
		}
		else if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_DEV)
		{
			int i = 0;
			for (DataBucket dbk : _membase_servers_dev)
			{
				if (user_id >= dbk._min_userid && user_id <= dbk._max_userid)
				{
					return _membase_servers[i];
				}
				
				i++;
			}
		}
		else if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_FILE)
		{
			return _membase_servers[0];
		}
		
		return null;
	}
	
	public static DBKeyValue GetMembaseServer(String name)
	{
		if (ProjectConfig.IS_SERVER_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_RANKING_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_NEWSBOARD_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_PIG == 1)
		{
			if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_FILE)
			{
				return _membase_servers[0];
			}
			
			return _membase_servers[_membase_servers.length - 1];
		}
		
		int i = 0;
		
		if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_LIVE)
		{
			if (ProjectConfig.IS_SERVER_GLOBAL_SING == 1 || ProjectConfig.IS_SERVER_GLOBAL_PAYMENT == 1)
			{
				for (DataBucket dbk : _membase_servers_global_live)
				{
					if (dbk._name.equals(name))
					{
						return _membase_servers[i];
					}

					i++;
				}
			}
			else
			{
				for (DataBucket dbk : _membase_servers_live)
				{
					if (dbk._name.equals(name))
					{
						return _membase_servers[i];
					}

					i++;
				}
			}
		}
		else if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_DEV)
		{
			for (DataBucket dbk : _membase_servers_dev)
			{
				if (dbk._name.equals(name))
				{
					return _membase_servers[i];
				}
				
				i++;
			}
		}
		else if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_FILE)
		{
			return _membase_servers[0];
		}
		
		return null;
	}
	
	public static DBKeyValue GetMembaseServerForTemporaryData()
	{
		if (ProjectConfig.IS_SERVER_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_RANKING_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_NEWSBOARD_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_PIG == 1)
		{
			if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_FILE)
			{
				return _membase_servers[0];
			}
			
			return _membase_servers[_membase_servers.length - 1];
		}
		
		int i = 0;
		
		if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_LIVE)
		{
			if (ProjectConfig.IS_SERVER_GLOBAL_SING == 1 || ProjectConfig.IS_SERVER_GLOBAL_PAYMENT == 1)
			{
				for (DataBucket dbk : _membase_servers_global_live)
				{
					if (dbk._name.equals("temp"))
					{
						return _membase_servers[i];
					}

					i++;
				}
			}
			else
			{
				for (DataBucket dbk : _membase_servers_live)
				{
					if (dbk._name.equals("temp"))
					{
						return _membase_servers[i];
					}

					i++;
				}
			}
		}
		else if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_DEV)
		{
			for (DataBucket dbk : _membase_servers_dev)
			{
				if (dbk._name.equals("temp"))
				{
					return _membase_servers[i];
				}
				
				i++;
			}
		}
		else if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_FILE)
		{
			return _membase_servers[0];
		}
		
		return null;
	}
	
    public static DBKeyValue GetMembaseServerForFreestyleData()
    {
        return _membase_servers[_membase_servers.length - 1];
    }
	
	public static DBKeyValue GetMembaseServerForGeneralData()
	{
		if (ProjectConfig.IS_SERVER_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_RANKING_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_NEWSBOARD_FREESTYLE == 1 
			|| ProjectConfig.IS_SERVER_PIG == 1)
		{
			if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_FILE)
			{
				return _membase_servers[0];
			}
			
			return _membase_servers[_membase_servers.length - 1];
		}
		
		int i = 0;
		
		if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_LIVE)
		{
			if (ProjectConfig.IS_SERVER_GLOBAL_SING == 1 || ProjectConfig.IS_SERVER_GLOBAL_PAYMENT == 1)
			{
				for (DataBucket dbk : _membase_servers_global_live)
				{
					if (dbk._name.equals("general"))
					{
						return _membase_servers[i];
					}

					i++;
				}
			}
			else
			{
				for (DataBucket dbk : _membase_servers_live)
				{
					if (dbk._name.equals("general"))
					{
						return _membase_servers[i];
					}

					i++;
				}
			}
		}
		else if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_DEV)
		{
			for (DataBucket dbk : _membase_servers_dev)
			{
				if (dbk._name.equals("general"))
				{
					return _membase_servers[i];
				}
				
				i++;
			}
		}
		else if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_FILE)
		{
			return _membase_servers[0];
		}
		
		return null;
	}
	
	
	private static final String		_NEW_USER_ID_KEY = "new_user_id";
	
	public static void FixNewUserID() throws Exception
	{
		for (DBKeyValue db : _membase_servers) {
			db.Delete(_NEW_USER_ID_KEY);
			db.Increase(_NEW_USER_ID_KEY, 1, 3000000L);
			LogHelper.Log("Set db new_user_id := " + db.Get(_NEW_USER_ID_KEY));
			LogHelper.Log("Set db new_user_id := " + db.Increase(_NEW_USER_ID_KEY, 1));
		}
	}
	
	public static long GenerateNewUserID()
	{
		long uid = 0;
		long val = -1;
		
		long[] gen_uid = new long[_membase_servers.length];
		int i = 0;
		int count_ex = 0;
		int count_max_val = 0;
		
		for (DBKeyValue db : _membase_servers)
		{
			try
			{
				val = db.Increase(_NEW_USER_ID_KEY, 1, 10000L);
			}
			catch (Exception ex)
			{
				val = -1;
				count_ex++;
				
				LogHelper.LogException("GenerateNewUserID Exception 1", ex);
			}
			
			
			//get max value for uid
			if (uid < val)
			{
				uid = val;
				count_max_val = 1;
			}
			else if (uid == val)
			{
				count_max_val++;
			}
			
//			LogHelper.Log("GenerateNewUser.. val := " + val);
//			LogHelper.Log("GenerateNewUser.. uid := " + uid);
			
			gen_uid[i] = val;
			i++;
		}
		
		//fix wrong new user id value
		try
		{
			for (i = gen_uid.length - 1; i >= 0; i--)
			{
				val = gen_uid[i];
				
				if (val != -1 && val < uid)
				{
					int delta = (int)(uid - val);
					
					_membase_servers[i].Increase(_NEW_USER_ID_KEY, delta);
					LogHelper.Log(LogHelper.LogType.ERROR, "GenerateNewUserID.. New user id at db " + i + " isn't up to date, fixed with delta = " + delta);
				}
			}
		}
		catch (Exception ex)
		{
			LogHelper.LogException("GenerateNewUserID Exception 2", ex);
		}
		
		//if has more than one exception or only one max value => too risk => return fail
		if (count_ex > 1 ||
			count_max_val < 2)
		{
			LogHelper.Log(LogHelper.LogType.ERROR, "GenerateNewUser.. err! count_ex=" + count_ex + " count_max_val=" + count_max_val);
			return 0;
		}
		
//		LogHelper.Log("GenerateNewUser.. new uid = " + uid);
		return uid;
	}
}

class DataBucket
{
	public String			_address;
	public String			_name;
	public long				_min_userid;
	public long				_max_userid;
	
	
	public DataBucket(String address, String name, long min_userid, long max_userid)
	{
		_address = address;
		_name = name;
		_min_userid = min_userid;
		_max_userid = max_userid;
	}
}

