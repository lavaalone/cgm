package com.vng.zaloSDK;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vng.log.LogHelper;
import firebat.framework.io.http.Http;
import firebat.framework.io.http.HttpResponseHandler;
import firebat.framework.log.Log;
import firebat.framework.util.Json;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.timeout.IdleState;
import io.netty.util.CharsetUtil;
import java.util.HashMap;
import com.vng.skygarden.game.ZaloAuthenticateTask;

public class FriendHandler extends HttpResponseHandler
{
    public final static int CONNECT_TIMEOUT			= 1000;
    public final static int IDLE_TIME				= 5000;
    public final static int MAX_CONTENT_LENGTH		= 128000;
    public final static int RECORD_PER_REQUEST		= 100;
	public final static String zaloUrl				= "http://openapi.zaloapp.com/query";

	String accessToken;
    private int pos;

    private String _friend_list;
	ZaloAuthenticateTask zAuthenTask;

    public FriendHandler(ZaloAuthenticateTask zaTask, String atk) throws Exception
    {
		accessToken = atk;
		zAuthenTask = zaTask;
        request();
    }

    private void request() throws Exception
    {
		StringBuilder url = new StringBuilder(zaloUrl).append("?act=lstfri&appid=").append(Common.APP_ID)
				.append("&pos=").append(pos)
				.append("&count=").append(RECORD_PER_REQUEST)
				.append("&accessTok=").append(accessToken);
		LogHelper.Log("zalo FriendHandler request: " + url.toString());
		Http.sendHttpRequest(Common.outLoopGroup, "GET", url.toString(), null, this, CONNECT_TIMEOUT, IDLE_TIME, MAX_CONTENT_LENGTH);
		pos += RECORD_PER_REQUEST;
    }

    private void response (String _friend_list)
    {
		LogHelper.Log("zalo friend list: " + _friend_list);
		zAuthenTask.ZaloCallback(_friend_list);
    }

    @Override
    public void active () throws Exception
    {
        super.active();
    }

    @Override
    public void inactive () throws Exception
    {
    }

    @Override
    public void exceptionCaught (Throwable cause)
    {
        close();
        response(null);
    }

    @Override
    public void idle (IdleState state) throws Exception
    {
        close();
        response(null);
    }

    @Override
    public void read (HttpResponse httpResponse, ByteBuf contentByteBuf) throws Exception
    {
        JsonObject request = new JsonParser().parse(contentByteBuf.toString(CharsetUtil.UTF_8)).getAsJsonObject();
        close();
		LogHelper.Log("zalo request: " + request);
        if (request.get("error").getAsInt() >= 0)
        {
			if (request.get("result").isJsonArray())
			{
				JsonArray result = request.get("result").getAsJsonArray();
				if (result.size() > 0)
				{
					StringBuilder _friend_list = new StringBuilder();
					_friend_list.append("dump_zalo_id").append(';');
					
					for (JsonElement e : result)
					{
						JsonObject jsObj = e.getAsJsonObject();
						if (jsObj.get("usingApp").getAsBoolean())
						{						
							_friend_list.append(jsObj.get("userId").getAsString()).append(';');
						}
					}
					if (result.size() < RECORD_PER_REQUEST)
						response(_friend_list.toString());
					else
						request();
				}
				else
				{
					response(null);
				}
			}
			else
			{
				response(null);
			}
        }
        else
        {
            response(null);
        }
    }
}