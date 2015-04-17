package client.network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lib.Json;
import lib.Logger;
import lib.QueueSync;
import lib.TaskPoolManager;
import lib.event.MiniEvent;
import all.EE;
import client.main.Manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;


/*
 * The network object for send out message
 * 
 * */
public class Sender implements TaskPoolManager {


	private ExecutorService mPool;
	private int mPoolSize = 50;
	private int mPoolSizeMax = 100;
	private long mKeepAliveTime = 30;
	private TimeUnit mTimeUnit = TimeUnit.SECONDS;
	private int mTaskBoxSize = 500;
	
	private int mSocketTimeOut = 3000;
	
	private Manager mManager;
	private QueueSync<JSONObject> mMsgOutBox;
	private ArrayBlockingQueue<Runnable> mTaskBox;
	private FetcherRunnable mFetcherRunnable;
	private Thread mFetcherThread;

	public Sender(Manager manager, QueueSync<JSONObject> msgOutBox) {
		initParams(manager, msgOutBox);
	}

	public void initParams(Manager manager, QueueSync<JSONObject> msgOutBox) {
		mManager = manager;
		mMsgOutBox = msgOutBox;
	}

	public void createRequiredObjects() {
		mTaskBox = new ArrayBlockingQueue<Runnable>(mTaskBoxSize);
		
		mPool = new ThreadPoolExecutor(mPoolSize, mPoolSizeMax, mKeepAliveTime, mTimeUnit, mTaskBox);
		mFetcherRunnable = new FetcherRunnable(mManager, mPool, mMsgOutBox, mTaskBox);
		mFetcherThread = new Thread(mFetcherRunnable);
		}

	/* getter and setter */

	@Override
	public void setPool(ExecutorService pool) {
		mPool = pool;
	}

	public void setMsgOutBox(QueueSync<JSONObject> msgOutBox) {
		mMsgOutBox = msgOutBox;
	}

	@Override
	public void setFetcherRunnable(Runnable fetcherRunnable) {
		// pass
	}

	@Override
	public void setWorkerRunnable(Runnable workerRunnable) {
		// pass
	}
	
	/* actions */

	public void run() {
		createRequiredObjects();
		mFetcherThread.start();
	}

	public void stop() {
		mFetcherRunnable.stop();
		mPool.shutdown();
	}

	
	
	
	/* classes */

	public class FetcherRunnable implements Runnable {
		private boolean mIsRunning = true;
		private QueueSync<JSONObject> mMsgOutBox;
		private Manager mManager;
		private ExecutorService mPool;

		public FetcherRunnable(Manager manager, ExecutorService pool, QueueSync<JSONObject> msgOutBox, ArrayBlockingQueue<Runnable> taskBox) {
			mMsgOutBox = msgOutBox;
			mManager = manager;
			mPool = pool;
		}
		
		/* actions */

		@Override
		public void run() {
			while (mIsRunning) {
				JSONObject data = mMsgOutBox.pop();
				mPool.execute(createWorkerRunnable(data));
			}
		}
		
		private Runnable createWorkerRunnable(JSONObject data) {
			return new WorkerRunnable(mManager, data);
		}

		public void stop() {
			mIsRunning = false;
		}
	}

	
	
	
	public class WorkerRunnable implements Runnable {
		private MiniEvent mClientEvent;
		private Manager mManager;
		private JSONObject mTask;

		public WorkerRunnable(Manager manager, JSONObject taskData) {
			mManager = manager;
			mClientEvent = (MiniEvent) mManager.getModule("miniEvent");
			setTask(taskData);
		}
		
		/* getter and setter */
		
		public void setTask(JSONObject taskData) {
			mTask = taskData;
		}
		
		/* actions */

		@Override
		public void run() {
			if (!Thread.interrupted() && mTask != null) {
				dealData(mTask);
				mTask = null;
			}
		}

		/* actions for data */

		public void dealData(JSONObject data) {
			try {
				Socket socket = new Socket(data.getJSONObject("receiver").getString("host"), data.getJSONObject("receiver").getIntValue("port"));
				socket.setSoTimeout(mSocketTimeOut);
				OutputStreamWriter osr = new OutputStreamWriter(socket.getOutputStream());
				
				mClientEvent.trigger(EE.network_send, Json.create(
						"socket", socket,
						"data", data
					));
				osr.write(JSON.toJSONString(data, SerializerFeature.WriteMapNullValue));
				osr.flush();
				mClientEvent.trigger(EE.network_send_after, Json.create(
						"socket", socket,
						"data", data
					));
				
				osr.close();
				socket.close();
			}
			catch (UnknownHostException e) {
				Logger.error("error in the host of the connection:", data.getJSONObject("receiver").getString("host"));
			}
			catch (IOException e) {
				mClientEvent.trigger(EE.network_ioError, data);
				Logger.error("error in connecting to network.");
				// e.printStackTrace();
			}
		}
	}


}