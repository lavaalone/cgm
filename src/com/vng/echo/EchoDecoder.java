package com.vng.echo;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import org.apache.log4j.Logger;

import com.vng.util.*;
import com.vng.log.*;

public class EchoDecoder extends FrameDecoder
{
	public EchoDecoder() 
	{
		super();
    }
	
	@Override
	protected Object decode (ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception
	{
		buffer.markReaderIndex();
		
		if (buffer.readableBytes() >= 4)
		{
			int data_len = buffer.readInt();
			
			if (buffer.readableBytes() < data_len)
			{
				buffer.resetReaderIndex();
			}
			else
			{
				return buffer.readBytes(data_len).array();
			}
		}
		else
		{
			buffer.resetReaderIndex();
		}
		
		LogHelper.Log(LogHelper.LogType.NEWSBOARD, "------------------------ EchoDecoder decode");
		return null;
	}
}