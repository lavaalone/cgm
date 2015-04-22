package com.vng.util;

import com.vng.log.LogHelper;
import org.apache.log4j.Logger;
import java.io.*;
import java.util.*;

public class FBDecode
{
	private ByteArrayInputStream 		bais = null;
	private DataInputStream 			dis = null;
	private HashMap<Integer, Object> 	set = null;

	private int							_max_input_len = 1024;

	//data type
	private static final int		STRING_UTF8		=	1;
	private static final int		BYTE			=	2;
	private static final int		SHORT			=	3;
	private static final int		INT				=	4;
	private static final int		LONG			=	5;
	private static final int		FLOAT			=	6;
	private static final int		DOUBLE			=	7;
	private static final int		BINARY			=	8;
	private static final int		STRING_ANSI		=	9;
	private static final int		STRING_UNICODE	=	10;
	private static final int		ARRAY			=	11;


	public FBDecode(int max_input_len)
	{
		_max_input_len = max_input_len;
		set = new HashMap<Integer, Object>();
	}

	public boolean decode(byte[] bin)
	{
		if (bin.length > _max_input_len)
		{
			return false;
		}

		bais = new ByteArrayInputStream(bin);
		dis = new DataInputStream(bais);
		set.clear();

		//boolean is_eof = false;
		try
		{
			while (dis.available() > 0)
			{
				//read key
				int k = dis.readInt();
				Integer key = new Integer(k);

				//read type len
				int type = dis.readByte()&0xFF;
	
				//read value
				switch (type)
				{
					case BYTE:						
						set.put(key, Byte.valueOf(dis.readByte()));
						break;

					case SHORT:
						set.put(key, Short.valueOf(dis.readShort()));
						break;

					case INT:
						set.put(key, Integer.valueOf(dis.readInt()));
						break;

					case LONG:
						set.put(key, Long.valueOf(dis.readLong()));
						break;

					case FLOAT:
						set.put(key, Float.valueOf(dis.readFloat()));
						break;

					case DOUBLE:
						set.put(key, Double.valueOf(dis.readDouble()));
						break;

					case STRING_UTF8:
						int s_len = dis.readShort() & 0xffff;
						String s_str = "";
						if (s_len > 0 && s_len < _max_input_len)	//max str len < _max_input_len
						{
							byte[] data = new byte[s_len];
							dis.read(data);
							s_str = new String(data, java.nio.charset.StandardCharsets.UTF_8);
						}
						set.put(key, s_str);
						break;

					case BINARY:
						int b_len = readVarInt(dis);

						if (b_len > 0 && b_len < _max_input_len)	//max binary len < _max_input_len
						{
							byte[] val = new byte[b_len];
							dis.read(val);
							set.put(key, val);
						}
						else
						{
							set.put(key, null);
						}
						break;
						
					case STRING_ANSI:
						int sa_len = readVarInt(dis);
						String sa_str = "";
						if (sa_len > 0 && sa_len < _max_input_len)	//max str len < _max_input_len
						{
							byte[] data = new byte[sa_len];
							dis.read(data);
							sa_str = new String(data, java.nio.charset.StandardCharsets.US_ASCII);
						}
						set.put(key, sa_str);
						break;
					
					case STRING_UNICODE:
						int su_len = readVarInt(dis) * 2;
						String su_str = "";
						if (su_len > 0 && su_len < _max_input_len)	//max str len < _max_input_len
						{
							byte[] data = new byte[su_len];
							dis.read(data);
							su_str = new String(data, java.nio.charset.StandardCharsets.UTF_16LE);
						}
						set.put(key, su_str);
						break;
						
					case ARRAY:
					
						if (decodeArray(key, dis) == false)
						{
							return false;
						}
						break;
					
					default:
						return false;
				}
			}
		}
		catch (Exception ex)
		{
			LogHelper.LogException("FBDecode.decode", ex);
			return false;
		}
		return true;
	}
	
	private boolean decodeArray(Integer key, DataInputStream dis)
	{
		try
		{
			int type = dis.readByte()&0xFF;
		
			switch (type)
			{
				case BYTE:						
				{
					int len = readVarInt(dis);
					
					if (len > 0 && len < _max_input_len)
					{
						byte[] value = new byte[len];
						
						for (int i = 0; i < len; i++)
						{
							value[i] = dis.readByte();
						}
						
						set.put(key, value);
					}
					else
					{
						return false;
					}
					
					break;
				}
					

				case SHORT:
				{
					int len = readVarInt(dis);
					
					if (len > 0 && len < _max_input_len)
					{
						short[] value = new short[len];
						
						for (int i = 0; i < len; i++)
						{
							value[i] = dis.readShort();
						}
						
						set.put(key, value);
					}
					else
					{
						return false;
					}
					
					break;
				}

				case INT:
				{
					int len = readVarInt(dis);
					
					if (len > 0 && len < _max_input_len)
					{
						int[] value = new int[len];
						
						for (int i = 0; i < len; i++)
						{
							value[i] = dis.readInt();
						}
						
						set.put(key, value);
					}
					else
					{
						return false;
					}
					
					break;
				}

				case LONG:
				{
					int len = readVarInt(dis);
					
					if (len > 0 && len < _max_input_len)
					{
						long[] value = new long[len];
						
						for (int i = 0; i < len; i++)
						{
							value[i] = dis.readLong();
						}
						
						set.put(key, value);
					}
					else
					{
						return false;
					}
					
					break;
				}

				case FLOAT:
				{
					int len = readVarInt(dis);
					
					if (len > 0 && len < _max_input_len)
					{
						float[] value = new float[len];
						
						for (int i = 0; i < len; i++)
						{
							value[i] = dis.readFloat();
						}
						
						set.put(key, value);
					}
					else
					{
						return false;
					}
					
					break;
				}

				case STRING_UTF8:
				{
					int len = readVarInt(dis);
					
					if (len > 0 && len < _max_input_len)
					{
						String[] value = new String[len];
						
						for (int i = 0; i < len; i++)
						{
							int s_len = dis.readShort() & 0xffff;
						
							if (s_len == 0)
							{
								value[i] = "";
							}
							else if (s_len > 0 && s_len < _max_input_len)	//max str len < _max_input_len
							{
								byte[] data = new byte[s_len];
								dis.read(data);
								String s_str = new String(data, java.nio.charset.StandardCharsets.UTF_8);
								value[i] = s_str;
							}
							else
							{
								return false;
							}
						}
						
						set.put(key, value);
					}
					else
					{
						return false;
					}
					
					break;
				}
				
				case BINARY:
				{
					int len = readVarInt(dis);
					
					if (len > 0 && len < _max_input_len)
					{
						byte[][] value = new byte[len][];
						
						for (int i = 0; i < len; i++)
						{
							int s_len = readVarInt(dis);
						
							if (s_len > 0 && s_len < _max_input_len)	//max str len < _max_input_len
							{
								byte[] data = new byte[s_len];
								dis.read(data);
								value[i] = data;
							}
							else
							{
								return false;
							}
						}
						
						set.put(key, value);
					}
					else
					{
						return false;
					}
					
					break;
				}
				
				default:
					return false;
			}
		}
		catch (Exception ex)
		{
			return false;
		}
		
		return true;
	}

	public boolean hasKey(String key)
	{
		int k = MurmurHash(key);
		
		return set.containsKey(new Integer(k));
	}

	public Object getValue(String key)
	{
		int k = MurmurHash(key);
		
		return set.get(new Integer(k));
	}

	public String getString(String key)
	{
		int k = MurmurHash(key);
		
		Object val = set.get(new Integer(k));
		
		if (val instanceof String)
		{
			return (String)val;
		}
		
		return "";
	}
	
	public String[] getStringArray(String key)
	{
		int k = MurmurHash(key);
		
		Object val = set.get(new Integer(k));
		
		if (val instanceof String[])
		{
			return (String[])val;
		}
		
		return null;
	}

	public byte[] getBinary(String key)
	{
		int k = MurmurHash(key);
		Object val = set.get(new Integer(k));
		if (val != null && val instanceof byte[])
		{
			return (byte[])val;
		}
		return null;
	}
	
	public byte[][] getBinaryArray(String key)
	{
		int k = MurmurHash(key);
		Object val = set.get(new Integer(k));
		if (val != null && val instanceof byte[][])
		{
			return (byte[][])val;
		}
		return null;
	}

	public byte getByte(String key)
	{
		int k = MurmurHash(key);
		Object val = set.get(new Integer(k));
		if (val instanceof Byte)
		{
			return ((Byte)val).byteValue();
		}
		return -1;
	}
	
	public byte[] getByteArray(String key)
	{
		int k = MurmurHash(key);
		Object val = set.get(new Integer(k));
		if (val instanceof byte[])
		{
			return ((byte[])val);
		}
		return null;
	}

	public boolean getBoolean (String key)
	{
		return (getByte(key)==1);
	}

	public short getShort(String key)
	{
		int k = MurmurHash(key);
		Object val = set.get(new Integer(k));
		if (val instanceof Short)
		{
			return ((Short)val).shortValue();
		}
		return -1;
	}
	
	public short[] getShortArray(String key)
	{
		int k = MurmurHash(key);
		Object val = set.get(new Integer(k));
		if (val instanceof short[])
		{
			return ((short[])val);
		}
		return null;
	}

	public int getInt(String key)
	{
		int k = MurmurHash(key);
		Object val = set.get(new Integer(k));
		if (val instanceof Integer)
		{
			return ((Integer)val).intValue();
		}
		return -1;
	}
	
	public int[] getIntArray(String key)
	{
		int k = MurmurHash(key);
		Object val = set.get(new Integer(k));
		if (val instanceof int[])
		{
			return ((int[])val);
		}
		return null;
	}

	public long getLong(String key)
	{
		int k = MurmurHash(key);
		Object val = set.get(new Integer(k));
		if (val instanceof Long)
		{
			return ((Long)val).longValue();
		}
		return -1;
	}
	
	public long[] getLongArray(String key)
	{
		int k = MurmurHash(key);
		Object val = set.get(new Integer(k));
		if (val instanceof long[])
		{
			return ((long[])val);
		}
		return null;
	}

	public float getFloat(String key)
	{
		int k = MurmurHash(key);
		Object val = set.get(new Integer(k));
		if (val instanceof Float)
		{
			return ((Float)val).floatValue();
		}
		return -1;
	}
	
	public float[] getFloatArray(String key)
	{
		int k = MurmurHash(key);
		Object val = set.get(new Integer(k));
		if (val instanceof float[])
		{
			return ((float[])val);
		}
		return null;
	}

	public double getDouble(String key)
	{
		int k = MurmurHash(key);
		Object val = set.get(new Integer(k));
		if (val instanceof Double)
		{
			return ((Double)val).doubleValue();
		}
		return -1;
	}
	
	private int readVarInt(DataInputStream dis) throws Exception
	{
		int v = 0;
		int i = 0;
		int b = 0;
		
		do
		{
			b = dis.readByte()&0xFF;
			
			v |= (b&0x7F)<<i;
			
			i += 7;
		}
		while (b > 127);
		
		return v;
	}

	private static int MurmurHash(String key)
	{
		byte[] data = key.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
		
		int len = data.length;
		
		final int c1 = 0xcc9e2d51;
		final int c2 = 0x1b873593;

		int h1 = 123456;
		int roundedEnd = (len & 0xfffffffc);  // round down to 4 byte block

		for (int i = 0; i < roundedEnd; i += 4)
		{
			// little endian load order
			int k1 = (data[i] & 0xff) | ((data[i+1] & 0xff) << 8) | ((data[i+2] & 0xff) << 16) | (data[i+3] << 24);
			k1 *= c1;
			k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
			k1 *= c2;

			h1 ^= k1;
			h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
			h1 = h1*5+0xe6546b64;
		}

		// tail
		int k1 = 0;

		switch (len & 0x03)
		{
			case 3:
				k1 = (data[roundedEnd + 2] & 0xff) << 16;
				// fallthrough
			case 2:
				k1 |= (data[roundedEnd + 1] & 0xff) << 8;
				// fallthrough
			case 1:
				k1 |= (data[roundedEnd] & 0xff);
				k1 *= c1;
				k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
				k1 *= c2;
				h1 ^= k1;
		}

		// finalization
		h1 ^= len;

		// fmix(h1);
		h1 ^= h1 >>> 16;
		h1 *= 0x85ebca6b;
		h1 ^= h1 >>> 13;
		h1 *= 0xc2b2ae35;
		h1 ^= h1 >>> 16;

		return h1;
	}
}