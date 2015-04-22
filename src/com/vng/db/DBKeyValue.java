package com.vng.db;

import java.util.*;

///@class 	DBKeyValue
///@brief	Abstract class for key-value Valuebases 	
///@details	
///To setup a db: 
///- new an object inherited from DBKeyValue
///- set appropriate configurations. @see SetConfigs
///- call Connect() to establish the connection
public abstract class DBKeyValue 
{
	///Members
	///@{
	protected HashMap<String, Object> m_Configs;
	///@}
	
	///For accessing data 
	///@{
	public abstract byte[] 							GetRaw(String Key) throws Exception;
	public abstract Object 							Get(String Key) throws Exception;
	
	public abstract CompareAndSwapValue<byte[]> 	GetRawAndCasId(String Key) throws Exception;
	public abstract CompareAndSwapValue<Object> 	GetValueAndCasId(String Key) throws Exception;
	
	public abstract Map<String, Object> 			GetMulti(String[] keys) throws Exception;
	public abstract Map<String, Object> 			GetMulti(Collection<String> keys) throws Exception;
	public abstract Map<String, byte[]> 			GetRawMulti(Collection<String> keys) throws Exception;
	
	public abstract boolean 						Add(String Key, Object Value);
	public abstract boolean 						Add(String Key, Object Value, int ExpireDuration);
	public abstract boolean 						AddRaw(String Key, byte[] Value);
	public abstract boolean 						AddRaw(String Key, byte[] Value, int ExpireDuration);
	
	public abstract boolean 						Set(String Key, Object Value);
	public abstract boolean 						Set(String Key, Object Value, int ExpireDuration);		//in seconds
	public abstract boolean 						SetRaw(String Key, byte[] Value);
	public abstract boolean 						SetRaw(String Key, byte[] Value, int ExpireDuration);	//in seconds
	
	public abstract boolean 						CompareAndSwap(String Key, long CasId, Object Value);
	public abstract boolean 						CompareAndSwap(String Key, long CasId, Object Value, int ExpireDuration);		//in seconds
	public abstract boolean 						CompareAndSwapRaw(String Key, long CasId, byte[] Value);
	public abstract boolean 						CompareAndSwapRaw(String Key, long CasId, byte[] Value, int ExpireDuration);	//in seconds
	
	public abstract long 							Increase(String Key, int num_incre) throws Exception;
	public abstract long 							Increase(String Key, int num_incre, long defaultValue) throws Exception;
	public abstract long 							Increase(String Key, int num_incre, long defaultValue, int ExpireDuration) throws Exception;
	
	public abstract long 							Decrease(String Key, int num_decre) throws Exception;
	public abstract long 							Decrease(String Key, int num_decre, long defaultValue) throws Exception;
	public abstract long 							Decrease(String Key, int num_decre, long defaultValue, int ExpireDuration) throws Exception;
	
	public abstract boolean 						Replace(String Key, Object Value, int ExpireDuration);
	
	public abstract boolean 						Delete(String Key);
	///@}	
	
	///For running DB
	///@{
	public abstract boolean ValidateConfigs(HashMap<String, Object> Configs);
	
	public boolean SetConfigs(HashMap<String, Object> Configs)
	{
		if (ValidateConfigs(Configs))
		{
			Set<String> KeySet = Configs.keySet();
			this.m_Configs.putAll(Configs);
			return true;
		}
		return false;
	}
	public HashMap<String, Object> GetConfigs()
	{
		return (HashMap<String, Object>) this.m_Configs.clone();
	}
	public abstract boolean 							Connect();
	public abstract boolean 							Connect(String server_address);
	public abstract void 								Disconnect();
	public abstract Map<Object, Map<String, String>> 	GetStats();
	///@}
}
