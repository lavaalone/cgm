package com.vng.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import org.apache.log4j.Logger;

import com.vng.util.*;

public class ServerDecoder extends FrameDecoder
{
	public ServerDecoder() 
	{
		super();
    }
	
	@Override
	protected Object decode (ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception
	{
		if (buffer.readableBytes() < 1)	
		{
			return null;		
		}		
		buffer.markReaderIndex();
		
		int type = buffer.readUnsignedByte();
		
		if (type == 100 || type == 101)
		{
			if (buffer.readableBytes() >= 8)
			{
				int hash = buffer.readInt();
				int data_len = buffer.readInt();	
				if (data_len == 0 || data_len > 1000000)
				{
					buffer.resetReaderIndex();
					throw new CorruptedFrameException("Server decoder: packet size too large: " + data_len);
				}
				
				if (buffer.readableBytes() < data_len)
				{
					buffer.resetReaderIndex();
				}
				else
				{
					byte[] data = buffer.readBytes(data_len).array();
					
					//check crc
					int recalc_hash = Misc.MurmurHash(data);
					
					if (hash == recalc_hash)
					{
						//return data
						return new SimpleDataFormat(data, (type == 101));
					}
					else
					{
						throw new CorruptedFrameException("Server decoder: check sum fail !");
					}
				}
			}
			else
			{
				buffer.resetReaderIndex();
			}
		}
		else
		{				
			throw new CorruptedFrameException("Server decoder: wrong data format !");
		}
		
		return null;
	}
}



