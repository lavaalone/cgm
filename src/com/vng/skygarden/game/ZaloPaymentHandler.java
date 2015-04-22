/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vng.log.LogHelper;
import com.vng.nettyhttp.*;
import com.vng.netty.Server;
import com.vng.skygarden.*;
import com.vng.skygarden._gen_.ProjectConfig;
import static com.vng.skygarden.game.SkyGardenUser.simpleUDP;
import com.vng.util.FBEncrypt;
import com.vng.util.Misc;
import io.netty.util.CharsetUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.sql.*;
import java.util.HashMap;

/* 
 * Sample:
 * amount=10000&items=&embedData=1088754628940856268&zacTranxID=ede1ce07f2421b1c4253&mac=bf74d738891c4681a9654cd14d3d9830690ab13123fac5bdc1cff23e7ad18dcf&appTime=1407749794526&zacServerTime=1407749841769&channel=122&appTranxID=dd6f4552-466c-42e2-9880-b4351b7b3c16&appID=4276041421271731779
 * Channel:
	Sandbox: 0
	Zing card : 100
	Zing Xu : 101
	Vinaphone telco card : 121
	Mobifone telco card : 122
	Viettel telco card : 123
	SMS : 160
	ATM: 181
	Google Wallet: 220
	App in-app purchase: 221

 */

public class ZaloPaymentHandler
{
	Client _client = null;
	String _data = null;
	
	long	_app_id;				// 3rd app id
	String	_app_transaction_id;	// unique identifier for payment request created by application
	long	_app_time;				// 3rd app timestamp in millisecond when order is made
	String	_items;					// an List of ZaloPaymentItem
	long	_amount;				// total amount of the order
	String	_embedded_data;			// the extra data provided by application (signature or security info)
	String	_zac_transaction_id;	// ZaloPay transaction ID
	long	_zac_server_time;		// ZaloPay server timestamp in millisecond when order is made
	String	_mac;					// signature of the order.
	String	_description;
	String	_channel;
	
	long _user_id;
	String _ip;
	int _server_id;
	int _pack_id;
	int _pack_price;
	String _pack_name;
	int _pack_quality;
	int _gross;
	int _net;
	
	boolean _is_SMS = false;
	boolean _is_ATM = false;
	private int ATM_RATIO = 100; // 100 vnd = 1 diamond
	
	private final int TRANSACTION_OK = 0;
	private final int TRANSACTION_WRONG_SIG = 1;
	private final int TRANSACTION_WRONG_UID = 2;
	private final int TRANSACTION_EXCEPTION = 3;
	private String _error_message = "";
	
	private int transactionresult = TRANSACTION_OK;
	
	boolean _promotion_first_pay = false;
	private final boolean TEST_MODE = !true;
	private String offer_firstpay_gift = "";
	
	private String s_gross = "";
	private HashMap<String, String> _params = new HashMap<String, String>();
	
	
	public ZaloPaymentHandler(Client client, String data)
	{
		_client = client;
		_data = data;
	}
	
	public void Execute() // throws Exception
	{
		_params = GetParams(_data);
		LogHelper.Log("params = " + _params);
		
		boolean get_params_ok = false;
		try
		{
			_app_id					= Long.parseLong(_params.get("appID"));
			_app_transaction_id		= _params.get("appTranxID");
			_app_time				= Long.parseLong(_params.get("appTime"));
			_items					= (String)_params.get("items");
			_amount					= Long.parseLong(_params.get("amount"));
			_embedded_data			= _params.get("embedData"); // user_id
			_zac_transaction_id		= _params.get("zacTranxID");
			_zac_server_time		= Long.parseLong(_params.get("zacServerTime"));
			_mac					= _params.get("mac");
			_channel				= _params.get("channel");
			
			_user_id = Long.parseLong(_embedded_data);
			_gross = (int)_amount;
			get_params_ok			= true;
		}
		catch (Exception e)
		{
			LogHelper.LogException("ZaloPaymentHandler.GetPaymentParams", e);
			get_params_ok = false;
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
		
		// get user info before adding money
		boolean load_result = false;
		byte[] userbin = null;
		try
		{
			userbin = DBConnector.GetMembaseServer(_user_id).GetRaw(_user_id + "_" + KeyID.KEY_USER_INFOS);
		}
		catch (Exception e)
		{
			userbin = null;
			LogHelper.Log("CardPaymentHandler.. can not get user info data.");
			LogHelper.LogException("CardPaymentHandler.LoadUserData", e);
		}
		
		UserInfo userInfo = GameUtil.GetUserInfo(_user_id);
		if (userbin != null)
		{
			log_iLevel = userInfo.getLevel();
			log_sUsername = userInfo.getName();
			log_sUserIP = userInfo.GetUserIP();
			load_result = true;
		}
		
		if (!load_result) 
		{
			transactionresult = TRANSACTION_WRONG_UID;
			_error_message = "wrong_uid";
		}
		
		// get last pay info
		String last_pay = "";
		try
		{
			last_pay = (String)DBConnector.GetMembaseServer(_user_id).Get(_user_id + "_" + KeyID.KEY_LAST_PAY_TIME);
			
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
			total_paid = (int)DBConnector.GetMembaseServer(_user_id).Get(_user_id + "_" + KeyID.KEY_TOTAL_PAID);
		} catch (Exception e) 
		{
			total_paid = 0;
		}
		
		/* check if first pay campaign is running */
		try 
		{
			Object obj = DBConnector.GetMembaseServerForTemporaryData().Get("payment_first_pay_str");
			if (obj != null) {
				_promotion_first_pay = ((String)obj).equals("ON");
			}
		} 
		catch (Exception e) 
		{
			LogHelper.LogException("LoadFirstPay", e);
		}
		LogHelper.Log("_promotion_first_pay := " + _promotion_first_pay);
		
		if (transactionresult == TRANSACTION_OK)
		{
			if (!IsValidSig(_mac))
			{
				transactionresult = TRANSACTION_WRONG_SIG;
				_error_message = "wrong_mac";
			}
		}
		
		boolean is_transaction_exist = true;
		try 
		{
			is_transaction_exist = IsTransactionExist(_zac_transaction_id);
		} 
		catch (Exception e) 
		{
			is_transaction_exist = true;
			LogHelper.LogException("CardPaymentHandler.CheckTransactionExist", e);
		}
		
		if (_channel.equals("160")) {
			_is_SMS = true;
		} else if (_channel.equals("181")) {
			_is_ATM = true;
		}
		
		LogHelper.Log("channel := " + _channel + ", _is_SMS := " + _is_SMS + ", _is_ATM := " + _is_ATM);
		
		if (load_result && (transactionresult == TRANSACTION_OK) && !is_transaction_exist)
		{
			//read offer info
			boolean has_special_offer = true;
			byte[] offerbin = null;
			try 
			{
				offerbin = DBConnector.GetMembaseServer(_user_id).GetRaw(_user_id + "_" + KeyID.KEY_SPECIAL_OFFER);
			} 
			catch (Exception e) 
			{
				offerbin = null;
				has_special_offer = false;
				LogHelper.LogException("CardPaymentHandler.LoadSpecialOffer", e);
			}
			
			Special_Offer _offer = null;
			if (has_special_offer &&  offerbin != null && offerbin.length > 0) 
			{
				_offer = new Special_Offer(offerbin);
			}
			//-------------------------------------------------------------
			
			// base on _gross, decides how many diamond will be added to user's money
			for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_PAYMENT].length; i++)
			{
				int v = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_GROSS_AMOUNT]);
				String s = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_OPERATOR]);
				
				if (_gross == v || _is_ATM)
				{
					// handle SMS
					if (s.equals("SMS")) {
						if (!_is_SMS) {
							continue;
						}
					}
					
					int diamond_real = 0;
					if (_is_ATM) {
						diamond_real = _gross / ATM_RATIO;
					} else {
						diamond_real = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_DIAMOND_RECEIVE_REAL]);
					}
					
					LogHelper.Log("PaymentHanlder.. diamond_real = " + diamond_real);
					
					int diamond_bonus = 0;

					if (_is_ATM) {
						// bonus from ATM
						int bonus_percent_atm = 0;
						for (int j = 0; j < Server.s_globalDB[DatabaseID.SHEET_PAYMENT].length; j++) {
							String atm_operator = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][j][DatabaseID.PAYMENT_OPERATOR]);
							if (atm_operator.equals("ATM")) {
								int atm_gross = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][j][DatabaseID.PAYMENT_GROSS_AMOUNT]);
								if (_gross < atm_gross) {
									bonus_percent_atm = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][j][DatabaseID.PAYMENT_DIAMOND_RECEIVE_BONUS]);
									break;
								}
							}
						}
						
						diamond_bonus += (diamond_real * ((double)bonus_percent_atm / 100));
						LogHelper.Log("bonus percent atm := " + bonus_percent_atm + " => bonus diamond := " + diamond_bonus);
					} else {
						// get bonus from order promotions
						diamond_bonus += (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_DIAMOND_RECEIVE_BONUS]);
					}
					
					// get bonus from first time paying
					if (_promotion_first_pay) {
						if (last_pay != null && last_pay.equals("null")) {
							diamond_bonus += (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_DIAMOND_RECEIVE_BONUS_FIRST_PAY]);
						}
					}

					int diamond_user_charged = diamond_real + diamond_bonus;
					
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
								} else {
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
									_offer.setChange(false);//saved not need change
									DBConnector.GetMembaseServer(_user_id).SetRaw(_user_id + "_" + KeyID.KEY_SPECIAL_OFFER,_offer.getData());
								}
							}
						}
						catch (Exception e)
						{
							LogHelper.LogException("PaymentHanlder.add_offer", e);
						}
					}						

					LogHelper.Log("PaymentHanlder.. diamond_bonus = " + diamond_bonus);

					log_iGameCoin = diamond_real;
					log_iPromotionCoin = diamond_bonus;

					MoneyManager moneyManager = new MoneyManager(Long.toString(_user_id));
					moneyManager.SetDatabase(DBConnector.GetMembaseServer(_user_id));

					if (moneyManager.LoadFromDatabase(KeyID.KEY_MONEY_REAL, KeyID.KEY_MONEY_TOTAL, KeyID.KEY_MONEY_BONUS))
					{
						log_iGameCoinBefore = moneyManager.GetRealMoney();
						log_iPromotionCoinBefore = moneyManager.GetBonusMoney();

						if (diamond_bonus == 0 || moneyManager.IncreaseBonusMoney(diamond_bonus, _data))
						{
							if (moneyManager.IncreaseRealMoney(diamond_real, _data))
							{
								log_iGameCoinAfter = moneyManager.GetRealMoney();
								log_iPromotionCoinAfter = moneyManager.GetBonusMoney();

								transactionresult = TRANSACTION_OK;
								_error_message = "transaction_ok";
								LogHelper.Log("PaymentHanlder.. transaction ok.");

								// log IFRS
								StringBuilder ifrs = new StringBuilder();
								ifrs.append(_user_id); // [Account]
								ifrs.append(',').append(log_iGameCoinAfter); // [xu_nap_ton]
								ifrs.append(',').append(log_iPromotionCoinAfter); // [xu_thuong_ton]
								ifrs.append(',').append(System.currentTimeMillis()); // [unix_time]
								ifrs.append(',').append(0); // server id
								ifrs.append(',').append(log_iGameCoin); // [xu_nap_gd]
								ifrs.append(',').append(log_iPromotionCoin); // [xu_thuong_gd]
								ifrs.append(',').append("Zalo"); // Item_ID
								ifrs.append(',').append(_zac_transaction_id); // Action_ID
								ifrs.append(',').append(_gross); // [gia_donvi]
								ifrs.append(',').append(_gross); // [Gross_revenue]
								ifrs.append(',').append(_net); // [Net_revenue]
								ifrs.append(',').append("mobile"); // [platform]

								if (ProjectConfig.IS_SERVER_PAYMENT == 1 || ProjectConfig.IS_SERVER_ZALO_PAYMENT == 1) {
									LogHelper.Log(LogHelper.LogType.IFRS, ifrs.toString());
									LogHelper.Log(ifrs.toString());
								} else if (ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1) {
									LogHelper.Log(ifrs.toString());
								}
								
								// log VD
								StringBuilder log2 = new StringBuilder();
								log2.append(Misc.getCurrentDateTime());
								log2.append('\t').append(_user_id); // 
								log2.append('\t').append(6); // payment gateway, 4 means SO6
								log2.append('\t').append(log_chargeType);
								log2.append('\t').append(_zac_transaction_id);
								log2.append('\t').append(log_sUserIP); // TODO: get phone number from SO6
								log2.append('\t').append(_gross); // so tien user tra
								log2.append('\t').append(_net); // so tien game nhan
								log2.append('\t').append(log_iGameCoin + log_iPromotionCoin); // tong so game coin nhan duoc
								log2.append('\t').append(log_iGameCoin);  // so game coin nhan
								log2.append('\t').append(log_iPromotionCoin); // so game coin duoc bonus
								log2.append('\t').append(log_iGameCoinAfter + log_iPromotionCoinAfter); // tong game coin sau khi nap
								log2.append('\t').append(log_iGameCoinAfter); // game coin bonus sau khi nap
								log2.append('\t').append(log_iPromotionCoinAfter);
								
								if (!Strings.isNullOrEmpty(offer_firstpay_gift) && !offer_firstpay_gift.equals("null")) {
									log2.append('\t').append("REFILL" + "_ZALO_" + _channel + " firstpay"); // description.
								} else {
									log2.append('\t').append("REFILL" + "_ZALO_" + _channel); // description.
								}
								
								log2.append('\t').append(transactionresult);
								log2.append('\t').append(_user_id);
								log2.append('\t').append(log_sUsername);
								log2.append('\t').append(0);
								log2.append('\t').append(log_iLevel);

								if (ProjectConfig.IS_SERVER_PAYMENT == 1 || ProjectConfig.IS_SERVER_ZALO_PAYMENT == 1) {
									LogHelper.Log(LogHelper.LogType.PAYING, log2.toString());
									LogHelper.Log(log2.toString());
								} else if (ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1) {
									LogHelper.Log(log2.toString());
								}
								
								// LOG PIG
								StringBuilder piglog = new StringBuilder();
								piglog.append(Misc.getCurrentDateTime());		//  1. thoi gian dang nhap
								piglog.append('\t').append("AddMoney");
								piglog.append('\t').append(userInfo.getPigID());
								piglog.append('\t').append("CGMFBS");
								piglog.append('\t').append(SkyGarden._server_id);
								piglog.append('\t').append("");
								piglog.append('\t').append(_user_id);
								piglog.append('\t').append(_zac_transaction_id);
								piglog.append('\t').append("ZALO" + "_" + _channel);
								piglog.append('\t').append("ZALO");
								piglog.append('\t').append(_channel);
								piglog.append('\t').append(userInfo.GetUserIP());
								piglog.append('\t').append(_gross);
								piglog.append('\t').append(_net);
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
								
								// insert record
								DBConnector.GetMembaseServerForTemporaryData().Set(_zac_transaction_id, ifrs.toString());

								if (_promotion_first_pay) {
									try {
										if (last_pay != null) {
											// first time paying
											if (last_pay.equals("null")) {
												GiftManager gift_mgr = new GiftManager(Long.toString(_user_id));
												gift_mgr.SetDatabase(DBConnector.GetMembaseServer(_user_id));
												if (gift_mgr.LoadFromDatabase(KeyID.KEY_GIFT)) {
													String gift_name		= Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][7][DatabaseID.GIFT_INFO_NAME]);
													String gift_description = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][7][DatabaseID.GIFT_INFO_DESCRIPTION]);
													String gift_items		= "";

													if (_gross <= 20000)
														gift_items			= Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_FIRST_PAY][0][DatabaseID.FIRSTPAY_GIFT_PACK_1]);
													else
														gift_items			= Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_FIRST_PAY][0][DatabaseID.FIRSTPAY_GIFT_PACK_2]);

													gift_mgr.AddGiftBox(gift_name, gift_description, gift_items);
													gift_mgr.SaveDataToDatabase(KeyID.KEY_GIFT);

													StringBuilder log = new StringBuilder();
													log.append(Misc.getCurrentDateTime());					//  1. log time
													log.append('\t').append(CommandID.CMD_REFILL_CARD);		//  2. action name
													log.append('\t').append(_user_id);						//  3. account name
													log.append('\t').append(_user_id);						//  4. role id
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

													LogHelper.Log("PaymentHanlder.. add gift first pay done.");
												}
												else
													LogHelper.Log("PaymentHanlder.. err! can not load gift manager");

												DBConnector.GetMembaseServer(_user_id).Set(_user_id + "_" + KeyID.KEY_LAST_PAY_TIME, Misc.getCurrentDateTime() + "|" + _gross + "|" + 1);
											} else {
												LogHelper.Log("PaymentHanlder.. not first pay, with last pay info =" + last_pay);
												DBConnector.GetMembaseServer(_user_id).Set(_user_id + "_" + KeyID.KEY_LAST_PAY_TIME, Misc.getCurrentDateTime() + "|" + _gross + "|" + 0);
											}
										}
										else
											LogHelper.Log("PaymentHanlder.. err! null key last pay");
									} catch (Exception e) {
										LogHelper.LogException("PaymentHanlder.AddGiftFirstPay", e);
									}
								}
								
//								// add gift first pay
								if (!Strings.isNullOrEmpty(offer_firstpay_gift) && !offer_firstpay_gift.equals("null")) {
									GiftManager gift_mgr = new GiftManager(Long.toString(_user_id));
									gift_mgr.SetDatabase(DBConnector.GetMembaseServer(_user_id));
									if (gift_mgr.LoadFromDatabase(KeyID.KEY_GIFT)) {
										String gift_name		= Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][7][DatabaseID.GIFT_INFO_NAME]);
										String gift_description = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][7][DatabaseID.GIFT_INFO_DESCRIPTION]);
										String gift_items		= offer_firstpay_gift;
										gift_mgr.AddGiftBox(gift_name, gift_description, gift_items);
										gift_mgr.SaveDataToDatabase(KeyID.KEY_GIFT);
										
										StringBuilder log = new StringBuilder();
										log.append(Misc.getCurrentDateTime());					//  1. log time
										log.append('\t').append(CommandID.CMD_REFILL_CARD);		//  2. action name
										log.append('\t').append(_user_id);						//  3. account name
										log.append('\t').append(_user_id);						//  4. role id
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
										DBConnector.GetMembaseServer(_user_id).Add(_user_id + "_" + "firstpay_bonus_order", Misc.SECONDS() + 3 * 24 * 60 * 60, 3 * 24 * 60 * 60);
										LogHelper.Log("Add gift offer first pay done := " + gift_items);
									}
								}
								
								//nap xu dc qua
								if(has_special_offer && _offer != null && _offer.isOfferring() && _offer.getOfferType() == DatabaseID.OFFER_CONTENT_CASHIN) {
									String[] offercontent = _offer.getOfferContent().split(":");
									int cashin = Integer.parseInt(offercontent[2]);
									String s_offer = Misc.stringCombine(offercontent, 3, ":");
									if (diamond_user_charged >= cashin && s_offer != null) // nap kc dc qua`
									{
										GiftManager gift_mgr = new GiftManager(Long.toString(_user_id));
										gift_mgr.SetDatabase(DBConnector.GetMembaseServer(_user_id));
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
												DBConnector.GetMembaseServer(_user_id).SetRaw(_user_id + "_" + KeyID.KEY_SPECIAL_OFFER,_offer.getData());
											}

											StringBuilder log = new StringBuilder();
											log.append(Misc.getCurrentDateTime());					//  1. log time
											log.append('\t').append(CommandID.CMD_REFILL_CARD);		//  2. action name
											log.append('\t').append(_user_id);						//  3. account name
											log.append('\t').append(_user_id);						//  4. role id
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

											LogHelper.Log("PaymentHanlder.. add gift offer done. gross value " +_gross);
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
								String last_pay_info = Misc.getCurrentDateTime() + "|" + _gross + "|" + 0;
								DBConnector.GetMembaseServer(_user_id).Set(_user_id + "_" + KeyID.KEY_LAST_PAY_TIME, last_pay_info);
								LogHelper.Log("PaymentHanlder.. saved last pay info = " + last_pay_info);

								// save total paid
								total_paid += _gross;
								DBConnector.GetMembaseServer(_user_id).Set(_user_id + "_" + KeyID.KEY_TOTAL_PAID, total_paid);
								LogHelper.Log("PaymentHanlder.. saved total paid = " + total_paid);
							}
							else
							{
								// decrease money
								moneyManager.UseRealMoneyAndBonusMoney(diamond_bonus, CommandID.CMD_REFILL_CARD, log_sUsername, log_iLevel, log_sUserIP, -1, -1, "RefillError", diamond_bonus, 1);

								transactionresult = TRANSACTION_EXCEPTION;
								_error_message = "transaction_exception";
								LogHelper.Log("PaymentHandler.. err! add money real fail");
							}
						}
						else
						{
							transactionresult = TRANSACTION_EXCEPTION;
							_error_message = "transaction_exception";
							LogHelper.Log("PaymentHandler.. err! add money bonus fail");
						}
					}
					else
					{
						transactionresult = TRANSACTION_WRONG_UID;
						_error_message = "wrong_uid";
						LogHelper.Log("PaymentHandler.. err! can't load money manager");
					}

					break;
				}
			}
		}
		else
		{
			if (transactionresult == TRANSACTION_OK)
			{
				transactionresult = TRANSACTION_EXCEPTION;
			}
			
			StringBuilder mess = new StringBuilder();
			mess.append("PaymentHanlder.. err TRANSACTION_EXCEPTION with details: ");
			mess.append("load_user_info=").append(load_result);
			mess.append("&transactionresult=").append(transactionresult);
			mess.append("&is_valid_sig=").append(IsValidSig(_zac_transaction_id));
			mess.append("&msql_transaction_exist=").append(is_transaction_exist);
			LogHelper.Log(mess.toString());
		}
		
		// build response object
		JSONObject res = new JSONObject();
		res.put("errorCode", transactionresult);
		res.put("errorMessage", _error_message);
		
		// log
		StringBuilder log = new StringBuilder();
		log.append(_data);
		log.append('\t').append(res.toString());
//		LogHelper.Log(log.toString());
		
		// response to zalo
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
		if (transactionresult == TRANSACTION_OK) {
			UpdateClientMoney();
		}
	}
	
	private boolean IsValidSig(String sig)
	{
//		if (_channel.equals("0")) // sandbox
//			return true;
		
		if (true) return true;
		
		// TODO: ask for another secrect key, this one is not safe
		return sig.equals(Misc.Hash(_app_id + _app_transaction_id + _amount + _pack_id + "." + _pack_name + "." + _pack_price + "." + _pack_quality + _app_time + _description + _embedded_data, "SHA-1"));
	}
	
	private boolean IsTransactionExist(String transaction_id)
	{
		String transaction_info = "";
		try
		{
			transaction_info = (String)DBConnector.GetMembaseServerForTemporaryData().Get(transaction_id);
		}
		catch (Exception e)
		{
			transaction_info = "null";
		}
		
		if (transaction_info == null || transaction_info.equals("null"))
			return false;
		
		return true;
	}
	
	private void UpdateClientMoney()
	{
		// check if user online and at which server
		LogHelper.Log("UpdateClientMoney called");
		String online_info = "";
		try
		{
			online_info = (String)DBConnector.GetMembaseServer(_user_id).Get(_user_id + "_" + KeyID.ONLINE);
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
			udpreq.addLong(KeyID.KEY_USER_ID, _user_id);
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
	
	private HashMap<String, String> GetParams(String data)
	{
		HashMap<String, String> result = new HashMap<String, String>();
		
		String[] aos = data.split("&");
		for (String s : aos)
		{
			String[] inner_aos = s.split("=");

			String key = "null_key";
			if (inner_aos.length > 0)
				key = inner_aos[0];

			String value = "null_value";
			if (inner_aos.length > 1)
				value = inner_aos[1];

			result.put(key, value);
		}
		
		return result;
	}
}
