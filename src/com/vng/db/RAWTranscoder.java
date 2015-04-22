package com.vng.db;

import net.spy.memcached.CachedData;
import net.spy.memcached.compat.SpyObject;
import net.spy.memcached.transcoders.*;

/**
 * Transcoder that serializes and unserializes longs.
 */
public final class RAWTranscoder extends SpyObject implements Transcoder<byte[]> 
{
	private static final int flags = (8<<8);
	
	public boolean asyncDecode(CachedData d) 
	{
		return false;
	}

	public CachedData encode(byte[] bin)
	{
		return new CachedData(flags, bin, CachedData.MAX_SIZE);
	}

	public byte[] decode(CachedData d) 
	{
		return d.getData();
	}

	public int getMaxSize() 
	{
		return CachedData.MAX_SIZE;
	}

}
