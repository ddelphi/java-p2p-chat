package client_example;

import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import all.EE;
import client.main.Manager;



/*
 * The client sample code
 * To initialize the Manager object, and the console's inputer and outputer object,
 * for interacting
*/
public class Client {

	private Manager mManager;
	private MiniEvent mClientEvent;
	private ConsoleInputer mConsoleInputer;
	private ConsoleOutputer mConsoleOutputer;

	public Client(int port) {
		initObject(port);
		initEvents();
	}

	private void initObject(int port) {
		mManager = new Manager(port);
		
		mClientEvent = (MiniEvent) mManager.getModule("miniEvent");
		mConsoleOutputer = new ConsoleOutputer(mManager);
		mConsoleInputer = new ConsoleInputer(mManager);
	}

	private void initEvents() {
		mClientEvent.register(EE.client_run, onRun);
	}
	
	/* events */
	
	private Executable onRun = new Executable() {
		public void execute(Object income) {
			int port = (Integer) income;
			mManager.setPort(port);
			mManager.run();
		}
	};


	/* actions */

	public void run() {
		mConsoleInputer.start();
		mConsoleOutputer.start();
	}
	
	public void stop() {
		mManager.stop();
		mConsoleInputer.stopSelf();
		mConsoleOutputer.stopSelf();
	}
}










