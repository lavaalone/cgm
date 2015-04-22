package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;

public class OwlSlot
{
	private byte 	food_id = -1;			// ma thuc an
	private int 	digest_time = -1;		// thoi gian tieu hoa
	
    private boolean load_result = true;
	
	public OwlSlot(int _food_id, int _digest_time)
	{
		this.food_id = (byte)_food_id;
		this.digest_time = _digest_time;
		
        if (_food_id < 0 || _digest_time < 0)
        {
            LogHelper.Log("OwlSlot.. can not init slot.");
            load_result = false;
            return;
        }
	}
	
	public OwlSlot(byte[] slot_bin)
	{
		FBEncrypt slot = new FBEncrypt(slot_bin);

		this.food_id = slot.getByte(KeyID.KEY_OWL_FOOD_ID);
		this.digest_time = slot.getInt(KeyID.KEY_OWL_DIGEST_TIME);
		
		if (food_id < 0 || digest_time < 0)
		{
			LogHelper.Log("OwlSlot.. can not load OwlSlot data.");
            load_result = false;
		}
	}
	
    public boolean isLoadSuccess()
    {
        return load_result;
    }
	
	public byte getFoodID()
	{
		return this.food_id;
	}

	public void setFoodID(int _food_id) 
	{
		if (_food_id == -1)
		{
			this.food_id = 0;
		}
		
		this.food_id = (byte)_food_id;
	}
	
	public int getDigestTime()
	{
		return this.digest_time;
	}

	public void setDigestTime(int _digest_time) 
	{
		this.digest_time = _digest_time;
	}
	
	public byte[] getData()
	{
		FBEncrypt slot = new FBEncrypt();

		slot.addByte(KeyID.KEY_OWL_FOOD_ID, this.food_id);
		slot.addInt(KeyID.KEY_OWL_DIGEST_TIME, this.digest_time);

		return slot.toByteArray();
	}
	
	public void displayDataPackage()
	{
		LogHelper.Log("food_id = " + food_id);
		LogHelper.Log("digest_time = " + digest_time);
	}
	
	public String LogOwlSlot()
	{
		StringBuilder log = new StringBuilder();
		
		log.append("slot");
		log.append('\t').append(this.food_id);
		log.append('\t').append(this.digest_time);
	
		return log.toString();
	}
}