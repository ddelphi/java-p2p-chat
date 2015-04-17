package lib;
import com.alibaba.fastjson.JSONArray;



public class JsonArray {

	/* actions */
	
	static public JSONArray create(Object... args) {
		JSONArray res = new JSONArray();

		for (int i = 0; i < args.length; i++) {
			res.add(args[i]);
		}

		return res;
	}
	
	// if error, then the JSONObject will be cleared
	static public boolean fill(JSONArray target, Object... args) {
		try {
			for (int i = 0; i < args.length; i++) {
				target.add(args[i]);
			}			
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

}

