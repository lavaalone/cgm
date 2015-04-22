package com.vng.util;

import com.vng.netty.Server;
import java.io.*;
import java.text.*;
import java.util.*;
import java.lang.StringBuilder;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.*;

public class Monitor implements Runnable
{		
	private static String 						_server_ip;
	private static int 							_threads;
	private static String 						_version;
	
	//Concurrent users
	private static AtomicInteger 				_ccu = new AtomicInteger(0);
	
	//connection
	private static AtomicInteger 				_concurrent_connection = new AtomicInteger(0);
	private static AtomicInteger 				_total_connection_in_30s = new AtomicInteger(0);
	private static AtomicInteger 				_total_connection_time_in_30s = new AtomicInteger(0);
	private static int							_avg_connection_time = 0;
	
	//command
	private static int							_command_to_monitor = -1;
	private static long							_cmd_monitor_begin_time;
	private static AtomicInteger 				_total_cmd = new AtomicInteger();
	private static AtomicLong 					_total_cmd_time = new AtomicLong();
	private static int 							_avg_cmd_delay_time;
	private static float 						_avg_cmd_per_sec;
	
	//exception
	private static AtomicInteger 				_total_exception = new AtomicInteger();
	
	
	public void run ()
	{	
		try
		{	
			int total_conn_in_30s = _total_connection_in_30s.get();
			_total_connection_in_30s.addAndGet(-total_conn_in_30s);
			
			int total_conn_time_in_30s = _total_connection_time_in_30s.get();
			_total_connection_time_in_30s.addAndGet(-total_conn_time_in_30s);
			
			if (total_conn_in_30s > 0)
			{
				_avg_connection_time = total_conn_time_in_30s/total_conn_in_30s;
			}
			else
			{
				_avg_connection_time = -1;
			}
			
			int total_cmd = _total_cmd.get();
			if (total_cmd > 0)
			{
				_avg_cmd_delay_time = (int)(_total_cmd_time.get()/total_cmd);
			}
			
			long secs = (System.currentTimeMillis() - _cmd_monitor_begin_time)/1000;
			if (secs > 0)
			{
				_avg_cmd_per_sec = total_cmd*1.0f/secs;
			}
		}
		catch (Exception e)
		{
		}
	}
	
	public static void InitMonitor(String server_ip, int num_threads, String version)
	{
		_server_ip = server_ip;
		_threads = num_threads;
		_version = version;
	}
	
	public static void IncrUser()
	{
		_ccu.incrementAndGet();
	}
	
	public static void DecrUser()
	{
		_ccu.decrementAndGet();
	}
	
	public static void IncrConnection()
	{		
		_concurrent_connection.incrementAndGet();		
		_total_connection_in_30s.incrementAndGet();	
	}
	
	public static void DecrConnection(int delta_time)
	{		
		_concurrent_connection.decrementAndGet();
		_total_connection_time_in_30s.addAndGet(delta_time);		
	}
	
	public static void SetCommandToMonitor(int cmd)
	{		
		if (_command_to_monitor != cmd)
		{
			_command_to_monitor = cmd;
			_cmd_monitor_begin_time = System.currentTimeMillis();
			
			_total_cmd.set(0);
			_total_cmd_time.set(0);
			_avg_cmd_delay_time = 0;
			_avg_cmd_per_sec = 0;
		}
	}
	
	public static void MonitorCommand(int cmd, long delta_time)
	{
		if (_command_to_monitor == cmd)
		{
			_total_cmd.incrementAndGet();
			_total_cmd_time.addAndGet(delta_time);
		}
	}
	
	public static void IncrException()
	{		
		_total_exception.incrementAndGet();		
	}
	
	public static String GetMonitorInfo()
	{		
		StringBuilder sb = new StringBuilder(1024);
		String separate = "\t";
		
		//server info
		sb.append("id=").append(_server_ip).append(separate);
		sb.append("threads=").append(_threads).append(separate);
		sb.append("version=").append(_version).append(separate);
		
		//user
		sb.append("ccu=").append(_ccu.get()).append(separate);
		
		//connection
		sb.append("concur_conn=").append(_concurrent_connection.get()).append(separate);
//		sb.append("avg_conn_time=").append(_avg_connection_time).append(separate);
		
		//cpu & memory
		sb.append("cpu_use=").append(com.vng.util.SigarMonitor.GetCpuUse()).append(separate);
		
		// jvm
		sb.append("mem_use_runtime=").append((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1048576/*>>20*/).append(separate);
		sb.append("mem_free_runtime=").append(Runtime.getRuntime().freeMemory()/1048576).append(separate);
		sb.append("mem_total_runtime=").append(Runtime.getRuntime().totalMemory()/1048576).append(separate);
		
		// system
		sb.append("mem_use_sigar=").append(com.vng.util.SigarMonitor.GetMemoryUse()).append(separate);
		sb.append("mem_free_sigar=").append(com.vng.util.SigarMonitor.GetMemoryFree()).append(separate);
		sb.append("mem_total_sigar=").append(com.vng.util.SigarMonitor.GetMemoryTotal()).append(separate);
		
		sb.append("mem_max_runtime=").append(Runtime.getRuntime().maxMemory()/1048576).append(separate);
		
		//command
		sb.append("cmd_id=").append(_command_to_monitor).append(separate);
		sb.append("cmd_per_sec=").append(_avg_cmd_per_sec).append(separate);
		sb.append("cmd_avg_delay_time=").append(_avg_cmd_delay_time).append(separate);
		
		//exception
		sb.append("exceptions=").append(_total_exception.get()).append(separate);
		
		return sb.toString();
	}
	
	public static String GetMonitorTaskInfo()
	{		
		StringBuilder sb = new StringBuilder(1024);
		String separate = "\t";
		
		//server info
		sb.append("id=").append(_server_ip).append(separate);
		sb.append("threads=").append(_threads).append(separate);
		sb.append("version=").append(_version).append(separate);
		
		sb.append("ccu=").append(_ccu.get()).append(separate);
		
		//connection
		sb.append("concur_conn=").append(_concurrent_connection.get()).append(separate);
		
		if (Server._task_queue == null)
		{
			sb.append("task_queue=null");
			return sb.toString();
		}
		// threads
		sb.append("active_threads=").append(Server._task_queue.GetActiveThreads()).append(separate);
		sb.append("total_threads=").append(Server._task_queue.GetTotalThreads()).append(separate);
		sb.append("max_threads=").append(Server._task_queue.GetMaxThreads()).append(separate);
		
		// tasks
		sb.append("num_task=").append(Server._task_queue.GetNumTask()).append(separate);
		sb.append("total_task=").append(Server._task_queue.GetTaskCount()).append(separate);
		sb.append("completed_task=").append(Server._task_queue.GetCompletedTaskCount()).append(separate);
		sb.append("remain_task=").append(Server._task_queue.GetTaskCount() - Server._task_queue.GetCompletedTaskCount()).append(separate);
		
		return sb.toString();
	}
	
	public static int GetConcurrentConnection() {
		return _concurrent_connection.get();
	}

}
