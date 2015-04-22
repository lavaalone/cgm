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
public class MailBox
{
	private long		_user_id = -1;
	private HashMap<Integer, Mail> _mail_list = new HashMap<Integer, Mail>();
	
	// default constructor
    MailBox() 
	{
	}
	
	MailBox(UserInfo user_info)
	{
		this._user_id = user_info.getID();
	}
	
	MailBox(long user_id)
	{
		this._user_id = user_id;
	}

	public boolean Load() 
	{
		_mail_list.clear();
		
		byte[] data = null;
		try
		{
			data = DBConnector.GetMembaseServer(_user_id).GetRaw(_user_id + "_" + KeyID.KEY_MAIL_BOX);
		}
		catch (Exception e)
		{
			LogHelper.LogException("MailBox.Load", e);
		}
		
		if (data != null && data.length > 0)
		{
			FBEncrypt fb = new FBEncrypt();
			fb.decode(data, true);
			
			int num_of_mail = fb.getInt(KeyID.KEY_MAIL_NUM);
			for (int i = 0; i < num_of_mail; i++)
			{
				byte[] mail_bin = fb.getBinary(KeyID.KEY_MAIL + "_" + i);
				if (mail_bin != null)
				{
					Mail mail = new Mail(mail_bin);
					_mail_list.put(_mail_list.size(), mail);
				}
			}
		}
		
		LogHelper.LogHappy("Mailbox: " + _mail_list.toString());
		
		return true;
	}
	
	public void Add(Mail mail)
	{
		_mail_list.put(_mail_list.size(), mail);
	}
	
	public void Remove(int mail_index)
	{
		_mail_list.remove(mail_index);
	}
	
	public void Save()
	{
		DBConnector.GetMembaseServer(_user_id).SetRaw(_user_id + "_" + KeyID.KEY_MAIL_BOX, GetData());
	}
	
	public byte[] GetData()
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_MAIL_NUM, _mail_list.size());
		int mail_index = 0;
		for (Entry<Integer, Mail> e : _mail_list.entrySet())
		{
			encrypt.addBinary(KeyID.KEY_MAIL + "_" + mail_index, e.getValue().GetData());
			mail_index++;
		}
		return encrypt.toByteArray();
	}
	
	public HashMap<Integer, Mail> GetMailList()
	{
		return _mail_list;
	}
}

class Mail 
{
	private int			_date = -1;
	private long		_sender = -1;
	private String		_title	= "";
	private String		_content = "";
	private String		_gift_list	= "";
	private boolean		_is_read = false;
	
	Mail()
	{
	}
	
	Mail(long sender, String title, String content)
	{
		_date = Misc.SECONDS();
		_is_read = false;
		_sender = sender;
		_title = title;
		_content = content;
	}
	
	Mail(long sender, String title, String content, String gift_list)
	{
		_date = Misc.SECONDS();
		_is_read = false;
		_sender = sender;
		_title = title;
		_content = content;
		_gift_list = gift_list;
	}

	Mail(byte[] data) 
	{
		FBEncrypt fb = new FBEncrypt();
		fb.decode(data, true);
		_date = fb.getInt(KeyID.KEY_MAIL_DATE);
		_sender = fb.getLong(KeyID.KEY_MAIL_SENDER);
		_title = fb.getString(KeyID.KEY_MAIL_TITLE);
		_content = fb.getString(KeyID.KEY_MAIL_CONTENT);
		_gift_list = fb.getString(KeyID.KEY_MAIL_GIFT_LIST);
		_is_read = fb.getBoolean(KeyID.KEY_MAIL_READ);
	}
	
	public byte[] GetData()
	{
		FBEncrypt encrypt = new FBEncrypt();
		encrypt.addInt(KeyID.KEY_MAIL_DATE, _date);
		encrypt.addLong(KeyID.KEY_MAIL_SENDER, _sender);
		encrypt.addString(KeyID.KEY_MAIL_TITLE, _title);
		encrypt.addString(KeyID.KEY_MAIL_CONTENT, _content);
		encrypt.addString(KeyID.KEY_MAIL_GIFT_LIST, _gift_list);
		encrypt.addBoolean(KeyID.KEY_MAIL_READ, _is_read);
		return encrypt.toByteArray();
	}
	
	public void SetRead(boolean b)
	{
		_is_read = b;
	}
	
		
	public String GetGiftList()
	{
		return _gift_list;
	}
	
	public String GetTitle()
	{
		return _title;
	}
	
	public String GetContent()
	{
		return _content;
	}
}

