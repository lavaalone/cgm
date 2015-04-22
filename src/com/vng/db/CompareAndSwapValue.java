package com.vng.db;


class CompareAndSwapValue<T> extends java.lang.Object
{
	private long	_cas_id;
	private T		_value;
	
	public CompareAndSwapValue(long cas_id, T value)
	{
		_cas_id = cas_id;
		_value = value;
	}
	
	public long GetID()
	{
		return _cas_id;
	}
	
	public T GetValue()
	{
		return _value;
	}
}