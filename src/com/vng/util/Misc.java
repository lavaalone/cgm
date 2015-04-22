package com.vng.util;

import com.vng.log.LogHelper;
import com.vng.skygarden.DBConnector;
import com.vng.skygarden.game.*;
import com.vng.netty.Server;
import com.vng.netty.ServerHandler;
import com.vng.skygarden.SkyGarden;
import com.vng.skygarden._gen_.ProjectConfig;

import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.zip.*;
import java.text.SimpleDateFormat;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
// import org.apache.commons.lang3.RandomStringUtils;

public class Misc
{
	public static Random random = new Random();
	
	public static final int _MINUTE_SECONDS = 60;
	public static final int _HOUR_SECONDS = 3600;
	public static final int _DAY_SECONDS = 86400;
	public static final int _WEEK_SECONDS = 604800;

	public static final int MAX_GIFT_CODE_LENGTH = 9;
	public static final int GIFT_CODE_HEADER = 3;
	public static final int GIFT_CODE_LENGTH = 6;
	public static final String AB = "123456789ABCDEFGHIJKLMNPQRSTUVWXYZ";	// remove 0 & O

	// public static String generateUniqueId()
	// {
		// return RandomStringUtils.randomAlphanumeric(GIFT_CODE_LENGTH).toUpperCase();
	// }
	
	// for case we dont want any char appear in generate string
	public static String generateRandomString() 
	{
		StringBuilder sb = new StringBuilder(GIFT_CODE_LENGTH);
		
		for( int i = 0; i < GIFT_CODE_LENGTH; i++ )
		{
			sb.append(AB.charAt(random.nextInt(AB.length())));
		}
		
		return sb.toString();
	}
	
	public static int divCeil (int num, int div)
	{
		if (num % div == 0)
			return num / div;
		else
			return (num / div) + 1;
	}
	
	static public byte[] readBytes(DataInputStream dis)
	{
		try
		{
			int len = dis.readShort();
			
			if (len > 0)
			{
				byte[] bytes = new byte[len];
				
				dis.read(bytes);
				
				return bytes;
			}
		}
		catch (Exception ex)
		{
			//LOG_EXCEPTION(ex);
		}
		
		return null;
	}
	
	static public String readString(DataInputStream dis)
	{
		try
		{
			int len = dis.readByte()&0xFF;
			
			if (len > 0)
			{
				byte[] bstr = new byte[len];
				
				dis.read(bstr);
				
				String str;
				try
				{
					str = new String(bstr, "UTF-8");
				}
				catch(Exception e)
				{
					str = new String(bstr);
				}
				
				return str;
			}
		}
		catch (Exception ex)
		{
			//LOG_EXCEPTION(ex);
		}
		
		return "";
	}
	
	static public void writeBytes(DataOutputStream dos, byte[] bytes)
	{
		try
		{
			if (bytes == null)
			{
				dos.writeShort(0);
				return;
			}
			
			int len = bytes.length;
			dos.writeShort(len);
			
			dos.write(bytes, 0, len);
		}
		catch (Exception ex)
		{
			//LOG_EXCEPTION(ex);
		}
	}
	
	static public void writeString(DataOutputStream dos, String str)
	{
		try
		{		
			int strlen;
			if ((str == null) || ((strlen = str.length()) == 0))
			{
				dos.writeByte(0);
				return;
			}
			
			int utflen = 0;
			int c;

			for (int i = 0; i < strlen; i++) 
			{
				c = str.charAt(i);
				if ((c >= 0x0001) && (c <= 0x007F))
					utflen++;
				else if (c > 0x07FF)
					utflen += 3;
				else
					utflen += 2;
			}

			if (utflen > 256)
				throw new Exception("writeString: string too long: " + utflen + " bytes");

			dos.writeByte(utflen);

			int i = 0;
			for (i = 0; i < strlen; i++) 
			{
				c = str.charAt(i);
				if (!((c >= 0x0001) && (c <= 0x007F))) 
					break;
				dos.writeByte(c);
			}

			for (;i < strlen; i++)
			{
				c = str.charAt(i);
				if ((c >= 0x0001) && (c <= 0x007F)) 
				{
					dos.writeByte(c);
				} 
				else if (c > 0x07FF) 
				{
					dos.writeByte(0xE0 | ((c >> 12) & 0x0F));
					dos.writeByte(0x80 | ((c >>  6) & 0x3F));
					dos.writeByte(0x80 | ((c >>  0) & 0x3F));
				}
				else 
				{
					dos.writeByte(0xC0 | ((c >>  6) & 0x1F));
					dos.writeByte(0x80 | ((c >>  0) & 0x3F));
				}
			}
		}
		catch (Exception ex)
		{
			//LOG_EXCEPTION(ex);
		}
	}
	
	static public String Hash(String str, String algorithm)
	{
		try
		{
			MessageDigest digester = MessageDigest.getInstance(algorithm);
			byte[] bytes = digester.digest(str.getBytes());
			StringBuffer sb = new StringBuffer();
			
			for (int i = 0; i < bytes.length; i++)
			{
				int val = 0xff & bytes[i];
				
				if (val <= 0x0f)
				{
					sb.append('0');
				}
			
				sb.append(Integer.toHexString(val));
			}
			
			return sb.toString();
		}
		catch(Exception e)
		{
			//LOG_EXCEPTION(e);
		}
		
		return null;
	}
	
	static public String Hash(byte[] input, String algorithm)
	{
		try
		{
			MessageDigest digester = MessageDigest.getInstance(algorithm);
			byte[] bytes = digester.digest(input);
			StringBuffer sb = new StringBuffer();
			
			for (int i = 0; i < bytes.length; i++)
			{
				int val = 0xff & bytes[i];
				
				if (val <= 0x0f)
				{
					sb.append('0');
				}
			
				sb.append(Integer.toHexString(val));
			}
			
			return sb.toString();
		}
		catch(Exception e)
		{
			//LOG_EXCEPTION(e);
		}
		
		return null;
	}
	
	static public int daydiff(long t1, long t2)
	{
		int d1 = (int)(t1/_DAY_SECONDS);
		int d2 = (int)(t2/_DAY_SECONDS);
		return Math.abs(d2 - d1);
	}

	static public long MemUsage()
	{
		Runtime rt = Runtime.getRuntime();
		return rt.totalMemory() - rt.freeMemory();
	}
	
	public static int MurmurHash(byte[] data)
	{
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
	
	public static int MurmurHash(String key)
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
	
	public static int FHash(String s)
	{		
		//With ansi string use toCharArray(), otherwise use getBytes()
		//toCharArray() is 5 time faster than getBytes()		
		char[] bytes = s.toCharArray();
		int h = bytes.length;
		int rem = h & 3;
		int j = 0;
		int i = h & 0xfffffffc;				
		h += 119891733;
		while (j < i)
		{
			h += (bytes[j++] & 0xff) | ((bytes[j++] & 0xff) << 8);			
			h = (h << 16) ^ ((((bytes[j++] & 0xff) | ((bytes[j++] & 0xff) << 8)) << 11) ^ h);
			h += h >> 11;
		}		
		switch (rem)
		{
			case 1:			
				h += (bytes[j++] & 0xff);
				h ^= h << 10;
				h += h >> 1;	
				break;
			case 2:			
				h += (bytes[j++] & 0xff) | ((bytes[j++] & 0xff) << 8);
				h ^= h << 11;
				h += h >> 17;				
				break;
			case 3:			
				h += (bytes[j++] & 0xff) | ((bytes[j++] & 0xff) << 8);
				h ^= h << 16;
				h ^= (bytes[j++] & 0xff) << 18;
				h += h >> 11;				
				break;
		}
		h ^= h << 3;
		h += h >> 5;
		h ^= h << 4;
		h += h >> 17;
		h ^= h << 25;
		h += h >> 6;
		return h;
	}
	
	public static int MHash (int a, int b, int c, int h)
	{	
		int k = 0;	
		
		k = a * 0xcc9e2d51;	
		h ^= ((k << 15) | (k >> 17)) * 0x1b873593;
		h = ((h << 13) | (h >> 19)) * 5 + 0xe6546b64;
		
		k = b * 0xcc9e2d51;	
		h ^= ((k << 15) | (k >> 17)) * 0x1b873593;
		h = ((h << 13) | (h >> 19)) * 5 + 0xe6546b64;
		
		k = c * 0xcc9e2d51;	
		h ^= ((k << 15) | (k >> 17)) * 0x1b873593;
		h = ((h << 13) | (h >> 19)) * 5 + 0xe6546b64;
		
		h ^= 12; //3 var * 4 byte = 12
		h ^= h >> 16;
		h *= 0x85ebca6b;
		h ^= h >> 13;
		h *= 0xc2b2ae35;
		h ^= h >> 16;	
		return h;
	}
	
	public static byte[] Compress(byte[] data) 
	{	
		if (data != null)
		{		
			try
			{				
				ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
				Deflater compresser = new Deflater(Deflater.BEST_COMPRESSION);							
				compresser.setInput(data);
				compresser.finish();
				byte[] buf = new byte[2048];
				int len;
				while (!compresser.finished())
				{					
					len = compresser.deflate(buf);		
					if (len == 0)
					{						
						break;
					}
					bos.write(buf, 0, len);
				}
				compresser.end();
				
				return bos.toByteArray();				
			}
			catch (Exception e)
			{
			}
		}
		
		return null;
	}
	
	public static byte[] Decompress(byte[] data) 
	{	
		if (data != null)
		{		
			try
			{				
				ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length << 2);
				Inflater decompresser = new Inflater();							
				decompresser.setInput(data);
				byte[] buf = new byte[2048];
				int len;
				while (!decompresser.finished())
				{					
					len = decompresser.inflate(buf);		
					if (len == 0)
					{						
						break;
					}
					bos.write(buf, 0, len);
				}
				decompresser.end();
				
				return bos.toByteArray();				
			}
			catch (Exception e)
			{
			}
		}
		return null;
	}
	
	public static byte[] CompressGZIP(byte[] data) 
	{	
		if (data != null)
		{		
			try
			{				
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				GZIPOutputStream gzip = new GZIPOutputStream(out);				
				gzip.write(data);
				gzip.close();

				return out.toByteArray();				
			}
			catch (Exception e)
			{
			}
		}
		
		return null;
	}
	
	public static byte[] DecompressGZIP(byte[] data) 
	{	
		if (data != null)
		{		
			try
			{
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(data));

				byte[] buf = new byte[2048];
				int len;
				
				while ((len = gis.read(buf)) > 0)
				{
					out.write(buf, 0, len);
				}
				gis.close();
				
				return out.toByteArray();
			}
			catch (Exception e)
			{
			}
		}
		
		return null;
	}
	
	public static String SendRequest(String link, int initSize, int connectTimeout, int readTimeout, String errResult)
	{		
		try
		{
			URL url = new URL(link);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder(initSize);
			String temp;
			while ((temp = in.readLine()) != null)
			{
				sb.append(temp).append("\n");
			}
			in.close();
			return sb.toString();
		}
		catch (Exception e)
		{		
		}
		return errResult;
	}

	public static int getDiamondEstimate(int type, int complete_time)
	{
		int _time_range 		= DatabaseID.DIAMOND_SKIP_TIME_PLANT_TIME_RANGE;
		int _ratio 				= DatabaseID.DIAMOND_SKIP_TIME_PLANT_RATIO;
		int _diamond_default 	= DatabaseID.DIAMOND_SKIP_TIME_PLANT_DIAMOND_DEFAULT;
		
		switch (type)
		{
			case DatabaseID.DIAMOND_SKIP_TIME_PRODUCT:
				_time_range 		= DatabaseID.DIAMOND_SKIP_TIME_PRODUCT_TIME_RANGE;
				_ratio 				= DatabaseID.DIAMOND_SKIP_TIME_PRODUCT_RATIO;
				_diamond_default 	= DatabaseID.DIAMOND_SKIP_TIME_PRODUCT_DIAMOND_DEFAULT;
				break;
				
			case DatabaseID.DIAMOND_SKIP_TIME_MACHINE:
				_time_range 		= DatabaseID.DIAMOND_SKIP_TIME_MACHINE_TIME_RANGE;
				_ratio 				= DatabaseID.DIAMOND_SKIP_TIME_MACHINE_RATIO;
				_diamond_default 	= DatabaseID.DIAMOND_SKIP_TIME_MACHINE_DIAMOND_DEFAULT;
				break;
		}
		
		int index = -1;
		int current_time = SECONDS();
		long remain_time = complete_time - current_time;
		
		if (remain_time <= 0) return 0;

		for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_DIAMOND_SKIP_TIME].length; i++)
		{
			long range_time = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DIAMOND_SKIP_TIME][i][_time_range]);
				
			if (remain_time < range_time)
			{
				index = i - 1;
				break;
			}
		}
		
		if (remain_time >= Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DIAMOND_SKIP_TIME][Server.s_globalDB[DatabaseID.SHEET_DIAMOND_SKIP_TIME].length - 1][_time_range]))
		{
			index = Server.s_globalDB[DatabaseID.SHEET_DIAMOND_SKIP_TIME].length - 1;
		}
		
		if (index < 0) return 0;
		
		long last_range_time = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DIAMOND_SKIP_TIME][index][_time_range]);
		double ratio = Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_DIAMOND_SKIP_TIME][index][_ratio]);
		long diamond_default = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DIAMOND_SKIP_TIME][index][_diamond_default]);
		
		int last_diamond_estimate = (int)((int)(Math.ceil((((remain_time - last_range_time) * ratio) / 100) * 100.0 / 100.0)) + diamond_default);
		
		if (last_diamond_estimate < 0)
		{
			LogHelper.Log("********* last_diamond_estimate = " + last_diamond_estimate + " < 0, NEED RE-CHECK getDiamondEstimate() FUNCTION");
			last_diamond_estimate = 0;
		}
		
		return last_diamond_estimate;
	}
	
	public static int initReceiveDailyOrderDiamond(int user_level, int daily_order_index, long daily_order_gold_reward)
	{
		if (daily_order_index >= Server.s_globalDB[DatabaseID.SHEET_DAILY_ORDER].length)
		{
			return 0;
		}
		
		long gold_per_diamond = PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DAILY_ORDER][daily_order_index][DatabaseID.DAILY_ORDER_GOLD_PER_DIAMOND]);
		long gold_reward = daily_order_gold_reward;
		double daily_order_diamond_ratio = PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][DatabaseID.CONSTANT_DAILY_ORDER_DIAMOND_RATIO]);
		
		int diamond_need = (int)Math.round((daily_order_diamond_ratio * gold_reward) / gold_per_diamond);
		
		// lam tron len
		// int diamond_need = (int)(Math.ceil(((daily_order_diamond_ratio * gold_reward) / gold_per_diamond) * 100.0 / 100.0));

		if (diamond_need <= 0) diamond_need = 1;
		
		// LogHelper.Log("gold_per_diamond = " + gold_per_diamond);
		// LogHelper.Log("gold_reward = " + gold_reward);
		// LogHelper.Log("daily_order_diamond_ratio = " + daily_order_diamond_ratio);
		// LogHelper.Log("diamond_need = " + diamond_need);
		
		return diamond_need;
	}
	
	// Ước lượng hệ số vàng/xp trong order
	public static long getGoldXPCoefficientEstimate(int order_type, int type_need, int item_type, int item_id, int item_num, boolean is_DO_paid, int user_level)
	{
		if (item_num < 0)
		{
			LogHelper.Log("********* item_num = " + item_num + " < 0, NEED RE-CHECK getGoldXPCoefficientEstimate() FUNCTION");
			return 0;
		}
		
		int rate = RANDOM_RANGE(1, 100);
		int index = 0;
		
		for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_GOLD_XP_COEFFICIENT].length; i++)
		{
			long rate_range = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_GOLD_XP_COEFFICIENT][i][DatabaseID.GOLD_XP_COEFFICIENT_RATE_RANGE]);
			
			if (rate <= rate_range)
			{
				index = i;
				break;
			}
		}
		
		double coefficient_min = Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_GOLD_XP_COEFFICIENT][index][DatabaseID.GOLD_XP_COEFFICIENT_MIN]);
		double coefficient_max = Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_GOLD_XP_COEFFICIENT][index][DatabaseID.GOLD_XP_COEFFICIENT_MAX]);
		int coefficient_value = (int)Math.round((double)(coefficient_max - coefficient_min) * 100);
		
		double coefficient_rand = RANDOM_RANGE(0, coefficient_value);
		double coefficient_final = coefficient_min + (coefficient_rand / 100);
		
		double basic_rate = 0;
		
		if (item_type == DatabaseID.IT_PLANT)
		{
			if (order_type == DatabaseID.ORDER_DAILY)
			{
				int DO_type = (is_DO_paid ? DatabaseID.SEED_EXP_BASIC_DO_PAID : DatabaseID.SEED_EXP_BASIC_DO_FREE);
				int type = (type_need == DatabaseID.GOLD_ID ? DatabaseID.SEED_GOLD_BASIC_DO : DO_type);
				basic_rate = Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_SEED][item_id][type]);
				
				int coefficient_type = -1;
				
				if (type_need == DatabaseID.GOLD_ID)
				{
					coefficient_type = (is_DO_paid ? DatabaseID.CONSTANT_DO_PAID_GOLD_COEFFICIENT_RATE : DatabaseID.CONSTANT_DO_FREE_GOLD_COEFFICIENT_RATE);
				}
				else
				{
					coefficient_type = (is_DO_paid ? DatabaseID.CONSTANT_DO_PAID_EXP_COEFFICIENT_RATE : DatabaseID.CONSTANT_DO_FREE_EXP_COEFFICIENT_RATE);
				}
				
				coefficient_final = Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][coefficient_type]);
				
				// int coefficient_type = (is_DO_paid ? DatabaseID.CONSTANT_DO_PAID_COEFFICIENT_RATE : DatabaseID.CONSTANT_DO_FREE_COEFFICIENT_RATE);
				// coefficient_final = Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][coefficient_type]);
			}
			else
			{
				int type = (type_need == DatabaseID.GOLD_ID ? DatabaseID.SEED_GOLD_BASIC : DatabaseID.SEED_EXP_BASIC);
				basic_rate = Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_SEED][item_id][type]);
				
				int coefficient_type = (type_need == DatabaseID.GOLD_ID ? DatabaseID.CONSTANT_NO_GOLD_COEFFICIENT_RATE : DatabaseID.CONSTANT_NO_XP_COEFFICIENT_RATE);
				coefficient_final = Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][coefficient_type]);
			}
		}
		else if (item_type == DatabaseID.IT_PRODUCT)
		{
			int type = (type_need == DatabaseID.GOLD_ID ? DatabaseID.PRODUCT_GOLD_BASIC : DatabaseID.PRODUCT_EXP_BASIC);
			basic_rate = Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_PRODUCT][item_id][type]);
			
			if (isInBugPearlList(item_id))
			{
				coefficient_final = Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][DatabaseID.CONSTANT_DO_BUG_PEARL_COEFFICIENT_RATE]);
			}
			else
			{
				int coefficient_type = (type_need == DatabaseID.GOLD_ID ? DatabaseID.CONSTANT_NO_GOLD_COEFFICIENT_RATE : DatabaseID.CONSTANT_NO_XP_COEFFICIENT_RATE);
				coefficient_final = Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][coefficient_type]);
			}
		}
		else if (item_type == DatabaseID.IT_BUG)
		{
			int type = (type_need == DatabaseID.GOLD_ID ? DatabaseID.PEST_GOLD_BASIC : DatabaseID.PEST_EXP_BASIC);
			basic_rate = Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_PEST][item_id][type]);
			coefficient_final = Misc.PARSE_DOUBLE(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][DatabaseID.CONSTANT_DO_BUG_PEARL_COEFFICIENT_RATE]);
		}
		else
		{
			return 0;
		}
		
		return (long)(Math.round(item_num * basic_rate * coefficient_final));
	}

	public static final int[] itemListPearl =	{
															DatabaseID.PRODUCT_ID_NGOCDO,
															DatabaseID.PRODUCT_ID_NGOCVANG,
															DatabaseID.PRODUCT_ID_NGOCXANHBIEN,
															DatabaseID.PRODUCT_ID_NGOCCAM,
															DatabaseID.PRODUCT_ID_NGOCTIM,
															DatabaseID.PRODUCT_ID_NGOCXANHLA,
														};
	
	public static final int[] itemListBugAndPearl =	{
															DatabaseID.PRODUCT_ID_BORUA,
															DatabaseID.PRODUCT_ID_DOMDOM,
															DatabaseID.PRODUCT_ID_OCSEN,
															DatabaseID.PRODUCT_ID_NGOCDO,
															DatabaseID.PRODUCT_ID_NGOCVANG,
															DatabaseID.PRODUCT_ID_NGOCXANHBIEN,
															DatabaseID.PRODUCT_ID_NGOCCAM,
															DatabaseID.PRODUCT_ID_NGOCTIM,
															DatabaseID.PRODUCT_ID_NGOCXANHLA,
															DatabaseID.PRODUCT_ID_HATHUONGDUONG,
															DatabaseID.PRODUCT_ID_CHUONCHUON,
															DatabaseID.PRODUCT_ID_BUOM,
															DatabaseID.PRODUCT_ID_ONG,
														};
	
	public static boolean isInBugPearlList(int id)
	{
		for (int i = 0; i < itemListBugAndPearl.length; i++)
		{
			if (id == itemListBugAndPearl[i])
			{
				return true;
			}
		}
		
		return false;
	}

	public static boolean isInPearlList(int id)
	{
		for (int i = 0; i < itemListPearl.length; i++)
		{
			if (id == itemListPearl[i])
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static byte getMachineID(int floor_num)
	{
        /*
		int machine_id = -1;
		
		for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_MACHINE].length; i++)
		{
			int _floor_num = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_MACHINE][i][DatabaseID.MACHINE_FLOOR]);
			if (_floor_num == floor_num)
			{
				machine_id = i;
				break;
			}
		}
		
		if (machine_id == -1)
		{
			LogHelper.Log("\n\ngetMachineID(): invalid machine id\n\n");
		}
		
		return (byte)machine_id;
		*/
		
		return (byte)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_MACHINE][floor_num][DatabaseID.MACHINE_ID]);
	}

	public static int getDailyResetTime()
	{
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		
		String dailyResetTime = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
		dailyResetTime = dailyResetTime + " " + "06:00:00";
		
		cal.add(Calendar.DAY_OF_YEAR, 1);
		
		Date tomorrow = cal.getTime();
		String tomorrow_date = new SimpleDateFormat("dd/MM/yyyy").format(tomorrow);
		
		String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
		String resetTime = tomorrow_date + " " + "06:00:00";
		
		Date t1 = null;
        Date t2 = null;
        Date t_daily_reset = null;
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
        try
		{
            t1 = format.parse(currentTime);
            t2 = format.parse(resetTime);
            t_daily_reset = format.parse(dailyResetTime);
        }
		catch (Exception ex)
		{
            ex.printStackTrace();
        }
		
		int reset_time = 0;
		
		if (t1.getTime() < t_daily_reset.getTime())
		{
			reset_time = (int)((t_daily_reset.getTime() - t1.getTime())/1000);	// tu 0-6g sang
		}
		else	// tu 6g sang den 24g
		{
			reset_time = (int)((t2.getTime() - t1.getTime())/1000);
		}
		
		return (Misc.SECONDS() + reset_time);
	}
	
	public static int GetServerCurrentResetTime()
	{
		String current_date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
		
		boolean after6AM = false;
		try
		{
			String now = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
			Date date_now = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(now);
			
			String zero_time = current_date + " " + "00:00:00";
			Date date_zero_time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(zero_time);
			
			after6AM = (date_now.getTime() - date_zero_time.getTime()) > 6 * 60 * 60 * 1000;
		}
		catch (Exception e)
		{
			LogHelper.LogException("GetServerCurrentResetTime", e);
			return -1;
		}
		
		String current_reset_time = "";
		if (after6AM)
		{
			current_reset_time = current_date + " " + "06:00:00";
		}
		else
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			String yesterday = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
			current_reset_time = yesterday + " " + "06:00:00";
		}
		
		Date result = null;
		
		try
		{
			result = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(current_reset_time);
		}
		catch (Exception e)
		{
			LogHelper.LogException("GetServerCurrentResetTime", e);
			return -1;
		}
		
		return (int)(result.getTime()/1000);
	}
	
	public static String GetServerCurrentResetTimeStr() throws Exception
	{
		String current_date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

		String now = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
		Date date_now = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(now);

		String zero_time = current_date + " " + "00:00:00";
		Date date_zero_time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(zero_time);

		boolean after6AM = (date_now.getTime() - date_zero_time.getTime()) > 6 * 60 * 60 * 1000;
		
		String current_reset_time = "";
		if (after6AM)
		{
			current_reset_time = current_date + " " + "06:00:00";
		}
		else
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			String yesterday = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
			current_reset_time = yesterday + " " + "06:00:00";
		}
		
		return current_reset_time;
	}
	
	public static int GetServerNextResetTime()
	{
		String current_date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
		
		boolean after6AM = false;
		try
		{
			String now = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
			Date date_now = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(now);
			
			String zero_time = current_date + " " + "00:00:00";
			Date date_zero_time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(zero_time);
			
			after6AM = (date_now.getTime() - date_zero_time.getTime()) > 6 * 60 * 60 * 1000;
		}
		catch (Exception e)
		{
			LogHelper.LogException("GetServerCurrentResetTime", e);
			return -1;
		}
		
		String s_nextday = "";
		if (after6AM)
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DAY_OF_YEAR, 1);

			String s = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
			s_nextday  = s + " " + "06:00:00";
		}
		else
		{
			s_nextday = current_date + " " + "06:00:00";
		}
		
		
		Date result = null;
		
		try
		{
			result = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(s_nextday);
		}
		catch (Exception e)
		{
			LogHelper.LogException("GetServerNextResetTime", e);
			return -1;
		}
		
		return (int)(result.getTime()/1000);
	}

	public static int GetServerWeekCurrentResetTime()
	{
		int first_day_of_week_time = -1;
		
		try
		{
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 6); // ! clear would not reset the hour of day !
			cal.clear(Calendar.MINUTE);
			cal.clear(Calendar.SECOND);
			cal.clear(Calendar.MILLISECOND);

			cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
			
			String first_day_of_week = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cal.getTime());
//			LogHelper.Log("first_day_of_week = " + first_day_of_week); 

			// Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(first_day_of_week);
			
			first_day_of_week_time = SECONDS(first_day_of_week);
//			LogHelper.Log("" + first_day_of_week_time); 
		}
		catch (Exception e)
		{
			LogHelper.LogException("GetWeekServerCurrentResetTime", e);
			return -1;
		}
		
		return first_day_of_week_time;
	}

	public static int GetServerMonthCurrentResetTime()
	{
		int first_day_of_month_time = -1;
		
		try
		{
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 6); // ! clear would not reset the hour of day !
			cal.clear(Calendar.MINUTE);
			cal.clear(Calendar.SECOND);
			cal.clear(Calendar.MILLISECOND);

			cal.set(Calendar.DAY_OF_MONTH, 1);
			
			String first_day_of_month = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cal.getTime());
//			LogHelper.Log("first_day_of_month = " + first_day_of_month); 

			// Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(first_day_of_week);
			
			first_day_of_month_time = SECONDS(first_day_of_month);
//			LogHelper.Log("" + first_day_of_month_time); 
		}
		catch (Exception e)
		{
			LogHelper.LogException("GetMonthServerCurrentResetTime", e);
			return -1;
		}
		
		return first_day_of_month_time;
	}

	public static byte randSelectValue(int user_level)
	{
		if (true) return 4;
		
		int rand = Misc.RANDOM_RANGE(1, 100);		
		
		int DO_LETTER_1_RATE = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][DatabaseID.CONSTANT_DO_LETTER_1]);
		int DO_LETTER_2_RATE = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][DatabaseID.CONSTANT_DO_LETTER_2]);
		int DO_LETTER_3_RATE = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][DatabaseID.CONSTANT_DO_LETTER_3]);
		int DO_LETTER_4_RATE = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][DatabaseID.CONSTANT_DO_LETTER_4]);
		int DO_LETTER_5_RATE = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][DatabaseID.CONSTANT_DO_LETTER_5]);
		int DO_LETTER_6_RATE = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][DatabaseID.CONSTANT_DO_LETTER_6]);
		
		if (rand > DO_LETTER_1_RATE)
		{
			rand -= DO_LETTER_1_RATE;
		}
		else return 1;
		
		if (rand > DO_LETTER_2_RATE)
		{
			rand -= DO_LETTER_2_RATE;
		}
		else return 2;

		if (rand > DO_LETTER_3_RATE)
		{
			rand -= DO_LETTER_3_RATE;
		}
		else return 3;
		
		if (rand > DO_LETTER_4_RATE)
		{
			rand -= DO_LETTER_4_RATE;
		}
		else return 4;
		
		if (rand > DO_LETTER_5_RATE)
		{
			rand -= DO_LETTER_5_RATE;
		}
		else return 5;
		
		return 6;
	}

	public static byte[] getGameConstant(int sheet_index)
	{
		FBEncrypt enc = new FBEncrypt();
		
		int max_row = Server.s_globalDB[sheet_index].length;
		int max_column = Server.s_globalDB[sheet_index][0].length;
		
		// LogHelper.Log("getGameConstant.. max_row = " + max_row);
		// LogHelper.Log("getGameConstant.. max_col = " + max_column);
		
		enc.addInt(KeyID.KEY_MAX_ROW, max_row);
		enc.addInt(KeyID.KEY_MAX_COL, max_column);
		
		for (int row = 0; row < max_row; row++)
		{
			for (int column = 0; column < max_column; column++)
			{
				Object contentOfCell = Server.s_globalDB[sheet_index][row][column];
				
				if (contentOfCell instanceof String)
				{
					// enc.addStringANSI(row + "_" + column, Misc.PARSE_STRING(contentOfCell));
					enc.addStringUNICODE(row + "_" + column, Misc.PARSE_STRING(contentOfCell));
				}
				else
				{
					double number = Misc.PARSE_DOUBLE(contentOfCell);
					
					if (number == Math.ceil(number))
					{
						enc.addLong(row + "_" + column, Misc.PARSE_LONG(contentOfCell));
					}
					else
					{
						// LogHelper.Log("float = " + number);
						enc.addFloat(row + "_" + column, Misc.PARSE_FLOAT(contentOfCell));
					}
				}
			}
		}
	   
		return enc.toByteArray();
	}
	
	public static byte[] getCustomGameConstantSheetPot()
	{
		int bonus_event = 0;
		String start_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][11][DatabaseID.EVENT_GLOBAL_START_DATE]);
		String end_event_time = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][11][DatabaseID.EVENT_GLOBAL_END_DATE]);
		if(Misc.InEvent(start_event_time, end_event_time))
		{
			bonus_event = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][11][DatabaseID.EVENT_GLOBAL_BONUS_EXP_RATE]);
			LogHelper.LogHappy("getCustomGameConstantSheetPot.. Bonus percent event := " + bonus_event);
		}
		
		FBEncrypt enc = new FBEncrypt();
		
		int max_row = Server.s_globalDB[DatabaseID.SHEET_POT].length;
		int max_column = Server.s_globalDB[DatabaseID.SHEET_POT][0].length;

		enc.addInt(KeyID.KEY_MAX_ROW, max_row);
		enc.addInt(KeyID.KEY_MAX_COL, max_column);
		
		for (int row = 0; row < max_row; row++)
		{
			for (int column = 0; column < max_column; column++)
			{
				Object contentOfCell = Server.s_globalDB[DatabaseID.SHEET_POT][row][column];
				
				if (contentOfCell instanceof String)
				{
					enc.addStringUNICODE(row + "_" + column, Misc.PARSE_STRING(contentOfCell));
				}
				else
				{
					double number = Misc.PARSE_DOUBLE(contentOfCell);
					
					if (column == DatabaseID.POT_UPGRADE_RATIO)
					{
//						LogHelper.LogHappy("Upgrade ratio before := " + number);
						number += bonus_event;
//						LogHelper.LogHappy("Upgrade ratio after := " + number);
					}
					
					if (number == Math.ceil(number))
					{
						enc.addLong(row + "_" + column, (long)number);
					}
					else
					{
						enc.addFloat(row + "_" + column, (float)number);
					}
				}
			}
		}
	   
		return enc.toByteArray();
	}
	
	// public static byte PARSE_BYTE(Object obj)			{	return ((Byte)obj).byteValue();		}
	// public static short PARSE_SHORT(Object obj)			{	return ((Short)obj).shortValue();	}
	// public static int PARSE_INT(Object obj)				{	return ((Integer)obj).intValue();	}
	// public static long PARSE_LONG(Object obj)			{	return ((Long)obj).longValue();		}
	public static long PARSE_LONG(Object obj)			{	return ((Double)obj).longValue();	}
	public static String PARSE_STRING(Object obj)		{	return (String)obj;					}
	public static double PARSE_DOUBLE(Object obj)		{	return ((Double)obj).doubleValue();	}
	public static float PARSE_FLOAT(Object obj)			{	return ((Double)obj).floatValue();	}
	
	public static int RANDOM_RANGE(int min, int max)
	{
		try
		{
			if(min == max) return min;
			return random.nextInt(max - min + 1) + min;
		}
		catch (Exception e)
		{
			LogHelper.Log("RANDOM_RANGE error: min = " + min + ", max = " + max);
			// LogHelper.LogException("RANDOM_RANGE", e);
			throw e;
		}
	}
	
	public static double RANDOM_DOUBLE_RANGE(double min, double max)
	{
		if (min == max) return min;
		
		return (min + ((max - min) * random.nextDouble()));
	}
	
	// If range=1000 and rate=1 : 1/1000 rate or 0,1%
	// function random_rate($range=100, $rate=1)
	// {
		// return (intval(fmod(mt_rand(1,$range), $range/$rate))==0)?1:0;
	// }

	public static boolean RANDOM_RATE(int range, int rate)
	{
		double rand = RANDOM_RANGE(0, range);
		// double intpart = (double)range/(double)rate;
		// double mod = rand%intpart;
		// LogHelper.Log("RANDOM_RATE("+range+","+rate+"); \nrand=" + rand + "; intpart = " + intpart + "; mod = " + mod);
		if((int)(rand%((double)range/(double)rate)) == 0)
		{
			// LogHelper.Log("Success RANDOM_RATE("+range+","+rate+"); \nrand=" + rand + "; intpart = " + intpart + "; mod = " + mod);
			return true;
		}
		return false;
		// if(mod == 0)
			// return true;
		// return false;
	}
	
	/*public static boolean test_RANDOM_RATE(int range, int rate)
	{
		for(int i = 0; i < range; i++)
		{
			double rand = i;
			double intpart = (double)range/(double)rate;
			double mod = rand%intpart;				
			if((int)(rand%((double)range/(double)rate)) == 0)
			{
				LogHelper.Log("Success "+i+" RANDOM_RATE("+range+","+rate+"); \nrand=" + rand + "; intpart = " + intpart + "; mod = " + mod);
				
			}
			
			// if(mod == 0)
				// return true;
			// return false;
		}
		return false;
	}*/
	
	
	public static long MILLISECONDS_OF_1_1_2010()
	{
		return 1262325600000L;
	}
	
	public static long MILLISECONDS()
	{
		return (System.currentTimeMillis() - MILLISECONDS_OF_1_1_2010());
	}
	
	public static int SECONDS()
	{
		return (int)(MILLISECONDS()/1000);
	}
	
	public static int SECONDS(String date)
	{
		/*
		 * Example: 20/10/2013 22:15:30
		 */
		try
		{
			Date date_result = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(date);
			return (int)((date_result.getTime() - MILLISECONDS_OF_1_1_2010())/1000);
		}
		catch (Exception e)
		{
			LogHelper.LogException("Misc.SECONDS", e);
			return -1;
		}
	}

	public static int SECONDS1(String spec_time)
	{
		/*
		 * Example: 22:15:30
		 */
		try
		{
			Date d = new Date();
			String current_date = new SimpleDateFormat("dd/MM/yyyy").format(d);
			spec_time = current_date + " " + spec_time;
			
			Date date_result = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(spec_time);
			return (int)((date_result.getTime() - MILLISECONDS_OF_1_1_2010())/1000);
		}
		catch (Exception e)
		{
			LogHelper.LogException("Misc.SECONDS1", e);
			return -1;
		}
	}
	
	public static int MINUTES()
	{
		return (int)(MILLISECONDS()/60000);
	}
	
	public static int HOURS()
	{
		return (int)(MILLISECONDS()/3600000);
	}
	
	public static int DAYS()
	{
		return (int)(MILLISECONDS()/86400000);
	}
	
	public static int GET_DAYS(long sec)
	{
		return (int)(sec/86400);
	}
	
	public static long GenerateNewUserID()
	{
		long new_user_id = -1;
		
		try
		{
			new_user_id = DBConnector.GetMembaseServerForGeneralData().Increase("new_user_id", 1, 1L);
		}
		catch (Exception e)
		{
			LogHelper.LogException("GenerateNewUserID", e);
			
			new_user_id = -1;
		}
		
		LogHelper.Log("GenerateNewUserID.. new user id: " + new_user_id);
		
		return new_user_id;
	}
	
	public static long GetUserID(String user_device_id)
    {
        long uid = -1;

        byte[] uid_data = null;
        try
        {
            uid_data = DBConnector.GetMembaseServerForGeneralData().GetRaw(user_device_id + "_" + KeyID.KEY_USER_ID);
        }
        catch (Exception e)
        {
            uid_data = null;
        }

        if (uid_data == null || uid_data.length == 0)
        {
            uid = -1;
        }
        else
        {
            uid =	((uid_data[0]&0xFF)<<56) |
                    ((uid_data[1]&0xFF)<<48) |
                    ((uid_data[2]&0xFF)<<40) |
                    ((uid_data[3]&0xFF)<<32) |
                    ((uid_data[4]&0xFF)<<24) |
                    ((uid_data[5]&0xFF)<<16) |
                    ((uid_data[6]&0xFF)<<8)  |
                    (uid_data[7]&0xFF);
        }

        return uid;
    }
	
	public static String GetCommandString(int command_id)
	{
		switch (command_id)
		{	
			case CommandID.CMD_TEST: return "CMD_TEST";
			case CommandID.CMD_LOGIN: return "CMD_LOGIN";
			case CommandID.CMD_PLACE_POT: return "CMD_PLACE_POT";
			case CommandID.CMD_HIDE_POT: return "CMD_HIDE_POT";
			case CommandID.CMD_UPGRADE_POT: return "CMD_UPGRADE_POT";
			case CommandID.CMD_PLANT: return "CMD_PLANT";
			case CommandID.CMD_HARVEST: return "CMD_HARVEST";
			case CommandID.CMD_OPEN_NEW_FLOOR: return "CMD_OPEN_NEW_FLOOR";
			case CommandID.CMD_UPGRADE_STOCK: return "CMD_UPGRADE_STOCK";
			case CommandID.CMD_START_MACHINE: return "CMD_START_MACHINE";
			case CommandID.CMD_CREATE_PRODUCT: return "CMD_CREATE_PRODUCT";
			case CommandID.CMD_MOVE_POT: return "CMD_MOVE_POT";
			case CommandID.CMD_UPGRADE_MACHINE: return "CMD_UPGRADE_MACHINE";
			case CommandID.CMD_REPAIR_MACHINE: return "CMD_REPAIR_MACHINE";
			case CommandID.CMD_PRODUCT_COMPLETED: return "CMD_PRODUCT_COMPLETED";
			case CommandID.CMD_UPGRADE_PRODUCT_SLOT: return "CMD_UPGRADE_PRODUCT_SLOT";
			case CommandID.CMD_MOVE_MACHINE_PRODUCT: return "CMD_MOVE_MACHINE_PRODUCT";
			case CommandID.CMD_PRODUCT_SKIP_TIME: return "CMD_PRODUCT_SKIP_TIME";
			case CommandID.CMD_CREATE_ORDER: return "CMD_CREATE_ORDER";
			case CommandID.CMD_CATCH_BUG: return "CMD_CATCH_BUG";
			case CommandID.CMD_READY_MACHINE: return "CMD_READY_MACHINE";
			case CommandID.CMD_SKIP_MACHINE_UNLOCK_TIME: return "CMD_SKIP_MACHINE_UNLOCK_TIME";
			case CommandID.CMD_BUY_ITEM_UPGRADE_STOCK: return "CMD_BUY_ITEM_UPGRADE_STOCK";
			case CommandID.CMD_PLANT_INSTANT_GROW_UP: return "CMD_PLANT_INSTANT_GROW_UP";
			case CommandID.CMD_DELIVERY_ORDER: return "CMD_DELIVERY_ORDER";
			case CommandID.CMD_SKIP_ORDER: return "CMD_SKIP_ORDER";
			case CommandID.CMD_SKIP_ORDER_WAIT_TIME: return "CMD_SKIP_ORDER_WAIT_TIME";
			case CommandID.CMD_COMPLETE_ORDER: return "CMD_COMPLETE_ORDER";
			case CommandID.CMD_BUY_ITEM_OPEN_FLOOR: return "CMD_BUY_ITEM_OPEN_FLOOR";
			case CommandID.CMD_BUY_IBSHOP_PACKAGE: return "CMD_BUY_IBSHOP_PACKAGE";
			case CommandID.CMD_LOAD_FRIEND_GARDEN: return "CMD_LOAD_FRIEND_GARDEN";
			case CommandID.CMD_UNLOCK_PS_SLOT_FRIEND: return "CMD_UNLOCK_PS_SLOT_FRIEND";
			case CommandID.CMD_UNLOCK_PS_SLOT_DIAMOND: return "CMD_UNLOCK_PS_SLOT_DIAMOND";
			case CommandID.CMD_PLACE_ITEM_PSHOP: return "CMD_PLACE_ITEM_PSHOP";
			case CommandID.CMD_REPAIR_MACHINE_FRIEND: return "CMD_REPAIR_MACHINE_FRIEND";
			case CommandID.CMD_CANCEL_ITEM_PSHOP: return "CMD_CANCEL_ITEM_PSHOP";
			case CommandID.CMD_SKIP_ADS_PSHOP: return "CMD_SKIP_ADS_PSHOP";
			case CommandID.CMD_SET_ADS_PSHOP: return "CMD_SET_ADS_PSHOP";
			case CommandID.CMD_LOAD_FRIEND_PSHOP: return "CMD_LOAD_FRIEND_PSHOP";
			case CommandID.CMD_RECONNECT: return "CMD_RECONNECT";
			case CommandID.CMD_SIMPLE_LOGIN: return "CMD_SIMPLE_LOGIN";
			case CommandID.CMD_LOAD_FRIEND_LIST: return "CMD_LOAD_FRIEND_LIST";
			case CommandID.CMD_COME_BACK_HOME: return "CMD_COME_BACK_HOME";
			case CommandID.CMD_BUY_ITEM_PRIVATE_SHOP: return "CMD_BUY_ITEM_PRIVATE_SHOP";
			case CommandID.CMD_RECEIVE_REWARD: return "CMD_RECEIVE_REWARD";
			case CommandID.CMD_LOAD_FRIEND_FB: return "CMD_LOAD_FRIEND_FB";
			case CommandID.CMD_COLLECT_MONEY_PSHOP: return "CMD_COLLECT_MONEY_PSHOP";
			case CommandID.CMD_BUY_MATERIAL_UPGRADE_POT: return "CMD_BUY_MATERIAL_UPGRADE_POT";
			case CommandID.CMD_GET_GAME_CONSTANT: return "CMD_GET_GAME_CONSTANT";
			case CommandID.CMD_UPDATE_PRIVATE_SHOP: return "CMD_UPDATE_PRIVATE_SHOP";
			case CommandID.CMD_LEAVE_FRIEND_SHOP: return "CMD_LEAVE_FRIEND_SHOP";
			case CommandID.CMD_NOTIFY_SHOP_IS_MODIFIED: return "CMD_NOTIFY_SHOP_IS_MODIFIED";
			case CommandID.CMD_UDP_REQUEST_RELOAD_PSHOP: return "CMD_UDP_REQUEST_RELOAD_PSHOP";
			case CommandID.CMD_REQUEST_RELOAD_PSHOP: return "CMD_REQUEST_RELOAD_PSHOP";
			case CommandID.CMD_REFRESH_NEWS_BOARD: return "CMD_REFRESH_NEWS_BOARD";
			case CommandID.CMD_LOAD_IBSHOP: return "CMD_LOAD_IBSHOP";
			case CommandID.CMD_COMPLETE_ITEMS_TO_PRODUCT: return "CMD_COMPLETE_ITEMS_TO_PRODUCT";
			case CommandID.CMD_INSTANT_BUY_SEED: return "CMD_INSTANT_BUY_SEED";
			case CommandID.CMD_LOAD_GAME_ACCOUNT_VIA_FB: return "CMD_LOAD_GAME_ACCOUNT_VIA_FB";
			case CommandID.CMD_CONFIRM_LOGIN: return "CMD_CONFIRM_LOGIN";
			case CommandID.CMD_ADS_ADD: return "CMD_ADS_ADD";
			case CommandID.CMD_ADS_REMOVE: return "CMD_ADS_REMOVE";
			case CommandID.CMD_LOGIN_DIFFERENT_ACCOUNT: return "CMD_LOGIN_DIFFERENT_ACCOUNT";
			case CommandID.CMD_RESET_ALL_DATA: return "CMD_RESET_ALL_DATA";
			case CommandID.CMD_RESET_ACCOUNT: return "CMD_RESET_ACCOUNT";
			case CommandID.CMD_KEEP_ALIVE: return "CMD_KEEP_ALIVE";
			case CommandID.CMD_CHANGE_TO_STABLE_USER: return "CMD_CHANGE_TO_STABLE_USER";
			case CommandID.CMD_ADD_DIAMOND: return "CMD_ADD_DIAMOND";
			case CommandID.CMD_MAX_VALUE: return "CMD_MAX_VALUE";
			case CommandID.CMD_INSTANT_BUY_BUG_ZAPPER: return "CMD_INSTANT_BUY_BUG_ZAPPER";
			case CommandID.CMD_LEVEL_UP: return "CMD_LEVEL_UP";
			case CommandID.CMD_BUY_PEARL_UPGRADE_POT: return "CMD_BUY_PEARL_UPGRADE_POT";
			case CommandID.CMD_INSTANT_BUY_LUCKY_LEAF: return "CMD_INSTANT_BUY_LUCKY_LEAF";
			case CommandID.CMD_LOAD_NPC_PSHOP: return "CMD_LOAD_NPC_PSHOP";
			case CommandID.CMD_RECEIVE_MACHINE_DURABILITY: return "CMD_RECEIVE_MACHINE_DURABILITY";
			case CommandID.CMD_LOAD_OWN_PSHOP: return "CMD_LOAD_OWN_PSHOP";
			case CommandID.CMD_REFILL_CARD: return "CMD_REFILL_CARD";
			case CommandID.CMD_ORDER_LETTER_SELECT: return "CMD_ORDER_LETTER_SELECT";
			case CommandID.CMD_ORDER_LETTER_RESELECT: return "CMD_ORDER_LETTER_RESELECT";
			case CommandID.CMD_RECEIVE_DAILY_ORDER_FREE: return "CMD_RECEIVE_DAILY_ORDER_FREE";
			case CommandID.CMD_RECEIVE_DAILY_ORDER_PAID: return "CMD_RECEIVE_DAILY_ORDER_PAID";
			case CommandID.CMD_TUTORIAL_UPDATE_STEP: return "CMD_TUTORIAL_UPDATE_STEP";
			case CommandID.CMD_ACCEPT_GIFT: return "CMD_ACCEPT_GIFT";
			case CommandID.CMD_OPEN_GIFT: return "CMD_OPEN_GIFT";
			case CommandID.CMD_DISCARD_GIFT: return "CMD_DISCARD_GIFT";
			case CommandID.CMD_FEED_OWL: return "CMD_FEED_OWL";
			case CommandID.CMD_BUY_OWL_LOT: return "CMD_BUY_OWL_LOT";
			case CommandID.CMD_DIGEST_COMPLETED: return "CMD_DIGEST_COMPLETED";
			case CommandID.CMD_NOTIFY_SHARE_FB_FINISH: return "CMD_NOTIFY_SHARE_FB_FINISH";
			case CommandID.CMD_LOAD_FRIEND_ZING: return "CMD_LOAD_FRIEND_ZING";
			case CommandID.CMD_LOAD_ACCOUNT_VIA_ZING: return "CMD_LOAD_ACCOUNT_VIA_ZING";
			case CommandID.CMD_AUTHENTICATE_GIFT_CODE: return "CMD_AUTHENTICATE_GIFT_CODE";
			case CommandID.CMD_DIGEST_COMPLETE_INSTANT: return "CMD_DIGEST_COMPLETE_INSTANT";
			case CommandID.CMD_NPC_BUY_EXPIRED_ITEM: return "CMD_NPC_BUY_EXPIRED_ITEM";
			case CommandID.CMD_CATCH_BUG_FRIEND: return "CMD_CATCH_BUG_FRIEND";
			case CommandID.CMD_REGISTER_PHONE: return "CMD_REGISTER_PHONE";
			case CommandID.CMD_VERIFY_PHONE: return "CMD_VERIFY_PHONE";
			case CommandID.CMD_USE_FERTILIZER: return "CMD_USE_FERTILIZER";
			case CommandID.CMD_NOTIFY_LIKE_FB_FINISH: return "CMD_NOTIFY_LIKE_FB_FINISH";
			case CommandID.CMD_CRASH_LOG: return "CMD_CRASH_LOG";
			case CommandID.CMD_RECEIVE_DAILY_GIFT: return "CMD_RECEIVE_DAILY_GIFT";
			
			case CommandID.CMD_DISCARD_DAILY_GIFT: return "CMD_DISCARD_DAILY_GIFT";
			case CommandID.CMD_INTERACT_EMO: return "CMD_INTERACT_EMO";
			case CommandID.CMD_BUY_ITEM_EMI: return "CMD_BUY_ITEM_EMI";
			case CommandID.CMD_CREATE_PRODUCT_OPTIMIZE_DATA_OUT: return "CMD_CREATE_PRODUCT_OPTIMIZE_DATA_OUT";
			case CommandID.CMD_PRODUCT_COMPLETED_OPTIMIZE_DATA_OUT: return "CMD_PRODUCT_COMPLETED_OPTIMIZE_DATA_OUT";
			case CommandID.CMD_MOVE_MACHINE_PRODUCT_OPTIMIZE_DATA_OUT: return "CMD_MOVE_MACHINE_PRODUCT_OPTIMIZE_DATA_OUT";
			case CommandID.CMD_PRODUCT_SKIP_TIME_OPTIMIZE_DATA_OUT: return "CMD_PRODUCT_SKIP_TIME_OPTIMIZE_DATA_OUT";
			case CommandID.CMD_BUY_ITEM_UPGRADE_STOCK_OPTIMIZE_DATA_OUT: return "CMD_BUY_ITEM_UPGRADE_STOCK_OPTIMIZE_DATA_OUT";
			case CommandID.CMD_ACCEPT_MERCHANT_REQUEST: return "CMD_ACCEPT_MERCHANT_REQUEST";
			case CommandID.CMD_DISCARD_MERCHANT_REQUEST: return "CMD_DISCARD_MERCHANT_REQUEST";
			case CommandID.CMD_REQUEST_MERCHANT: return "CMD_REQUEST_MERCHANT";
			case CommandID.CMD_BUY_ITEMS_FOR_MERCHANT: return "CMD_BUY_ITEMS_FOR_MERCHANT";
			case CommandID.CMD_SET_STATE_MERCHANT: return "CMD_SET_STATE_MERCHANT";
			case CommandID.CMD_REQUEST_FEED_INFO: return "CMD_REQUEST_FEED_INFO";
			case CommandID.CMD_PROVIDE_FB_BIRTHDAY: return "CMD_PROVIDE_FB_BIRTHDAY";
			case CommandID.CMD_UPDATE_RANKING_INFO: return "CMD_UPDATE_RANKING_INFO";
			case CommandID.CMD_GET_ACTIVE_RANKING_INFO: return "CMD_GET_ACTIVE_RANKING_INFO";
			case CommandID.CMD_GET_PREVIOUS_RANKING_INFO: return "CMD_GET_PREVIOUS_RANKING_INFO";
			case CommandID.CMD_GET_RANKING_ACCUMULATION: return "CMD_GET_RANKING_ACCUMULATION";
			case CommandID.CMD_GET_ACTIVE_RANKING_RESULT: return "CMD_GET_ACTIVE_RANKING_RESULT";
			case CommandID.CMD_GET_BASIC_RANKING_GIFT: return "CMD_GET_BASIC_RANKING_GIFT";
			case CommandID.CMD_GET_ALL_RANKING_INFO: return "CMD_GET_ALL_RANKING_INFO";
			case CommandID.CMD_LOAD_SPECIAL_OFFER: return "CMD_LOAD_SPECIAL_OFFER";
			case CommandID.CMD_BUY_SALE_OFF_SPECIAL_OFFER: return "CMD_BUY_SALE_OFF_SPECIAL_OFFER";
			case CommandID.CMD_UNLOCK_AIRSHIP: return "CMD_UNLOCK_AIRSHIP";
			case CommandID.CMD_SKIP_UNLOCK_TIME_AIRSHIP: return "CMD_SKIP_UNLOCK_TIME_AIRSHIP";
			case CommandID.CMD_SKIP_DEPART_TIME_AIRSHIP: return "CMD_SKIP_DEPART_TIME_AIRSHIP";
			case CommandID.CMD_DISPOSE_AIRSHIP: return "CMD_DISPOSE_AIRSHIP";
			case CommandID.CMD_COMPLETE_CARGO: return "CMD_COMPLETE_CARGO";
			case CommandID.CMD_QUICK_COMPLETE_CARGO: return "CMD_QUICK_COMPLETE_CARGO";
			case CommandID.CMD_LOAD_AIRSHIP: return "CMD_LOAD_AIRSHIP";
			case CommandID.CMD_DELETE_AIRSHIP: return "CMD_DELETE_AIRSHIP";
			case CommandID.CMD_COMPLETE_AIRSHIP: return "CMD_COMPLETE_AIRSHIP";
			case CommandID.CMD_QUICK_COMPLETE_AIRSHIP: return "CMD_QUICK_COMPLETE_AIRSHIP";
			case CommandID.CMD_LOAD_TOM_KID: return "CMD_LOAD_TOM_KID";
			case CommandID.CMD_REQUEST_TOM_KID_ITEM: return "CMD_REQUEST_TOM_KID_ITEM";
			case CommandID.CMD_ACCEPT_TOM_KID_ITEM: return "CMD_ACCEPT_TOM_KID_ITEM";
			case CommandID.CMD_DENY_TOM_KID_ITEM: return "CMD_DENY_TOM_KID_ITEM";
			case CommandID.CMD_HIRE_TOM_KID: return "CMD_HIRE_TOM_KID";
			case CommandID.CMD_DELETE_TOM_KID: return "CMD_DELETE_TOM_KID";
			case CommandID.CMD_PROVIDE_GCM_REG_ID: return "CMD_PROVIDE_GCM_REG_ID";
			case CommandID.CMD_START_TOM: return "CMD_START_TOM";
			case CommandID.CMD_CANCEL_AIRSHIP: return "CMD_CANCEL_AIRSHIP";
			case CommandID.CMD_GET_ADS: return "CMD_GET_ADS";
			case CommandID.CMD_RECEIVE_ADS: return "CMD_RECEIVE_ADS";
			case CommandID.CMD_RECEIVE_ACM_GIFT: return "CMD_RECEIVE_ACM_GIFT";
			case CommandID.CMD_DELETE_ACM: return "CMD_DELETE_ACM";
			case CommandID.CMD_LOAD_FORTUNE: return "CMD_LOAD_FORTUNE";
			case CommandID.CMD_LOAD_ACM: return "CMD_LOAD_ACM";
			case CommandID.CMD_BUY_FORTUNE: return "CMD_BUY_FORTUNE";
			case CommandID.CMD_USE_FORTUNE: return "CMD_USE_FORTUNE";
			case CommandID.CMD_ASK_FOR_HELP_AIRSHIP: return "CMD_ASK_FOR_HELP_AIRSHIP";
			case CommandID.CMD_HELP_FRIEND_AIRSHIP: return "CMD_HELP_FRIEND_AIRSHIP";
			case CommandID.CMD_QUICK_HELP_FRIEND_AIRSHIP: return "CMD_QUICK_HELP_FRIEND_AIRSHIP";
			case CommandID.CMD_UNLINK_ZALO: return "CMD_UNLINK_ZALO";
			case CommandID.CMD_GET_ZALO_TOKEN: return "CMD_GET_ZALO_TOKEN";
			case CommandID.CMD_QUICK_REFRESH_NEWSBOARD: return "CMD_QUICK_REFRESH_NEWSBOARD";
			case CommandID.CMD_RECEIVED_GIFT_INVITE_FRIEND: return "CMD_RECEIVED_GIFT_INVITE_FRIEND";
			case CommandID.CMD_FILTER_ZING_FRIEND: return "CMD_FILTER_ZING_FRIEND";
			case CommandID.CMD_RESET_INVITE_FRIEND: return "CMD_RESET_INVITE_FRIEND";
			case CommandID.CMD_NOTIFY_INVITE_FRIEND: return "CMD_NOTIFY_INVITE_FRIEND";
			case CommandID.CMD_UPDATE_PHONE_NUMBER: return "CMD_UPDATE_PHONE_NUMBER";
			case CommandID.CMD_RECEIVED_GIFT_CROSS_INSTALL: return "CMD_RECEIVED_GIFT_CROSS_INSTALL";
			case CommandID.CMD_LOAD_FRIEND_ZALO: return "CMD_LOAD_FRIEND_ZALO";
			case CommandID.CMD_LOAD_ACCOUNT_VIA_ZALO: return "CMD_LOAD_ACCOUNT_VIA_ZALO";
			case CommandID.CMD_PRELOAD_IMG_SPECIAL_OFFER: return "CMD_PRELOAD_IMG_SPECIAL_OFFER";
			case CommandID.CMD_LOAD_USER_INFO: return "CMD_LOAD_USER_INFO";
			case CommandID.CMD_LOAD_MAIL_BOX: return "CMD_LOAD_MAIL_BOX";
			case CommandID.CMD_SET_MAIL_READ: return "CMD_SET_MAIL_READ";
			case CommandID.CMD_DELETE_MAIL: return "CMD_DELETE_MAIL";
			case CommandID.CMD_SEND_MAIL: return "CMD_SEND_MAIL";
			case CommandID.CMD_ACCEPT_MAIL_GIFT: return "CMD_ACCEPT_MAIL_GIFT";
			case CommandID.CMD_VALIDATE_APPLE_IAP: return "CMD_VALIDATE_APPLE_IAP";
			case CommandID.CMD_GET_ANDROID_DEVELOPER_PAYLOAD: return "CMD_GET_ANDROID_DEVELOPER_PAYLOAD";
			case CommandID.CMD_VALIDATE_ANDROID_RECEIPT: return "CMD_VALIDATE_ANDROID_RECEIPT";
			case CommandID.CMD_SET_STATE_NEW_TUTORIAL: return "CMD_SET_STATE_NEW_TUTORIAL";
			case CommandID.CMD_PLACE_ITEM_XMAS_TREE: return "CMD_PLACE_ITEM_XMAS_TREE";
			case CommandID.CMD_FORCE_CHANGE_SERVER: return "CMD_FORCE_CHANGE_SERVER";
			case CommandID.CMD_VALIDATE_WP_RECEIPT: return "CMD_VALIDATE_WP_RECEIPT";
			case CommandID.CMD_BUY_OFFER_BUG: return "CMD_BUY_OFFER_BUG";
			case CommandID.CMD_BUY_OFFER_GEM: return "CMD_BUY_OFFER_GEM";
			case CommandID.CMD_BUY_OFFER_FLOOR: return "CMD_BUY_OFFER_FLOOR";
			case CommandID.CMD_BUY_OFFER_MACHINE: return "CMD_BUY_OFFER_MACHINE";
			case CommandID.CMD_REQUEST_OFFER_FLOOR: return "CMD_REQUEST_OFFER_FLOOR";
			case CommandID.CMD_REQUEST_OFFER_MACHINE: return "CMD_REQUEST_OFFER_MACHINE";
			case CommandID.CMD_REQUEST_OFFER_GOLD: return "CMD_REQUEST_OFFER_GOLD";
			case CommandID.CMD_BUY_OFFER_GOLD: return "CMD_BUY_OFFER_GOLD";
			case CommandID.CMD_BUY_OFFER_LUCKY_LEAF: return "CMD_BUY_OFFER_LUCKY_LEAF";
			case CommandID.CMD_BUY_OFFER_LUCKY_LEAF_PURPLE: return "CMD_BUY_OFFER_LUCKY_LEAF_PURPLE";
			case CommandID.CMD_GIVE_FRIEND_EVENT_GIFT: return "CMD_GIVE_FRIEND_EVENT_GIFT";
			case CommandID.CMD_RECEIVE_EVENT_GIFT_FROM_FRIEND: return "CMD_RECEIVE_EVENT_GIFT_FROM_FRIEND";
			case CommandID.CMD_OPEN_ITEM_EVENT_GIFT: return "CMD_OPEN_ITEM_EVENT_GIFT";
			case CommandID.CMD_GET_EVENT_GIFT_LIST: return "CMD_GET_EVENT_GIFT_LIST";

			default: return "COMMAND_UNKNOWN" + "_" + command_id;
		}
	}

	public static String getActionName(int command_id)
	{
		return getActionName(command_id, "");
	}
	
	public static String getActionName(int command_id, String action_code)
	{
		switch (command_id)
		{	
			case CommandID.CMD_LOGIN: return "LogIn";
			case CommandID.CMD_PLACE_POT: return "PlacePot";
			case CommandID.CMD_HIDE_POT: return "HidePot";
			case CommandID.CMD_UPGRADE_POT: return "UpgradePot";
			case CommandID.CMD_PLANT: return "Plant";
			case CommandID.CMD_HARVEST: return "Harvest";
			case CommandID.CMD_OPEN_NEW_FLOOR: return "OpenNewFloor";
			case CommandID.CMD_UPGRADE_STOCK: return "UpgradeStock";
			case CommandID.CMD_START_MACHINE: return "StartMachine";
			case CommandID.CMD_CREATE_PRODUCT: return "CreateProduct";
			case CommandID.CMD_MOVE_POT: return "MovePot";
			case CommandID.CMD_UPGRADE_MACHINE: return "UpgradeMachine";
			case CommandID.CMD_REPAIR_MACHINE: return "RepairMachine";
			case CommandID.CMD_PRODUCT_COMPLETED: return "ProductCompleted";
			case CommandID.CMD_MOVE_MACHINE_PRODUCT: return "MoveMachineProduct";
			case CommandID.CMD_CREATE_ORDER: return "CreateOrder";
			case CommandID.CMD_CATCH_BUG: return "CatchBug";
			case CommandID.CMD_READY_MACHINE: return "ReadyMachine";
			case CommandID.CMD_CANCEL_ITEM_PSHOP: return "CancelItemPShop";
			case CommandID.CMD_ORDER_LETTER_RESELECT: return "OrderLetterReselect";
			
			case CommandID.CMD_PLANT_INSTANT_GROW_UP: return appendActionCode("PlantInstantGrowUp", action_code);
			case CommandID.CMD_COMPLETE_ITEMS_TO_PRODUCT: return appendActionCode("CompleteItemsToProd", action_code);
			case CommandID.CMD_SKIP_MACHINE_UNLOCK_TIME: return appendActionCode("SkipMachinUnlockTime", action_code);
			case CommandID.CMD_PRODUCT_SKIP_TIME: return appendActionCode("ProductSkipTime", action_code);
			case CommandID.CMD_UPGRADE_PRODUCT_SLOT: return appendActionCode("UpgradeProductSlot", action_code);
			case CommandID.CMD_COMPLETE_ORDER: return appendActionCode("CompleteOrder", action_code);
			case CommandID.CMD_SKIP_ORDER_WAIT_TIME: return appendActionCode("SkipOrderWaitTime", action_code);
			case CommandID.CMD_RECEIVE_DAILY_ORDER_PAID: return appendActionCode("ReceiveDOPaid", action_code);
			case CommandID.CMD_UNLOCK_PS_SLOT_DIAMOND: return appendActionCode("UnlockPSSlotDiamond", action_code);
			case CommandID.CMD_ORDER_LETTER_SELECT: return appendActionCode("OrderLetterSelect", action_code);
			case CommandID.CMD_RECEIVE_DAILY_ORDER_FREE: return appendActionCode("ReceiveDOFree", action_code);
			case CommandID.CMD_HIRE_TOM_KID: return appendActionCode("HireTomKid", action_code);

			case CommandID.CMD_BUY_ITEM_UPGRADE_STOCK: return "BuyItemUpgradeStock";
			case CommandID.CMD_DELIVERY_ORDER: return "DeliveryOrder";
			case CommandID.CMD_SKIP_ORDER: return "SkipOrder";
			case CommandID.CMD_BUY_ITEM_OPEN_FLOOR: return "BuyItemOpenFloor";
			case CommandID.CMD_BUY_IBSHOP_PACKAGE: return "BuyIBShopPackage";
			case CommandID.CMD_LOAD_FRIEND_GARDEN: return "LoadFriendGarden";
			case CommandID.CMD_UNLOCK_PS_SLOT_FRIEND: return "UnlockPSSlotFriend";
			case CommandID.CMD_PLACE_ITEM_PSHOP: return "PlaceItemPShop";
			case CommandID.CMD_REPAIR_MACHINE_FRIEND: return "RepairMachineFriend";
			case CommandID.CMD_SKIP_ADS_PSHOP: return "SkipADSPShop";
			case CommandID.CMD_SET_ADS_PSHOP: return "SetADSPShop";
			case CommandID.CMD_LOAD_FRIEND_PSHOP: return "LoadFriendPShop";
			case CommandID.CMD_RECONNECT: return "Reconnect";
			case CommandID.CMD_SIMPLE_LOGIN: return "SimpleLogin";
			case CommandID.CMD_LOAD_FRIEND_LIST: return "LoadFriendList";
			case CommandID.CMD_COME_BACK_HOME: return "ComeBackHome";
			case CommandID.CMD_BUY_ITEM_PRIVATE_SHOP: return "BuyItemPrivateShop";
			case CommandID.CMD_RECEIVE_REWARD: return "ReceiveReward";
			case CommandID.CMD_LOAD_FRIEND_FB: return "LoadFriendFB";
			case CommandID.CMD_COLLECT_MONEY_PSHOP: return "CollectMoneyPShop";
			case CommandID.CMD_BUY_MATERIAL_UPGRADE_POT: return "BuyMateUpgradePot";
			case CommandID.CMD_GET_GAME_CONSTANT: return "GetGameConstant";
			case CommandID.CMD_UPDATE_PRIVATE_SHOP: return "UpdatePrivateShop";
			case CommandID.CMD_LEAVE_FRIEND_SHOP: return "LeaveFriendShop";
			case CommandID.CMD_NOTIFY_SHOP_IS_MODIFIED: return "NotifyShopIsModified";
			case CommandID.CMD_UDP_REQUEST_RELOAD_PSHOP: return "RequestReloadPShop";
			case CommandID.CMD_REQUEST_RELOAD_PSHOP: return "RequestReloadPShop";
			case CommandID.CMD_REFRESH_NEWS_BOARD: return "RefreshNewsboard";
			case CommandID.CMD_LOAD_IBSHOP: return "LoadIBShop";
			case CommandID.CMD_INSTANT_BUY_SEED: return "InstantBuySeed";
			case CommandID.CMD_LOAD_GAME_ACCOUNT_VIA_FB: return "LoadGameAccountViaFB";
			case CommandID.CMD_CONFIRM_LOGIN: return "ConfirmLogin";
			case CommandID.CMD_ADS_ADD: return "ADSAdd";
			case CommandID.CMD_ADS_REMOVE: return "ADSRemove";
			case CommandID.CMD_LOGIN_DIFFERENT_ACCOUNT: return "LoginDifferentAcc";
			case CommandID.CMD_RESET_ALL_DATA: return "ResetAllData";
			case CommandID.CMD_RESET_ACCOUNT: return "ResetAccount";
			case CommandID.CMD_KEEP_ALIVE: return "KeepAlive";
			case CommandID.CMD_CHANGE_TO_STABLE_USER: return "ChangeToStableUser";
			case CommandID.CMD_ADD_DIAMOND: return "AddDiamond";
			case CommandID.CMD_MAX_VALUE: return "MaxValue";
			case CommandID.CMD_INSTANT_BUY_BUG_ZAPPER: return "InstantBuyBugZapper";
			case CommandID.CMD_LEVEL_UP: return "LevelUp";
			case CommandID.CMD_BUY_PEARL_UPGRADE_POT: return "BuyPearlUpgradePot";
			case CommandID.CMD_INSTANT_BUY_LUCKY_LEAF: return "InstantBuyLuckyLeaf";
			case CommandID.CMD_LOAD_NPC_PSHOP: return "LoadNPCPShop";
			case CommandID.CMD_RECEIVE_MACHINE_DURABILITY: return "ReceiveMachineDur";
			case CommandID.CMD_LOAD_OWN_PSHOP: return "LoadOwnPShop";
			case CommandID.CMD_REFILL_CARD: return "RefillCard";
			
			case CommandID.CMD_TUTORIAL_UPDATE_STEP: return "TutorialUpdateStep";
			case CommandID.CMD_ACCEPT_GIFT: return "AcceptGift";
			case CommandID.CMD_OPEN_GIFT: return "OpenGift";
			case CommandID.CMD_DISCARD_GIFT: return "DiscardGift";
			case CommandID.CMD_FEED_OWL: return "FeedOwl";
			case CommandID.CMD_BUY_OWL_LOT: return "BuyOwnSlot";
			case CommandID.CMD_DIGEST_COMPLETED: return "DigestCompleted";
			case CommandID.CMD_DIGEST_COMPLETE_INSTANT: return "DigestCompletedInstant";
			case CommandID.CMD_NOTIFY_SHARE_FB_FINISH: return "NotifyShareFBFinish";
			case CommandID.CMD_NPC_BUY_EXPIRED_ITEM: return "NpcBuyExpiredItems";
			case CommandID.CMD_CATCH_BUG_FRIEND: return "CatchBugFriend";
			case CommandID.CMD_AUTHENTICATE_GIFT_CODE: return "AuthenticateGiftCode";
			case CommandID.CMD_REGISTER_PHONE: return "RegisterPhone";
			case CommandID.CMD_VERIFY_PHONE: return "VerifyPhone";
			case CommandID.CMD_USE_FERTILIZER: return "UseFertilizer";
			case CommandID.CMD_CRASH_LOG: return "Crash";
			case CommandID.CMD_RECEIVE_DAILY_GIFT: return "ReceiveDailyGift";

			case CommandID.CMD_DISCARD_DAILY_GIFT: return "DiscardDailyGift";
			case CommandID.CMD_INTERACT_EMO: return "InteractEMO";
			case CommandID.CMD_BUY_ITEM_EMI: return "BuyItemEMI";
			case CommandID.CMD_CREATE_PRODUCT_OPTIMIZE_DATA_OUT: return "CreateProductOpt";
			case CommandID.CMD_PRODUCT_COMPLETED_OPTIMIZE_DATA_OUT: return "ProductCompletedOpt";
			case CommandID.CMD_MOVE_MACHINE_PRODUCT_OPTIMIZE_DATA_OUT: return "MoveMachineProductOpt";
			case CommandID.CMD_PRODUCT_SKIP_TIME_OPTIMIZE_DATA_OUT: return "ProductSkipTimeOpt";
			case CommandID.CMD_BUY_ITEM_UPGRADE_STOCK_OPTIMIZE_DATA_OUT: return "BuyItemUpgradeStockOpz";
			case CommandID.CMD_ACCEPT_MERCHANT_REQUEST: return "AcceptMerchantRequest";
			case CommandID.CMD_DISCARD_MERCHANT_REQUEST: return "DiscardMerchantRequest";
			case CommandID.CMD_REQUEST_MERCHANT: return "RequestMerchant";
			case CommandID.CMD_BUY_ITEMS_FOR_MERCHANT: return "BuyItemsForMerchant";
			case CommandID.CMD_SET_STATE_MERCHANT: return "SetStateMerchant";
			case CommandID.CMD_REFILL_ATM: return "RefillATM";
			case CommandID.CMD_REQUEST_FEED_INFO: return "RequestFeedInfo";
			case CommandID.CMD_PROVIDE_FB_BIRTHDAY: return "ProvideFBBirthday";
			case CommandID.CMD_UPDATE_RANKING_INFO: return "UpdateRankingInfo";
			case CommandID.CMD_GET_ACTIVE_RANKING_INFO: return "GetActiveRankingInfo";
			case CommandID.CMD_GET_PREVIOUS_RANKING_INFO: return "GetPreviousRankingInfo";
			case CommandID.CMD_GET_RANKING_ACCUMULATION: return "GetRankingAccumulation";
			case CommandID.CMD_GET_ACTIVE_RANKING_RESULT: return "GetActiveRankingResult";
			case CommandID.CMD_GET_BASIC_RANKING_GIFT: return "GetBasicRankingGift";
			case CommandID.CMD_GET_ALL_RANKING_INFO: return "GetAllRakingInfo";
			case CommandID.CMD_LOAD_SPECIAL_OFFER: return "LoadSpecialOffer";
			case CommandID.CMD_BUY_SALE_OFF_SPECIAL_OFFER: return "BuySaleOffSpecialOffer";
			case CommandID.CMD_UNLOCK_AIRSHIP: return "UnlockAirship";
			case CommandID.CMD_SKIP_UNLOCK_TIME_AIRSHIP: return "SkipUnlockTimeAirship";
			case CommandID.CMD_SKIP_DEPART_TIME_AIRSHIP: return "SkipDepartTimeAirship";
			case CommandID.CMD_DISPOSE_AIRSHIP: return "DisposeAirship";
			case CommandID.CMD_COMPLETE_AIRSHIP: return "CompleteAirship";
			case CommandID.CMD_QUICK_COMPLETE_AIRSHIP: return "QuickCompleteAirship";
			case CommandID.CMD_COMPLETE_CARGO: return "CompleteCargo";
			case CommandID.CMD_QUICK_COMPLETE_CARGO: return "QuickCompleteCargo";
			case CommandID.CMD_LOAD_TOM_KID: return "LoadTomKid";
			case CommandID.CMD_REQUEST_TOM_KID_ITEM: return "RequestTomKidItem";
			case CommandID.CMD_ACCEPT_TOM_KID_ITEM: return "AcceptTomKidItem";
			case CommandID.CMD_DENY_TOM_KID_ITEM: return "DenyTomKidItem";
			case CommandID.CMD_DELETE_TOM_KID: return "DeleteTomKid";
			case CommandID.CMD_START_TOM: return "StartTom";
			case CommandID.CMD_CANCEL_AIRSHIP: return "CancelAirship";
			case CommandID.CMD_GET_ADS: return "GetAds";
			case CommandID.CMD_RECEIVE_ADS: return "ReceivedAds";
			case CommandID.CMD_RECEIVE_ACM_GIFT: return "ReceiveAchivementGift";
			case CommandID.CMD_DELETE_ACM: return "DeleteAchievement";
			case CommandID.CMD_LOAD_FORTUNE: return "LoadFortune";
			case CommandID.CMD_LOAD_ACM: return "LoadAchievement";
			case CommandID.CMD_BUY_FORTUNE: return "BuyFortune";
			case CommandID.CMD_USE_FORTUNE: return "UseFortune";
			case CommandID.CMD_ASK_FOR_HELP_AIRSHIP: return "AskFriendForHelpAirship";
			case CommandID.CMD_HELP_FRIEND_AIRSHIP: return "HelpFriendAirship";
			case CommandID.CMD_QUICK_HELP_FRIEND_AIRSHIP: return "QuickHelpFriendAirship";
			case CommandID.CMD_ACCEPT_MAIL_GIFT: return "AcceptMailGift";
			case CommandID.CMD_QUICK_REFRESH_NEWSBOARD: return "QuickRefreshNewsboard";
			case CommandID.CMD_VALIDATE_APPLE_IAP: return "ValidateAppleIAP";
			case CommandID.CMD_GET_ANDROID_DEVELOPER_PAYLOAD: return "GetAndroidDeveloperPayload";
			case CommandID.CMD_VALIDATE_ANDROID_RECEIPT: return "ValidateAndroidReceipt";
			case CommandID.CMD_SET_STATE_NEW_TUTORIAL: return "SetStateNewTutorial";
			case CommandID.CMD_PLACE_ITEM_XMAS_TREE: return "PlaceItemXmasTree";
			case CommandID.CMD_FORCE_CHANGE_SERVER: return "ForceChangeServer";
			case CommandID.CMD_VALIDATE_WP_RECEIPT: return "ValidateWinPhoneReceipt";
			case CommandID.CMD_BUY_OFFER_BUG: return "BuyOfferBug";
			case CommandID.CMD_BUY_OFFER_GEM: return "BuyOfferGem";
			case CommandID.CMD_BUY_OFFER_FLOOR: return "BuyOfferFloor";
			case CommandID.CMD_BUY_OFFER_MACHINE: return "BuyOfferMachine";
			case CommandID.CMD_REQUEST_OFFER_FLOOR: return "RequestOfferFloor";
			case CommandID.CMD_REQUEST_OFFER_MACHINE: return "RequestOfferMachine";
			case CommandID.CMD_REQUEST_OFFER_GOLD: return "RequestOfferGold";
			case CommandID.CMD_BUY_OFFER_GOLD: return "BuyOfferGold";
			case CommandID.CMD_BUY_OFFER_LUCKY_LEAF: return "BuyOfferLuckyLeaf";
			case CommandID.CMD_BUY_OFFER_LUCKY_LEAF_PURPLE: return "BuyOfferLuckyLeafPurple";
			case CommandID.CMD_GIVE_FRIEND_EVENT_GIFT: return "GiveFriendEventGift";
			case CommandID.CMD_RECEIVE_EVENT_GIFT_FROM_FRIEND: return "ReceiveEventGiftFromFriend";
			case CommandID.CMD_OPEN_ITEM_EVENT_GIFT: return "OpenItemEventGift";
			case CommandID.CMD_GET_EVENT_GIFT_LIST: return "GetEventGiftList";
			case CommandID.CMD_DELETE_MAIL: return "DeleteMailGift";
			case CommandID.CMD_ADD_CLOSE_FRIEND: return "AddCloseFriend";
			
			default: return "COMMAND_UNKNOWN" + "_" + command_id;
		}
	}
	
	private static String appendActionCode(String command, String action_code)
	{
		if (action_code.equals(""))
		{
			return command;
		}
		else
		{
			return command + "_" + action_code;
		}
	}
	
	public static String GetItemName(int type, int id)
	{
		if (type < 0 || id < 0) return "unknown" + "_" + type + "_" + id;
		
		int sheet_idx = -1;
		
		switch (type)
		{
			case DatabaseID.IT_POT:
				sheet_idx = DatabaseID.SHEET_POT;
				break;
			case DatabaseID.IT_PLANT:
				sheet_idx = DatabaseID.SHEET_SEED;
				break;
			case DatabaseID.IT_PRODUCT:
				sheet_idx = DatabaseID.SHEET_PRODUCT;
				break;
			case DatabaseID.IT_MATERIAL:
				sheet_idx = DatabaseID.SHEET_MATERIAL;
				break;
			case DatabaseID.IT_BUG:
				sheet_idx = DatabaseID.SHEET_PEST;
				break;
			case DatabaseID.IT_DECOR:
				sheet_idx = DatabaseID.SHEET_DECOR;
				break;
			default:
				return "unknown" + "_" + type + "_" + id;
		}
		
		if (id < Server.s_globalDB[sheet_idx].length)
		{
			return Misc.PARSE_STRING(Server.s_globalDB[sheet_idx][id][1]) + "_" + type + "_" + id;
		}
		
		return "unknown" + "_" + type + "_" + id;
	}
	
	public static String GetShortItemName(int type, int id) {
		if (type < 0 || id < 0) return "unknown" + "_" + type + "_" + id;
		
		int sheet_idx = -1;
		
		switch (type)
		{
			case DatabaseID.IT_POT:
				sheet_idx = DatabaseID.SHEET_POT;
				break;
			case DatabaseID.IT_PLANT:
				sheet_idx = DatabaseID.SHEET_SEED;
				break;
			case DatabaseID.IT_PRODUCT:
				sheet_idx = DatabaseID.SHEET_PRODUCT;
				break;
			case DatabaseID.IT_MATERIAL:
				sheet_idx = DatabaseID.SHEET_MATERIAL;
				break;
			case DatabaseID.IT_BUG:
				sheet_idx = DatabaseID.SHEET_PEST;
				break;
			case DatabaseID.IT_DECOR:
				sheet_idx = DatabaseID.SHEET_DECOR;
				break;
			default:
				return "unknown" + "_" + type + "_" + id;
		}
		
		if (id < Server.s_globalDB[sheet_idx].length) {
			return Misc.PARSE_STRING(Server.s_globalDB[sheet_idx][id][1]);
		}
		
		return "unknown" + "_" + type + "_" + id;
	}
	
	public static String GetDeviceID(long user_id)
	{
		if (user_id < 0) return "";
		
		try
        {
            byte[] userbin = null;

			userbin = DBConnector.GetMembaseServer(user_id).GetRaw(user_id + "_" + KeyID.KEY_USER_INFOS);            

            if (userbin == null || userbin.length == 0)
            {
				LogHelper.Log("Misc.GetDeviceID.. err! null user bin.");
                return "";
            }
            else
            {
                UserInfo userInfo = new UserInfo(userbin);
				return userInfo.getDeviceID();
            }
        }
        catch (Exception e)
        {
            LogHelper.LogException("Misc.GetDeviceID", e);
			
			return "";
        }
	}
	
	public static String GetFacebookID(long user_id) {
		byte[] userbin = null;
        try
        {
            userbin = DBConnector.GetMembaseServer(user_id).GetRaw(user_id + "_" + KeyID.KEY_USER_INFOS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            userbin = null;
        }

        if (userbin == null || userbin.length == 0)
        {
            return "null";
        }
        else
        {
            UserInfo userInfo = new UserInfo(userbin);
			if (userInfo.isLoadSuccess()) {
				return userInfo.getFaceBookID();
			} else {
				return "null";
			}
        }
	}
	
	public static double MaxRate(double v0, double v1, double v2)
	{
		if (v0 > v1)
		{
			if (v0 > v2)
			{
				return v0;
			}
			else
			{
				return v2;
			}
		}
		else
		{
			if (v1 > v2)
			{
				return v1;
			}
			else
			{
				return v2;
			}
		}
	}
	
	public static double MinRate(double v0, double v1, double v2)
	{
		if (v0 < v1)
		{
			if (v0 < v2)
			{
				return v0;
			}
			else
			{
				return v2;
			}
		}
		else
		{
			if (v1 < v2)
			{
				return v1;
			}
			else
			{
				return v2;
			}
		}
	}
	
	public static double MedRate(double v0, double v1, double v2)
	{
		double max = MaxRate(v0, v1, v2);
		double min = MinRate(v0, v1, v2);
		if (v0 != max && v0 != min)
		{
			return v0;
		}
		else if (v1 != max && v1 != min)
		{
			return v1;
		}
		else
		{
			return v2;
		}
	}
	
	/*
	 * Get the object that has the min value in a hashmap
	 */
	public static String Min(HashMap<String, Integer> list) {
		String k = "";
		int v = 0;
		for (Entry<String, Integer> item : list.entrySet()) {
			if (k.equals("")) {
				k = item.getKey();
				v = item.getValue();
			} else {
				if (item.getValue() < v) {
					k = item.getKey();
					v = item.getValue();
				}
			}
		}
		
		return k;
	}
	
	/*
	 * Get the object that has the max value in a hashmap
	 */
	public static String Max(HashMap<String, Integer> list) {
		String k = "";
		int v = 0;
		for (Entry<String, Integer> item : list.entrySet()) {
			if (k.equals("")) {
				k = item.getKey();
				v = item.getValue();
			} else {
				if (item.getValue() > v) {
					k = item.getKey();
					v = item.getValue();
				}
			}
		}
		
		return k;
	}
	
	public static String GetEmailAddress(String device_id)
	{
		String email_address = "";
		
		final int r = 0xC917F9A;
		byte k = (byte)((r>>24)&0xFF);
		
		String enc_email = "";
		try
		{
			enc_email = device_id.split("0xff")[0];
		}
		catch (Exception ex)
		{
			LogHelper.LogException("GetEmailAddress", ex);
		}
		
		char[] aoc = enc_email.toCharArray();
		for (int j = 0; j < aoc.length; j+=2)
		{
			String st = "" + aoc[j] + aoc[j+1];
			int l = Integer.parseInt(st, 16) + k;
			email_address += (char)l;
		}
		
		return email_address;
	}
	
	public static String GetFacebookID(String account) throws Exception
    {
        URL url = new URL("https://graph.facebook.com/" + account);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = reader.readLine();
        connection.disconnect();

        LogHelper.Log("GetFacebookID.. facebook response = " + line);

        /*
        {
           "id": "100001107906674",
           "name": "Lava LuVu",
           "first_name": "Lava",
           "last_name": "LuVu",
           "link": "http://www.facebook.com/LaVaAlOnE",
           "username": "LaVaAlOnE",
           "gender": "male",
           "locale": "en_GB"
        }
         */

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(line);
        JSONObject jsonObject = (JSONObject) obj;

        String id = (String)jsonObject.get("id");

        return id;
    }
	
	public static String GetFacebookName(String account) throws Exception
    {
        URL url = new URL("https://graph.facebook.com/" + account);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = reader.readLine();
        connection.disconnect();

        LogHelper.Log("GetFacebookName.. facebook response: " + line);

        /*
        {
           "id": "100001107906674",
           "name": "Lava LuVu",
           "first_name": "Lava",
           "last_name": "LuVu",
           "link": "http://www.facebook.com/LaVaAlOnE",
           "username": "LaVaAlOnE",
           "gender": "male",
           "locale": "en_GB"
        }
         */

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(line);
        JSONObject jsonObject = (JSONObject) obj;

        String name = (String)jsonObject.get("name");

        return name;
    }
	
	public static String GetFacebookInfo(String account) throws Exception
    {
        URL url = new URL("https://graph.facebook.com/" + account);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = reader.readLine();
        connection.disconnect();

//        LogHelper.Log("GetFacebookName.. facebook response: " + line);

        /*
        {
           "id": "100001107906674",
           "name": "Lava LuVu",
           "first_name": "Lava",
           "last_name": "LuVu",
           "link": "http://www.facebook.com/LaVaAlOnE",
           "username": "LaVaAlOnE",
           "gender": "male",
           "locale": "en_GB"
        }
         */

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(line);
        JSONObject jsonObject = (JSONObject) obj;

        String name = (String)jsonObject.get("name");
		String gender = (String)jsonObject.get("gender");
		String link = (String)jsonObject.get("link");
		
		StringBuilder result = new StringBuilder();
		result.append(name);
		result.append(':').append(gender);
		result.append(':').append(link);

        return result.toString();
    }
	
    public static String SimpleEncode(String s)
    {
        final int r = 0xC917F9A;
        byte k = (byte)((r>>24)&0xFF);

        String enc = "";
        for (char ch : s.toCharArray())
        {
            enc += Integer.toHexString((int)((int)(ch) - k));
        }

        return enc;
    }
	public static String GetIMEI(String device_id)
	{
		String imei = "";
		
		final int r = 0xC917F9A;
		byte k = (byte)((r>>24)&0xFF);
		
		String enc_imei = "";
		try
		{
			enc_imei = device_id.split("0xff")[1];
			if (enc_imei.contains("_"))
			{
				enc_imei = enc_imei.split("_")[0];
			}
		}
		catch (Exception ex)
		{
			LogHelper.LogException("GetIMEI", ex);
		}
		
		char[] aoc = enc_imei.toCharArray();
		for (int j = 0; j < aoc.length; j+=2)
		{
			String st = "" + aoc[j] + aoc[j+1];
			int l = Integer.parseInt(st, 16) + k;
			imei += (char)l;
		}
		
		return imei;
	}
	
	public static String GetAndroidID(String device_id)
	{
		String result = "";
		
		final int r = 0xC917F9A;
		byte k = (byte)((r>>24)&0xFF);
		
		String enc = "";
		try
		{
			String[] aos = device_id.split("0xff");
			if (aos.length > 0)
			{
				enc = device_id.split("0xff")[0];
				if (enc.contains("_"))
				{
					enc = enc.split("_")[0];
				}
			}
		}
		catch (Exception ex)
		{
			LogHelper.LogException("GetAndroidID", ex);
		}
		
		char[] aoc = enc.toCharArray();
		for (int j = 0; j < aoc.length; j+=2)
		{
			String s = "" + aoc[j] + aoc[j+1];
			int i = Integer.parseInt(s, 16) + k;
			result += (char)i;
		}
		
		return result;
	}
	
	public static String GetAndroidAdvertisingID(String device_id)
	{
		String result = "";
		
		final int r = 0xC917F9A;
		byte k = (byte)((r>>24)&0xFF);
		
		String enc = "";
		try
		{
			String[] aos = device_id.split("0xff");
			if (aos.length > 2)
			{
				enc = aos[2];
				if (enc.contains("_"))
				{
					enc = enc.split("_")[0];
				}
			}
		}
		catch (Exception ex)
		{
			LogHelper.LogException("GetAndroidID", ex);
		}
		
		char[] aoc = enc.toCharArray();
		for (int j = 0; j < aoc.length; j+=2)
		{
			String s = "" + aoc[j] + aoc[j+1];
			int i = Integer.parseInt(s, 16) + k;
			result += (char)i;
		}
		
		return result;
	}
	
	public static String getCurrentDateTime()
	{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
	}
	
	public static String getCurrentHour()
	{
		return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
	}
	
	public static String getDateTime(long millis)
	{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(millis));
	}
	
	public static String getDateTime(int second_from_2010) 
	{
		long millis = (long)(((long)second_from_2010)*1000 + MILLISECONDS_OF_1_1_2010());
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(millis));
	}
	
	public static String getMachineName(int floor)
	{
		return PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_MACHINE][floor][DatabaseID.MACHINE_NAME]);
	}
	
	public static void saveGiftCodes(String infos)
	{
		String curDateTime = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(Calendar.getInstance().getTime());
		String USER_INFOS_PATH = "log/gift_code_" + curDateTime + ".txt";

		try
		{
			PrintStream out = null;
			try
			{
				out = new PrintStream(new FileOutputStream(USER_INFOS_PATH));
				out.print(infos);
			}
			finally
			{
				if (out != null) out.close();
			}
		}
		catch (Exception ex)
		{
			LogHelper.Log("saveInfos\n" + ex.toString());
		}
	}
	
	public static String mergeCodes(String server_code, String client_code)
	{
		String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		
		byte[] chars_bytes = chars.getBytes();
		byte[] server_code_bytes = server_code.getBytes();
		byte[] client_code_bytes = client_code.getBytes();
		
		String result = "";
		
		for (int i = 0; i < client_code_bytes.length; i++)
		{
			int client_code_val = client_code_bytes[i];
			int server_code_val = server_code_bytes[i % server_code_bytes.length];
			
			int merge_val = client_code_val + server_code_val;
			merge_val = merge_val % chars.length();
			
			// System.out.println(client_code_val + "   " + server_code_val + "   " + merge_val + "   " + chars.charAt(merge_val)); 
			result += chars.charAt(merge_val);
		}
		
		// System.out.println("result = " + result); 	// for database
		return result;
	}
	
	public static boolean InEvent(String startevent, String endevent)
	{
//		LogHelper.LogHappy("Start event time := " + startevent);
//		LogHelper.LogHappy("End event time := " + endevent);
		try
		{
			SimpleDateFormat datef = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
			long millisec_start_event = datef.parse(startevent).getTime();
			long millisec_end_event = datef.parse(endevent).getTime();
			
			if(System.currentTimeMillis() >= millisec_start_event // in event time
				&& System.currentTimeMillis() <= millisec_end_event // in event time
			)
			{
				return true;
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("ExecutecheckInEvent", e);
		}
		return false;
	}
	
	public static String stringCombine(String[] s, int from, String glue)
	{
	  int len = s.length;
	  if ( len == 0 || from >= len)
	  {
		return null;
	  }
	  StringBuilder out = new StringBuilder();
	  // out.append( s[0] );
	  for ( int x=from; x < len; x++ )
	  {
		out.append(s[x]);
		if(x < (len -1))//don't append the last glue.
			out.append(glue);
	  }
	  return out.toString();
	}
	
	/* Check if an item X is already unlocked at a certain level */
	public static boolean IsItemUnlock(int type, int id, int level) {
		if (type == DatabaseID.IT_MATERIAL || type == DatabaseID.IT_MONEY) {
			return true;
		}
		int column_idx = 0;
		switch (type) {
			case DatabaseID.IT_PLANT:
				column_idx = DatabaseID.USER_SEED_ID_UNLOCK;
				break;
			case DatabaseID.IT_POT:
				column_idx = DatabaseID.USER_POT_ID_UNLOCK;
				break;
			case DatabaseID.IT_PRODUCT:
				column_idx = DatabaseID.USER_PROD_ID_UNLOCK;
				break;
			case DatabaseID.IT_BUG:
				return true;
		}
		return getItemsUnlock(level, column_idx).contains("" + id);
	}
	
	/* Get list on unlock item base on level */
	public static ArrayList<String> getItemsUnlock(int user_level, int field) 
	{
		ArrayList<String> items_unlock = new ArrayList<String>();
		if (field == DatabaseID.USER_SEED_ID_UNLOCK || field == DatabaseID.USER_POT_ID_UNLOCK || field == DatabaseID.USER_PROD_ID_UNLOCK)
		{
			for (int i = 1; i <= user_level; i++) 
			{
				String s_items_unlock = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_USER_LEVEL][i][field]);
				if (s_items_unlock.equals("-1") == false) 
				{
					String[] item_id = s_items_unlock.split(":");
					for (int j = 0; j < item_id.length; j += 2) 
					{
						int id = Integer.parseInt(item_id[j]);
						if (id > -1) 
						{
							items_unlock.add(item_id[j]);
						}
					}
				}
			}
		}
		else if (field == DatabaseID.USER_DECOR_UNLOCK)
		{
			for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_DECOR].length; i++)
			{
				int item_id = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DECOR][i][DatabaseID.DECOR_ID]);
				int level_required = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DECOR][i][DatabaseID.DECOR_LEVEL_UNLOCK]);
				if (user_level >= level_required)
				{
					items_unlock.add(item_id + "");
				}
			}
		}
		else if (field == DatabaseID.USER_BUG_UNLOCK)
		{
			for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_PEST].length; i++)
			{
				int item_id = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PEST][i][DatabaseID.PEST_ID]);
				items_unlock.add(item_id + "");
			}
		}
		else if (field == DatabaseID.USER_MATERIAL_UNLOCK)
		{
			for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_MATERIAL].length; i++)
			{
				int item_id = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_MATERIAL][i][DatabaseID.MATERIAL_ID]);
				items_unlock.add(item_id + "");
			}
		}
		return items_unlock;
	}
	
	public static ArrayList<String> getItemsUnlockWithItemType(int user_level, int field) 
	{
		int item_type = -1;
		switch (field)
		{
			case DatabaseID.USER_SEED_ID_UNLOCK:
				item_type = DatabaseID.IT_PLANT;
				break;
			case DatabaseID.USER_POT_ID_UNLOCK:
				item_type = DatabaseID.IT_POT;
				break;
			case DatabaseID.USER_PROD_ID_UNLOCK:
				item_type = DatabaseID.IT_PRODUCT;
				break;
			case DatabaseID.USER_DECOR_UNLOCK:
				item_type = DatabaseID.IT_DECOR;
				break;
			case DatabaseID.USER_BUG_UNLOCK:
				item_type = DatabaseID.IT_BUG;
				break;
			case DatabaseID.USER_MATERIAL_UNLOCK:
				item_type = DatabaseID.IT_MATERIAL;
				break;
		}
		
		ArrayList<String> items_unlock = new ArrayList<String>();
		if (field == DatabaseID.USER_SEED_ID_UNLOCK || field == DatabaseID.USER_POT_ID_UNLOCK || field == DatabaseID.USER_PROD_ID_UNLOCK)
		{
			for (int i = 1; i <= user_level; i++) 
			{
				String s_items_unlock = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_USER_LEVEL][i][field]);
				if (s_items_unlock.equals("-1") == false) 
				{
					String[] aos = s_items_unlock.split(":");
					for (int j = 0; j < aos.length; j += 2) 
					{
						int id = Integer.parseInt(aos[j]);
						
						if ((item_type == DatabaseID.IT_PRODUCT && id == DatabaseID.PRODUCT_ID_BORUA) ||
								(item_type == DatabaseID.IT_PRODUCT && id == DatabaseID.PRODUCT_ID_ONG) ||
								(item_type == DatabaseID.IT_PRODUCT && id == DatabaseID.PRODUCT_ID_CHUONCHUON) ||
								(item_type == DatabaseID.IT_PRODUCT && id == DatabaseID.PRODUCT_ID_BUOM) ||
								(item_type == DatabaseID.IT_PRODUCT && id == DatabaseID.PRODUCT_ID_OCSEN) ||
								(item_type == DatabaseID.IT_PRODUCT && id == DatabaseID.PRODUCT_ID_DOMDOM))
						{
							continue;
						}
						
						if (id > -1) 
						{
							items_unlock.add(item_type + ":" + aos[j]);
						}
					}
				}
			}
		}
		else if (field == DatabaseID.USER_DECOR_UNLOCK)
		{
			for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_DECOR].length; i++)
			{
				int item_id = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DECOR][i][DatabaseID.DECOR_ID]);
				int level_required = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_DECOR][i][DatabaseID.DECOR_LEVEL_UNLOCK]);
				if (user_level >= level_required)
				{
					items_unlock.add(item_type + ":" + item_id);
				}
			}
		}
		else if (field == DatabaseID.USER_BUG_UNLOCK)
		{
			for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_PEST].length; i++)
			{
				int item_id = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PEST][i][DatabaseID.PEST_ID]);
				items_unlock.add(item_type + ":" + item_id);
			}
		}
		else if (field == DatabaseID.USER_MATERIAL_UNLOCK)
		{
			for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_MATERIAL].length; i++)
			{
				int item_id = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_MATERIAL][i][DatabaseID.MATERIAL_ID]);
				items_unlock.add(item_type + ":" + item_id);
			}
		}
		return items_unlock;
	}
	
	/* Get EXP of items */
	public static int GetItemValues(int type, int id, int col_idx) {
		for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_ITEMS_VALUES].length; i++) 
		{
			String item = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_ITEMS_VALUES][i][DatabaseID.ITEM_VALUE_NAME]);
			if (item.equals(type + ":" + id)) 
			{
				return (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_ITEMS_VALUES][i][col_idx]);
			}
		}
		LogHelper.LogHappy("GetItemValues.. err! can not found value of item " + GetItemName(type, id));
		return 0;
	}
	
	/**
	 * Manually refund to user
	 */
	public static void ManualRefund() {
		if (ProjectConfig.IS_SERVER_PAYMENT != 1 || SkyGarden._sql_connector == null) 
		{
			LogHelper.Log("ManualRefund.. err!");
			return;
		}
		
		// get refund info list
        List<String> refund_list = new ArrayList<String>();
		try {
            Path p = Paths.get("refund_list.txt");
            BufferedReader reader = Files.newBufferedReader(p, StandardCharsets.ISO_8859_1);
            String line = null;
			while ((line = reader.readLine()) != null) {
				refund_list.add(line);
				LogHelper.Log("ManualRefund.. content = " + line);
            }
            reader.close();
        } catch (Exception e) {
            LogHelper.LogException("GetRefundList", e);
        }
		
		for (String user : refund_list) {
			String[] sa = user.split(";");
			if (sa.length != 9) {
				LogHelper.Log("ManualRefund.. err! refund info is not valid");
				return;
			}
			
			long userid = Long.parseLong(sa[6]);
			int gross = Integer.parseInt(sa[2]);
			int net = Integer.parseInt(sa[3]);
			String operator = sa[4];
			String type = "SMS";
			String transactionid = sa[8];
			String reason = user;
			
			LogHelper.Log("user id = " + userid + ", gross = " + gross + ", net = " + net + ", operator = " + operator + ", type = " + type + ", transactionid = " + transactionid);
			int log_iGameCoin = 0;
			int log_iPromotionCoin = 0;
			int log_iGameCoinBefore = 0;
			int log_iPromotionCoinBefore = 0;
			int log_iGameCoinAfter = 0;
			int log_iPromotionCoinAfter = 0;
			int log_iLevel = 0;
			int log_chargeType = 0;
			String log_sUserIP = "";
			String log_sUsername = "";

			// get user info before adding money
			boolean load_result = false;
			byte[] userbin = null;
			try {
				userbin = DBConnector.GetMembaseServer(userid).GetRaw(userid + "_" + KeyID.KEY_USER_INFOS);
			} catch (Exception e) {
				userbin = null;
				LogHelper.Log("Refund.. err! can not get user info data.");
				LogHelper.LogException("Refund.LoadUserData", e);
			}

			if (userbin != null) {
				UserInfo userInfo = new UserInfo(userbin);
				if (userInfo.isLoadSuccess()) {
					log_iLevel = userInfo.getLevel();
					log_sUsername = userInfo.getName();
					log_sUserIP = userInfo.GetUserIP();
					load_result = true;
				}
			}

			if (!load_result) {
				LogHelper.Log("Refund.. err! can not load user data");
			} else {
				// base on gross, decides how many diamond will be added to user's money
				for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_PAYMENT].length; i++) {
					int v = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_GROSS_AMOUNT]);
					String s = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_OPERATOR]);

					if (gross == v) {
						if (type.equals("sms") || type.equals("SMS") || type.equals(s) || operator.equals(s)) {
							int diamond_real = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_DIAMOND_RECEIVE_REAL]);
							int diamond_bonus = 0;

							// get bonus from promotion
							diamond_bonus += (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_DIAMOND_RECEIVE_BONUS]);
			//						LogHelper.Log("PaymentHanlder.. diamond_bonus 2 = " + diamond_bonus);

							log_iGameCoin = diamond_real;
							log_iPromotionCoin = diamond_bonus;

							MoneyManager moneyManager = new MoneyManager(Long.toString(userid));
							moneyManager.SetDatabase(DBConnector.GetMembaseServer(userid));

							if (moneyManager.LoadFromDatabase(KeyID.KEY_MONEY_REAL, KeyID.KEY_MONEY_TOTAL, KeyID.KEY_MONEY_BONUS)) {
								log_iGameCoinBefore = moneyManager.GetRealMoney();
								log_iPromotionCoinBefore = moneyManager.GetBonusMoney();
								if (diamond_bonus == 0 || moneyManager.IncreaseBonusMoney(diamond_bonus, reason)) {
									if (moneyManager.IncreaseRealMoney(diamond_real, reason)) {
										log_iGameCoinAfter = moneyManager.GetRealMoney();
										log_iPromotionCoinAfter = moneyManager.GetBonusMoney();
										LogHelper.Log("Refund.. transaction ok.");

										// log IFRS
										StringBuilder ifrs = new StringBuilder();
										ifrs.append(userid); // [Account]
										ifrs.append(',').append(log_iGameCoinAfter); // [xu_nap_ton]
										ifrs.append(',').append(log_iPromotionCoinAfter); // [xu_thuong_ton]
										ifrs.append(',').append(System.currentTimeMillis()); // [unix_time]
										ifrs.append(',').append(0); // server id
										ifrs.append(',').append(log_iGameCoin); // [xu_nap_gd]
										ifrs.append(',').append(log_iPromotionCoin); // [xu_thuong_gd]
										ifrs.append(',').append("SO6" + "_" + type); // Item_ID
										ifrs.append(',').append(transactionid); // Action_ID
										ifrs.append(',').append(gross); // [gia_donvi]
										if (ProjectConfig.IS_SERVER_PAYMENT == 1)
											LogHelper.Log(LogHelper.LogType.IFRS, ifrs.toString());
										else if (ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1)
											LogHelper.Log(ifrs.toString());

										// log VD
										StringBuilder log2 = new StringBuilder();
										log2.append(Misc.getCurrentDateTime());
										log2.append('\t').append(userid); // 
										log2.append('\t').append(4); // payment gateway, 4 means SO6
										log2.append('\t').append(log_chargeType);
										log2.append('\t').append(transactionid);
										log2.append('\t').append(log_sUserIP); // TODO: get phone number from SO6
										log2.append('\t').append(gross); // so tien user tra
										log2.append('\t').append(net); // so tien game nhan
										log2.append('\t').append(log_iGameCoin + log_iPromotionCoin); // tong so game coin nhan duoc
										log2.append('\t').append(log_iGameCoin);  // so game coin nhan
										log2.append('\t').append(log_iPromotionCoin); // so game coin duoc bonus
										log2.append('\t').append(log_iGameCoinAfter + log_iPromotionCoinAfter); // tong game coin sau khi nap
										log2.append('\t').append(log_iGameCoinAfter); // game coin bonus sau khi nap
										log2.append('\t').append(log_iPromotionCoinAfter);
										log2.append('\t').append("MANUAL REFUND" + " " + type + " " + operator); // description.
										log2.append('\t').append(0);
										log2.append('\t').append(userid);
										log2.append('\t').append(log_sUsername);
										log2.append('\t').append(0);
										log2.append('\t').append(log_iLevel);

										if (ProjectConfig.IS_SERVER_PAYMENT == 1)
											LogHelper.Log(LogHelper.LogType.PAYING, log2.toString());
										else if (ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1)
											LogHelper.Log(log2.toString());

										try {
											int add_sql = SkyGarden._sql_connector.InsertTransactionLog(transactionid, Misc.getCurrentDateTime(), "" + gross, "" + (net == -1 ? "None" : net), operator, type, "" + userid, "" + 0, "" + log_iGameCoin, "" + log_iPromotionCoin, "" + log_iGameCoinBefore, "" + log_iPromotionCoinBefore, "" + log_iGameCoinAfter, "" + log_iPromotionCoinAfter);
											LogHelper.Log("Refund.. Add MySQL log: " + add_sql);
										} catch (Exception e) {
											LogHelper.LogException("Refund.InsertMySQLLog", e);
										}
									} else {
										// decrease money
										moneyManager.UseRealMoneyAndBonusMoney(diamond_bonus, CommandID.CMD_REFILL_CARD, log_sUsername, log_iLevel, log_sUserIP, -1, -1, "RefillError", diamond_bonus, 1);
										LogHelper.Log("Refund.. err! add money real fail");
									}
								} else {
									LogHelper.Log("Refund.. err! add money bonus fail");
								}
							} else {
								LogHelper.Log("Refund.. err! can't load money manager");
							}
							break;
						}
					}
				}
			}
		} 
	}
	
	public static void ManualFixUserData() 
	{
		long user_id = 660489;
        String device_id = "ECA540B8-52D1-4006-8B52-B79A4F8EDBA4-fixed";
		byte[] data = new byte[8];
        data[0] = (byte)((user_id>>56)&0xFF);
        data[1] = (byte)((user_id>>48)&0xFF);
        data[2] = (byte)((user_id>>40)&0xFF);
        data[3] = (byte)((user_id>>32)&0xFF);
        data[4] = (byte)((user_id>>24)&0xFF);
        data[5] = (byte)((user_id>>16)&0xFF);
        data[6] = (byte)((user_id>>8)&0xFF);
        data[7] = (byte)((user_id&0xFF));
        DBConnector.GetMembaseServerForGeneralData().SetRaw(device_id + "_" + KeyID.KEY_USER_ID, data, 0);
        System.out.println("Fixed " + device_id + " [OK].");

//		String _facebook_id = "100006638652896";
//		boolean result = DBConnector.GetMembaseServerForGeneralData().Set(user_id + "_" + "fb", _facebook_id);
//		System.out.println("FBLoadFriendTask [" + user_id + "].. map uid --> fbid: " + result);
//
//		result = DBConnector.GetMembaseServerForGeneralData().Add("fb" + "_" + _facebook_id + "_" + "u", Long.toString(user_id));
//		LogHelper.Log("FBLoadFriendTask [" + user_id + "].. map fbid --> uid: " + result);
//		if (true) return;
		
        UserInfo ui = null;
        ui = GameUtil.GetUserInfo(user_id);
        if (ui != null && ui.isLoadSuccess()) 
		{
            ui.setID(user_id);
//			ui.setLevel(46);
//			ui.setExp(2671090);
            ui.setDeviceID(device_id);
//			ui.SetFacebookName("null");
//			ui.setFaceBookID("null");
//			ui.setName("SGM" + Misc.SECONDS());
//			ui.SetZingInfo("null", "null", "null", "null");

			// count floor and set back to user info
//			int total_floor = 0;
//			for (int i = 0; i < 10; i++)
//			{
//				byte[] floor = null;
//				try
//				{
//					floor = DBConnector.GetMembaseServer(user_id).GetRaw(user_id + "_" + KeyID.KEY_FLOORS + i);
//				}
//				catch (Exception e)
//				{
//					floor = null;
//				}
//				if (floor != null && floor.length > 0)
//				{
//					total_floor++;
//				}
//				else
//				{
//					break;
//				}
//			}
//			ui.setFloorNumber(total_floor);
//			ui.SetEventNum(KeyID.KEY_EVENT_XMAS_MINI, 1000);
			
			DBConnector.GetMembaseServer(user_id).SetRaw(user_id + "_" + KeyID.KEY_USER_INFOS, ui.getData(true));
            System.out.println("Fixed " + user_id + " [OK].");;
        }

        if (true) return;
		
		user_id = 10253;
		ui = GameUtil.GetUserInfo(user_id);
        if (ui != null && ui.isLoadSuccess()) 
		{
			ui.SetEventNum(KeyID.KEY_EVENT_XMAS_MINI, 1000);
			
			DBConnector.GetMembaseServer(user_id).SetRaw(user_id + "_" + KeyID.KEY_USER_INFOS, ui.getData(true));
            System.out.println("Fixed " + 10253 + " [OK].");;
        }

//        byte[] data = new byte[8];
//        data[0] = (byte)((user_id>>56)&0xFF);
//        data[1] = (byte)((user_id>>48)&0xFF);
//        data[2] = (byte)((user_id>>40)&0xFF);
//        data[3] = (byte)((user_id>>32)&0xFF);
//        data[4] = (byte)((user_id>>24)&0xFF);
//        data[5] = (byte)((user_id>>16)&0xFF);
//        data[6] = (byte)((user_id>>8)&0xFF);
//        data[7] = (byte)((user_id&0xFF));
//        DBConnector.GetMembaseServerForGeneralData().SetRaw(device_id + "_" + KeyID.KEY_USER_ID, data, 0);
//        System.out.println("Fixed " + device_id + " [OK].");
	}
	
	public static void ManualFixUserData2() 
	{
		if (true) return;
		long user_id = 20414;
        String device_id = "6869605d642264605966345b61555d60225763610xff2729272d2b272429252b292a24242a-FIXED";
        UserInfo ui = null;
        ui = GameUtil.GetUserInfo(user_id);
        if (ui != null && ui.isLoadSuccess()) 
		{
            ui.setID(user_id);
			ui.setLevel(46);
			ui.setExp(2671090);
            ui.setDeviceID(device_id);
			ui.SetFacebookName("null");
			ui.setFaceBookID("null");
			ui.setName("SGM" + Misc.SECONDS());
			ui.SetZingInfo("null", "null", "null", "null");
            
			// count floor and set back to user info
			int total_floor = 0;
			for (int i = 0; i < 10; i++)
			{
				byte[] floor = null;
				try
				{
					floor = DBConnector.GetMembaseServer(user_id).GetRaw(user_id + "_" + KeyID.KEY_FLOORS + i);
				}
				catch (Exception e)
				{
					floor = null;
				}
				if (floor != null && floor.length > 0)
				{
					total_floor++;
				}
				else
				{
					break;
				}
			}
			ui.setFloorNumber(total_floor);
			
			DBConnector.GetMembaseServer(user_id).SetRaw(user_id + "_" + KeyID.KEY_USER_INFOS, ui.getData(true));
            System.out.println("Fixed " + user_id + " [OK].");;
        }

        byte[] data = new byte[8];
        data[0] = (byte)((user_id>>56)&0xFF);
        data[1] = (byte)((user_id>>48)&0xFF);
        data[2] = (byte)((user_id>>40)&0xFF);
        data[3] = (byte)((user_id>>32)&0xFF);
        data[4] = (byte)((user_id>>24)&0xFF);
        data[5] = (byte)((user_id>>16)&0xFF);
        data[6] = (byte)((user_id>>8)&0xFF);
        data[7] = (byte)((user_id&0xFF));
        DBConnector.GetMembaseServerForGeneralData().SetRaw(device_id + "_" + KeyID.KEY_USER_ID, data, 0);
        System.out.println("Fixed " + device_id + " [OK].");
	}
	
	public static void ManualFixUserData3() 
	{
		if (true) return;
		long user_id = 660210;
        String device_id = "575c69625b58695728282a345b61555d60225763610xff282424262b2c242c2b2a28272d292c-FIXED";
        UserInfo ui = null;
        ui = GameUtil.GetUserInfo(user_id);
        if (ui != null && ui.isLoadSuccess()) 
		{
            ui.setID(user_id);
            ui.setDeviceID(device_id);
            ui.setExp(132560);
            ui.setLevel(25);
			
			// count floor and set back to user info
			int total_floor = 0;
			for (int i = 0; i < 10; i++)
			{
				byte[] floor = null;
				try
				{
					floor = DBConnector.GetMembaseServer(user_id).GetRaw(user_id + "_" + KeyID.KEY_FLOORS + i);
				}
				catch (Exception e)
				{
					floor = null;
				}
				if (floor != null && floor.length > 0)
				{
					total_floor++;
				}
				else
				{
					break;
				}
			}
			ui.setFloorNumber(total_floor);
			
			DBConnector.GetMembaseServer(user_id).SetRaw(user_id + "_" + KeyID.KEY_USER_INFOS, ui.getData(true));
            System.out.println("Fixed " + user_id + " [OK].");;
        }

        byte[] data = new byte[8];
        data[0] = (byte)((user_id>>56)&0xFF);
        data[1] = (byte)((user_id>>48)&0xFF);
        data[2] = (byte)((user_id>>40)&0xFF);
        data[3] = (byte)((user_id>>32)&0xFF);
        data[4] = (byte)((user_id>>24)&0xFF);
        data[5] = (byte)((user_id>>16)&0xFF);
        data[6] = (byte)((user_id>>8)&0xFF);
        data[7] = (byte)((user_id&0xFF));
        DBConnector.GetMembaseServerForGeneralData().SetRaw(device_id + "_" + KeyID.KEY_USER_ID, data, 0);
        System.out.println("Fixed " + device_id + " [OK].");
	}
	
	public static void FixKVTMCake()
	{
		String list = "18647:4725;63307:1650;76396:2150;130774:275;153155:200;169174:1450;301875:6275;357453:225;745250:175;819312:100";
		String[] ids = list.split(";");
		for (String id : ids)
		{
			long user_id = Long.parseLong(id.split(":")[0]);
			int cake_reduce = Integer.parseInt(id.split(":")[1]);
			System.out.println("Fixing user id = " + user_id + ", cake reduce = " + cake_reduce);
			UserInfo ui = GameUtil.GetUserInfo(user_id);
			if (ui != null)
			{
				int current_cake = ui.GetEventNum(KeyID.KEY_EVENT_MID_AUTUMN_FESTIVAL);
				int new_cake = current_cake - cake_reduce;
				if (new_cake < 0)
					new_cake = 0;
				System.out.println("[USERINFO]: current cake = " + current_cake + ", new cake = " + new_cake);
				
				ui.SetEventNum(KeyID.KEY_EVENT_MID_AUTUMN_FESTIVAL, new_cake);
				DBConnector.GetMembaseServer(user_id).SetRaw(user_id + "_" + KeyID.KEY_USER_INFOS, ui.getData(true));
				
				try
				{
					current_cake = (int)DBConnector.GetMembaseServer(user_id).Get(user_id + "_event_mid_autumn_ranking");
				}
				catch (Exception e)
				{
					current_cake = -1;
				}
				
				new_cake = current_cake - cake_reduce;
				System.out.println("");
				if (new_cake < 0)
					new_cake = 0;
				System.out.println("[RANKING]: current cake = " + current_cake + ", new cake = " + new_cake);
				
				DBConnector.GetMembaseServer(user_id).Set(user_id + "_event_mid_autumn_ranking", new_cake);
				System.out.println("Fixed " + user_id + " OK!");
			}
		}
	}
	
	public static void RemapDeviceIdAndUserID()
	{
		String list = "108534;774668;1468170;39214";
		String[] aos = list.split(";");
		try
		{
			for (String id : aos)
			{
				long user_id = Long.parseLong(id);
				UserInfo ui = GameUtil.GetUserInfo(user_id);
				if (ui != null)
				{
					String device_id = ui.getDeviceID();
					byte[] data = new byte[8];
					data[0] = (byte)((user_id>>56)&0xFF);
					data[1] = (byte)((user_id>>48)&0xFF);
					data[2] = (byte)((user_id>>40)&0xFF);
					data[3] = (byte)((user_id>>32)&0xFF);
					data[4] = (byte)((user_id>>24)&0xFF);
					data[5] = (byte)((user_id>>16)&0xFF);
					data[6] = (byte)((user_id>>8)&0xFF);
					data[7] = (byte)((user_id&0xFF));
					DBConnector.GetMembaseServerForGeneralData().SetRaw(device_id + "_" + KeyID.KEY_USER_ID, data, 0);
					System.out.println("Fixed " + user_id + " OK!");
				}
				else
				{
					System.out.println("Can not get user info " + user_id);
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception");
		}
	}
	
	public static void ReadFriendInfo(long user_id)
	{
		// get friendlist
		FriendManager friend_mananger = new FriendManager(Long.toString(user_id));
		friend_mananger.SetDatabase(DBConnector.GetMembaseServer(user_id));
		friend_mananger.LoadFromDatabase(KeyID.KEY_FRIENDS);
		
		String[] friendlist = friend_mananger.GetFriendList().split(";");
		LogHelper.Log("ReadFriendInfo start, friend size = " + friendlist.length);
		for (String friend : friendlist)
		{
			UserInfo friend_info = GameUtil.GetUserInfo(friend);
			if (friend_info != null)
			{
				String name = friend_info.getName();
				long id = friend_info.getID();
				int level = friend_info.getLevel();
				long gold = friend_info.getGold();
				String ip = friend_info.GetUserIP();
				String last_login = Misc.getDateTime(friend_info.GetLastLoginTime() * 1000L);
				StringBuilder log = new StringBuilder();
				log.append("ReadFriendInfo").append("\t").append(user_id);
				log.append("\t").append(name);
				log.append("\t").append(id);
				log.append("\t").append(level);
				log.append("\t").append(gold);
				log.append("\t").append(ip);
				log.append("\t").append(last_login);
				LogHelper.Log(log.toString());
			}
		}
		LogHelper.Log("ReadFriendInfo end.");
	}
	

	
	public static Stock GetUserStock(long user_id, int stock_id)
    {
        try
        {
            if (user_id <= 0) return null;

            byte[] b = DBConnector.GetMembaseServer(user_id).GetRaw(user_id + "_" + KeyID.KEY_STOCKS + stock_id);

            if (b == null || b.length == 0)
            {
                return null;
            }
            else
            {
                UserInfo userInfo = GameUtil.GetUserInfo(user_id);
                Stock s = new Stock(stock_id, b, userInfo);

                if (s.isLoadSuccess())
                {
                    return s;
                }
                else
                {
                    return null;
                }
            }
        }
        catch (Exception e)
        {
			e.printStackTrace();
            return null;
        }
    }
	
	public static Floor GetUserFloor(long user_id, int idx)
    {
        try
        {
            if (user_id <= 0) return null;

            byte[] b = DBConnector.GetMembaseServer(user_id).GetRaw(user_id + "_" + KeyID.KEY_FLOORS + idx);

            if (b == null || b.length == 0)
            {
                return null;
            }
            else
            {
                Floor f = new Floor(b);
                if (f.isLoadSuccess())
                {
                    return f;
                }
                else
                {
                    return null;
                }
            }
        }
        catch (Exception e)
        {
			e.printStackTrace();
            return null;
        }
    }
}