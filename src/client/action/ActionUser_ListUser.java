package client.action;

import java.util.ArrayList;

import lib.Json;
import lib.Logger;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import all.EE;
import client.info.UserInfo;
import client.main.Manager;
import client.messenger.MessengerServer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/*
 * actions for the list of user
 * 
 * */
public class ActionUser_ListUser {
	private final String ACTION_GET_LIST_USER = "getListUser";
	private int mListUserSpanTime = 60000;

	private Manager mManager;
	private UserInfo mUserInfo;
	private RegularUpdateListUser mRuuRunnable;
	private Thread mRuuThread;
	private MiniEvent mClientEvent;
	private MessengerServer mMessengerServer;

	public ActionUser_ListUser(Manager manager) {
		initParams(manager);
		initEvents();
	}

	public void initParams(Manager manager) {
		mManager = manager;
		mClientEvent = (MiniEvent) mManager.getModule("miniEvent");
		
		mMessengerServer = (MessengerServer) mManager.getModule("messengerServer");
		mUserInfo = (UserInfo) mManager.getModule("userInfo");
		
		mRuuRunnable = new RegularUpdateListUser();
		mRuuThread = new Thread(mRuuRunnable);
	}

	public void initEvents() {
		mClientEvent.register(EE.messager_getMessage, onReceiveResult);
		mClientEvent.register(EE.client_start, onStart);
		mClientEvent.register(EE.client_switch_action, onSwitchAction);
		mClientEvent.register(EE.client_command_getListUser, onGetListUserCommand);
	}

	/* events */

	private Executable onReceiveResult = new Executable() {
		public void execute(Object data) {
			JSONObject obj = (JSONObject) data;
			String action = obj.getString("action");
			
			try {
				if (action.equals(ACTION_GET_LIST_USER)) {
					onGetListUserResult.execute(obj);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private Executable onGetListUserResult = new Executable() {
		public void execute(Object data) {
			JSONObject obj = (JSONObject) data;
			JSONArray arr = obj.getJSONArray("content");
			if (arr == null) {
				// nothing
				return;
			}
			mUserInfo.updateUserList(arr);
			mClientEvent.trigger(EE.actionUser_getListUser_done, arr);
		}
	};

	private Executable onStart = new Executable() {
		public void execute(Object data) {
			startRegularGetListUser();
		}
	};
	
	private Executable onSwitchAction = new Executable() {
		public void execute(Object data) {
			boolean state = (Boolean) data;
			if (state) {
				startRegularGetListUser();
			} else {
				stop();
			}
		}
	};

	private Executable onGetListUserCommand = new Executable() {
		public void execute(Object data) {
			getListUser();
		}
	};
	
	/* actions */
	
	// only for the beginning when the user list is empty
	public void getListUser() {
		int lastId = mUserInfo.getUserList().size();		// maybe 0
		JSONObject msg = mMessengerServer.makeMessage("getListUser", Json.create(
			"lastId", lastId
		));
		mMessengerServer.addMessage(msg);
	}

	public boolean startRegularGetListUser() {
		if (mRuuThread.isAlive()) { return false; }

		mRuuRunnable.setup(mMessengerServer, mUserInfo, mListUserSpanTime);
		mRuuThread.start();
		return true;
	}

	public void stop() {
		mRuuRunnable.stop();
		mRuuThread.interrupt();
	}
	
	
	/* classes */

	static public class RegularUpdateListUser implements Runnable {
		private boolean mIsRunning = true;

		private MessengerServer mMessengerServer;
		private UserInfo mUserInfo;
		private int mSpanTime;
		private ArrayList<JSONObject> mUserList;
		private int mLastId;
		
		public void setup(MessengerServer messagerServer, UserInfo userInfo, int spanTime) {
			mMessengerServer = messagerServer;
			mUserInfo = userInfo;
			mSpanTime = spanTime;
			
			mUserList = mUserInfo.getUserList();
		}

		public void initMessage() {
			
		}

		public void run() {
			while (mIsRunning) {
				getListUser();
				sleep();
			}
		}

		/* actions */

		public void getListUser() {
			if (mUserList.size() == 0) {
				Logger.log("[AULU] user list size still 0.");
				return;
			}
			mLastId = mUserList.get(mUserList.size() - 1).getIntValue("id") + 1;
			JSONObject msg = mMessengerServer.makeMessage("getListUser", Json.create(
				"lastId", mLastId
			));
			mMessengerServer.addMessage(msg);
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