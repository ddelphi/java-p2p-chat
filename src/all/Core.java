package all;

import java.util.HashMap;
import lib.event.EventSystem;


public class Core {
	private static HashMap<String, Object> mServiceList;
	
	public void init() {
		initServices();
	}

	public void initServices() {
		addService("eventSystem", EventSystem.getSingleInstance());
	}

	/* actions */

	static public Object getService(String name) {
		return mServiceList.get(name);
	}

	static public void addService(String name, Object obj) {
		mServiceList.put(name, obj);
	}
}