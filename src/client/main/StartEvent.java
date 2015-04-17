package client.main;

import all.EE;
import lib.Logger;
import lib.event.EventSystem;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;


/*
 * This is the state object, for check the state of some actions,
 * and then dispatch the "start" event
 * 
 * */
public class StartEvent {
	boolean mIsServerInfoSet = false;
	boolean mIsUserInfoSet =false;
	boolean mIsGetListUserDone;

	private EventSystem mEventSystem;
	private MiniEvent mClientEvent;

	public StartEvent(Manager manager) {
		initParams(manager);
		initEvents();
	}
	
	private void initParams(Manager manager) {
		mEventSystem = EventSystem.getSingleInstance();
		mClientEvent = (MiniEvent) manager.getModule("miniEvent");
	}
	
	private void initEvents() {
		mClientEvent.register(EE.serverInfo_done, onSetServerDone);
		mClientEvent.register(EE.userInfo_done, onGetUserDone);
		mClientEvent.register(EE.actionUser_getListUser_done, onGetListUserDone);
	}
	
	/* events */
	
	private Executable onSetServerDone = new Executable() {
		public void execute(Object data) {
			mIsServerInfoSet = true;
			Logger.info("[info] setServer done");
			toStart();
		}
	};
	
	private Executable onGetUserDone = new Executable() {
		public void execute(Object data) {
			mIsUserInfoSet = true;
			Logger.info("[info] setUser done");
			toStart();
		}
	};

	private Executable onGetListUserDone = new Executable() {
		public void execute(Object income) {
			mIsGetListUserDone = true;
			Logger.info("[info] getListUser done");
			toStart();
		}
	};

	/* actions */
	
	private void toStart() {
		if (mIsServerInfoSet && mIsUserInfoSet && mIsGetListUserDone) {
			Logger.info("[info] app started");
			mIsServerInfoSet = false;
			mIsUserInfoSet = false;
			mIsGetListUserDone = false;
			
			mClientEvent.trigger(EE.client_start, null);
			mEventSystem.trigger(EE.client_start, null);
		}
	}
	
	
}