package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.skygarden.DBConnector;
import com.vng.skygarden.SkyGarden;
import com.vng.skygarden._gen_.ProjectConfig;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import java.util.*;

public class RankingTarget {
	private long _uid;
	private UserInfo _user_info;
	private int _count;
	
	// running ranking
	private int _active_ranking = -1;
	private int _extra_ranking_command = -1;
	private int _active_ranking_idx = -1;
	private long _current_floor_value = -1;
	private long _start_time = -1;
	
	// previous ranking
	private int _previous_ranking_idx = -1;
	
	// type of ranking 
	//** no need to use atomic/volatile here, these data are not important
	private long _harvest_count = 0;
	private long _catch_bug_count = 0;
	private long _deliver_order_count = 0;
	private long _garden_appraisal = 0;
	private long _collect_product = 0;
	
	private String _gift_received = "";
	
	public RankingTarget(long uid) {
		_uid = uid;
		_harvest_count = 0;
		_gift_received = "";
		_user_info = null;
		
		LoadActiveRanking();
	}
	
	public RankingTarget(long uid, UserInfo user_info) {
		_uid = uid;
		_harvest_count = 0;
		_gift_received = "";
		_user_info = user_info;
		
		LoadActiveRanking();
	}
	
	public RankingTarget(byte[] b, UserInfo user_info) 
	{
		FBEncrypt ranking_info = new FBEncrypt(b);
		_uid = ranking_info.getLong(KeyID.KEY_USER_ID);
		_gift_received = ranking_info.getString(KeyID.KEY_RANKING_GIFT_RECEIVED);
		_previous_ranking_idx = ranking_info.getInt("previous_ranking_idx");
		
		if (ranking_info.hasKey(KeyID.KEY_RANKING_HARVEST)) 
		{
			_harvest_count = ranking_info.getLong(KeyID.KEY_RANKING_HARVEST);
		}
		
		if (ranking_info.hasKey("catch_bug")) 
		{
			_catch_bug_count = ranking_info.getLong("catch_bug");
		}
		
		if (ranking_info.hasKey("deliver_order_count")) 
		{
			_deliver_order_count = ranking_info.getLong("deliver_order_count");
		}
		
		if (ranking_info.hasKey("garden_appraisal")) 
		{
			_garden_appraisal = ranking_info.getLong("garden_appraisal");
		}
		
		if (ranking_info.hasKey("collect_product")) 
		{
			_collect_product = ranking_info.getLong("collect_product");
		}
		
		_user_info = user_info;
		
		LoadActiveRanking();
	}
	
	public RankingTarget(byte[] b)
	{
		FBEncrypt ranking_info = new FBEncrypt(b);
		_uid = ranking_info.getLong(KeyID.KEY_USER_ID);
		_gift_received = ranking_info.getString(KeyID.KEY_RANKING_GIFT_RECEIVED);
		_previous_ranking_idx = ranking_info.getInt("previous_ranking_idx");
		
		if (ranking_info.hasKey(KeyID.KEY_RANKING_HARVEST)) 
		{
			_harvest_count = ranking_info.getLong(KeyID.KEY_RANKING_HARVEST);
		}
		
		if (ranking_info.hasKey("catch_bug")) 
		{
			_catch_bug_count = ranking_info.getLong("catch_bug");
		}
		
		if (ranking_info.hasKey("deliver_order_count")) 
		{
			_deliver_order_count = ranking_info.getLong("deliver_order_count");
		}
		
		if (ranking_info.hasKey("garden_appraisal")) 
		{
			_garden_appraisal = ranking_info.getLong("garden_appraisal");
		}
		
		if (ranking_info.hasKey("collect_product")) 
		{
			_collect_product = ranking_info.getLong("collect_product");
		}
	}
	
	public byte[] GetData() {
		FBEncrypt ranking = new FBEncrypt();
		ranking.addLong(KeyID.KEY_USER_ID, _uid);
		ranking.addLong(KeyID.KEY_RANKING_HARVEST, _harvest_count);
		ranking.addLong("catch_bug", _catch_bug_count);
		ranking.addLong("deliver_order_count", _deliver_order_count);
		ranking.addLong("garden_appraisal", _garden_appraisal);
		ranking.addLong("collect_product", _collect_product);
		ranking.addString(KeyID.KEY_RANKING_GIFT_RECEIVED, _gift_received);
		ranking.addInt("previous_ranking_idx", _previous_ranking_idx);
		return ranking.toByteArray();
	}
	
	private boolean LoadActiveRanking() {
		_active_ranking = _active_ranking_idx = -1;
		_current_floor_value = _start_time = -1;
		RankingInfo r = null;
		if (SkyGarden._ranking_info != null && SkyGarden._ranking_info.containsKey("active")) 
		{
			r = SkyGarden._ranking_info.get("active");
		}
		
		if ( r != null) 
		{
			_active_ranking = r.GetRankingCommand();
			_extra_ranking_command = r.GetExtraCommand();
			_active_ranking_idx = r.GetID();
			_current_floor_value = r.GetCurrentFloorValue();
			_start_time = r.GetStartTime();
			
			boolean new_season = _previous_ranking_idx != _active_ranking_idx;
			if (new_season) 
			{
				// update the previous ranking idx to the new one
				_previous_ranking_idx = _active_ranking_idx;
				
				// reset all the accumulation value
				_harvest_count = 0;
				_catch_bug_count = 0;
				_deliver_order_count = 0;
				_garden_appraisal = 0;
				_collect_product = 0;
				if (_user_info != null)
				{
					_user_info.SetRankingAccumulation(0);
				}
			}
		}
		
		if (_active_ranking == -1 || _active_ranking_idx == -1) 
		{
			LogHelper.Log("RankingTarget.. err! no ranking is active!");
		}
		
		return (_active_ranking != -1 && _active_ranking_idx != -1);
	}
	
	public void Increase(int command_id, int extra_command_id, long v) {
		if (_active_ranking == -1) {
			return;
		}
		Set(command_id, extra_command_id, Get(command_id) + v);
	}
	
	public void Increase(int command_id, long v) {
		if (_active_ranking == -1) {
			return;
		}
		Increase(command_id, -1, v);
	}
	
	public void Set(int command_id, int extra_command_id, long v) {
		if (_active_ranking == -1 || _active_ranking_idx == -1 || Misc.SECONDS() < _start_time) {
			LogHelper.Log("RankingTarget.. no ranking is active, canceled set");
			return;
		}
		
		switch (command_id) {
			case CommandID.CMD_HARVEST:
				if (_extra_ranking_command == -1) {
					_harvest_count = v;
				} else {
					if (_extra_ranking_command == extra_command_id) {
						_harvest_count = v;
					}
				}
				_count++;
				break;
			case CommandID.CMD_CATCH_BUG:
				_catch_bug_count = v;
				_count++;
				break;
			case CommandID.CMD_DELIVERY_ORDER:
				_deliver_order_count = v;
				_count++;
				break;
			case CommandID.CMD_UPDATE_GARDEN_APPRAISAL:
				_garden_appraisal = v;
				_count++;
				break;
			case CommandID.CMD_MOVE_MACHINE_PRODUCT:
				if (_extra_ranking_command == -1) {
					_collect_product = v;
				} else {
					if (_extra_ranking_command == extra_command_id) {
						_collect_product = v;
					}
				}
				_count++;
				break;
			default:
				break;
		}
		
		//if (_count == 2) {
			//_count = 0;
			Flush();
		//}
		
		if (_user_info != null) {
			_user_info.SetRankingAccumulation(v);
		}
	}
	
	public long Get(int command_id) {
		switch (command_id) {
			case CommandID.CMD_HARVEST:
				return _harvest_count;
			case CommandID.CMD_CATCH_BUG:
				return _catch_bug_count;
			case CommandID.CMD_DELIVERY_ORDER:
				return _deliver_order_count;
			case CommandID.CMD_UPDATE_GARDEN_APPRAISAL:
				return _garden_appraisal;
			case CommandID.CMD_MOVE_MACHINE_PRODUCT:
				return _collect_product;
			default:
				break;
		}
		return -1;
	}
	
	public boolean IsReceivedGift(int ranking_idx) {
		if (ranking_idx == 6)
		{
			ranking_idx = 7;
		}
		if (_gift_received.length() == 0 || _gift_received.equals("")) {
			return false;
		}
//		LogHelper.Log("RankingTarget[" + _uid + "].. gift_received = " + _gift_received);
		String[] sa = _gift_received.split(";");
		for (String s : sa) {
			int idx = Integer.parseInt(s.split("_")[0]);
			if (idx == ranking_idx) {
				return Integer.parseInt(s.split("_")[1]) == 1;
			}
		}
		return false;
	}
	
	public void SetReceivedGift(int ranking_idx) {
		if (ranking_idx == 6)
		{
			ranking_idx = 7;
		}
		_gift_received = _gift_received + ranking_idx + "_" + 1 + ";";
//		LogHelper.Log("RankingTarget[" + _uid + "].. gift_received = " + _gift_received);
	}
	
	public void Flush() {
		long v = -1;
		switch (_active_ranking) {
			case CommandID.CMD_HARVEST:
				v = _harvest_count;
				break;
			case CommandID.CMD_CATCH_BUG:
				v = _catch_bug_count;
				break;
			case CommandID.CMD_DELIVERY_ORDER:
				v = _deliver_order_count;
				break;
			case CommandID.CMD_UPDATE_GARDEN_APPRAISAL:
				v = _garden_appraisal;
				break;
			case CommandID.CMD_MOVE_MACHINE_PRODUCT:
				v = _collect_product;
				break;
			default:
				break;
		}
		
		if ( v < _current_floor_value || v == 0) {
			// LogHelper.Log("RankingTarget[" + _uid + "].. saving value [" + v + "] is less than current floor [" + _current_floor_value + "] value. Cancel flush.");
			return;
		}
		
		// use udp to flush
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addShort(KeyID.KEY_USER_COMMAND_ID, CommandID.CMD_UPDATE_RANKING_INFO);
		encrypt.addInt("ranking_command", _active_ranking);
		encrypt.addLong(KeyID.KEY_USER_ID, _uid);
		encrypt.addLong("ranking_value", v);
		
		byte[] ba = encrypt.toByteArray();
		ByteBuffer buffer = ByteBuffer.allocate(ba.length);
		buffer.put(ba);
		buffer.flip();
		if (ProjectConfig.IS_SERVER_LOGIC == 1) // real ranking
		{ 
			udpRequest(buffer, ProjectConfig.RANKING_IP, ProjectConfig.UDP_PORT, false);
		} 
		else // happy
		{ 
			udpRequest(buffer, ProjectConfig.RANKING_IP, 8305, false);
		}
		
//		LogHelper.Log("RankingTarget[" + _uid + "].. Flush OK, value = " + v);
	}
	
	private void udpRequest(ByteBuffer buffer, String ip, int port, boolean receive)
	{
		try
		{
//			LogHelper.Log("udpRequest " + buffer.remaining() + " to " + ip + ":" + port + " receive=" + receive);
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
					// LogHelper.Log("[Retry] count=" + count + " buffer=" + buffer);
				}
				while (buffer.position() == 0 && count < /*MAX_RETRY_UDPRECEIVE*/3);

				// LogHelper.Log("[after]" + buffer);
				buffer.flip();
				// LogHelper.Log("[udpRequest]" + buffer);
				
				short i = buffer.getShort();
				// LogHelper.Log("i = " + i);
				int j = buffer.getInt();
				// LogHelper.Log("j = " + j);
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

