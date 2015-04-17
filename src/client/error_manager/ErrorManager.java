package client.error_manager;

import java.util.Date;

import lib.Json;
import lib.event.EventSystem;
import lib.event.EventSystem.Executable;
import all.EE;
import client.main.Manager;
import client.messenger.Messenger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/*
 * The error manager object
 * 
 * */
public class ErrorManager {

	private int mRetryTimes = 3;
	private EventSystem mEventSystem;
	private Manager mManager;
	private Messenger mMessenger;
	private JSONObject mCollection;
	private JSONArray mCollectBox;
	private JSONArray mRecordBox;
	
	public ErrorManager(Manager manager) {
		initParams(manager);
		initEvents();
	}
	
	private void initParams(Manager manager) {
		mManager = manager;
		mEventSystem = EventSystem.getSingleInstance();
		mMessenger = (Messenger) mManager.getModule("messenger");

		mCollection = new JSONObject();
		mCollection.put("collect", new JSONArray());
		mCollection.put("record", new JSONArray());
		mCollectBox = mCollection.getJSONArray("collect");
		mRecordBox = mCollection.getJSONArray("record");
	}
	
	private void initEvents() {
		mEventSystem.register(EE.error_collect, onCollectingError);
		mEventSystem.register(EE.error_record, onRecordingError);
	}
	
	/* getter and setter */
	
	public void setRetryTimes(int times) {
		mRetryTimes = times;
	}

	/* events */

	private Executable onCollectingError = new Executable() {
		public void execute(Object data) {
			JSONObject msg = (JSONObject) data;
			collect(msg);
		}
	};

	private Executable onRecordingError = new Executable() {
		public void execute(Object data) {
			record(data);
		}
	};


	/* actions */

	public void collect(JSONObject msg) {
		int count = getErrorCount(msg);
		if (canReSend(count)) {
			reSendMessage(msg);
		} else {
			endRound(msg);
		}	
	}

	public void record(Object data) {
		mRecordBox.add(buildErrorObject(data));
	}


	/* helpers */

	private int getErrorCount(JSONObject msg) {
		JSONObject errorJSONObject = msg.getJSONObject("error");
		if (errorJSONObject == null) {
			errorJSONObject = Json.create(
				"count", 0
			);
			msg.put("error", errorJSONObject);
		}
		
		int count = errorJSONObject.getIntValue("count");
		errorJSONObject.put("count", ++count);
		return count;
	}

	public boolean canReSend(int count) {
		return count <= mRetryTimes;
	}
	
	private void reSendMessage(JSONObject msg) {
		// mEventSystem.trigger(EE.error_reSendMessage:before, msg);
		mMessenger.addMessage(msg);
		// mEventSystem.trigger(EE.error_reSendMessage:after, msg);
	}

	public void endRound(JSONObject msg) {
		mCollectBox.add(buildErrorObject(msg));
	}

	public JSONObject buildErrorObject(Object obj) {
		return Json.create(
			"addTime", new Date().getTime(),
			"content", obj
		);
	}
}
