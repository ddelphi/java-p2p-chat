package client_example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import lib.Logger;
import lib.event.EventSystem;
import all.EE;
import client.main.Manager;


/*
 * This object is for getting the input from console
 * 
 * */
public class ConsoleInputer extends Thread {
	private boolean mIsRunning = true;
	private String MSG_EXIT = ":exit";
	
	private EventSystem mEventSystem;

	public ConsoleInputer(Manager manager) {
		mEventSystem = EventSystem.getSingleInstance();
	}
	
	/* run */
	
	@Override
	public void run() {
		scanInput();
	}

	/* actions */

	public void scanInput() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String line;
		boolean flagExit;
		try {
			while (mIsRunning) {
					line = br.readLine();
					flagExit = dealInput(line);
					if (flagExit) {
						break;
					}
			}
			if (br != null) br.close();
		} catch (IOException e) { e.printStackTrace(); }
		
	}

	/* helpers */

	private boolean dealInput(String msg) {
		mEventSystem.trigger(EE.client_input, msg);
		if (msg == MSG_EXIT) {
			Logger.log("[ConsoleInputer] exit.");
			return true;
		}
		return false;
	}
	
	/* stop */
	
	public void stopSelf() {
		mIsRunning = false;
	}
}







