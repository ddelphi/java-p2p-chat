package server.messager;

import lib.Json;
import lib.Verifier;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import server.info.Info;
import server.info.NetworkInfo;
import server.info.UserList;
import server.main.Manager;
import all.EE;

import com.alibaba.fastjson.JSONObject;



/*
 * the messenger object for sending messages to all the user
 * 
 */
public class MessengerUser {

	private Manager mManager;
	private MiniEvent mServerEvent;
	private Verifier mVerifier;
	private MessengerSingle mMessenger;
	private UserList mUserList;
	private Info mInfo;
	private NetworkInfo mNetworkInfo;

	public MessengerUser(Manager manager) {
		initParams(manager);
		initEvents();
	}

	public void initParams(Manager manager) {
		mManager = manager;
		mServerEvent = (MiniEvent) mManager.getModule("miniEvent");
		mVerifier = (Verifier) mManager.getModule("verifier");
		mMessenger = (MessengerSingle) mManager.getModule("messengerSingle");
		
		mInfo = (Info) mManager.getModule("info");
		mUserList = mInfo.getUserList();
		mNetworkInfo = (NetworkInfo) mManager.getModule("networkInfo");
	}

	public void initEvents() {
		mServerEvent.register(EE.messagerUser_addMessage, onAddMessage);
		// trigger "messengerUser_getMessage"
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
			mServerEvent.trigger(EE.messagerUser_addMessage_before, msg);
			boolean status = mVerifier.verify(msg);
			if (!status) {
				System.out.println("[server MessengerUser] message verified fail: " + msg);
			} else {
				bulkAddMessage(msg);
			}
		}
		catch (Exception e) {
			System.out.println("[server MessengerUser] error adding message: " + msg);
			e.printStackTrace();
		}
	}

	public JSONObject getMessage() {
		return mMessenger.getMessage();
	}

	public JSONObject makeMessage(String action, JSONObject content) {
		return mMessenger.makeMessage(action, content);
	}
	
	/* helpers */
	
	private void bulkAddMessage(JSONObject msg) {
		attachSenderInfo(msg);
		
		JSONObject newMsg;
		JSONObject targetUser;
		for (int i = 0; i < mUserList.size(); i++) {
			targetUser = mUserList.get(i);
			if (targetUser == null) { continue; };
			
			newMsg = (JSONObject) msg.clone();
			attachReceiverInfo(newMsg, targetUser);
			
			mMessenger.addMessage(newMsg);
		}
	}
	
	private JSONObject attachSenderInfo(JSONObject msg) {
		msg.put("sender", Json.create(
			"host", mNetworkInfo.get("host"),
			"port", mNetworkInfo.get("port")
		));
		return msg;
	}

	private JSONObject attachReceiverInfo(JSONObject msg, JSONObject targetUser) {
		msg.put("receiver", Json.create(
			"id", targetUser.getInteger("id"),
			"host", targetUser.getString("host"),
			"port", targetUser.getIntValue("port")
		));
		return msg;
	}
}