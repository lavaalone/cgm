/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;

import com.google.common.base.Strings;
import com.vng.netty.*;
import com.vng.util.*;
import com.vng.taskqueue.*;
import com.vng.log.LogHelper;
import com.vng.skygarden.DBConnector;
import java.util.Map;

public class GeoIPTask extends Task {
	private Client		_client;
	private String		_ip;
	
	public GeoIPTask(Client client, String ip) {
		super();
		
		_client = client;
		_ip = ip;
	}
	
	@Override
	protected void HandleTask()  {
		if (Strings.isNullOrEmpty(_ip)) {
			return;
		}
		
		long ip = IpToLong(_ip);
		for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_GEO_IP].length; i++) {
			if (ip >= Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_GEO_IP][i][DatabaseID.GEO_IP_LONG_FROM]) &&
				ip <= Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_GEO_IP][i][DatabaseID.GEO_IP_LONG_TO]))
			{
				_client.GetUserInstance()._country = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GEO_IP][i][DatabaseID.GEO_IP_COUNTRY_CODE]);
				DBConnector.GetMembaseServer(_client.GetUserInstance()._user_id).Set(_client.GetUserInstance()._user_id + "_" + "country", _client.GetUserInstance()._country);
				break;
			}
		}
		
		LogHelper.Log("GeoIP, ip := " + _ip + ", country := " + _client.GetUserInstance()._country);
	}
	
	private long IpToLong(String ipAddress) {
		long result = 0;
		String[] ipAddressInArray = ipAddress.split("\\.");
		for (int i = 3; i >= 0; i--) {
			long ip = Long.parseLong(ipAddressInArray[3 - i]);

			//left shifting 24,16,8,0 and bitwise OR=
			//1. 192 << 24
			//1. 168 << 16
			//1. 1   << 8
			//1. 2   << 0
			result |= ip << (i * 8);
		}
		return result;
	}
}
