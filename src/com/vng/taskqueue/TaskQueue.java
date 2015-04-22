package com.vng.taskqueue;

import java.util.concurrent.*;

public class TaskQueue
{
	private ThreadPoolExecutor 	_worker_pool;
	private int 				_num_tasks;
	private boolean 			_reject_new_task;
	
	public TaskQueue(int max_req_len, int pool_size, int max_pool_size)
	{
		_worker_pool = new ThreadPoolExecutor(	pool_size, 
												max_pool_size,
												30L,
												TimeUnit.SECONDS,
												new LinkedBlockingQueue<Runnable>(max_req_len)
												);
		_num_tasks = 0;
		_reject_new_task = false;
	}
	
	public void RejectNewTask()
	{
		_reject_new_task = true;
	}
	
	public void StopAllTask()
	{
		_worker_pool.shutdown();
	}
	
	public boolean AddTask(Task task)
	{
		if (_reject_new_task)
		{
			return false;
		}
		
		try
		{
			_worker_pool.execute(task);
			_num_tasks++;
		}
		catch (Exception ex)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean IsFinishAllTask()
	{
		return (_num_tasks <= _worker_pool.getCompletedTaskCount());
	}
	
	public int GetNumTask()
	{
		return _num_tasks;
	}
	
	public int GetCompletedTaskCount()
	{
		return (int)_worker_pool.getCompletedTaskCount();
	}
	
	public int GetTaskCount()
	{
		return (int)_worker_pool.getTaskCount();
	}
	
	public int GetActiveThreads()
	{
		return _worker_pool.getActiveCount();
	}
	
	public int GetTotalThreads()
	{
		return _worker_pool.getPoolSize();
	}
	
	public int GetMaxThreads()
	{
		return _worker_pool.getLargestPoolSize();
	}
}