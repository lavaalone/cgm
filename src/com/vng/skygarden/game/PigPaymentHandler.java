/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;
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
public class PigPaymentHandler
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
	
	private String s_gross = "";
	private boolean _is_ATM = false;
	private int ATM_RATIO = 100; // 100 vnd = 1 diamond
	
	private String pig_txnid;
	private String pig_deviceid;
	private String pig_gameid;
	private String pig_userid;
	private String pig_amount;
	private String pig_type;
	private String pig_desc;
	private String pig_addinfo;
	private String pig_sig;
	
	public PigPaymentHandler(Client client, String data)
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
			LogHelper.LogException("PigPaymentHandler.parse_data", e);
		}
		
		try
		{
			pig_txnid		= (String)jsonObject.get("txnid");
			pig_deviceid	= (String)jsonObject.get("deviceid");
			pig_gameid		= (String)jsonObject.get("gameid");
			pig_userid		= (String)jsonObject.get("userid");
			pig_amount		= (String)jsonObject.get("amount");
			pig_type		= (String)jsonObject.get("type");
			pig_desc		= (String)jsonObject.get("desc");
			pig_addinfo		= (String)jsonObject.get("addinfo");
			pig_sig			= (String)jsonObject.get("sig");
		}
		catch (Exception e)
		{
			LogHelper.LogException("PigPaymentHandler.GetPaymentParams", e);
		}
		
		// in case user inpust uid as text in sms
		try 
		{
			userid = Long.parseLong(pig_userid);
		}
		catch (Exception e) 
		{
			transactionresult = TRANSACTION_WRONG_UID;
			LogHelper.LogException("PigPaymentHandler.GetUserID", e);
		}
		
		LogHelper.Log("pig_txnid := " + pig_txnid);
		LogHelper.Log("pig_deviceid := " + pig_deviceid);
		LogHelper.Log("pig_gameid := " + pig_gameid);
		LogHelper.Log("pig_userid := " + pig_userid);
		LogHelper.Log("pig_amount := " + pig_amount);
		LogHelper.Log("pig_type := " + pig_type);
		LogHelper.Log("pig_desc := " + pig_desc);
		LogHelper.Log("pig_addinfo := " + pig_addinfo);
		LogHelper.Log("pig_sig := " + pig_sig);
		
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
			userbin = DBConnector.GetMembaseServer(userid).GetRaw(userid + "_" + KeyID.KEY_USER_INFOS);
		}
		catch (Exception e)
		{
			userbin = null;
			LogHelper.Log("PigPaymentHandler.. can not get user info data.");
			LogHelper.LogException("PigPaymentHandler.LoadUserData", e);
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
		}
		catch (Exception e) 
		{
			total_paid = 0;
		}
		
		if (transactionresult == TRANSACTION_OK)
		{
			if (!IsValidSig(pig_sig))
			{
				transactionresult = TRANSACTION_WRONG_SIG;
				LogHelper.Log("PigPaymentHandler: wrong sig");
			}
		}
		
		boolean is_transaction_exist = true;
		try 
		{
			if (DBConnector.GetMembaseServerForTemporaryData().Add(pig_txnid, _data))
			{
				is_transaction_exist = false;
			}
		} 
		catch (Exception e) 
		{
			is_transaction_exist = true;
			LogHelper.LogException("CardPaymentHandler.CheckSQLTransactionExist", e);
		}
		
		if (load_result && (transactionresult == TRANSACTION_OK) && !is_transaction_exist)
		{
			int diamond_real = 0;
//			try
//			{
//				diamond_real = Integer.parseInt(pig_amount);
//			}
//			catch (Exception e)
//			{
//				diamond_real = 0;
//				LogHelper.LogException("PigPaymentHandler.ParseDiamond", e);
//			}

			LogHelper.Log("diamond real = " + diamond_real);

			int diamond_bonus = 0;
			try {
				diamond_bonus = Integer.parseInt(pig_amount);
			} catch (Exception e) {
				diamond_bonus = 0;
				LogHelper.LogException("PigPaymentHandler.ParseDiamond", e);
			}
			LogHelper.Log("diamond bonus = " + diamond_bonus);

			int diamond_user_charged = diamond_real + diamond_bonus;				

			log_iGameCoin = diamond_real;
			log_iPromotionCoin = diamond_bonus;

			MoneyManager moneyManager = new MoneyManager(Long.toString(userid));
			moneyManager.SetDatabase(DBConnector.GetMembaseServer(userid));

			if (moneyManager.LoadFromDatabase(KeyID.KEY_MONEY_REAL, KeyID.KEY_MONEY_TOTAL, KeyID.KEY_MONEY_BONUS)) {
				log_iGameCoinBefore = moneyManager.GetRealMoney();
				log_iPromotionCoinBefore = moneyManager.GetBonusMoney();

				if (moneyManager.IncreaseBonusMoney(diamond_bonus, jsonObject.toString())) {
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
					LogHelper.Log(LogHelper.LogType.IFRS, ifrs.toString());
					
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

					// save last pay info
					String last_pay_info = Misc.getCurrentDateTime() + "|" + gross + "|" + 0;
					DBConnector.GetMembaseServer(userid).Set(userid + "_" + KeyID.KEY_LAST_PAY_TIME, last_pay_info);
					LogHelper.Log("PaymentHanlder.. saved last pay info = " + last_pay_info);

					// save total paid
					total_paid += gross;
					DBConnector.GetMembaseServer(userid).Set(userid + "_" + KeyID.KEY_TOTAL_PAID, total_paid);
					LogHelper.Log("PaymentHanlder.. saved total paid = " + total_paid);
				} else {
					transactionresult = TRANSACTION_EXCEPTION;
					LogHelper.Log("PaymentHandler.. err! add money bonus fail");
				}
			} else {
				transactionresult = TRANSACTION_WRONG_UID;
				LogHelper.Log("PaymentHandler.. err! can't load money manager");
			}
		} else {
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
//		res.put("TransactionID", transactionid);
//		res.put("UserID", s_userid);
		
		switch (transactionresult)
		{
			case TRANSACTION_OK:
				res.put("returnCode", "1");
				res.put("errorMessage", "TRANSACTION_OK");
				break;
			case TRANSACTION_WRONG_SIG:
				res.put("returnCode", "-2");
				res.put("errorMessage", "TRANSACTION_WRONG_SIG");
				break;
			case TRANSACTION_WRONG_UID:
				res.put("returnCode", "-1");
				res.put("errorMessage", "TRANSACTION_WRONG_UID");
				break;
			case TRANSACTION_EXCEPTION:
				res.put("returnCode", "-6");
				res.put("errorMessage", "TRANSACTION_EXCEPTION");
				break;
			default:
				res.put("returnCode", "-6");
				res.put("errorMessage", "TRANSACTION_EXCEPTION");
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
//		if (type.equals("SMS") || type.equals("sms") || _is_ATM)
		{
			UpdateClientMoney();
		}
		
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
		log2.append('\t').append("REFILL" + " " + type + " " + operator); // description.
		log2.append('\t').append(transactionresult);
		log2.append('\t').append(userid);
		log2.append('\t').append(log_sUsername);
		log2.append('\t').append(0);
		log2.append('\t').append(log_iLevel);
		
		LogHelper.Log(LogHelper.LogType.PAYING, log2.toString());
//		LogHelper.Log(log2.toString());
	}
	
	private boolean IsValidSig(String sig)
	{
		return sig.equals(Misc.Hash(pig_txnid + pig_deviceid + pig_gameid + pig_userid + pig_amount + pig_type + pig_desc + pig_addinfo + ProjectConfig.PIG_KEY_1, "MD5"));
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
