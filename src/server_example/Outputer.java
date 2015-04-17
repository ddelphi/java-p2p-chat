package server_example;

import lib.QueueSync;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import server.main.Manager;
import all.EE;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class Outputer extends Thread {
	private String ACTION_SELF = "message";
	private boolean mIsRunning = true;
	
	private Manager mManager;
	private QueueSync<JSONObject> mMessageSelfBox;
	private MiniEvent mClientEvent;

	public Outputer(Manager manager) {
		initParams(manager);
		initEvents();
	}

	private void initParams(Manager manager) {
		mManager = manager;
		mClientEvent = (MiniEvent) mManager.getModule("miniEvent");
		
		mMessageSelfBox = new QueueSync<JSONObject>();
	}
	
	private void initEvents() {
		mClientEvent.register(EE.messager_getMessage, onGetMessage);
	}
	
	/* runnable */

	public void run() {
		loopGetMessage();
	}
	
	/* events */
	
	private Executable onGetMessage = new Executable() {
		public void execute(Object data) {
			JSONObject msg = (JSONObject) data;
			if (!isSelfMessage(msg)) { return; }
			
			mMessageSelfBox.push(msg);
		}
	};
			

	/* actions */

	private void loopGetMessage() {
		while (mIsRunning) {
			JSONObject dict = mMessageSelfBox.pop();
			String msg = getMessage(dict);
			show(msg);
		}
	}
	
	public String getMessage(JSONObject dict) {
		System.out.println("[Outputer getMessage]:" + JSON.toJSONString(dict));
		if (!isSelfMessage(dict)) { return null; }
		
		String msg = ((JSONObject) dict.get("content")).getString("message");
		return msg;
	}
	
	public boolean isSelfMessage(JSONObject dict) {
		return dict.getString("action").equals(ACTION_SELF);
	}
	
	/* actions for show message */
	
	public void show(String msg) {
		if (!mIsRunning) { return; };
		
		System.out.println("[Outputer]: " + msg);
	}

	public void exit(String msg) {
		show(msg);
		mIsRunning = false;
	}
}

/*
	maybe adding a listener for seperate the display action.

	using Platform Seperation Pattern
*/