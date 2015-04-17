package client_example;

import lib.Logger;



/*
 * The user B
 * 
 * */
public class Main_2 {
	/* main */
	
	public static void main(String[] args) {
		
		// setup the Logger state
		
		Logger.setBitState(false);
		Logger.setLogState(false);
		
		// create server and client
		
		int clientPort = (int) (Math.random() * 1000) + 8000;
		
		Client client = new Client(clientPort);
		client.run();
	}
}