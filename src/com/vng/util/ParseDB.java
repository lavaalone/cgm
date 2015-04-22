package com.vng.util;

import com.vng.log.LogHelper;
import com.vng.netty.*;
import com.vng.skygarden.*;
import com.vng.skygarden.game.*;
import com.vng.db.*;

import java.io.*;
import java.util.*;

public class ParseDB
{
	public static boolean checkDBValid()
	{
		LogHelper.Log("");
		
		int data_type = 0;
		
		for (int i = DatabaseID.SHEET_USER; i < DatabaseID.SHEET_MAX; i++)
		{
			// LogHelper.Log("\n*************** sheet " + i + " ***************\n"); 
			int sheet_index = i;
			
			int max_row = Server.s_globalDB[sheet_index].length;
			int max_column = Server.s_globalDB[sheet_index][0].length;
			
			for (int column = 0; column < max_column; column++)
			{
				// LogHelper.Log("\n---------- " + column + " ----------\n"); 
				
				data_type = 0;
				
				for (int row = 0; row < max_row; row++)
				{
					Object contentOfCell = Server.s_globalDB[sheet_index][row][column];
					
					if (contentOfCell instanceof String)
					{
						if (data_type != 0 && data_type != 1)
						{
							LogHelper.Log("Data error at sheet " + sheet_index + ", col = " + column + ", row = " + row);
							return false;
						}
						
						data_type = 1;
						// LogHelper.Log("++++ String"); 
					}
					else
					{
						if (data_type != 0 && data_type != 2)
						{
							LogHelper.Log("Data error at sheet " + sheet_index + ", col = " + column + ", row = " + row);
							return false;
						}
						
						data_type = 2;
						// LogHelper.Log("number"); 
					}
				}
			}
		}
		
		return true;
	}
	
	public static void parseData()
	{
		HashMap<String, String> key_name = new HashMap<String, String>();

		int sheet_index = DatabaseID.SHEET_MAX;
		
		long id;
		String type = "";
		String name = "";
		String key;
		
		for (int i = 0; i < Server.s_globalDB[sheet_index].length; i++)
		{
			id = Misc.PARSE_LONG(Server.s_globalDB[sheet_index][i][0]);
			name = Misc.PARSE_STRING(Server.s_globalDB[sheet_index][i][1]);
			
			if (id == -1)
			{
				type = name;
				
				if (type.equals("-1"))
				{
					type = "";
				}
			}
			else
			{
				if (type.equals(""))
					key = id + "";
				else
					key = type + ":" + id;

				// System.out.print(name + "       " + key); 
				
				key_name.put(name, key);
			}
			
			// LogHelper.Log(); 
		}
		
		// for (Entry<String, String> item: key_name.entrySet())
		// {
			// LogHelper.Log(item.getKey() + " -> " + item.getValue()); 
		// }
		
		for (int i = DatabaseID.SHEET_USER; i < DatabaseID.SHEET_MAX; i++)
		{
			// int i = 3;
			
			if (	i == DatabaseID.SHEET_USER_LEVEL 	||
					i == DatabaseID.SHEET_POT 			|| 
					i == DatabaseID.SHEET_STOCK_UPGRADE || 
					i == DatabaseID.SHEET_MACHINE 		|| 
					i == DatabaseID.SHEET_PRODUCT 		|| 
					i == DatabaseID.SHEET_FLOOR_UNLOCK	|| 
					i == DatabaseID.SHEET_ITEM_DROP		||
					i == DatabaseID.SHEET_IBSHOP_ITEM	||
					i == DatabaseID.SHEET_FRIEND_BUG	||
					i == DatabaseID.SHEET_DECOR			||
					i == DatabaseID.SHEET_GIFTS_INFO	||
					i == DatabaseID.SHEET_DAILY_GIFT	||
					i == DatabaseID.SHEET_FIRST_PAY		||
					i == DatabaseID.SHEET_CONSTANT		||
					i == DatabaseID.SHEET_EVENT			||
					i == DatabaseID.SHEET_EVENT_MAIN_OBJECT		||
					i == DatabaseID.SHEET_EVENT_MAIN_ITEM ||
					i == DatabaseID.SHEET_AIRSHIP ||
					i == DatabaseID.SHEET_ITEMS_VALUES ||
					i == DatabaseID.SHEET_ACHIEVEMENT ||
					i == DatabaseID.SHEET_INVITE_FRIEND ||
					i == DatabaseID.SHEET_EVENT_MID_AUTUMN ||
					i == DatabaseID.SHEET_NPC_MERCHANT ||
					i == DatabaseID.SHEET_ORDER_EVENT ||
					i == DatabaseID.SHEET_OPEN_EVENT_ITEM ||
					i == DatabaseID.SHEET_COMBO ||
					i == DatabaseID.SHEET_DROP_CUSTOM ||
					i == DatabaseID.SHEET_CLOSE_FRIEND ||
					i == DatabaseID.SHEET_TREASURE_TRUNK
				)
			{
				for (int col = 0; col < Server.s_globalDB[i][0].length; col++)
				{
					for (int row = 0; row < Server.s_globalDB[i].length; row++)
					{
						// khong parse
						if (	(i == DatabaseID.SHEET_SEED && (col == DatabaseID.SEED_NAME || 
																col == DatabaseID.SEED_NAME_EN || 
																col == DatabaseID.SEED_NAME_SC || 
																col == DatabaseID.SEED_NAME_TC))			||
								(i == DatabaseID.SHEET_POT && (col == DatabaseID.POT_NAME ||
																col == DatabaseID.POT_NAME_EN ||
																col == DatabaseID.POT_NAME_SC ||
																col == DatabaseID.POT_NAME_TC))			||
								(i == DatabaseID.SHEET_PEST && (col == DatabaseID.PEST_NAME ||
																col == DatabaseID.PEST_NAME_EN ||
																col == DatabaseID.PEST_NAME_SC ||
																col == DatabaseID.PEST_NAME_TC))			||
								(i == DatabaseID.SHEET_MACHINE && (col == DatabaseID.MACHINE_NAME ||
																	col == DatabaseID.MACHINE_NAME_EN ||
																	col == DatabaseID.MACHINE_NAME_SC ||
																	col == DatabaseID.MACHINE_NAME_TC))	||
								(i == DatabaseID.SHEET_PRODUCT && (col == DatabaseID.PRODUCT_NAME ||
																	col == DatabaseID.PRODUCT_NAME_EN ||
																	col == DatabaseID.PRODUCT_NAME_SC ||
																	col == DatabaseID.PRODUCT_NAME_TC))	||
								(i == DatabaseID.SHEET_IBSHOP_ITEM && (col == DatabaseID.IBSHOP_DESCRIPTION ||
																		col == DatabaseID.IBSHOP_DESCRIPTION_EN ||
																		col == DatabaseID.IBSHOP_DESCRIPTION_SC ||
																		col == DatabaseID.IBSHOP_DESCRIPTION_TC)) ||
								(i == DatabaseID.SHEET_IBSHOP_ITEM && (col == DatabaseID.IBSHOP_NAME ||
																		col == DatabaseID.IBSHOP_NAME_EN ||
																		col == DatabaseID.IBSHOP_NAME_SC ||
																		col == DatabaseID.IBSHOP_NAME_TC))		||
								(i == DatabaseID.SHEET_EVENT && (col == DatabaseID.EVENT_NAME ||
																	col == DatabaseID.EVENT_NAME_EN ||
																	col == DatabaseID.EVENT_NAME_SC ||
																	col == DatabaseID.EVENT_NAME_TC))		||
								(i == DatabaseID.SHEET_EVENT_MAIN_OBJECT && (col == DatabaseID.EMO_NAME ||
																			col == DatabaseID.EMO_NAME_EN ||
																			col == DatabaseID.EMO_NAME_SC ||
																			col == DatabaseID.EMO_NAME_TC))		||
								(i == DatabaseID.SHEET_EVENT_MAIN_ITEM && (col == DatabaseID.EMI_NAME ||
																			col == DatabaseID.EMI_NAME_EN ||
																			col == DatabaseID.EMI_NAME_SC ||
																			col == DatabaseID.EMI_NAME_TC))		||
								(i == DatabaseID.SHEET_GIFTS_INFO && (col == DatabaseID.GIFT_INFO_NAME ||
																		col == DatabaseID.GIFT_INFO_NAME_EN ||
																		col == DatabaseID.GIFT_INFO_NAME_SC ||
																		col == DatabaseID.GIFT_INFO_NAME_TC))		||
								(i == DatabaseID.SHEET_GIFTS_INFO && (col == DatabaseID.GIFT_INFO_DESCRIPTION ||
																		col == DatabaseID.GIFT_INFO_DESCRIPTION_EN ||
																		col == DatabaseID.GIFT_INFO_DESCRIPTION_SC ||
																		col == DatabaseID.GIFT_INFO_DESCRIPTION_TC))
							)
						{
							continue;
						}
						
						if (Server.s_globalDB[i][row][col] instanceof String)
						{
							String field = Misc.PARSE_STRING(Server.s_globalDB[i][row][col]);
							String[] items = field.split(":");
							
							for (int j = 0; j < items.length; j++)
							{
								if (items[j].length() > 0 && 
									(items[j].substring(0, 1).equals(" ") == true || items[j].substring(items[j].length()-1, items[j].length()).equals(" ") == true))
								{
									LogHelper.Log("\n" + field + " <-- need check!!!\n");
									items[j] = items[j].trim();
								}
								
								if (key_name.containsKey(items[j]))
								{
									// LogHelper.Log("before: " + field); 
									
									// LogHelper.Log(key_name.get(items[j])); 
									
									String type_id = "";
									
									// parse
									if (	(i == DatabaseID.SHEET_USER_LEVEL && col != DatabaseID.USER_REWARD_ITEM && col != DatabaseID.USER_LEVEL_SHARE_REWARD && col != DatabaseID.USER_ORDER_SLOT_UNLOCK && col != DatabaseID.USER_MACHINE_UNLOCK && col != DatabaseID.USER_NEW_COMER_GIFT)	||
//											(i == DatabaseID.SHEET_POT && col == DatabaseID.POT_PEARL_ID_NUM)			||
											(i == DatabaseID.SHEET_STOCK_UPGRADE)										||
											(i == DatabaseID.SHEET_MACHINE && col == DatabaseID.MACHINE_PRODUCT_ID)		||
											(i == DatabaseID.SHEET_EVENT && col == DatabaseID.EVENT_EMO_ID)				||
											(i == DatabaseID.SHEET_EVENT_MAIN_OBJECT && col == DatabaseID.EMO_EMI_ID)	||
											(i == DatabaseID.SHEET_CONSTANT && col == DatabaseID.CONSTANT_DO_RANDOM_PLANT)
										) // chi lay id, ko lay type
									{
										// LogHelper.Log("i = " + i); 
										// LogHelper.Log("col = " + col); 
										// LogHelper.Log("row = " + row); 
										// LogHelper.Log("field = " + field); 
										// LogHelper.Log(key_name.get(items[j])); 
										
										String[] s = key_name.get(items[j]).split(":");
										type_id = s[1];
									}
									else // lay id va type
									{
										type_id = key_name.get(items[j]);
									}
									
									// replace string (dont use replaceAll to avoid same item name such as 'Vot' and 'Vot Dai'
									String[] items_value = field.split(":");
									for (int r = 0; r < items_value.length; r++)
									{
										if (items_value[r].equals(items[j]))
										{
											items_value[r] = type_id;
										}
									}
									field = join(items_value, ":");
									
									// field = field.replaceAll(items[j], type_id);
									
									Server.s_globalDB[i][row][col] = field;
									
									// LogHelper.Log("after: " + field);
									// LogHelper.Log(""); 
								}
							}
						}
						
						if (i == DatabaseID.SHEET_USER_LEVEL && (col == DatabaseID.USER_MACHINE_UNLOCK || col == DatabaseID.USER_ORDER_SLOT_UNLOCK))
						{
							if (Server.s_globalDB[i][row][col] instanceof String)
							{
								double d = Double.parseDouble("" + Server.s_globalDB[i][row][col]);
								Server.s_globalDB[i][row][col] = d;
							}
						}
					}
				}
			}

		}
	}
	
	public static void parseGiftCodeData()
	{
		HashMap<String, String> key_name = new HashMap<String, String>();

		int sheet_index = 1;
		
		long id;
		String type = "";
		String name = "";
		String key;
		
		for (int i = 0; i < Server.s_globalGift[sheet_index].length; i++)
		{
			id = Misc.PARSE_LONG(Server.s_globalGift[sheet_index][i][0]);
			name = Misc.PARSE_STRING(Server.s_globalGift[sheet_index][i][1]);
			
			if (id == -1)
			{
				type = name;
				
				if (type.equals("-1"))
				{
					type = "";
				}
			}
			else
			{
				if (type.equals(""))
					key = id + "";
				else
					key = type + ":" + id;

				key_name.put(name, key);
			}
		}
		
		int i = DatabaseID.SHEET_GIFT_CODE;
		int col = DatabaseID.GIFT_CODE_GIFT;
		
		for (int row = 0; row < Server.s_globalGift[i].length; row++)
		{
			String field = Misc.PARSE_STRING(Server.s_globalGift[i][row][col]);
			String[] items = field.split(":");
			
			for (int j = 0; j < items.length; j++)
			{
				if (key_name.containsKey(items[j]))
				{
					String type_id = "";
					
					type_id = key_name.get(items[j]);
					
					// replace string (dont use replaceAll to avoid same item name such as 'Vot' and 'Vot Dai'
					String[] items_value = field.split(":");
					for (int r = 0; r < items_value.length; r++)
					{
						if (items_value[r].equals(items[j]))
						{
							items_value[r] = type_id;
						}
					}
					field = join(items_value, ":");
					
					// field = field.replaceAll(items[j], type_id);
					
					Server.s_globalGift[i][row][col] = field;
				}
			}
		}
		
		/*
		sheet_index = 0;
			
		int max_row = Server.s_globalGift[sheet_index].length;
		int max_column = Server.s_globalGift[sheet_index][0].length;
		
		for (int column = 0; column < max_column; column++)
		{
			LogHelper.Log("\n---------- " + column + " ----------\n"); 
			
			for (int row = 0; row < max_row; row++)
			{
				Object contentOfCell = Server.s_globalGift[sheet_index][row][column];
				LogHelper.Log("" + contentOfCell); 
			}
		}
		*/
	}
	
	/*
	private static String sortLevelUnlock(String field)
	{
		String[] values = field.split(":");
		
		Arrays.sort(values);
		
		String new_field = join(values, ":");
		return new_field;
	}
	*/
	
	public static String join(String[] values, String conjunction)
	{
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < values.length; i++)
		{
			if (i > 0)
			{
				sb.append(conjunction);
			}
			
			sb.append(values[i]);
		}
		return sb.toString();
	}	
	
	public static void loadSheetData()
	{
		Server.s_globalSheetData = new byte[Server.s_globalDB.length][];
		
		for (int i = DatabaseID.SHEET_USER; i < DatabaseID.SHEET_MAX; i++)	// skip USER sheet, client never use
		{
			if ( i == DatabaseID.SHEET_TUTORIAL || i == DatabaseID.SHEET_VERSION || i == DatabaseID.SHEET_FRIEND_BUG || i == DatabaseID.SHEET_CONSTANT)
			{
				continue;
			}
			
			Server.s_globalSheetData[i] = Misc.getGameConstant(i);
		}
	}
	
	public static void loadMachineUnlockData()
	{
		Server.s_globalMachineUnlockData = new long[Server.s_globalDB[DatabaseID.SHEET_MACHINE_UNLOCK].length][][];
		
		for (int floor = 0; floor < Server.s_globalDB[DatabaseID.SHEET_MACHINE_UNLOCK].length; floor++)
		{
			Server.s_globalMachineUnlockData[floor] = new long[DatabaseID.MACHINE_LEVEL_MAX][];
			
			for (int level = 0; level < DatabaseID.MACHINE_LEVEL_MAX; level++)
			{
				Server.s_globalMachineUnlockData[floor][level] = new long[DatabaseID.MACHINE_UNLOCK_PROPERTY_MAX];

				String[] values = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_MACHINE_UNLOCK][floor][level]).split(":");
				
				for (int property = 0; property < DatabaseID.MACHINE_UNLOCK_PROPERTY_MAX; property++)
				{
					Server.s_globalMachineUnlockData[floor][level][property] = Long.parseLong(values[property]);
				}
			}
		}
	}
	
	public static void initGiftCodeFromDB()
	{
		DBKeyValue base = DBConnector.GetMembaseServerForTemporaryData();
		String codes = "";
		
		for (int i = 0; i < Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE].length; i++)
		{
			long id = Misc.PARSE_LONG(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_ID]);
			String type = Misc.PARSE_STRING(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_TYPE]);
			String name = Misc.PARSE_STRING(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_NAME]);
			String description = Misc.PARSE_STRING(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_DESCRIPTION]);
			String gift = Misc.PARSE_STRING(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_GIFT]);
			String start_time = Misc.PARSE_STRING(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_START_TIME]);
			String end_time = Misc.PARSE_STRING(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_END_TIME]);
			long use_time = Misc.PARSE_LONG(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_USE_TIME]);
			
			String _id = "gc_" + id + "_" + type;
			int _start_time = Misc.SECONDS(start_time);
			int _end_time = Misc.SECONDS(end_time);
			int _expire_time = _end_time - Misc.SECONDS();
			int _use_time = (int)use_time;

			if (type.length() != Misc.GIFT_CODE_HEADER)
			{
				LogHelper.Log("Type of code " + _id + " should has 3 chars.");
				continue;
			}
			
			if (_end_time < Misc.SECONDS() || _end_time < _start_time)
			{
				LogHelper.Log("The gift code " + _id + " is out of date. Please check database!");
				continue;
			}
			
			if (use_time <=0)
			{
				LogHelper.Log("use_time of code " + _id + " should be > 0");
				continue;
			}
			
			String server_code = Misc.PARSE_STRING(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_CODE]);
			String client_code = Misc.generateRandomString();
			
			client_code = type + client_code;
			
			// use UPPERCASE for gift code
			server_code = server_code.toUpperCase();
			client_code = client_code.toUpperCase();
			
			String gift_code = Misc.mergeCodes(server_code, client_code);
			
			GiftCode giftCode = new GiftCode(_id, server_code, client_code, gift_code, name, description, gift, _start_time, _end_time, _use_time);
			
			// boolean saved = base.Add(_id, gift_code, _expire_time + Misc._DAY_SECONDS);
			
			int t1 = (int)((System.currentTimeMillis() / 1000) + _expire_time + Misc._DAY_SECONDS);
			boolean saved = base.Add(_id, gift_code, t1);
			
			if (saved)
			{
				// saved = base.Add(client_code, giftCode.getData(), _expire_time + Misc._DAY_SECONDS);
				
				int t2 = (int)((System.currentTimeMillis() / 1000) + _expire_time + Misc._DAY_SECONDS);
				saved = base.Add(client_code, giftCode.getData(), t2); 
			}
			
			if (saved == false)
			{
				LogHelper.Log("The gift code " + _id + " is existed in database!");
				codes += "The gift code " + _id + " is existed in database!\n";
			}
			else
			{
				LogHelper.Log("The gift code " + _id + " - " + client_code + " had been added!");
				codes += client_code + '\n';
			}
		}
		
		if (codes.equals("") == false)
		{
			Misc.saveGiftCodes(codes);
		}
		else
		{
			LogHelper.Log("Cannot generate gift codes.");
		}
	}
	
	public static void initGiftCodeFromDBToFile()
	{
		String codes = "";
		
		DBKeyValue client_gift_file = new DBFileStore();
		DBKeyValue server_gift_file = new DBFileStore();
		
		FBEncrypt client_codes_save_to_file = new FBEncrypt();
		FBEncrypt server_codes_save_to_file = new FBEncrypt();
		
		for (int i = 0; i < Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE].length; i++)
		{
			long id = Misc.PARSE_LONG(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_ID]);
			String type = Misc.PARSE_STRING(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_TYPE]);
			String name = Misc.PARSE_STRING(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_NAME]);
			String description = Misc.PARSE_STRING(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_DESCRIPTION]);
			String gift = Misc.PARSE_STRING(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_GIFT]);
			String start_time = Misc.PARSE_STRING(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_START_TIME]);
			String end_time = Misc.PARSE_STRING(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_END_TIME]);
			long use_time = Misc.PARSE_LONG(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_USE_TIME]);
			
			String _id = "gc_" + id + "_" + type;
			int _start_time = Misc.SECONDS(start_time);
			int _end_time = Misc.SECONDS(end_time);
			int _expire_time = _end_time - Misc.SECONDS();
			int _use_time = (int)use_time;

			if (type.length() != Misc.GIFT_CODE_HEADER)
			{
				LogHelper.Log("Type of code " + _id + " should has 3 chars.");
				continue;
			}
			
			if (_end_time < Misc.SECONDS() || _end_time < _start_time)
			{
				LogHelper.Log("The gift code " + _id + " is out of date. Please check database!");
				continue;
			}
			
			if (use_time <=0)
			{
				LogHelper.Log("use_time of code " + _id + " should be > 0");
				continue;
			}
			
			String server_code = Misc.PARSE_STRING(Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE][i][DatabaseID.GIFT_CODE_CODE]);
			String client_code = Misc.generateRandomString();
			
			client_code = type + client_code;
			
			// use UPPERCASE for gift code
			server_code = server_code.toUpperCase();
			client_code = client_code.toUpperCase();
			
			String gift_code = Misc.mergeCodes(server_code, client_code);
			
			GiftCode giftCode = new GiftCode(_id, server_code, client_code, gift_code, name, description, gift, _start_time, _end_time, _use_time);

			codes += client_code + '\n';
			
			// ---------------------------------------------------------------------------------------------------------------
			
			client_codes_save_to_file.addStringANSI("client_gift_id" + "_" + i, _id);
			client_codes_save_to_file.addStringANSI("client_gift_code" + "_" + i, gift_code);
			client_codes_save_to_file.addInt("client_gift_expire_time" + "_" + i, (_expire_time + Misc._DAY_SECONDS));
			
			server_codes_save_to_file.addStringANSI("server_gift_client_code" + "_" + i, client_code);
			server_codes_save_to_file.addBinary("server_gift_code" + "_" + i, giftCode.getData());
			server_codes_save_to_file.addInt("server_gift_expire_time" + "_" + i, (_expire_time + Misc._DAY_SECONDS));
			
			System.out.println(_id + '\t' + 
								gift_code + '\t' + 
								((_expire_time + Misc._DAY_SECONDS)) + '\t' + 
								client_code + '\t' + 
								giftCode.getData().length + '\t' + 
								(_expire_time + Misc._DAY_SECONDS)); 
		}
		
		client_codes_save_to_file.addInt("client_gift_total", Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE].length);
		server_codes_save_to_file.addInt("server_gift_total", Server.s_globalGift[DatabaseID.SHEET_GIFT_CODE].length);
		
		boolean client_saved = client_gift_file.AddRaw("client_gift_code_binary.bin", client_codes_save_to_file.toByteArray());
		boolean server_saved = server_gift_file.AddRaw("server_gift_code_binary.bin", server_codes_save_to_file.toByteArray());
		
		if (client_saved && server_saved)
		{
			Misc.saveGiftCodes(codes);
		}
		else
		{
			System.out.println("Cannot generate gift codes.");
		}
	}
	
	public static void importGiftCodeFromFileToDB()
	{
		System.out.println("\nImport gift code to database..."); 
		
		try
		{
			DBKeyValue base = DBConnector.GetMembaseServerForTemporaryData();
			
			DBKeyValue client_gift_file = new DBFileStore();
			DBKeyValue server_gift_file = new DBFileStore();
			
			byte[] data_client_gift = client_gift_file.GetRaw("client_gift_code_binary.bin");
			byte[] data_server_gift = server_gift_file.GetRaw("server_gift_code_binary.bin");

			FBEncrypt client_codes_save_to_file = new FBEncrypt(data_client_gift);
			FBEncrypt server_codes_save_to_file = new FBEncrypt(data_server_gift);

			int client_gift_total = client_codes_save_to_file.getInt("client_gift_total");
			int server_gift_total = server_codes_save_to_file.getInt("server_gift_total");
			
			if (client_gift_total != server_gift_total)
			{
				System.out.println("Can not import gift data. Should re-check!!!"); 
				return;
			}
			
			boolean saved = false;
			
			for (int i = 0; i < client_gift_total; i++)
			{
				String client_gift_id 		= client_codes_save_to_file.getString("client_gift_id" + "_" + i);
				String client_gift_code 	= client_codes_save_to_file.getString("client_gift_code" + "_" + i);
				int client_gift_expire_time = client_codes_save_to_file.getInt("client_gift_expire_time" + "_" + i);
				
				// saved = base.Add(client_gift_id, client_gift_code, client_gift_expire_time);
				
				int t1 = (int)((System.currentTimeMillis() / 1000) + client_gift_expire_time);
				saved = base.Add(client_gift_id, client_gift_code, t1);
				// System.out.println("gift " + i + " = " + saved); 
				
				if (saved)
				{
					String server_gift_client_code 	= server_codes_save_to_file.getString("server_gift_client_code" + "_" + i);
					byte[] server_gift_code 		= server_codes_save_to_file.getBinary("server_gift_code" + "_" + i);
					int server_gift_expire_time 	= server_codes_save_to_file.getInt("server_gift_expire_time" + "_" + i);
					
					// saved = base.Add(server_gift_client_code, server_gift_code, server_gift_expire_time);
					
					int t2 = (int)((System.currentTimeMillis() / 1000) + server_gift_expire_time);
					saved = base.Add(server_gift_client_code, server_gift_code, t2);
					
					if (saved)
						System.out.println(client_gift_id + '\t' + 
											client_gift_code + '\t' + 
											client_gift_expire_time + '\t' + 
											
											server_gift_client_code + '\t' + 
											server_gift_code.length + '\t' + 
											server_gift_expire_time); 
				}
				
				if (!saved)
				{
					System.out.println("break break break "); 
					break;
				}
			}
			
			if (!saved)
			{
				System.out.println("Cannot generate gift codes.");
			}

		}
		catch (Exception e)
		{		
			System.out.println(e.toString()); 
		}
	}	

	public static void checkGiftCode(String code)
	{
		System.out.println("\nGift code: " + code); 
		
		try
		{
			DBKeyValue base = DBConnector.GetMembaseServerForTemporaryData();
			
			byte[] gift_data = null;
			try
			{
				gift_data = base.GetRaw(code);
			}
			catch (Exception e)
			{
				LogHelper.Log("checkGiftCode.. can not get gift data.");
				gift_data = null;
			}
			
			if (gift_data == null || gift_data.length == 0)
			{
				if (gift_data == null) System.out.println("gift code data is null"); 
				else System.out.println("gift_data.length = " + gift_data.length); 
				return;
			}

			GiftCode giftCode = new GiftCode(gift_data);
			giftCode.displayDataPackage();
		}
		catch (Exception e)
		{		
			System.out.println(e.toString()); 
		}
	}	
	
	// DataInputStream dis_client_gift = new DataInputStream(new FileInputStream(".client_gift_code_binary.bin"));
	// byte[] data_client_gift = new byte[dis_client_gift.available()];
	// dis_client_gift.read(data_client_gift);
	// dis_client_gift.close();

	// System.out.println("data_client_gift.length = " + data_client_gift.length); 
	
	// DataInputStream dis_server_gift = new DataInputStream(new FileInputStream(".server_gift_code_binary.bin"));
	// byte[] data_server_gift = new byte[dis_server_gift.available()];
	// dis_server_gift.read(data_server_gift);
	// dis_server_gift.close();
	
	// System.out.println("data_server_gift.length = " + data_server_gift.length); 
}