package client.main;

import lib.Actioner;
import lib.event.MiniEvent;
import all.EE;

import com.alibaba.fastjson.JSONObject;


/*
 * This object is for executing the command text from user input
 * 
 * */
@SuppressWarnings("unused")
public class InputerActioner extends Actioner {
	
	private Manager mManager;
	private MiniEvent mClientEvent;

	public InputerActioner(Manager manager) {
		mManager = manager;
		mClientEvent = (MiniEvent) manager.getModule("miniEvent");

		this.initActions();
	}
	
	/* actions */

	private ActionObject action_run = new ActionObject() {
		public boolean execute(Object data) {
			JSONObject dict = (JSONObject) data;
			Integer port = dict.getInteger("content");
			if (port == null) {
				port = mManager.getPort();
			}
			mClientEvent.trigger(EE.client_run, port);
			return true;
		}
	};
	private ActionObject action_exit = new ActionObject() {
		public boolean execute(Object data) {
			JSONObject dict = (JSONObject) data;
			dict.put("return", true);
			mClientEvent.trigger(EE.client_exit, null);
			return true;
		}
	};

	private ActionObject action_setServer = new ActionObject() {
		public boolean execute(Object data) {
			JSONObject dict = (JSONObject) data;
			String content = dict.getString("content");
			mClientEvent.trigger(EE.client_command_setServer, content);
			return true;
		}
	};

	private ActionObject action_setUser = new ActionObject() {
		public boolean execute(Object data) {
			JSONObject dict = (JSONObject) data;
			String content = dict.getString("content");
			mClientEvent.trigger(EE.client_command_setUser, content);
			return true;
		}
	};

	private ActionObject action_getListUser = new ActionObject() {
		public boolean execute(Object data) {
			mClientEvent.trigger(EE.client_command_getListUser, null);
			return true;
		}
	};
}
