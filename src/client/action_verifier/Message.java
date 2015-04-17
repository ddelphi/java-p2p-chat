package client.action_verifier;

import lib.Verifier.VerifierAction;

import com.alibaba.fastjson.JSONObject;


/*
 * verifierAction for message with action="message"
 * 
 * */
public class Message implements VerifierAction {
	private final String HOST_PATTERN = "\\d{1,3}(\\.\\d{1,3}){3}";

	@Override
	public boolean execute(JSONObject dict) {
		if (isNotNull(dict.getJSONObject("sender"))
				&& isNotNull(dict.getJSONObject("receiver"))
				&& hasIp(dict.getJSONObject("sender"))
				&& hasIp(dict.getJSONObject("receiver"))) {
			return true;
		}
		return false;
	}

	private boolean hasIp(JSONObject obj) {
		return obj.getInteger("id") != null
				|| (obj.getString("host").matches(HOST_PATTERN)
						&& obj.getInteger("port") != null);
	}

	private boolean isNotNull(JSONObject obj) {
		return obj != null;
	}
	
	
}
