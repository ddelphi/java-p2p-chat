package server.main;
import lib.QueueSync;
import lib.Verifier;
import lib.event.MiniEvent;
import server.action.ActionOnline;
import server.info.Info;
import server.info.NetworkInfo;
import server.messager.Messenger;
import server.messager.MessengerSingle;
import server.messager.MessengerUser;
import server.network.Receiver;
import server.network.Sender;

import com.alibaba.fastjson.JSONObject;


/*
 * the server manager object,
 * for initializing all the objects which should be load at the beginning
 * 
 * */
public class Manager {
	private JSONObject mModuleList;
	private Sender mServerSender;
	private Receiver mServerReceiver;
	private QueueSync<JSONObject> mMsgOutBox;
	private QueueSync<JSONObject> mMsgInBox;
	private int mPort = 9090;
	
	public Manager() {
		this(((int) Math.random() * 1000) + 9000);
	}
	
	public Manager(int port) {
		initParams(port);
		initObejcts();
		// initEvents();
	}

	private void initParams(int port) {
		mModuleList = new JSONObject();
		mPort = port;
	}
	
	private void initObejcts() {
		mMsgOutBox = new QueueSync<JSONObject>();
		mMsgInBox = new QueueSync<JSONObject>();

		addModule("miniEvent", MiniEvent.getSingleInstance());
		addModule("msgOutBox", mMsgOutBox);
		addModule("msgInBox", mMsgInBox);
		
		addModule("networkInfo", new NetworkInfo(this, mPort));
		Info info = new Info();
		addModule("info", info);
		addModule("userList", info.getUserList());
		
		addModule("verifier", new Verifier());
		addModule("messenger", new Messenger(this, mMsgOutBox, mMsgInBox));
		addModule("messengerSingle", new MessengerSingle(this));
		addModule("messengerUser", new MessengerUser(this));
		
		addModule("actionOnline", new ActionOnline(this));
		
		new StartEvent(this);
	}
	
	/* getter and setter */

	public Object getModule(String name) {
		if (mModuleList.containsKey(name)) {
			return mModuleList.get(name);
		}
		throw new RuntimeException("module '" + name + "' not found.");
	}
	
	public void addModule(String name, Object obj) {
		mModuleList.put(name, obj);
	}
	
	public int getPort() {
		return mPort;
	}
	
	public void setPort(int port) {
		mPort = port;
	}
	
	/* actions */
	
	public void run() {
		mServerSender = new Sender(this, mMsgOutBox);
		mServerReceiver = new Receiver(this, mPort, mMsgInBox);

		mServerSender.run();
		mServerReceiver.run();
	}
	
	/* stop */
	
	public void stop() {
		mServerSender.stop();
		mServerReceiver.stop();
	}
}
