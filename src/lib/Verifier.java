package lib;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class Verifier {

	public static final String DEFAULT_ACTION = ":default";
	private final String ACTION_NAME = "action";
	
	// private Manager mManager;
	private JSONObject mActionsMap;
	private JSONArray mDefaultList;


	public Verifier() {
		initParams();
		initDefault();
	}

	public void initParams() {
		mActionsMap = new JSONObject();
	}
	
	private void initDefault() {
		mActionsMap.put(DEFAULT_ACTION, new JSONArray());
		mDefaultList = mActionsMap.getJSONArray(DEFAULT_ACTION);
	}

	/* actions for actionList */

	public void addAction(String type, VerifierAction action) {
		if (!mActionsMap.containsKey(type)) {
			mActionsMap.put(type, new JSONArray());
		}
		mActionsMap.getJSONArray(type).add(action);
	}

	/* actions */

	// @return
	// true, when list not exists, or actions of list all returned true
	// false, when list exists, and with one action in list returned false
	public boolean verify(JSONObject dict) {
		String type = getType(dict);
		JSONArray actionList = mActionsMap.getJSONArray(type);
		boolean result = true;
		boolean resultDef = true;
		if (actionList != null) {
			result = verifyAction(actionList, dict);
		}
		resultDef = verifyAction(mDefaultList, dict);
		
		return result && resultDef;
	}
	
	/* helpers */
	
	private boolean verifyAction(JSONArray actionList, JSONObject dict) {
		VerifierAction action;
		boolean result = true;
		for (int i = 0; i < actionList.size(); i++) {
			action = (VerifierAction) actionList.get(i);
			result = action.execute(dict);
			if (result == false) {
				break;
			}
		}
		return result;
	}
	
	public String getType(JSONObject dict) {
		return dict.getString(ACTION_NAME);
	}

	/* classes */

	static public interface VerifierAction {
		public boolean execute(JSONObject dict);
	}
}