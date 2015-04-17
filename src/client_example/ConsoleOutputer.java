package client_example;

import lib.Logger;
import lib.QueueSync;
import lib.event.EventSystem;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import all.EE;
import client.info.UserInfo;
import client.main.Manager;

import com.alibaba.fastjson.JSONObject;


/*
 * This object is for receiving the message,
 * and then print them in the console
 *  
 * */
public class ConsoleOutputer extends Thread {
	private String ACTION_SELF = "message";
	private boolean mIsRunning = true;
	
	private Manager mManager;
	private QueueSync<JSONObject> mMessageSelfBox;
	private MiniEvent mClientEvent;
	private EventSystem mEventSystem;
	private UserInfo mInfo;

	public ConsoleOutputer(Manager manager) {
		initParams(manager);
		initEvents();
	}

	private void initParams(Manager manager) {
		mManager = manager;
		mClientEvent = (MiniEvent) mManager.getModule("miniEvent");
		mMessageSelfBox = new QueueSync<JSONObject>();
		mEventSystem = EventSystem.getSingleInstance();
		
		mInfo = (UserInfo) mManager.getModule("userInfo"); 
	}
	
	private void initEvents() {
		mClientEvent.register(EE.messager_getMessage, onGetMessage);
		mEventSystem.register(EE.client_output, onOutput);
		mClientEvent.register(EE.client_stop, onStop);
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
			
	private EventSystem.Executable onOutput = new EventSystem.Executable() {
		public void execute(Object data) {
			String msg = (String) data;
			show(msg);
		}
	};

	private Executable onStop = new Executable() {
		public void execute(Object data) {
			show("[app exit]");
			stopSelf();
		}
	};

	/* actions */

	private void loopGetMessage() {
		while (mIsRunning) {
			try {
				JSONObject dict = mMessageSelfBox.pop();
				String msg = getMessage(dict);
				show(msg);
			}
			catch (Exception e) {
				Logger.error("[ConsoleOutputer] error in getting message.");
				e.printStackTrace();
			}
		}
	}
	
	/* actions for show message */
	
	public void show(String msg) {
		if (!mIsRunning) { return; };
		
		System.out.println(getPrefix() + msg);
	}

	/* helpers */
	
	public String getMessage(JSONObject dict) {
		if (!isSelfMessage(dict)) { return null; }
		
		String msg = getMessageBody(dict);
		String name = getName(dict);
		return makeOutput(name, msg);
	}
	
	public boolean isSelfMessage(JSONObject dict) {
		return dict.getString("action").equals(ACTION_SELF);
	}
	
	public String getPrefix() {
		return "";
	}
	
	private String getMessageBody(JSONObject dict) {
		return dict.getJSONObject("content").getString("message");
	}
	
	private String getName(JSONObject dict) {
		Integer id = dict.getJSONObject("sender").getInteger("id");
		JSONObject userDict = mInfo.getUser(id);
		String name;
		if (userDict != null) {
			name = userDict.getString("name");
		} else {
			name = dict.getJSONObject("sender").getString("name");
		}
		return name;
	}

	private String makeOutput(String name, String msg) {
		if (name == null) {
			name = "err";
		}
		if (msg == null) {
			throw new RuntimeException("getting message error.");
		}
		return String.format("[%s]: %s", name, msg);
	}
	
	/* stop */
	
	public void stopSelf() {
		mIsRunning = false;
	}
}
