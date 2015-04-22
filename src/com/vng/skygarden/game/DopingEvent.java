/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;

import com.vng.taskqueue.*;
import com.vng.log.LogHelper;
import com.vng.netty.Server;
import com.vng.skygarden.DBConnector;
import com.vng.util.Misc;

import java.util.concurrent.atomic.AtomicLong;

public class DopingEvent extends Task
{
	public DopingEvent()
	{
		super();
	}
	
	@Override
	protected void HandleTask() 
	{
		String start = "13/04/2015 06:00:00";
		String end = "14/04/2015 20:00:00";
		if(Misc.InEvent(start, end)) {
			try {
				AtomicLong event_counter = new AtomicLong(0);
				String key = "birthday_2015" + "_" + 3;
				Object obj = DBConnector.GetMembaseServerForTemporaryData().Get(key);
				if (obj != null) {
					long v = (long)obj;
					event_counter.set(v);
				}
				
				LogHelper.Log("Current value of key birthday_2015_3 := " + event_counter.longValue());
				if (event_counter.longValue() < 27000000) {
					event_counter.addAndGet(1890);
					DBConnector.GetMembaseServerForTemporaryData().Set("birthday_2015_3", event_counter.longValue());
				}
			} catch (Exception e) {
				LogHelper.LogException("DopingEvent", e);
			}
		} else {
			LogHelper.Log("Out of event");
		}
	}
}
