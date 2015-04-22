package com.vng.taskqueue;

import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.*;
import com.vng.log.*;
import com.vng.netty.Server;
import com.vng.skygarden.game.DatabaseID;
import com.vng.echo.ServerNewsBoard;

public class TaskControl
{
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public void executeTaskFor3Hour()
	{
		final Runnable execute = new Runnable()
		{
			public void run()
			{
				ServerNewsBoard.s_serverNewsBoardManager.refresh();
			}
		};
		
		final ScheduledFuture<?> executerHandle = scheduler.scheduleAtFixedRate(execute, DatabaseID.NEWS_BOARD_REFRESH_TIME, DatabaseID.NEWS_BOARD_REFRESH_TIME, SECONDS);
		
		// stop schedule after 1 hour
		// scheduler.schedule(new Runnable()
							// {
								// public void run()
								// {
									// executerHandle.cancel(true);
								// }
							// }, 60 * 60, SECONDS);
	}
}