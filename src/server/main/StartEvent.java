package server.main;

import lib.event.EventSystem;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import all.EE;


/*
 * This is the state object, for check the state of some actions,
 * and then dispatch the "start" event
 * 
 * */
public class StartEvent {
	boolean mIsServerInfoSet = false;

	private EventSystem mEventSystem;
	private MiniEvent mServerEvent;

	public StartEvent(Manager manager) {
		initParams(manager);
		initEvents();
	}
	
	private void initParams(Manager manager) {
		mEventSystem = EventSystem.getSingleInstance();
		mServerEvent = (MiniEvent) manager.getModule("miniEvent");
	}
	
	private void initEvents() {
		mServerEvent.register(EE.server_command_setServer_done, onSetServerDone);
	}
	
	/* events */
	
	private Executable onSetServerDone = new Executable() {
		public void execute(Object data) {
			mIsServerInfoSet = true;
			toStart();
		}
	};


	/* actions */
	
	private void toStart() {
		if (mIsServerInfoSet) {
			mIsServerInfoSet = false;
			
			mServerEvent.trigger(EE.server_start, null);
			mEventSystem.trigger(EE.server_start, null);
		}
	}
	
	
}