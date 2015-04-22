package com.vng.netty;

public class SimpleDataFormat
{
	private byte[]		_data;
	private boolean		_gzip;
	
	public SimpleDataFormat(byte[] data, boolean use_gzip)
	{
		_data = data;
		_gzip = use_gzip;
	}
	
	public byte[] GetData()
	{
		return _data;
	}
	
	public boolean IsUseGzip()
	{
		return _gzip;
	}
}