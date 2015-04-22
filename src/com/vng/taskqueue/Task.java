package com.vng.taskqueue;

public abstract class Task implements Runnable
{
	private boolean _is_finished;
	
	public Task()
	{
		_is_finished = false;
	}
	
	public void run()
	{
		HandleTask();
		
		_is_finished = true;
	}
	
	public boolean IsFinished()
	{
		return _is_finished;
	}
	
	protected abstract void HandleTask();
}