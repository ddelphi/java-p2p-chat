package example;


import client.main.Manager;


public class Example {
	public Manager mManager;
	public Example(int stopTime) {
		initParams(stopTime);
	}
	
	public void initParams(int stopTime) {
		int port = (int) (Math.random() * 1000) + 9000;
		
		mManager = new Manager(port);
		mManager.run();

		AutoClient autoClient = new AutoClient(mManager);
		autoClient.setStopTime(stopTime);
		autoClient.start();
		
		new AutoClientOutputer(mManager).start();
	}

}
