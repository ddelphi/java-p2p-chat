package lib;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Array extends ArrayList<Object> {

	private static final long serialVersionUID = 1L;

	/* actions */
	
	static public Array create(Object... args) {
		Array res = new Array();
	
		try {
			for (int i = 0; i < args.length; i++) {
				res.add(args[i]);
			}
		} catch (Exception ex) {
			return null;
		}
		return res;
	}
	
	// if error, then the dict will be cleared
	public boolean fill(Object... args) {
		try {
			for (int i = 0; i < args.length; i++) {
				this.add(args[i]);
			}
		} catch (Exception ex) {
			return false;
		}
		return true;
	}
	
	/* actions get */
	
	public Dict getDict(int key) {
		Object val = get(key, Dict.class);
		return val == null ? null : (Dict) val;
	}
	
	public Array getArray(int key) {
		Object val = get(key, Array.class);
		return val == null ? null : (Array) val;
	}

	public String getString(int key) {
		Object val = get(key, String.class);
		return val == null ? null : (String) val;
	}

	public int getInt(int key) {
		Object val = get(key, Integer.class);
		return val == null ? null : (Integer) val;
	}
	
	public BigDecimal getBigDecimal(int key) {
		Object val = get(key, BigDecimal.class);
		return val == null ? null : (BigDecimal) val;
	}

	public long getLong(int key) {
		Object val = get(key, Long.class);
		return val == null ? null : (Long) val;
	}
	
	public float getFloat(int key) {
		Object val = get(key, Float.class);
		return val == null ? null : (Float) val;
	}
	
	public boolean getBoolean(int key) {
		Object val = get(key, Boolean.class);
		return val == null ? null : (Boolean) val;
	}

	/* helpers */

	private Object get(int key, Object type) {
		Object val = get(key);
		if (val != null && val.getClass().equals(type)) {
			return val;
		}
		return null;
	}

}
