package com.vng.nettyhttp;

import java.nio.ByteBuffer;
import java.util.*;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.*;

import org.apache.log4j.Logger;

import com.vng.util.*;
import com.vng.log.*;
import com.vng.skygarden.*;
import com.vng.skygarden._gen_.ProjectConfig;
import com.vng.skygarden.game.*;

public class Client
{
	private ServerHandler 	_server_handler;
	SkyGardenUser 			_user;
	CardPaymentHandler		_paymenthandler;
	FBSharingHandler		_fbhandler;
	SMSInviteTask			_sms_invite_handler;
	ZaloPaymentHandler		_zalo_payment_handler;
	AppleValidatingReceiptHandler _apple_iap_validating_handler;
	AndroidValidatingReceiptHandler _android_iab_validating_handler;
	WinValidatingReceiptHandler _win_iab_validating_handler;
	PigPaymentHandler _pig_payment_handler;
	
	Client(ServerHandler server_handler)
	{	
		_server_handler = server_handler;
	}
	
	public void MessageReceived(byte[] request_content, String content_encode, String md5) throws Exception
	{
		if ((request_content != null) && (request_content.length > 0))
		{
			//check hash
			if (Misc.Hash(request_content, "MD5").equals(md5))
			{
				byte[] data = null;
				
				if (HttpHeaders.Values.GZIP.equals(content_encode))
				{
					//decompress
					data = Misc.DecompressGZIP(request_content);
				}
				else
				{
					data = request_content;
				}
				
				//handle client data
			}
			else
			{
				//log wrong md5
			}
		}
	}
	
	public void MessageReceived(String postdata) throws Exception
	{
		if (ProjectConfig.IS_SERVER_PAYMENT == 1 || ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1)
		{
			if (postdata.contains("win"))
			{
				_win_iab_validating_handler = new WinValidatingReceiptHandler(this, postdata);
				try
				{
					_win_iab_validating_handler.Execute();
				}
				catch (Exception e)
				{
					LogHelper.LogException("PaymentHandler.Execute", e);
				}
			}
			else if (postdata.contains("android")) {
				_android_iab_validating_handler = new AndroidValidatingReceiptHandler(this, postdata);
				try
				{
					_android_iab_validating_handler.Execute();
				}
				catch (Exception e)
				{
					LogHelper.LogException("AndroidValidatingReceiptHandler.Execute", e);
				}
			}
			else
			{
				_paymenthandler = new CardPaymentHandler(this, postdata);
				try
				{
					_paymenthandler.Execute();
				}
				catch (Exception e)
				{
					LogHelper.LogException("PaymentHandler.Execute", e);
				}
			}
		}
		else if (ProjectConfig.IS_SERVER_SOCIAL == 1)
		{
			// handle social logic
			_fbhandler = new FBSharingHandler(this, postdata);
			try
			{
				_fbhandler.Execute();
			}
			catch (Exception e)
			{
				LogHelper.LogException("FBSharingHandler.Execute", e);
			}
		}
		else if (ProjectConfig.IS_SERVER_SMS == 1)
		{
			// handle send sms
			_sms_invite_handler = new SMSInviteTask(this, postdata);
			try
			{
				_sms_invite_handler.Execute();
			}
			catch (Exception e)
			{
				LogHelper.LogException("SMSInviteTask.Execute", e);
			}
			
		}
		else if (ProjectConfig.IS_SERVER_ZALO_PAYMENT == 1)
		{
			_zalo_payment_handler = new ZaloPaymentHandler(this, postdata);
			try
			{
				_zalo_payment_handler.Execute();
			}
			catch (Exception e)
			{
				LogHelper.LogException("ZaloPaymentHandler.Execute", e);
			}
		}
		else if (ProjectConfig.IS_SERVER_GLOBAL_PAYMENT == 1)
		{
			if (postdata.contains("apple"))
			{
				_apple_iap_validating_handler = new AppleValidatingReceiptHandler(this, postdata);
				try
				{
					_apple_iap_validating_handler.Execute();
				}
				catch (Exception e)
				{
					LogHelper.LogException("AppleValidatingReceiptHandler.Execute", e);
				}
			}
			else if (postdata.contains("android"))
			{
				_android_iab_validating_handler = new AndroidValidatingReceiptHandler(this, postdata);
				try
				{
					_android_iab_validating_handler.Execute();
				}
				catch (Exception e)
				{
					LogHelper.LogException("AndroidValidatingReceiptHandler.Execute", e);
				}
			}
		}
		else if (ProjectConfig.IS_SERVER_PIG == 1)
		{
			_pig_payment_handler = new PigPaymentHandler(this, postdata);
			try
			{
				_pig_payment_handler.Execute();
			}
			catch (Exception e)
			{
				LogHelper.LogException("PigPaymentHandler.Execute", e);
			}
		}
	}
	
	public void write(byte[] data) throws Exception
	{
		//compress data before respond to client
		byte[] compressed_data = Misc.CompressGZIP(data);
		
		_server_handler.WriteResponse(compressed_data, "application/octet-stream", HttpHeaders.Values.GZIP);
	}
	
	public void writeRaw(byte[] data) throws Exception
	{		
		_server_handler.WriteResponse(data, "application/octet-stream", HttpHeaders.Values.IDENTITY);
	}
	
	public void write(String json) throws Exception
	{
		_server_handler.WriteResponse(json);
	}
}
