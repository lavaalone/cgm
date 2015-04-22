/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;
import com.vng.util.*;
import com.vng.netty.*;
import com.vng.log.*;
import com.vng.skygarden.DBConnector;
import com.vng.skygarden._gen_.ProjectConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author thinhnn3
 */
public class OfferManager
{
	private OfferBug		_offer_bug;
	private OfferGem		_offer_gem;
	private OfferFloor		_offer_floor;
	private OfferMachine	_offer_machine;
	private OfferLuckyLeafGreen	_offer_lucky_leaf_green;
	private OfferLuckyLeafPurple	_offer_lucky_leaf_purple;
	private OfferGold		_offer_gold;
	
	// default constructor
    public OfferManager() 
	{
		_offer_bug = new OfferBug();
		_offer_gem = new OfferGem();
		_offer_floor = new OfferFloor();
		_offer_machine = new OfferMachine();
		_offer_lucky_leaf_green = new OfferLuckyLeafGreen();
		_offer_lucky_leaf_purple = new OfferLuckyLeafPurple();
		_offer_gold = new OfferGold();
	}
	
	public void Load(byte[] b)
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(b, true);
		_offer_bug.Load(encrypt.getBinary(KeyID.KEY_OFFER_BUG));
		_offer_gem.Load(encrypt.getBinary(KeyID.KEY_OFFER_GEM));
		_offer_floor.Load(encrypt.getBinary(KeyID.KEY_OFFER_FLOOR));
		_offer_machine.Load(encrypt.getBinary(KeyID.KEY_OFFER_MACHINE));
		_offer_lucky_leaf_green.Load(encrypt.getBinary(KeyID.KEY_OFFER_LUCKY_LEAF_GREEN));
		_offer_lucky_leaf_purple.Load(encrypt.getBinary(KeyID.KEY_OFFER_LUCKY_LEAF_PURPLE));
		_offer_gold.Load(encrypt.getBinary(KeyID.KEY_OFFER_GOLD));
	}
	
	public OfferBug getOfferBug()
	{
		return _offer_bug;
	}
	
	public OfferGem getOfferGem()
	{
		return _offer_gem;
	}
	
	public OfferFloor getOfferFloor()
	{
		return _offer_floor;
	}
	
	public OfferMachine getOfferMachine()
	{
		return _offer_machine;
	}
	
	public OfferLuckyLeafGreen getOfferLuckyLeafGreen()
	{
		return _offer_lucky_leaf_green;
	}
	
	public OfferLuckyLeafPurple getOfferLuckyLeafPurple()
	{
		return _offer_lucky_leaf_purple;
	}
	
	public OfferGold getOfferGold()
	{
		return _offer_gold;
	}
	
	public void setOfferActive(BasicOffer offer, boolean b)
	{
		offer.setActive(b);
	}
	
	public boolean isOfferActive(BasicOffer offer)
	{
		return offer.isActive();
	}
	
	public void setOfferContent(BasicOffer offer, String s)
	{
		offer.setContent(s);
	}
	
	public String getOfferContent(BasicOffer offer)
	{
		return offer.getContent();
	}
	
	public void updateOffer(BasicOffer offer)
	{
		offer.update();
	}
	
	public int getOfferID(BasicOffer offer)
	{
		return offer.getID();
	}
	
	public int getOfferDuration(BasicOffer offer)
	{
		return offer.getDurationTime();
	}
	
	public int getOfferMaxUseTime(BasicOffer offer)
	{
		return offer.getMaxUseTime();
	}
	
	public int getOfferUseTime(BasicOffer offer)
	{
		return offer.getUseTime();
	}
	
	public void setOfferUseTime(BasicOffer offer, int i)
	{
		offer.setUseTime(i);
	}
	
	public int getOfferReceiveTime(BasicOffer offer)
	{
		return offer.getReceiveTime();
	}
	
	public void setOfferReceiveTime(BasicOffer offer, int i)
	{
		offer.setReceiveTime(i);
	}
	
	public boolean isOfferUseable(BasicOffer offer)
	{
		return offer.isUseable();
	}
	
	public String getOfferPacksInfo(BasicOffer offer)
	{
		return offer.getPacksInfo();
	}
	
	public byte[] getOfferData(BasicOffer offer)
	{
		return offer.getData();
	}
	
	public byte[] getData()
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addBinary(KeyID.KEY_OFFER_BUG, _offer_bug.getData());
		encrypt.addBinary(KeyID.KEY_OFFER_GEM, _offer_gem.getData());
		encrypt.addBinary(KeyID.KEY_OFFER_FLOOR, _offer_floor.getData());
		encrypt.addBinary(KeyID.KEY_OFFER_MACHINE, _offer_machine.getData());
		encrypt.addBinary(KeyID.KEY_OFFER_LUCKY_LEAF_GREEN, _offer_lucky_leaf_green.getData());
		encrypt.addBinary(KeyID.KEY_OFFER_LUCKY_LEAF_PURPLE, _offer_lucky_leaf_purple.getData());
		encrypt.addBinary(KeyID.KEY_OFFER_GOLD, _offer_gold.getData());
		return encrypt.toByteArray();
	}
}

abstract class BasicOffer
{
	private boolean _active = false;
	private String _content = "";
	private int _offer_id = -1;
	private int _duration_time = -1;
	private int _max_use_time = -1;
	private String _url = "";
	private String _pack_info = "";
	private int _receive_time = -1;
	private int _use_time = -1;
	
	public BasicOffer()
	{
	}
	
	public void Load(byte[] b)
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(b, true);
		_offer_id = encrypt.getInt(KeyID.KEY_SPECIAL_OFFER_ID);
		_duration_time = encrypt.getInt(KeyID.KEY_SPECIAL_OFFER_DURATION);
		_max_use_time = encrypt.getInt(KeyID.KEY_SPECIAL_OFFER_AVAILABLE_TIME);
		_pack_info = encrypt.getString(KeyID.KEY_SPECIAL_OFFER_PACK_INFO);
		_receive_time = encrypt.getInt(KeyID.KEY_SPECIAL_OFFER_RECEIVE_TIME);
		_use_time = encrypt.getInt(KeyID.KEY_SPECIAL_OFFER_USE_TIME);
		_url = encrypt.getString(KeyID.KEY_SPECIAL_OFFER_WEB_LINK);
	}
	
	public byte[] getData()
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_SPECIAL_OFFER_ID, _offer_id);
		encrypt.addInt(KeyID.KEY_SPECIAL_OFFER_DURATION, _duration_time);
		encrypt.addInt(KeyID.KEY_SPECIAL_OFFER_AVAILABLE_TIME, _max_use_time);
		encrypt.addString(KeyID.KEY_SPECIAL_OFFER_PACK_INFO, _pack_info);
		encrypt.addString(KeyID.KEY_SPECIAL_OFFER_WEB_LINK, _url);
		
		encrypt.addInt(KeyID.KEY_SPECIAL_OFFER_RECEIVE_TIME, _receive_time);
		encrypt.addInt(KeyID.KEY_SPECIAL_OFFER_USE_TIME, _use_time);
		LogHelper.LogHappy("getData: " + ToString());
		return encrypt.toByteArray();
	}
	
	// 0 start;1 end;2 offer_id;3 duration;4 use_time; 5 url; 6 reservation_2; 7 reservation_3; 8 reservation_4; 9 package_id
	public void update()
	{
		String[] aos = _content.split(";");
		if (aos.length < 9)
		{
			LogHelper.Log("ERROR!! INVALID OFFER INFO := " + _content);
			return;
		}
		
		_offer_id = Integer.parseInt(aos[2]);
		_duration_time = Integer.parseInt(aos[3]);
		_max_use_time = Integer.parseInt(aos[4]);
		_url = aos[5];
		
		StringBuilder sb = new StringBuilder();
		for (int i = 9; i < aos.length; i++)
		{
			if (sb.length() == 0)
			{
				sb.append(aos[i]);
			}
			else
			{
				sb.append(';').append(aos[i]);
			}
		}
		
		_pack_info = sb.toString();
	}
	
	public boolean isUseable()
	{
		return (((Misc.SECONDS() - _receive_time) < _duration_time) && (_use_time < _max_use_time));
	}
	
	public boolean isActive()
	{
		return _active;
	}
	
	public void setActive(boolean b)
	{
		this._active = b;
	}
	
	public String getContent()
	{
		return _content;
	}
	
	public void setContent(String s)
	{
		this._content = s;
	}
	
	public int getID()
	{
		return this._offer_id;
	}
	
	public int getDurationTime()
	{
		return this._duration_time;
	}
	
	public int getMaxUseTime()
	{
		return this._max_use_time;
	}
	
	public int getUseTime()
	{
		return this._use_time;
	}
	
	public void setUseTime(int i)
	{
		this._use_time = i;
	}
	
	public int getReceiveTime()
	{
		return this._receive_time;
	}
	
	public void setReceiveTime(int i)
	{
		this._receive_time = i;
	}
	
	public String getPacksInfo()
	{
		return this._pack_info;
	}
	
	public String ToString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("_offer_id :=").append(_offer_id);
		sb.append(',').append("_duration_time :=").append(_duration_time);
		sb.append(',').append("_max_use_time :=").append(_max_use_time);
		sb.append(',').append("_pack_info :=").append(_pack_info);
		sb.append(',').append("_receive_time :=").append(_receive_time);
		sb.append(',').append("_use_time :=").append(_use_time);
		sb.append(',').append("_url :=").append(_url);
		return sb.toString();
	}
}

class OfferBug extends BasicOffer
{
}

class OfferGem extends BasicOffer
{
}

class OfferFloor extends BasicOffer
{
}

class OfferMachine extends BasicOffer
{
}

class OfferLuckyLeafGreen extends BasicOffer
{
}

class OfferLuckyLeafPurple extends BasicOffer
{
}

class OfferGold extends BasicOffer
{
}

