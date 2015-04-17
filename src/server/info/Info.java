package server.info;


import com.alibaba.fastjson.JSONObject;


/*
 * server info object
 * 
 * */
public class Info {

	private UserList mUserList;
	private JSONObject mServerInfo;
	
	public Info() {
		initParams();
	}

	public void initParams() {
		mUserList = new UserList();
		mServerInfo = new JSONObject();
	}

	/* getter and setter */

	public UserList getUserList() {
		return mUserList;
	}
	
	public JSONObject getServerInfo() {
		return mServerInfo;
	}
	
	/* actions */
	
	public void setServerInfo(JSONObject dict) {
		mServerInfo.putAll(dict);
	}
	
	
}