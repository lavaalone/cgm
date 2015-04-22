package com.vng.skygarden.game;

import com.vng.zaloSDK.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vng.log.LogHelper;
import com.vng.netty.Client;
import com.vng.skygarden.DBConnector;
import com.vng.skygarden._gen_.ProjectConfig;
import firebat.framework.io.http.Http;
import firebat.framework.io.http.HttpResponseHandler;
import firebat.framework.log.Log;
import firebat.framework.util.Monitor;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.timeout.IdleState;
import io.netty.util.CharsetUtil;
import com.vng.skygarden.game.ZaloAuthenticateTask;
import com.vng.util.FBEncrypt;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;


public class AppleValidatingReceiptTask extends HttpResponseHandler
{
	private Client		_client;
	private String		_receipt_data = "";
	private String		_password = "";
	private long		_user_id;

	private final int CONNECT_TIMEOUT    = 1000;
    private final int IDLE_TIME          = 5000;
    private final int CONTENT_LENGTH     = 4096;
	
	public static final String VERIFICATION_URL_REAL = "https://buy.itunes.apple.com/verifyReceipt";
    public static final String VERIFICATION_URL_SANDBOX = "https://sandbox.itunes.apple.com/verifyReceipt";
	
	private String		_response = "";
	
    public AppleValidatingReceiptTask(Client client, long user_id, String receipt_data, String password) throws Exception
	{
		_client = client;
		_user_id = user_id;
		_receipt_data = receipt_data;
		_password = password;
        request();
    }

    private void request() throws Exception
	{
		// build json object
		JSONObject obj = new JSONObject();
		obj.put("receipt-data", _receipt_data);
		
		LogHelper.LogHappy("obj.toString() := " + obj.toString());

		// send request to server payment
		try
		{

			URL url;
			if (ProjectConfig.IS_SERVER_LOGIC == 1) 
			{
				url = new URL(VERIFICATION_URL_REAL);
			}
			else 
			{
				url = new URL(VERIFICATION_URL_SANDBOX);
			}

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(obj.toString().getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(obj.toString());
			wr.flush();
			wr.close();

			// receive response code
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) 
			{
				_response = _response + line;
			}

			connection.disconnect();
		}
		catch (Exception e)
		{
			LogHelper.LogException("CardPaymentTask.OpenHTTPConnection", e);
		}
		
		LogHelper.LogHappy("_reponse := " + _response);
		if (_response.length() > 0)
		{
			/*
			{
				"receipt":{
					"original_purchase_date_pst":"2014-11-11 23:59:21 America/Los_Angeles",
					"purchase_date_ms":"1415779161923",
					"unique_identifier":"f3369ee0ce466277ae765cae6cf3d19261e5af2a",
					"original_transaction_id":"1000000131326451",
					"bvrs":"0.02.37030",
					"transaction_id":"1000000131326451",
					"quantity":"1",
					"unique_vendor_identifier":"B91FF3B2-FB3D-4FA5-9946-69BE529E968C",
					"item_id":"929979678",
					"product_id":"vn.kvtm.skygraden.testapplegold10",
					"purchase_date":"2014-11-12 07:59:21 Etc/GMT",
					"original_purchase_date":"2014-11-12 07:59:21 Etc/GMT",
					"purchase_date_pst":"2014-11-11 23:59:21 America/Los_Angeles",
					"bid":"vn.kvtm.skygarden",
					"original_purchase_date_ms":"1415779161923"
				},
				"status":0
			}
			*/
			JsonObject purchase_data = new JsonParser().parse(_response).getAsJsonObject();
			JsonObject receipt = purchase_data.getAsJsonObject("receipt");
			
			String transaction_id = receipt.get("transaction_id").toString().replace("\"", "");
			DBConnector.GetMembaseServer(_user_id).Set(transaction_id, purchase_data.toString());
			LogHelper.LogHappy("Set transaction_id");
			LogHelper.LogHappy("key := " + transaction_id);
			LogHelper.LogHappy("value := " + purchase_data.toString());
			
			StringBuilder url = new StringBuilder();
			url.append("http://10.30.81.47:8305/appleIAP.html/validating?");
			if (ProjectConfig.IS_SERVER_LOGIC == 1)
			{
				url.append("server=").append("product");
			}
			else
			{
				url.append("server=").append("development");
			}
			url.append("&payment_method=").append("apple");
			url.append("&user_id=").append(_user_id);
			url.append("&transaction_id=").append(transaction_id);
			LogHelper.LogHappy("url = " + url.toString());
			Http.sendHttpRequest(Common.outLoopGroup, "GET", url.toString(), null, this, CONNECT_TIMEOUT, IDLE_TIME, CONTENT_LENGTH);
		}
	}

    private void response(int result)
	{
		_client.GetUserInstance().AppleValidatingReceiptCallback(result);
    }

    @Override
    public void read(HttpResponse httpResponse, ByteBuf byteBuf) throws Exception
	{
		String response = byteBuf.toString(CharsetUtil.UTF_8);
        close();
		if (response.contains("Success"))
		{
			response(ReturnCode.RESPONSE_OK);
		}
		else
		{
			response(ReturnCode.RESPONSE_ERROR);
		}
    }

    @Override
    public void exceptionCaught(Throwable cause)
	{
        close();
        response(ReturnCode.RESPONSE_ERROR);
    }

    @Override
    public void inactive() throws Exception
	{
    }

    @Override
    public void idle(IdleState idleState) throws Exception
	{
        close();
        response(ReturnCode.RESPONSE_ERROR);
    }
}
