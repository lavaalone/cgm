/**
 * (c) Copyright 2009 Cloudera, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vng.log;

import org.apache.log4j.AsyncAppender;

/*
 * An asynchronous version of {@link ScribeAppender}, which extends
 * Log4j's AsyncAppender.
 */
public class AsyncScribeAppender extends AsyncAppender 
{
	private String scribeHost;
	private int scribePort;
	private String scribeCategory;
	public static int bufferSize;

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

	@Override
	public void activateOptions() 
	{
		super.activateOptions();
		
		synchronized(this) 
		{
			ScribeAppender scribeAppender = new ScribeAppender();
			scribeAppender.setLayout(getLayout());
			scribeAppender.setScribeHost(getScribeHost());
			scribeAppender.setScribePort(getScribePort());
			scribeAppender.setScribeCategory(getScribeCategory());
			scribeAppender.activateOptions();
			addAppender(scribeAppender);
		}
	}

	@Override
	public boolean requiresLayout() 
	{
		return true;
	}
}
