package client_example;

import lib.Logger;
import server_example.Server;


/*
 * The user A 
 * 
 * */
public class Main_1 {
	/* main */
	
	public static void main(String[] args) {
		
		// setup the Logger state
		
		Logger.setBitState(false);
		Logger.setLogState(false);
		
		// create server and client
		
		int serverPort = 9088;
		int clientPort = (int) (Math.random() * 1000) + 8000;
		
		Server server = new Server(serverPort);
		server.run();
		
		Client client = new Client(clientPort);
		client.run();
	}
}