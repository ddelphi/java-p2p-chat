package server_example;

import lib.Logger;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import server.main.Manager;
import all.EE;


/*
 * The server object, for initializing the manager object
 * */
public class Server {
	private Manager mManager;
	private MiniEvent mServerEvent;

	public Server(int port) {
		initObject(port);
		initEvents();
	}

	private void initObject(int port) {
		mManager = new Manager(port);
		mServerEvent = (MiniEvent) mManager.getModule("miniEvent");
		
		Logger.log("[server Manager] the port:", port);
	}
	
	private void initEvents() {
		mServerEvent.register(EE.server_start, onStart);
	}
	
	/* events */
	
	private Executable onStart = new Executable() {
		public void execute(Object income) {
			mManager.run();
		}
	};


	/* actions */

	public void run() {
		mManager.run();
	}

}
