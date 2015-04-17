package server.action;

import java.net.Socket;
import java.util.Date;
import java.util.List;

import lib.Json;
import lib.Verifier;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import server.info.UserList;
import server.main.Manager;
import server.messager.MessengerSingle;
import server.messager.MessengerUser;
import all.EE;

import com.alibaba.fastjson.JSONObject;


/*
 * object for dealing all the user actions
 * 
 * */
public class ActionOnline {
	private final String ACTION_NAME = "action";
	private final String ACTION_NEW = "newUser";
	private final String ACTION_REMOVE = "removeUser";
	private final String ACTION_REGULAR_PONG = "regularPongUser";
	private final String ACTION_GETLIST = "getListUser";

	private Manager mManager;
	private MiniEvent mServerEvent;
	public UserList mUserList;
	private MessengerSingle mMessengerSingle;
	private Verifier mVerifier;
	private MessengerUser mMessengerUser;

	public ActionOnline(Manager manager) {
		initParams(manager);
		initEvents();
	}

	private void initParams(Manager manager) {
		mManager = manager;
		mServerEvent = (MiniEvent) mManager.getModule("miniEvent");
		mUserList = (UserList) mManager.getModule("userList");
		mMessengerSingle = (MessengerSingle) mManager.getModule("messengerSingle");
		mMessengerUser = (MessengerUser) mManager.getModule("messengerUser");
		mVerifier = (Verifier) mManager.getModule("verifier");
	}

	private void initEvents() {
		mServerEvent.register(EE.network_receive, onGetData);
	}

	/* events */

	private Executable onGetData = new Executable() {
		public void execute(Object income) {
			JSONObject data = (JSONObject) income;
			JSONObject dict = data.getJSONObject("data");
			Socket socket = (Socket) data.get("socket");
			
			JSONObject res = processDealData(dict, socket);
			processAddToMsgOutBox(res);
		}
	};

	/* processes */

	public JSONObject processDealData(JSONObject dict, Socket socket) {
		String action = dict.getString(ACTION_NAME);
		Object result = null;
		try {
			if (mVerifier.verify(dict.getJSONObject("content"))) {
			
				if (action.equals(ACTION_NEW)) {
					result = newUser(dict, socket);
				}
				else if (action.equals(ACTION_REMOVE)) {
					result = removeUser(dict);
				}
				else if (action.equals(ACTION_REGULAR_PONG)) {
					result = regularPongUser(dict);
				}
				else if (action.equals(ACTION_GETLIST)) {
					result = getListUser(dict);
				}
				else {
					result = null;
				}
			
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return Json.create(
			"action", action,
			"content", result,
			"sender", dict.getJSONObject("receiver"),
			"receiver", dict.getJSONObject("sender")
		);
	}

	private void processAddToMsgOutBox(JSONObject dict) {
		String action = dict.getString(ACTION_NAME);
	
		if (action.equals(ACTION_REMOVE)) {
			mMessengerUser.addMessage(dict);
		}
		mMessengerSingle.addMessage(dict);
	}

	/* actions */

	public JSONObject newUser(JSONObject dict, Socket socket) {
		long time = new Date().getTime();
		JSONObject content = dict.getJSONObject("content");
		int id = mUserList.size();
		
		JSONObject user = Json.create(
			"id", id,
			"name", content.getString("name"),
			"host", getRemoteHost(socket),
			"port", content.getIntValue("port"),
			"addTime", time,
			"lastTime", time
		);

		
		// update the sender's id
		dict.getJSONObject("sender").put("id", id);
		return mUserList.add(user);
	}

	public JSONObject removeUser(JSONObject dict) {
		int id = dict.getJSONObject("content").getIntValue("id");
		mUserList.remove(id);
		return dict.getJSONObject("content");
	}

	public boolean regularPongUser(JSONObject dict) {
		int id = dict.getJSONObject("content").getIntValue("id");
		boolean status = mUserList.updateLastTime(id);
		return status;
	}

	public List<JSONObject> getListUser(JSONObject dict) {
		int id = dict.getJSONObject("content").getIntValue("lastId");
		List<JSONObject> result = mUserList.getListUser(id);
		return result;
	}
	
	/* helpers */
	
	private String getRemoteHost(Socket socket) {
		return socket.getInetAddress().getHostAddress();
	}

}



