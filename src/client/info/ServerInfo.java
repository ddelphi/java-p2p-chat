package client.info;

import com.alibaba.fastjson.JSONObject;


/*
 * server info
 * 
 * */
public class ServerInfo {	
	JSONObject mInfo;

	public ServerInfo() {
		initParams();
	}
	
	public void initParams() {
		mInfo = new JSONObject();
	}
	
	/* getter and setter */
	
	public void put(String key, Object val) {
		mInfo.put(key, val);
	}
	
	public Object get(String key) {
		return mInfo.get(key);
	}
	
	public JSONObject getInfo() {
		return mInfo;
	}
}
