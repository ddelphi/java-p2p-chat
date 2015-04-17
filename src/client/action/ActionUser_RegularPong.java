package client.action;

import lib.Json;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import all.EE;
import client.info.UserInfo;
import client.main.Manager;
import client.messenger.MessengerServer;

import com.alibaba.fastjson.JSONObject;


/*
 * actions for regular sending active state to server of the user
 * 
 * */
public class ActionUser_RegularPong {
	private final String ACTION_REGULAR_PONG_USER = "regularPongUser";

	private Manager mManager;
	private UserInfo mUserInfo;
	private RegularPongRunnable mRegularPongRunnable;
	private Thread mRegularPongThread;
	private MiniEvent mClientEvent;
	private int mPongSpanTime = 60000;
	private MessengerServer mMessengerServer;

	public ActionUser_RegularPong(Manager manager) {
		initParams(manager);
		initEvents();
	}

	public void initParams(Manager manager) {
		mManager = manager;
		mClientEvent = (MiniEvent) mManager.getModule("miniEvent");
		
		mMessengerServer = (MessengerServer) mManager.getModule("messengerServer");
		mUserInfo = (UserInfo) mManager.getModule("userInfo");
		
		mRegularPongRunnable = new RegularPongRunnable();
		mRegularPongThread = new Thread(mRegularPongRunnable);
	}

	public void initEvents() {
		mClientEvent.register(EE.messager_getMessage, onReceiveResult);
		mClientEvent.register(EE.client_start, onStart);
		mClientEvent.register(EE.client_switch_action, onSwitchAction);
	}

	/* events */
	
	private Executable onReceiveResult = new Executable() {
		public void execute(Object data) {
			JSONObject obj = (JSONObject) data;
			String action = obj.getString("action");
			
		try {
				if (action.equals(ACTION_REGULAR_PONG_USER)) {
					onRegularPongUserResult.execute(obj);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private Executable onRegularPongUserResult = new Executable() {
		public void execute(Object data) {
			JSONObject obj = (JSONObject) data;
			if (obj.getBoolean("content") == null) {
				// nothing
				// let the server auto clear the not active user
				return;
			}
			mClientEvent.trigger(EE.actionUser_regularPongUser_done, obj);			
		}
	};

	private Executable onStart = new Executable() {
		public void execute(Object data) {
			startRegularPongUser();
		}
	};
	
	private Executable onSwitchAction = new Executable() {
		public void execute(Object data) {
			boolean state = (Boolean) data;
			if (state) {
				startRegularPongUser();
			} else {
				stop();
			}
		}
	};

	/* actions */

	public boolean startRegularPongUser() {
		if (mRegularPongThread.isAlive()) { return false; }
		
		int id = mUserInfo.getUserInfo().getInteger("id");
		mRegularPongRunnable.setup(mMessengerServer, id, mPongSpanTime);
		mRegularPongThread.start();
		return true;
	}

	public void stop() {
		mRegularPongRunnable.stop();
		mRegularPongThread.interrupt();
	}



	/* objects */

	static public class RegularPongRunnable implements Runnable {
		private MessengerServer mMessengerServer;
		private int mId;
		private int mSpanTime;
		private JSONObject mMsg;
		private boolean mIsRunning = true;

		public void setup(MessengerServer messagerServer, int id, int spanTime) {
			mMessengerServer = messagerServer;
			mId = id;
			mSpanTime = spanTime;

			initMessage();
		}

		public void initMessage() {
			mMsg = mMessengerServer.makeMessage("regularPongUser", Json.create(
				"id", mId
			));
		}

		public void run() {
			while (mIsRunning) {
				pongUser();
				sleep();
			}
		}

		/* actions */

		public void pongUser() {
			mMessengerServer.addMessage(mMsg);
		}

		public void sleep() {
			try {
				Thread.sleep(mSpanTime);
			} catch (InterruptedException e) {
				stop();
			}
		}

		public void stop() {
			mIsRunning = false;
		}
	}
}