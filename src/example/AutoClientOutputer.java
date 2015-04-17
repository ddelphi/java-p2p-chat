package example;

import lib.event.MiniEvent;
import lib.event.MiniEvent.Executable;
import all.EE;
import client.main.Manager;

import com.alibaba.fastjson.JSONObject;

class AutoClientOutputer extends Thread {
	private Manager mManager;
	private MiniEvent mEvent;
	private int mPort;

	public AutoClientOutputer(Manager manager) {
		initParams(manager);
		initEvents();
	}
	
	private void initParams(Manager manager) {
		mManager = manager;
		mEvent = (MiniEvent) mManager.getModule("miniEvent");
		mPort = mManager.getPort();
	}

	public void initEvents() {
		mEvent.register(EE.messager_getMessage, onGetMessage);
	}
	
	/* events */
	
	private Executable onGetMessage = new Executable() {
		@Override
		public void execute(Object data) {
			JSONObject dict = (JSONObject) data;
			if (!dict.getString("action").equals("message")) { return; }
			
			String content = dict.getJSONObject("content").getString("message");
			System.out.println(mPort + " : " + content);
		}
		
	};
}
