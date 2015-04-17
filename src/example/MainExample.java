package example;

import lib.Logger;
import server.main.Manager;


public class MainExample {
	
	static public void main(String[] args) {
		// Logger state
		
		Logger.setBitState(false);
		Logger.setLogState(false);
		
		// server
		
		int serverPort = 9088;
		Manager serverManager = new Manager(serverPort);
		serverManager.run();
		
		// client
		
		Example user1 = new Example(3000);
		Example user2 = new Example(5000);
	}

}
