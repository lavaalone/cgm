package com.vng.skygarden.game;

import com.vng.db.*;
import com.vng.log.*;
import com.vng.netty.*;
import com.vng.skygarden.SkyGarden;
import com.vng.util.*;
import com.vng.skygarden._gen_.ProjectConfig;

import java.util.concurrent.atomic.*;

public class MoneyManager
{
	private AtomicInteger	_money = null;
	private AtomicInteger	_bonus_money = null;
	private int				_money_total = 0;
	
	private DBKeyValue 		_db = null;
	private String			_uid = "";
	private String 			_key_money = "";
	private String 			_key_total_money = "";
	private String 			_key_bonus_money = "";
	
	private final int		WRITE_DB_ERROR  = -1;
	private final int		MONEY_NEGATIVE  = -2;
	private final int		CAS_FAILED		= -3;
	
	private UserInfo		_user_info = null;
	
	public MoneyManager(String uid)
	{
		_uid = uid;
		
		_money = new AtomicInteger(0);
		_bonus_money = new AtomicInteger((int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_USER][0][DatabaseID.USER_DIAMOND]));
		_money_total = 0;
	}
	
	public int GetRealMoney()
	{
		return _money.get();
	}
	
	public int GetBonusMoney()
	{
		return _bonus_money.get();
	}
	
	public int GetTotalMoney()
	{
		return _money_total;
	}
	
	public void SetUserID(String uid)
	{
		_uid = uid;
	}
	
	public void SetDatabase(DBKeyValue db)
	{
		_db = db;
	}
	
	public boolean InitValueOnDatabase(String key_money, String key_mtotal, String key_mbonus, int expire_duration)
	{
		try
		{
			_key_money = _uid + "_" + key_money;
			_key_total_money = _uid + "_" + key_mtotal;
			_key_bonus_money = _uid + "_" + key_mbonus;
			
			if (expire_duration > 0)
			{
				_db.Add(_key_money, "0", expire_duration);
				
				_db.Add(_key_total_money, "0", expire_duration);
			
				_db.Add(_key_bonus_money, "" + Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_USER][0][DatabaseID.USER_DIAMOND]), expire_duration);
			}
			else
			{
				_db.Add(_key_money, "0");
				
				_db.Add(_key_total_money, "0");
			
				_db.Add(_key_bonus_money, "" + Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_USER][0][DatabaseID.USER_DIAMOND]));
			}
		}
		catch (Exception ex)
		{
			LogHelper.LogException("MoneyManager.InitValueOnDatabase()", ex);
			
			return false;
		}
		
		return true;
	}
	
	public boolean InitValueOnDatabase(String key_any, int expire_duration)
	{
		try
		{
			if (expire_duration > 0)
			{
				_db.Add(_uid + "_" + key_any, "0", expire_duration);
			}
			else
			{
				_db.Add(_uid + "_" + key_any, "0");
			}
		}
		catch (Exception ex)
		{
			LogHelper.LogException("MoneyManager.InitValueOnDatabase()", ex);
			
			return false;
		}
		
		return true;
	}

	public boolean LoadFromDatabase(String key_money, String key_mtotal, String key_mbonus)
	{
		try
		{
			_key_money = _uid + "_" + key_money;
			_key_total_money = _uid + "_" + key_mtotal;
			_key_bonus_money = _uid + "_" + key_mbonus;
			
			String str_money = "";
			
			str_money = (String) _db.Get(_key_money);
			_money.set(Integer.parseInt(str_money));
			
			str_money = (String) _db.Get(_key_bonus_money);
			_bonus_money.set(Integer.parseInt(str_money));
			
			str_money = (String) _db.Get(_key_total_money);
			_money_total = Integer.parseInt(str_money);
		}
		catch (Exception ex)
		{
			LogHelper.LogException("MoneyManager.LoadFromDatabase()", ex);
			
			return false;
		}
		
		return true;
	}
	
	public boolean MoveToDatabase(DBKeyValue dest_db, String new_uid, String key_money, String key_mtotal, String key_mbonus, int expire_duration)
	{
		try
		{
			_uid = new_uid;
			_db = dest_db;
			
			_key_money = _uid + "_" + key_money;
			_key_total_money = _uid + "_" + key_mtotal;
			_key_bonus_money = _uid + "_" + key_mbonus;
			
			if (expire_duration > 0)
			{
				_db.Add(_key_money, "" + _money.get(), expire_duration);
				
				_db.Add(_key_total_money, "" + _money_total, expire_duration);
			
				_db.Add(_key_bonus_money, "" + _bonus_money.get(), expire_duration);
			}
			else
			{
				_db.Add(_key_money, "" + _money.get());
				
				_db.Add(_key_total_money, "" + _money_total);
			
				_db.Add(_key_bonus_money, "" + _bonus_money.get());
			}
		}
		catch (Exception ex)
		{
			LogHelper.LogException("MoneyManager.InitValueOnDatabase()", ex);
			
			return false;
		}
		
		// log IFRS
		StringBuilder ifrs = new StringBuilder();
		ifrs.append(_uid);
		ifrs.append(',').append(_money.get());
		ifrs.append(',').append(Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_USER][0][DatabaseID.USER_DIAMOND]));
		ifrs.append(',').append(System.currentTimeMillis());
		ifrs.append(',').append(0);
		ifrs.append(',').append(0);
		ifrs.append(',').append(Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_USER][0][DatabaseID.USER_DIAMOND]));
		ifrs.append(',').append("");
		ifrs.append(',').append(0); // 0 means register
		ifrs.append(',').append(Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_USER][0][DatabaseID.USER_DIAMOND]));
		LogHelper.Log(LogHelper.LogType.IFRS, ifrs.toString());
		
		return true;
	}
	
	public UseMoneyResult UseRealMoneyOnly(int amount)
	{
		UseMoneyResult result = new UseMoneyResult(UseMoneyResult.CODE.INVALID_NUMBER, 
													0,
													0);
		
		if (amount <= 0)
		{
			return result;
		}
		
		int new_money = 0;
		do
		{
			int current_money = _money.get();
			
			if (amount > current_money)
			{
				result._code = UseMoneyResult.CODE.NOT_ENOUGH_MONEY;
				
				return result;
			}
			
			//use money
			new_money = DecreaseRealMoney(amount, current_money);
			
			//error
			if (new_money == -1 || new_money == -2)
			{
				result._num_real_money = 0;		//old value
				result._num_bonus_money = 0;	//new value
				
				if (new_money == -1)
				{
					result._code = UseMoneyResult.CODE.WRITE_DB_ERROR;
				}
				else if (new_money == -2)
				{
					result._code = UseMoneyResult.CODE.MONEY_NEGATIVE;
				}
				
				return result;
			}
		}
		while (new_money == -3);
		
		result._code = UseMoneyResult.CODE.SUCCESS;
		result._num_real_money = amount;
		result._num_bonus_money = 0;
		
		return result;
	}

// original by pH
//	public UseMoneyResult UseBonusMoneyAndRealMoney(int amount)
//	{
//		UseMoneyResult result = new UseMoneyResult(UseMoneyResult.CODE.INVALID_NUMBER, 0, 0);
//
//		if (amount <= 0)
//		{
//			return result;
//		}
//		
//		int new_money = 0;
//		int new_bonus_money = 0;
//		
//		do
//		{
//			int current_money = _money.get();
//			int current_bonus_money = _bonus_money.get();
//			
//			if (amount > (current_money + current_bonus_money))
//			{
//				result._code = UseMoneyResult.CODE.NOT_ENOUGH_MONEY;
//				
//				return result;
//			}
//			
//			//use bonus money
//			int bonus_amount = 0;
//			if (current_bonus_money > 0)
//			{
//				// if stored_bonus_amount > amount_need) => decrease amount_need in bonus money
//				// else => decrease all bonus money
//				if (current_bonus_money >= amount)
//				{
//					bonus_amount = amount;
//				}
//				else
//				{
//					bonus_amount = current_bonus_money;
//				}
//				
//				new_bonus_money = DecreaseBonusMoney(bonus_amount, current_bonus_money);
//				
//				if (new_bonus_money == -1 || new_bonus_money == -2)
//				{
//					result._num_real_money = 0;
//					result._num_bonus_money = 0;
//					
//					if (new_bonus_money == -1)
//					{
//						result._code = UseMoneyResult.CODE.WRITE_DB_ERROR;
//					}
//					else if (new_bonus_money == -2)
//					{
//						result._code = UseMoneyResult.CODE.MONEY_NEGATIVE;
//					}
//					
//					return result;
//				}
//			}
//			
//			//use real money
//			if (new_bonus_money >= 0)
//			{
//				result._code = UseMoneyResult.CODE.SUCCESS;
//				result._num_real_money = 0;
//				result._num_bonus_money = bonus_amount;
//				
//				int real_amount = amount - bonus_amount;
//				
//				if (real_amount > 0)
//				{
//					do
//					{
//						current_money = _money.get();
//						
//						//not enough real money
//						if (real_amount > current_money)
//						{
//							//refund bonus money
//							if (bonus_amount > 0)
//							{
//								IncreaseBonusMoney(bonus_amount, Reason.REFUND_TO_USER);
//							}
//							
//							//log
//							
//							result._code = UseMoneyResult.CODE.NOT_ENOUGH_MONEY;
//				
//							return result;
//						}
//						
//						//use real money
//						new_money = DecreaseRealMoney(real_amount, current_money);
//						
//						//error
//						if (new_money == -1 || new_money == -2)
//						{
//							result._num_real_money = 0;		//old value
//							result._num_bonus_money = 0;	//new value
//							
//							if (new_money == -1)
//							{
//								result._code = UseMoneyResult.CODE.WRITE_DB_ERROR;
//							}
//							else if (new_money == -2)
//							{
//								result._code = UseMoneyResult.CODE.MONEY_NEGATIVE;
//							}
//							
//							return result;
//						}
//						
//						if (new_money >= 0)
//						{
//							result._code = UseMoneyResult.CODE.SUCCESS;
//							result._num_real_money = real_amount;
//						}
//					}
//					while (new_money == -3);
//				}
//			}
//		}
//		while (new_money == -3 || new_bonus_money == -3);
//			
//		return result;
//	}

	public UseMoneyResult UseRealMoneyAndBonusMoney(int amount, int command_id, String user_name, int user_level, String user_ip, int item_type, int item_id, String item_name, long item_price, int item_num)
	{
		return UseRealMoneyAndBonusMoney(amount, command_id, "", user_name, user_level, user_ip, item_type, item_id, item_name, item_price, item_num);
	}
	
	public UseMoneyResult UseRealMoneyAndBonusMoney(int amount, int command_id, String action_code, String user_name, int user_level, String user_ip, int item_type, int item_id, String item_name, long item_price, int item_num)
	{
		UseMoneyResult result = new UseMoneyResult(UseMoneyResult.CODE.INVALID_NUMBER, 0, 0);
		
		if (!LoadFromDatabase(KeyID.KEY_MONEY_REAL, KeyID.KEY_MONEY_TOTAL, KeyID.KEY_MONEY_BONUS))
		{
			return result;
		}

		if (amount <= 0)
		{
			return result;
		}
		
		int new_real_money = 0;
		int new_bonus_money = 0;
		
		do
		{
			int current_real_money = _money.get();
			int current_bonus_money = _bonus_money.get();
			
			if (amount > (current_real_money + current_bonus_money))
			{
				result._code = UseMoneyResult.CODE.NOT_ENOUGH_MONEY;
				
				return result;
			}
			
			// decrease real money
			int decreased_real = 0;
			if (current_real_money > 0)
			{
				do
				{
//					LogHelper.Log("UseRealMoneyAndBonusMoney... ###Decrease real money");
					current_real_money = _money.get();
					
					if (current_real_money >= amount)
					{
						decreased_real = amount;
					}
					else
					{
						decreased_real = current_real_money;
					}

					new_real_money = DecreaseRealMoney(decreased_real, current_real_money);
					
					// decrease money fail
					if (new_real_money == -1 || new_real_money == -1)
					{
						result._num_bonus_money = 0;
						result._num_real_money = 0;

						if (new_real_money == WRITE_DB_ERROR)
						{
							result._code = UseMoneyResult.CODE.WRITE_DB_ERROR;
						}
						else if (new_real_money == MONEY_NEGATIVE)
						{
							result._code = UseMoneyResult.CODE.MONEY_NEGATIVE;
						}

						return result;
					}
				}
				while (new_real_money == CAS_FAILED);
			}
			
			// decrease bonus money
			if (new_real_money >= 0)
			{
				result._code = UseMoneyResult.CODE.SUCCESS;
				result._num_real_money = decreased_real;
				result._num_bonus_money = 0;
				
				int decreased_bonus = amount - decreased_real;
				
				if (decreased_bonus > 0)
				{
//					LogHelper.Log("UseRealMoneyAndBonusMoney... ###Decrease bonus money");
					if (decreased_bonus > current_bonus_money) // not enough bonus money
					{
						// refund bonus money
						if (decreased_real > 0)
						{
							IncreaseBonusMoney(decreased_real, Reason.REFUND_TO_USER);
						}

						// log

						result._code = UseMoneyResult.CODE.NOT_ENOUGH_MONEY;

						return result;
					}
					
					//use bonus money
					new_bonus_money = DecreaseBonusMoney(decreased_bonus, current_bonus_money);
					
					//error
					if (new_bonus_money == -1 || new_bonus_money == -2)
					{
						result._num_real_money = 0;		//old value
						result._num_bonus_money = 0;	//new value

						if (new_bonus_money == WRITE_DB_ERROR)
						{
							result._code = UseMoneyResult.CODE.WRITE_DB_ERROR;
						}
						else if (new_bonus_money == MONEY_NEGATIVE)
						{
							result._code = UseMoneyResult.CODE.MONEY_NEGATIVE;
						}

						return result;
					}

					if (new_bonus_money >= 0)
					{
						result._code = UseMoneyResult.CODE.SUCCESS;
						result._num_bonus_money = decreased_bonus;
					}
				}
			}
		}
		while (new_real_money == CAS_FAILED || new_bonus_money == CAS_FAILED);
		
		// log VD
		StringBuilder log = new StringBuilder();
		log.append(Misc.getCurrentDateTime());								//  1. thoi gian tieu tien
		log.append('\t').append(Misc.getActionName(command_id, action_code));//  2. hanh dong cua gamer
		log.append('\t').append(_uid);										//  3. ten tai khoan
		log.append('\t').append(_uid);										//  4. id cua role nap tien
		log.append('\t').append(user_name);									//  5. ten role
		log.append('\t').append(0);											//  6. id cua server
		log.append('\t').append(user_level);								//  7. level cua gamer
		log.append('\t').append(_uid + "_" + System.currentTimeMillis());	//  8. transaction id duy nhat do billing server sinh ra
		log.append('\t').append(user_ip);									//  9. ip login v4 cua gamer
		if (command_id == CommandID.CMD_BUY_IBSHOP_PACKAGE // ibshop
				|| command_id == CommandID.CMD_BUY_ITEM_UPGRADE_STOCK || command_id == CommandID.CMD_BUY_ITEM_UPGRADE_STOCK_OPTIMIZE_DATA_OUT
				|| command_id == CommandID.CMD_BUY_PEARL_UPGRADE_POT
				|| command_id == CommandID.CMD_BUY_MATERIAL_UPGRADE_POT
				|| command_id == CommandID.CMD_COMPLETE_ITEMS_TO_PRODUCT
				|| command_id == CommandID.CMD_COMPLETE_ORDER
				|| command_id == CommandID.CMD_BUY_ITEM_OPEN_FLOOR
				|| command_id == CommandID.CMD_BUY_ITEMS_FOR_MERCHANT)
		{
			log.append('\t').append(1);
			log.append('\t').append(item_name + "_" + item_type + "_" + item_id);
			log.append('\t').append(item_name + "_" + item_type + "_" + item_id);
		}
		else
		{
			log.append('\t').append(2);
			log.append('\t').append(Misc.getActionName(command_id, action_code));
			log.append('\t').append(Misc.getActionName(command_id, action_code));
		}
											// 12. ten item
		log.append('\t').append(item_price);								// 13. gia game coin cua item
		log.append('\t').append(item_num);									// 14. so luong vat pham
		log.append('\t').append(item_price * item_num);						// 15. so tien tieu
		log.append('\t').append(result._num_real_money);					// 16. so game coin tieu tu nguon nap
		log.append('\t').append(result._num_bonus_money);					// 17. so game coin tieu tu nguon thuong them
		log.append('\t').append(_money.get() + _bonus_money.get());			// 19. tong so coin con lai sau khi tieu
		log.append('\t').append(_money.get());								// 20. so game coin sau khi tieu
		log.append('\t').append(_bonus_money.get());						// 21. so game coin thuong them sau khi tieu
		
		if (command_id == CommandID.CMD_BUY_IBSHOP_PACKAGE // ibshop
				|| command_id == CommandID.CMD_BUY_ITEM_UPGRADE_STOCK || command_id == CommandID.CMD_BUY_ITEM_UPGRADE_STOCK_OPTIMIZE_DATA_OUT
				|| command_id == CommandID.CMD_BUY_PEARL_UPGRADE_POT
				|| command_id == CommandID.CMD_BUY_MATERIAL_UPGRADE_POT
				|| command_id == CommandID.CMD_COMPLETE_ITEMS_TO_PRODUCT
				|| command_id == CommandID.CMD_COMPLETE_ORDER
				|| command_id == CommandID.CMD_BUY_ITEM_OPEN_FLOOR
				|| command_id == CommandID.CMD_BUY_ITEMS_FOR_MERCHANT)
		{
			log.append('\t').append("");									// 22. thong tin khac
		}
		else
		{
			if (!item_name.equals(""))
				log.append('\t').append(item_name + ":" + item_num);			// 22. thong tin khac
			else
				log.append('\t').append("");									// 22. thong tin khac
		}
		
		log.append('\t').append(1);											// 23. 1: success; 0: error code
		LogHelper.Log(LogHelper.LogType.SPENT_MONEY, log.toString());
		
		// log IFRS
		StringBuilder ifrs = new StringBuilder();
		ifrs.append(_uid);
		ifrs.append(',').append(_money.get());
		ifrs.append(',').append(_bonus_money.get());
		ifrs.append(',').append(System.currentTimeMillis());
		ifrs.append(',').append(0);
		ifrs.append(',').append(result._num_real_money > 0 ? "-" + result._num_real_money : result._num_real_money);
		ifrs.append(',').append(result._num_bonus_money > 0 ? "-" + result._num_bonus_money : result._num_bonus_money);
		if (command_id == CommandID.CMD_BUY_IBSHOP_PACKAGE // ibshop
				|| command_id == CommandID.CMD_BUY_ITEM_UPGRADE_STOCK || command_id == CommandID.CMD_BUY_ITEM_UPGRADE_STOCK_OPTIMIZE_DATA_OUT
				|| command_id == CommandID.CMD_BUY_PEARL_UPGRADE_POT
				|| command_id == CommandID.CMD_BUY_MATERIAL_UPGRADE_POT
				|| command_id == CommandID.CMD_COMPLETE_ITEMS_TO_PRODUCT
				|| command_id == CommandID.CMD_COMPLETE_ORDER
				|| command_id == CommandID.CMD_BUY_ITEM_OPEN_FLOOR
				|| command_id == CommandID.CMD_BUY_ITEMS_FOR_MERCHANT)
		{
			ifrs.append(',').append(item_name + "_" + item_type + "_" + item_id); 
			ifrs.append(',').append("");
		}
		else // nonibshop
		{
			ifrs.append(',').append(""); 
			ifrs.append(',').append(Misc.getActionName(command_id, action_code));
		}
		ifrs.append(',').append(amount);
		LogHelper.Log(LogHelper.LogType.IFRS, ifrs.toString());
		
		// LOG PIG
		StringBuilder piglog = new StringBuilder();
		piglog.append(Misc.getCurrentDateTime());		//  1. thoi gian dang nhap
		piglog.append('\t').append("SpentMoney");
		piglog.append('\t').append(_user_info != null ? _user_info.getPigID() : "null");
		piglog.append('\t').append("CGMFBS");
		piglog.append('\t').append(SkyGarden._server_id);
		piglog.append('\t').append(_user_info != null ? _user_info.getDeviceOS() : "null");
		piglog.append('\t').append(_uid);
		piglog.append('\t').append(_uid + "_" + Misc.SECONDS());
		piglog.append('\t').append(amount);
		piglog.append('\t').append(Misc.getActionName(command_id, action_code));
		piglog.append('\t').append(item_name + "_" + item_type + "_" + item_id);
		LogHelper.Log(LogHelper.LogType.PIG_LOG, piglog.toString());
		
		return result;
	}

	private int DecreaseRealMoney(int amount, int current_money)
	{
		if (_money.compareAndSet(current_money, current_money - amount))
		{
			//use real money
			int new_money = 0;
			
			try
			{
				new_money = (int)_db.Decrease(_key_money, amount);
			}
			catch (Exception ex)
			{
				//log special exception
				LogHelper.LogDecreaseMoneyException(LogHelper.MoneyType.XU, _uid, amount, ex);
				
				return -1;
			}
			
			//set newest value from db
			_money.set(new_money);
			
			//log change money
//			LogHelper.LogDecreaseMoney(LogHelper.MoneyType.XU, _uid, amount, new_money);
			
			if (new_money < 0)
			{
				return -2;
			}
			
			return new_money;
		}
		
		return -3;
	}
	
	private int DecreaseBonusMoney(int amount, int current_money)
	{
		if (_bonus_money.compareAndSet(current_money, current_money - amount))
		{
			//use real money
			int new_money = 0;
			
			try
			{
				new_money = (int)_db.Decrease(_key_bonus_money, amount);
			}
			catch (Exception ex)
			{
				//log special exception
				LogHelper.LogDecreaseMoneyException(LogHelper.MoneyType.XU_THUONG, _uid, amount, ex);
				
				return -1;
			}
			
			//set newest value from db
			_bonus_money.set(new_money);
			
			//log change bonus money
//			LogHelper.LogDecreaseMoney(LogHelper.MoneyType.XU_THUONG, _uid, amount, new_money);
			
			if (new_money < 0)
			{
				return -2;
			}
			
			return new_money;
		}
		
		return -3;
	}
	
	public void IncreaseBonusMoney(int amount, Reason reason)
	{
		if (amount > 0)
		{
			int new_money = 0;
			int current_money = GetBonusMoney();
			
			try
			{
				new_money = (int)_db.Increase(_key_bonus_money, amount);
			}
			catch (Exception ex)
			{
				//log special exception
				LogHelper.LogIncreaseMoneyException(LogHelper.MoneyType.XU_THUONG, _uid, amount, reason.toString(), ex);
				
				return;
			}
			
			_bonus_money.set(new_money);
		}
	}
	
	public boolean IncreaseBonusMoney(int amount, String reason)
	{
		if (amount > 0)
		{
			int new_money = 0;
//			int current_money = GetBonusMoney();
			
			try
			{
				new_money = (int)_db.Increase(_key_bonus_money, amount);
			}
			catch (Exception ex)
			{
				LogHelper.LogIncreaseMoneyException(LogHelper.MoneyType.XU_THUONG, _uid, amount, reason, ex);
				return false;
			}
			
			_bonus_money.set(new_money);
			return true;
		}
		
		return false;
	}
	
	
	
	public void IncreaseBonusMoney(int amount, Reason reason,
											int command_id,
											String user_name,
											int user_level,
											String user_ip,
											int item_type, 
											int item_id, 
											String item_name, 
											long item_price, 
											int item_num)
	{
		if (amount > 0)
		{
			if (!LoadFromDatabase(KeyID.KEY_MONEY_REAL, KeyID.KEY_MONEY_TOTAL, KeyID.KEY_MONEY_BONUS))
			{
				return ;
			}
			
			int new_money = 0;
			int current_money = GetBonusMoney();
			
			try
			{
				new_money = (int)_db.Increase(_key_bonus_money, amount);
			}
			catch (Exception ex)
			{
				//log special exception
				LogHelper.LogIncreaseMoneyException(LogHelper.MoneyType.XU_THUONG, _uid, amount, reason.toString(), ex);
				
				return;
			}
			
			_bonus_money.set(new_money);
			
//			// log VD
//			StringBuilder log = new StringBuilder();
//			log.append(Misc.getCurrentDateTime());								//  1. thoi gian tieu tien
//			log.append('\t').append(Misc.getActionName(command_id));			//  2. hanh dong cua gamer
//			log.append('\t').append(_uid);										//  3. ten tai khoan
//			log.append('\t').append(_uid);										//  4. id cua role nap tien
//			log.append('\t').append(user_name);									//  5. ten role
//			log.append('\t').append(0);											//  6. id cua server
//			log.append('\t').append(user_level);								//  7. level cua gamer
//			log.append('\t').append(_uid + "_" + System.currentTimeMillis());	//  8. transaction id duy nhat do billing server sinh ra
//			log.append('\t').append(user_ip);									//  9. ip login v4 cua gamer
//			if (command_id == CommandID.CMD_BUY_IBSHOP_PACKAGE)
//			{
//				log.append('\t').append(1);
//				log.append('\t').append(item_name + "_" + item_type + "_" + item_id);
//			}
//			else
//			{
//				log.append('\t').append(2);
//				log.append('\t').append("");
//			}
//			log.append('\t').append(item_name);									// 12. ten item
//			log.append('\t').append(item_price);								// 13. gia game coin cua item
//			log.append('\t').append(item_num);									// 14. so luong vat pham
//			log.append('\t').append(item_price * item_num);						// 15. so tien tieu
//			log.append('\t').append(0);											// 16. so game coin tieu tu nguon nap
//			log.append('\t').append(item_price);								// 17. so game coin tieu tu nguon thuong them
//			log.append('\t').append(_money.get() + _bonus_money.get());			// 19. tong so coin con lai sau khi tieu
//			log.append('\t').append(_money.get());								// 20. so game coin sau khi tieu
//			log.append('\t').append(_bonus_money.get());						// 21. so game coin thuong them sau khi tieu
//
//			if (command_id == CommandID.CMD_BUY_IBSHOP_PACKAGE)
//			{
//				log.append('\t').append("");									// 22. thong tin khac
//			}
//			else
//			{
//				if (!item_name.equals("")) log.append('\t').append(item_name + ":" + item_num);			// 22. thong tin khac
//			}
//
//			log.append('\t').append(1);											// 23. 1: success; 0: error code
//			LogHelper.Log(LogHelper.LogType.PAYING, log.toString());
			
			// log VD
			StringBuilder log2 = new StringBuilder();
			log2.append(Misc.getCurrentDateTime());
			log2.append('\t').append(_uid); // 
			log2.append('\t').append(5); // payment gateway, 4 means SO6
			log2.append('\t').append(1);
			log2.append('\t').append(System.currentTimeMillis());
			log2.append('\t').append(user_ip); // TODO: get phone number from SO6
			log2.append('\t').append(0); // so tien user tra
			log2.append('\t').append(0); // so tien game nhan
			log2.append('\t').append(amount); // tong so game coin nhan duoc
			log2.append('\t').append(0);  // so game coin nhan
			log2.append('\t').append(amount); // so game coin duoc bonus
			log2.append('\t').append(_money.get() + _bonus_money.get()); // tong game coin sau khi nap
			log2.append('\t').append(_money.get()); // game coin bonus sau khi nap
			log2.append('\t').append(_bonus_money.get());
			log2.append('\t').append(Misc.getActionName(command_id)); // description.
			log2.append('\t').append(0);
			log2.append('\t').append(_uid);
			log2.append('\t').append(user_name);
			log2.append('\t').append(0);
			log2.append('\t').append(user_level);
			LogHelper.Log(LogHelper.LogType.PAYING, log2.toString());
			
			// log IFRS
			StringBuilder ifrs = new StringBuilder();
			ifrs.append(_uid);
			ifrs.append(',').append(_money.get());
			ifrs.append(',').append(_bonus_money.get());
			ifrs.append(',').append(System.currentTimeMillis());
			ifrs.append(',').append(0);
			ifrs.append(',').append(0);
			ifrs.append(',').append(amount);
			
			String item = "";
			if (item_type != -1 && item_id != -1) // ibshop
			{
				item = item_type + "_" + item_id;
			}
			ifrs.append(',').append(item);
			
			if (item.equals("")) // nonibshop
			{
				ifrs.append(',').append(Misc.getActionName(command_id));
			}
			else
			{
				ifrs.append(',').append("");
			}
			
			ifrs.append(',').append(item_price);
			LogHelper.Log(LogHelper.LogType.IFRS, ifrs.toString());
			
//			LogHelper.LogIncreaseMoney(LogHelper.MoneyType.XU_THUONG, _uid, current_money, amount, new_money, reason.toString());
		}
	}
	
	public boolean IncreaseRealMoney(int amount, String reason)
	{
		if (amount > 0)
		{
			int current_money = GetRealMoney();
			
			if (_money.compareAndSet(current_money, current_money + amount))
			{
				int new_money = 0;

				try
				{
					new_money = (int)_db.Increase(_key_money, amount);
				}
				catch (Exception ex)
				{
					LogHelper.LogIncreaseMoneyException(LogHelper.MoneyType.XU, _uid, amount, "increase", ex);
					return false;
				}

				//set newest value from db
				_money.set(new_money);
				
//				LogHelper.LogIncreaseMoney(LogHelper.MoneyType.XU, _uid, current_money, amount, new_money, reason);
				
				return true;
			}
		}
		
		return false;
	}
	
	public void displayDataPackage()
	{
		try
		{
			String _diamond = "" + (GetBonusMoney() + GetRealMoney());
			LogHelper.Log("diamond: " + _diamond);
		}
		catch (Exception ex)
		{
		
		}
	}
	
	public enum Reason
	{
		BONUS_MONEY_FOR_USER,
		ADD_BY_GM,
		REFUND_TO_USER,
		SYSTEM_GIFT
	}
	
	public void SetUserInfo(UserInfo user_info)
	{
		this._user_info = user_info;
	}
}

class UseMoneyResult
{
	public enum CODE
	{
		SUCCESS,
		INVALID_NUMBER,
		NOT_ENOUGH_MONEY,
		WRITE_DB_ERROR,
		MONEY_NEGATIVE
	}
	
	public CODE		_code;
	public int		_num_real_money;
	public int		_num_bonus_money;
	
	public UseMoneyResult(CODE code, int real, int bonus)
	{
		_code = code;
		_num_real_money = real;
		_num_bonus_money = bonus;
	}
}