package com.vng.nettyhttp;

import com.vng.log.LogHelper;
import java.util.*;
import java.util.Map.Entry;
import java.net.InetSocketAddress;
import java.net.URLDecoder;


import org.jboss.netty.channel.*;
import org.jboss.netty.buffer.*;

import org.jboss.netty.handler.timeout.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.codec.http.multipart.*;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import org.jboss.netty.handler.ssl.SslHandler;

import org.apache.log4j.Logger;

import com.vng.util.*;
import com.vng.skygarden._gen_.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.jboss.netty.util.CharsetUtil;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class ServerHandler extends IdleStateAwareChannelUpstreamHandler
{
	private static final HttpDataFactory 	_data_factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed MINSIZE
	
	private long 					_connected_time;
	
//	private byte[]					_response_data;
//	private String					_response_type;
//	private String					_response_encode;
	private String					_response_json;
	
	private boolean					_is_closed;
	private boolean					_is_idle;
	
	private Client					_client = null;
	protected String 				_address;
	protected String 				_real_address;
	protected boolean 				_is_keep_alive = true;
	
	private long					_channel_id = -1;

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
		_channel_id = Server._channel_id.incrementAndGet();
		_connected_time = System.currentTimeMillis();
		
		Monitor.IncrConnection();
		
		LogHelper.Log("channelOpen with channel_id=" + _channel_id);
	}	
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
		_response_json = null;
		_client = null;
		
		Monitor.DecrConnection((int)(System.currentTimeMillis() - _connected_time));
		LogHelper.Log("channelClosed with channel_id=" + _channel_id);
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
        //
		if (ProjectConfig.USE_HTTPS)
		{
			// Get the SslHandler in the current pipeline.
			// We added it in ServerPipelineFactory.
			final SslHandler ssl_handler = (SslHandler)(ctx.getPipeline().get("ssl"));

			// Get notified when SSL handshake is done.
			ChannelFuture handshakeFuture = ssl_handler.handshake();
			handshakeFuture.addListener(new Greeter());
		}
		
		_address = _real_address = ((InetSocketAddress) e.getChannel().getRemoteAddress()).getAddress().getHostAddress();		
		LogHelper.Log("channelConnected with channel_id=" + _channel_id + "&ip=" + _address);
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
		LogHelper.Log("channelDisconnected with channel_id=" + _channel_id + "&ip=" + _address);
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
		LogHelper.Log("messageReceived start with channel_id=" + _channel_id + "&ip=" + _address);
		
		Channel channel = e.getChannel();		
		HttpRequest request = (HttpRequest)e.getMessage();
		
		//don't support HTTP code 100 continue
		if (HttpHeaders.is100ContinueExpected(request)) 
		{
			LogHelper.Log("messageReceived[" + _channel_id + "].. is100ContinueExpected");
			channel.close();			
			return;
		}
		
		//don't support chunk HTTP request
		if (request.isChunked())
		{
			LogHelper.Log("messageReceived[" + _channel_id + "].. isChunked");
			channel.close();						
			return;
		}
		
		//check connection keep alive
		if (HttpHeaders.Values.CLOSE.equals(request.getHeader("Connection")))
		{
			_is_keep_alive = false;
			LogHelper.Log("messageReceived[" + _channel_id + "] _is_keep_alive=false");
		}

		byte[] request_content = null;
		ChannelBuffer request_content_buf = null;
		
		_response_json = null;
		
		HttpMethod http_method = request.getMethod();
		
		if (ProjectConfig.IS_SERVER_PAYMENT == 1 || ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1 || ProjectConfig.IS_SERVER_ZALO_PAYMENT == 1)
		{
			String uri = request.getUri();
			LogHelper.Log("messageReceived[" + _channel_id + "].. uri := " + uri);
			
			if (http_method.equals(HttpMethod.POST))
			{
				LogHelper.Log("HTTP method POST");
				if (uri.equals("/so6payment.html")) // handle payment request from so6 payment gateway
				{
					String postdata = request.getContent().toString(org.jboss.netty.util.CharsetUtil.UTF_8);
					LogHelper.Log("messageReceived[" + _channel_id + "].. postdata: " + postdata);

					if (postdata.length() > 0)
					{
						//run payment logic
						if (_client == null)
						{
							_client = new Client(this);
						}

						_client.MessageReceived(postdata);
					}
				}
				else if (uri.equals("/refill.html")) // receive refill request from server logic
				{
					// get json from http data
					String urlParameters = request.getContent().toString(org.jboss.netty.util.CharsetUtil.UTF_8);
					
					URL url;
					if (ProjectConfig.IS_SERVER_PAYMENT == 1)
						url = new URL(ProjectConfig.SO6_PAY_GATE);
					else
						url = new URL(ProjectConfig.SO6_PAY_GATE_TEST);

					LogHelper.Log("messageReceived[" + _channel_id + "].. urlParameters: " + urlParameters);
					LogHelper.Log("messageReceived[" + _channel_id + "].. url: " + url);

					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type", "application/json");
					connection.setRequestProperty("Host", "smspaytest.g6-mobile.zing.vn");
					connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
					connection.setRequestProperty("Content-Language", "en-US");
					connection.setDoOutput(true);

					DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
					wr.writeBytes(urlParameters);
					wr.flush();
					wr.close();

					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String line = "";
					while ((line = reader.readLine()) != null) 
					{
						LogHelper.Log("messageReceived[" + _channel_id + "].. response from so6: " + line);
						_response_json = line;
					}
				}
				else if (uri.equals("/zalopay.html"))
				{
					String postdata = request.getContent().toString(org.jboss.netty.util.CharsetUtil.UTF_8);
					LogHelper.Log("messageReceived[" + _channel_id + "].. postdata: " + postdata);

					if (postdata.length() > 0)
					{
						//run payment logic
						if (_client == null)
						{
							_client = new Client(this);
						}

						_client.MessageReceived(postdata);
					}
				}
				else if (uri.equals("/pig.html"))
				{
					String postdata = request.getContent().toString(org.jboss.netty.util.CharsetUtil.UTF_8);
					LogHelper.Log("messageReceived[" + _channel_id + "].. postdata: " + postdata);
					
					if (postdata.length() > 0)
					{
						//run payment logic
						if (_client == null)
						{
							_client = new Client(this);
						}

						_client.MessageReceived(postdata);
					}
				}
				else
				{
					LogHelper.Log("messageReceived[" + _channel_id + "].. err! don't support this uri=" + uri);
					String postdata = request.getContent().toString(org.jboss.netty.util.CharsetUtil.UTF_8);
					LogHelper.Log("messageReceived[" + _channel_id + "].. postdata: " + postdata);
				}
			}
			else
			{
				LogHelper.Log("HTTP method GET");
				if (uri.contains("WinIAP.html"))
				{
					uri = uri.replace("WinIAP.html/validating?", "");

					if (_client == null)
					{
						_client = new Client(this);
					}

					_client.MessageReceived(uri);
				}
				else if (uri.contains("androidIAB.html"))
				{
					uri = uri.replace("androidIAB.html/validating?", "");

					//run social handler logic
					if (_client == null)
					{
						_client = new Client(this);
					}

					_client.MessageReceived(uri);
				}
				else
				{
					LogHelper.Log("messageReceived[" + _channel_id + "].. err! don't support this http method");
					String postdata = request.getContent().toString(org.jboss.netty.util.CharsetUtil.UTF_8);
					LogHelper.Log("messageReceived[" + _channel_id + "].. postdata: " + postdata);
					//don't support other methods	
					if (!_is_keep_alive)
					{
						channel.close();
					}

					return;
				}
			}
		}
		else if (ProjectConfig.IS_SERVER_SOCIAL == 1)
		{
			if (http_method.equals(HttpMethod.POST))
			{
				String uri = request.getUri();
				
				if (uri.equals("/fbsharing.html")) // receive request from server social
				{
					// get json from http data
					String postdata = request.getContent().toString(org.jboss.netty.util.CharsetUtil.UTF_8);
					LogHelper.Log("messageReceived[" + _channel_id + "].. postdata: " + postdata);

					if (postdata.length() > 0)
					{
						//run social handler logic
						if (_client == null)
						{
							_client = new Client(this);
						}

						_client.MessageReceived(postdata);
					}
				}
				
			}
		}
		else if (ProjectConfig.IS_SERVER_SMS == 1)
		{
			if (http_method.equals(HttpMethod.GET))
			{
				String uri = request.getUri();
				String phone_number = "";
				
				if (uri.contains("/sms?phone="))
				{
					phone_number = uri.replace("/sms?phone=", "");
				}
				
				if (phone_number.length() > 0)
				{
					//run social handler logic
					if (_client == null)
					{
						_client = new Client(this);
					}

					_client.MessageReceived(phone_number);
				}
			}
		}
		else if (ProjectConfig.IS_SERVER_GLOBAL_PAYMENT == 1)
		{
			if (http_method.equals(HttpMethod.GET))
			{
				String uri = request.getUri();
				LogHelper.Log("HTTP method GET");
				LogHelper.Log("uri = " + uri);
				if (uri.contains("appleIAP.html"))
				{
					uri = uri.replace("appleIAP.html/validating?", "");

					//run social handler logic
					if (_client == null)
					{
						_client = new Client(this);
					}

					_client.MessageReceived(uri);
				}
				else if (uri.contains("androidIAB.html"))
				{
					uri = uri.replace("androidIAB.html/validating?", "");

					//run social handler logic
					if (_client == null)
					{
						_client = new Client(this);
					}

					_client.MessageReceived(uri);
				}
				else
				{
					LogHelper.Log("Not support this URI");
					String postdata = request.getContent().toString(org.jboss.netty.util.CharsetUtil.UTF_8);
					LogHelper.Log("messageReceived[" + _channel_id + "].. postdata: " + postdata);
					//don't support other methods	
					if (!_is_keep_alive)
					{
						channel.close();
					}

					return;
				}
			}
			else
			{
				LogHelper.Log("messageReceived[" + _channel_id + "].. err! don't support this http method");
				String postdata = request.getContent().toString(org.jboss.netty.util.CharsetUtil.UTF_8);
				LogHelper.Log("messageReceived[" + _channel_id + "].. postdata: " + postdata);
				//don't support other methods	
				if (!_is_keep_alive)
				{
					channel.close();
				}

				return;
			}
		}
		else if (ProjectConfig.IS_SERVER_PIG == 1)
		{
			if (http_method.equals(HttpMethod.POST))
			{
				String uri = request.getUri();
				
				if (uri.equals("/pig.html")) // receive request from server pig
				{
					// get json from http data
					String postdata = request.getContent().toString(org.jboss.netty.util.CharsetUtil.UTF_8);
					LogHelper.Log("messageReceived[" + _channel_id + "].. postdata: " + postdata);

					if (postdata.length() > 0)
					{
						//run social handler logic
						if (_client == null)
						{
							_client = new Client(this);
						}

						_client.MessageReceived(postdata);
					}
				}
			}
			else
			{
				LogHelper.Log("messageReceived[" + _channel_id + "].. err! don't support this http method");
				if (!_is_keep_alive)
				{
					channel.close();
				}

				return;
			}
		}
		
		
		if (_response_json.equals("") || _response_json.length() == 0)
		{
			if (!_is_keep_alive)
			{
				channel.close();
				LogHelper.Log("messageReceived[" + _channel_id + "].. channel close with empty response json");
			}
		}
		else
		{
			if (_is_closed)
			{
				LogHelper.Log("messageReceived[" + _channel_id + "].. channel already closed");
				return;
			}
			
			LogHelper.Log("messageReceived[" + _channel_id + "].. _response_json: " + _response_json);
			
			HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
			response.setContent(ChannelBuffers.copiedBuffer(_response_json, CharsetUtil.UTF_8));
			response.setHeader("Content-Type", "application/json");
			response.setHeader("Content-Length", "" + Integer.toString(_response_json.getBytes().length));

			if (!_is_keep_alive)
			{
				//Write the response and close connection
				response.setHeader("Connection", "close");
				ChannelFuture future = channel.write(response);					
				future.addListener(ChannelFutureListener.CLOSE);
				LogHelper.Log("messageReceived[" + _channel_id + "].. response json with add header close");
			}
			else
			{
				channel.write(response);
				LogHelper.Log("messageReceived[" + _channel_id + "].. response json");
			}
		}
		
		LogHelper.Log("messageReceived end with channel_id=" + _channel_id + "&ip=" + _address);
	}
	
	@Override
	public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception
	{
		_is_closed = true;
		_is_idle = true;
		
		e.getChannel().close();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
	{
		Monitor.IncrException();
		
		Throwable cause = e.getCause();		
		if (cause != null)
		{
			boolean should_write_log = true;
			
//			if ( 	(cause instanceof java.nio.channels.ClosedChannelException) || 
//					(	cause instanceof java.io.IOException && 
//						cause.getMessage() != null && 
//						cause.getMessage().equals("Connection reset by peer")
//					)
//				)
//			{
//				should_write_log = false;
//			}
			
			if (should_write_log)
			{
				LogHelper.LogException("Netty exceptionCaught", cause);
			}
		}
		
		_is_closed = true;
		e.getChannel().close();
	}	
	
	public void WriteResponse(byte[] data, String type, String encode) 
	{
    }
	
	public void WriteResponse(String json)
	{
		_response_json = json;
	}
	
	private static final class Greeter implements ChannelFutureListener 
	{
		Greeter() 
		{
		}

		public void operationComplete(ChannelFuture future) throws Exception 
		{
			if (future.isSuccess() == false) 
			{
				future.getChannel().close();
			}
		}
	}
}



