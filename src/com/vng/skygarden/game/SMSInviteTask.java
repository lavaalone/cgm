/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;
import com.vng.log.LogHelper;
import com.vng.nettyhttp.*;
import com.vng.netty.Server;
import com.vng.skygarden.*;
import com.vng.skygarden._gen_.ProjectConfig;
import com.vng.util.FBEncrypt;
import com.vng.util.Misc;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.sql.*;

public class SMSInviteTask
{
	Client _client = null;
	String _phone = null;
	
	private static final String SMS_SERVER_TEST = "http://10.30.81.51:8080/";
	private static final String SMS_SERVER_REAL = "http://10.30.81.51:80/";
	private static final String SMS_SERVER_LOCAL = "http://127.0.0.1:2000/";
	private static final String SECRECT_KEY = "ffubJKOdxMiyxwh7";
	private static final String PRODUCT_NAME = "SGMB";
	private static final String MESSAGE = "KVTM Mobile gui ban link tai game, chuc ban choi game vui ve. http://kvtm.vn/download/0528636855345b61555/sgmb_0052.apk";
	
	public SMSInviteTask(Client client, String data)
	{
		_client = client;
		_phone = data;
	}
	
	public void Execute()
	{
		try
		{
			long uid = System.currentTimeMillis();
			
			// parse phone number
			LogHelper.Log("phone_number=" + _phone);
			
			// check phone number format
			if (!checkPhoneNumber(_phone))
			{
				return;
			}
			
			// check if this phone number is used
			int val = 0;
			try
			{
				val = (int)DBConnector.GetMembaseServerForTemporaryData().Get("phone" + "_" + _phone);
			}
			catch (Exception e)
			{
				LogHelper.LogException("SMSInviteTask.GetPhoneFromDB", e);
			}
			
			if (val == 1) // already used in the last 24h
			{
				return;
			}
			
			StringBuilder parameters = new StringBuilder();
			parameters.append("register?");
			parameters.append("product=").append(PRODUCT_NAME);
			parameters.append("&user=").append(uid);
			parameters.append("&phone=").append(_phone);
			parameters.append("&msg=").append(MESSAGE);
			parameters.append("&hash=").append(Misc.Hash(SECRECT_KEY + PRODUCT_NAME + uid + _phone + MESSAGE, "SHA-1"));
			LogHelper.Log("parameters: " + parameters.toString());

			java.net.URL url = new java.net.URL(SMS_SERVER_REAL + parameters.toString());
			java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			// receive response code
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = reader.readLine();
			connection.disconnect();
			
			if (line.contains("0")) // send success
			{
				// save logs
				StringBuilder log = new StringBuilder();
				log.append(Misc.getCurrentDateTime());
				log.append('\t').append(uid);
				log.append('\t').append(5); // 5 = phone number / SMSInviteTask
				log.append('\t').append(_phone);
				log.append('\t').append("");
				log.append('\t').append("");
				LogHelper.Log(LogHelper.LogType.SOCIAL, log.toString());
				
				// mark this phone as used in the next 24 hours
				DBConnector.GetMembaseServerForTemporaryData().Add("phone" + "_" + _phone, 0, 24 * 60 * 60);
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("SMSInviteTask.Execute", e);
		}
	}
	
	private boolean checkPhoneNumber(String phone)
    {
        if (phone.matches("[0-9]*"))
        {
            if (phone.startsWith("84"))
            {
                return (phone.length() == 11 && phone.startsWith("849"))
                    || (phone.length() == 12 && phone.startsWith("841"));
            }
        }       
        return false;
    }
}
