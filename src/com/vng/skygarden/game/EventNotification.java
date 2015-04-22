/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vng.skygarden.game;

import com.vng.log.LogHelper;
import com.vng.skygarden.DBConnector;

/**
 *
 * @author thinhnn3
 */
public class EventNotification {
	public boolean	_enable = false;
	public boolean _enable_hd = false;
	public boolean _enable_sd = false;
	public boolean _enable_ios = false;
	
	public int		_type = 0;
	public String	_name = "";
	
	
	public String	_img_small = "";
	public String	_img_large = "";
	public String	_img_large_md5 = "";
	public String	_img_small_md5 = "";
	public String	_details = "";
	
	public String	_img_small_ios = "";
	public String	_img_large_ios = "";
	public String	_img_small_ios_md5 = "";
	public String	_img_large_ios_md5 = "";
	public String	_details_ios = "";
	
	public String	_img_small_sd = "";
	public String	_img_large_sd = "";
	public String	_img_small_sd_md5 = "";
	public String	_img_large_sd_md5 = "";
	public String	_details_sd = "";
	
	public EventNotification() {
//		try
//		{
//			_enable				= (boolean)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_enable");
//			_type				= (int)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_type");
//			_name				= (String)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_name");
//			_details			= (String)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_details");
//			
//			_img_small			= (String)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_img_small");
//			_img_large			= (String)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_img_large");
//			_img_small_md5		= (String)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_img_small_md5");
//			_img_large_md5		= (String)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_img_large_md5");
//			
//			_img_small_sd		= (String)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_img_small_sd");
//			_img_large_sd		= (String)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_img_large_sd");
//			_img_small_sd_md5	= (String)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_img_small_sd_md5");
//			_img_large_sd_md5	= (String)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_img_large_sd_md5");
//			
//			_img_small_ios		= (String)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_img_small_ios");
//			_img_large_ios		= (String)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_img_large_ios");
//			_img_small_ios_md5	= (String)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_img_small_ios_md5");
//			_img_large_ios_md5	= (String)DBConnector.GetMembaseServerForTemporaryData().Get("event_notify_img_large_ios_md5");
//			
//			LogHelper.Log("EventNotification.. load done");
//		}
//		catch (Exception e)
//		{
//			LogHelper.LogException("EventNotification", e);
//		}
	}
	
	public void Save() {
		try
		{
			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_enable", _enable);
			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_type", _type);
			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_name", _name);
			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_details", _details);

			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_img_small", _img_small);
			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_img_large", _img_large);
			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_img_small_md5", _img_small_md5);
			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_img_large_md5", _img_large_md5);

			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_img_small_sd", _img_small_sd);
			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_img_large_sd", _img_large_sd);
			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_img_small_sd_md5", _img_small_sd_md5);
			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_img_large_sd_md5", _img_large_sd_md5);

			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_img_small_ios", _img_small_ios);
			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_img_large_ios", _img_large_ios);
			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_img_small_ios_md5", _img_small_ios_md5);
			DBConnector.GetMembaseServerForTemporaryData().Set("event_notify_img_large_ios_md5", _img_large_ios_md5);
			
			LogHelper.Log("EventNotification.. save done.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("EventNotification.Save", e);
		}
	}
}
