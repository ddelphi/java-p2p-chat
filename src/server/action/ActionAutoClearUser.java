package server.action;

import java.util.Date;

import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import server.info.UserList;
import server.main.Manager;
import all.EE;

import com.alibaba.fastjson.JSONObject;


/*
 * actions for auto clear users that in user list
 * which are not activity in N time
 * 
 * */
public class ActionAutoClearUser {
	
	private int mTimeSpan = 120000;
	private UserList mUserList;
	private AutoClearRunnable mAutoClearRunnable;
	private Thread mAutoClearThread;
	private MiniEvent mServerEvent;
	
	public ActionAutoClearUser(Manager manager) {
		initParams(manager);
		initEvents();
	}
	
	private void initParams(Manager manager) {
		mServerEvent = (MiniEvent) manager.getModule("miniEvent");
		mUserList = (UserList) manager.getModule("userList");
		mAutoClearRunnable = new AutoClearRunnable();
		mAutoClearThread = new Thread(mAutoClearRunnable);
	}
	
	private void initEvents() {
		mServerEvent.register(EE.server_switch_action, onSwitchAction);
	}
	
	/* events */
	
	private Executable onSwitchAction = new Executable() {
		public void execute(Object income) {
			boolean state = (Boolean) income;
			if (state) {
				start();
			} else {
				stop();
			}
		}
	};
	
	/* actions */
	
	public void start() {
		if (mAutoClearThread.isAlive()) { return; }
		mAutoClearThread.start();
	}
	
	public void stop() {
		mAutoClearRunnable.stop();
	}

	
	/* runnable */
	
	class AutoClearRunnable implements Runnable {
		private boolean mIsRunning = true;

		public void run() {
			while (mIsRunning) {
				try {
					JSONObject user;
					long now = new Date().getTime();
					for (int i = 0; i < mUserList.size(); i++) {
						user = mUserList.get(i);
						if (now - user.getLong("lastTime") > mTimeSpan) {
							mUserList.set(i, null);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					if (!mIsRunning) { break; }
					Thread.sleep(mTimeSpan);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		
		public void stop() {
			mIsRunning = false;
			Thread.interrupted();
		}
	}
	
}