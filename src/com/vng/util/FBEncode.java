package com.vng.util;

import java.io.*;
import java.util.*;

public class FBEncode
{
	private ByteArrayOutputStream 			baos = null;
	private DataOutputStream 				dos = null;
	private LinkedList<Integer> 			list_keys = null;

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


	public FBEncode()
	{
		baos = new ByteArrayOutputStream(1024);
		dos = new DataOutputStream(baos);
		list_keys = new LinkedList<Integer>();
	}

	public byte[] toByteArray()
	{
		return baos.toByteArray();
	}
	
	public LinkedList<Integer> getKeys()
	{
		return list_keys;
	}

	public String toString()
	{
		String val = "";

		try
		{
			val = baos.toString("US-ASCII");
		}
		catch (Exception ex)
		{
		}
		return val;
	}

	public boolean appendBinary(byte[] bin, LinkedList<Integer> keys)
	{
		for (Integer i : keys)
		{
			if (list_keys.contains(i))
			{
				return false;
			}
		}
		
		try
		{
			dos.write(bin, 0, bin.length);
		}
		catch (Exception ex)
		{
			return false;
		}
		
		list_keys.addAll(keys);
		
		return true;
	}

	public boolean addString(String key, String value)
	{
		int k = MurmurHash(key);
		
		if (addKey(k) == false)
		{
//			System.out.println("FBENCODE ERROR: Key \"" + key + "\" has one duplicate !");
			
			return false;
		}
		
		try
		{
			//write key
			dos.writeInt(k);
			//write type
			dos.writeByte(STRING_UTF8);
			//write UTF-8 string
			dos.writeUTF(value);
		}
		catch (Exception ex)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean addStringANSI(String key, String value)
	{
		int k = MurmurHash(key);
		
		if (addKey(k) == false)
		{
//			System.out.println("FBENCODE ERROR: Key \"" + key + "\" has one duplicate !");
			
			return false;
		}
		
		try
		{
			//write key
			dos.writeInt(k);
			//write type
			dos.writeByte(STRING_ANSI);
			//write value len
			writeVarInt(dos, value.length());
			//write ANSI string
			dos.writeBytes(value);
		}
		catch (Exception ex)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean addStringUNICODE(String key, String value)
	{
		int k = MurmurHash(key);
		
		if (addKey(k) == false)
		{
//			System.out.println("FBENCODE ERROR: Key \"" + key + "\" has one duplicate !");
			
			return false;
		}
		
		try
		{
			//write key
			dos.writeInt(k);
			//write type
			dos.writeByte(STRING_UNICODE);
			//write value len
			writeVarInt(dos, value.length());
			//write UNICODE string
			dos.writeChars(value);
		}
		catch (Exception ex)
		{
			return false;
		}
		
		return true;
	}

	public boolean addBinary(String key, byte[] value)
	{
		int k = MurmurHash(key);
		
		if (addKey(k) == false)
		{
//			System.out.println("FBENCODE ERROR: Key \"" + key + "\" has one duplicate !");
			
			return false;
		}
		
		try
		{
			//write key
			dos.writeInt(k);
			//write type
			dos.writeByte(BINARY);
			//write str value len
			if (value == null || value.length == 0)
			{
				writeVarInt(dos, 0);
			}
			else
			{
				writeVarInt(dos, value.length);
				//write str value
				dos.write(value, 0, value.length);
			}
		}
		catch (Exception ex)
		{
			return false;
		}
		
		return true;
	}

	public boolean addByte(String key, int value)
	{
		int k = MurmurHash(key);
		
		if (addKey(k) == false)
		{
//			System.out.println("FBENCODE ERROR: Key \"" + key + "\" has one duplicate !");
			
			return false;
		}
		
		try
		{
			//write key
			dos.writeInt(k);
			//write type
			dos.writeByte(BYTE);
			//write value
			dos.writeByte(value);
		}
		catch (Exception ex)
		{
			return false;
		}
		
		return true;
	}

	public boolean addBoolean (String key, boolean value)
	{
		return addByte(key, value ? 1 : 0);
	}

	public boolean addShort(String key, int value)
	{
		int k = MurmurHash(key);
		
		if (addKey(k) == false)
		{
//			System.out.println("FBENCODE ERROR: Key \"" + key + "\" has one duplicate !");
			
			return false;
		}
		
		try
		{
			//write key
			dos.writeInt(k);
			//write type
			dos.writeByte(SHORT);
			//write value
			dos.writeShort(value);
		}
		catch (Exception ex)
		{
			return false;
		}
		
		return true;
	}

	public boolean addInt(String key, int value)
	{
		int k = MurmurHash(key);
		
		if (addKey(k) == false)
		{
//			System.out.println("FBENCODE ERROR: Key \"" + key + "\" has one duplicate !");
			
			return false;
		}
		
		try
		{
			//write key
			dos.writeInt(k);
			//write type
			dos.writeByte(INT);
			//write value
			dos.writeInt(value);
		}
		catch (Exception ex)
		{
			return false;
		}
		
		return true;
	}

	public boolean addLong(String key, long value)
	{
		int k = MurmurHash(key);
		
		if (addKey(k) == false)
		{
//			System.out.println("FBENCODE ERROR: Key \"" + key + "\" has one duplicate !");
			
			return false;
		}
		
		try
		{
			//write key
			dos.writeInt(k);
			//write type len
			dos.writeByte(LONG);
			//write value
			dos.writeLong(value);
		}
		catch (Exception ex)
		{
			return false;
		}
		
		return true;
	}

	public boolean addFloat(String key, float value)
	{
		int k = MurmurHash(key);
		
		if (addKey(k) == false)
		{
//			System.out.println("FBENCODE ERROR: Key \"" + key + "\" has one duplicate !");
			
			return false;
		}
		
		try
		{
			//write key
			dos.writeInt(k);
			//write type len
			dos.writeByte(FLOAT);
			//write value
			dos.writeFloat(value);
		}
		catch (Exception ex)
		{
			return false;
		}
		
		return true;
	}

	public boolean addDouble(String key, double value)
	{
		int k = MurmurHash(key);
		
		if (addKey(k) == false)
		{
//			System.out.println("FBENCODE ERROR: Key \"" + key + "\" has one duplicate !");
			
			return false;
		}
		
		try
		{
			//write key
			dos.writeInt(k);
			//write type len
			dos.writeByte(DOUBLE);
			//write value
			dos.writeDouble(value);
		}
		catch (Exception ex)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean addBinaryArray(String key, List<byte[]> value)
	{
		int k = MurmurHash(key);
		
		if (addKey(k) == false)
		{
//			System.out.println("FBENCODE ERROR: Key \"" + key + "\" has one duplicate !");
			
			return false;
		}
		
		try 
		{
			//write key
			dos.writeInt(k);
			//write type
			dos.writeByte(ARRAY);
			//write sub type
			dos.writeByte(BINARY);
			
			//write len
			writeVarInt(dos, value.size());
			//write array
			for (byte[] v : value)
			{
				writeVarInt(dos, v.length);
				dos.write(v, 0, v.length);
			}
		}
		catch (Exception ex)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean addStringArray(String key, List<String> value)
	{
		int k = MurmurHash(key);
		
		if (addKey(k) == false)
		{
//			System.out.println("FBENCODE ERROR: Key \"" + key + "\" has one duplicate !");
			
			return false;
		}
		
		try 
		{
			//write key
			dos.writeInt(k);
			//write type
			dos.writeByte(ARRAY);
			//write sub type
			dos.writeByte(STRING_UTF8);
			
			//write len
			writeVarInt(dos, value.size());
			//write array
			for (String v : value)
			{
				dos.writeUTF(v);
			}
		}
		catch (Exception ex)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean addArray(String key, Object value) throws Exception
	{
		int k = MurmurHash(key);
		
		if (addKey(k) == false)
		{
//			System.out.println("FBENCODE ERROR: Key \"" + key + "\" has one duplicate !");
			
			return false;
		}
		
		//write key
		dos.writeInt(k);
		//write type
		dos.writeByte(ARRAY);
		
		switch (value.getClass().getName())
		{
			case "[B":
			{
				dos.writeByte(BYTE);
				byte[] v = (byte[])value;
				writeVarInt(dos, v.length);
				dos.write(v, 0, v.length);
				break;
			}
			
			case "[[B":
			{
				dos.writeByte(BINARY);
				byte[][] v = (byte[][])value;
				writeVarInt(dos, v.length);
				for (byte[] d : v)
				{
					writeVarInt(dos, d.length);
					dos.write(d, 0, d.length);
				}
				break;
			}
				
			case "[S":
			{
				dos.writeByte(SHORT);
				short[] v = (short[])value;
				writeVarInt(dos, v.length);
				
				for (short s : v)
				{
					dos.writeShort(s);
				}
				break;
			}
				
			
			case "[I":
			{
				dos.writeByte(INT);
				int[] v = (int[])value;
				writeVarInt(dos, v.length);
				
				for (int i : v)
				{
					dos.writeInt(i);
				}
				break;
			}
				
			
			case "[J":
			{
				dos.writeByte(LONG);
				long[] v = (long[])value;
				writeVarInt(dos, v.length);
				
				for (long l : v)
				{
					dos.writeLong(l);
				}
				break;
			}
				
				
			case "[F":
			{
				dos.writeByte(FLOAT);
				float[] v = (float[])value;
				writeVarInt(dos, v.length);
				
				for (float f : v)
				{
					dos.writeFloat(f);
				}
				break;
			}
			
			case "[Ljava.lang.String;":
			{
				dos.writeByte(STRING_UTF8);
				String[] v = (String[])value;
				writeVarInt(dos, v.length);
				
				for (String s : v)
				{
					dos.writeUTF(s);
				}
				break;
			}
			
			default:
			{
				throw new Exception("Dont support array type: " + value.getClass().getName());
			}
		}
		
		return true;
	}
	
	private void writeVarInt(DataOutputStream dos, int i) throws Exception
	{
		do
		{
			int a = i&0x7F;
			i >>>= 7;
			
			if (i > 0)
			{
				a |= 0x80;
			}
			
			dos.writeByte(a);
		}
		while (i > 0);
	}
	
	private boolean addKey(int key)
	{
		Integer k = new Integer(key);
		
		if (list_keys.contains(k))
		{
			return false;
		}
		
		list_keys.add(k);
		
		return true;
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