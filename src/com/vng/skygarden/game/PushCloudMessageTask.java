/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.taskqueue.*;
import com.vng.log.LogHelper;
import java.util.Map;
import com.vng.gcm.*;
import java.util.ArrayList;
import java.util.List;

public class PushCloudMessageTask extends Task
{
	private Client		_client;
	private FBEncrypt	_encrypt;
	
	private String content = "";
	private String reg_id_list = "";
	private int os = -1;
	
	private final String AUTHKEY_ANDROID = "AIzaSyC7AhbE2GvVAjy2KofgEcD6sAtV2g7xxCw";
	
	public PushCloudMessageTask(Client client, FBEncrypt encrypt)
	{
		super();
		
		_client = client;
		_encrypt = encrypt;
		LogHelper.Log("PushCloudMessageTask.. init ok!");
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
			LogHelper.LogException("PushCloudMessageTask.Sleep", ex);
		}
		
		content		= _encrypt.getString(KeyID.KEY_CLOUD_MESSAGE_CONTENT);
		reg_id_list = _encrypt.getString(KeyID.KEY_CLOUD_MESSAGE_REG_LIST);
		os			= _encrypt.getInt(KeyID.KEY_CLOUD_MESSAGE_OS);
		LogHelper.Log("PushCloudMessageTask.. content = " + content);
		LogHelper.Log("PushCloudMessageTask.. reg list = " + reg_id_list);
		LogHelper.Log("PushCloudMessageTask.. os = " + os);
		
		List<String> recipent_list = new ArrayList<String>();
		String[] aos = reg_id_list.split(";");
		for (String s : aos) {
			if (!s.equals("") && !s.equals("null") && !s.equals("ERROR")) {
				recipent_list.add(s);
			}
		}
		if (os == 0) {
			// android	
			String collapseKey = "kvtm";
            boolean delayWhileIdle = true;
            boolean dryRun = false;
            String restrictedPackageName = "vn.kvtm.khuvuontrenmay";
            int ttl = 108;
			int retry = 0;
			
			Message message = new Message.Builder()
                        .collapseKey(collapseKey)
                        .delayWhileIdle(delayWhileIdle)
                        .dryRun(dryRun)
                        .restrictedPackageName(restrictedPackageName)
                        .timeToLive(ttl)
                        .addData("content", content)
                        .build();
			
			try {
				Sender sender = new Sender(AUTHKEY_ANDROID);
				LogHelper.Log("SendPushNotificationAndroid.. start sending to list: " + recipent_list);
				MulticastResult result = sender.send(message, recipent_list, retry);
				LogHelper.Log("SendPushNotificationAndroid.. result: " + result.toString());
			} catch (Exception e) {
				LogHelper.LogException("SendPushNotificationAndroid", e);
			}
		} else if (os == 1) {
//			// ios
//			try {
//				Push.alert(content, "/home/sgmb/no_delete/Firebat.p12", "0000", false, recipent_list);
//			} catch (Exception e) {
//				LogHelper.LogException("SendPushNotificationIOS", e);
//			}
		}
		
	}
}
