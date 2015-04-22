package com.vng.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import static org.jboss.netty.buffer.ChannelBuffers.*;

import com.vng.util.*;

public class ServerEncoder extends OneToOneEncoder 
{
    /**
     * Creates a new instance with the current system character set.
     */
    public ServerEncoder() 
	{
		super();
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception 
	{
        if (msg instanceof SimpleDataFormat)
		{
			SimpleDataFormat sdf = (SimpleDataFormat) msg;
			
			byte[] data = sdf.GetData();
			ChannelBuffer buff = channel.getConfig().getBufferFactory().getBuffer(data.length + 9);
			
			//calc crc
			int crc = Misc.MurmurHash(data);
			
			//type
			if (sdf.IsUseGzip())
			{
				buff.writeByte(101);
			}
			else
			{
				buff.writeByte(100);
			}
			//crc
			buff.writeInt(crc);
			//length
			buff.writeInt(data.length);
			//data body
			buff.writeBytes(data);
			
			return buff;
        }

        return msg;
    }
}
