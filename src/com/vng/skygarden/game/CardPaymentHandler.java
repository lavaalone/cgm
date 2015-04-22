/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;
import com.google.common.base.Strings;
import com.vng.log.LogHelper;
import com.vng.nettyhttp.*;
import com.vng.netty.Server;
import com.vng.skygarden.*;
import com.vng.skygarden._gen_.ProjectConfig;
import static com.vng.skygarden.game.SkyGardenUser.simpleUDP;
import com.vng.util.FBEncrypt;
import com.vng.util.Misc;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.sql.*;
import com.vng.skygarden.game.GameUtil;

/* 
 * Sample
 * {"TransactionID":"201309271251060SE1598999","Type":"MCARD","MobileOperator":"VNP","UserID":"125","GrossAmount":100000,"NetAmount":80000,"OptionData":"UmVmaWxsIGJ5IFZOUA==","RequestTime":"2013-09-27 12:51:06","Sig":"58d5848d5cc77138160adc08e6d499ad"}
 * {"TransactionID":"20140707ATM11404703750971","Type":"ATM","MobileOperator":"123PVTB","UserID":"10001","GrossAmount":"50000","NetAmount":"50000","OptionData":"dGVzdF9hZGRfaW5mbw==","RequestTime":"2014-07-07 10:29:38","Sig":"e17f1210c350af1e2ef23db430468c7a"}
 * MobileOperator: VNP
 * TransactionID: 201309271251060SE1598999
 * Sig: 58d5848d5cc77138160adc08e6d499ad
 * OptionData: UmVmaWxsIGJ5IFZOUA==
 * Type: MCARD
 * NetAmount: 80000
 * UserID: 125
 * GrossAmount: 100000
 * RequestTime: 2013-09-27 12:51:06
 */
public class CardPaymentHandler
{
	Client _client = null;
	String _data = null;
	
	private String sig = "";
	private String transactionid = "";
	private String type = "";
	private int gross = -1;
	private int net = -1;
	private long userid = -1;
	private String s_userid = "";
	private String operator = "";
	
	private final int TRANSACTION_OK = 0;
	private final int TRANSACTION_WRONG_SIG = 1;
	private final int TRANSACTION_WRONG_UID = 2;
	private final int TRANSACTION_EXCEPTION = 3;
	
	private int transactionresult = TRANSACTION_OK;
	
	boolean _promotion_first_pay = false;
	private final boolean TEST_MODE = !true;
	private String offer_firstpay_gift = "";
	
	private String s_gross = "";
	private boolean _is_ATM = false;
	private int ATM_RATIO = 100; // 100 vnd = 1 diamond
	
	public CardPaymentHandler(Client client, String data)
	{
		_client = client;
		_data = data;
	}
	
	public void Execute() // throws Exception
	{
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		try
		{
			Object obj = parser.parse(_data);
			jsonObject = (JSONObject) obj;
		}
		catch (Exception e)
		{
			LogHelper.LogException("CardPaymentHandler.parse_data", e);
		}
		
		// get params
		try
		{
			sig				= (String)jsonObject.get("Sig");
			transactionid	= (String)jsonObject.get("TransactionID");
			type			= (String)jsonObject.get("Type");
			s_userid		= (String)jsonObject.get("UserID");
			operator		= (String)jsonObject.get("MobileOperator");
		}
		catch (Exception e)
		{
			LogHelper.LogException("CardPaymentHandler.GetPaymentParams", e);
		}
		
		// in case user inpust uid as text in sms
		try 
		{
			userid = Long.parseLong(s_userid);
		} 
		catch (Exception e) 
		{
			transactionresult = TRANSACTION_WRONG_UID;
			LogHelper.LogException("CardPaymentHandler.GetUserID", e);
		}
		
		// *important: get gross
		try
		{
			if (jsonObject.get("GrossAmount") instanceof String) //sms
			{
				s_gross = (String)jsonObject.get("GrossAmount"); // to valid sig
				
				String s = ((String)jsonObject.get("GrossAmount"));
				String[] aos = s.split("\\.");
				
				if (aos == null || aos.length == 0)
				{
					if (s.contains(".0"))
					{
						String ss = s.replace(".0", "");
						gross = (int)(Long.parseLong(ss));
						LogHelper.Log("GetGross.. case 1");
					}
					else
					{
						gross = (int)(Long.parseLong(s));
						LogHelper.Log("GetGross.. case 2");
					}
				}
				else
				{
					gross = (int)(Long.parseLong(aos[0]));
					LogHelper.Log("GetGross.. case 3");
				}
			}
			else //cards
			{
				gross = Integer.parseInt(Long.toString((long)jsonObject.get("GrossAmount")));
				
				s_gross = "" + gross;
			}
			
		}
		catch (Exception e)
		{
			gross = -1;
			LogHelper.LogException("CardPaymentHandler.get_gross", e);
		}
		
		// *important: get net
		try
		{
			if (jsonObject.get("NetAmount") instanceof String) //sms
			{
				String s_net = (String)(jsonObject.get("NetAmount"));
				if (!s_net.equals("None"))
				{
					net = (int)(Long.parseLong(s_net));
				}
			}
			else //cards
			{
				net = Integer.parseInt(Long.toString((long)jsonObject.get("NetAmount")));
			}
			
		}
		catch (Exception e)
		{
			net = -1;
			LogHelper.LogException("CardPaymentHandler.get_net", e);
		}
		
		int log_iGameCoin = 0;
		int log_iPromotionCoin = 0;
		int log_iGameCoinBefore = 0;
		int log_iPromotionCoinBefore = 0;
		int log_iGameCoinAfter = 0;
		int log_iPromotionCoinAfter = 0;
		int log_iLevel = 0;
		int log_chargeType = 0;
		String log_sUserIP = "";
		String log_sUsername = "";
		
		if (type.equals("MCARD") || type.equals("mcard") || type.equals("ZCard") || type.equals("ZC"))		
		{
			log_chargeType = 2;
		}
		else if (type.equals("SMS") || type.equals("sms"))		
		{
			log_chargeType = 3;
		}
		else if (type.equals("ATM") || type.equals("atm"))		
		{
			log_chargeType = 4;
			_is_ATM = true;
		}
		LogHelper.Log("payment via atm = " + _is_ATM);
		
		// get user info before adding money
		boolean load_result = false;
		byte[] userbin = null;
		try
		{
			userbin = DBConnector.GetMembaseServer(userid).GetRaw(userid + "_" + KeyID.KEY_USER_INFOS);
		}
		catch (Exception e)
		{
			userbin = null;
			LogHelper.Log("CardPaymentHandler.. can not get user info data.");
			LogHelper.LogException("CardPaymentHandler.LoadUserData", e);
		}
		UserInfo userInfo = new UserInfo(userid);
		if (userbin != null)
		{
			userInfo = new UserInfo(userbin);
			if (userInfo.isLoadSuccess())
			{
				log_iLevel = userInfo.getLevel();
				log_sUsername = userInfo.getName();
				log_sUserIP = userInfo.GetUserIP();
				load_result = true;
			}
		}
		
		if (!load_result) 
		{
			transactionresult = TRANSACTION_WRONG_UID;
		}
		
		// get last pay info
		String last_pay = "";
		try
		{
			last_pay = (String)DBConnector.GetMembaseServer(userid).Get(userid + "_" + KeyID.KEY_LAST_PAY_TIME);
			
			if (last_pay == null)
				last_pay = "null";
		}
		catch (Exception e)
		{
			last_pay = null;
		}
		
		// get total paid
		int total_paid = 0;
		try 
		{
			total_paid = (int)DBConnector.GetMembaseServer(userid).Get(userid + "_" + KeyID.KEY_TOTAL_PAID);
		} catch (Exception e) 
		{
			total_paid = 0;
		}
		
		/* check if first pay campaign is running */
		try 
		{
			_promotion_first_pay = (int)DBConnector.GetMembaseServerForTemporaryData().Get(KeyID.KEY_PAYMENT_USE_FIRST_PAY) == 1;
		} 
		catch (Exception e) 
		{
		}
		
		if (TEST_MODE)
		{
			int rand = Misc.RANDOM_RANGE(0, 23);
			switch (rand)
			{
				case 0:
					gross = 20000;
					operator = "ZC";
					break;
				case 1:
					gross = 60000;
					operator = "ZC";
					break;
				case 2:
					gross = 120000;
					operator = "ZC";
					break;
				case 3:
					gross = 200000;
					operator = "ZC";
					break;
				case 4:
					gross = 500000;
					operator = "ZC";
					break;
				case 5:
					gross = 20000;
					operator = "VNP";
					break;
				case 6:
					gross = 50000;
					operator = "VNP";
					break;
				case 7:
					gross = 100000;
					operator = "VNP";
					break;
				case 8:
					gross = 200000;
					operator = "VNP";
					break;
				case 9:
					gross = 500000;
					operator = "VNP";
					break;
				case 10:
					gross = 20000;
					operator = "VMS";
					break;
				case 11:
					gross = 50000;
					operator = "VMS";
					break;
				case 12:
					gross = 100000;
					operator = "VMS";
					break;
				case 13:
					gross = 200000;
					operator = "VMS";
					break;
				case 14:
					gross = 500000;
					operator = "VMS";
					break;
				case 15:
					gross = 20000;
					operator = "VTT";
					break;
				case 16:
					gross = 50000;
					operator = "VTT";
					break;
				case 17:
					gross = 100000;
					operator = "VTT";
					break;
				case 18:
					gross = 200000;
					operator = "VTT";
					break;
				case 19:
					gross = 500000;
					operator = "VTT";
					break;
				case 21:
					type = "SMS";
					gross = 5000;
					operator = "SMS";
					break;
				case 22:
					type = "SMS";
					gross = 10000;
					operator = "SMS";
					break;
				case 23:
					type = "SMS";
					gross = 15000;
					operator = "SMS";
					break;
			}
		}
		
		if (transactionresult == TRANSACTION_OK)
		{
			if (!IsValidSig(sig))
			{
				transactionresult = TRANSACTION_WRONG_SIG;
			}
		}
		
		boolean is_transaction_exist = true;
		try 
		{
			is_transaction_exist = SkyGarden._sql_connector.IsTransactionExist(transactionid);
		} 
		catch (Exception e) 
		{
			is_transaction_exist = true;
			LogHelper.LogException("CardPaymentHandler.CheckSQLTransactionExist", e);
		}
		
		if (load_result && (transactionresult == TRANSACTION_OK) && !is_transaction_exist)
		{
			//read offer info
			boolean has_special_offer = true;
			byte[] offerbin = null;
			try 
			{
				offerbin = DBConnector.GetMembaseServer(userid).GetRaw(userid + "_" + KeyID.KEY_SPECIAL_OFFER);
			} 
			catch (Exception e) 
			{
				offerbin = null;
				has_special_offer = false;
				LogHelper.LogException("CardPaymentHandler.LoadSpecialOffer", e);
			}
			
			Special_Offer _offer = null;
			if (has_special_offer &&  offerbin != null && offerbin.length > 0) {
				_offer = new Special_Offer(offerbin);
			}
			//-------------------------------------------------------------
			
			// base on gross, decides how many diamond will be added to user's money
			for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_PAYMENT].length; i++)
			{
				int v = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_GROSS_AMOUNT]);
				String s = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_OPERATOR]);
				
				if (gross == v || _is_ATM)
				{
					if (type.equals("sms") || type.equals("SMS") || type.equals(s) || operator.equals(s) || _is_ATM)
					{
						int diamond_real = 0;
						
						if (SkyGarden.InSaleOffEvent()) 
						{
							diamond_real = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT_EVENT][i][DatabaseID.PAYMENT_DIAMOND_RECEIVE_REAL]);
						} 
						else if (_is_ATM)
						{
							diamond_real = gross / ATM_RATIO;
						}
						else 
						{
							diamond_real = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_DIAMOND_RECEIVE_REAL]);
						}
						LogHelper.Log("diamond real = " + diamond_real);
						
						int diamond_bonus = 0;
						
						/* get bonus from first time paying */
						if (_promotion_first_pay) 
						{
							if (last_pay != null && last_pay.equals("null")) 
							{
								diamond_bonus = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_DIAMOND_RECEIVE_BONUS_FIRST_PAY]);
							}
						}
						
						/* get bonus from promotion */
						if (SkyGarden.InSaleOffEvent())
						{
							diamond_bonus += (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT_EVENT][i][DatabaseID.PAYMENT_DIAMOND_RECEIVE_BONUS]);
						}
						else if (_is_ATM)
						{
							int bonus_percent_atm = 0; // todo: calculate bonus from atm
							for (int j = 0; j < Server.s_globalDB[DatabaseID.SHEET_PAYMENT].length; j++)
							{
								String atm_operator = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][j][DatabaseID.PAYMENT_OPERATOR]);
								if (atm_operator.equals("ATM"))
								{
									int atm_gross = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][j][DatabaseID.PAYMENT_GROSS_AMOUNT]);
									if (gross < atm_gross)
									{
										bonus_percent_atm = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][j][DatabaseID.PAYMENT_DIAMOND_RECEIVE_BONUS]);
										break;
									}
								}
							}
							
							diamond_bonus += (diamond_real * ((double)bonus_percent_atm / 100));
							LogHelper.Log("bonus percent = " + bonus_percent_atm + ", bonus diamond = " + diamond_bonus);
						}
						else
						{
							diamond_bonus += (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_DIAMOND_RECEIVE_BONUS]);
						}
						
						// bonus user winphone
						try
						{
							if (!type.equals("sms") && !type.equals("SMS") && GameUtil.GetUserInfo(userid).getDeviceOS().equals("WINPHONE"))
							{
								String paid_count = GameUtil.GetUserMisc(userid).Get("paid_count");
								if (paid_count.equals("")) // first time user winphone
								{
									diamond_bonus = diamond_bonus + (int)(diamond_real * 0.5); // bonus 50%
									GameUtil.GetUserMisc(userid).Set("paid_count", "1");
									LogHelper.Log("First time paying, diamond bonus := " + (int)(diamond_real * 0.5));
								}
								else
								{
									String start_bonus_lumina = "";
									String end_bonus_lumina = "";
									Object obj1 = DBConnector.GetMembaseServerForTemporaryData().Get("start_bonus_payment_lumina");
									Object obj2 = DBConnector.GetMembaseServerForTemporaryData().Get("end_bonus_payment_lumina");
									
									if (obj1 != null && obj2 != null)
									{
										start_bonus_lumina = (String)obj1;
										end_bonus_lumina = (String)obj2;
										if (Misc.InEvent(start_bonus_lumina, end_bonus_lumina) && Integer.parseInt(paid_count) == 1)
										{
											diamond_bonus = diamond_bonus + (int)(diamond_real * 0.3); // bonus 30%
											GameUtil.GetUserMisc(userid).Set("paid_count", "2");
											LogHelper.Log("Second time paying, diamond bonus := " + (int)(diamond_real * 0.3));
										}
									}
								}
							}
						}
						catch (Exception e)
						{
							LogHelper.LogException("BonusUserWinphone", e);
						}

						int diamond_user_charged = diamond_real + diamond_bonus;
						LogHelper.Log("diamond_bonus 1:= " + diamond_bonus);
						/* Get bonus from special offer */
						if (has_special_offer && _offer != null)
						{
							LogHelper.Log("has_special_offer");
							try
							{
								if(_offer.isOfferring() && _offer.getOfferType() == DatabaseID.OFFER_CONTENT_DIAMOND)
								{
									LogHelper.Log("_offer.getOfferContent() := " + _offer.getOfferContent());
									String[] offercontent = _offer.getOfferContent().split(":");
									if (_is_ATM) {
										for(int k = 0; k < (offercontent.length - 1); k+=2)
										{
											if(Integer.parseInt(offercontent[k]) == 49 || Integer.parseInt(offercontent[k]) == 50)
											{
												// if offering ATM, recalculate the diamond bonus value
												int offer_bonus_atm = Integer.parseInt(offercontent[k+1]);
												LogHelper.Log("offer_bonus_atm := " + offer_bonus_atm);
												
												diamond_bonus = (int)(diamond_real * ((double)offer_bonus_atm / 100));
												LogHelper.Log("diamond_bonus atm:= " + diamond_bonus);
												
												_offer.setOfferAccept(true,CommandID.CMD_REFILL_CARD,userInfo);
												break;
											}
										}
									}
									else
									{
										for(int k = 0; k < (offercontent.length - 1); k+=2)
										{
											if(Integer.parseInt(offercontent[k]) == i)
											{
												diamond_bonus += Integer.parseInt(offercontent[k+1]);
												LogHelper.Log("offer diamond_bonus := " + diamond_bonus);
												_offer.setOfferAccept(true,CommandID.CMD_REFILL_CARD,userInfo);
												break;
											}
										}
									}
									
									if (_offer.getOfferName().equals("firstpay")) {
										offer_firstpay_gift = _offer.getOfferDescription(); // cheat: use offer description as gift for first pay
										LogHelper.Log("_offer.getOfferDescription() := " + _offer.getOfferDescription());
									}
									
									if(_offer.isChange())
									{
										_offer.setChange(false);
										DBConnector.GetMembaseServer(userid).SetRaw(userid + "_" + KeyID.KEY_SPECIAL_OFFER,_offer.getData());
									}
								}
							}
							catch (Exception e)
							{
								LogHelper.LogException("PaymentHanlder.add_offer", e);
							}
						}						
						
						log_iGameCoin = diamond_real;
						log_iPromotionCoin = diamond_bonus;

						MoneyManager moneyManager = new MoneyManager(Long.toString(userid));
						moneyManager.SetDatabase(DBConnector.GetMembaseServer(userid));
						
						if (moneyManager.LoadFromDatabase(KeyID.KEY_MONEY_REAL, KeyID.KEY_MONEY_TOTAL, KeyID.KEY_MONEY_BONUS))
						{
							log_iGameCoinBefore = moneyManager.GetRealMoney();
							log_iPromotionCoinBefore = moneyManager.GetBonusMoney();

							if (diamond_bonus == 0 || moneyManager.IncreaseBonusMoney(diamond_bonus, jsonObject.toString()))
							{
								if (moneyManager.IncreaseRealMoney(diamond_real, jsonObject.toString()))
								{
									log_iGameCoinAfter = moneyManager.GetRealMoney();
									log_iPromotionCoinAfter = moneyManager.GetBonusMoney();

									transactionresult = TRANSACTION_OK;
									LogHelper.Log("PaymentHanlder.. transaction ok.");

									// log IFRS
									StringBuilder ifrs = new StringBuilder();
									ifrs.append(userid); // [Account]
									ifrs.append(',').append(log_iGameCoinAfter); // [xu_nap_ton]
									ifrs.append(',').append(log_iPromotionCoinAfter); // [xu_thuong_ton]
									ifrs.append(',').append(System.currentTimeMillis()); // [unix_time]
									ifrs.append(',').append(0); // server id
									ifrs.append(',').append(log_iGameCoin); // [xu_nap_gd]
									ifrs.append(',').append(log_iPromotionCoin); // [xu_thuong_gd]
									ifrs.append(',').append("SO6" + "_" + type); // Item_ID
									ifrs.append(',').append(transactionid); // Action_ID
									ifrs.append(',').append(gross); // [gia_donvi]
									ifrs.append(',').append(gross); // [Gross_revenue]
									ifrs.append(',').append(net); // [Net_revenue]
									ifrs.append(',').append("mobile"); // [platform]
									
									if (ProjectConfig.IS_SERVER_PAYMENT == 1)
										LogHelper.Log(LogHelper.LogType.IFRS, ifrs.toString());
									else if (ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1)
										LogHelper.Log(ifrs.toString());
									
									// log VD
									StringBuilder log2 = new StringBuilder();
									log2.append(Misc.getCurrentDateTime());
									log2.append('\t').append(userid); // 
									log2.append('\t').append(4); // payment gateway, 4 means SO6
									log2.append('\t').append(log_chargeType);
									log2.append('\t').append(transactionid);
									log2.append('\t').append(log_sUserIP); // TODO: get phone number from SO6
									log2.append('\t').append(gross); // so tien user tra
									log2.append('\t').append(net); // so tien game nhan
									log2.append('\t').append(log_iGameCoin + log_iPromotionCoin); // tong so game coin nhan duoc
									log2.append('\t').append(log_iGameCoin);  // so game coin nhan
									log2.append('\t').append(log_iPromotionCoin); // so game coin duoc bonus
									log2.append('\t').append(log_iGameCoinAfter + log_iPromotionCoinAfter); // tong game coin sau khi nap
									log2.append('\t').append(log_iGameCoinAfter); // game coin bonus sau khi nap
									log2.append('\t').append(log_iPromotionCoinAfter);
									if (!Strings.isNullOrEmpty(offer_firstpay_gift) && !offer_firstpay_gift.equals("null")) {
										log2.append('\t').append("REFILL" + " " + type + " " + operator + " firstpay" ); // description.
									} else {
										log2.append('\t').append("REFILL" + " " + type + " " + operator); // description.
									}
									
									log2.append('\t').append(transactionresult);
									log2.append('\t').append(userid);
									log2.append('\t').append(log_sUsername);
									log2.append('\t').append(0);
									log2.append('\t').append(log_iLevel);

									if (ProjectConfig.IS_SERVER_PAYMENT == 1)
										LogHelper.Log(LogHelper.LogType.PAYING, log2.toString());
									else if (ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1)
										LogHelper.Log(log2.toString());
									
									// LOG PIG
									StringBuilder piglog = new StringBuilder();
									piglog.append(Misc.getCurrentDateTime());		//  1. thoi gian dang nhap
									piglog.append('\t').append("AddMoney");
									piglog.append('\t').append(userInfo.getPigID());
									piglog.append('\t').append("CGMFBS");
									piglog.append('\t').append(SkyGarden._server_id);
									piglog.append('\t').append("");
									piglog.append('\t').append(userid);
									piglog.append('\t').append(transactionid);
									piglog.append('\t').append("SO6" + "_" + type);
									piglog.append('\t').append("SO6");
									piglog.append('\t').append(type);
									piglog.append('\t').append(userInfo.GetUserIP());
									piglog.append('\t').append(gross);
									piglog.append('\t').append(net);
									LogHelper.Log(LogHelper.LogType.PIG_LOG, piglog.toString());
									
									if (!Strings.isNullOrEmpty(offer_firstpay_gift) && !offer_firstpay_gift.equals("null")) {
										StringBuilder log = new StringBuilder();
										log.append(Misc.getCurrentDateTime());								//  1. thoi gian tieu tien
										log.append('\t').append("AcceptOfferFirstPay");			//  2. hanh dong cua gamer
										log.append('\t').append(userInfo.getID());							//  3. id
										log.append('\t').append(userInfo.getID());							//  4. role id
										log.append('\t').append(userInfo.getName());						//  5. name
										log.append('\t').append(SkyGarden._server_id);											//  6. server id
										log.append('\t').append(userInfo.getLevel());						//  7. level
										LogHelper.Log(LogHelper.LogType.TRACKING_ACTION, log.toString());
									}
									
									if (_promotion_first_pay) {
										try
										{
											if (last_pay != null)
											{
												if (last_pay.equals("null")) // first time paying
												{
													GiftManager gift_mgr = new GiftManager(Long.toString(userid));
													gift_mgr.SetDatabase(DBConnector.GetMembaseServer(userid));
													if (gift_mgr.LoadFromDatabase(KeyID.KEY_GIFT))
													{
														String gift_name		= Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][7][DatabaseID.GIFT_INFO_NAME]);
														String gift_description = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][7][DatabaseID.GIFT_INFO_DESCRIPTION]);
														String gift_items		= "";

														if (gross <= 20000)
															gift_items			= Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_FIRST_PAY][0][DatabaseID.FIRSTPAY_GIFT_PACK_1]);
														else
															gift_items			= Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_FIRST_PAY][0][DatabaseID.FIRSTPAY_GIFT_PACK_2]);

														gift_mgr.AddGiftBox(gift_name, gift_description, gift_items);
														gift_mgr.SaveDataToDatabase(KeyID.KEY_GIFT);

														StringBuilder log = new StringBuilder();
														log.append(Misc.getCurrentDateTime());					//  1. log time
														log.append('\t').append(CommandID.CMD_REFILL_CARD);		//  2. action name
														log.append('\t').append(userid);						//  3. account name
														log.append('\t').append(userid);						//  4. role id
														log.append('\t').append(log_sUsername);					//  5. role name
														log.append('\t').append("1");							//  6. server id
														log.append('\t').append(log_iLevel);					//  7. user level
														log.append('\t').append(log_sUserIP);					//  8. user ip
														log.append('\t').append(gift_name);						//  9. user gift code
														log.append('\t').append(gift_description);				//  10. user gift code
														log.append('\t').append(gift_items);					//  11. user gift code

														if (ProjectConfig.IS_SERVER_PAYMENT == 1)
															LogHelper.Log(LogHelper.LogType.GIFT_BOX, log.toString());
														else if (ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1)
															LogHelper.Log(log.toString());

														LogHelper.Log("PaymentHanlder.. add gift first pay done.");
													}
													else
														LogHelper.Log("PaymentHanlder.. err! can not load gift manager");

													DBConnector.GetMembaseServer(userid).Set(userid + "_" + KeyID.KEY_LAST_PAY_TIME, Misc.getCurrentDateTime() + "|" + gross + "|" + 1);
												}
												else
												{
													LogHelper.Log("PaymentHanlder.. not first pay, with last pay info =" + last_pay);
													DBConnector.GetMembaseServer(userid).Set(userid + "_" + KeyID.KEY_LAST_PAY_TIME, Misc.getCurrentDateTime() + "|" + gross + "|" + 0);
												}
											}
											else
												LogHelper.Log("PaymentHanlder.. err! null key last pay");
										}
										catch (Exception e)
										{
											LogHelper.LogException("PaymentHanlder.AddGiftFirstPay", e);
										}
									}
									
									// add gift first pay
									if (!Strings.isNullOrEmpty(offer_firstpay_gift) && !offer_firstpay_gift.equals("null")) {
										GiftManager gift_mgr = new GiftManager(Long.toString(userid));
										gift_mgr.SetDatabase(DBConnector.GetMembaseServer(userid));
										if (gift_mgr.LoadFromDatabase(KeyID.KEY_GIFT)) {
											String gift_name		= Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][7][DatabaseID.GIFT_INFO_NAME]);
											String gift_description = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][7][DatabaseID.GIFT_INFO_DESCRIPTION]);
											String gift_items		= offer_firstpay_gift;
											gift_mgr.AddGiftBox(gift_name, gift_description, gift_items);
											gift_mgr.SaveDataToDatabase(KeyID.KEY_GIFT);

											StringBuilder log = new StringBuilder();
											log.append(Misc.getCurrentDateTime());					//  1. log time
											log.append('\t').append(CommandID.CMD_REFILL_CARD);		//  2. action name
											log.append('\t').append(userid);						//  3. account name
											log.append('\t').append(userid);						//  4. role id
											log.append('\t').append(log_sUsername);					//  5. role name
											log.append('\t').append("1");							//  6. server id
											log.append('\t').append(log_iLevel);					//  7. user level
											log.append('\t').append(log_sUserIP);					//  8. user ip
											log.append('\t').append(gift_name);						//  9. user gift code
											log.append('\t').append(gift_description);				//  10. user gift code
											log.append('\t').append(gift_items);					//  11. user gift code

											if (ProjectConfig.IS_SERVER_PAYMENT == 1 || ProjectConfig.IS_SERVER_ZALO_PAYMENT == 1)
												LogHelper.Log(LogHelper.LogType.GIFT_BOX, log.toString());
											else if (ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1)
												LogHelper.Log(log.toString());

											// key bonus order
											DBConnector.GetMembaseServer(userid).Add(userid + "_" + "firstpay_bonus_order", Misc.SECONDS() + 3 * 24 * 60 * 60, 3 * 24 * 60 * 60);
											LogHelper.Log("Add gift offer first pay done := " + gift_items);
										}
									}
									
									if(has_special_offer && _offer != null && _offer.isOfferring() && _offer.getOfferType() == DatabaseID.OFFER_CONTENT_CASHIN)//nap xu dc qua
									{
										String[] offercontent = _offer.getOfferContent().split(":");
										int cashin = Integer.parseInt(offercontent[2]);
										String s_offer = Misc.stringCombine(offercontent, 3, ":");
										if (diamond_user_charged >= cashin && s_offer != null) // nap kc dc qua`
										{
											GiftManager gift_mgr = new GiftManager(Long.toString(userid));
											gift_mgr.SetDatabase(DBConnector.GetMembaseServer(userid));
											if (gift_mgr.LoadFromDatabase(KeyID.KEY_GIFT))
											{
												String gift_name		= _offer.getOfferName();
												String gift_description = _offer.getOfferDescription();
												String gift_items		= s_offer;

												gift_mgr.AddGiftBox(gift_name, gift_description, gift_items);
												gift_mgr.SaveDataToDatabase(KeyID.KEY_GIFT);

												_offer.setOfferAccept(true,CommandID.CMD_REFILL_CARD,userInfo);

												if(_offer.isChange())
												{
													_offer.setChange(false);//saved not need change
													DBConnector.GetMembaseServer(userid).SetRaw(userid + "_" + KeyID.KEY_SPECIAL_OFFER,_offer.getData());
												}

												StringBuilder log = new StringBuilder();
												 log.append(Misc.getCurrentDateTime());					//  1. log time
												 log.append('\t').append(CommandID.CMD_REFILL_CARD);		//  2. action name
												 log.append('\t').append(userid);						//  3. account name
												 log.append('\t').append(userid);						//  4. role id
												 log.append('\t').append(log_sUsername);					//  5. role name
												 log.append('\t').append("1");							//  6. server id
												 log.append('\t').append(log_iLevel);					//  7. user level
												 log.append('\t').append(log_sUserIP);					//  8. user ip
												 log.append('\t').append(gift_name);						//  9. user gift code
												 log.append('\t').append(gift_description);				//  10. user gift code
												 log.append('\t').append(gift_items);					//  11. user gift code

												if (ProjectConfig.IS_SERVER_PAYMENT == 1)
													LogHelper.Log(LogHelper.LogType.GIFT_BOX, log.toString());
												else if (ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1)
													LogHelper.Log(log.toString());

												LogHelper.Log("PaymentHanlder.. add gift offer done. gross value " +gross);
											}
											else
												LogHelper.Log("PaymentHanlder.. err! can not load gift manager");
										}
										else
										{
											LogHelper.Log("PaymentHanlder.. Cash in not enough " + last_pay);
										}
									}
									
									// save last pay info
									String last_pay_info = Misc.getCurrentDateTime() + "|" + gross + "|" + 0;
									DBConnector.GetMembaseServer(userid).Set(userid + "_" + KeyID.KEY_LAST_PAY_TIME, last_pay_info);
									LogHelper.Log("PaymentHanlder.. saved last pay info = " + last_pay_info);
									
									// save total paid
									total_paid += gross;
									DBConnector.GetMembaseServer(userid).Set(userid + "_" + KeyID.KEY_TOTAL_PAID, total_paid);
									LogHelper.Log("PaymentHanlder.. saved total paid = " + total_paid);
								}
								else
								{
									// decrease money
									moneyManager.UseRealMoneyAndBonusMoney(diamond_bonus, CommandID.CMD_REFILL_CARD, log_sUsername, log_iLevel, log_sUserIP, -1, -1, "RefillError", diamond_bonus, 1);

									transactionresult = TRANSACTION_EXCEPTION;
									LogHelper.Log("PaymentHandler.. err! add money real fail");
								}
							}
							else
							{
								transactionresult = TRANSACTION_EXCEPTION;
								LogHelper.Log("PaymentHandler.. err! add money bonus fail");
							}
						}
						else
						{
							transactionresult = TRANSACTION_WRONG_UID;
							LogHelper.Log("PaymentHandler.. err! can't load money manager");
						}

						break;
					}
				}
			}
		}
		else
		{
			if (transactionresult == TRANSACTION_OK)
				transactionresult = TRANSACTION_EXCEPTION;
			
			StringBuilder mess = new StringBuilder();
			mess.append("PaymentHanlder.. err TRANSACTION_EXCEPTION with details: ");
			mess.append("load_user_info=").append(load_result);
			mess.append("transactionresult=").append(transactionresult);
			mess.append("&is_valid_sig=").append(IsValidSig(sig));
			mess.append("&msql_transaction_exist=").append(is_transaction_exist);
			LogHelper.Log(mess.toString());
		}
		
		// build response object
		JSONObject res = new JSONObject();
		res.put("TransactionID", transactionid);
		res.put("UserID", s_userid);
		
		switch (transactionresult)
		{
			case TRANSACTION_OK:
				res.put("ErrorCode", "1");
				break;
			case TRANSACTION_WRONG_SIG:
				res.put("ErrorCode", "-2");
				break;
			case TRANSACTION_WRONG_UID:
				res.put("ErrorCode", "-1");
				break;
			case TRANSACTION_EXCEPTION:
				res.put("ErrorCode", "-6");
				break;
			default:
				res.put("ErrorCode", "-6");
				break;
		}
		
		// log
		StringBuilder log = new StringBuilder();
		log.append(jsonObject.toString());
		log.append('\t').append(res.toString());
//		LogHelper.Log(log.toString());
		
		// response to so6
		try
		{
			_client.write(res.toString());
			LogHelper.Log("CardPaymentHandler.. response to SO6 with json=" + log.toString());
		}
		catch (Exception e)
		{
			LogHelper.LogException("CardPaymentHandler.response", e);
		}
		
		// refresh client's new diamond
		if (type.equals("SMS") || type.equals("sms") || _is_ATM)
		{
			UpdateClientMoney();
		}
		
		// insert log to mysql
		if (ProjectConfig.IS_SERVER_PAYMENT == 1)
		{
			try
			{
				int add_sql = SkyGarden._sql_connector.InsertTransactionLog(transactionid, Misc.getCurrentDateTime(), "" + gross, "" + (net == -1 ? "None" : net), operator, type, "" + userid, "" + transactionresult, "" + log_iGameCoin, "" + log_iPromotionCoin, "" + log_iGameCoinBefore, "" + log_iPromotionCoinBefore, "" + log_iGameCoinAfter, "" + log_iPromotionCoinAfter);
				LogHelper.Log("Add MySQL log: " + add_sql);
			}
			catch (Exception e)
			{
				LogHelper.LogException("CardPaymentHandler.InsertMySQLLog", e);
			}
		}
	}
	
	private boolean IsValidSig(String sig)
	{
		if (TEST_MODE) return true;
		
		return sig.equals(Misc.Hash(transactionid + type + s_userid + s_gross + (net == -1 ? "None" : net) + ProjectConfig.SECRECT_KEY, "MD5"));
	}
	
	private void UpdateClientMoney()
	{
		// check if user online and at which server
		String online_info = "";
		try
		{
			online_info = (String)DBConnector.GetMembaseServer(userid).Get(userid + "_" + KeyID.ONLINE);
		}
		catch (Exception e)
		{
			online_info = "";
			LogHelper.LogException("UpdateClientMoney.GetOnlineInfo", e);
		}
		
		if (online_info == null || online_info.equals(""))
		{
			LogHelper.Log("UpdateClientMoney.. user is offline.");
		}
		else
		{
			
			String user_ip = online_info.split(":")[0];
			
			FBEncrypt udpreq = new FBEncrypt();
			udpreq.addShort(KeyID.KEY_USER_COMMAND_ID, CommandID.CMD_UDP_REFRESH_DIAMOND);
			udpreq.addLong(KeyID.KEY_USER_ID, userid);
			udpreq.addInt(KeyID.KEY_PAYMENT_RESULT, transactionresult);

			ByteBuffer buffer = ByteBuffer.allocate(udpreq.toByteArray().length + 8);
			buffer.put(udpreq.toByteArray());
			buffer.flip();

			udpRequest(buffer, user_ip, ProjectConfig.UDP_PORT, false);
		}
	}
	
	private void udpRequest(ByteBuffer buffer, String ip, int port, boolean receive)
	{
		if (true)
		{
			try
			{
				simpleUDP(buffer, ip, port);
			}
			catch (Exception e)
			{
				LogHelper.LogException("udpRequest.simpleUDP", e);
			}
		}
		else
		{
			try
			{
	//			LogHelper.Log("udpRequest " + buffer.remaining() + " to " +ip + ":" + port + " receive=" + receive);
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
						LogHelper.Log("[Retry] count=" + count + " buffer=" + buffer);
					}
					while (buffer.position() == 0 && count < /*MAX_RETRY_UDPRECEIVE*/3);

					LogHelper.Log("[after]" + buffer);
					buffer.flip();
					LogHelper.Log("[udpRequest]" + buffer);

					short i = buffer.getShort();
					LogHelper.Log("i = " + i);
					int j = buffer.getInt();
					LogHelper.Log("j = " + j);
				}
				channel.close();
			}
			catch(Exception e)
			{
				LogHelper.LogException("udpRequest.udpRequest", e);
				if(receive)
					buffer.flip();
			}
		}
		
	}
	
	private int simpleUDP(ByteBuffer input, String ip, int port) throws IOException
    {
        try (DatagramChannel channel = DatagramChannel.open())
        {
            InetSocketAddress address = new InetSocketAddress(ip, port);            
            channel.configureBlocking(false);
            channel.connect(address);
            return channel.send(input, address);
        }
    }
}
