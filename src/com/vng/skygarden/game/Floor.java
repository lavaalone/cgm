package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import com.vng.skygarden.*;

public class Floor
{
	private short id;
	private boolean load_result = true;

	Slot[] slot;
	
	final boolean LOG_FLOOR = !true;
	
	private boolean changed = true;

	public Floor(int floor_id) 
	{
		this.id = (short)floor_id;
		
		slot = new Slot[DatabaseID.MAX_SLOT_PER_FLOOR];
		
		for (int i = 0; i < DatabaseID.MAX_SLOT_PER_FLOOR; i++) 
		{
			slot[i] = new Slot(i);
		}
		
		changed = true;
	}
	
	public Floor(byte[] floor_bin)
	{
		FBEncrypt floor = new FBEncrypt(floor_bin);

		this.id = floor.getShort(KeyID.KEY_FLOOR_INDEX);

		// load data for slot
		slot = new Slot[DatabaseID.MAX_SLOT_PER_FLOOR];

		for (int i = 0; i < DatabaseID.MAX_SLOT_PER_FLOOR; i++)
		{
			byte[] slot_bin = floor.getBinary("slot_" + i);

			if (slot_bin == null || slot_bin.length == 0)
			{
				LogHelper.Log("Floor.. can not load data for slot " + i);
				load_result = false;
				return;
			}

			FBEncrypt slot_data = new FBEncrypt(slot_bin, true);
			slot_data.addShort(KeyID.KEY_SLOT_INDEX, i);

			slot[i] = new Slot(slot_data.toByteArray());
			if (slot[i].isLoadSuccess() == false)
			{
				load_result = false;
				return;
			}
		}
	}
	
	public boolean isLoadSuccess()
	{
		return load_result;
	}
	
	public short getID() 
	{
		return id;
	}

	public Slot getSlot(int index)
	{
		return slot[index];
	}
	
	public byte[] getData(boolean save) 
	{
		FBEncrypt floor = new FBEncrypt();
		floor.addShort(KeyID.KEY_FLOOR_INDEX, (short)id);

		for (int i = 0; i < DatabaseID.MAX_SLOT_PER_FLOOR; i++) 
		{
			floor.addBinary(KeyID.KEY_SLOTS + i, slot[i].getData(save));
		}
		
		floor.addInt(KeyID.KEY_FLOOR_COMBO_ID, GetComboID());
		
		if (LOG_FLOOR)
		{
			LogFloor();
		}
		
		if (save)
		{
			changed = false;
		}

		return floor.toByteArray();
	}
	
	public void LogFloor()
	{
		StringBuilder l = new StringBuilder();
		l.append("floor_" + getID());
		
		for (int i = 0; i < DatabaseID.MAX_SLOT_PER_FLOOR; i++)
		{
			l.append('\t').append(slot[i].getIndex());
			l.append('\t').append(slot[i].pot.getID());
			l.append('\t').append(slot[i].pot.plant.getID());
			
			// l.append('\t').append(slot[i].pot.getName());
			// l.append('\t').append(slot[i].pot.plant.getName());
		}
		
		LogHelper.Log(l.toString());
	}
	
	public int GetComboID()
	{
		for (int i = 1; i < DatabaseID.MAX_SLOT_PER_FLOOR; i++)
		{
			int cPotID = slot[i].pot.getID();
			int pPotID = slot[i-1].pot.getID();
			
			if (cPotID != pPotID)
			{
				return -1;
			}
		}
		
//		LogHelper.Log("Floor.. combo at floor " + getID());
		
		return slot[0].pot.getID();
	}
	
	public boolean isChanged()
	{
		for (int i = 1; i < DatabaseID.MAX_SLOT_PER_FLOOR; i++)
		{
			if (slot[i].isChanged()) return true;
		}
		return changed;
	}
}