/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;
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
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.sql.*;

public class AppleValidatingReceiptHandler
{
	Client _client = null;
	String _data = null;
	
	private String _transaction_id = "";
	
	private String sig = "";
	private String transactionid = "";
	private String type = "";
	private int gross = -1;
	private int net = -1;
	private long _user_id = -1;
	private String s_userid = "";
	private String operator = "";
	
	private final int TRANSACTION_OK = 0;
	private final int TRANSACTION_WRONG_SIG = 1;
	private final int TRANSACTION_WRONG_UID = 2;
	private final int TRANSACTION_EXCEPTION = 3;
	
	private int transactionresult = TRANSACTION_OK;
	
	boolean _promotion_first_pay = false;
	private final boolean TEST_MODE = !true;
	
	private String s_gross = "";
	private boolean _is_ATM = false;
	private int ATM_RATIO = 100; // 100 vnd = 1 diamond
	
	String _product_id = "";
	
	public AppleValidatingReceiptHandler(Client client, String data)
	{
		_client = client;
		_data = data;
	}
	
	public void Execute() // throws Exception
	{
		LogHelper.Log("data = " + _data);
		try
		{
			String[] aos = _data.split("&");
			for (String s : aos)
			{
				if (s.contains("user_id"))
				{
					_user_id = Long.parseLong(s.split("=")[1]);
				}
				else if (s.contains("transaction_id"))
				{
					_transaction_id = s.split("=")[1];
				}
			}
			LogHelper.Log("user id = " + _user_id);
			LogHelper.Log("transaction id = " + _transaction_id);
		}
		catch (Exception e)
		{
			transactionresult = TRANSACTION_WRONG_UID;
			LogHelper.LogException("ValidatingHandler.GetParams", e);
		}
		
		int log_iGameCoin = 0;
		int log_iPromotionCoin = 0;
		int log_iGameCoinBefore = 0;
		int log_iPromotionCoinBefore = 0;
		int log_iGameCoinAfter = 0;
		int log_iPromotionCoinAfter = 0;
		int log_iLevel = 0;
		int log_chargeType = 6; // 5 : android, 6: apple
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
		UserInfo userInfo = new UserInfo(_user_id);
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
			_promotion_first_pay = (int)DBConnector.GetMembaseServerForTemporaryData().Get(KeyID.KEY_PAYMENT_USE_FIRST_PAY) == 1;
		} 
		catch (Exception e) 
		{
		}
		
		// read purchase info
		try
		{
			Object obj = DBConnector.GetMembaseServer(_user_id).Get(_transaction_id);
			if (obj != null)
			{
				String s = (String)obj;
				JsonObject purchase_data = new JsonParser().parse(s).getAsJsonObject();
				LogHelper.Log("purchase_data := " + purchase_data);
				
				String status = purchase_data.get("status").toString().replace("\"", "");
				LogHelper.Log("status := " + status);
				if (status.equals("0"))
				{
					JsonObject receipt = purchase_data.getAsJsonObject("receipt");
					LogHelper.Log("receipt := " + receipt);
					
					String bundle_id = receipt.get("bid").toString().replace("\"", "");
					if (!bundle_id.equals(ProjectConfig.APP_BUNDLE_ID))
					{
						transactionresult = TRANSACTION_EXCEPTION;
						LogHelper.Log("Invalid bundle id := " + bundle_id);
					}
					
					String product_id = receipt.get("product_id").toString().replace("\"", "");
					if (product_id == null || product_id.length() == 0)
					{
						transactionresult = TRANSACTION_EXCEPTION;
						LogHelper.Log("Invalid product id := " + product_id);
					}
					
					_product_id = product_id;
				}
				else
				{
					transactionresult = TRANSACTION_EXCEPTION;
					LogHelper.Log("Invalid transaction status := " + status);
				}
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("AndroidValidatingReceiptHandler.ReadPurchaseData", e);
		}
		
		if (load_result && (transactionresult == TRANSACTION_OK))
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
			if (has_special_offer &&  offerbin != null && offerbin.length > 0) {
				_offer = new Special_Offer(offerbin);
			}
			
			// base on gross, decides how many diamond will be added to user's money
			for (int i = 0; i < Server.s_globalDB[DatabaseID.SHEET_PAYMENT].length; i++)
			{
				int v = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_GROSS_AMOUNT]);
				String s = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_OPERATOR]);
				String product_id = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_PACK_APPLE_PRODUCT_ID]);
				LogHelper.Log("_product_id := " + _product_id);
				LogHelper.Log("product_id := " + product_id);
				
				if (_product_id.equals(product_id))
				{
					int diamond_real = 0;

					if (SkyGarden.InSaleOffEvent()) 
					{
						diamond_real = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT_EVENT][i][DatabaseID.PAYMENT_DIAMOND_RECEIVE_REAL]);
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
					else
					{
						diamond_bonus += (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PAYMENT][i][DatabaseID.PAYMENT_DIAMOND_RECEIVE_BONUS]);
					}

					int diamond_user_charged = diamond_real + diamond_bonus;
					/* Get bonus from special offer */
					if (has_special_offer && _offer != null) 
					{
						try
						{
							if(_offer.isOfferring() && _offer.getOfferType() == DatabaseID.OFFER_CONTENT_DIAMOND)
							{
								String[] offercontent = _offer.getOfferContent().split(":");
								for(int k = 0; k < (offercontent.length - 1); k+=2)
								{
									if(Integer.parseInt(offercontent[k]) == i)
									{
										diamond_bonus += Integer.parseInt(offercontent[k+1]);
										_offer.setOfferAccept(true,CommandID.CMD_REFILL_CARD,userInfo);
										break;
									}
								}
								if(_offer.isChange())
								{
									_offer.setChange(false);
									DBConnector.GetMembaseServer(_user_id).SetRaw(_user_id + "_" + KeyID.KEY_SPECIAL_OFFER,_offer.getData());
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

					MoneyManager moneyManager = new MoneyManager(Long.toString(_user_id));
					moneyManager.SetDatabase(DBConnector.GetMembaseServer(_user_id));

					if (moneyManager.LoadFromDatabase(KeyID.KEY_MONEY_REAL, KeyID.KEY_MONEY_TOTAL, KeyID.KEY_MONEY_BONUS))
					{
						log_iGameCoinBefore = moneyManager.GetRealMoney();
						log_iPromotionCoinBefore = moneyManager.GetBonusMoney();

						if (diamond_bonus == 0 || moneyManager.IncreaseBonusMoney(diamond_bonus, /*jsonObject.toString()*/""))
						{
							if (moneyManager.IncreaseRealMoney(diamond_real, /*jsonObject.toString()*/""))
							{
								log_iGameCoinAfter = moneyManager.GetRealMoney();
								log_iPromotionCoinAfter = moneyManager.GetBonusMoney();

								transactionresult = TRANSACTION_OK;
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

								if (_promotion_first_pay) {
									try
									{
										if (last_pay != null)
										{
											if (last_pay.equals("null")) // first time paying
											{
												GiftManager gift_mgr = new GiftManager(Long.toString(_user_id));
												gift_mgr.SetDatabase(DBConnector.GetMembaseServer(_user_id));
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
													log.append('\t').append(_user_id);						//  3. account name
													log.append('\t').append(_user_id);						//  4. role id
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

												DBConnector.GetMembaseServer(_user_id).Set(_user_id + "_" + KeyID.KEY_LAST_PAY_TIME, Misc.getCurrentDateTime() + "|" + gross + "|" + 1);
											}
											else
											{
												LogHelper.Log("PaymentHanlder.. not first pay, with last pay info =" + last_pay);
												DBConnector.GetMembaseServer(_user_id).Set(_user_id + "_" + KeyID.KEY_LAST_PAY_TIME, Misc.getCurrentDateTime() + "|" + gross + "|" + 0);
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
								if(has_special_offer && _offer != null && _offer.isOfferring() && _offer.getOfferType() == DatabaseID.OFFER_CONTENT_CASHIN)//nap xu dc qua
								{
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
								DBConnector.GetMembaseServer(_user_id).Set(_user_id + "_" + KeyID.KEY_LAST_PAY_TIME, last_pay_info);
								LogHelper.Log("PaymentHanlder.. saved last pay info = " + last_pay_info);

								// save total paid
								total_paid += gross;
								DBConnector.GetMembaseServer(_user_id).Set(_user_id + "_" + KeyID.KEY_TOTAL_PAID, total_paid);
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
		else
		{
			if (transactionresult == TRANSACTION_OK)
				transactionresult = TRANSACTION_EXCEPTION;
			
			StringBuilder mess = new StringBuilder();
			mess.append("PaymentHanlder.. err TRANSACTION_EXCEPTION with details: ");
			mess.append("load_user_info=").append(load_result);
			mess.append("transactionresult=").append(transactionresult);
			LogHelper.Log(mess.toString());
		}
		
				// build response object
		JSONObject res = new JSONObject();
		res.put("TransactionID", _transaction_id);
		res.put("UserID", _user_id);
		if (transactionresult == TRANSACTION_OK)
		{
			res.put("Result", "Success");
		}
		else
		{
			res.put("Result", "Failed");
		}
		
		// log
		StringBuilder log = new StringBuilder();
		log.append(res.toString());
		LogHelper.Log(log.toString());
		
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
		
		// log VD
		StringBuilder log2 = new StringBuilder();
		log2.append(Misc.getCurrentDateTime());
		log2.append('\t').append(_user_id); // 
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
		log2.append('\t').append("REFILL" + " " + type + " " + operator); // description.
		log2.append('\t').append(transactionresult);
		log2.append('\t').append(_user_id);
		log2.append('\t').append(log_sUsername);
		log2.append('\t').append(0);
		log2.append('\t').append(log_iLevel);
		
		if (ProjectConfig.IS_SERVER_PAYMENT == 1)
			LogHelper.Log(LogHelper.LogType.PAYING, log2.toString());
		else if (ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1)
			LogHelper.Log(log2.toString());
	}
}
