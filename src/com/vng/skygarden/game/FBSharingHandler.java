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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.sql.*;

public class FBSharingHandler
{
	Client _client = null;
	String _data = null;
	
	private long	post_uid = -1;
	private int		post_type = -1;
	private long	post_at = -1;
	private long	click_fbid = -1;
	
	public FBSharingHandler(Client client, String data)
	{
		_client = client;
		_data = data;
	}
	
	public void Execute()
	{
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		try
		{
			Object obj = parser.parse(_data);
			jsonObject = (JSONObject) obj;
		}
		catch (Exception e)
		{
			LogHelper.LogException("FBSharingHandler.parse_data", e);
		}
		
		// get params
		try
		{
			post_uid = Long.parseLong((String)jsonObject.get("post_uid"));
			post_type = Integer.parseInt((String)jsonObject.get("post_type"));
			post_at = Long.parseLong((String)jsonObject.get("post_at"));
			click_fbid = Long.parseLong((String)jsonObject.get("click_fbid"));
		}
		catch (Exception e)
		{
			LogHelper.LogException("FBSharingHandler.get_values", e);
		}
		
		//TODO: check post time
		
		//TODO: check post_type
		
		//TODO: from post_type, detertime gifts for poster & clicker
		
		// handle logic for poster
		
		// handle logic for clicker

	}
	
//	private boolean IsValidSig(String sig)
//	{
//		// TODO: ask for another secrect key, this one is not safe
//		return sig.equals(Misc.Hash(transactionid + type + userid + gross + (net == -1 ? "None" : net) + ProjectConfig.SECRECT_KEY, "MD5"));
//	}
	
//	private void UpdateClientMoney()
//	{
//		// check if user online and at which server
//		String online_info = "";
//		try
//		{
//			online_info = (String)DBConnector.GetMembaseServer(userid).Get(userid + "_" + KeyID.ONLINE);
//		}
//		catch (Exception e)
//		{
//			online_info = "";
//			LogHelper.LogException("UpdateClientMoney.GetOnlineInfo", e);
//		}
//		
//		if (online_info == null || online_info.equals(""))
//		{
//			LogHelper.Log("UpdateClientMoney.. user is offline.");
//		}
//		else
//		{
//			String user_ip = online_info.split(":")[0];
//			
//			FBEncrypt udpreq = new FBEncrypt();
//			udpreq.addShort(KeyID.KEY_USER_COMMAND_ID, CommandID.CMD_UDP_REFRESH_DIAMOND);
//			udpreq.addLong(KeyID.KEY_USER_ID, userid);
//			udpreq.addInt(KeyID.KEY_PAYMENT_RESULT, transactionresult);
//
//			ByteBuffer buffer = ByteBuffer.allocate(udpreq.toByteArray().length + 8);
//			buffer.put(udpreq.toByteArray());
//			buffer.flip();
//
//			udpRequest(buffer, user_ip, ProjectConfig.UDP_PORT, false);
//		}
//	}
	
	private void udpRequest(ByteBuffer buffer, String ip, int port, boolean receive)
	{
		try
		{
			LogHelper.Log("udpRequest " + buffer.remaining() + " to " +ip + ":" + port + " receive=" + receive);
			InetSocketAddress server = new InetSocketAddress(ip, port);
			DatagramChannel channel = DatagramChannel.open();
			channel.configureBlocking(false);
			channel.connect(server);
			if (channel.send(buffer, server) > 0 && receive)
			{
				buffer.clear();
				int count = 0;
				do
				{
					Thread.sleep(/*DELAY_RETRY_UDPRECEIVE*/100);
					channel.receive(buffer);
					count++;
					LogHelper.Log("[Retry] count=" + count + " buffer=" + buffer);
				}
				while (buffer.position() == 0 && count < /*MAX_RETRY_UDPRECEIVE*/3);

				LogHelper.Log("[after]" + buffer);
				buffer.flip();
				LogHelper.Log("[udpRequest]" + buffer);
				
				short i = buffer.getShort();
				LogHelper.Log("i = " + i);
				int j = buffer.getInt();
				LogHelper.Log("j = " + j);
			}
			channel.close();
		}
		catch(Exception e)
		{
			if(receive)
				buffer.flip();
		}
	}		
}
