package client.messenger;

import lib.Json;
import lib.Logger;
import lib.Verifier;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import all.EE;
import client.info.NetworkInfo;
import client.info.ServerInfo;
import client.info.UserInfo;
import client.main.Manager;

import com.alibaba.fastjson.JSONObject;


/*
 * The Messenger object for the messages
 * which will be sent to the server
 * 
 * */
public class MessengerServer {

	private Manager mManager;
	private MiniEvent mClientEvent;
	private Verifier mVerifier;

	private Messenger mMessenger;
	private ServerInfo mServerInfo;
	private JSONObject mServerInfoDict;
	private UserInfo mUserInfo;
	private JSONObject mUserInfoDict;
	private NetworkInfo mNetworkInfo;


	public MessengerServer(Manager manager) {
		initParams(manager);
		initEvents();
	}

	public void initParams(Manager manager) {
		mManager = manager;
		mClientEvent = (MiniEvent) mManager.getModule("miniEvent");
		mVerifier = (Verifier) mManager.getModule("verifier");
		mMessenger = (Messenger) mManager.getModule("messenger");
		
		mServerInfo = (ServerInfo) mManager.getModule("serverInfo");
		mServerInfoDict = mServerInfo.getInfo();
		mUserInfo = (UserInfo) mManager.getModule("userInfo");
		mUserInfoDict = mUserInfo.getUserInfo();
		mNetworkInfo = (NetworkInfo) mManager.getModule("networkInfo");
	}

	public void initEvents() {
		mClientEvent.register(EE.messagerServer_addMessage, onAddMessage);
		// trigger "messengerServer_getMessage"
	}

	/* events */

	private Executable onAddMessage = new Executable() {
		public void execute(Object data) {
			addMessage((JSONObject) data);
		}
	};

	/* getter and setter */
	
	/* actions */

	public void addMessage(JSONObject msg) {
		try {
			mClientEvent.trigger(EE.messagerServer_addMessage_before, msg);
			boolean status = mVerifier.verify(msg);
			if (!status) {
				Logger.error("[MessengerServer] message verified fail:", msg);
			} else {
				attachInfo(msg);
				mMessenger.addMessage(msg);
			}
		}
		catch (Exception e) {
			Logger.error("[MessengerServer] error adding message:", msg);
			e.printStackTrace();
		}
	}
	
	@Deprecated
	public JSONObject getMessage() {
		return mMessenger.getMessage();
	}

	public JSONObject makeMessage(String action, JSONObject content) {
		return mMessenger.makeMessage(action, content);
	}
	
	/* helpers */
	
	private void attachInfo(JSONObject msg) {
		
		// attach user info
		
		msg.put("sender", Json.create(
			"id", mUserInfoDict.getInteger("id"),
			"host", mNetworkInfo.getInfo().getString("host"),
			"port", mNetworkInfo.getInfo().getIntValue("port")
		));
		
		// attach server info
		
		String host = mServerInfoDict.getString("host");
		Integer port = mServerInfoDict.getInteger("port");
		
		if (host == null || port == null) {
			throw new RuntimeException("no server info.");
		}
		
		msg.put("receiver", Json.create(
			"name", mServerInfoDict.getString("name"),
			"host", host,
			"port", port
		));
	}
	
}