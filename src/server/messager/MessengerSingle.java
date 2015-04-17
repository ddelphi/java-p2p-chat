package server.messager;

import lib.Logger;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import server.info.Info;
import server.info.UserList;
import server.main.Manager;
import all.EE;

import com.alibaba.fastjson.JSONObject;


/*
 * the messenger object for send back the message to single user
 * 
 * */
public class MessengerSingle {

	private Manager mManager;
	private MiniEvent mClientEvent;
	private UserList mUserList;
	private Messenger mMessenger;
	private Info mInfo;

	public MessengerSingle(Manager manager) {
		initParams(manager);
		initEvents();
	}

	public void initParams(Manager manager) {
		mManager = manager;
		mClientEvent = (MiniEvent) mManager.getModule("miniEvent");
		mMessenger = (Messenger) mManager.getModule("messenger");
		
		mInfo = (Info) manager.getModule("info");
		mUserList = mInfo.getUserList();
	}

	public void initEvents() {
		mClientEvent.register(EE.messagerSingle_addMessage, onAddMessage);
		// trigger "messengerSingle_getMessage"
	}

	/* events */

	private Executable onAddMessage = new Executable() {
		public void execute(Object data) {
			JSONObject msg = (JSONObject) data;
			attachReceiver(msg);
			addMessage((JSONObject) data);
		}
	};

	/* getter and setter */
	
	/* actions */

	public void addMessage(JSONObject msg) {
		mMessenger.addMessage(msg);
	}

	public JSONObject getMessage() {
		return mMessenger.getMessage();
	}

	public JSONObject makeMessage(String action, JSONObject content) {
		return mMessenger.makeMessage(action, content);
	}
	
	/* helpers */
	
	private boolean attachReceiver(JSONObject msg) {
		JSONObject receiver = msg.getJSONObject("receiver");
		Integer id = receiver.getInteger("id");

		// if it has host and port
		if (receiver.get("host") != null && receiver.get("port") != null) {
			// pass
		}
		// if only has the id
		else if (id != null && id >= 0 && id < mUserList.size()) {
			receiver.put("host", mUserList.get(id).getString("host"));
			receiver.put("port", mUserList.get(id).getInteger("port"));
		}
		// all else
		else {
			Logger.log("[server MS] error id:", id);
			mClientEvent.trigger(EE.errorManager_record, msg);
			return false;
		}
		return true;
	}
}