package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;
import java.util.*;

public class Event
{
	private int 	id;
	private String 	name;
	private int		start_time;
	private int		end_time;
	private int		start_time_before;
	private int		end_time_before;
	private int		emo_id;
	
	public Event()
	{
		try
		{
			int event_id = 0;
			
			id = event_id;
			name = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT][id][DatabaseID.EVENT_NAME]);
			start_time = Misc.SECONDS(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT][id][DatabaseID.EVENT_START]));
			end_time = Misc.SECONDS(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT][id][DatabaseID.EVENT_END]));
			start_time_before = Misc.SECONDS(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT][id][DatabaseID.EVENT_START_BEFORE]));
			end_time_before = Misc.SECONDS(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT][id][DatabaseID.EVENT_END_BEFORE]));
			
			String s_emo_id = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT][id][DatabaseID.EVENT_EMO_ID]);
			emo_id = Integer.parseInt(s_emo_id);
		}
		catch (Exception e)
		{
			LogHelper.LogException("Event", e);
		}
	}
	
	public Event(byte[] event_bin)
	{
		try
		{
			FBEncrypt event = new FBEncrypt(event_bin);
			
			id = event.getInt(KeyID.KEY_EVENT_ID);
			name = event.getString(KeyID.KEY_EVENT_NAME);
			start_time = event.getInt(KeyID.KEY_EVENT_START_TIME);
			end_time = event.getInt(KeyID.KEY_EVENT_END_TIME);
			start_time_before = event.getInt(KeyID.KEY_EVENT_START_TIME_BEFORE);
			end_time_before = event.getInt(KeyID.KEY_EVENT_END_TIME_BEFORE);
		}
		catch (Exception e)
		{
			LogHelper.LogException("load Event", e);
		}
	}
	
	public byte[] getData()
	{
		FBEncrypt event = new FBEncrypt();
		
		event.addInt(KeyID.KEY_EVENT_ID, id);
		event.addString(KeyID.KEY_EVENT_NAME, name);
		event.addInt(KeyID.KEY_EVENT_START_TIME, start_time);
		event.addInt(KeyID.KEY_EVENT_END_TIME, end_time);
		event.addInt(KeyID.KEY_EVENT_START_TIME_BEFORE, start_time_before);
		event.addInt(KeyID.KEY_EVENT_END_TIME_BEFORE, end_time_before);
		
		return event.toByteArray();
	}
	
	public int getEMO_ID()
	{
		return emo_id;
	}

	public void updateEventData()
	{
		try
		{
			name = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT][id][DatabaseID.EVENT_NAME]);
			start_time = Misc.SECONDS(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT][id][DatabaseID.EVENT_START]));
			end_time = Misc.SECONDS(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT][id][DatabaseID.EVENT_END]));
			start_time_before = Misc.SECONDS(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT][id][DatabaseID.EVENT_START_BEFORE]));
			end_time_before = Misc.SECONDS(Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT][id][DatabaseID.EVENT_END_BEFORE]));
			
			String s_emo_id = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT][id][DatabaseID.EVENT_EMO_ID]);
			emo_id = Integer.parseInt(s_emo_id);
		}
		catch (Exception e)
		{
			LogHelper.LogException("updateEventData", e);
		}
	}
	
	public void displayDataPackage()
	{
		LogHelper.Log("id = " + id);
		LogHelper.Log("name = " + name);
		LogHelper.Log("start_time = " + start_time);
		LogHelper.Log("end_time = " + end_time);
		LogHelper.Log("start_time_before = " + start_time_before);
		LogHelper.Log("end_time_before = " + end_time_before);
	}

	public String LogProperties()
	{
		return "";
	}	
}