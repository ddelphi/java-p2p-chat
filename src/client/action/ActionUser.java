package client.action;

import lib.Json;
import lib.Logger;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import all.EE;
import client.info.NetworkInfo;
import client.info.UserInfo;
import client.main.Manager;
import client.messenger.MessengerServer;

import com.alibaba.fastjson.JSONObject;


/*
 * actions for user info
 * 
 * */
public class ActionUser {
	private final String ACTION_NEW_USER = "newUser";
	private final String ACTION_REMOVE_USER = "removeUser";

	private Manager mManager;
	private UserInfo mUserInfo;
	private MiniEvent mClientEvent;
	private NetworkInfo mNetworkInfo;
	private MessengerServer mMessengerServer;
	private JSONObject mUserInfoDict;

	public ActionUser(Manager manager) {
		initParams(manager);
		initEvents();
	}

	public void initParams(Manager manager) {
		mManager = manager;
		mClientEvent = (MiniEvent) mManager.getModule("miniEvent");
		
		mMessengerServer = (MessengerServer) mManager.getModule("messengerServer");
		mUserInfo = (UserInfo) mManager.getModule("userInfo");
		mUserInfoDict = mUserInfo.getUserInfo();
		mNetworkInfo = (NetworkInfo) mManager.getModule("networkInfo");
		
	}

	public void initEvents() {
		mClientEvent.register(EE.messager_getMessage, onReceiveResult);
		mClientEvent.register(EE.client_command_setUser, onSetUserCommand);
		mClientEvent.register(EE.network_ioError, onNetworkIoError);
	}

	/* events */
	
	private Executable onSetUserCommand = new Executable() {
		public void execute(Object income) {
			// need only name for setting user info
			String name = (String) income;
			newUser(name);
		}
	};
	
	private Executable onNetworkIoError = new Executable() {
		public void execute(Object income) {
			JSONObject user = (JSONObject) income;
			onRemoveUserResult.execute(user);
		}
	};
	
	// a dispatcher
	private Executable onReceiveResult = new Executable() {
		public void execute(Object data) {
			JSONObject obj = (JSONObject) data;
			String action = obj.getString("action");
			
			try {
				if (action.equals(ACTION_NEW_USER)) {
					onNewUserResult.execute(obj);
				} else if (action.equals(ACTION_REMOVE_USER)) {
					onRemoveUserResult.execute(obj);
				}
			}
			catch (Exception e) {
				Logger.error("error in onReceiveResult(): " + obj);
				e.printStackTrace();
			}
		}
	};
	
	private Executable onNewUserResult = new Executable() {
		public void execute(Object data) {
			JSONObject obj = (JSONObject) data;
			JSONObject contentDict = obj.getJSONObject("content");
			if (contentDict == null) {
				// no retry
				Logger.error("error in getting content in onNewUserResult().");
				return;
			}
			mUserInfo.updateSelfUser(contentDict);
			mClientEvent.trigger(EE.actionUser_newUser_done, obj);
		}
	};

	private Executable onRemoveUserResult = new Executable() {
		public void execute(Object data) {
			JSONObject obj = (JSONObject) data;
			if (obj.get("content") == null) {
				Logger.error("error in onRemoveUserResult: content field in message not exists.");
				return;
			}
			
			Integer id = obj.getJSONObject("content").getInteger("id");
			if (id == mUserInfoDict.getInteger("id")) {
				mUserInfo.removeSelfUser();
			}
			if (id != null) {
				mUserInfo.removeUser(id);				
			}
			mClientEvent.trigger(EE.actionUser_removeUser_done, obj);
	}
	};

	/* actions */

	public void newUser(String name) {
		JSONObject msg = mMessengerServer.makeMessage("newUser", Json.create(
			"name", name,
			"host", mNetworkInfo.getInfo().getString("host"),
			"port", mNetworkInfo.getInfo().getIntValue("port")
		));
		mMessengerServer.addMessage(msg);
	}

	public void removeUser() {
		int id = mUserInfo.getUserInfo().getIntValue("id");
		JSONObject msg = mMessengerServer.makeMessage("removeUser", Json.create(
			"id", id
		));
		mMessengerServer.addMessage(msg);
	}

}