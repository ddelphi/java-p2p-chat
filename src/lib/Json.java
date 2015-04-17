package lib;
import com.alibaba.fastjson.JSONObject;



public class Json {

	/* actions */
	
	static public JSONObject create(Object... args) {
		JSONObject res = new JSONObject();
		String key = null;
		Object val;

		try {
			for (int i = 0; i < args.length; i++) {
				if (i % 2 == 0) {
					key = (String) args[i];
				} else {
					val = args[i];
					res.put(key, val);
				}
			}
		} catch (Exception ex) {
			return null;
		}
		return res;
	}
	
	// if error, then the JSONObject will be cleared
	static public boolean fill(JSONObject target, Object... args) {
		String key = null;
		Object val;
	
		try {
			for (int i = 0; i < args.length; i++) {
				if (i % 2 == 0) {
					key = (String) args[i];
				} else {
					val = args[i];
					target.put(key, val);
				}
			}			
		} catch (Exception ex) {
			target.clear();
			return false;
		}
		return true;
	}

}

