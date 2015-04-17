package example;

import java.util.ArrayList;

import lib.Json;
import lib.TestTool;
import lib.event.EventSystem;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import all.EE;
import client.action.ActionUser;
import client.action.ActionUser_ListUser;
import client.action.ActionUser_RegularPong;
import client.info.UserInfo;
import client.main.Manager;
import client.messenger.Messenger;
import client.messenger.MessengerServer;
import client.messenger.MessengerUser;

import com.alibaba.fastjson.JSONObject;

public class AutoClient {

		private Manager mManager;
		private Messenger mMessenger;
		private MiniEvent mClientEvent;
		private MessengerUser mMessengerUser;
		private MessengerServer mMessengerServer;
		private EventSystem mEventSystem;
		private ActionUser mActionUser;
		
		protected boolean mIsServerInfoDone;
		protected boolean mIsUserInfoDone;
		protected boolean mIsGetListUserDone;
		private ActionUser_ListUser mActionUser_listUser;
		private ActionUser_RegularPong mActionUser_pongUser;
		private ScheduleDeleteThread mScheduleDeleteThread;

		private AutoSender mAutoSenderThread;
		private int mStopTime;

		public AutoClient(Manager manager) {
			initParams(manager);
			initEvents();
		}

		private void initParams(Manager manager) {
			mManager = manager;
			mEventSystem = EventSystem.getSingleInstance();
			mActionUser = (ActionUser) manager.getModule("actionUser");
			mActionUser_listUser = (ActionUser_ListUser) manager.getModule("actionUser_listUser");
			mActionUser_pongUser = (ActionUser_RegularPong) manager.getModule("actionUser_regularPong");
			
			mClientEvent = (MiniEvent) mManager.getModule("miniEvent");
			mMessenger = (Messenger) mManager.getModule("messenger");
			mMessengerUser = (MessengerUser) mManager.getModule("messengerUser");
			mMessengerServer = (MessengerServer) mManager.getModule("messengerServer");
			
			mStopTime = (int) (Math.random() * 10000);
		}
		
		private void initEvents() {
			mClientEvent.register(EE.serverInfo_done, onServerInfoDone);
			mClientEvent.register(EE.userInfo_done, onUserInfoDone);
			mClientEvent.register(EE.actionUser_getListUser_done, onGetListUserDone);
		}

		/* getter and setter */
		
		public void setStopTime(int time) {
			mStopTime = time;
		}

		/* events */

		private Executable onServerInfoDone = new Executable() {
			public void execute(Object income) {
				TestTool.log("[AutoClient] server info done.");
				mIsServerInfoDone = true;
				checkToStart();
			}
		};

		private Executable onUserInfoDone = new Executable() {
			public void execute(Object income) {
				TestTool.log("[AutoClient] user info done.");
				mIsUserInfoDone = true;
				checkToStart();
			}
		};

		private Executable onGetListUserDone = new Executable() {
			public void execute(Object income) {
				TestTool.log("[AutoClient] get list user done.");
				mainStart();
			}
		};

		/* process */
		
		public void start() {
			setupServer();
			setupNewUser();
		}
		
		public void checkToStart() {
			if (mIsServerInfoDone && mIsUserInfoDone) {
				setupUserList();
			}
		}
		
		public void mainStart() {
			if (!mIsServerInfoDone || !mIsUserInfoDone) {
				return;
			}
			mIsServerInfoDone = false;
			mIsUserInfoDone = false;
			

			autoBuildMessageAndSend();
			scheduleDeleteUser();
			// stop();
		}
		
		/* actions */
	
		private void setupServer() {
			String info = "127.0.0.1|9088";
			mClientEvent.trigger(EE.client_command_setServer, info);
		}
		
		private void setupNewUser() {
			String name = "user" + mManager.getPort();
			mClientEvent.trigger(EE.client_command_setUser, name);
		}
		
		private void setupUserList() {
			mClientEvent.trigger(EE.client_command_getListUser, null);
		}
		
		/* actions 2 */
		
		private void autoBuildMessageAndSend() {
			mAutoSenderThread = new AutoSender(mManager);
			mAutoSenderThread.start();
		}
		
		private void scheduleDeleteUser() {
			mScheduleDeleteThread = new ScheduleDeleteThread(mManager, mStopTime);
			mScheduleDeleteThread.start();
		}
		
		private void stop() {
			mAutoSenderThread.stopSelf();
		}
}








class AutoSender extends Thread {
	private boolean mIsRunning = true; 
	private Manager mManager;
	private MessengerUser mMessengerUser;
	private ArrayList<JSONObject> mUserList;
	private int mPort;
	private MiniEvent mClientEvent;

	public AutoSender(Manager manager) {
		mManager = manager;
		
		mClientEvent = (MiniEvent) mManager.getModule("miniEvent");
		mMessengerUser = (MessengerUser) mManager.getModule("messengerUser");

		UserInfo userInfo = (UserInfo) mManager.getModule("userInfo");
		mUserList = userInfo.getUserList();
		mPort = mManager.getPort();
	}

	public void run() {
		// wait for 2 users added
		TestTool.log(mPort + "[AutoSender] waiting...");
		while (mUserList.size() < 2) {
			try {
				mClientEvent.trigger(EE.client_command_getListUser, null);
				Thread.sleep(1000);
				if (mUserList.size() >= 2) {
					break;
				}
			} catch (InterruptedException e) { e.printStackTrace(); }
		}
		TestTool.log(mPort + "[AutoSender] ...okay");

		
		int count = 0;
		while (mIsRunning && count++ < 5) {
			JSONObject msg = autoCreateMessage(count);
			TestTool.log(mPort + "[AutoSender: run] sending message:", msg);
			mMessengerUser.addMessage(msg);
			
			try {
				Thread.sleep((int) Math.random() * 1000);
			} catch (InterruptedException e) { e.printStackTrace(); }
		}
	}

	public JSONObject autoCreateMessage(int count) {
		return mMessengerUser.makeMessage("message", Json.create(
			"message", mPort + " says " + count + count + count
		));
	}
	
	public void stopSelf() {
		mIsRunning = false;
	}

}



class ScheduleDeleteThread extends Thread {
	private Manager mManager;
	private int mTime;
	private ActionUser mActionUser;
	private MiniEvent mEvent;
	
	public ScheduleDeleteThread(Manager manager, int time) {
		mManager = manager;
		mActionUser = (ActionUser) manager.getModule("actionUser");
		mEvent = (MiniEvent) manager.getModule("miniEvent");
		mTime = time;
	}
	
	/* actions */
	
	public void run() {
		try {
			Thread.sleep(mTime);
		} catch (InterruptedException e) { e.printStackTrace(); }
		
		mEvent.trigger(EE.client_switch_action, false);
		mActionUser.removeUser();
	}
}










