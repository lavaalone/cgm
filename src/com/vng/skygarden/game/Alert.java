package com.vng.skygarden.game;

import java.util.HashMap;
import com.vng.util.*;
import java.util.Map.Entry;
import com.vng.log.*;

public class Alert
{
	//for machine_alert	
	private HashMap<Long, Boolean> machine_alert;
	
	//funtion 
	public Alert()
	{
		machine_alert = new HashMap<Long, Boolean>();
	}
	public byte[] getData()
	{
		FBEncrypt alert = getAlertMachine();		
		return alert.toByteArray();
	}
	
	public void updateAlertMachine(Long userID, boolean showalert)
	{
		if(!showalert)
		{
			machine_alert.remove(userID);
		}
		else
		{
			machine_alert.put(userID,showalert);
		}
	}
	
	public FBEncrypt getAlertMachine()
	{
		FBEncrypt alert = new FBEncrypt();
		alert.addShort(KeyID.KEY_NUM_FRIEND_ALERT, machine_alert.size());
		int idx = 0;
		for(Entry<Long, Boolean> entry : machine_alert.entrySet())
		{
			Long key = entry.getKey();
			boolean value = entry.getValue();
			alert.addLong(KeyID.KEY_USER_ID+"_"+idx, key);
			alert.addBoolean(KeyID.KEY_SHOW_MACHINE_ALERT+"_"+idx, value);
			idx++;
		}
		return alert;
	}
}