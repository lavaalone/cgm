package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;

public class MachineRepairLimit
{
	private short	reputation_max_per_date;	
	private short	reputation_collected_per_date;
	private int		daily_reset_time;
	
	private final boolean LOG = !true;
	
	public MachineRepairLimit(int user_level)
	{
		this.reputation_max_per_date = (short)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][DatabaseID.CONSTANT_REPUTATION_COLLECT_MAX]);
		this.reputation_collected_per_date = 0;
		this.daily_reset_time = 0;
	}
	
	public MachineRepairLimit(byte[] machine_bin)
	{
		FBEncrypt mr = new FBEncrypt(machine_bin);

		this.reputation_max_per_date = mr.getShort(KeyID.KEY_REPUTATION_MAX_PER_DATE);
		this.reputation_collected_per_date = mr.getShort(KeyID.KEY_REPUTATION_COLLECTED_PER_DATE);
		this.daily_reset_time = mr.getInt(KeyID.KEY_MACHINE_REPAIR_RESET_TIME);
		
		// should check isNewDate to update Reputation max
	}
	
	public boolean checkConditionMachineRepair()
	{
		boolean can_repair_machine = (this.reputation_collected_per_date < this.reputation_max_per_date);
//		LogHelper.Log("checkConditionMachineRepair: " + can_repair_machine + " " + this.reputation_collected_per_date + " " + this.reputation_max_per_date);
		
		return can_repair_machine;
	}
	
	public byte[] getData()
	{
		FBEncrypt mr = new FBEncrypt();

		mr.addShort(KeyID.KEY_REPUTATION_MAX_PER_DATE, this.reputation_max_per_date);
		mr.addShort(KeyID.KEY_REPUTATION_COLLECTED_PER_DATE, this.reputation_collected_per_date);
		mr.addInt(KeyID.KEY_MACHINE_REPAIR_RESET_TIME, this.daily_reset_time);
		
		if (LOG)
		{
			LogMachineDurability();
		}
		
		return mr.toByteArray();
	}

	public void updateReputationMax(int user_level)
	{
		this.reputation_max_per_date = (short)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][DatabaseID.CONSTANT_REPUTATION_COLLECT_MAX]);
	}
	
	public boolean isNewDate()
	{
		boolean is_new_date = (Misc.SECONDS() > daily_reset_time);
		
		if (is_new_date)
		{
			resetMachineRepairLimit();
		}

		return is_new_date;
	}
	
	public void resetMachineRepairLimit()
	{
		this.reputation_collected_per_date = 0;
		this.daily_reset_time = Misc.getDailyResetTime();
	}
	
	public int getDailyResetTime()
	{
		return this.daily_reset_time;
	}
	
	public void setDailyResetTime(int daily_reset_time)
	{
		this.daily_reset_time = daily_reset_time;
	}

	public short getReputationMaxPerDate()
	{
		return this.reputation_max_per_date;
	}

	public void setReputationMaxPerDate(int reputation_max_per_date)
	{
		this.reputation_max_per_date = (short)reputation_max_per_date;
	}
	
	public short getReputationCollectedPerDate()
	{
		return this.reputation_collected_per_date;
	}

	public void increaseReputationCollectedPerDate(int value)
	{
		// check new date to reset MRL
		isNewDate();
		
		this.reputation_collected_per_date += (short)value;
	}
	
	// public void setReputationCollectedPerDate(int value)
	// {
		// this.reputation_collected_per_date = (short)value;
	// }

	public void displayDataPackage()
	{
		LogHelper.Log("reputation_max_per_date: " + this.reputation_max_per_date);
		LogHelper.Log("reputation_collected_per_date: " + this.reputation_collected_per_date);
		LogHelper.Log("daily_reset_time: " + this.daily_reset_time);
	}
	
	public void LogMachineDurability()
	{
		StringBuilder log = new StringBuilder();
		log.append("MachineRepairLimit");
		log.append('\t').append(this.reputation_max_per_date);
		log.append('\t').append(this.reputation_collected_per_date);
		log.append('\t').append(this.daily_reset_time);
		
		LogHelper.Log(log.toString());
	}
}