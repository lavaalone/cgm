package com.vng.skygarden.game;

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
import com.vng.util.FBEncrypt;
import com.vng.zaloSDK.Common;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;


public class AndroidValidatingReceiptTask extends HttpResponseHandler
{
	private Client		_client;
	private long		_user_id;
	private String		_base64_public_key = ProjectConfig.ANDROID_BASE64_PUBLIC_KEY;
	private String		_signed_data = null;
	private String		_signature = null;

	private final int CONNECT_TIMEOUT    = 1000;
    private final int IDLE_TIME          = 5000;
    private final int CONTENT_LENGTH     = 4096;
	
	private static final String KEY_FACTORY_ALGORITHM = "RSA";
	private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
	
    public AndroidValidatingReceiptTask(Client client, long user_id, String signedData, String signature) throws Exception
	{
		_client = client;
		_user_id = user_id;
		_signed_data = signedData;
		_signature = signature;
		verifyAndroidPurchase();
    }
	
	/**
     * Verifies that the data was signed with the given signature, and returns
     * the verified purchase. The data is in JSON format and signed
     * with a private key. The data also contains the {@link PurchaseState}
     * and product ID of the purchase.
     * @param base64PublicKey the base64-encoded public key to use for verifying.
     * @param signedData the signed JSON string (signed, not encrypted)
     * @param signature the signature for the data, signed with the private key
     */
	public void verifyAndroidPurchase() throws Exception
	{
        if (_signed_data == null) 
		{
			LogHelper.Log("err! null signed data");
            return;
        }
        boolean verified = false;
        if (_signed_data.length() > 0) 
		{
            PublicKey key = generatePublicKey(_base64_public_key);
			LogHelper.Log("decoded public key := " + key.toString());
			
            verified = verifyAndroidSignedData(key, _signed_data, _signature);
            if (!verified) 
			{
				LogHelper.Log("verify failed!");
				_client.GetUserInstance().AndroidValidatingReceiptCallback(ReturnCode.RESPONSE_ERROR);
				return;
            }
			else
			{
				LogHelper.Log("verified ok!");
				
				// TODO: get purchase status from GG
				
				verifyPurchaseContent();
				return;
			}
        }
    }
	
	/**
     * Generates a PublicKey instance from a string containing the
     * Base64-encoded public key.
     *
     * @param encodedPublicKey Base64-encoded public key
     * @throws IllegalArgumentException if encodedPublicKey is invalid
     */

    public PublicKey generatePublicKey(String encodedPublicKey) 
	{
        try 
		{
            byte[] decodedKey = Base64.decodeBase64(encodedPublicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
        }
		catch (Exception e) 
		{
            LogHelper.LogException("generatePublicKey", e);
			return null;
        }
    }
	
	/**
     * Verifies that the signature from the server matches the computed
     * signature on the data.  Returns true if the data is correctly signed.
     *
     * @param publicKey public key associated with the developer account
     * @param signedData signed data from server
     * @param signature server signature
     * @return true if the data and signature match
     */

    public boolean verifyAndroidSignedData(PublicKey publicKey, String signedData, String signature) 
	{
        Signature sig;
        try 
		{
            sig = Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initVerify(publicKey);
            sig.update(signedData.getBytes());
            if (!sig.verify(Base64.decodeBase64(signature))) 
			{
                return false;
            }
            return true;
        }
		catch (Exception e)
		{
			LogHelper.LogException("verifyAndroidSignedData", e);
		}
        return false;
    }

    private void request(String order_id, String purchase_token) throws Exception
	{
		// local verify ok, now request add money
		StringBuilder url = new StringBuilder();
		url.append("http://10.30.81.47:8303/androidIAB.html/validating?");
		if (ProjectConfig.IS_SERVER_LOGIC == 1)
		{
			url.append("server=").append("product");
		}
		else
		{
			url.append("server=").append("development");
		}
		url.append("&payment_method=").append("android");
		url.append("&user_id=").append(_user_id);
		url.append("&order_id=").append(order_id);
		url.append("&purchase_token=").append(purchase_token);
		LogHelper.Log("url = " + url.toString());
		Http.sendHttpRequest(Common.outLoopGroup, "GET", url.toString(), null, this, CONNECT_TIMEOUT, IDLE_TIME, CONTENT_LENGTH);
    }
	
	/*
	{
		"orderId":"12999763169054705758.1388915048349340",
		"packageName":"vn.kvtm.skygarden",
		"productId":"vn.kvtm.skygarden.diamond100",
		"purchaseTime":1415690140871,
		"purchaseState":0,
		"developerPayload":"24eb52dc7626969f91e2ef855c1d2c165bede1ef5141e8fbd1203302401f589b",
		"purchaseToken":"ebmnpooaplfhjiahhcjiadki.AO-J1OxPVAS2VJJ1v12WSu0lz99bj9AtIfMTrGaW96k6uINo2dFgCIt8dkFrLE_JoaGkeGZJmFtVs-VxLzAuhsD1Sqj35c-kHFRVQVhUbLnaLSw245cvnmlonm6ubopaDfhwz4pcXVod"
	}
	*/
	private void verifyPurchaseContent() throws Exception
	{
		JsonObject purchase_data = new JsonParser().parse(_signed_data).getAsJsonObject();
		String order_id = purchase_data.get("orderId").toString().replace("\"", "");
		String package_name = purchase_data.get("packageName").toString().replace("\"", "");
		String product_id = purchase_data.get("productId").toString().replace("\"", "");
		String purchase_time = purchase_data.get("purchaseTime").toString().replace("\"", "");
		String purchase_state = purchase_data.get("purchaseState").toString().replace("\"", "");
		String developer_payload = purchase_data.get("developerPayload").toString().replace("\"", "");
		String purchase_token = purchase_data.get("purchaseToken").toString().replace("\"", "");

		LogHelper.Log("order_id := " + order_id);
		LogHelper.Log("package_name := " + package_name);
		LogHelper.Log("product_id := " + product_id);
		LogHelper.Log("purchase_time := " + purchase_time);
		LogHelper.Log("purchase_state := " + purchase_state);
		LogHelper.Log("developer_payload := " + developer_payload);
		LogHelper.Log("purchase_token := " + purchase_token);

		// valid package name
		if (!package_name.equals(ProjectConfig.APP_PACKAGE_NAME))
		{
			LogHelper.Log("Wrong package name := " + package_name);
			_client.GetUserInstance().AndroidValidatingReceiptCallback(ReturnCode.RESPONSE_ERROR);
			return;
		}
		LogHelper.Log("Verify package name OK!");

		// valid purchase state
		if (!purchase_state.equals("0"))
		{
			LogHelper.Log("Purchase state failed := " + purchase_state);
			_client.GetUserInstance().AndroidValidatingReceiptCallback(ReturnCode.RESPONSE_ERROR);
			return;
		}
		LogHelper.Log("Verify purchase state OK!");

		// valid payload
		String payload = "";
		Object obj = DBConnector.GetMembaseServer(_user_id).Get(_user_id + "_" + "developer_payload");
		if (obj != null)
		{
			payload = (String) obj;
			if (!developer_payload.equals(payload))
			{
				LogHelper.Log("Invalid developer payload := " + developer_payload);
				LogHelper.Log("Server payload := " + payload);
				_client.GetUserInstance().AndroidValidatingReceiptCallback(ReturnCode.RESPONSE_ERROR);
				return;
			}
		}
		LogHelper.Log("Verify developer payload OK!");

		// valid & save purchase token
		obj = DBConnector.GetMembaseServer(_user_id).Get(purchase_token);
		if (obj != null)
		{
			String s = (String)obj;
			LogHelper.Log("Purchase token is used := " + purchase_token);
			LogHelper.Log("Last purchase info := " + s);
			_client.GetUserInstance().AndroidValidatingReceiptCallback(ReturnCode.RESPONSE_ERROR);
			return;
		}
		else
		{
			DBConnector.GetMembaseServer(_user_id).Set(purchase_token, purchase_data.toString());
			LogHelper.Log("Set purchase token");
			LogHelper.Log("key := " + purchase_token);
			LogHelper.Log("value := " + purchase_data.toString());
		}

//		// valid & save order id
		obj = DBConnector.GetMembaseServer(_user_id).Get(order_id);
		if (obj != null)
		{
			String s = (String)obj;
			LogHelper.Log("Order id is used := " + order_id);
			LogHelper.Log("Last order id info := " + s);
			_client.GetUserInstance().AndroidValidatingReceiptCallback(ReturnCode.RESPONSE_ERROR);
			return;
		}
		else
		{
			DBConnector.GetMembaseServer(_user_id).Set(order_id, purchase_data.toString());
			LogHelper.Log("Set order id");
			LogHelper.Log("key := " + order_id);
			LogHelper.Log("value := " + purchase_data.toString());
		}
		
		// request add money
		request(order_id, purchase_token);
	}

    private void response(int result) throws Exception
	{
		_client.GetUserInstance().AndroidValidatingReceiptCallback(result);
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
//        response(ReturnCode.RESPONSE_ERROR);
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
