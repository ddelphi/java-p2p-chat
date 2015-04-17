package lib.event;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


/*
 * The event system
 * 
 * */
public class MiniEvent {
	static MiniEvent instance;
	HashMap<String, ArrayList<Executable>> collection;
	private static final String TAG = "[MiniEvent]";
	
	public MiniEvent() {
		collection = new HashMap<String, ArrayList<Executable>>();
	}
	
	public synchronized static MiniEvent newSingleInstance() {
		instance = null;
		return getSingleInstance();
	}

	public synchronized static MiniEvent getSingleInstance() {
		if (instance == null) {
			instance = new MiniEvent();
		}
		return instance;
	}

	/* actions */
	
	public void trigger(String name, Object data) {
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
	

		// System.out.println(TAG + "trigger done: " + name);
	}
	
	public void register(String name, Executable executable) {
		ArrayList<Executable> list;
		if (!collection.containsKey(name)) {
			list = new ArrayList<Executable>();
			collection.put(name, list);
		} else {
			list = collection.get(name);
		}
		list.add(executable);
	}
	
	private void removeExecutableOnce(Executable obj, Iterator<Executable> iter) {
		if (obj.toString().equals(ExecutableOnce.class.getSimpleName())) {
			iter.remove();
		}
	}
	
	public boolean remove(String name) {
		if (collection.containsKey(name)) {
			collection.get(name).clear();
			return true;
		}
		return false;
	}
	
	// TODO: to refine
	public boolean remove(String name,String id) {
		return false;
	}
	
	public void clear() {
		Set<String> keySet = collection.keySet();
		for (Iterator<String> iter = keySet.iterator(); iter.hasNext(); ) {
			collection.get(iter.next()).clear();
		}
		collection.clear();
	}
	
	public void destory() {
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




