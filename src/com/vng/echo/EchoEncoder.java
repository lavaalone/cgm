package com.vng.echo;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import org.jboss.netty.buffer.ChannelBuffers.*;

import com.vng.util.*;
import com.vng.log.*;

public class EchoEncoder extends OneToOneEncoder 
{
    public EchoEncoder() 
	{
		super();
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) // throws Exception 
	{
		if (msg instanceof byte[])
		{
			byte[] data = (byte[])msg;
			
			ChannelBuffer buff = channel.getConfig().getBufferFactory().getBuffer(data.length + 4);
			buff.writeInt(data.length);
			buff.writeBytes(data);
			
			return buff;
		}
		
		LogHelper.Log(LogHelper.LogType.NEWSBOARD, "------------------------ EchoEncoder encode");
		return msg;
    }
}