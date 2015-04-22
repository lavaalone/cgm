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

public class FBNotificationTask extends Task
{
	private long		_uid = -1;
	private String		_from_fb_id;
	private String		_to_fb_id;
	
	public FBNotificationTask(long uid, String from_fb_id, String to_fb_id)
	{
		super();

		_uid = uid;
		_from_fb_id = from_fb_id;
		_to_fb_id = to_fb_id;
	}
	
	@Override
	protected void HandleTask() 
	{
		try
		{
			Thread.sleep(1000);
		}
		catch (Exception ex)
		{
			LogHelper.LogException("HandleTask", ex);
		}

		try
		{
			String template = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_NOTIFY_TEXT][6][DatabaseID.NOTIFY_TEXT_DESCRIPTION]).replace("dd", _from_fb_id);
			StringBuilder sb = new StringBuilder();
			sb.append("access_token=").append(ProjectConfig.FACEBOOK_APP_CLIENT_ID + "|" + ProjectConfig.FACEBOOK_APP_ACCESS_TOKEN);
			sb.append("&href=").append("");
			sb.append("&template=").append(URLEncoder.encode(template, "UTF-8"));

			LogHelper.Log("FBNotificationTask [" + _uid + "].. URL = " + sb.toString());

			URL url = new URL("https://graph.facebook.com/" + _to_fb_id + "/notifications?");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "multipart/form-data");
			connection.setRequestProperty("accept-charset", "UTF-8");
			connection.setRequestProperty("Content-Length", "" + sb.toString().length());
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(sb.toString());
			wr.flush();
			wr.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) 
			{
				LogHelper.Log("FBNotificationTask [" + _uid + "].. fb response: " + line);
			}
			connection.disconnect();
		}
		catch (Exception e)
		{
			LogHelper.LogException("FBNotificationTask", e);
		}
	}
}
