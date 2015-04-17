package client.messenger;

import lib.Json;
import lib.QueueSync;
import lib.Verifier;
import lib.event.EventSystem;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import all.EE;
import client.main.Manager;

import com.alibaba.fastjson.JSONObject;


/*
 * The main messenger object,
 * is the gate object of the network package
 * 
 * */
public class Messenger {

	private Manager mManager;
	private MiniEvent mClientEvent;
	private Verifier mVerifier;
	private QueueSync<JSONObject> mMsgOutBox;
	private QueueSync<JSONObject> mMsgInBox;
	private MessageDispatcherRunnable mMessageDispatcherRunnable;
	private Thread mMessageDispatcherThread;
	private EventSystem mEventSystem;

	public Messenger(Manager manager, QueueSync<JSONObject> msgOutBox, QueueSync<JSONObject> msgInBox) {
		initParams(manager, msgOutBox, msgInBox);
		initEvents();
	}

	public void initParams(Manager manager, QueueSync<JSONObject> msgOutBox, QueueSync<JSONObject> msgInBox) {
		mManager = manager;
		mEventSystem = EventSystem.getSingleInstance();
		mClientEvent = (MiniEvent) mManager.getModule("miniEvent");
		mVerifier = (Verifier) mManager.getModule("verifier");
		mMessageDispatcherRunnable = new MessageDispatcherRunnable();
		mMessageDispatcherThread = new Thread(mMessageDispatcherRunnable);

		mMsgOutBox = msgOutBox;
		mMsgInBox = msgInBox;
	}

	public void initEvents() {
		mClientEvent.register(EE.messager_addMessage, onAddMessage);
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
			mClientEvent.trigger(EE.messager_addMessage_before, msg);
			boolean status = mVerifier.verify(msg);
			if (!status) {
				// mClientEvent.trigger(EE.info_warn, String.format("message not right: %1", msg));
			} else {
				mMsgOutBox.push(msg);
			}
		}
		catch (Exception e) {
			// mClientEvent.trigger(EE.info_error, String.format("error in adding message of %1.", msg));
			e.printStackTrace();
		}
	}

	@Deprecated
	public JSONObject getMessage() {
		JSONObject dict = mMsgInBox.pop();
		boolean status = mVerifier.verify(dict);
		if (!status) {
			// mClientEvent.trigger(EE.info_warn, String.format("message not right: %1", JOSN.toJSONObject(dict)));
			return null;
		} else {
			mClientEvent.trigger(EE.messager_getMessage, dict);
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
					mClientEvent.trigger(EE.messager_getMessage, dict);
					mEventSystem.trigger(EE.messager_getMessage, dict);
				}
			}
		}
		
		public void stop() {
			mIsRunning = false;
		}
	}
}