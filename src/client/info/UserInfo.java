package client.info;

import java.util.ArrayList;

import lib.Json;
import lib.event.MiniEvent;
import all.EE;
import client.main.Manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/*
 * object that contains user info
 *
 * */
public class UserInfo {

	private ArrayList<JSONObject> mUserList;
	private JSONObject mUserInfo;
	private Manager mManager;
	private MiniEvent mClientEvent;

	public UserInfo(Manager manager) {
		initParams(manager);
	}

	private void initParams(Manager manager) {
		mManager = manager;
		mClientEvent = (MiniEvent) mManager.getModule("miniEvent");
		
		mUserList = new ArrayList<JSONObject>();
		mUserInfo = new JSONObject();
	}

	/* getter and setter */

	public ArrayList<JSONObject> getUserList() {
		return mUserList;
	}

	public JSONObject getUserInfo() {
		return mUserInfo;
	}

	/* actions for self info */

	public void removeSelfUser() {
		mUserInfo.clear();
	}

	public void updateSelfUser(JSONObject dict) {
		Json.fill(mUserInfo,
			"id", dict.getIntValue("id"),
			"name", dict.getString("name"),
			"host", dict.getString("host"),
			"port", dict.getIntValue("port"),
			"addTime", dict.getBigInteger("addTime"),
			"lastTime", dict.getBigInteger("lastTime")
		);
		mClientEvent.trigger(EE.userInfo_done, mUserInfo);
	}

	/* actions for user list */
	
	public void addUser(JSONObject user) {
		mUserList.add(user);
	}

	public JSONObject getUser(int num) {
		if (num > -1 && num < mUserList.size()) {
			return mUserList.get(num);
		}
		mClientEvent.trigger(EE.client_command_getListUser, null);
		return null;
	}

	public boolean removeUser(int id) {
		if (id > -1 && id < mUserList.size()) {
			mUserList.set(id, null);
			return true;
		}
		return false;
	}
	
	public void updateUserList(JSONArray jarr) {
		for (int i = 0; i < jarr.size(); i++) {
			addUser(jarr.getJSONObject(i));
		}
	}
}

