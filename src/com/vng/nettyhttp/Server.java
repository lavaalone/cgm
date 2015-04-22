package com.vng.nettyhttp;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.Channel;
import java.util.concurrent.*;

import com.vng.taskqueue.*;
import java.util.concurrent.atomic.AtomicLong;

public class Server
{
	private static final int	_SYSTEM_MONITOR_INITIALDELAY = 30;
	private static final int	_SYSTEM_MONITOR_DELAY = 30;
	
	private final static ScheduledExecutorService s_systemMonitor = Executors.newSingleThreadScheduledExecutor();
	
	private static ServerBootstrap s_serverBootstrap;	
	private static Channel s_serverChannel;	
	private static InetSocketAddress s_isa;
	private static int s_numThread;
	
	public static TaskQueue _task_queue;
	public static AtomicLong	_channel_id = new AtomicLong(0L);
	
	public static void Init(InetSocketAddress isa, int num_thread, int num_thread_for_task_queue) throws Exception
	{	
		s_isa = isa;
		s_numThread = num_thread;
		
		Start();
		
		//Init task queue
		_task_queue = new TaskQueue(1000, num_thread_for_task_queue, num_thread_for_task_queue*3/2);
		
		//Init scheduler task
		s_systemMonitor.scheduleWithFixedDelay(new com.vng.util.Monitor(), _SYSTEM_MONITOR_INITIALDELAY, _SYSTEM_MONITOR_DELAY, TimeUnit.SECONDS);
	}
	
	public static void Stop() throws Exception
	{
		if (_task_queue != null)
		{
			_task_queue.RejectNewTask();
		}
		
		if (s_serverChannel != null)
		{
			s_serverChannel.close().awaitUninterruptibly();
			Thread.sleep(100);			
		}
		
		if (s_serverBootstrap != null)
		{
			s_serverBootstrap.releaseExternalResources();
			Thread.sleep(100);
		}
		
		//stop task queue
		if (_task_queue != null)
		{
			while (true)
			{
				if (_task_queue.IsFinishAllTask())
				{
					_task_queue.StopAllTask();
					break;
				}
				else
				{
					try
					{
						Thread.sleep(100);
					}
					catch (Exception ex)
					{
					}
				}
			}
		}
		
		s_serverChannel = null;
		s_serverBootstrap = null;
		_task_queue = null;
	}
	
	public static void Start() throws Exception
	{
		Stop();
		
		s_serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool(), s_numThread));
		s_serverBootstrap.setOption("child.tcpNoDelay", true); 		// better latency over bandwidth
        s_serverBootstrap.setOption("child.keepAlive", true);		//
		s_serverBootstrap.setOption("reuseAddress", true); 			// kernel optimization
		s_serverBootstrap.setOption("child.reuseAddress", true); 	// kernel optimization
		
		s_serverBootstrap.setPipelineFactory(new ServerPipelineFactory());
		s_serverChannel = s_serverBootstrap.bind(s_isa);
	}
}
