package server.messager;

import lib.Json;
import lib.Logger;
import lib.QueueSync;
import lib.Verifier;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import server.main.Manager;
import all.EE;

import com.alibaba.fastjson.JSONObject;


/*
 * the main messenger object,
 * is the gate object of network package
 * 
 * */
public class Messenger {

	private Manager mManager;
	private MiniEvent mServerEvent;
	private Verifier mVerifier;
	private QueueSync<JSONObject> mMsgOutBox;
	private QueueSync<JSONObject> mMsgInBox;
	private MessageDispatcherRunnable mMessageDispatcherRunnable;
	private Thread mMessageDispatcherThread;

	public Messenger(Manager manager, QueueSync<JSONObject> msgOutBox, QueueSync<JSONObject> msgInBox) {
		initParams(manager, msgOutBox, msgInBox);
		initEvents();
	}

	public void initParams(Manager manager, QueueSync<JSONObject> msgOutBox, QueueSync<JSONObject> msgInBox) {
		mManager = manager;
		mServerEvent = (MiniEvent) mManager.getModule("miniEvent");
		mVerifier = (Verifier) mManager.getModule("verifier");

		mMsgOutBox = msgOutBox;
		mMsgInBox = msgInBox;
		
		mMessageDispatcherRunnable = new MessageDispatcherRunnable();
		mMessageDispatcherThread = new Thread(mMessageDispatcherRunnable);
	}

	public void initEvents() {
		mServerEvent.register(EE.messager_addMessage, onAddMessage);
		mMessageDispatcherThread.start();
		// trigger "messenger_getMessage"
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
			mServerEvent.trigger(EE.messager_addMessage_before, msg);
			boolean status = mVerifier.verify(msg);
			if (!status) {
				// mServerEvent.trigger(EE.info_warn, String.format("message not right: %1", JSON.toJSONObject(msg)));
			} else {
				Logger.log("[server Messenger] tosend msg:", msg);
				mMsgOutBox.push(msg);
			}
		}
		catch (Exception e) {
			// mServerEvent.trigger(EE.info_error, String.format("error in adding message of %1.", JSON.toJSONObject(msg)));
			e.printStackTrace();
		}
	}

	public JSONObject getMessage() {
		JSONObject dict = mMsgInBox.pop();
		boolean status = mVerifier.verify(dict);
		if (!status) {
			// mServerEvent.trigger(EE.info_warn, String.format("message not right: %1", JOSN.toJSONObject(dict)));
			return null;
		} else {
			mServerEvent.trigger(EE.messager_getMessage, dict);
			return dict;
		}
	}

	public JSONObject makeMessage(String action, JSONObject content) {
		return Json.create(
			"action", action,
			"content", content
		);
	}

	public void stop() {
		mMessageDispatcherRunnable.stop();
	}

	
	
	/* classes */

	private class MessageDispatcherRunnable implements Runnable {
		private boolean mIsRunning = true;

		public void run() {
			while (mIsRunning) {
				JSONObject dict = mMsgInBox.pop();
				boolean status = mVerifier.verify(dict);
				if (status) {
					mServerEvent.trigger(EE.messager_getMessage, dict);
				}
			}
		}
		
		public void stop() {
			mIsRunning = false;
		}
	}
}