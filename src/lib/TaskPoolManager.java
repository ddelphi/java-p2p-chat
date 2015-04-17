package lib;

import java.util.concurrent.ExecutorService;


public interface TaskPoolManager {

	public void setFetcherRunnable(Runnable fetcherRunnable);
	public void setWorkerRunnable(Runnable workerRunnable);
	public void setPool(ExecutorService pool);

	/* actions */

	public void run();
	public void stop();

}

