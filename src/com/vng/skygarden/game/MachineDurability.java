package com.vng.skygarden.game;

import com.vng.skygarden.game.UserInfo;
import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import java.util.*;

public class MachineDurability
{
	private byte 	id;	
	private byte 	floor;
	private short 	durability_max;
	private short 	durability_cur;
	private short 	durability_repaired;
	private byte[] 	user;
	
	public MachineDurability(int floor)
	{
		this.floor = (byte)floor;
		this.id = Misc.getMachineID(floor);
		
		this.durability_cur = 0;
		this.durability_max = (short)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_MACHINE][this.floor][DatabaseID.MACHINE_DURABILITY]);
		
		this.durability_repaired = 0;
		this.user = null;
	}
	
	public MachineDurability(byte[] machine_bin)
	{
		FBEncrypt machine = new FBEncrypt(machine_bin);

		this.id = machine.getByte(KeyID.KEY_MACHINE_ID);
		this.floor = machine.getByte(KeyID.KEY_MACHINE_FLOOR);
		this.durability_cur = machine.getShort(KeyID.KEY_MACHINE_DURABILITY_CUR);
		this.durability_max = machine.getShort(KeyID.KEY_MACHINE_DURABILITY_MAX);
		
		this.durability_repaired = machine.getShort(KeyID.KEY_MACHINE_DURABILITY_REPAIRED);
		this.user = machine.getBinary(KeyID.KEY_USER_INFOS);
		
		// due to user data saved as byte so we can not read data (they return < 0) so we reinit these values
		if (this.durability_max < 0) this.durability_max = (short)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_MACHINE][this.floor][DatabaseID.MACHINE_DURABILITY]);
		if (this.durability_cur < 0) this.durability_cur = this.durability_max;
		if (this.durability_repaired < 0) this.durability_repaired = 0;
		
		// for safe
		if (this.durability_max < this.durability_cur) this.durability_max = this.durability_cur;
	}
	
	public byte[] getData()
	{
		FBEncrypt machine = new FBEncrypt();

		machine.addByte(KeyID.KEY_MACHINE_ID, this.id);
		machine.addByte(KeyID.KEY_MACHINE_FLOOR, this.floor);
		machine.addShort(KeyID.KEY_MACHINE_DURABILITY_CUR, this.durability_cur);
		machine.addShort(KeyID.KEY_MACHINE_DURABILITY_MAX, this.durability_max);
		
		if (this.user != null)
		{
			machine.addShort(KeyID.KEY_MACHINE_DURABILITY_REPAIRED, this.durability_repaired);
			machine.addBinary(KeyID.KEY_USER_INFOS, this.user);
		}
		
		return machine.toByteArray();
	}
	
	public short getDurability()
	{
		return this.durability_cur;
	}
	
	public void setDurability(int durability_cur)
	{
		this.durability_cur = (short)durability_cur;
	}	
	
	public short getDurabilityMax()
	{
		return this.durability_max;
	}
	
	public void setDurabilityMax(int durability_max)
	{
		this.durability_max = (short)durability_max;
	}	
	
	public short getDurabilityRepaired()
	{
		return this.durability_repaired;
	}
	
	public void setDurabilityRepaired(int durability_repaired)
	{
		this.durability_repaired = (short)durability_repaired;
	}	
	
	public byte[] getUser()
	{
		return this.user;
	}
	
	public void setUser(byte[] user)
	{
		this.user = user;
	}
	
	public int getID()
	{
		return (int)this.id;
	}
	
	public int getFloor()
	{
		return (int)this.floor;
	}
	
	public void displayDataPackage()
	{
		FBEncrypt machine = new FBEncrypt(getData());
	
		LogHelper.Log("id: " + machine.getByte(KeyID.KEY_MACHINE_ID));
		LogHelper.Log("floor: " + machine.getByte(KeyID.KEY_MACHINE_FLOOR));
		LogHelper.Log("durability: " + machine.getShort(KeyID.KEY_MACHINE_DURABILITY_CUR));
		LogHelper.Log("durability_max: " + machine.getShort(KeyID.KEY_MACHINE_DURABILITY_MAX));
		
		LogHelper.Log("durability_repaired: " + machine.getShort(KeyID.KEY_MACHINE_DURABILITY_REPAIRED));

		LogHelper.Log("repairer: ");
		UserInfo repairer = new UserInfo(machine.getBinary(KeyID.KEY_USER_INFOS));
		repairer.displayDataPackage();
	}
	
	public void LogMachineDurability()
	{
		StringBuilder log = new StringBuilder();
		log.append("machine_durability");
		log.append('\t').append(this.id);
		log.append('\t').append(this.floor);
		log.append('\t').append(this.durability_cur);
		log.append('\t').append(this.durability_max);
		log.append('\t').append(this.durability_repaired);
		
		UserInfo user = new UserInfo(this.user);
		log.append('\t').append(user.getDeviceID());
		
		LogHelper.Log(log.toString());
	}
}