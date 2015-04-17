package lib.event;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;



/*
 * The synchronized event system
 * 
 * */
public class EventSystem {
	static EventSystem instance;
	HashMap<String, ArrayList<Executable>> collection;
	private static final String TAG = "[EventSystem]";
	
	public EventSystem() {
		collection = new HashMap<String, ArrayList<Executable>>();
	}
	
	public synchronized static EventSystem newSingleInstance() {
		instance = null;
		return getSingleInstance();
	}

	public synchronized static EventSystem getSingleInstance() {
		if (instance == null) {
			instance = new EventSystem();
		}
		return instance;
	}

	/* actions */

	public synchronized void trigger(String name, Object data) {
		ArrayList<Executable> list = collection.get(name);
		// System.out.println(TAG + "trigger: " + name);
		if (list == null) {
			// System.out.println(TAG + "event list not exists: " + name);
			return;
		}
		Executable obj;
		for (Iterator<Executable> iter = list.iterator(); iter.hasNext(); ) {
			try {
				obj = iter.next();
				
				removeExecutableOnce(obj, iter);
				obj.execute(data);
			} catch (Exception ex) { ex.printStackTrace(); }
		}
	}
	
	public synchronized void register(String name, Executable executable) {
		ArrayList<Executable> list;
		
		if (!collection.containsKey(name)) {
			list = new ArrayList<Executable>();
			collection.put(name, list);
		} else {
			list = collection.get(name);
		}
		list.add(executable);
	}
	
	private void removeExecutableOnce(Object obj, Iterator<Executable> iter) {
		if (obj.toString().equals(ExecutableOnce.class.getSimpleName())) {
			iter.remove();
		}
	}
	
	public synchronized boolean remove(String name) {
		if (collection.containsKey(name)) {
			collection.get(name).clear();
			return true;
		}
		return false;
	}
	
	// TODO: to refine
	public synchronized boolean remove(String name,String id) {
		return false;
	}
	
	public synchronized void clear() {
		Set<String> keySet = collection.keySet();
		for (Iterator<String> iter = keySet.iterator(); iter.hasNext(); ) {
			collection.get(iter.next()).clear();
		}
		collection.clear();
	}
	
	public synchronized void destory() {
		instance = null;
		clear();
	}
	
	/* interfaces */
	
	public static interface Executable {
		public void execute(Object data);
	}
	public static class ExecutableOnce {
		public void execute(Object data) {};
		public String toString() {
			return "ExecutableOnce";
		}
	}
}




