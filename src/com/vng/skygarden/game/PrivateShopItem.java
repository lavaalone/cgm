
package com.vng.skygarden.game;
import com.vng.log.LogHelper;
import com.vng.util.FBEncrypt;
import com.vng.util.Misc;
import java.util.concurrent.atomic.*;

public class PrivateShopItem
{
	private int type;

	private int id;
	private int number;
	
	private int money_type;
	private long price;
	private int cancel_price; // diamond cost to cancel this item from selling
	
	private boolean hasAdvertise;
	private int end_advertise_time;
	
	private int start_date;
	private int end_date;
	
	private AtomicInteger status = new AtomicInteger(DatabaseID.PS_ITEM_STATUS_EMPTY);
	
	private String buyer_id;
	
	public PrivateShopItem()
	{
		type = -1;
		id = -1;
		number = -1;
		
		hasAdvertise = false;
		end_advertise_time = -1;
		
		money_type = -1;
		price = -1;
		cancel_price = -1;
		
		start_date = -1;
		end_date = -1;
		
		status = new AtomicInteger(DatabaseID.PS_ITEM_STATUS_EMPTY);
		
		buyer_id = "-1";
	}
	
	public int getType() 
	{
		return type;
	}

	public void setType(int type) 
	{
		this.type = type;
	}

	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}

	public int getNumber() 
	{
		return number;
	}

	public void setNumber(int number) 
	{
		this.number = number;
	}

	public int getMoneyType() {
		return money_type;
	}

	public void setMoneyType(int money_type) 
	{
		this.money_type = money_type;
	}

	public long getPrice() 
	{
		return price;
	}

	public void setPrice(long price) 
	{
		this.price = price;
	}
	
	public void setCancelPrice(int price)
	{
		this.cancel_price = price;
	}
	
	public int getCancelPrice()
	{
		return this.cancel_price;
	}
	
	public void setAdvertise(boolean status)
	{
		this.hasAdvertise = status;
	}
	
	public boolean getAdvertiseStatus()
	{
		if (Misc.SECONDS() > end_advertise_time)
		{
			return false;
		}
		
		return this.hasAdvertise;
	}
	
	public void setAdvertiseEndTime(int _time)
	{
		this.end_advertise_time = _time;
	}
	
	public int getAdvertiseEndTime()
	{
		return this.end_advertise_time;
	}

	public int getStartDate() 
	{
		return start_date;
	}

	public void setStartDate(int start_date) 
	{
		this.start_date = start_date;
	}

	public int getEndDate() {
		return end_date;
	}

	public void setEndDate(int end_date) 
	{
		this.end_date = end_date;
	}

	public int getStatus() 
	{
		return status.get();
	}

	public boolean setStatus(int _status) 
	{
		return status.compareAndSet(status.get(), _status);
	}
	
	public void setBuyerID(String _buyer_id)
	{
		this.buyer_id = _buyer_id;
	}
	
	public String getBuyerID()
	{
		return this.buyer_id;
	}
	
	public byte[] getData()
	{
		FBEncrypt data = new FBEncrypt();
		data.addShort(KeyID.KEY_PS_ITEM_TYPE, (short)getType());
		data.addShort(KeyID.KEY_PS_ITEM_ID, (short)getId());
		data.addShort(KeyID.KEY_PS_ITEM_NUMBER, (short)getNumber());
		data.addShort(KeyID.KEY_PS_ITEM_MONEY_TYPE, (short)getMoneyType());
		data.addLong(KeyID.KEY_PS_ITEM_PRICE, getPrice());
		data.addInt(KeyID.KEY_PS_ITEM_START_SELL_DATE, getStartDate());
		data.addInt(KeyID.KEY_PS_ITEM_END_SELL_DATE, getEndDate());
		data.addShort(KeyID.KEY_PS_ITEM_STATUS, (short)getStatus());
		data.addShort(KeyID.KEY_PS_ITEM_CANCEL_PRICE, (short)getCancelPrice());
		data.addBoolean(KeyID.KEY_PS_ITEM_ADVERTISE, getAdvertiseStatus());
		data.addInt(KeyID.KEY_PS_ITEM_END_ADVERTISE_TIME, getAdvertiseEndTime());
		data.addString(KeyID.KEY_PS_ITEM_BUYER_ID, getBuyerID());
		
		return data.toByteArray();
	}
	
	public void displayDataPackage()
	{
		FBEncrypt item = new FBEncrypt(getData());
		item.displayDataPackage();
	}
	
	public PrivateShopItem(byte[] item_bin)
	{
		FBEncrypt data = new FBEncrypt(item_bin);
		
		type = data.getShort(KeyID.KEY_PS_ITEM_TYPE);
		id = data.getShort(KeyID.KEY_PS_ITEM_ID);
		number = data.getShort(KeyID.KEY_PS_ITEM_NUMBER);
		money_type = data.getShort(KeyID.KEY_PS_ITEM_MONEY_TYPE);
		price = data.getLong(KeyID.KEY_PS_ITEM_PRICE);
		start_date = data.getInt(KeyID.KEY_PS_ITEM_START_SELL_DATE);
		end_date = data.getInt(KeyID.KEY_PS_ITEM_END_SELL_DATE);
		setStatus((int)data.getShort(KeyID.KEY_PS_ITEM_STATUS));
		cancel_price = data.getShort(KeyID.KEY_PS_ITEM_CANCEL_PRICE);
		hasAdvertise = data.getBoolean(KeyID.KEY_PS_ITEM_ADVERTISE);
		end_advertise_time = data.getInt(KeyID.KEY_PS_ITEM_END_ADVERTISE_TIME);
		buyer_id = data.getString(KeyID.KEY_PS_ITEM_BUYER_ID);
	}
	
	public String ToString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("item type = ").append(type);
		sb.append(", item id = ").append(id);
		sb.append(", item number = ").append(number);
		return sb.toString();
	}
}