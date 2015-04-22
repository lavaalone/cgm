package com.vng.util;

import java.lang.StringBuilder;
import org.hyperic.sigar.*;
import org.hyperic.sigar.cmd.*;

public class SigarMonitor
{
	protected static Sigar _sigar = new Sigar();
	
	public static String GetStatsCPU(boolean isLog)
	{
		try
		{
			String separate = "\n";
			if (isLog)
			{
				separate = "&";
			}
		
			CpuPerc cpu = _sigar.getCpuPerc();
			
			StringBuilder result = new StringBuilder();
			
			result.append("cpu_use=").append(CpuPerc.format(cpu.getCombined())).append(separate);
			result.append("cpu_idle=").append(CpuPerc.format(cpu.getIdle())).append(separate);
			
			return result.toString();
		}
		catch (Exception ex)
		{
			
		}
		
		return "";
	}
	
	public static int GetCpuUse()
	{
		try
		{
			return (int) (101 - (_sigar.getCpuPerc().getIdle() * 100));
		}
		catch (Exception ex)
		{
			
		}
		return -1;
	}
	
	public static int GetMemoryUse()
	{
		try
		{			
			return (int) (_sigar.getMem().getUsed()/1048576);
		}
		catch (Exception ex)
		{
			
		}
		return -1;
	}
	
	public static int GetMemoryFree()
	{
		try
		{			
			return (int) (_sigar.getMem().getFree()/1048576);
		}
		catch (Exception ex)
		{
			
		}
		return -1;
	}
	
	public static int GetMemoryTotal()
	{
		try
		{			
			return (int) (_sigar.getMem().getTotal()/1048576);
		}
		catch (Exception ex)
		{
			
		}
		return -1;
	}
	
	public static String GetStatsMemory(boolean isLog)
	{
		try
		{
			String separate = "\n";
			if (isLog)
			{
				separate = "&";
			}
			
			Mem mem = _sigar.getMem();
			
			StringBuilder result = new StringBuilder();
			
			result.append("mem_total=").append(mem.getTotal()/1024).append(separate);
			result.append("mem_used=").append(mem.getUsed()/1024).append(separate);
			result.append("mem_free=").append(mem.getFree()/1024).append(separate);
			
			return result.toString();
		}
		catch (Exception ex)
		{
		
		}
		
		return "";
	}
	
	public static String GetStatsNetwork(boolean isLog)
	{
		try
		{
			String separate = "\n";
			if (isLog)
			{
				separate = "&";
			}
			
			StringBuilder result = new StringBuilder();
			
			//get network IO stats
			String[] net_interface_names = _sigar.getNetInterfaceList();
			for (String name: net_interface_names)
			{
				NetInterfaceConfig net_interface_config = _sigar.getNetInterfaceConfig(name);
				NetInterfaceStat net_interface_stat = _sigar.getNetInterfaceStat(name);
				
				result.append("inet_addr=").append(net_interface_config.getAddress()).append(separate);
				result.append("received_packet=").append(net_interface_stat.getRxPackets()).append(separate);
				result.append("received_packet_errors=").append(net_interface_stat.getRxErrors()).append(separate);
				result.append("received_packet_drops=").append(net_interface_stat.getRxDropped()).append(separate);
				result.append("received_bytes=").append(net_interface_stat.getRxBytes()).append(separate);
				result.append("transfer_packet=").append(net_interface_stat.getTxPackets()).append(separate);
				result.append("transfer_packet_errors=").append(net_interface_stat.getTxErrors()).append(separate);
				result.append("transfer_packet_drops=").append(net_interface_stat.getTxDropped()).append(separate);
				result.append("transfer_bytes=").append(net_interface_stat.getTxBytes()).append(separate);
				
			}
			
			return result.toString();
		}
		catch (Exception ex)
		{
		
		}
		
		return "";
	}
}