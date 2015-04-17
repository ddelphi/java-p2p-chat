package client.action;

import java.net.InetAddress;
import java.net.UnknownHostException;

import client.info.NetworkInfo;
import client.main.Manager;


/*
 * actions for network info
 * 
 * */
public class Action_NetworkInfo {
		
	private NetworkInfo mNetworkInfo;
	private int mPort;

	public Action_NetworkInfo(Manager manager) {
		initParams(manager);
	}
	
	public void initParams(Manager manager) {
		mNetworkInfo = (NetworkInfo) manager.getModule("networkInfo");
		mPort = manager.getPort();
		initNetworkInfo(mPort);
	}
	
	public void initNetworkInfo(int port) {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			
			mNetworkInfo.set("host", addr.getHostAddress());
			mNetworkInfo.set("port", port);
			
		} catch (UnknownHostException e) { e.printStackTrace(); }
	}

}
