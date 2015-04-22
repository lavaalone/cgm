package com.vng.skygarden.game;

import com.vng.util.FBEncrypt;
import java.util.*;

public class CloseFriend {
	private ArrayList<Long> _friend_list = new ArrayList<Long>();
	private long _close_friend_id;
	private boolean _allow_add_close_friend = true;
	
	public CloseFriend() {
		_close_friend_id = -1;
		_friend_list.clear();
		_allow_add_close_friend = true;
	}
	
	public CloseFriend(byte[] data) {
		_allow_add_close_friend = true;
		_close_friend_id = -1;
		_friend_list.clear();
		
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.decode(data);
		
		int max = encrypt.getInt(KeyID.KEY_MAX);
		for (int i = 0; i < max; i++) {
			if (encrypt.hasKey(KeyID.KEY_FRIEND_INDEX + i)) {
				_friend_list.add(encrypt.getLong(KeyID.KEY_FRIEND_INDEX + i));
			}
		}
		
		_close_friend_id = encrypt.getLong(KeyID.KEY_CLOSE_FRIEND);
		_allow_add_close_friend = encrypt.getBoolean(KeyID.KEY_ALLOW);
	}
	
	public void AddFriend(long id) {
		_friend_list.add(id);
	}
	
	public void SetCloseFriend(long id) {
		_close_friend_id = id;
	}
	
	public long GetCloseFriend() {
		return _close_friend_id;
	}
	
	public void SetAllowAddFriend(boolean b) {
		_allow_add_close_friend = b;
	}
	
	public boolean GetAllowAddFriend() {
		return _allow_add_close_friend;
	}
	
	public ArrayList<Long> FriendList() {
		return _friend_list;
	}
	
	public boolean IsCloseFriend(long id) {
		return (_close_friend_id == id || _friend_list.contains(id));
	}
	
	public byte[] GetData() {
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_MAX, _friend_list.size());
		for (int i = 0; i < _friend_list.size(); i++) {
			encrypt.addLong(KeyID.KEY_FRIEND_INDEX + i,_friend_list.get(i));
		}
		encrypt.addLong(KeyID.KEY_CLOSE_FRIEND, _close_friend_id);
		encrypt.addInt(KeyID.KEY_MAX_DEFAULT, 10);
		encrypt.addBoolean(KeyID.KEY_ALLOW, _allow_add_close_friend);
		return encrypt.toByteArray();
	}
	
	public String ToString() {
		StringBuilder sb = new StringBuilder();
		for (long l : _friend_list) {
			sb.append(l).append(";");
		}
		sb.append(_close_friend_id);
		sb.append(";").append(_allow_add_close_friend);
		
		return sb.toString();
	}
}