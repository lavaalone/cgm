package com.vng.db;

import java.io.*;
import java.lang.*;
import java.util.*;

public class DBFileStore extends DBKeyValue
{
	private String m_sFolder = ".";
	
	public DBFileStore()
	{
		m_Configs = new HashMap<String, Object>();
	}
	
	public boolean ValidateConfigs(HashMap<String, Object> Configs)
	{
		return true;
	}
	
	public boolean Connect()
	{	
		if (m_Configs.containsKey("folder"))
		{
			m_sFolder = ((String) m_Configs.get("folder"));
			if (!m_sFolder.endsWith("/"))
				m_sFolder += "/";
			File f = new File(m_sFolder);
			f.mkdirs();
		}
		return true;
	}
	
	public boolean Connect(String server_address)
	{	
		if (m_Configs.containsKey("folder"))
		{
			m_sFolder = ((String) m_Configs.get("folder"));
			if (!m_sFolder.endsWith("/"))
				m_sFolder += "/";
			File f = new File(m_sFolder);
			f.mkdirs();
		}
		return true;
	}
	
	public void Disconnect()
	{
	}
	
	public Object Get(String key) throws Exception
	{		
		try
		{
			DataInputStream dis = new DataInputStream(new FileInputStream(m_sFolder + key));
			
			int type = dis.read();
			if (type == 'S')
			{
				byte[] data = new byte[dis.available()];
				dis.read(data);				
				return new String(data, "UTF-8");
			}
			else
			{
				byte[] data = new byte[dis.available()];
				dis.read(data);
				return data;
			}
		}
		catch (Exception e)
		{		
		}
		return null;		
	}
	
	public Map<String, Object> GetMulti(String[] keys) throws Exception
	{
		return GetMulti(Arrays.asList(keys));
	}
	
	public Map<String, Object> GetMulti(Collection<String> keys) throws Exception
	{
		Map<String, Object> data = new HashMap<String, Object>();
		for (String key :keys)
		{
			data.put(key, Get(key));
		}
		return data;
	}
	
	public boolean Add (String Key, Object Value, int ExpireDuration)
	{
		return Add(Key, Value);
	}
	public boolean Add (String Key, Object Value)
	{
		File f = new File(m_sFolder + Key);
		if (!f.exists())
		{
			return Set(Key, Value);
		}
		return false;
	}
	
	public boolean AddRaw(String Key, byte[] Value)
	{
		File f = new File(m_sFolder + Key);
		if (!f.exists())
		{
			return Set(Key, Value);
		}
		return false;
	}
	
	public boolean AddRaw(String Key, byte[] Value, int ExpireDuration)
	{
		File f = new File(m_sFolder + Key);
		if (!f.exists())
		{
			return Set(Key, Value);
		}
		return false;
	}
	
	public boolean Set (String key, Object obj)
	{
		if (obj != null)
		{
			try
			{
				DataOutputStream dos = new DataOutputStream(new FileOutputStream(m_sFolder + key));
				
				if (obj instanceof String)
				{
					dos.write('S');
					dos.writeBytes((String) obj);
				}
				else
				{
					dos.write('B');
					dos.write((byte[]) obj);
				}
				
				dos.flush();
				dos.close();
				
				return true;
			}
			catch (Exception e)
			{		
			}
		}	
		return false;
	}
	
	public boolean Set(String Key, Object Value, int ExpireDuration)	//in seconds
	{
		return Set(Key, Value);
	}	
	
	public long Increase(String Key, int num_incre, long defaultValue) throws Exception
	{
		if (Get(Key) == null)
		{
			if (Set(Key, Long.toString(defaultValue)))
			{
				return defaultValue;
			}
			else
			{
				return 0;
			}
		}
		else
		{
			return Increase(Key, num_incre);
		}
	}
	
	public long Increase(String Key, int num_incre, long defaultValue, int ExpireDuration) throws Exception
	{
		return Increase(Key, num_incre, defaultValue);
	}
	
	public long Increase(String Key, int num_incre) throws Exception
	{
		long v = Long.parseLong((String) Get(Key));
		v += num_incre;
		Set(Key, Long.toString(v));
		return v;
	}
	
	public long Decrease(String Key, int num_incre, long defaultValue) throws Exception
	{
		if (Get(Key) == null)
		{
			if (Set(Key, Long.toString(defaultValue)))
			{
				return defaultValue;
			}
			else
			{
				return 0;
			}
		}
		else
		{
			return Decrease(Key, num_incre);
		}
	}
	
	public long Decrease(String Key, int num_incre, long defaultValue, int ExpireDuration) throws Exception
	{
		return Decrease(Key, num_incre, defaultValue);
	}
	
	public long Decrease(String Key, int num_decre) throws Exception
	{
		long v = Long.parseLong((String) Get(Key));
		v -= num_decre;
		Set(Key, Long.toString(v));
		return v;
	}
	
	public boolean Replace(String Key, Object Value, int ExpireDuration)
	{
		File f = new File(m_sFolder + Key);
		if (f.exists())
		{
			return Set(Key, Value);
		}
		return false;
	}
	
	public boolean Delete(String Key)
	{
		File file = new File(m_sFolder + Key);
		return file.delete();
	}
	
	public Map<Object, Map<String, String>> GetStats()
	{
		return null;
	}	
	
	public byte[] GetRaw(String Key) throws Exception
	{
		return (byte[]) Get(Key);
	}
	
	public boolean SetRaw(String Key, byte[] Value)
	{
		return Set(Key, Value);
	}
	
	public boolean SetRaw(String Key, byte[] Value, int ExpireDuration)
	{
		return Set(Key, Value);
	}
	
	public CompareAndSwapValue<byte[]> GetRawAndCasId(String Key) throws Exception
	{
		return new CompareAndSwapValue<byte[]>(0, GetRaw(Key));
	}
	
	public CompareAndSwapValue<Object> GetValueAndCasId(String Key) throws Exception
	{
		return new CompareAndSwapValue<Object>(0, Get(Key));
	}
	
	public Map<String, byte[]> GetRawMulti(Collection<String> keys) throws Exception
	{
		Map<String, byte[]> data = new HashMap<String, byte[]>();
		
		for (String key : keys)
		{
			data.put(key, GetRaw(key));
		}
		
		return data;
	}
	
	public boolean CompareAndSwap(String Key, long CasId, Object Value)
	{
		return Set(Key, Value);
	}
	
	public boolean CompareAndSwap(String Key, long CasId, Object Value, int ExpireDuration)
	{
		return Set(Key, Value);
	}
	
	public boolean CompareAndSwapRaw(String Key, long CasId, byte[] Value)
	{
		return Set(Key, Value);
	}
	
	public boolean CompareAndSwapRaw(String Key, long CasId, byte[] Value, int ExpireDuration)
	{
		return Set(Key, Value);
	}
}
