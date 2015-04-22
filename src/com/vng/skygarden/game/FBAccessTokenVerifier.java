package com.vng.skygarden.game;

import com.vng.zaloSDK.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vng.log.LogHelper;
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


public class FBAccessTokenVerifier extends HttpResponseHandler
{
	private final int CONNECT_TIMEOUT    = 1000;
    private final int IDLE_TIME          = 5000;
    private final int CONTENT_LENGTH     = 4096;
	private final String FACEBOOK_URL		= "https://graph.facebook.com/debug_token?";
	
	private final String FB_CLIENT_ID = "375025142630446";
	private final String FB_CLIENT_SECRECT = "3e2c9a3e00e8c21e050bbc72295bb427";
	private final String FB_APP_ACCESS_TOKEN = ProjectConfig.FACEBOOK_APP_CLIENT_ID + "|" + ProjectConfig.FACEBOOK_APP_ACCESS_TOKEN;
	FBAuthenticateTask _fb_authenticate_task;
	String _access_token;
	String _facebook_id;
	
    public FBAccessTokenVerifier(FBAuthenticateTask fb_authenticate_task, String access_token, String facebook_id) throws Exception
	{
		this._fb_authenticate_task = fb_authenticate_task;
		this._access_token = access_token;
		this._facebook_id = facebook_id;
        request();
    }

    private void request() throws Exception
	{
		StringBuilder url = new StringBuilder();
		url.append(FACEBOOK_URL);
		url.append("input_token=").append(_access_token);
		url.append("&access_token=").append(FB_APP_ACCESS_TOKEN);
		Http.sendHttpRequest(Common.outLoopGroup, "GET", url.toString(), null, this, CONNECT_TIMEOUT, IDLE_TIME, CONTENT_LENGTH);
    }

    private void response(int return_code)
	{
		_fb_authenticate_task.FBCallback(return_code);
    }

    @Override
    public void read(HttpResponse httpResponse, ByteBuf byteBuf) throws Exception
	{
		String response = byteBuf.toString(CharsetUtil.UTF_8);
        close();
		if (response.contains("true") && response.contains(FB_CLIENT_ID) && response.contains(_facebook_id))
		{
			response(ReturnCode.RESPONSE_OK);
		}
		else
		{
			response(ReturnCode.RESPONSE_FB_AUTHENTICATE_FAIL);
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
