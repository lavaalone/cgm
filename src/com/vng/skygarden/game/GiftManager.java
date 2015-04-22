package com.vng.skygarden.game;
import com.vng.db.DBKeyValue;
import com.vng.log.*;
import com.vng.netty.*;
import com.vng.skygarden._gen_.ProjectConfig;
import com.vng.util.*;
import java.util.*;

public class GiftManager 
{
	private DBKeyValue _db = null;
	
	private String	_uid = "";
	
	public LinkedList<GiftBox> _gifts;
	
	final boolean LOG_GIFT = (ProjectConfig.IS_SERVER_FREESTYLE == 1);
	
	GiftManager(String uid)
	{
		_uid = uid;
		_gifts = new LinkedList<GiftBox>();
	}
	
	public void SetDatabase(DBKeyValue db)
	{
		_db = db;
	}
	
	public void SetUserId(String uid)
	{
		_uid = uid;
	}
	
	public boolean AddGiftBox(String name, String description, String item_list)
	{
		GiftBox gb = new GiftBox(_gifts.size());
		gb.SetName(name);
		gb.SetDescription(description);
		gb.SetItemList(item_list);
		
		return _gifts.add(gb);
	}
	
	public boolean LoadFromDatabase(String key)
	{
		_gifts.clear();

		try
		{
			byte[] bin = _db.GetRaw(_uid + "_" + key);

			if (bin == null || bin.length == 0)
			{
				LogHelper.LogHappy("GiftManager.. gift list is empty.");
				return true;
			}

			FBEncrypt fb = new FBEncrypt();
			fb.decode(bin, true);
			
			int i = 0;
			while (fb.hasKey(KeyID.KEY_GIFT_LIST + i))
			{
				String[] gift_info = fb.getString(KeyID.KEY_GIFT_LIST + i).split(";");
				
				if (gift_info.length != 4)
				{
					LogHelper.Log("GiftManager.. err! invalid gift info");
					return false;
				}
				
				GiftBox gb = new GiftBox(Integer.parseInt(gift_info[0]));
				gb.SetName(gift_info[1]);
				gb.SetDescription(gift_info[2]);
				gb.SetItemList(gift_info[3]);
				_gifts.add(gb);
				
				i++;
			}
			LogHelper.LogHappy("GiftManager.. load from database OK.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("GiftManager.loadFromDatabase()", e);
			
			return false;
		}
		
		if (LOG_GIFT)
		{
			Log();
		}
			
		return true;
	}
	
	public boolean SaveDataToDatabase(String key)
	{
		if (_gifts.size() == 0)
		{
//			LogHelper.Log("GiftManager.. gift list contains zero object");
			_db.Delete(_uid + "_" + key);
			return true;
		}
		
		FBEncrypt data = new FBEncrypt();
		for (GiftBox gb : _gifts)
		{
			data.addString(KeyID.KEY_GIFT_LIST + _gifts.indexOf(gb), gb.GetInfo());
		}
		
		if (LOG_GIFT)
		{
			Log();
		}
		
		return _db.SetRaw(_uid + "_" + key, data.toByteArray());
	}
	
	public void Log()
	{
		StringBuilder l = new StringBuilder();
		
		l.append(_uid);
		l.append('\t').append("gift");
		l.append('\t').append(_gifts.size());
		
		for (GiftBox gb : _gifts)
		{
			l.append('\t').append(gb.GetId());
			l.append('\t').append(gb.GetName());
			l.append('\t').append(gb.GetDescription());
			l.append('\t').append(gb.GetItemList());
		}
		
		LogHelper.Log(l.toString());
	}
	
	public int GetGiftSize()
	{
		return _gifts.size();
	}
	
	public int GetLastGiftBoxID()
	{
		return _gifts.getLast().GetId();
	}
	
	public String GetLastGiftBoxName()
	{
		return _gifts.getLast().GetName();
	}
	
	public String GetLastGiftBoxDescription()
	{
		return _gifts.getLast().GetDescription();
	}
	
	public String GetLastGiftBoxItemLists()
	{
		return _gifts.getLast().GetItemList();
	}
}

class GiftBox
{
	private int _id;
	String _name = "";
	String _description = "";
	
	String _item_list = "";
	
	GiftBox(int id)
	{
		_id = id;
	}
	
	public int GetId() 
	{
		return _id;
	}
	
	public void SetItemList(String s)
	{
		this._item_list = s;
	}
	
	public String GetItemList()
	{
		return _item_list;
	}
	
	public void SetName(String s)
	{
		this._name = s;
	}
	
	public String GetName()
	{
		return _name;
	}
	
	public void SetDescription(String s)
	{
		this._description = s;
	}
	
	public String GetDescription()
	{
		return _description;
	}
	
	public String GetInfo()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(_id);
		sb.append(';').append(_name);
		sb.append(';').append(_description);
		sb.append(';').append(_item_list);
		
		return sb.toString();
	}
}