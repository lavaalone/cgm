/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;
import com.vng.util.*;
import com.vng.netty.*;
import com.vng.log.*;
import com.vng.skygarden.DBConnector;
import com.vng.skygarden.SkyGarden;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author thinhnn3
 */
public class InviteFriend
{
	private boolean _is_received_gift_0 = false;
	private boolean _is_received_gift_1 = false;
	private boolean _is_received_gift_2 = false;
	private boolean _is_received_gift_3 = false;
	private int		_num_friend_invited	= 0;
	
	// default constructor
    InviteFriend() 
	{
	}
	
	InviteFriend(byte[] data) 
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(data, true);
		_is_received_gift_0 = encrypt.getBoolean(KeyID.KEY_RECEIVED_GIFT_INVITE_FRIEND + 0);
		_is_received_gift_1 = encrypt.getBoolean(KeyID.KEY_RECEIVED_GIFT_INVITE_FRIEND + 1);
		_is_received_gift_2 = encrypt.getBoolean(KeyID.KEY_RECEIVED_GIFT_INVITE_FRIEND + 2);
		_is_received_gift_3 = encrypt.getBoolean(KeyID.KEY_RECEIVED_GIFT_INVITE_FRIEND + 3);
		_num_friend_invited = encrypt.getInt(KeyID.KEY_NUM_FRIEND_INVITED);
	}
	
	public byte[] GetData() 
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addBoolean(KeyID.KEY_RECEIVED_GIFT_INVITE_FRIEND + 0, _is_received_gift_0);
		encrypt.addBoolean(KeyID.KEY_RECEIVED_GIFT_INVITE_FRIEND + 1, _is_received_gift_1);
		encrypt.addBoolean(KeyID.KEY_RECEIVED_GIFT_INVITE_FRIEND + 2, _is_received_gift_2);
		encrypt.addBoolean(KeyID.KEY_RECEIVED_GIFT_INVITE_FRIEND + 3, _is_received_gift_3);
		encrypt.addInt(KeyID.KEY_NUM_FRIEND_INVITED, _num_friend_invited);
		return encrypt.toByteArray();
	}
	
	public boolean IsReceivedGift(int gift_index)
	{
		switch (gift_index)
		{
			case 0: return _is_received_gift_0;
			case 1: return _is_received_gift_1;
			case 2: return _is_received_gift_2;
			case 3: return _is_received_gift_3;
			default: return true;
		}
	}
	
	public void SetReceiveGift(int gift_index, boolean b)
	{
		switch (gift_index)
		{
			case 0: this._is_received_gift_0 = b; break;
			case 1: this._is_received_gift_1 = b; break;
			case 2: this._is_received_gift_2 = b; break;
			case 3: this._is_received_gift_3 = b; break;
		}
	}
	
	public int GetNumFriendInvited()
	{
		return this._num_friend_invited;
	}
	
	public void SetNumFriendInvited(int v)
	{
		this._num_friend_invited = v;
	}
	
	public String GetGiftInviteFriend(int gift_index)
	{
		return Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_INVITE_FRIEND][gift_index][DatabaseID.INVITE_FRIEND_GIFT]);
	}
}