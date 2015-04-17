package lib;

import java.lang.reflect.Field;


public class Actioner {

	public Dict mActionsMap;
	private String mActionFieldPattern = "action_.+";
	private String mActionSplitPattern = "_";
	
	public Actioner() {
		initParams();
		// initActions();
	}

	private void initParams() {
		mActionsMap = new Dict();
	}
	
	// @Instruction
	//   This method should be called by the sub class.
	// @Description
	//   auto inspect the fields of myself,
	//   then add the ActionObject to the box
	protected void initActions() {
		batchAddActions(this);
	}

	/* actions for actionList */

	public void addAction(String type, ActionObject action) {
		if (!mActionsMap.containsKey(type)) {
			mActionsMap.put(type, new Array());
		}
		mActionsMap.getArray(type).add(action);
	}

	public boolean removeAction(String name) {
		if (!mActionsMap.containsKey(name)) {
			return false;
		}
		mActionsMap.getArray(name).clear();
		return true;
	}
	
	public boolean removeAction(String name, ActionObject action) {
		if (!mActionsMap.containsKey(name)) {
			return false;
		}
		return mActionsMap.getArray(name).remove(action);
	}

	/* actions */
	
	public void batchAddActions(Object obj) {
		Class<? extends Object> cls = obj.getClass();
		Field[] fields = cls.getDeclaredFields();
		
		try {
			String name;
			String actionName;
			String[] actionSplits;
			
			for (Field field : fields) {
				field.setAccessible(true);
				name = field.getName();
				if (name.matches(mActionFieldPattern)) {
					actionSplits = name.split(mActionSplitPattern);
					actionName = actionSplits[1];
					addAction(actionName, (ActionObject) field.get(obj));
				}
			}
		}
		catch (IllegalArgumentException e) { e.printStackTrace(); }
		catch (IllegalAccessException e) { e.printStackTrace(); }

	}
	
	public boolean execute(String name, Object value) {
		boolean flag = false;
		
		if (mActionsMap.containsKey(name)) {
			Array list = mActionsMap.getArray(name);

			try {
				ActionObject obj;
				for (int i = 0; i < list.size(); i++) {
					obj = (ActionObject) list.get(i);
					obj.execute(value);
				}
				flag = true;
			} catch (Exception e) {
				Logger.error("running action '" + name + "' error.");
				e.printStackTrace();
			}
		} else {
			Logger.warn("action '" + name + "' not exists.");
		}
		return flag;
	}

	/* classes */

	static public interface ActionObject {
		public boolean execute(Object data);
	}
}