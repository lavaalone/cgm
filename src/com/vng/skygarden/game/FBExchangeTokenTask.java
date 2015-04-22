/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.taskqueue.*;
import com.vng.skygarden.*;
import com.vng.log.LogHelper;
import com.vng.skygarden._gen_.ProjectConfig;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class FBExchangeTokenTask extends Task
{
	private long		_uid = -1;
	private Client		_client;
	private String		_short_lived_token;
	private String		_long_lived_token;
	
	public FBExchangeTokenTask(Client client, long uid, String token)
	{
		super();

		_client = client;
		_uid = uid;
		_short_lived_token = token;
		_long_lived_token = "";
	}
	
	@Override
	protected void HandleTask() 
	{
		try {
			Thread.sleep(1000);
		} catch (Exception ex) {
			LogHelper.LogException("HandleTask", ex);
		}

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("https://graph.facebook.com/oauth/access_token?");
			sb.append("grant_type=fb_exchange_token");
			sb.append("&client_id=").append(ProjectConfig.FACEBOOK_APP_CLIENT_ID);
			sb.append("&client_secret=").append(ProjectConfig.FACEBOOK_APP_CLIENT_SECRECT);
			sb.append("&fb_exchange_token=").append(_short_lived_token);

			URL url = new URL(sb.toString());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = reader.readLine();
			connection.disconnect();
			LogHelper.Log("FBExchangeTokenTask [" + _uid + "].. fb response: " + line);
			
			/*	Extract long lived token and save to user info.
				Example: access_token=CAAFVFVhoiC4BABrf7DuVQoPqLgZARl5XZBZCneZCiWNltdrDLmlw1yvKZCUOTsqPHsX7oVIB2JI1WITNRi2qE5EmHq17XZCUgJn16LFwZAwTpzCVDbfQmSJPPzh6uv5A9LAxOWpSsZAe61ZAvhrUYL06NAwZBRWagb0XHO51KfI3R1zcSo3ZAiChezu&expires=5150634
			*/
			String[] sa = line.split("&");
			_long_lived_token = sa[0].replace("access_token=", "");
		} catch (Exception e) {
			LogHelper.LogException("FBExchangeTokenTask", e);
		}
		
		boolean result = false;
		if (_long_lived_token.equals("") || _long_lived_token.length() == 0) {
			LogHelper.Log("FBExchangeTokenTask [" + _uid + "].. err! invalid long lived token");
		} else {
			result = _client.GetUserInstance().SetAndSaveFBLongLivedToken(_long_lived_token);
			LogHelper.Log("FBExchangeTokenTask [" + _uid + "].. save facebook long lived token ["+ _long_lived_token +"] to user info: " + result);
		}
		
		/* create response status info */
		FBEncrypt responseStatus = new FBEncrypt();
		responseStatus.addLong(KeyID.KEY_USER_ID, _uid);
		responseStatus.addStringANSI(KeyID.KEY_USER_SESSION_ID, _client.GetUserInstance()._session_id);
		responseStatus.addShort(KeyID.KEY_USER_COMMAND_ID, CommandID.CMD_PROVIDE_FB_SHORT_TOKEN);
		responseStatus.addLong(KeyID.KEY_USER_REQUEST_ID, _client.GetUserInstance()._request_id);
		responseStatus.addByte(KeyID.KEY_USER_REQUEST_STATUS, result ? ReturnCode.RESPONSE_OK : ReturnCode.RESPONSE_ERROR);

		/* reponse to client */
		FBEncrypt encoder = new FBEncrypt();
		encoder.addBinary(KeyID.KEY_REQUEST_STATUS, responseStatus.toByteArray());
		encoder.addBinary(KeyID.KEY_USER_INFOS, _client.GetUserInstance().GetUserData());
		try {
			_client.WriteZip(encoder.toByteArray());
			LogHelper.Log("FBExchangeTokenTask [" + _uid + "].. response to client OK.");
		} catch (Exception e) {
			LogHelper.LogException("FBExchangeTokenTask.Response", e);
		}
	}
}
