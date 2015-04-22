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
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;


// WINDOWS PHONE
import javax.xml.crypto.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dom.*;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WinValidatingReceiptTask extends HttpResponseHandler
{
	private Client		_client;
	private String		_receipt_data = "";
	private long		_user_id;
	
	private final int CONNECT_TIMEOUT    = 1000;
    private final int IDLE_TIME          = 5000;
    private final int CONTENT_LENGTH     = 4096;

	public static final String KEY_INFO = "<KeyInfo><X509Data><X509Certificate>MIIDFDCCAgCgAwIBAgIQrih3cQuSeL1CgpLFusfJsTAJBgUrDgMCHQUAMB8xHTAbBgNVBAMTFElhcFJlY2VpcHRQcm9kdWN0aW9uMB4XDTEyMDIxNzAxMTYyNFoXDTM5MTIzMTIzNTk1OVowHzEdMBsGA1UEAxMUSWFwUmVjZWlwdFByb2R1Y3Rpb24wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDb0CeltVqOOIJiwNGgAr7Z0K4rAYsHCa1oSFPJXtokz134bi2neJ8bHIKAnT0kwa3xViUxwp3+OZd2t2PshDv0ucZ5dus6WCnuAw/MHVAodgLQMqYiKeM7VTIi3S1s3iV/66Y8KP7jH3CmE2XCXOQae+bQUuyGsTit0ScU7+MofODoNhvONs54n/K1WVnct2wWBpn8GGAS+l2mzOF0jXbMSjtz7wuK77GeydG+x9paLuHIyCso7tjOqv/lvol5IIX0VnC5G2vC6dWR6MkNL5FzLXnsSuQgoYEUZXPlXJhsmv6oyyenaP0PpYJZcCLLVi1L2hcVo8B2DIEg3I3t8ch/AgMBAAGjVDBSMFAGA1UdAQRJMEeAEHGLK3BRpCWDa2vU50kI73ehITAfMR0wGwYDVQQDExRJYXBSZWNlaXB0UHJvZHVjdGlvboIQrih3cQuSeL1CgpLFusfJsTAJBgUrDgMCHQUAA4IBAQC4jmOu0H3j7AwVBvpQzPMLBd0GTimBXmJw+nruE+0Hh/0ywGTFNE+KcQ21L4v+IuP8iMh3lpOcPb23ucuaoNSdWi375/KxrW831dbh+goqCZP7mWbxpnSnFnuV+R1VPsQjdS+0tg5gjDKNMSx/2fH8krLAkidJ7rvUNmtEWMeVNk0/ZM/ECinobMSSwbqUuc9Qql9T1epe+xv34a6eek+m4W0VXnLSuKhQS5jdILsyeJWHROZF5mrh3DQuS0Ll5FzKmJxHf0hyXAo03SSA+x3JphAU4oYbkE9nRTU1tR6iq1D9ZxfQmvzmIbMfyJ/y89PLs/ewHopSK7vQmGFjfjIl</X509Certificate></X509Data><KeyValue><RSAKeyValue><Modulus>29AnpbVajjiCYsDRoAK+2dCuKwGLBwmtaEhTyV7aJM9d+G4tp3ifGxyCgJ09JMGt8VYlMcKd/jmXdrdj7IQ79LnGeXbrOlgp7gMPzB1QKHYC0DKmIinjO1UyIt0tbN4lf+umPCj+4x9wphNlwlzkGnvm0FLshrE4rdEnFO/jKHzg6DYbzjbOeJ/ytVlZ3LdsFgaZ/BhgEvpdpszhdI12zEo7c+8Liu+xnsnRvsfaWi7hyMgrKO7Yzqr/5b6JeSCF9FZwuRtrwunVkejJDS+Rcy157ErkIKGBFGVz5VyYbJr+qMsnp2j9D6WCWXAiy1YtS9oXFaPAdgyBINyN7fHIfw==</Modulus><Exponent>AQAB</Exponent></RSAKeyValue></KeyValue></KeyInfo>";
	
	private String		_response = "";
	
    public WinValidatingReceiptTask(Client client, long user_id, String receipt_data) throws Exception
	{
		_client = client;
		_user_id = user_id;
		_receipt_data = receipt_data;
		ValidateReceipt();
    }

    private void response(String result) throws Exception
	{
		_client.GetUserInstance().WinPhoneValidatingReceiptCallback(result);
    }
	
	public void ValidateReceipt() throws Exception
	{
		boolean verify_result = false;
		
		String spliter = "</Signature></Receipt>";
		if (!_receipt_data.contains(spliter))
		{
			throw new Exception("Cannot find spliter");
		}
		
		String[] aos = _receipt_data.split(spliter);
		StringBuilder sb = new StringBuilder();
		sb.append(aos[0]);
		sb.append(KEY_INFO);
		sb.append(spliter);
		
		LogHelper.Log("RECEIPT := " + sb.toString());
		
		// Instantiate the document to be validated
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)));
		
		// Find Signature element
		NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
		if (nl.getLength() == 0) 
		{
			throw new Exception("Cannot find Signature element");
		}
		
		NodeList nl2 = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "KeyInfo");
		if (nl2.getLength() == 0)
		{
			throw new Exception("Cannot find KeyInfo element");
		}
		
		// Create a DOM XMLSignatureFactory that will be used to unmarshal the
		// document containing the XMLSignature
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

		// Create a DOMValidateContext and specify a KeyValue KeySelector
		// and document context
		DOMValidateContext valContext = new DOMValidateContext(new KeyValueKeySelector(), nl.item(0));

		// unmarshal the XMLSignature
		XMLSignature signature = fac.unmarshalXMLSignature(valContext);

		// Validate the XMLSignature (generated above)
		boolean coreValidity = signature.validate(valContext);

		// Check core validation status
		if (coreValidity == false)
		{
			LogHelper.Log("Signature failed core validation");
			boolean sv = signature.getSignatureValue().validate(valContext);
			LogHelper.Log("signature validation status: " + sv);
			// check the validation status of each Reference
			Iterator i = signature.getSignedInfo().getReferences().iterator();
			for (int j=0; i.hasNext(); j++)
			{
				boolean refValid = ((Reference) i.next()).validate(valContext);
				LogHelper.Log("ref["+j+"] validity status: " + refValid);
			}
			verify_result = false;
		} 
		else 
		{
			LogHelper.Log("Signature passed core validation");
			verify_result = true;
		}
		
		if (!verify_result)
		{
			response("error");
		}
		else
		{
			doc.getDocumentElement().normalize();
			
			Element root = doc.getDocumentElement();
			LogHelper.LogHappy(root.getNodeName());
			
			NodeList nList = doc.getElementsByTagName("ProductReceipt");
			Node node = nList.item(0);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
			   //Print each employee's detail
			   Element eElement = (Element) node;
			   
			   String PurchasePrice = eElement.getAttribute("PurchasePrice");
			   String PurchaseDate = eElement.getAttribute("PurchaseDate");
			   String Id = eElement.getAttribute("Id");
			   String AppId = eElement.getAttribute("AppId");
			   String ProductId = eElement.getAttribute("ProductId");
			   String ProductType = eElement.getAttribute("ProductType");
			   String PublisherUserId = eElement.getAttribute("PublisherUserId");
			   String PublisherDeviceId = eElement.getAttribute("PublisherDeviceId");
			   String MicrosoftProductId = eElement.getAttribute("MicrosoftProductId");
			   String MicrosoftAppId = eElement.getAttribute("MicrosoftAppId");
			   
			   LogHelper.Log("PurchasePrice := " + PurchasePrice);
			   LogHelper.Log("PurchaseDate := " + PurchaseDate);
			   LogHelper.Log("Id := " + Id);
			   LogHelper.Log("AppId := " + AppId);
			   LogHelper.Log("ProductId := " + ProductId);
			   LogHelper.Log("ProductType := " + ProductType);
			   LogHelper.Log("PublisherUserId := " + PublisherUserId);
			   LogHelper.Log("PublisherDeviceId := " + PublisherDeviceId);
			   LogHelper.Log("MicrosoftProductId := " + MicrosoftProductId);
			   LogHelper.Log("MicrosoftAppId := " + MicrosoftAppId);
			   
			   // verify MicrosoftAppId
			   if (!MicrosoftAppId.equals("65ddb0de-412f-43ad-8517-e28a03d6e188"))
			   {
				   LogHelper.Log("Err! Invalid App ID := " + MicrosoftAppId);
				   response("error");
				   return;
			   }
			   
			   // verify & write purchase unique id to base
			   if (!DBConnector.GetMembaseServer(_user_id).Add(Id, ProductId))
			   {
				   LogHelper.Log("Used purchase ID");
				   response("Success");
				   return;
			   }
			   
			   request(_user_id, Id);
			}
		}
	}
	
	public void request(long user_id, String purchase_id) throws Exception
	{
		// local verify ok, now request add money
		StringBuilder url = new StringBuilder();
		url.append("http://").append(ProjectConfig.PAYMENT_IP).append(':').append(ProjectConfig.IS_SERVER_LOGIC == 1 ? ProjectConfig.PAYMENT_PORT : ProjectConfig.PAYMENT_FREESTYLE_PORT);
		url.append("/WinIAP.html/validating?");
		url.append("server=").append(ProjectConfig.IS_SERVER_LOGIC == 1 ? "product" : "development");
		url.append("&payment_method=").append("win");
		url.append("&user_id=").append(_user_id);
		url.append("&transaction_id=").append(purchase_id);
		LogHelper.Log("url := " + url.toString());
		Http.sendHttpRequest(Common.outLoopGroup, "GET", url.toString(), null, this, CONNECT_TIMEOUT, IDLE_TIME, CONTENT_LENGTH);
	}

    @Override
    public void read(HttpResponse httpResponse, ByteBuf byteBuf) throws Exception
	{
		String response = byteBuf.toString(CharsetUtil.UTF_8);
        close();
		LogHelper.Log("response := " + response);
		response(response);
    }

    @Override
    public void exceptionCaught(Throwable cause)
	{
        close();
		LogHelper.LogException("exceptionCaught", cause);
    }

    @Override
    public void inactive() throws Exception
	{
    }

    @Override
    public void idle(IdleState idleState) throws Exception
	{
        close();
        response("error");
    }
}

/**
 * KeySelector which retrieves the public key out of the
 * KeyValue element and returns it.
 * NOTE: If the key algorithm doesn't match signature algorithm,
 * then the public key will be ignored.
 */
class KeyValueKeySelector extends KeySelector 
{
    public KeySelectorResult select(KeyInfo keyInfo,
                                    KeySelector.Purpose purpose,
                                    AlgorithmMethod method,
                                    XMLCryptoContext context) throws KeySelectorException 
	{

		LogHelper.LogHappy("keyInfo := " + keyInfo);
        LogHelper.LogHappy("method := " + method);


        if (keyInfo == null) 
		{
            throw new KeySelectorException("Null KeyInfo object!");
        }

        SignatureMethod sm = (SignatureMethod) method;
        List list = keyInfo.getContent();

        for (int i = 0; i < list.size(); i++) 
		{
            XMLStructure xmlStructure = (XMLStructure) list.get(i);
			
            if (xmlStructure instanceof KeyValue) {
                PublicKey pk = null;
                try 
				{
                    pk = ((KeyValue)xmlStructure).getPublicKey();
                    System.out.println(pk);
                } 
				catch (KeyException ke) 
				{
                    throw new KeySelectorException(ke);
                }
				
				LogHelper.LogHappy("sm.getAlgorithm() := " + sm.getAlgorithm());
				LogHelper.LogHappy("pk.getAlgorithm() := " + pk.getAlgorithm());
                // make sure algorithm is compatible with method
                if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) 
				{
                    return new SimpleKeySelectorResult(pk);
                }
            }
        }
        throw new KeySelectorException("No KeyValue element found!");
    }

    //@@@FIXME: this should also work for key types other than DSA/RSA
    static boolean algEquals(String algURI, String algName) {

        XMLSignatureFactory factory = XMLSignatureFactory.getInstance();
        SignatureMethod sm = null;
        try 
		{
            sm = factory.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", (SignatureMethodParameterSpec) null);
        } 
		catch (NoSuchAlgorithmException e) 
		{
			LogHelper.LogException("NoSuchAlgorithmException", e);
        } 
		catch (InvalidAlgorithmParameterException e) 
		{
			LogHelper.LogException("InvalidAlgorithmParameterException", e);
        }

		LogHelper.LogHappy("algURI := " + algURI);
		LogHelper.LogHappy("algName := " + algName);
		LogHelper.LogHappy("SHa1 := " + sm.getAlgorithm());
//        LogHelper.LogHappy("algURI:" + algURI + " algName:" + algName + " SHa1:" + sm.getAlgorithm());
		
        if (algName.equalsIgnoreCase("DSA") && algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) 
		{
            return true;
        } 
		else if (algName.equalsIgnoreCase("RSA") && algURI.equalsIgnoreCase(sm.getAlgorithm())) 
		{
            LogHelper.LogHappy("will return true");
            return true;
        } 
		else 
		{
            return false;
        }
    }
}

class SimpleKeySelectorResult implements KeySelectorResult 
{
	private PublicKey pk;
	
	SimpleKeySelectorResult(PublicKey pk) 
	{
		this.pk = pk;
	}

	public Key getKey() 
	{
		return pk; 
	}
}
