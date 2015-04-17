package lib;
import java.math.BigDecimal;
import java.util.HashMap;



public class Dict extends HashMap<String, Object> {
	
	private static final long serialVersionUID = 1L;

	/* actions */
	
	static public Dict create(Object... args) {
		Dict res = new Dict();
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
			// throw new Exception("Wrong dict arguments.");
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

	public BigDecimal getBigDecimal(String key) {
		Object val = get(key, BigDecimal.class);
		return val == null ? null : (BigDecimal) val;
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
	
	public Array getArray(String key) {
		Object val = get(key, Array.class);
		return val == null ? null : (Array) val;
	}
		
	/**/
	
	public Dict getDict(String key, Dict def) {
		Object val = get(key, Dict.class);
		return val == null ? def : (Dict) val;
	}

	public String getString(String key, String def) {
		Object val = get(key, String.class);
		return val == null ? def : (String) val;
	}

	public int getInt(String key, int def) {
		Object val = get(key, Integer.class);
		return val == null ? def : (Integer) val;
	}

	public long getLong(String key, long def) {
		Object val = get(key, Long.class);
		return val == null ? def : (Long) val;
	}
	
	public float getFloat(String key, float def) {
		Object val = get(key, Float.class);
		return val == null ? def : (Float) val;
	}
	
	public boolean getBoolean(String key, boolean def) {
		Object val = get(key, Boolean.class);
		return val == null ? def : (Boolean) val;
	}

	public Array getArray(String key, Array def) {
		Object val = get(key, Array.class);
		return val == null ? def : (Array) val;
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

