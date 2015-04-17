package client.main;

import lib.Logger;
import lib.QueueSync;
import lib.Verifier;
import lib.event.EventSystem;
import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import all.EE;
import client.action.ActionServer;
import client.action.ActionUser;
import client.action.ActionUser_ListUser;
import client.action.ActionUser_RegularPong;
import client.action.Action_NetworkInfo;
import client.action_verifier.Message;
import client.info.NetworkInfo;
import client.info.ServerInfo;
import client.info.UserInfo;
import client.messenger.Messenger;
import client.messenger.MessengerServer;
import client.messenger.MessengerUser;
import client.network.Receiver;
import client.network.Sender;

import com.alibaba.fastjson.JSONObject;


/*
 * the client manager object,
 * for initializing all the class that should be load at the beginning
 * 
 * */
public class Manager {
	private int mPort = 8898;
	
	private QueueSync<JSONObject> mMsgOutBox;
	private QueueSync<JSONObject> mMsgInBox;

	private Sender mSender;
	private Receiver mReceiver;
	private JSONObject mActionList;
	private Manager mManager;
	

	public Manager() {
		this(((int) Math.random() * 1000) + 9000);
	}

	public Manager(int port) {
		initParams(port);
		initObjects();
		initEvents();
		initVerifiers();
	}

	public void initParams(int port) {
		mActionList = new JSONObject();
		mPort = port;
		mManager = this;
		Logger.log("[client Manager] the port:", port);
	}
	
	public void initObjects() {
		mMsgOutBox = new QueueSync<JSONObject>();
		mMsgInBox = new QueueSync<JSONObject>();
		
		
		addModule("eventSystem", EventSystem.getSingleInstance());
		addModule("miniEvent", new MiniEvent());
		
		addModule("userInfo", new UserInfo(this));
		addModule("networkInfo", new NetworkInfo());
		addModule("serverInfo", new ServerInfo());
		
		addModule("verifier", new Verifier());
		addModule("messenger", new Messenger(this, mMsgOutBox, mMsgInBox));
		addModule("messengerUser", new MessengerUser(this));
		addModule("messengerServer", new MessengerServer(this));
		
		addModule("actionNetworkInfo", new Action_NetworkInfo(this));
		addModule("actionUser", new ActionUser(this));
		addModule("actionUser_regularPong", new ActionUser_RegularPong(this));
		addModule("actionUser_listUser", new ActionUser_ListUser(this));
		addModule("actionServer", new ActionServer(this));
		
		addModule("inputer", new Inputer(this));
		
		new StartEvent(this);
	}
	
	private void initVerifiers() {
		Verifier verifier = (Verifier) getModule("verifier");
		verifier.addAction("message", new Message());
	}

	private void initEvents() {
		MiniEvent event = (MiniEvent) mManager.getModule("miniEvent");
		event.register(EE.client_start, onStart);
	}
	
	/* events */
	
	private Executable onStart = new Executable() {
		public void execute(Object income) {
			NetworkInfo networkInfo = (NetworkInfo) mManager.getModule("networkInfo");
			int port = (Integer) networkInfo.get("port");
			mManager.setPort(port);
		}
	};
	
	/* actions for modules */	
	
	public Object getModule(String name) {
		if (mActionList.containsKey(name)) {
			return mActionList.get(name);
		} else {
			throw new RuntimeException("module '" + name + "' not initialized.");
		}
	}

	public void addModule(String name, Object obj) {
		mActionList.put(name, obj);
	}
	
	/* getter and setter */
	
	public int getPort() {
		return mPort;
	}
	
	public void setPort(int port) {
		mPort = port;
	}

	/* actions */

	public void run() {
		mSender = new Sender(this, mMsgOutBox);
		mReceiver = new Receiver(this, mPort, mMsgInBox);
		mSender.run();
		mReceiver.run();
	}
	
	public void stop() {
		mSender.stop();
		mReceiver.stop();
	}
}

