package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import java.util.*;

public class EventMainObject
{
	private int		emo_id;
	private int		emo_pos;
	private int		emo_hit_count;
	private int		emo_hit_current;
	private String	emo_first_action;
	private String	emo_last_action;
	private String	emo_hit_action;
	private String	emo_sprite_id;
	
	private int		emi_id;
	
	public EventMainObject(int _emo_id)
	{
		// emo_id = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_EVENT][event_id][DatabaseID.EVENT_EMO_ID]);
		
		emo_id = _emo_id;
		emo_pos = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_EVENT_MAIN_OBJECT][emo_id][DatabaseID.EMO_POS]);
		emo_hit_count = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_EVENT_MAIN_OBJECT][emo_id][DatabaseID.EMO_HIT_COUNT]);
		emo_hit_current = emo_hit_count;
		emo_first_action = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_MAIN_OBJECT][emo_id][DatabaseID.EMO_FIRST_ACTION]);
		emo_last_action = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_MAIN_OBJECT][emo_id][DatabaseID.EMO_LAST_ACTION]);
		emo_hit_action = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_MAIN_OBJECT][emo_id][DatabaseID.EMO_HIT_ACTION]);
		emo_sprite_id = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_MAIN_OBJECT][emo_id][DatabaseID.EMO_SPRITE_ID]);
		
		String s_emi_id = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_MAIN_OBJECT][emo_id][DatabaseID.EMO_EMI_ID]);
		emi_id = Integer.parseInt(s_emi_id);
	}
	
	public EventMainObject(byte[] emo_bin)
	{
		try
		{
			FBEncrypt event = new FBEncrypt(emo_bin);

			emo_id = event.getInt(KeyID.KEY_EVENT_MO_ID);
			emo_pos = event.getInt(KeyID.KEY_EVENT_MO_POS);
			emo_hit_count = event.getInt(KeyID.KEY_EVENT_MO_HIT_COUNT);
			emo_hit_current = event.getInt(KeyID.KEY_EVENT_MO_HIT_CURRENT);
			emo_first_action = event.getString(KeyID.KEY_EVENT_MO_FIRST_ACTION);
			emo_last_action = event.getString(KeyID.KEY_EVENT_MO_LAST_ACTION);
			emo_hit_action = event.getString(KeyID.KEY_EVENT_MO_HIT_ACTION);
			emo_sprite_id = event.getString(KeyID.KEY_EVENT_MO_SPRITE_ID);
			
			emi_id = event.getInt(KeyID.KEY_EVENT_MI_ID);
		}
		catch (Exception e)
		{
			LogHelper.LogException("load Event", e);
		}
	}
	
	public byte[] getData()
	{
		FBEncrypt event = new FBEncrypt();

		event.addInt(KeyID.KEY_EVENT_MO_ID, emo_id);
		event.addInt(KeyID.KEY_EVENT_MO_POS, emo_pos);
		event.addInt(KeyID.KEY_EVENT_MO_HIT_COUNT, emo_hit_count);
		event.addInt(KeyID.KEY_EVENT_MO_HIT_CURRENT, emo_hit_current);
		event.addString(KeyID.KEY_EVENT_MO_FIRST_ACTION, emo_first_action);
		event.addString(KeyID.KEY_EVENT_MO_LAST_ACTION, emo_last_action);
		event.addString(KeyID.KEY_EVENT_MO_HIT_ACTION, emo_hit_action);
		event.addString(KeyID.KEY_EVENT_MO_SPRITE_ID, emo_sprite_id);
		
		event.addInt(KeyID.KEY_EVENT_MI_ID, emi_id);
		
		return event.toByteArray();
	}
	
	public int getEMOHitCount()
	{
		return emo_hit_count;
	}

	public void setEMOHitCount(int new_hit_count)
	{
		emo_hit_count = new_hit_count;
	}
	
	public int getEMOHitCurrent()
	{
		return emo_hit_current;
	}

	public void setEMOHitCurrent(int new_emo_hit_current)
	{
		emo_hit_current = new_emo_hit_current;
	}

	public int getEMI_ID()
	{
		return emi_id;
	}

	public void resetEMOProperties()
	{
		emo_hit_current = emo_hit_count;
	}
	
	public void displayDataPackage()
	{
		LogHelper.Log("emo_id = " + emo_id);
		LogHelper.Log("emo_pos = " + emo_pos);
		LogHelper.Log("emo_hit_count = " + emo_hit_count);
		LogHelper.Log("emo_hit_current = " + emo_hit_current);
		LogHelper.Log("emo_first_action = " + emo_first_action);
		LogHelper.Log("emo_last_action = " + emo_last_action);
		LogHelper.Log("emo_hit_action = " + emo_hit_action);
		LogHelper.Log("emo_sprite_id = " + emo_sprite_id);
		LogHelper.Log("emi_id = " + emi_id);
	}

	public String LogProperties()
	{
		return "";
	}
}