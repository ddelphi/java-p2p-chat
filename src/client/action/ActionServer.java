package client.action;

import lib.Json;
import lib.Logger;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import all.EE;
import client.info.ServerInfo;
import client.main.Manager;
import client.messenger.MessengerServer;

import com.alibaba.fastjson.JSONObject;


/*
 * actions for server
 * 
 * */
public class ActionServer {
	private String ACTION_GET_SERVER_INFO = "getServerInfo";
	private String COMMAND_SPLITER = "[|]";
	private String mServerHostPattern = "\\d{1,3}(\\.\\d{1,3}){3}";
	private String mServerPortPattern = "\\d{1,5}";
	
	ServerInfo mInfo;
	private MessengerServer mMessengerServer;
	private Manager mManager;
	private MiniEvent mClientEvent;
	
	public ActionServer(Manager manager) {
		initParams(manager);
		initEvents();
	}
	
	public void initParams(Manager manager) {
		mManager = manager;
		mInfo = (ServerInfo) manager.getModule("serverInfo");
		mClientEvent = (MiniEvent) manager.getModule("miniEvent");
		mMessengerServer = (MessengerServer) mManager.getModule("messengerServer");
	}
	
	public void initEvents() {
		mClientEvent.register(EE.client_command_setServer, onSetServerCommand);
		mClientEvent.register(EE.messager_getMessage, onGetMessage);
	}
	
	/* getter and setter */

	
	/* events */
	
	private Executable onSetServerCommand = new Executable() {
		public void execute(Object data) {
			try {
				JSONObject dict = parseData((String) data);
				setServer(dict);
				toUpdateServerInfoFromServer(dict);
			}
			catch (Exception e) {
				Logger.error("error in setting server info:", data);
				// e.printStackTrace();
			}
		}
	};
	
	private Executable onGetMessage = new Executable() {
		@Override
		public void execute(Object income) {
			JSONObject data = (JSONObject) income;
			try {
				if (data.getString("action").equals(ACTION_GET_SERVER_INFO)) {
					updateServerInfo(data);
				}
			}
			catch (Exception e) {
				Logger.error("error in updating server info: ", data);
				e.printStackTrace();
			}
		}
	};
	
	
	/* actions */
	
	public void toUpdateServerInfoFromServer(JSONObject dict) {
		JSONObject msg = mMessengerServer.makeMessage(ACTION_GET_SERVER_INFO, Json.create());
		mMessengerServer.addMessage(msg);
	}

	public void updateServerInfo(JSONObject dict) {
		JSONObject content = dict.getJSONObject("content");
		setServer(content);
		mClientEvent.trigger(EE.serverInfo_done, dict);
	}

	public void setServer(JSONObject dict) {
		if (dict == null) { return; }
		
		String host = dict.getString("host");
		Integer port = dict.getInteger("port");
		String name = dict.getString("name");
		
		// leave out the name check
		if (host == null || port == null) {
			Logger.error("error in setting server info: " + dict);
			return;
		}
		mInfo.put("host", host);
		mInfo.put("port", port);
		mInfo.put("name", name);
	}
	
	public JSONObject parseData(String data) {
		String[] splits = data.split(COMMAND_SPLITER );
		if (!isCorrectServerInfo(splits)) {
			throw new RuntimeException("server info not right: " + data);
		}
		return Json.create(
			"host", splits[0],
			"port", Integer.valueOf(splits[1])
		);
	}
	
	private boolean isCorrectServerInfo(String[] arr) {
		if (arr.length != 2
				|| !arr[0].matches(mServerHostPattern)
				|| !arr[1].matches(mServerPortPattern)) {
			return false;
		}
		return true;
	}
}
