package com.vng.netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

import com.vng.taskqueue.*;
import com.vng.skygarden.game.*;
import com.vng.skygarden.DBConnector;
import com.vng.skygarden._gen_.ProjectConfig;
import com.vng.util.*;

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
	public static Object[][][] s_globalDB;
	public static Object[][][] s_globalGift;
	public static byte[][] s_globalSheetData;
	public static ConcurrentHashMap<String, SkyGardenUser> s_serverUserOnline;
	public static ConcurrentHashMap<Long, SkyGardenUser> s_recentOnlineUser = new ConcurrentHashMap<Long, SkyGardenUser>();
	public static long[][][] s_globalMachineUnlockData;
	
	public static IBShopManager s_ibShopManager;
	public static ConcurrentHashMap<Integer, String> s_broadcast_list;
	public static int		_server_status;
	public static String	_forward_server_ip = "";
	public static int		_forward_server_port = 0;
	
	
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
		// init global objects
		s_serverUserOnline = new ConcurrentHashMap<String, SkyGardenUser>();
		
		// ibshop
		s_ibShopManager = new IBShopManager();
		s_ibShopManager.SetDatabase(DBConnector.GetMembaseServerForGeneralData());
		s_ibShopManager.loadIBShopPackages();
		
		// broadcast list
		s_broadcast_list = new ConcurrentHashMap<Integer, String>();
		
		// create NPC if not existed
		if (ParseUserData.initNPCAccount())
		{
			ParseUserData.editNPCInfos();
		}
		
		_server_status = DatabaseID.SERVER_STATUS_READY;
		if (ProjectConfig.IS_SERVER_LOGIC == 1 && ProjectConfig.RUN_LOCAL == 0)
		{
			_server_status = DatabaseID.SERVER_STATUS_PAUSE;
		}
		
		// --------------------------------------------------------------------------
		
		Stop();
		
		s_serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool(), s_numThread));
		s_serverBootstrap.setOption("child.tcpNoDelay", true); 		// better latency over bandwidth This option instructs TCP not to wait around until enough data is queued up, but to send whatever data you write to it immediately.
        s_serverBootstrap.setOption("child.keepAlive", true);		//
		s_serverBootstrap.setOption("reuseAddress", true); 			// kernel optimization
		s_serverBootstrap.setOption("child.reuseAddress", true); 	// kernel optimization
		
		s_serverBootstrap.setPipelineFactory(new ServerPipelineFactory());
		s_serverChannel = s_serverBootstrap.bind(s_isa);
	}
}
