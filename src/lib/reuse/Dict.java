package lib.reuse;
import java.util.concurrent.ConcurrentHashMap;
import lib.Reuse;


public class Dict extends ConcurrentHashMap<String, Object> {

	private static final long serialVersionUID = 1L;
	public static Reuse<Dict> mReuse = new Reuse<Dict>();

	/* actions */
	
	static public Dict create(Object... args) {
		Dict res = mReuse.size() == 0 ? new Dict() : mReuse.get();
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
	
	// if error, then the dict will be cleared
	public boolean fill(Object... args) {
		String key = null;
		Object val;
	
		try {
			for (int i = 0; i < args.length; i++) {
				if (i % 2 == 0) {
					key = (String) args[i];
				} else {
					val = args[i];
					put(key, val);
				}
			}			
		} catch (Exception ex) {
			this.clear();
			return false;
		}
		return true;
	}

	public Dict getDict(String key) {
		Object val = get(key, Dict.class);
		return val == null ? null : (Dict) val;
	}

	public String getString(String key) {
		Object val = get(key, String.class);
		return val == null ? null : (String) val;
	}

	public int getInt(String key) {
		Object val = get(key, Integer.class);
		return val == null ? null : (Integer) val;
	}

	public long getLong(String key) {
		Object val = get(key, Long.class);
		return val == null ? null : (Long) val;
	}
	
	public float getFloat(String key) {
		Object val = get(key, Float.class);
		return val == null ? null : (Float) val;
	}
	
	public boolean getBoolean(String key) {
		Object val = get(key, Boolean.class);
		return val == null ? null : (Boolean) val;
	}

	public void recycle() {
		this.clear();
		mReuse.put(this);
	}

	/* helpers */

	private Object get(String key, Object type) {
		Object val = get(key);
		if (val != null && val.getClass().equals(type)) {
			return val;
		}
		return null;
	}

}

