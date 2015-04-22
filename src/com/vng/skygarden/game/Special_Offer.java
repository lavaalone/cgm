package com.vng.skygarden.game;

import com.vng.skygarden.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.skygarden._gen_.ProjectConfig;
import java.util.*;
public class Special_Offer
{

	//special offer
	private int special_offer_id = 0;
	private int special_offer_lasttime = 0;
	private int special_offer_lastduration = 0;
	private String special_offer_offerring = null;
	private boolean special_offer_accepted = false;
	private int special_offer_rejected = 0;
	private int special_offer_type = 0;
	private int special_offer_remaining_time = 0;
	private short special_offer_user_group = -1;
	private String special_offer_description = null;
	private String special_offer_name = null;
	private String special_offer_link_img = null;
	private String special_offer_md5_img = null;
	private String special_offer_button_label = null;
	
	private String _web_link = "";
	private int _available_time = 0;

	boolean change = false;
	
	public Special_Offer()
	{
		special_offer_id 		= 0;
		special_offer_lasttime 	= 0;
		special_offer_lastduration = 0;
		special_offer_offerring = "";
		special_offer_type = 0;
		special_offer_remaining_time = 0;
		special_offer_user_group = -1;
		special_offer_description = "";
		special_offer_name = "";
		special_offer_link_img = "";
		special_offer_md5_img = "";
		special_offer_button_label = "";
		_web_link = "";
		_available_time = 0;
	}
	
	public Special_Offer(byte[] bin_db)
	{
		FBEncrypt offer = new FBEncrypt();
		offer.decode(bin_db, true);
		
		special_offer_id 					= offer.getInt(KeyID.KEY_SPECIAL_OFFER_ID);
		special_offer_lasttime 				= offer.getInt(KeyID.KEY_SPECIAL_OFFER_LASTTIME);
		special_offer_lastduration 			= offer.getInt(KeyID.KEY_SPECIAL_OFFER_DURATION);
		special_offer_offerring 			= offer.getString(KeyID.KEY_SPECIAL_OFFER_OFFERRING);
		special_offer_accepted 				= offer.getBoolean(KeyID.KEY_SPECIAL_OFFER_ACCEPTED);
		special_offer_rejected 				= offer.getInt(KeyID.KEY_SPECIAL_OFFER_REJECTED);
		special_offer_type 					= offer.getInt(KeyID.KEY_SPECIAL_OFFER_TYPE);
		special_offer_remaining_time		= offer.getInt(KeyID.KEY_SPECIAL_OFFER_REMAINING_TIME);
		special_offer_user_group			= offer.getShort(KeyID.KEY_SPECIAL_OFFER_USER_GROUP);
		special_offer_description			= offer.getString(KeyID.KEY_SPECIAL_OFFER_DESCRIPTION);
		special_offer_name					= offer.getString(KeyID.KEY_SPECIAL_OFFER_NAME);
		special_offer_link_img				= offer.getString(KeyID.KEY_SPECIAL_OFFER_LINK_IMG);
		special_offer_md5_img				= offer.getString(KeyID.KEY_SPECIAL_OFFER_MD5_IMG);
		special_offer_button_label			= offer.getString(KeyID.KEY_SPECIAL_OFFER_BUTTON_LABEL);
		
		if (offer.hasKey(KeyID.KEY_SPECIAL_OFFER_WEB_LINK))
		{
			_web_link = offer.getString(KeyID.KEY_SPECIAL_OFFER_WEB_LINK);
		}
		
		if (offer.hasKey(KeyID.KEY_SPECIAL_OFFER_AVAILABLE_TIME))
		{
			_available_time = offer.getInt(KeyID.KEY_SPECIAL_OFFER_AVAILABLE_TIME);
		}
		
		change = false;
	}
	
	public boolean isOfferring()
	{
		int duration = Misc.SECONDS() - special_offer_lasttime;
		if(duration <=  special_offer_lastduration && !special_offer_accepted)
			return true;
		return false;
	}
	
	public void setOfferID(int id)
	{
		special_offer_id 		= id;
		change = true;
	}
	
	public void setOfferDescription(String des)
	{
		special_offer_description = des;
		change = true;
	}
	
	public void setOfferName(String name)
	{
		special_offer_name = name;
		change = true;
	}
	
	public void setOfferLink(String link)
	{
		special_offer_link_img = link;
		change = true;
	}
	
	public void setOfferMd5(String md5)
	{
		special_offer_md5_img = md5;
		change = true;
	}
	
	public void setOfferButtonLabel(String label)
	{
		special_offer_button_label = label;
		change = true;
	}
	
	public void setOfferWeblink(String link)
	{
		_web_link = link;
		change = true;
	}
	
	public void setAvailableTime(int v)
	{
		_available_time = v;
		change = true;
		LogHelper.LogHappy("Available time := " + _available_time);
	}
	
	public int getAvailableTime()
	{
		return _available_time;
	}
	
	public void setOfferRemainingTime(int remain)
	{
		special_offer_remaining_time 		= remain;
		change = true;
	}
	
	public void updateRemainingTime()
	{
		int remaining_time = 0;
//		LogHelper.Log("updateRemainingTime		special_offer_accepted "+special_offer_accepted);
		if(!special_offer_accepted)
		{
			int past = (Misc.SECONDS() - special_offer_lasttime);		
			remaining_time = special_offer_lastduration - past;//remaining time
//			LogHelper.Log("updateRemainingTime		past "+past);
		}
//		LogHelper.Log("updateRemainingTime		remaining_time "+remaining_time);
		setOfferRemainingTime(remaining_time);
		change = true;
	}
	
	
	public void setOfferType(int type)
	{
		special_offer_type 		= type;
		change = true;
	}
	
	public void setOfferDuration(int durex)
	{
		special_offer_lastduration 		= durex;
		change = true;
	}
	
	public void setOfferLastTime(int duration)
	{
		special_offer_lasttime 	= duration;
		change = true;
	}
	
	public void setOfferContent(String offerring)
	{
		special_offer_offerring	= offerring;
		change = true;		
	}
	
	public void setOfferAccept(boolean isAccept, int cmdID ,UserInfo userInfo)
	{
		special_offer_accepted = isAccept;
		if(isAccept)
		{
			setOfferRemainingTime(0);
			
			LogHelper.Log("Accept Special Offer ID		"+special_offer_id);
			LogHelper.Log("Accept Special Offer TyPE	"+special_offer_type);
			LogHelper.Log("Accept Special Offer content	"+special_offer_offerring);
		}
		
		change = true;
		StringBuilder log = new StringBuilder();
		log.append(Misc.getCurrentDateTime());							//  1. log time
		log.append('\t').append(cmdID);									//  2. action name
		log.append('\t').append(userInfo.getID());								//  3. account name
		log.append('\t').append(userInfo.getID());								//  4. role id
		log.append('\t').append(userInfo.getName());					//  5. role name
		log.append('\t').append("1");									//  6. server id
		log.append('\t').append(userInfo.getLevel());					//  7. user level
		log.append('\t').append(userInfo.GetUserIP());						//  8. user ip
		log.append('\t').append(special_offer_id);						//  9. user group get offer
		log.append('\t').append(special_offer_type);					//  10. offer type
		log.append('\t').append(special_offer_offerring);				//  11. offer content
		log.append('\t').append(special_offer_name);	//  12. offer name.
		log.append('\t').append(special_offer_rejected);				// 	13. num offer was rejected
		// log.append('\t').append(isAccept);								// 	14. new set new offer = false; otherwise = true.
	
	
		if (isAccept)
			LogHelper.Log(LogHelper.LogType.OFFER_ACCEPT, log.toString());
		else
			LogHelper.Log(LogHelper.LogType.OFFER_NEW, log.toString());
	}
	
	public void setOfferReject()
	{
		special_offer_rejected++;
		change = true;
	}
	
	public void setChange( boolean change)
	{		
		change = change;
	}
	
	public void setOfferUserGroup(short group)
	{
		special_offer_user_group = group;
		change = true;
	}
	
	public byte[] getData()
	{
		FBEncrypt offer = new FBEncrypt();
		updateRemainingTime();
		offer.addInt(KeyID.KEY_SPECIAL_OFFER_ID, special_offer_id);
		offer.addInt(KeyID.KEY_SPECIAL_OFFER_LASTTIME, special_offer_lasttime);
		offer.addInt(KeyID.KEY_SPECIAL_OFFER_DURATION, special_offer_lastduration);
		offer.addString(KeyID.KEY_SPECIAL_OFFER_OFFERRING, special_offer_offerring);
		offer.addBoolean(KeyID.KEY_SPECIAL_OFFER_ACCEPTED, special_offer_accepted);
		offer.addInt(KeyID.KEY_SPECIAL_OFFER_REJECTED, special_offer_rejected);
		offer.addInt(KeyID.KEY_SPECIAL_OFFER_TYPE, special_offer_type);	
		offer.addInt(KeyID.KEY_SPECIAL_OFFER_REMAINING_TIME, special_offer_remaining_time);	
		offer.addShort(KeyID.KEY_SPECIAL_OFFER_USER_GROUP, special_offer_user_group);
		offer.addString(KeyID.KEY_SPECIAL_OFFER_DESCRIPTION, special_offer_description);
		offer.addString(KeyID.KEY_SPECIAL_OFFER_NAME, special_offer_name);
		offer.addStringANSI(KeyID.KEY_SPECIAL_OFFER_LINK_IMG, special_offer_link_img);
		offer.addStringANSI(KeyID.KEY_SPECIAL_OFFER_MD5_IMG, special_offer_md5_img);
		offer.addStringANSI(KeyID.KEY_SPECIAL_OFFER_BUTTON_LABEL, special_offer_button_label);
		offer.addStringANSI(KeyID.KEY_SPECIAL_OFFER_WEB_LINK, _web_link);
		offer.addInt(KeyID.KEY_SPECIAL_OFFER_AVAILABLE_TIME, _available_time);

		return offer.toByteArray();
	}
	
	public byte[] getDataToClient()
	{
		FBEncrypt offer = new FBEncrypt();
		updateRemainingTime();
		offer.addInt(KeyID.KEY_SPECIAL_OFFER_ID, special_offer_id);
		offer.addInt(KeyID.KEY_SPECIAL_OFFER_LASTTIME, special_offer_lasttime);
		offer.addInt(KeyID.KEY_SPECIAL_OFFER_DURATION, special_offer_lastduration);
		
		StringBuilder sb = new StringBuilder();
		String[] aos = special_offer_offerring.split(":");
		int count = 0;
		for (String s : aos) {
			if (sb.length() == 0) {
				sb.append(s);
			} else {
				sb.append(":").append(s);
			}
			count++;
			if (count >= 118)
				break;
		}
		LogHelper.LogHappy("sb length := " + sb.length());
		LogHelper.LogHappy("sb string := " + sb.toString());
		
		offer.addString(KeyID.KEY_SPECIAL_OFFER_OFFERRING, sb.toString());
		offer.addBoolean(KeyID.KEY_SPECIAL_OFFER_ACCEPTED, special_offer_accepted);
		offer.addInt(KeyID.KEY_SPECIAL_OFFER_REJECTED, special_offer_rejected);
		offer.addInt(KeyID.KEY_SPECIAL_OFFER_TYPE, special_offer_type);	
		offer.addInt(KeyID.KEY_SPECIAL_OFFER_REMAINING_TIME, special_offer_remaining_time);	
		offer.addShort(KeyID.KEY_SPECIAL_OFFER_USER_GROUP, special_offer_user_group);
		offer.addString(KeyID.KEY_SPECIAL_OFFER_DESCRIPTION, special_offer_description);
		offer.addString(KeyID.KEY_SPECIAL_OFFER_NAME, special_offer_name);
		offer.addStringANSI(KeyID.KEY_SPECIAL_OFFER_LINK_IMG, special_offer_link_img);
		offer.addStringANSI(KeyID.KEY_SPECIAL_OFFER_MD5_IMG, special_offer_md5_img);
		offer.addStringANSI(KeyID.KEY_SPECIAL_OFFER_BUTTON_LABEL, special_offer_button_label);
		offer.addStringANSI(KeyID.KEY_SPECIAL_OFFER_WEB_LINK, _web_link);
		offer.addInt(KeyID.KEY_SPECIAL_OFFER_AVAILABLE_TIME, _available_time);

		return offer.toByteArray();
	}
	
	public boolean inOfferTime()
	{
		if (ProjectConfig.IS_SERVER_FREESTYLE == 1)
			return true;
		
		Calendar rightNow = Calendar.getInstance();
		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		if(hour>=6 && hour <= 24)
			return true;
		else
			return false;
	}
	
	public int getOfferID()
	{
		return special_offer_id;
	}
	
	public String getOfferDescription()
	{
		return special_offer_description;
	}
	
	public String getOfferName()
	{
		return special_offer_name;
	}
	
	public short getOfferUserGroup()
	{
		return special_offer_user_group;
	}
	
	public int getOfferDuration()
	{
		return special_offer_lastduration;
	}
	
	public int getOfferType()
	{
		return special_offer_type;
	}
	
	public boolean isChange()
	{
		return change;
	}
	
	public boolean getOfferAccept()
	{
		return special_offer_accepted;		
	}
	
	public String getOfferLinkMD5()
	{
		return special_offer_md5_img;		
	}
	
	public String getOfferButtonLabel()
	{
		return special_offer_button_label;		
	}
	
	public String getOfferLink()
	{
		return special_offer_link_img;		
	}
	
	public int getOfferLastTime()
	{
		return special_offer_lasttime;		
	}
	
	public String getOfferContent()
	{
		return special_offer_offerring;
	}
	
	public int getOfferRemainingTime()
	{
		return special_offer_remaining_time;		
	}
	
}