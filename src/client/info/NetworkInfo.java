package client.info;

import com.alibaba.fastjson.JSONObject;


/*
 * network info object
 * 
 * */
public class NetworkInfo {
	
	JSONObject mInfo;
	
	public NetworkInfo() {
		initParams();
	}
	
	public void initParams() {
		mInfo = new JSONObject();
	}
		
	/* getter and setter */
	
	public void set(String key, Object val) {
		mInfo.put(key, val);
	}
	
	public Object get(String key) {
		return mInfo.get(key);
	}
	
	public JSONObject getInfo() {
		return mInfo;
	}
}
