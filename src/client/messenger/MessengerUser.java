package client.messenger;

import java.util.ArrayList;

import lib.Json;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import all.EE;
import client.info.UserInfo;
import client.main.Manager;

import com.alibaba.fastjson.JSONObject;


/*
 * The Messenger object for dealing the messages
 * which will be sent to users
 * 
 * */
public class MessengerUser {

	private Manager mManager;
	private MiniEvent mClientEvent;
	private Messenger mMessenger;
	private UserInfo mUserInfo;
	private JSONObject mUserInfoDict;
	private ArrayList<JSONObject> mUserList;

	public MessengerUser(Manager manager) {
		initParams(manager);
		initEvents();
	}

	public void initParams(Manager manager) {
		mManager = manager;
		mClientEvent = (MiniEvent) mManager.getModule("miniEvent");
		mMessenger = (Messenger) mManager.getModule("messenger");
		
		mUserInfo = (UserInfo) mManager.getModule("userInfo");
		mUserInfoDict = mUserInfo.getUserInfo();
		mUserList = mUserInfo.getUserList();
	}

	public void initEvents() {
		mClientEvent.register(EE.messagerUser_addMessage, onAddMessage);
		// trigger "messengerUser_getMessage"
	}

	/* events */

	private Executable onAddMessage = new Executable() {
		public void execute(Object data) {
			addMessage((JSONObject) data);
		}
	};
	
	/* actions */

	public void addMessage(JSONObject msg) {
		mClientEvent.trigger(EE.messagerUser_addMessage_before, msg);
		bulkAddMessage(msg);
	}
	
	@Deprecated
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
			"id", mUserInfoDict.get("id"),
			"name", mUserInfoDict.get("name")
		));
		return msg;
	}

	private JSONObject attachReceiverInfo(JSONObject msg, JSONObject targetUser) {
		msg.put("receiver", Json.create(
			"id", targetUser.getIntValue("id"),
			"host", targetUser.getString("host"),
			"port", targetUser.getIntValue("port")
		));
		return msg;
	}
}