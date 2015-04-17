package server.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lib.Json;
import lib.QueueSync;
import lib.TaskPoolManager;
import lib.event.MiniEvent;
import all.EE;
import server.main.Manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;


/*
 * The network object for receiving messages
 * 
 * */
public class Receiver implements TaskPoolManager {

	ExecutorService mPool;
	private int mPoolSize = 50;
	private int mPoolSizeMax = 100;
	private long mKeepAliveTime = 30;
	private TimeUnit mTimeUnit = TimeUnit.SECONDS;
	private int mTaskBoxSize = 500;

	Thread mServerThread;
	ServerRunnable mServerRunnable;
	private Manager mManager;
	private MiniEvent mServerEvent;

	private BlockingQueue<Runnable> mTaskBox;
	QueueSync<JSONObject> mMsgInBox;

	private int mPort;
	

	public Receiver(Manager manager, int port, QueueSync<JSONObject> msgInBox) {
		initParams(manager, port, msgInBox);
	}

	private void initParams(Manager manager, int port, QueueSync<JSONObject> msgInBox) {
		mPort = port;
		mManager = manager;
		mServerEvent = (MiniEvent) manager.getModule("miniEvent");
		
		mMsgInBox = msgInBox;
	}

	public void createRequiredObjects() {
		mTaskBox = new ArrayBlockingQueue<Runnable>(mTaskBoxSize);
		
		mPool = new ThreadPoolExecutor(mPoolSize, mPoolSizeMax, mKeepAliveTime, mTimeUnit, mTaskBox);
		mServerRunnable = new ServerRunnable(mManager, mPort, mPool, mMsgInBox, mTaskBox);
		mServerThread = new Thread(mServerRunnable);
	}
	
	/* getter and setter */

	@Override
	public void setFetcherRunnable(Runnable fetcherRunnable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWorkerRunnable(Runnable workerRunnable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPool(ExecutorService pool) {
		// TODO Auto-generated method stub
		
	}


	/* actions */

	@Override
	public void run() {
		createRequiredObjects();
		mServerThread.start();
	}

	public void stop() {
		mServerRunnable.stop();
		mPool.shutdown();
	}

	
	
	
	/* classes */
	
	class ServerRunnable implements Runnable {
		private boolean mIsRunning = true;

		private ServerSocket mServer;
		private Manager mManager;
		private int mPort;
		private ExecutorService mPool;
		private QueueSync<JSONObject> mMsgInBox;
		private BlockingQueue<Runnable> mTaskBox;

		public ServerRunnable(Manager manager, int port, ExecutorService pool,
				QueueSync<JSONObject> msgInBox, BlockingQueue<Runnable> taskBox) {
				mManager = manager;
				mPort = port;
				mPool = pool;
				mMsgInBox = msgInBox;
				mTaskBox = taskBox;
				
				initServer();
		}
		
		private void initServer() {
			try {
				mServer = new ServerSocket(mPort);
			} catch (IOException e1) { e1.printStackTrace(); }
		}
		
		/* actions */
		
		@Override
		public void run() {
			
			while (mIsRunning) {
				try {
					Socket socket = mServer.accept();
					mPool.submit(createWorkerRunnable(socket));
				} catch (IOException e) { e.printStackTrace(); }
			}
		}
		
		public void stop() {
			try {
				mIsRunning = false;
				mServer.close();
			} catch (IOException e) { e.printStackTrace(); }
		}
		
		public Runnable createWorkerRunnable(Socket socket) {
			return new RecvWorkerRunnable(mManager, socket, mMsgInBox);
		}
	}


	

	class RecvWorkerRunnable implements Runnable {
		private boolean mIsRunning = true;

		private QueueSync<Socket> mTaskBox;
		private QueueSync<JSONObject> mMsgInBox;
		private Manager mManager;
		private MiniEvent mServerEvent;

		private Socket mTask;

		public RecvWorkerRunnable(Manager manager, Socket socket, QueueSync<JSONObject> msgInBox) {
			mManager = manager;
			mTask = socket;
			mMsgInBox = msgInBox;
			
			mServerEvent = (MiniEvent) mManager.getModule("miniEvent");
		}

		@Override
		public void run() {
			if (!Thread.interrupted() && mTask != null) {
				try {
					Socket socket = mTask;
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
					StringBuilder str = new StringBuilder();
					String line;
					while ((line = br.readLine()) != null) {
						str.append(line);
					}

					dealMessage(socket, str.toString());
					br.close();
					socket.close();
					
				} catch (IOException e) { e.printStackTrace(); }

			}
		}
		
		public void stop() {
			mIsRunning = false;
		}
		
		/* actions */
		
		private void dealMessage(Socket socket, String msg) {
			JSONObject dict = JSON.parseObject(msg, new TypeReference<JSONObject>() {});
			mServerEvent.trigger(EE.network_receive, Json.create(
				"socket", socket,
				"data", dict
			));
			mMsgInBox.push(dict);
		}

	}

}





