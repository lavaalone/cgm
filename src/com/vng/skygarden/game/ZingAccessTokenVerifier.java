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


public class ZingAccessTokenVerifier extends HttpResponseHandler
{
	private final int CONNECT_TIMEOUT    = 1000;
    private final int IDLE_TIME          = 5000;
    private final int CONTENT_LENGTH     = 4096;
	private final String ZINGME_URL		= "https://graphapi-me.zing.vn/me/@";
	private final String ZING_APP_NAME = "khuvuontrenmay";
	
	ZingAuthenticateTask _z_authenticate_task;
	String _access_token;
	String _zing_id;
	String _zing_name;
	
    public ZingAccessTokenVerifier(ZingAuthenticateTask z_authenticate_task, String access_token, String zing_id, String zing_name) throws Exception
	{
		this._z_authenticate_task = z_authenticate_task;
		this._access_token = access_token;
		this._zing_id = zing_id;
		this._zing_name = zing_name;
        request();
    }

    private void request() throws Exception
	{
		StringBuilder url = new StringBuilder();
		url.append(ZINGME_URL).append(ZING_APP_NAME);
		url.append("?access_token=").append(_access_token);
		LogHelper.Log("zing url out = " + url.toString());
		Http.sendHttpRequest(Common.outLoopGroup, "GET", url.toString(), null, this, CONNECT_TIMEOUT, IDLE_TIME, CONTENT_LENGTH);
    }

    private void response(int return_code)
	{
		_z_authenticate_task.ZMCallback(return_code);
    }

    @Override
    public void read(HttpResponse httpResponse, ByteBuf byteBuf) throws Exception
	{
		String response = byteBuf.toString(CharsetUtil.UTF_8);
        close();
		if (response.contains("Successful") && response.contains(_zing_id) && response.contains(_zing_name))
		{
			response(ReturnCode.RESPONSE_OK);
		}
		else
		{
			response(ReturnCode.RESPONSE_ZING_AUTHENTICATE_FAIL);
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
