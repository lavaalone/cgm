package com.vng.zaloSDK;

import io.netty.channel.nio.NioEventLoopGroup;

public class Common
{
	public static NioEventLoopGroup outLoopGroup = new NioEventLoopGroup();
    public final static String APP_ID				= "4276041421271731779";
    public final static String SECRET_KEY			= "ORTqw45xY7w1cOBrf0nU";

	public static void stop()
	{
		outLoopGroup.shutdownGracefully();
	}

    public static void log(Object var)
	{
		System.out.println(var);
	}
}
