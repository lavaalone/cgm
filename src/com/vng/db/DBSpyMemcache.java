package com.vng.db;

import java.util.*;
import java.util.concurrent.*;
import java.net.URI;

import net.spy.memcached.*;
import net.spy.memcached.internal.*;
import net.spy.memcached.transcoders.*;


public class DBSpyMemcache extends DBKeyValue
{
	private MemcachedClient				_spymemcached_client	=	null;
	private int							_time_out				=	5000;
	
	private final SerializingTranscoder	_serial_transcoder 		= new SerializingTranscoder();
			  
	public DBSpyMemcache()
	{
		m_Configs = new HashMap<String, Object>();
	}
	
	public boolean ValidateConfigs(HashMap<String, Object> Configs)
	{
		return true;
	}
	
	public boolean Connect()
	{
		try
		{			
			List<String> all_server = Arrays.asList((String[]) m_Configs.get("Servers"));
			_spymemcached_client = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses(all_server));
			
			if (m_Configs.containsKey("TimeOut"))
			{
				Integer time_out = (Integer)(m_Configs.get("TimeOut"));
				_time_out = time_out.intValue();
			}
		}
		catch (Exception ex)
		{			
			return false;
		}
		
		return true;
	}
	
	public boolean Connect(String server_address)
	{
		try
		{
			_spymemcached_client = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses(server_address));
			
			if (m_Configs.containsKey("TimeOut"))
			{
				Integer time_out = (Integer)(m_Configs.get("TimeOut"));
				_time_out = time_out.intValue();
			}
			
			//check connection
			String test_val = server_address + System.currentTimeMillis();
			String test_key = "test_connect";
			//test set operation
			if (Set(test_key, test_val, 90) == false)
			{
				return false;
			}
			
			String return_str = (String)Get(test_key);
			
			return return_str.equals(test_val);
		}
		catch (Exception ex)
		{
		}
		
		return false;
	}
	
	public void Disconnect()
	{
		_spymemcached_client.shutdown();
	}
	
	public Object Get(String key) throws Exception
	{
		// Try to get a value, for up to 5 seconds, and cancel if it
		// doesn't return
		Object value = null;
		GetFuture<Object> f = null;
		
		try 
		{
			f = _spymemcached_client.asyncGet(key, _serial_transcoder);
			value = f.get(_time_out, TimeUnit.MILLISECONDS);
			// throws expecting InterruptedException, ExecutionException or TimeoutException
		} 
		catch (Exception ex) 
		{
			// Since we don't need this, go ahead and cancel the operation.
			// This is not strictly necessary, but it'll save some work on
			// the server.  It is okay to cancel it if running.
			if (f != null)
			{
				f.cancel(true);
			}
			
			// Do other timeout related stuff
			throw ex;
		}
		
		return value;
	}
	
	public CompareAndSwapValue<Object> 	GetValueAndCasId(String key) throws Exception
	{
		// Try to get a value, for up to 5 seconds, and cancel if it
		// doesn't return
		CASValue<Object> value = null;	
		OperationFuture<CASValue<Object>> f = null;
		
		try 
		{
			f = _spymemcached_client.asyncGets(key, _serial_transcoder);
			value = f.get(_time_out, TimeUnit.MILLISECONDS);
			// throws expecting InterruptedException, ExecutionException or TimeoutException
		} 
		catch (Exception ex) 
		{
			// Since we don't need this, go ahead and cancel the operation.
			// This is not strictly necessary, but it'll save some work on
			// the server.  It is okay to cancel it if running.
			if (f != null)
			{
				f.cancel();
			}
			
			// Do other timeout related stuff
			throw ex;
		}
		
		return new CompareAndSwapValue<Object>(value.getCas(), value.getValue());
	}
	
	public Map<String, Object> GetMulti(String[] keys) throws Exception
	{
		return GetMulti(Arrays.asList(keys));
	}
	
	public Map<String, Object> GetMulti(Collection<String> keys) throws Exception
	{
		BulkFuture<Map<String, Object>> f = null;
		Map<String, Object> values = null;
		
		try 
		{
			f = _spymemcached_client.asyncGetBulk(keys, _serial_transcoder);
			values = f.getSome(_time_out, TimeUnit.MILLISECONDS);
		}
		catch (Exception ex) 
		{
			// Do other timeout related stuff
			if (f != null)
			{
				f.cancel(true);
			}
			
			throw ex;
		}
		
		if (f != null && f.isTimeout())
		{
			throw new Exception("GetMulti timeout");
		}
		
		return values;
	}
	
	public Map<String, byte[]> GetRawMulti(Collection<String> keys) throws Exception
	{
		BulkFuture<Map<String, Object>> f = null;
				
		try 
		{
			f = _spymemcached_client.asyncGetBulk(keys, _serial_transcoder);			
			Map<String, byte[]> values = new HashMap<String, byte[]>();
			for (Map.Entry<String, Object> entry: f.getSome(_time_out, TimeUnit.MILLISECONDS).entrySet())
			{
				values.put(entry.getKey(), (byte[]) entry.getValue());
			}
			return values;
		}
		catch (Exception ex) 
		{
			// Do other timeout related stuff
			if (f != null)
			{
				f.cancel(true);
			}
			
			throw ex;
		}
		
		// if (f != null && f.isTimeout())
		// {
			// throw new Exception("GetMulti timeout");
		// }
		
		// return null;
	}
	
	public boolean Add (String key, Object value)
	{
		return Add(key, value, 0);
	}
	
	public boolean Add (String key, Object value, int exp)
	{
		Boolean result = new Boolean(false);
		
		try 
		{
			OperationFuture<Boolean> f = _spymemcached_client.add(key, exp, value, _serial_transcoder);
			result = f.get(_time_out, TimeUnit.MILLISECONDS);			
		} 
		catch (Exception e) 
		{
			return false;
		}
		
		return result.booleanValue();
	}
	
	public boolean AddRaw(String key, byte[] value)
	{
		Boolean result = new Boolean(false);
		
		try 
		{
			OperationFuture<Boolean> f = _spymemcached_client.add(key, 0, value, _serial_transcoder);
			result = f.get(_time_out, TimeUnit.MILLISECONDS);			
		} 
		catch (Exception e) 
		{
			return false;
		}
		
		return result.booleanValue();
	}
	
	public boolean AddRaw(String key, byte[] value, int exp)
	{
		Boolean result = new Boolean(false);
		
		try 
		{
			OperationFuture<Boolean> f = _spymemcached_client.add(key, exp, value, _serial_transcoder);
			result = f.get(_time_out, TimeUnit.MILLISECONDS);			
		} 
		catch (Exception e) 
		{
			return false;
		}
		
		return result.booleanValue();
	}
	
	public boolean Set(String key, Object value)
	{
		Boolean result = new Boolean(false);
		
		try 
		{
			OperationFuture<Boolean> f = _spymemcached_client.set(key, 0, value, _serial_transcoder);
			result = f.get(_time_out, TimeUnit.MILLISECONDS);
		} 
		catch (Exception e) 
		{
			return false;
		}
		
		return result.booleanValue();
	}
	
	public boolean Set(String key, Object value, int exp)	//in seconds
	{
		if (exp <= 0)
		{
			exp = 0;
		}
		
		Boolean result = new Boolean(false);
		try 
		{
			OperationFuture<Boolean> f = _spymemcached_client.set(key, exp, value, _serial_transcoder);
		
			result = f.get(_time_out, TimeUnit.MILLISECONDS);
			// throws expecting InterruptedException, ExecutionException or TimeoutException
		} 
		catch (Exception e) 
		{
			return false;
		}
		
		return result.booleanValue();
	}
	
	public boolean CompareAndSwap(String key, long CasId, Object value)
	{
		CASResponse resp = CASResponse.NOT_FOUND;
		
		try 
		{
			resp = _spymemcached_client.cas(key, CasId, value, _serial_transcoder);
		} 
		catch (Exception e) 
		{
			return false;
		}
		
		if (resp == CASResponse.OK)
		{
			return true;
		}
		
		return false;
	}
	
	public boolean CompareAndSwap(String key, long CasId, Object value, int exp)
	{
		CASResponse resp = CASResponse.NOT_FOUND;
		
		try 
		{
			resp = _spymemcached_client.cas(key, CasId, exp, value, _serial_transcoder);
		} 
		catch (Exception e) 
		{
			return false;
		}
		
		if (resp == CASResponse.OK)
		{
			return true;
		}
		
		return false;
	}
	
	public long Increase(String key, int num_incre) throws Exception
	{
		/*
		Long ret_val = new Long(-1);
		
		try
		{
			OperationFuture<Long> f = _spymemcached_client.asyncIncr(key, num_incre);
			
			ret_val = f.get(_time_out, TimeUnit.MILLISECONDS);
		}
		catch (Exception ex)
		{
			throw ex;
		}
		
		return ret_val.longValue();
		*/
		
		return _spymemcached_client.incr(key, num_incre);
	}
	
	public long Increase (String key, int num_incre, long defaultValue) throws Exception
	{
		return _spymemcached_client.incr(key, num_incre, defaultValue);
	}
	
	public long Increase (String key, int num_incre, long defaultValue, int exp) throws Exception
	{
		return _spymemcached_client.incr(key, num_incre, defaultValue, exp);
	}
	
	public long Decrease(String key, int num_decre) throws Exception
	{
		Long ret_val = new Long(-1);
		
		try
		{
			OperationFuture<Long> f = _spymemcached_client.asyncDecr(key, num_decre);
			
			ret_val = f.get(_time_out, TimeUnit.MILLISECONDS);
		}
		catch (Exception ex)
		{
			throw ex;
		}
		
		return ret_val.longValue();
	}
	
	public long Decrease(String key, int num_decre, long defaultValue) throws Exception
	{
		return _spymemcached_client.decr(key, num_decre, defaultValue);
	}
	
	public long Decrease(String key, int num_decre, long defaultValue, int exp) throws Exception
	{
		return _spymemcached_client.decr(key, num_decre, defaultValue, exp);
	}
	
	public boolean Replace(String key, Object value, int exp)
	{
		if (exp <= 0)
		{
			exp = 0;
		}
		
		Boolean result = new Boolean(false);
		try 
		{
			OperationFuture<Boolean> f = _spymemcached_client.replace(key, exp, value, _serial_transcoder);
			result = f.get(_time_out, TimeUnit.MILLISECONDS);
		} 
		catch (Exception e) 
		{
			return false;
		}
		
		return result.booleanValue();
	}
	
	public boolean Delete(String key)
	{
		OperationFuture<Boolean> f = _spymemcached_client.delete(key);
		
		Boolean result = new Boolean(false);
		try 
		{
			result = f.get(_time_out, TimeUnit.MILLISECONDS);
		} 
		catch (Exception e) 
		{
			return false;
		}
		
		return result.booleanValue();
	}
	
	public Map<Object, Map<String, String>> GetStats()
	{
		Map<java.net.SocketAddress, Map<String, String>> allstats = _spymemcached_client.getStats();
		
		HashMap<Object, Map<String, String>> rv_map = new HashMap<Object, Map<String, String>>(allstats);
		
		return rv_map;
	}
	
	
	public byte[] GetRaw(String key) throws Exception
	{
		// Try to get a value, for up to 5 seconds, and cancel if it
		// doesn't return
		byte[] value = null;
		
		GetFuture<Object> f = null;
		
		try 
		{
			f = _spymemcached_client.asyncGet(key, _serial_transcoder);
			value = (byte[])f.get(_time_out, TimeUnit.MILLISECONDS);
			// throws expecting InterruptedException, ExecutionException or TimeoutException
		} 
		catch (Exception ex) 
		{
			// Since we don't need this, go ahead and cancel the operation.
			// This is not strictly necessary, but it'll save some work on
			// the server.  It is okay to cancel it if running.
			if (f != null)
			{
				f.cancel(true);
			}
			
			// Do other timeout related stuff
			throw ex;
		}
		
		return value;
	}
	
	public CompareAndSwapValue<byte[]> 	GetRawAndCasId(String key) throws Exception
	{
		// Try to get a value, for up to 5 seconds, and cancel if it
		// doesn't return
		CASValue<Object> value = null;
		
		OperationFuture<CASValue<Object>> f = null;
		
		try 
		{
			f = _spymemcached_client.asyncGets(key, _serial_transcoder);
			value = f.get(_time_out, TimeUnit.MILLISECONDS);
			// throws expecting InterruptedException, ExecutionException or TimeoutException
		} 
		catch (Exception ex) 
		{
			// Since we don't need this, go ahead and cancel the operation.
			// This is not strictly necessary, but it'll save some work on
			// the server.  It is okay to cancel it if running.
			if (f != null)
			{
				f.cancel();
			}
			
			// Do other timeout related stuff
			throw ex;
		}
		
		return new CompareAndSwapValue<byte[]>(value.getCas(), (byte[]) value.getValue());
	}
	
	public boolean SetRaw(String key, byte[] value)
	{
		Boolean result = new Boolean(false);
		try 
		{
			OperationFuture<Boolean> f = _spymemcached_client.set(key, 0, value, _serial_transcoder);
		
			result = f.get(_time_out, TimeUnit.MILLISECONDS);
			// throws expecting InterruptedException, ExecutionException or TimeoutException
		} 
		catch (Exception e) 
		{
			return false;
		}
		
		return result.booleanValue();
	}
	
	public boolean CompareAndSwapRaw(String key, long CasId, byte[] value)
	{
		CASResponse resp = CASResponse.NOT_FOUND;
		
		try 
		{
			resp = _spymemcached_client.cas(key, CasId, value, _serial_transcoder);
		} 
		catch (Exception e) 
		{
			return false;
		}
		
		if (resp == CASResponse.OK)
		{
			return true;
		}
		
		return false;
	}
	
	public boolean SetRaw(String key, byte[] value, int exp)
	{
		if (exp <= 0)
		{
			exp = 0;
		}
		
		Boolean result = new Boolean(false);
		try 
		{
			OperationFuture<Boolean> f = _spymemcached_client.set(key, exp, value, _serial_transcoder);
		
			result = f.get(_time_out, TimeUnit.MILLISECONDS);
			// throws expecting InterruptedException, ExecutionException or TimeoutException
		} 
		catch (Exception e) 
		{
			return false;
		}
		
		return result.booleanValue();
	}
	
	public boolean CompareAndSwapRaw(String key, long CasId, byte[] value, int exp)
	{
		CASResponse resp = CASResponse.NOT_FOUND;
		
		try 
		{
			resp = _spymemcached_client.cas(key, CasId, exp, value, _serial_transcoder);
		} 
		catch (Exception e) 
		{
			return false;
		}
		
		if (resp == CASResponse.OK)
		{
			return true;
		}
		
		return false;
	}
}
