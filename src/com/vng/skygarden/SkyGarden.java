package com.vng.skygarden;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import java.util.*;
import java.io.*;
import java.util.Map.Entry;

import org.apache.log4j.*;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.*;
import org.apache.log4j.PropertyConfigurator;

import com.vng.log.*;
import com.vng.util.*;

import com.vng.skygarden._gen_.*;
import com.vng.skygarden.game.*;

import com.vng.netty.*;
import com.vng.nettyUDP.*;
import com.vng.echo.*;
import static com.vng.netty.Server.s_broadcast_list;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


public class SkyGarden
{	
	public static String	s_serverAddress = "";
	public static int		s_serverPort;
	public static String	s_server_local_address = "";
	
	public static MySQLConnector _sql_connector;
	public static RankArrangement _rank_arrangement;
	public static ConcurrentHashMap<String, RankingInfo> _ranking_info;
	public static AdvertiseManager _ads_manager;
	public static EventNotification _event_notification;
	public static OfferManager _offer_manager;
	public static ConcurrentHashMap<String, Integer> _server_config;
	public static ConcurrentHashMap<Integer, String> _active_users;
	
	// bonus bug appear ratio base on user level
	public static ConcurrentHashMap<Integer, String[]> _bug_bonus;
	
	// xmas tree 2014
	public static AtomicLong _event_counter;
	public static boolean _is_use_server_event = false;
	
	public static int		_ccu_limit = 0;
	public static String	_server_list = "";
	public static int		_server_id = -1;
	
	public static void main(String[] args)
	{
		LogHelper.Log("==================================================");
		LogHelper.Log("\n*** Start server version " + ProjectConfig.VERSION + " (" + ProjectConfig.BUILD_TIME +") ***");
		LogHelper.Log("CurrentDir: " + System.getProperty("user.dir"));
		try
		{
			//get params
			String hostAddress				= args[0].trim();
			int hostPort					= Integer.parseInt(args[1]);
			int num_thread_for_netty		= Integer.parseInt(args[2]);
			int num_thread_for_task_queue	= Integer.parseInt(args[3]);
			String local_address			= args[4].trim();
			int ccu_limit					= Integer.parseInt(args[5]);
			String server_list				= args[6].trim();
			
			// set static params
			s_serverAddress			= hostAddress; // not use anymore
			s_serverPort			= hostPort;
			s_server_local_address	= "0.0.0.0"; //local_address;
			_ccu_limit				= ccu_limit;
			_server_list			= server_list;

			// auto detect local address
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			OUTSIDE: for (NetworkInterface netint : Collections.list(nets))
			{
				LogHelper.Log("Network Interface Display name: " + netint.getDisplayName());
				List<InterfaceAddress> list_interface = netint.getInterfaceAddresses();
				INNER: for (InterfaceAddress interface_address : list_interface)
				{
					short subnet_mask = interface_address.getNetworkPrefixLength();
					LogHelper.Log("Subnet mask : " + subnet_mask);
					
					if (subnet_mask == 24)
					{
						s_server_local_address = interface_address.getAddress().getHostAddress();
						LogHelper.Log("Found local address: " + s_server_local_address);
						break OUTSIDE;
					}
				}
			}
			
			// double check local address
			if (s_server_local_address.equals("0.0.0.0"))
			{
				LogHelper.Log("SYSTEM ERROR! CAN NOT GET LOCAL IP");
				System.exit(1);
				return;
			}
			
			switch (s_server_local_address)
			{
				case "10.30.81.46":
					_server_id = 1;
					break;
				case "10.30.81.50":
					_server_id = 2;
					break;
				case "10.30.81.45":
					_server_id = 3;
					break;
				case "10.30.81.52":
					_server_id = 4;
					break;
				case "10.30.81.53":
					_server_id = 5;
					break;
				case "10.30.81.54":
					_server_id = 6;
					break;
				case "10.30.81.55":
					_server_id = 7;
					break;
			}
			LogHelper.Log("Start server with: public ip = " + hostAddress + ", port = " + hostPort + ", thread for netty = " + num_thread_for_netty + ", thread for task queue = " + num_thread_for_task_queue + ", local ip address = " + local_address + ", ccu limit = " + ccu_limit + ", server list = " + server_list + ", server id = " + _server_id);
			
			//start log client
			if (ProjectConfig.RUN_LOCAL == 1 || ProjectConfig.IS_SERVER_FREESTYLE == 1 || ProjectConfig.IS_SERVER_CDN == 1 || ProjectConfig.IS_SERVER_RANKING == 1 || ProjectConfig.IS_SERVER_RANKING_FREESTYLE == 1)
			{
				StartLogFile(s_server_local_address, hostPort);
			}
			else
			{		
				StartLog(s_server_local_address, hostPort);
			}
			
			//load game data
			LogHelper.Log("Loading const...");
			if (ExcelData.LoadExcelData("./const") == false)
			{
				LogHelper.Log("Loading const: FAIL !");
				return;
			}

			Server.s_globalDB = ExcelData.GetData("db");
			
			if (ParseDB.checkDBValid() == false)
			{
				LogHelper.Log("Reading data failed!\n");
				return;
			}
			
			ParseDB.parseData();
			
			LogHelper.Log("Loading const OK !");
			
			//connect to database
			LogHelper.Log("Connecting to database...\n");
			if (DBConnector.InitDBConnection() == false)
			{
				LogHelper.Log("Connect to database: FAIL !");
				return;
			}
			LogHelper.Log("\nConnecting to database OK !");
			
			// load sheet data
			ParseDB.loadSheetData();
			LogHelper.Log("Load sheet data to ram OK !");
			
			ParseDB.loadMachineUnlockData();
			LogHelper.Log("Load machine unlock data to ram OK !");
			
			// init gift code from DB
			if (ProjectConfig.USE_GIFT_CODE == true)
			{
				Server.s_globalGift = ExcelData.GetData("gift");

				if (Server.s_globalGift == null)
				{
					LogHelper.Log("Can not load excel file.");
					return;
				}
				
				ParseDB.parseGiftCodeData();
				
				if (!ProjectConfig.USE_EXPORT_GIFT_CODE && !ProjectConfig.USE_IMPORT_GIFT_CODE)
				{
					ParseDB.initGiftCodeFromDB();
				}
				else
				{
					if (ProjectConfig.USE_EXPORT_GIFT_CODE == true) ParseDB.initGiftCodeFromDBToFile();
					if (ProjectConfig.USE_IMPORT_GIFT_CODE == true) ParseDB.importGiftCodeFromFileToDB();
				}
				
				System.out.println("\nGENERATED ALL GIFT CODES FROM DB!!!\nCtrl+C to exit.");
				System.exit(1);
				return;
			}
			
			// update key payment
			DBConnector.GetMembaseServerForTemporaryData().Add(KeyID.KEY_PAYMENT_ENABLE, ProjectConfig.USE_PAYMENT==true ? 1 : 0);
			DBConnector.GetMembaseServerForTemporaryData().Add(KeyID.KEY_PAYMENT_APPSTORE_ENABLE, ProjectConfig.USE_PAYMENT_APPSTORE==true ? 1 : 0);
			DBConnector.GetMembaseServerForTemporaryData().Add(KeyID.KEY_PAYMENT_USE_FIRST_PAY, 0); // key firstpay
			
			//Print out server configurations
			LogHelper.Log("");
			LogHelper.Log("------------------");
			if (ProjectConfig.RELEASE)
			{
				LogHelper.Log("Build     : Release");
			}
			else
			{
				LogHelper.Log("Build     : Debug");
			}
			
			LogHelper.Log("Address   : " + s_server_local_address);
			LogHelper.Log("Port      : " + s_serverPort);			
			LogHelper.Log("Version   : " + ProjectConfig.VERSION);
			
			if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_LIVE)
			{
				LogHelper.Log("Database  : LIVE");
			}
			else if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_DEV)
			{
				LogHelper.Log("Database  : DEV");
			}
			else if (ProjectConfig.USE_DATABASE == ProjectConfig.DATABASE_FILE)
			{
				LogHelper.Log("Database  : File Store");
			}
			
			LogHelper.Log("------------------");
			if (ProjectConfig.IS_SERVER_FREESTYLE == 1)
			{
				LogHelper.Log("SERVER HAPPY");
			}
			else if (ProjectConfig.IS_SERVER_LOGIC == 1)
			{
				LogHelper.Log("SERVER LOGIC");
			}
			else if (ProjectConfig.IS_SERVER_PAYMENT == 1)
			{
				LogHelper.Log("SERVER PAYMENT");
			}
			else if (ProjectConfig.IS_SERVER_PIG == 1)
			{
				LogHelper.Log("SERVER PAYMENT PIG");
			}
			else if (ProjectConfig.IS_SERVER_SOCIAL == 1)
			{
				LogHelper.Log("SERVER SOCIAL");
			}
			else if (ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1)
			{
				LogHelper.Log("SERVER PAYMENT HAPPY");
			}
			else if (ProjectConfig.IS_SERVER_CDN == 1)
			{
				LogHelper.Log("SERVER CDN");
			}
			else if (ProjectConfig.IS_SERVER_RANKING_FREESTYLE == 1)
			{
				LogHelper.Log("SERVER RANKING HAPPY");
			}
			else if (ProjectConfig.IS_SERVER_RANKING == 1)
			{
				LogHelper.Log("SERVER RANKING REAL");
			}
			else if (ProjectConfig.IS_SERVER_NEWSBOARD == 1)
			{
				LogHelper.Log("SERVER NEWSBOARD REAL");
			}
			else if (ProjectConfig.IS_SERVER_NEWSBOARD_FREESTYLE == 1)
			{
				LogHelper.Log("SERVER NEWSBOARD HAPPY");
			}
			else if (ProjectConfig.IS_SERVER_ZALO_LOGIC == 1)
			{
				LogHelper.Log("SERVER ZALO LOGIC");
			}
			else if (ProjectConfig.IS_SERVER_ZALO_HAPPY == 1)
			{
				LogHelper.Log("SERVER ZALO HAPPY");
			}
			else if (ProjectConfig.IS_SERVER_ZALO_PAYMENT == 1)
			{
				LogHelper.Log("SERVER ZALO PAYMENT");
			}
			
			LogHelper.Log("------------------");
			LogHelper.Log("");
			
			// add shutdown hook
			if (ProjectConfig.RUN_LOCAL != 1)
			{
				Runtime.getRuntime().addShutdownHook(new Thread() 
				{
					public void run() 
					{
						System.out.println("###DEBUG ..... addShutdownHook");
						System.out.println("addShutdownHook... online size: " + Server.s_serverUserOnline.size());
						// handle events before shutdown here
						while (Server.s_serverUserOnline.size() > 0)
						{
							System.out.println("addShutdownHook.. online size: " + Server.s_serverUserOnline.size());
							for (Map.Entry<String,SkyGardenUser> e : Server.s_serverUserOnline.entrySet())
							{
								SkyGardenUser u = e.getValue();
								if (u != null)
								{
									ServerHandler.removeUser(u.GetUserDeviceID(), ReturnCode.RESPONSE_SERVER_MAINTAIN);
								}
							}
						}
						
						try 
						{
							if (ProjectConfig.IS_SERVER_LOGIC == 1 || ProjectConfig.IS_SERVER_ZALO_LOGIC == 1)
							{
								// update server status
								UpdateServerStatus(s_server_local_address, s_serverPort, "Off");
								
								Thread.sleep(5000);
							}
							else
							{
								Thread.sleep(1000);
							}
						}
						catch (Exception e) 
						{
							e.printStackTrace();
						}

						System.out.println("addShutdownHook... !!!SEVER SHUTDOWN");
					}
				});
			}
			System.out.println("addShutdownHook... done!");
			
			if (ProjectConfig.RUN_LOCAL == 1)
			{
				// logic
				InetSocketAddress isa = new InetSocketAddress(hostPort);
				com.vng.netty.Server.Init(isa, num_thread_for_netty, num_thread_for_task_queue);
				LogHelper.Log("\nServer ready to use at [*:" + hostPort +"]......");
				
				// ranking
				LoadRankingInfo();
				LogHelper.Log("\nRanking Info Manager ready to use.......");

				// event notification
				LoadEventNotification();
				LogHelper.Log("\nEvent Notification Manager ready to use.......");

				LoadOfferManager();
				LogHelper.Log("\nOffer Manager ready to use.......");

				UpdateServerConfig();
				LogHelper.Log("\nStart update server misc.......");
				
//				// ranking
//				LoadRankingInfo();
//				LogHelper.Log("\nRanking Info Manager ready to use.......");
				
//				// udp
//				new UDPServer(s_server_local_address, s_serverPort);
//				LogHelper.Log("\nServer UDP ready to use......");
				
				DBConnector.GetMembaseServerForTemporaryData().Set("use_snow_str", "OFF");
			}
			else
			{
				if (ProjectConfig.IS_SERVER_PAYMENT == 1 || ProjectConfig.IS_SERVER_PAYMENT_FREESTYLE == 1) // server payment
				{
					// http server for payment
					InetSocketAddress isa = new InetSocketAddress(hostPort);
					com.vng.nettyhttp.Server.Init(isa, num_thread_for_netty, num_thread_for_task_queue);
					LogHelper.Log("\nServer HTTP ready to use at [*:" + hostPort + "] ......");
					
					// sql
					_sql_connector = new MySQLConnector();
					LogHelper.Log("\nMySQLConnector ready to use.");
					
					// udp
					if (ProjectConfig.IS_SERVER_PAYMENT == 1)
					{
						new UDPServer(s_server_local_address, ProjectConfig.UDP_PORT);
						LogHelper.Log("\nServer UDP ready to use at [" + ProjectConfig.PAYMENT_IP + ":" + ProjectConfig.UDP_PORT + "]......");
					}
				}
				else if (ProjectConfig.IS_SERVER_PIG == 1)
				{
					// http server for payment
					InetSocketAddress isa = new InetSocketAddress(hostPort);
					com.vng.nettyhttp.Server.Init(isa, num_thread_for_netty, num_thread_for_task_queue);
					LogHelper.Log("\nServer HTTP ready to use at [*:" + hostPort + "] ......");
				}
				else if (ProjectConfig.IS_SERVER_SOCIAL == 1) // server social
				{
					// http server for social task
					InetSocketAddress isa = new InetSocketAddress(ProjectConfig.SOCIAL_PORT);
					com.vng.nettyhttp.Server.Init(isa, num_thread_for_netty, num_thread_for_task_queue);
					LogHelper.Log("\nServer HTTP ready to use at [*:" + ProjectConfig.SOCIAL_PORT + "] ......");
					
					// udp
					new UDPServer(s_server_local_address, ProjectConfig.UDP_PORT);
					LogHelper.Log("\nServer UDP ready to use at [" + ProjectConfig.SOCIAL_IP + ":" + ProjectConfig.UDP_PORT + "]......");
				}
				else if (ProjectConfig.IS_SERVER_LOGIC == 1 || ProjectConfig.IS_SERVER_ZALO_LOGIC == 1 || ProjectConfig.IS_SERVER_GLOBAL_SING == 1) // server logic 1
				{
					//start server
//					InetSocketAddress isa = new InetSocketAddress(hostAddress, hostPort); // listen public ip address:port
					InetSocketAddress isa = new InetSocketAddress(hostPort); // listen *:port
					com.vng.netty.Server.Init(isa, num_thread_for_netty, num_thread_for_task_queue);
					LogHelper.Log("\nServer ready to use at [*:" + hostPort +"]......");					

					// ranking
					LoadRankingInfo();
					LogHelper.Log("\nRanking Info Manager ready to use.......");
					
					// event notification
					LoadEventNotification();
					LogHelper.Log("\nEvent Notification Manager ready to use.......");
					
					LoadOfferManager();
					LogHelper.Log("\nOffer Manager ready to use.......");
					
					UpdateServerConfig();
					LogHelper.Log("\nStart update server misc.......");
					
					InitServerEventCounter();
					
					StartServerScheduledTask();
					LogHelper.Log("\nStart server schedule task.......");

					// udp
					new UDPServer(s_server_local_address, ProjectConfig.UDP_PORT);
					LogHelper.Log("\nServer UDP ready to use at [" + s_server_local_address + ":" + ProjectConfig.UDP_PORT + "]......");
					
//					DBConnector.GetMembaseServerForTemporaryData().Set("use_npc_custome", false);
//					DBConnector.GetMembaseServerForTemporaryData().Set("use_snow", false);
				}
				else if (ProjectConfig.IS_SERVER_FREESTYLE == 1) // server happy
				{
					//start server
//					InetSocketAddress isa = new InetSocketAddress(hostAddress, hostPort); // listen public ip address:port
					InetSocketAddress isa = new InetSocketAddress(hostPort); // listen *:port
					com.vng.netty.Server.Init(isa, num_thread_for_netty, num_thread_for_task_queue);
					LogHelper.Log("\nServer ready to use at [" + "*" + ":" + hostPort +"]......");
					
					// ranking
					LoadRankingInfo();
					LogHelper.Log("\nRanking Info Manager ready to use.......");
					
					// event notification
					LoadEventNotification();
					LogHelper.Log("\nEvent Notification Manager ready to use.......");
					
					LoadOfferManager();
					LogHelper.Log("\nOffer Manager ready to use.......");
					
					UpdateServerConfig();
					LogHelper.Log("\nStart update server misc.......");
					
					StartServerScheduledTask();
					LogHelper.Log("\nStart server schedule task.......");
					
					InitServerEventCounter();
				}
				else if (ProjectConfig.IS_SERVER_CDN == 1) // server cdn
				{
					//start server
					InetSocketAddress isa = new InetSocketAddress(hostPort); // listen *:port
					com.vng.netty.Server.Init(isa, num_thread_for_netty, num_thread_for_task_queue);
					LogHelper.Log("\nServer CDN ready to use at [*:" + hostPort +"]......");					
				}
				else if (ProjectConfig.IS_SERVER_RANKING == 1 || ProjectConfig.IS_SERVER_RANKING_FREESTYLE == 1) 
				{
					// start ranking server
					if (ProjectConfig.IS_SERVER_RANKING == 1) 
					{
						new UDPServer(s_server_local_address, ProjectConfig.UDP_PORT);
						LogHelper.Log("\nServer UDP ready to use at [" + ProjectConfig.RANKING_IP + ":" + ProjectConfig.UDP_PORT + "]......");
					}
					else 
					{
						new UDPServer(s_server_local_address, 8305);
						LogHelper.Log("\nServer UDP ready to use at [" + ProjectConfig.RANKING_IP + ":" + ProjectConfig.UDP_PORT + "]......");
					}
					
					_rank_arrangement = new RankArrangement();
					LogHelper.Log("\nRankingManager ready to use.");
				}
				else if (ProjectConfig.IS_SERVER_NEWSBOARD == 1 || ProjectConfig.IS_SERVER_NEWSBOARD_FREESTYLE == 1)
				{
					if (ProjectConfig.IS_SERVER_NEWSBOARD == 1)
					{
						new UDPServer(s_server_local_address, ProjectConfig.UDP_PORT);
						LogHelper.Log("\nServer newsboard UDP ready to use at [" + ProjectConfig.NEWSBOARD_IP + ":" + ProjectConfig.UDP_PORT + "]......");
					}
					else
					{
						new UDPServer(s_server_local_address, 8305);
						LogHelper.Log("\nServer newsboard UDP ready to use at [" + ProjectConfig.NEWSBOARD_IP + ":" + 8305 + "]......");
					}
					_ads_manager = new AdvertiseManager();
					LogHelper.Log("\nAdvertiseManager ready to use.");
				}
				else if (ProjectConfig.IS_SERVER_ZALO_PAYMENT == 1)
				{
					InetSocketAddress isa1 = new InetSocketAddress(0); // listen *:port
					com.vng.netty.Server.Init(isa1, num_thread_for_netty, num_thread_for_task_queue);
					LogHelper.Log("\nServer ready to use at [*:" + 0 +"]......");
					
					// http server for payment
					InetSocketAddress isa = new InetSocketAddress(hostPort);
					com.vng.nettyhttp.Server.Init(isa, num_thread_for_netty, num_thread_for_task_queue);
					LogHelper.Log("\nServer HTTP ready to use at [*:" + hostPort + "] ......");

//					new UDPServer(s_server_local_address, ProjectConfig.UDP_PORT);
//					LogHelper.Log("\nServer UDP ready to use at [" + local_address + ":" + ProjectConfig.UDP_PORT + "]......");
				}
			}
			
			// start log ccu
			if ((ProjectConfig.IS_SERVER_LOGIC == 1 || ProjectConfig.IS_SERVER_ZALO_LOGIC == 1 || ProjectConfig.IS_SERVER_GLOBAL_SING == 1)&& ProjectConfig.RUN_LOCAL != 1)
			{
				StartLogCCU();

				Monitor.InitMonitor(s_server_local_address, num_thread_for_netty, ProjectConfig.VERSION);
				StartLogMonitor();

				if (ProjectConfig.USE_CACHE_OFFLINE)
				{
					MaintainCachedList();
				}
			}
			
			// update server status
			if (ProjectConfig.IS_SERVER_LOGIC == 1 || ProjectConfig.IS_SERVER_FREESTYLE == 1)
			{
				UpdateServerStatus(s_server_local_address, s_serverPort, "Pause");
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("Exception in start server !", e);
			System.exit(1);
		}
	}
	
	public static void StartLog(String hostAddress, int port)
	{
		try 
		{
			ScribeAppender.ConfigMissLog(true, "./log/MissLog", "_" + hostAddress + "_" + port, "_yyyyMMdd");
			Properties props = new Properties();
			props.load(ClassLoader.getSystemClassLoader().getResourceAsStream("log4j.properties"));
			PropertyConfigurator.configure(props);					
			
			ScribeAppender appenderAudit = (ScribeAppender) Logger.getLogger("IFRS").getAppender("IFRS_A");
			appenderAudit.setSplitFolderByDay(true);
			
			AsyncScribeAppender.bufferSize = 10000;
			
			LogHelper.Log("StartLog server.");
		} 
		catch (Exception e) 
		{
			LogHelper.LogException("Exception in start scribe log !", e);
		}
	}
	
	public static void StartLogFile(String hostAddress, int port)
	{
		try 
		{
			ScribeAppender.ConfigMissLog(true, "./log/MissLog", "_" + hostAddress + "_" + port, "_yyyyMMdd");
			Properties props = new Properties();
			props.load(ClassLoader.getSystemClassLoader().getResourceAsStream("log4j_file.properties"));
			PropertyConfigurator.configure(props);					
			
			LogHelper.Log("StartLog file.");
		} 
		catch (Exception e) 
		{
			LogHelper.LogException("Exception in start scribe log !", e);
		}
	}
	
	public static void StartLogCCU()
	{
		Timer t = new Timer("LogCCU");
		t.schedule(new TimerTask() 
		{
			@Override
			public void run()
			{
				StringBuilder log = new StringBuilder();
				log.append(Misc.getCurrentDateTime());
				log.append('\t').append(Server.s_serverUserOnline.size());
				log.append('\t').append(s_server_local_address);
				log.append('\t').append("0");
				
				LogHelper.Log(LogHelper.LogType.SYSTEM, log.toString());
			}
		}, 5*60*1000, 5*60*1000);
	}
	
	public static void StartLogMonitor()
	{
		Timer t = new Timer("LogMonitor");
		t.schedule(new TimerTask() 
		{
			@Override
			public void run()
			{
				StringBuilder log_server = new StringBuilder();
				log_server.append("MonitorServer").append("\t");
				log_server.append(Monitor.GetMonitorInfo());
                log_server.append('\t').append("online_size=").append(Server.s_serverUserOnline.size());
				LogHelper.Log(LogHelper.LogType.MONITOR_SERVER, log_server.toString());
				
				StringBuilder log_task = new StringBuilder();
				log_task.append("MonitorTask").append("\t");
				log_task.append(Monitor.GetMonitorTaskInfo());
				LogHelper.Log(LogHelper.LogType.MONITOR_TASKQUEUE, log_task.toString());
				
				if (ProjectConfig.USE_CACHE_OFFLINE)
				{
					StringBuilder log_cache = new StringBuilder();
					log_cache.append("MonitorCached").append("\t");
					log_cache.append("size=").append(Server.s_recentOnlineUser.size());
					LogHelper.Log(log_cache.toString());
				}
			}
		}, 1*60*1000, 1*60*1000);
	}
	
	// temp
	public static void MaintainCachedList()
	{
		Timer t = new Timer("MaintainCached");
		t.schedule(new TimerTask() 
		{
			@Override
			public void run()
			{
				LogHelper.Log("MaintainCachedList.. perform maintain cached list.");
				for (Map.Entry<Long, SkyGardenUser> e : Server.s_recentOnlineUser.entrySet())
				{
					if (Server.s_recentOnlineUser.size() < ProjectConfig.MAX_CACHE/2)
					{
						break;
					}
					
					SkyGardenUser u = e.getValue();
					if (u != null && u.GetUserInfo().GetLastLoginTime() < (System.currentTimeMillis()/1000 - 5*60*1000))
					{
						Server.s_recentOnlineUser.remove(e.getKey());
						LogHelper.Log("MaintainCachedList.. remove cached user [" + u.GetUserID() + "].");
					}
				}
			}
		}, 1*60*1000, 1*60*1000);
	}
	
	/* Timer for event 30-4 */
	public static boolean InSaleOffEvent()
	{
		try
		{
			String start_time	= Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][7][DatabaseID.EVENT_GLOBAL_START_DATE]);
			String end_time		= Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][7][DatabaseID.EVENT_GLOBAL_END_DATE]);
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			long millisec_start_event = sdf.parse(start_time).getTime();
			long millisec_end_event = sdf.parse(end_time).getTime();
			if ( System.currentTimeMillis() > millisec_start_event && System.currentTimeMillis() < millisec_end_event)
			{
				return true;
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("InSaleOffEvent", e);
		}
		
		return false;
	}
	
	/* Load all ranking info */
	public static void LoadRankingInfo() {
		// load ranking info each SORT_INTERVAL seconds
		_ranking_info = new ConcurrentHashMap<String, RankingInfo>();
		Timer t = new Timer("LoadRankingInfo");
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					LogHelper.Log("Updating ranking info.");
					// previous ranking info
					boolean found_previous = false;
					for (int i = 0; i < 40; i++) {
						RankingInfo r = new RankingInfo(i);
						if (r.Load()) {
							if (r.IsPast()) {
								if (_ranking_info.containsKey("previous")) {
									_ranking_info.remove("previous");
								}
								_ranking_info.put("previous", r);
								found_previous = true;
							}
						} else {
							break;
						}
					}
					if (!found_previous) {
						if (_ranking_info.containsKey("previous")) {
							_ranking_info.remove("previous");
						}
					}

					// current ranking info
					boolean found_active = false;
					for (int i = 0; i < 40; i++) {
						RankingInfo r = new RankingInfo(i);
						if (r.Load()) {
							if (r.IsActive()) {
								if (_ranking_info.containsKey("active")) {
									_ranking_info.remove("active");
								}
								_ranking_info.put("active", r);
								found_active = true;
								break;
							}
						} else {
							break;
						}
					}
					if (!found_active) {
						if (_ranking_info.containsKey("active")) {
							_ranking_info.remove("active");
						}
					}

					// other ranking info here

					// debug
					if (_ranking_info.get("previous") != null) {
						LogHelper.Log("LoadRankingInfo.. previous ranking command = " + _ranking_info.get("previous").GetRankingCommand() + ", ranking idx = " + _ranking_info.get("previous").GetID());
					} else  {
						LogHelper.Log("LoadRankingInfo.. previous ranking command = [N/A]");
					}
					if (_ranking_info.get("active") != null) {
						LogHelper.Log("LoadRankingInfo.. active ranking command = " + _ranking_info.get("active").GetRankingCommand() + ", ranking idx = " + _ranking_info.get("active").GetID());
					} else {
						LogHelper.Log("LoadRankingInfo.. active ranking command = [N/A]");
					}
				} catch (Exception e) {
					LogHelper.Log("Exception in updating ranking info.");
				}
			}
		}, 1000L, 60 * 1000L);
	}
	
	/* Load event notification*/
	public static void LoadEventNotification()
	{
		// event notification
		_event_notification = new EventNotification();
		Timer t = new Timer("LoadEventNotification");
		t.schedule(new TimerTask() 
		{
			@Override
			public void run() 
			{
				for (int i = 0; i < 3; i++)
				{
					Object obj_info;
					try
					{
						obj_info = DBConnector.GetMembaseServerForTemporaryData().Get("event_notify" + "_" + i);
					}
					catch (Exception e)
					{
						LogHelper.LogException("LoadEventNotification", e);
						obj_info = null;
					}
					
					if (obj_info == null)
					{
						LogHelper.Log("Warn! Can not find event notification info.");
						continue;
					}
					else
					{
						String info = (String)obj_info;
						String[] aos = info.split(";");

						if (aos.length != 9)
						{
							LogHelper.Log("Load event notification failed! Invalid info size = " + aos.length + ", content = " + info);
							return;
						}
						
						// check if it's in event time
						boolean is_enable = false;
						try
						{
							String start_time = aos[2];
							String end_time = aos[3];
							SimpleDateFormat datef = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							long millisec_start_event = datef.parse(start_time).getTime();
							long millisec_end_event = datef.parse(end_time).getTime();

							if (System.currentTimeMillis() >= millisec_start_event && System.currentTimeMillis() <= millisec_end_event)
							{
								is_enable = true;
							}
						}
						catch (Exception e)
						{
							LogHelper.LogException("ParseTimeError", e);
						}
						
						if (!is_enable)
						{
							_event_notification._type = 0;
							_event_notification._name = "";
							switch (i)
							{
								case 0:
									_event_notification._enable_hd = false;
									_event_notification._img_small = "";
									_event_notification._img_large = "";
									_event_notification._img_large_md5 = "";
									_event_notification._img_small_md5 = "";
									_event_notification._details = "";
									LogHelper.Log("Event notification: disabled event notification for Anroid HD done.");
									break;
								case 1:
									_event_notification._enable_sd = false;
									_event_notification._img_small_ios = "";
									_event_notification._img_large_ios = "";
									_event_notification._img_small_ios_md5 = "";
									_event_notification._img_large_ios_md5 = "";
									_event_notification._details_sd			= "";
									LogHelper.Log("Event notification: disabled event notification for Anroid SD done.");
									break;
								case 2:
									_event_notification._enable_ios = false;
									_event_notification._img_small_sd = "";
									_event_notification._img_large_sd = "";
									_event_notification._img_small_sd_md5 = "";
									_event_notification._img_large_sd_md5 = "";
									_event_notification._details_ios	= "";
									LogHelper.Log("Event notification: disabled event notification for iOS done.");
									break;
							}
						}
						else
						{
							String event_name = aos[1];
							String details_link = aos[4];
							String thumbnail_img = aos[5];
							String thumnbnail_img_md5 = aos[6];
							String details_img = aos[7];
							String details_img_md5 = aos[8];
							
//							_event_notification._enable		= true;
							_event_notification._type		= 0; // 0 = wolf or redhood
							_event_notification._name		= event_name;
							
							switch (i)
							{
								case 0:
									_event_notification._enable_hd = true;
									_event_notification._img_small			= thumbnail_img;
									_event_notification._img_small_md5		= thumnbnail_img_md5;
									_event_notification._img_large			= details_img;
									_event_notification._img_large_md5		= details_img_md5;
									_event_notification._details			= details_link;
									LogHelper.Log("Event notification: set event notification for Anroid HD done.");
									break;
								case 1:
									_event_notification._enable_sd = true;
									_event_notification._img_small_sd		= thumbnail_img;
									_event_notification._img_small_sd_md5	= thumnbnail_img_md5;
									_event_notification._img_large_sd		= details_img;
									_event_notification._img_large_sd_md5	= details_img_md5;
									_event_notification._details_sd			= details_link;
									LogHelper.Log("Event notification: set event notification for Anroid SD done.");
									break;
								case 2:
									_event_notification._enable_ios = true;
									_event_notification._img_small_ios		= thumbnail_img;
									_event_notification._img_small_ios_md5	= thumnbnail_img_md5;
									_event_notification._img_large_ios		= details_img;
									_event_notification._img_large_ios_md5	= details_img_md5;
									_event_notification._details_ios		= details_link;
									LogHelper.Log("Event notification: set event notification for iOS done.");
									break;
							}
						}
					}
				}
			}
		}, 1000L, 60 * 1000L);
	}
	
	/* Event Xmas 2014*/
	public static void InitServerEventCounter() throws Exception
	{
		_event_counter = new AtomicLong();
		String key = "birthday_2015" + "_" + _server_id;
		Object obj = DBConnector.GetMembaseServerForTemporaryData().Get(key);
		if (obj != null) {
			long v = (long)obj;
			_event_counter.set(v);
		}
		
		LogHelper.Log("Load server event counter, key := " + key + ", value := " + _event_counter.longValue());

		Timer t = new Timer("SaveLoadXmasTree");
		t.schedule(new TimerTask() 
		{
			@Override
			public void run()
			{
				String start = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][17][DatabaseID.EVENT_GLOBAL_START_DATE]);
				String end = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_EVENT_GLOBAL][17][DatabaseID.EVENT_GLOBAL_END_DATE]);
				if(Misc.InEvent(start, end)) {
					_is_use_server_event = true;
				} else {
					_is_use_server_event = false;
				}

				if (_is_use_server_event) {
					long v = _event_counter.longValue();
					DBConnector.GetMembaseServerForTemporaryData().Set("birthday_2015" + "_" + _server_id, v);
//						LogHelper.Log("Save Xmas Tree 2014, key := " + "xmas_tree_2014" + "_" + _server_id + ", value := " + v);
				}
			}
		}, 1*1*1000L, 1*1*1000L);
	}

	public static void UpdateServerStatus(String ip, int port, String status)
	{
		try
		{
			Object obj = DBConnector.GetMembaseServerForGeneralData().Get("server_status");
			if (obj != null)
			{
				String server_status = (String)obj;
				LogHelper.Log("Current servers status := " + server_status);
				String[] aos = server_status.split(";");

				boolean updated = false;
				for (String s : aos)
				{
					String l_ip = s.split(":")[0];
					int l_port = Integer.parseInt(s.split(":")[1]);
					String l_status = s.split(":")[2];
					
					if (l_ip.equals(ip) && l_port == port)
					{
						server_status = server_status.replace(l_ip + ":" + l_port + ":" + l_status, ip + ":" + port + ":" + status);
						LogHelper.Log("New servers status := " + server_status);
						DBConnector.GetMembaseServerForGeneralData().Set("server_status", server_status);
						updated = true;
						break;
					}
				}
				
				if (!updated)
				{
					StringBuilder sb = new StringBuilder();
					sb.append(server_status).append(";");
					sb.append(ip);
					sb.append(":").append(port);
					sb.append(":").append(status);
					DBConnector.GetMembaseServerForGeneralData().Set("server_status", sb.toString());
					LogHelper.Log("New servers status := " + sb.toString());
					updated = true;
				}
			}
			else
			{
				StringBuilder sb = new StringBuilder();
				sb.append(ip);
				sb.append(":").append(port);
				sb.append(":").append(status);
				DBConnector.GetMembaseServerForGeneralData().Set("server_status", sb.toString());
				LogHelper.Log("New servers status := " + sb.toString());
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("UpdateServerStatus", e);
		}
	}
	
	public static void LoadOfferManager()
	{
		_offer_manager = new OfferManager();
		Timer t = new Timer("LoadOfferManager");
		t.schedule(new TimerTask() 
		{
			@Override
			public void run() 
			{
				LogHelper.Log("Updating Offer Manager.");

				// Offer bug
				boolean is_offer_bug_active = false;
				for (int i = 0; i < 40; i++)
				{
					try
					{
						Object obj = DBConnector.GetMembaseServerForTemporaryData().Get("offer" + "_" + "bug" + "_" + i);
						if (obj != null)
						{
							String s = (String)obj;
							String[] aos = s.split(";");
							String start_time = aos[0];
							String end_time = aos[1];
							if (Misc.InEvent(start_time, end_time))
							{
								_offer_manager.setOfferContent(_offer_manager.getOfferBug(), s);
								_offer_manager.updateOffer(_offer_manager.getOfferBug());
								_offer_manager.setOfferActive(_offer_manager.getOfferBug(), true);
								is_offer_bug_active = true;
								break;
							}
						}
						else
						{
							LogHelper.LogHappy("Can not find key := " + "offer" + "_" + "bug" + "_" + i);
							break;
						}
					}
					catch (Exception e)
					{
						LogHelper.LogException("UpdateOfferBug", e);
					}
				}
				
				if (!is_offer_bug_active)
				{
					_offer_manager.setOfferActive(_offer_manager.getOfferBug(), false);
					_offer_manager.setOfferContent(_offer_manager.getOfferBug(), "inactive");
				}

				LogHelper.Log("Offer bug status := " + _offer_manager.isOfferActive(_offer_manager.getOfferBug()));
				LogHelper.Log("Offer bug content := " + _offer_manager.getOfferContent(_offer_manager.getOfferBug()));
				
				// Offer gem
				boolean is_offer_gem_active = false;
				for (int i = 0; i < 40; i++)
				{
					try
					{
						Object obj = DBConnector.GetMembaseServerForTemporaryData().Get("offer" + "_" + "gem" + "_" + i);
						if (obj != null)
						{
							String s = (String)obj;
							String[] aos = s.split(";");
							String start_time = aos[0];
							String end_time = aos[1];
							if (Misc.InEvent(start_time, end_time))
							{
								_offer_manager.setOfferContent(_offer_manager.getOfferGem(), s);
								_offer_manager.updateOffer(_offer_manager.getOfferGem());
								_offer_manager.setOfferActive(_offer_manager.getOfferGem(), true);
								is_offer_gem_active = true;
								break;
							}
						}
						else
						{
							LogHelper.LogHappy("Can not find key := " + "offer" + "_" + "gem" + "_" + i);
							break;
						}
					}
					catch (Exception e)
					{
						LogHelper.LogException("UpdateOfferGem", e);
					}
				}
				
				if (!is_offer_gem_active)
				{
					_offer_manager.setOfferActive(_offer_manager.getOfferGem(), false);
					_offer_manager.setOfferContent(_offer_manager.getOfferGem(), "inactive");
				}

				LogHelper.Log("Offer gem status := " + _offer_manager.isOfferActive(_offer_manager.getOfferGem()));
				LogHelper.Log("Offer gem content := " + _offer_manager.getOfferContent(_offer_manager.getOfferGem()));
				
				// Offer lucky leaf green
				boolean is_offer_leaf_active = false;
				for (int i = 0; i < 40; i++)
				{
					try
					{
						Object obj = DBConnector.GetMembaseServerForTemporaryData().Get("offer" + "_" + "leaf" + "_" + i);
						if (obj != null)
						{
							String s = (String)obj;
							String[] aos = s.split(";");
							String start_time = aos[0];
							String end_time = aos[1];
							if (Misc.InEvent(start_time, end_time))
							{
								_offer_manager.setOfferContent(_offer_manager.getOfferLuckyLeafGreen(), s);
								_offer_manager.updateOffer(_offer_manager.getOfferLuckyLeafGreen());
								_offer_manager.setOfferActive(_offer_manager.getOfferLuckyLeafGreen(), true);
								is_offer_leaf_active = true;
								break;
							}
						}
						else
						{
							LogHelper.LogHappy("Can not find key := " + "offer" + "_" + "leaf" + "_" + i);
							break;
						}
					}
					catch (Exception e)
					{
						LogHelper.LogException("UpdateOfferLeaf", e);
					}
				}
				
				if (!is_offer_leaf_active)
				{
					_offer_manager.setOfferActive(_offer_manager.getOfferLuckyLeafGreen(), false);
					_offer_manager.setOfferContent(_offer_manager.getOfferLuckyLeafGreen(), "inactive");
				}

				LogHelper.Log("Offer leaf green status := " + _offer_manager.isOfferActive(_offer_manager.getOfferLuckyLeafGreen()));
				LogHelper.Log("Offer leaf green content := " + _offer_manager.getOfferContent(_offer_manager.getOfferLuckyLeafGreen()));
				
				// Offer lucky leaf green
				boolean is_offer_leaf_purple_active = false;
				for (int i = 0; i < 40; i++)
				{
					try
					{
						Object obj = DBConnector.GetMembaseServerForTemporaryData().Get("offer" + "_" + "leaf" + "_" + "purple" + "_" + i);
						if (obj != null)
						{
							String s = (String)obj;
							String[] aos = s.split(";");
							String start_time = aos[0];
							String end_time = aos[1];
							if (Misc.InEvent(start_time, end_time))
							{
								_offer_manager.setOfferContent(_offer_manager.getOfferLuckyLeafPurple(), s);
								_offer_manager.updateOffer(_offer_manager.getOfferLuckyLeafPurple());
								_offer_manager.setOfferActive(_offer_manager.getOfferLuckyLeafPurple(), true);
								is_offer_leaf_purple_active = true;
								break;
							}
						}
						else
						{
							LogHelper.LogHappy("Can not find key := " + "offer" + "_" + "leaf" + "_" + "purple" + "_" + i);
							break;
						}
					}
					catch (Exception e)
					{
						LogHelper.LogException("UpdateOfferLeaf", e);
					}
				}
				
				if (!is_offer_leaf_purple_active)
				{
					_offer_manager.setOfferActive(_offer_manager.getOfferLuckyLeafPurple(), false);
					_offer_manager.setOfferContent(_offer_manager.getOfferLuckyLeafPurple(), "inactive");
				}

				LogHelper.Log("Offer leaf purple status := " + _offer_manager.isOfferActive(_offer_manager.getOfferLuckyLeafPurple()));
				LogHelper.Log("Offer leaf purple content := " + _offer_manager.getOfferContent(_offer_manager.getOfferLuckyLeafPurple()));
			}
		}, 1000L, 60 * 1000L);
	}
	
	public static void UpdateServerConfig() throws Exception {
		_server_config = new ConcurrentHashMap<String, Integer>();
		_bug_bonus = new ConcurrentHashMap<Integer, String[]>();
		
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		ScheduledFuture<?> schedule_future = scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				boolean has_bonus = false;
				String key = KeyID.KEY_BONUS_PLANT;
				for (int i = 0; i < 40; i++) {
					try {
						Object obj = DBConnector.GetMembaseServerForTemporaryData().Get(key + "_" + i);
						if (obj != null) {
							String s = (String) obj;
							String[] aos = s.split(";");
							String start_time = aos[0];
							String end_time = aos[1];
							if (Misc.InEvent(start_time, end_time)) {
								int value = Integer.parseInt(aos[2]);
								if (_server_config.containsKey(key)) {
									_server_config.replace(key, value);
								} else {
									_server_config.put(key, value);
								}
								has_bonus = true;
								break;
							}
						} else {
							break;
						}
					} catch (Exception e) {
						LogHelper.LogException("UpdateBonusPlant", e);
					}
				}

				if (!has_bonus) {
					if (_server_config.containsKey(key)) {
						_server_config.remove(key);
					}
				}

				// airship gold
				has_bonus = false;
				key = KeyID.KEY_BONUS_AIRSHIP_GOLD;
				for (int i = 0; i < 40; i++) {
					try {
						Object obj = DBConnector.GetMembaseServerForTemporaryData().Get(key + "_" + i);
						if (obj != null) {
							String s = (String) obj;
							String[] aos = s.split(";");
							String start_time = aos[0];
							String end_time = aos[1];
							if (Misc.InEvent(start_time, end_time)) {
								int value = Integer.parseInt(aos[2]);
								if (_server_config.containsKey(key)) {
									_server_config.replace(key, value);
								} else {
									_server_config.put(key, value);
								}
								has_bonus = true;
								break;
							}
						} else {
							break;
						}
					} catch (Exception e) {
						LogHelper.LogException("UpdateBonusAirshipGold", e);
					}
				}

				if (!has_bonus) {
					if (_server_config.containsKey(key)) {
						_server_config.remove(key);
					}
				}

				// airship exp
				has_bonus = false;
				key = KeyID.KEY_BONUS_AIRSHIP_EXP;
				for (int i = 0; i < 40; i++) {
					try {
						Object obj = DBConnector.GetMembaseServerForTemporaryData().Get(key + "_" + i);
						if (obj != null) {
							String s = (String) obj;
							String[] aos = s.split(";");
							String start_time = aos[0];
							String end_time = aos[1];
							if (Misc.InEvent(start_time, end_time)) {
								int value = Integer.parseInt(aos[2]);
								if (_server_config.containsKey(key)) {
									_server_config.replace(key, value);
								} else {
									_server_config.put(key, value);
								}
								has_bonus = true;
								break;
							}
						} else {
							break;
						}
					} catch (Exception e) {
						LogHelper.LogException("UpdateBonusAirshipExp", e);
					}
				}

				if (!has_bonus) {
					if (_server_config.containsKey(key)) {
						_server_config.remove(key);
					}
				}

				// order normal
				has_bonus = false;
				key = KeyID.KEY_BONUS_ORDER_NORMAL;
				for (int i = 0; i < 40; i++) {
					try {
						Object obj = DBConnector.GetMembaseServerForTemporaryData().Get(key + "_" + i);
						if (obj != null) {
							String s = (String) obj;
							String[] aos = s.split(";");
							String start_time = aos[0];
							String end_time = aos[1];
							if (Misc.InEvent(start_time, end_time)) {
								int value = Integer.parseInt(aos[2]);
								if (_server_config.containsKey(key)) {
									_server_config.replace(key, value);
								} else {
									_server_config.put(key, value);
								}
								has_bonus = true;
								break;
							}
						} else {
							break;
						}
					} catch (Exception e) {
						LogHelper.LogException("UpdateBonusOrderNormal", e);
					}
				}

				if (!has_bonus) {
					if (_server_config.containsKey(key)) {
						_server_config.remove(key);
					}
				}

				// order daily
				has_bonus = false;
				key = KeyID.KEY_BONUS_ORDER_DAILY;
				for (int i = 0; i < 40; i++) {
					try {
						Object obj = DBConnector.GetMembaseServerForTemporaryData().Get(key + "_" + i);
						if (obj != null) {
							String s = (String) obj;
							String[] aos = s.split(";");
							String start_time = aos[0];
							String end_time = aos[1];
							if (Misc.InEvent(start_time, end_time)) {
								int value = Integer.parseInt(aos[2]);
								if (_server_config.containsKey(key)) {
									_server_config.replace(key, value);
								} else {
									_server_config.put(key, value);
								}
								has_bonus = true;
								break;
							}
						} else {
							break;
						}
					} catch (Exception e) {
						LogHelper.LogException("UpdateBonusOrderDaily", e);
					}
				}

				if (!has_bonus) {
					if (_server_config.containsKey(key)) {
						_server_config.remove(key);
					}
				}
				
				try {
					boolean use_npc_custome = false;
					Object obj = DBConnector.GetMembaseServerForTemporaryData().Get("use_npc_custome_str");
					if (obj != null) {
						use_npc_custome = ((String)obj).equals("ON");
					}
					_server_config.put("use_npc_custome_str", use_npc_custome ? 1 : 0);
					
					boolean use_snow = false;
					obj = DBConnector.GetMembaseServerForTemporaryData().Get("use_snow_str");
					if (obj != null) {
						use_snow = ((String)obj).equals("ON");
					}
					_server_config.put("use_snow_str", use_snow ? 1 : 0);
					
					boolean use_fortune_wheel = false;
					obj = DBConnector.GetMembaseServerForTemporaryData().Get("use_fortune_wheel");
					if (obj != null) {
						use_fortune_wheel = ((String)obj).equals("ON");
					}
					_server_config.put("use_fortune_wheel", use_fortune_wheel ? 1 : 0);
					
					boolean use_npc_at_friend_garden = false;
					obj = DBConnector.GetMembaseServerForTemporaryData().Get("use_npc_at_friend_garden");
					if (obj != null) {
						use_npc_at_friend_garden = ((String)obj).equals("ON");
					}
					_server_config.put("use_npc_at_friend_garden", use_npc_at_friend_garden ? 1 : 0);
					
					int broadcast_frequence = 30;
					obj = DBConnector.GetMembaseServerForTemporaryData().Get("broadcast_frequence");
					if (obj != null) {
						broadcast_frequence = (int)obj;
					}
					_server_config.put("broadcast_frequence", broadcast_frequence);
					
					obj = DBConnector.GetMembaseServerForTemporaryData().Get("personal_bonus");
					if (obj != null) {
						if(((String)obj).equals("ON")) {
							_server_config.put("personal_bonus", 1);
						} else {
							_server_config.put("personal_bonus", 0);
						}
					}
					
					boolean use_geo_ip = false;
					obj = DBConnector.GetMembaseServerForTemporaryData().Get("use_geo_ip");
					if (obj != null) {
						use_geo_ip = ((String)obj).equals("ON");
					}
					_server_config.put("use_geo_ip", use_geo_ip ? 1 : 0);
					
					boolean use_payment_geo = false;
					obj = DBConnector.GetMembaseServerForTemporaryData().Get("payment_geo");
					if (obj != null) {
						use_payment_geo = ((String)obj).equals("ON");
					}
					_server_config.put("payment_geo", use_payment_geo ? 1 : 0);
					
					boolean use_payment_geo_mwork = false;
					obj = DBConnector.GetMembaseServerForTemporaryData().Get("payment_geo_mwork");
					if (obj != null) {
						use_payment_geo_mwork = ((String)obj).equals("ON");
					}
					_server_config.put("payment_geo_mwork", use_payment_geo_mwork ? 1 : 0);
					
				} catch (Exception e) {
					LogHelper.LogException("UpdateServerConfig", e);
				}
				
				for (Entry<String, Integer> e : _server_config.entrySet()) {
					LogHelper.Log("key := " + e.getKey() + ", value := " + e.getValue());
				}
				
				// update bug bonus
				try {
					for (int i = 0; i < 200; i++) {
						String key_bug = "bug_bonus_level_" + i;
						Object obj = DBConnector.GetMembaseServerForTemporaryData().Get(key_bug);
						if (obj != null) {
							String s = (String)obj;
							if (s.equals("null")) {
								if (_bug_bonus.containsKey(i))
									_bug_bonus.remove(i);
								
								continue;
							}
							
							_bug_bonus.put(i, ((String)obj).split((":")));
						}
					}
					
//					for (Map.Entry<Integer, String[]> e : _bug_bonus.entrySet()) {
//						LogHelper.LogHappy("key := " + e + ", value := " + e.getValue()[0] + ", " + e.getValue()[1] + ", " + e.getValue()[0]);
//					}
				} catch (Exception e) {
					LogHelper.LogException("UpdateBugBonus", e);
				}
			}
		}, 1, 15, TimeUnit.SECONDS);
	}
	
	public static void StartServerScheduledTask() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		ScheduledFuture<?> schedule_future = scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				// run broadcast every 30 seconds
				Server._task_queue.AddTask(new ExecuteBroadcastTask());
			}
		}, 10, 30, TimeUnit.SECONDS);
	}
	
	public static ConcurrentHashMap<Integer, String> ActiveUsers()
	{
		if (_active_users == null)
			_active_users = new ConcurrentHashMap<>();
		
		return _active_users;
	}
	
	public static ConcurrentHashMap<Integer, String> BroadcastList() {
		return Server.s_broadcast_list;
	}
	
	public static void AddBroadcast(String content) {
		BroadcastList().put(BroadcastList().size(), content);
		LogHelper.Log("Add new broadcast content := " + content + ", size := " + BroadcastList().size());
	}
}