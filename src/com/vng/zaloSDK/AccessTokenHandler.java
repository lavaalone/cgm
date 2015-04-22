package com.vng.zaloSDK;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vng.log.LogHelper;
import firebat.framework.io.http.Http;
import firebat.framework.io.http.HttpResponseHandler;
import firebat.framework.log.Log;
import firebat.framework.util.Monitor;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.timeout.IdleState;
import io.netty.util.CharsetUtil;
import com.vng.skygarden.game.ZaloAuthenticateTask;


public class AccessTokenHandler extends HttpResponseHandler
{
	public final static int CONNECT_TIMEOUT    = 1000;
    public final static int IDLE_TIME          = 5000;
    public final static int CONTENT_LENGTH     = 4096;
    public final static String zaloUrl = "https://oauth.zaloapp.com/v2/access_token";

    private String code;
	ZaloAuthenticateTask zAuthenTask;

    public AccessTokenHandler(ZaloAuthenticateTask zaTask, String code) throws Exception
	{
        zAuthenTask = zaTask;
		this.code = code;
        request();
    }

    private void request() throws Exception
	{
        StringBuilder url = new StringBuilder(zaloUrl).append("?app_id=").append(Common.APP_ID)
                .append("&app_secret=").append(Common.SECRET_KEY)
                .append("&code=").append(code);
		LogHelper.Log("Do AccessTokenHandler request: " + url.toString());
        Http.sendHttpRequest(Common.outLoopGroup, "GET", url.toString(), null, this, CONNECT_TIMEOUT, IDLE_TIME, CONTENT_LENGTH);
    }

    private void response(String accessToken)
	{
        LogHelper.Log("*CODE=" + code + ";*TOKEN=" + accessToken);
		zAuthenTask.ZaloCallback(accessToken);
    }

    @Override
    public void read(HttpResponse httpResponse, ByteBuf byteBuf) throws Exception
	{
		LogHelper.Log("read");
        JsonObject request = new JsonParser().parse(byteBuf.toString(CharsetUtil.UTF_8)).getAsJsonObject();
        close();
		LogHelper.Log("Do AccessTokenHandler response: " + request);
        if (request.has("access_token"))
            response(request.get("access_token").getAsString());
        else
            response(null);
    }

    @Override
    public void exceptionCaught(Throwable cause)
	{
		LogHelper.Log("exceptionCaught");
        close();
        response(null);
    }

    @Override
    public void inactive() throws Exception
	{
    }

    @Override
    public void idle(IdleState idleState) throws Exception
	{
		LogHelper.Log("idle");
        close();
        response(null);
    }
}
