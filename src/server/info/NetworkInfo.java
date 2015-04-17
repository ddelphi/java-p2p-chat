package server.info;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import server.main.Manager;
import all.EE;

import com.alibaba.fastjson.JSONObject;


/*
 * network info object
 * 
 * */
public class NetworkInfo {
	
	JSONObject mInfo;
	private MiniEvent mServerEvent;
	private Manager mManager;
	
	public NetworkInfo(Manager manager, int port) {
		initParams(manager, port);
		initEvents();
	}
	
	private void initParams(Manager manager, int port) {
		mManager = manager;
		mServerEvent = (MiniEvent) mManager.getModule("miniEvent");
		mInfo = new JSONObject();
		
		initNetworkInfo(port);
	}
	
	private void initEvents() {
		mServerEvent.register(EE.server_command_setServer, onSetServerCommand);
	}
	
	private void initNetworkInfo(int port) {
		setServerInfo(port);
	}
	
	/* events */
	
	// currently only support the port setting
	private Executable onSetServerCommand = new Executable() {
		public void execute(Object income) {
			String data = (String) income;
			
			setServerInfo(Integer.valueOf(data));
		}
	};
	
	/* getter and setter */
	
	public void put(String key, Object val) {
		mInfo.put(key, val);
	}
	
	public Object get(String key) {
		return mInfo.get(key);
	}
	
	public JSONObject getInfo() {
		return mInfo;
	}
	
	public void setServerInfo(Object host, Object port, Object name) {
		put("host", host);
		put("port", port);
		put("name", name);
		mServerEvent.trigger(EE.server_command_setServer_done, mInfo);
	}
	
	public void setServerInfo(int port) {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			setServerInfo(addr.getHostAddress(), port, "server" + port);
		} catch (UnknownHostException e) { e.printStackTrace(); }
	}
}
