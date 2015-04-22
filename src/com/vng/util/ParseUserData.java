package com.vng.util;

import java.util.*;
import java.io.*;

import com.vng.util.*;
import com.vng.log.*;
import com.vng.skygarden.*;
import com.vng.db.*;
import com.vng.skygarden.game.*;
import com.vng.skygarden._gen_.*;

public class ParseUserData
{
	public static boolean initNPCAccount()
	{
		String _device_id = KeyID.NPC_NAME;
		
		try
		{
			SkyGardenUser npc = new SkyGardenUser(null, null);
			npc.SetUserDeviceID(_device_id);
			
			boolean created = npc.generateNewUser();
			
			if (created)
			{
				long _user_id = -1;
				
				if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_DEV || ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_LIVE)
				{
					_user_id = DBConnector.GenerateNewUserID();
				}
				else
				{
					_user_id = Misc.GenerateNewUserID();
				}

				if (_user_id > 0)
				{
					npc.changeUserToStable(_user_id);
				}
				else
				{
					LogHelper.Log("initNPCAccount: ERROR! invalid new uid: " + _user_id);
				}
				
				DBKeyValue base = DBConnector.GetMembaseServer(_user_id);
				
				String _new_session_id = Misc.Hash(_device_id + System.currentTimeMillis() + _user_id + Math.random(), "SHA-256");
				base.Set(_user_id + "_" + KeyID.KEY_USER_SESSION_ID, _new_session_id);
				
				// load user info
				byte[] user_bin = null;
				try
				{
					user_bin = base.GetRaw(_user_id + "_" + KeyID.KEY_USER_INFOS);
				}
				catch (Exception e)
				{
					LogHelper.Log("initNPCAccount.. can not get user data.");
					user_bin = null;
				}
				
				if (user_bin == null || user_bin.length == 0)
				{
					LogHelper.Log("initNPCAccount.. can not get user data.");
				}
				
				UserInfo userInfo = new UserInfo(user_bin);
				userInfo.setLevel(35);
				userInfo.setName("JACK");

				int newFloorIndex;
				Machine machine;
				MachineDurability machine_durability;
				
				for (int i = 0; i < 5; i++)
				{
					if (i == 0)
					{
						machine = new Machine(i);	// may say
						machine_durability = new MachineDurability(i);
						machine.setStatus(DatabaseID.MACHINE_READY);
				        
						machine.setSlotCur(machine.getSlotCur() + 1);
						machine.getSlot().add(new SlotMachine(0, Misc.SECONDS() + 1000000000));	// hong say
						
						machine_durability.setDurability(machine_durability.getDurabilityMax());
						
						base.SetRaw(_user_id + "_" + (KeyID.KEY_MACHINES + i), machine.getData());
						base.SetRaw(_user_id + "_" + (KeyID.KEY_MACHINES_DURABILITY + i), machine_durability.getData());
					}
					else
					{
						userInfo.increaseFloorNumber();
						
						newFloorIndex = userInfo.getTotalFloor() - 1;
						Floor floor = new Floor(newFloorIndex);
						
						for (int j = 0; j < DatabaseID.SLOT_TUTORIAL; j++) 
						{
							floor.getSlot(j).getPot().createNewPot(0);
							floor.getSlot(j).getPot().getPlant().createNewPlant(i);
							floor.getSlot(j).getPot().getPlant().setGrowTime(Misc.SECONDS());
						}
						
						machine = new Machine(newFloorIndex);
						machine_durability = new MachineDurability(newFloorIndex);
						machine.setStatus(DatabaseID.MACHINE_READY);
						
						switch (i)
						{
							case 1:	// may nuoc ep
								machine.setSlotCur(machine.getSlotCur() + 1);
								machine.getSlot().add(new SlotMachine(14, Misc.SECONDS() + 1000000000));	// nuoc tao
								break;
							
							case 2:	// may det
								machine.setSlotCur(machine.getSlotCur() + 1);
								machine.getSlot().add(new SlotMachine(38, Misc.SECONDS() + 1000000000));	// vai do
								break;
							
							case 3:	// may che ngoc
								machine.setSlotCur(machine.getSlotCur() + 1);
								machine.getSlot().add(new SlotMachine(10, Misc.SECONDS() + 1000000000));	// ngoc do
								break;
							
							case 4:	// may tinh dau
								machine.setSlotCur(machine.getSlotCur() + 1);
								machine.getSlot().add(new SlotMachine(19, Misc.SECONDS() + 1000000000));	// tinh dau hoa hong
								break;
						}
						
						
						machine_durability.setDurability(machine_durability.getDurabilityMax());
						
						base.AddRaw(_user_id + "_" + (KeyID.KEY_FLOORS + newFloorIndex), floor.getData(true));
						base.AddRaw(_user_id + "_" + (KeyID.KEY_MACHINES + newFloorIndex), machine.getData());
						base.AddRaw(_user_id + "_" + (KeyID.KEY_MACHINES_DURABILITY + newFloorIndex), machine_durability.getData());
					}
				}
				
				base.SetRaw(_user_id + "_" + KeyID.KEY_USER_INFOS, userInfo.getData(true));
				
				LogHelper.Log("\n----------------------------------------------- created NPC done!"); 
				return true;
			}
			else
			{
				LogHelper.Log("\ninitNPCAccount: Cannot generateNewUser NPC account! " + _device_id + " is existing");
			}
		}
		catch (Exception e)
		{
			LogHelper.Log("\ninitNPCAccount: Cannot create NPC account! " + _device_id + " is existing");
		}
		
		return false;
	}
	
	public static void editNPCInfos()
	{
		String _device_id = KeyID.NPC_NAME;
		long _user_id = -1;
		
		try
		{
			SkyGardenUser npc = new SkyGardenUser(null, null);
			npc.SetUserDeviceID(_device_id);
			_user_id = npc.GetUserID(_device_id);
			npc.SetUserID(_user_id);
			
			DBKeyValue base = DBConnector.GetMembaseServer(_user_id);
				
			for (int i = 0; i < 5; i++)
			{
				byte[] floorbin = null;
				try
				{
					floorbin = base.GetRaw(_user_id + "_" + KeyID.KEY_FLOORS + i);
				}
				catch (Exception e)
				{
					LogHelper.Log("loadUserData.. can not get floor data from db.");
					floorbin = null;
				}
				
				if (floorbin == null || floorbin.length == 0)
				{
					LogHelper.Log("loadUserData.. floorbin is null. i = " + i);
				}
				
				Floor floor = new Floor(floorbin);

				for (int j = 0; j < DatabaseID.SLOT_TUTORIAL; j++) 
				{
					while (floor.getSlot(j).getPot().getID() > i+3) floor.getSlot(j).getPot().decreasePotID();
					while (floor.getSlot(j).getPot().getID() < i+3) floor.getSlot(j).getPot().increasePotID();
					
					// floor.getSlot(j).getPot().setID(i + 3);
					
					int a = floor.getSlot(j).getPot().getID();
				}
				
				base.SetRaw(_user_id + "_" + (KeyID.KEY_FLOORS + i), floor.getData(true));
				
				LogHelper.Log("Modified floor " + i + " DONE!"); 
			}
		}
		catch (Exception e)
		{
			LogHelper.Log("editNPCInfos: Cannot edit NPCInfos!");
		}
	}
		
	public static void parseUser(String _device_id)
	{
		try
		{
			LogHelper.Log("\nLOADING: " + _device_id);
			
			SkyGardenUser npc = new SkyGardenUser(null, null);
			npc.SetUserDeviceID(_device_id);
			npc.SetUserID(npc.GetUserID(_device_id));
			npc.SetStableUser(true);

			DBKeyValue base = DBConnector.GetMembaseServer(npc.GetUserID());
			npc.SetDBKeyValue(base);
			
			boolean loaded = npc.loadUserData();
			
			if (loaded)
			{
				LogHelper.Log("\n\n--------------------------------------");
				
					npc.userInfo.LogUser();
				
				LogHelper.Log("--------------------------------------");
				
					int floor_num = npc.floor.size();
					
					for (int i = 0; i < floor_num; i++)
					{
						npc.floor.get(i).LogFloor();
					}
				
				LogHelper.Log("--------------------------------------");
				
					int machine_num = npc.machine.size();
					
					for (int i = 0; i < machine_num; i++)
					{
						npc.machine.get(i).LogMachine();
					}
				
				LogHelper.Log("--------------------------------------");
				
					int machine_durability_num = npc.machineDurability.size();
					
					for (int i = 0; i < machine_durability_num; i++)
					{
						npc.machineDurability.get(i).LogMachineDurability();
					}
				
				LogHelper.Log("--------------------------------------");
				
					int stock_num = npc.stock.size();
					
					for (int i = 0; i < stock_num; i++)
					{
						npc.stock.get(i).LogStorage();
					}
				
				LogHelper.Log("--------------------------------------");
				
					npc.orderManager.LogOrderManager();
					// updateOrderManager_10591(npc);
				
				LogHelper.Log("--------------------------------------");
				
					npc.dailygift.displayDataPackage();
				
				LogHelper.Log("--------------------------------------");
				
				LogHelper.Log("\nexportUserData: " + _device_id + " is loaded!");
			}
			else
			{
				LogHelper.Log("\nexportUserData: " + _device_id + " is not existed!");
			}
		}
		catch (Exception e)
		{
			LogHelper.Log("\nexportUserData: can not load the account " + _device_id);
		}
	}
	
	public static void updateOrderManager_10591(SkyGardenUser skygarden_user)
	{
		try
		{
			long _user_id = 10591;
			DBKeyValue base = DBConnector.GetMembaseServer(_user_id);
					
			OrderManager orderManager = new OrderManager();
			
			for (int i = 1; i <= 6; i++)
			{
				orderManager.createOrder(i == (DatabaseID.ORDER_DAILY_INDEX+1) ? DatabaseID.ORDER_DAILY : DatabaseID.ORDER_NORMAL, 
					skygarden_user.getRandomProducts((int)(i-1)), 
					null, 
					skygarden_user.machine, 
					skygarden_user.floor, 
					skygarden_user.userInfo.getLevel(), 
					false,
					0,
					0);
			}
			
			base.SetRaw(_user_id + "_" + KeyID.KEY_ORDER, orderManager.getData());
		}
		catch (Exception e)
		{
			LogHelper.Log("" + e);
		}
	}
	
	public static void exportUserInfos()
	{
		try
		{
			LogHelper.Log("Connecting to database...\n");
			if (DBConnector.InitDBConnection() == false)
			{
				LogHelper.Log("Connect to database: FAIL !");
				return;
			}
			LogHelper.Log("\nConnecting to database OK !\n\n");
			
			String result = "";

			String userList = loadUserList();
			String[] user_list = userList.split("\n");
			
			LogHelper.Log("user_list.length = " + user_list.length);
			
			for (int i = 0; i < user_list.length; i++)
			{
				long _user_id = Long.parseLong(user_list[i]);
				LogHelper.Log("_user_id = " + _user_id);

				DBKeyValue base = DBConnector.GetMembaseServer(_user_id);
						
				byte[] user_bin = null;
				
				try
				{
					user_bin = base.GetRaw(_user_id + "_" + KeyID.KEY_USER_INFOS);
				}
				catch (Exception e)
				{
					user_bin = null;
				}
				
				if (user_bin == null || user_bin.length == 0)
				{
					LogHelper.Log("exportUserInfos.. can not get user " + _user_id);
					result += "can not get user " + _user_id;
				}
				else
				{
					FBEncrypt user = new FBEncrypt(user_bin);
					String name = user.getString(KeyID.KEY_USER_NAME);
					String device_id = user.getString(KeyID.KEY_DEVICE_ID);

					result += device_id + '\t' + _user_id + '\t' + name + '\n';
				}
				
				Thread.sleep(100);
			}
			
			saveUserInfos(result);
			
			LogHelper.Log("Export done!");
		}
		catch (Exception e)
		{
			LogHelper.Log("" + e);
		}
	}
	
	public static String loadUserList()
	{
		String infos = "";
		
		try
		{
			String USER_INFOS_PATH = "users/user_infos.txt";

			BufferedReader br = new BufferedReader(new FileReader(USER_INFOS_PATH));
			
			try
			{
				StringBuilder sb = new StringBuilder();
				String line = null;

				while ((line = br.readLine()) != null)
				{
					if (line != null && line.equals("") == false)
					{
						infos += line + "\n";
					}
				}
			}
			finally
			{
				br.close();
			}
		}
		catch (Exception ex)
		{
			LogHelper.Log("loadUserInfos\n" + ex.toString()); 
			infos = "";
		}

		return infos;
	}

	public static void saveUserInfos(String infos)
	{
		try
		{
			PrintStream out = null;
			try
			{
				String USER_INFOS_PATH = "users/user_infos.txt";
				
				out = new PrintStream(new FileOutputStream(USER_INFOS_PATH + ".done"));
				out.print(infos);
			}
			finally
			{
				if (out != null) out.close();
			}
		}
		catch (Exception ex)
		{
			LogHelper.Log("saveUserInfos\n" + ex.toString()); 
		}
	}	
}