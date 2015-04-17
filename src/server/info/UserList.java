package server.info;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



import com.alibaba.fastjson.JSONObject;



/*
 * @description
 *   using a List to simulate a db,
 *   and then this object is the DAO object 
 * 
 * */
public class UserList {

	private ArrayList<JSONObject> mUserDb;
	
	public UserList() {
		initParams();
	}

	public void initParams() {
		mUserDb = new ArrayList<JSONObject>();
	}

	/* getter and setter */

	public ArrayList<JSONObject> getOriginList() {
		return mUserDb;
	}

	/* actions for list */

	public JSONObject get(int num) {
		return mUserDb.get(num);
	}
	
	public void set(int i, JSONObject dict) {
		mUserDb.set(i, dict);
	}

	public int size() {
		return mUserDb.size();
	}

	/* actions */

	public JSONObject add(JSONObject user) {

		mUserDb.add(user);
		return user;
	}
	
	public boolean remove(int id) {
		if (isRightId(id)
			&& (mUserDb.get(id) != null)) {
			mUserDb.set(id, null);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean updateLastTime(int id) {
		if (!isRightId(id)) { return false; }
		
		JSONObject user = (JSONObject) mUserDb.get(id);

		user.put("lastTime", new Date().getTime());
		return true;
	}
	
	public List<JSONObject> getListUser(int id) {
		if (isRightId(id)) {
			return mUserDb.subList(id, mUserDb.size());
		}
		return null;
	}
	
	/* helpers */
	
	private boolean isRightId(int id) {
		return id > -1 && id < mUserDb.size();
	}

}