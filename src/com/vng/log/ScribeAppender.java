package com.vng.log;

import java.util.List;
import java.util.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.TException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LocationInfo;

import com.vng.log.gen.*;
import com.vng.log.gen.Scribe.Client;

public class ScribeAppender extends AppenderSkeleton
{
	public static final String DEFAULT_SCRIBE_HOST			= "127.0.0.1";
	public static final int DEFAULT_SCRIBE_PORT				= 1463;
	public static final String DEFAULT_SCRIBE_CATEGORY		= "skygarden";

	public static final int TIME_WAIT_BEFORE_RETRY			= 5000;
	public static final String MISSLOG_FOLDER				= "./log/MissLog";
	public static final String MISSLOG_FILEDATEFORMAT		= "_yyyyMMdd";

	private long _timeBeforeRetry = 0;
	private static boolean s_useScribe = true;
	private static String s_missLog_Folder = MISSLOG_FOLDER;
	private static String s_missLog_FileDateFormat = MISSLOG_FILEDATEFORMAT;
	private static String s_missLog_FileSuffix = "";


	private String scribeHost = "";
	private int scribePort = 0;
	private String scribeCategory = "";
	
	private static final SimpleDateFormat s_splitFolderDateFormat = new SimpleDateFormat("_yyyyMMdd");
	private boolean splitFolderByDay = false;

	// NOTE: logEntries, client, and transport are all protected by a lock on 'this.'
	// The Scribe interface for sending log messages accepts a list.  This list is created
	// once and cleared and appended when new logs are created.  The list is always size 1.
	private List<LogEntry> logEntries;

	private Client client;
	private TFramedTransport transport;


	public String getScribeHost()
	{
		return scribeHost;
	}

	public void setScribeHost(String scribeHost)
	{
		this.scribeHost = scribeHost;
	}

	public int getScribePort()
	{
		return scribePort;
	}

	public void setScribePort(int scribePort)
	{
		this.scribePort = scribePort;
	}

	public String getScribeCategory()
	{
		return scribeCategory;
	}

	public void setScribeCategory(String scribeCategory)
	{
		this.scribeCategory = scribeCategory;
	}
	
	public void setSplitFolderByDay (boolean value)
	{	
		splitFolderByDay = value;		
	}

	@Override
	public void activateOptions()
	{
		configureScribe();
	}

	public boolean configureScribe()
	{
		try
		{
			synchronized(this)
			{
				_timeBeforeRetry = System.currentTimeMillis() + TIME_WAIT_BEFORE_RETRY;				
				
				if (scribeHost.length() == 0)
					scribeHost = DEFAULT_SCRIBE_HOST;
				if (scribePort == 0)
					scribePort = DEFAULT_SCRIBE_PORT;
				if (scribeCategory.length() == 0)
					scribeCategory = DEFAULT_SCRIBE_CATEGORY;

				logEntries = new ArrayList<LogEntry>(1);
				TSocket sock = new TSocket(new Socket(scribeHost, scribePort));
				transport = new TFramedTransport(sock);
				TBinaryProtocol protocol = new TBinaryProtocol(transport, false, false);
				client = new Client(protocol, protocol);

				//success
				return true;
			}
		}
		catch (Exception e)
		{
		}
		return false;
	}

	private BufferedWriter _bwMissLog;

	@Override
	public void append(LoggingEvent event)
	{
		synchronized(this)
		{
			String message = null;
			try
			{
				message = String.format("%s", layout.format(event));
				if (s_useScribe && connect()) //reconnect if needed
				{						
					LogEntry entry;
					if (splitFolderByDay)
						entry = new LogEntry(scribeCategory + s_splitFolderDateFormat.format(new Date()), message);
					else
						entry = new LogEntry(scribeCategory, message);

					logEntries.add(entry);
					
					if (client.Log(logEntries) == ResultCode.OK) //write log
					{
						message = null;
					}
				}
			}
			catch (TTransportException e)
			{
				CloseScribe();
			}
			catch (Exception e)
			{
			}
			finally
			{
				if (logEntries != null) logEntries.clear();
			}

			if (message != null)
			{
				try
				{
					if (_bwMissLog == null)
					{
						File f = new File(s_missLog_Folder);
						if (!f.exists()) f.mkdirs();

						DateFormat dateFormat = new SimpleDateFormat(s_missLog_FileDateFormat);
						String fileName = s_missLog_Folder + "/" + scribeCategory + s_missLog_FileSuffix + dateFormat.format(new Date()) + ".miss.log";

						_bwMissLog = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true), "UTF-8"));//append with exist file
					}
					
					_bwMissLog.write(message);
					_bwMissLog.flush();
				}
				catch (Exception e)
				{
					if (_bwMissLog != null)
					{
						try
						{
							_bwMissLog.close();
						}
						catch (Exception e1)
						{						
						}
						_bwMissLog = null;
					}
				}
			}
		}
	}

	public boolean connect()
	{
		if (transport != null)
		{
			if (transport.isOpen())
				return true;
			else
				CloseScribe();
		}

		if (System.currentTimeMillis() > _timeBeforeRetry)
			return configureScribe();
		else
			return false;
	}

	public static void ConfigMissLog (boolean useScribe, String missFolder, String fileSuffix, String fileDateFormat)
	{
		s_useScribe = useScribe;
		s_missLog_Folder = missFolder;
		s_missLog_FileSuffix = fileSuffix;
		s_missLog_FileDateFormat = fileDateFormat;		
	}

	public void CloseScribe ()
	{
		if (transport != null)
		{
			transport.close();
			transport = null;
			client = null;
		}
	}

	@Override
	public void close()
	{
		if (transport != null)
			transport.close();
	}

	@Override
	public boolean requiresLayout()
	{
		return true;
	}
}