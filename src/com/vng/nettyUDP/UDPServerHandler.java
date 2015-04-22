package com.vng.nettyUDP;


import java.util.Random;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.vng.util.*;
import com.vng.log.*;
import com.vng.netty.ServerHandler;
import com.vng.skygarden.game.*;
import java.nio.ByteBuffer;
import com.vng.skygarden.DBConnector;
import com.vng.netty.Server;
import com.vng.skygarden.SkyGarden;
import com.vng.skygarden._gen_.ProjectConfig;
import com.vng.skygarden.game.PrivateShopItem;

public class UDPServerHandler extends SimpleChannelUpstreamHandler
{
	int _command_id = -1;
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
//		LogHelper.Log("\n----------------------[UDP] Received message from client ---------------------------");
		
		ChannelBuffer reqbuf = ((ChannelBuffer)e.getMessage());
		
		FBEncrypt encrypt = new FBEncrypt();
		if (encrypt.decode(reqbuf.array(), true) == false)
		{
			LogHelper.Log("[udp] err! decode failed.");
			return;
		}
		
		_command_id = encrypt.getShort(KeyID.KEY_USER_COMMAND_ID);
//		LogHelper.Log("[udp] received command ID = " + _command_id);
		
		//check command id
		if (_command_id < CommandID.CMD_TEST || _command_id > CommandID.CMD_MAX_VALUE)
		{
			LogHelper.Log("[udp] err! invalid command id.");
			return;
		}

		ChannelBuffer resbuf = messageExecute(encrypt);
		
		if(resbuf != null)
		{
			e.getChannel().write(resbuf, e.getRemoteAddress());
			LogHelper.Log("[udp] response to sender OK.");
		}
		else
		{
//			LogHelper.Log("[udp] resbuf = null. No need to response.");
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception
	{
		LogHelper.Log("\n----------------------[UDP] exceptionCaught ---------------------------");		
	}
	
	public ChannelBuffer messageExecute(FBEncrypt encrypt)
	{
		switch (_command_id)
		{
			case CommandID.CMD_NOTIFY_SHOP_IS_MODIFIED:
				return handleNotifyShopChanged(encrypt);
				
			case CommandID.CMD_UDP_REQUEST_RELOAD_PSHOP:
				return handleRequestReloadShop(encrypt);
			
			case CommandID.CMD_REPAIR_MACHINE_FRIEND:
				return handleRequestRepairMachine_Friend(encrypt);
				
			case CommandID.CMD_UDP_REFRESH_DIAMOND:
				return handleRequestRefreshDiamond(encrypt);
				
			case CommandID.CMD_KICK_USER:
				return handleRequestKickUser(encrypt);
				
			case CommandID.CMD_UPDATE_RANKING_INFO:
				return handleUpdateRankingList(encrypt);
				
			case CommandID.CMD_ADS_ADD:
				return handleAddAdvertise(encrypt);
				
			case CommandID.CMD_ADS_REMOVE:
				return handleRemoveAdvertise(encrypt);
				
			default:
				return null;
		}
	}
	
	/*
	 * Somebody has bought an item in my private shop.
	 * I must told everybody who are viewing my private shop to reload my shop content.
	 * I also have to resend my shop content to my client
	 */
	private ChannelBuffer handleNotifyShopChanged(FBEncrypt encrypt)
	{
		String my_device_id = encrypt.getString(KeyID.KEY_DEVICE_ID);
		
		// read my key viewer to get the list of friend who's watching my shop
		if (ServerHandler.isUserOnline(my_device_id))
		{
			LogHelper.Log("[udp] handleNotifyShopChanged.. " + my_device_id + " is online.");
			
			SkyGardenUser me = ServerHandler.getUser(my_device_id);
			
			if (me == null) 
			{
				LogHelper.Log("[udp] handleNotifyShopChanged.. can not access ram of " + my_device_id);
				return null;
			}
			
			try
			{
				byte[] viewer_bin = null;
				
				DBConnector.GetMembaseServer(me.GetUserID()).GetRaw(me.GetUserID() + "_" + KeyID.KEY_PS_VIEWER);
				
				if (viewer_bin == null || viewer_bin.length == 0)
				{
					LogHelper.Log("[udp] handleNotifyShopChanged.. empty viewer list. Request is cancelled.");
					return null;
				}
				else
				{
					FBEncrypt viewer_list = new FBEncrypt();
					viewer_list.decode(viewer_bin, true);
					
					int i = 0;
					while (viewer_list.hasKey("viewer" + "_" + "did" + "_" + i))
					{
						String viewer_did = viewer_list.getString("viewer" + "_" + "did" + "_" + i);
						String viewer_ip = viewer_list.getString("viewer" + "_" + "ip" + "_" + i);
//						int viewer_port = viewer_list.getInt("viewer" + "_" + "port" + "_" + i);
						
						FBEncrypt udpreq = new FBEncrypt();
						udpreq.addShort(KeyID.KEY_USER_COMMAND_ID, CommandID.CMD_UDP_REQUEST_RELOAD_PSHOP);
						udpreq.addString(KeyID.KEY_DEVICE_ID, viewer_did);
						udpreq.addString(KeyID.KEY_FRIEND_DEVICE_ID, me.GetUserDeviceID());

						ByteBuffer buffer = ByteBuffer.allocate(udpreq.toByteArray().length + 8);
						buffer.put(udpreq.toByteArray());
						buffer.flip();
						
						SkyGardenUser.udpRequest(buffer, viewer_ip, ProjectConfig.UDP_PORT, false);
						LogHelper.Log("[udp] handleNotifyShopChanged.. sent notification to " + viewer_did + ":" + viewer_ip + ":" + ProjectConfig.UDP_PORT);
						
						i++;
					}
				}
			}
			catch (Exception e)
			{
				LogHelper.LogException("handleNotifyShopChanged", e);
				return null;
			}
			
			// resend my shop content to my client
			
			// create response status
			FBEncrypt responseStatus = new FBEncrypt();
			responseStatus.addShort(KeyID.KEY_USER_COMMAND_ID, CommandID.CMD_UPDATE_PRIVATE_SHOP);
			responseStatus.addLong(KeyID.KEY_USER_ID, me.GetUserID());
			responseStatus.addByte(KeyID.KEY_USER_REQUEST_STATUS, ReturnCode.RESPONSE_OK);
			responseStatus.addLong(KeyID.KEY_USER_REQUEST_ID, me.getRequestID());
			responseStatus.addStringANSI(KeyID.KEY_USER_SESSION_ID, me.getSessionID());
			
			FBEncrypt encoder = new FBEncrypt();
			encoder.addBinary(KeyID.KEY_REQUEST_STATUS, responseStatus.toByteArray());
			
			me.PShopMgr.loadFromDatabase(KeyID.KEY_PRIVATE_SHOP);
			encoder.addBinary(KeyID.KEY_PRIVATE_SHOP, me.PShopMgr.getDataToClient());

			try
			{
				me.getClient().WriteZip(encoder.toByteArray());
				LogHelper.Log("[udp] handleNotifyShopChanged.. response new private shop to " + my_device_id + " OK.");
			}
			catch (Exception ex)
			{
				LogHelper.LogException("handleNotifyShopChanged", ex);
			}
		}
		else
		{
			LogHelper.Log("[udp] handleNotifyShopChanged.. " + my_device_id + " is offline. Request is cancelled.");
		}
		
		return null;
	}
	
	/*
	 * My friend ask me to refresh his private shop content.
	 * OK, i should ask the game client to resend the command load private shop.
	 */
	private ChannelBuffer handleRequestReloadShop(FBEncrypt encrypt)
	{
		String my_device_id = encrypt.getString(KeyID.KEY_DEVICE_ID);
		String friend_device_id = encrypt.getString(KeyID.KEY_FRIEND_DEVICE_ID);
		if (ServerHandler.isUserOnline(my_device_id))
		{
			LogHelper.Log("[udp] handleRequestReloadShop.. " + my_device_id + " is online.");
			SkyGardenUser me = ServerHandler.getUser(my_device_id);
			
			// create response status
			FBEncrypt responseStatus = new FBEncrypt();
			responseStatus.addShort(KeyID.KEY_USER_COMMAND_ID, CommandID.CMD_REQUEST_RELOAD_PSHOP);
			responseStatus.addLong(KeyID.KEY_USER_ID, me.GetUserID());
			responseStatus.addByte(KeyID.KEY_USER_REQUEST_STATUS, ReturnCode.RESPONSE_OK);
			responseStatus.addLong(KeyID.KEY_USER_REQUEST_ID, me.getRequestID() + 1);
			responseStatus.addStringANSI(KeyID.KEY_USER_SESSION_ID, me.getSessionID());
			
			// reponse to client
			FBEncrypt encoder = new FBEncrypt();
			encoder.addString(KeyID.KEY_FRIEND_DEVICE_ID, friend_device_id);
			encoder.addBinary(KeyID.KEY_REQUEST_STATUS, responseStatus.toByteArray());

			try
			{
				me.getClient().WriteZip(encoder.toByteArray());
				LogHelper.Log("[udp] handleRequestReloadShop.. response to client OK.");
			}
			catch (Exception ex)
			{
			}
		}
		else
		{
			LogHelper.Log("[udp] handleRequestReloadShop.. " + my_device_id + " is offline. Request is cancelled.");
		}
		return null;
	}
	
	private ChannelBuffer handleRequestRepairMachine_Friend(FBEncrypt encrypt)
	{
		String _device_id = encrypt.getString(KeyID.KEY_FRIEND_DEVICE_ID);
		byte _floor = encrypt.getByte(KeyID.KEY_MACHINE_FLOOR);
		
		SkyGardenUser me = ServerHandler.getUser(_device_id);
		
		if (me != null)
		{
			me.refreshMachineToClient(_floor);
		}
		
		return null;
	}
	
	/*
	 * Client charge money via SMS
	 */
	private ChannelBuffer handleRequestRefreshDiamond(FBEncrypt encrypt)
	{
		long user_id = encrypt.getLong(KeyID.KEY_USER_ID);
		int transactionresult = encrypt.getInt(KeyID.KEY_PAYMENT_RESULT);
		
		String device_id = Misc.GetDeviceID(user_id);
		
		SkyGardenUser me = ServerHandler.getUser(device_id);
		
		if (me != null)
		{
			// refresh money from db
			me.GetMoneyManager().LoadFromDatabase(KeyID.KEY_MONEY_REAL, KeyID.KEY_MONEY_TOTAL, KeyID.KEY_MONEY_BONUS);
			
			// create response status
			FBEncrypt responseStatus = new FBEncrypt();
			responseStatus.addShort(KeyID.KEY_USER_COMMAND_ID, CommandID.CMD_REFILL_CARD);
			responseStatus.addLong(KeyID.KEY_USER_ID, me.GetUserID());
			responseStatus.addByte(KeyID.KEY_USER_REQUEST_STATUS, ReturnCode.RESPONSE_OK);
			responseStatus.addLong(KeyID.KEY_USER_REQUEST_ID, me.getRequestID() + 1);
			responseStatus.addStringANSI(KeyID.KEY_USER_SESSION_ID, me.getSessionID());
			
			// reponse to client
			FBEncrypt encoder = new FBEncrypt();
			encoder.addBinary(KeyID.KEY_REQUEST_STATUS, responseStatus.toByteArray());
			encoder.addInt(KeyID.KEY_PAYMENT_RESULT, transactionresult);
			encoder.addInt(KeyID.KEY_USER_DIAMOND, me.GetMoneyManager().GetRealMoney() + me.GetMoneyManager().GetBonusMoney());			
			if (transactionresult == 0 && me.GetGiftManager().LoadFromDatabase(KeyID.KEY_GIFT) && me.GetGiftManager().GetGiftSize() > 0)
			{
				encoder.addInt(KeyID.KEY_GIFT_ID, me.GetGiftManager().GetLastGiftBoxID());
				encoder.addString(KeyID.KEY_GIFT_NAME, me.GetGiftManager().GetLastGiftBoxName());
				encoder.addString(KeyID.KEY_GIFT_DESCRIPTION, me.GetGiftManager().GetLastGiftBoxDescription());
				encoder.addString(KeyID.KEY_GIFT_ITEM_LIST, me.GetGiftManager().GetLastGiftBoxItemLists());
			}
			
			//respone special offer
			byte[] offerbin = null;
			try
			{
				offerbin = DBConnector.GetMembaseServer(user_id).GetRaw(user_id + "_" + KeyID.KEY_SPECIAL_OFFER);
				if (offerbin != null && offerbin.length > 0) {
					Special_Offer _offer = new Special_Offer(offerbin);
					encoder.addBinary(KeyID.KEY_SPECIAL_OFFER, _offer.getData());
					LogHelper.Log("[udp] SpecialOffer respone OK.");
				}
				else
				{
					LogHelper.Log("[udp] SpecialOffer not respone.");
				}
			} catch (Exception e) {
				LogHelper.LogException("handleRequestRefreshDiamond.loadOfferBin", e);
			}

			try
			{
				me.getClient().WriteZip(encoder.toByteArray());
				LogHelper.Log("[udp] handleRequestRefreshDiamond.. response to client OK.");
			}
			catch (Exception ex)
			{
				LogHelper.LogException("handleRequestRefreshDiamond", ex);
			}
		}
		
		return null;
	}
	
	/*
	 * Kick online user
	 */
	private ChannelBuffer handleRequestKickUser(FBEncrypt encrypt)
	{
		String device_id = encrypt.getString(KeyID.KEY_DEVICE_ID);
		long user_id = encrypt.getLong(KeyID.KEY_USER_ID);
		
		LogHelper.Log("handleRequestKickUser.. kick device id " + device_id);
		
		if (ServerHandler.isUserOnline(device_id))
		{
			ServerHandler.removeUser(device_id, ReturnCode.RESPONSE_FORCE_QUIT);
			LogHelper.Log("handleRequestKickUser.. kick user done.");
		}
		// cause logic bug which allows multiple login.
//		else
//		{
//			DBConnector.GetMembaseServerForTemporaryData().Delete(device_id + "_" + KeyID.ONLINE);
//			DBConnector.GetMembaseServer(user_id).Delete(user_id + "_" + KeyID.ONLINE);
//			LogHelper.Log("handleRequestKickUser.. delete online key done.");
//		}
		
		return null;
	}
	
	/**
	 * Update ranking list
	 */
	
	private ChannelBuffer handleUpdateRankingList(FBEncrypt encrypt) {
		if (ProjectConfig.IS_SERVER_RANKING != 1 && ProjectConfig.IS_SERVER_RANKING_FREESTYLE != 1) { // dont handle if this is not ranking server
			return null;
		}
		
		int ranking_command = encrypt.getInt("ranking_command");
		long user_id = encrypt.getLong(KeyID.KEY_USER_ID);
		long value = encrypt.getLong("ranking_value");
		
		if (SkyGarden._rank_arrangement == null) {
			SkyGarden._rank_arrangement = new RankArrangement();
		}
		
		SkyGarden._rank_arrangement.Add(ranking_command, "" + user_id, value);
		return null;
	}
	
	/**
	 * Add advertise to list
	 */
	private ChannelBuffer handleAddAdvertise(FBEncrypt encrypt)
	{
		if (ProjectConfig.IS_SERVER_NEWSBOARD != 1 && ProjectConfig.IS_SERVER_NEWSBOARD_FREESTYLE != 1)
		{
			LogHelper.Log("handleAddAdvertise.. not server newsboard. Aborted.");
			return null;
		}
		String key = encrypt.getString(KeyID.KEY_ADS_KEY);
		byte[] b = encrypt.getBinary(KeyID.KEY_ADS_ITEM);
		PrivateShopItem item = new PrivateShopItem(b);
		
		if (SkyGarden._ads_manager == null)
		{
			SkyGarden._ads_manager = new AdvertiseManager();
		}
		
		try
		{
			SkyGarden._ads_manager.Add(key, item);
		} 
		catch (Exception e)
		{
			LogHelper.LogException("UDPServerHandler.handleAddAdvertise", e);
		}
		return null;
	}
	
	
	/**
	 * Removed advertise from list
	 */
	private ChannelBuffer handleRemoveAdvertise(FBEncrypt encrypt)
	{
		if (ProjectConfig.IS_SERVER_NEWSBOARD != 1 && ProjectConfig.IS_SERVER_NEWSBOARD_FREESTYLE != 1)
		{
			LogHelper.Log("handleAddAdvertise.. not server newsboard. Aborted.");
			return null;
		}
		String key = encrypt.getString(KeyID.KEY_ADS_KEY);
		if (SkyGarden._ads_manager == null)
		{
			SkyGarden._ads_manager = new AdvertiseManager();
		}
		
		try
		{
			SkyGarden._ads_manager.Remove(key);
		} 
		catch (Exception e)
		{
			LogHelper.LogException("UDPServerHandler.handleRemoveAdvertise", e);
		}
		return null;
	}
}
