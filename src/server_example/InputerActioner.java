package server_example;

import server.main.Manager;
import lib.Actioner;
import lib.event.MiniEvent;

import all.EE;

import com.alibaba.fastjson.JSONObject;



@SuppressWarnings("unused")
public class InputerActioner extends Actioner {
	
	private Outputer mOutputer;
	private Manager mManager;
	private MiniEvent mServerEvent;

	public InputerActioner(Manager manager) {
		super();
		
		mManager = manager;
		mServerEvent = (MiniEvent) manager.getModule("miniEvent");
	}

	/* getter and setter */
	
	public void setOutputer(Outputer outputer) {
		mOutputer = outputer;
	}
	
	/* actions */
	
	private ActionObject action_exit = new ActionObject() {
		public boolean execute(Object data) {
			JSONObject dict = (JSONObject) data;
			dict.put("return", true);
			return true;
		}
	};

	private ActionObject action_setServer = new ActionObject() {
		public boolean execute(Object data) {
			JSONObject dict = (JSONObject) data;
			String content = dict.getString("content");
			mServerEvent.trigger(EE.server_command_setServer, content);
			return true;
		}
	};

}
