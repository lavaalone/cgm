/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.taskqueue.*;
import com.vng.skygarden.*;
import com.vng.log.LogHelper;
import com.vng.skygarden._gen_.ProjectConfig;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONObject;

public class CardPaymentTask extends Task
{
	private Client		_client;
	private FBEncrypt	_encrypt;
	
	private long uid = -1;
	private String cardno = "";
	private String cardpin = "";
	private int cardtype = -1;
	private int transactionresult = -1;
	
	private final int TRANSACTION_OK		= 1;
	
	public CardPaymentTask(Client client, FBEncrypt encrypt)
	{
		super();
		
		_client = client;
		_encrypt = encrypt;
	}
	
	@Override
	protected void HandleTask() 
	{
		boolean task_result = true;
		
		if (!_encrypt.hasKey(KeyID.KEY_USER_ID) || 
				!_encrypt.hasKey(KeyID.KEY_PAYMENT_CARD_SERIALNO) ||
				!_encrypt.hasKey(KeyID.KEY_PAYMENT_CARD_PIN) ||
				!_encrypt.hasKey(KeyID.KEY_PAYMENT_CARD_TYPE) ||
				!_encrypt.hasKey(KeyID.KEY_DEVICE_ID))
		{
			LogHelper.Log("CardPaymentTask.. err! invalid client params.");
			task_result = false;
		}
		else
		{
			uid			= _encrypt.getLong(KeyID.KEY_USER_ID);
			cardno		= _encrypt.getString(KeyID.KEY_PAYMENT_CARD_SERIALNO);
			cardpin		= _encrypt.getString(KeyID.KEY_PAYMENT_CARD_PIN);
			cardtype	= _encrypt.getInt(KeyID.KEY_PAYMENT_CARD_TYPE);
			
			if (uid <= 0 || cardno.equals("") || cardpin.equals("") || cardtype < DatabaseID.PAYMENT_CARD_ZING || cardtype > DatabaseID.PAYMENT_CARD_VTT)
			{
				LogHelper.Log("CardPaymentTask.. err! invalid params");
				
				task_result = false;
			}
			
			if (task_result)
			{
				// build json object
				JSONObject obj = new JSONObject();
				obj.put("cardSerialNo", cardno);
				obj.put("cardCode", cardpin);
				obj.put("gameID", ProjectConfig.GAME_ID);
				obj.put("userID", uid);
				if (GameUtil.GetUserInfo(uid).getRefCode().equals("") || GameUtil.GetUserInfo(uid).getRefCode().equals("null"))
					obj.put("addInfo", GameUtil.GetUserInfo(uid).getRefCode());
				else
					obj.put("addInfo", "mwo:" + GameUtil.GetUserInfo(uid).getRefCode());
				
				switch (cardtype)
				{
					case DatabaseID.PAYMENT_CARD_ZING:
						obj.put("cardType","ZC");
						break;
					case DatabaseID.PAYMENT_CARD_VNP:
						obj.put("cardType","VNP");
						break;
					case DatabaseID.PAYMENT_CARD_VMS:
						obj.put("cardType","VMS");
						break;
					case DatabaseID.PAYMENT_CARD_VTT:
						obj.put("cardType","VTT");
						break;
				}
				
				// send request to server payment
				try
				{
					
					URL url;
					if (ProjectConfig.IS_SERVER_LOGIC == 1) {
						url = new URL(ProjectConfig.SERVER_PAYMENT);
					} else {
						url = new URL(ProjectConfig.SERVER_PAYMENT_FREESTYLE);
					}
					
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type", "application/json");
					connection.setRequestProperty("Content-Length", "" + Integer.toString(obj.toString().getBytes().length));
					connection.setRequestProperty("Content-Language", "en-US");
					connection.setDoOutput(true);

					LogHelper.Log("json content := " + obj.toString());
					DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
					wr.writeBytes(obj.toString());
					wr.flush();
					wr.close();
					
					// receive response code
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String line = "";
					while ((line = reader.readLine()) != null) 
					{
						LogHelper.Log("ErrorCode: " + line);
						
						transactionresult = Integer.parseInt(line);
					}
					
					connection.disconnect();
				}
				catch (Exception e)
				{
					LogHelper.LogException("CardPaymentTask.OpenHTTPConnection", e);
					transactionresult = -1;
				}
			}
		}
		
		int new_money = 0;
		if (transactionresult == TRANSACTION_OK)
		{
			MoneyManager moneyManager = _client.GetUserInstance().GetMoneyManager();
			
			if (moneyManager != null)
			{
				boolean result = moneyManager.LoadFromDatabase(KeyID.KEY_MONEY_REAL, KeyID.KEY_MONEY_TOTAL, KeyID.KEY_MONEY_BONUS);

				if (result)
				{
					new_money = moneyManager.GetBonusMoney() + moneyManager.GetRealMoney();
				}
			}
		}
		
		// create response status info
		FBEncrypt responseStatus = new FBEncrypt();
		responseStatus.addShort(KeyID.KEY_USER_COMMAND_ID, _encrypt.getShort(KeyID.KEY_USER_COMMAND_ID));
		responseStatus.addLong(KeyID.KEY_USER_ID, _encrypt.getLong(KeyID.KEY_USER_ID));
		responseStatus.addLong(KeyID.KEY_USER_REQUEST_ID, _encrypt.getLong(KeyID.KEY_USER_REQUEST_ID) + 2);
		responseStatus.addStringANSI(KeyID.KEY_USER_SESSION_ID, _encrypt.getString(KeyID.KEY_USER_SESSION_ID));
		responseStatus.addByte(KeyID.KEY_USER_REQUEST_STATUS, ReturnCode.RESPONSE_OK);
		
		// reponse to client
		FBEncrypt encoder = new FBEncrypt();
		encoder.addBinary(KeyID.KEY_REQUEST_STATUS, responseStatus.toByteArray());
//		encoder.addInt(KeyID.KEY_PAYMENT_RESULT, transactionresult);
		encoder.addInt(KeyID.KEY_PAYMENT_RESULT, transactionresult == TRANSACTION_OK ? 0 : transactionresult); // cheat here to match with client's result display panel
		
		if (transactionresult == TRANSACTION_OK)
		{
			encoder.addInt(KeyID.KEY_USER_DIAMOND, new_money);
			if (_client.GetUserInstance().GetGiftManager().LoadFromDatabase(KeyID.KEY_GIFT) && _client.GetUserInstance().GetGiftManager()._gifts.size() > 0)
			{
				encoder.addBoolean(KeyID.KEY_GIFT_AVAILABLE, _client.GetUserInstance().GetGiftManager()._gifts.size() > 0 ? true : false);
				encoder.addInt(KeyID.KEY_GIFT_ID, _client.GetUserInstance().GetGiftManager()._gifts.getLast().GetId());
				encoder.addString(KeyID.KEY_GIFT_NAME, _client.GetUserInstance().GetGiftManager()._gifts.getLast().GetName());
				encoder.addString(KeyID.KEY_GIFT_DESCRIPTION, _client.GetUserInstance().GetGiftManager()._gifts.getLast().GetDescription());
				encoder.addString(KeyID.KEY_GIFT_ITEM_LIST, _client.GetUserInstance().GetGiftManager()._gifts.getLast().GetItemList());
			}
			
			//respone special offer
			byte[] offerbin = null;
			try
			{
				offerbin = DBConnector.GetMembaseServer(uid).GetRaw(uid + "_" + KeyID.KEY_SPECIAL_OFFER);
				if (offerbin != null && offerbin.length > 0) {
					Special_Offer _offer = new Special_Offer(offerbin);
					encoder.addBinary(KeyID.KEY_SPECIAL_OFFER, _offer.getData());//update remaining time
					LogHelper.Log("[udp] SpecialOffer respone OK.");
				}
				else
				{
					LogHelper.Log("[udp] SpecialOffer not respone.");
				}
			} catch (Exception e) {
				LogHelper.LogException("handleRequestRefreshDiamond.loadOfferBin", e);
			}
		}
		
		try
		{
			String last_pay = (String)DBConnector.GetMembaseServer(uid).Get(uid + "_" + KeyID.KEY_LAST_PAY_TIME);
			encoder.addStringANSI(KeyID.KEY_LAST_PAY_TIME, last_pay);
		}
		catch (Exception e)
		{
			LogHelper.LogException("CardPaymentTask.ReadLastPay", e);
		}
		
		try
		{
			_client.WriteZip(encoder.toByteArray());
			LogHelper.Log("CardPaymentTask.. response to client OK.");
		}
		catch (Exception e)
		{
			LogHelper.LogException("CardPaymentTask.Response", e);
		}
	}
}