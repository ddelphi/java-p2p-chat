package lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;



@SuppressWarnings("rawtypes")
public class Inspector {
	public Inspector() {
		// nothing
	}
	
	/* actions for classes */
	
	static public Object newInstance(String className) {
		Object result = null;
		try {
			Class cls = Class.forName(className);
			result = cls.newInstance();
		}
		catch (ClassNotFoundException e) { e.printStackTrace(); }
		catch (InstantiationException e) { e.printStackTrace(); }
		catch (IllegalAccessException e) { e.printStackTrace(); }
		
		return result;
	}
	
	static public Object newInstance(Class cls) {
		Object result = null;
		try {
			result = cls.newInstance();
		}
		catch (InstantiationException e) { e.printStackTrace(); }
		catch (IllegalAccessException e) { e.printStackTrace(); }
		
		return result;
	}

	static public Object newInstance(String className, Object... args) {
		Object instance = null;
		
		try {
			Class<?> cls = Class.forName(className);
			
			int argsSize = args.length / 2;
			Class[] types = new Class[argsSize];
			Object[] values = new Object[argsSize];
			
			for (int i = 0; i < args.length; i++) {
				if (i % 2 == 0) {
					types[i/2] = (Class) args[i];
				} else {
					values[(i - 1) / 2] = args[i];
				}
			}
			
			Constructor constructor = cls.getConstructor(types);
			instance = constructor.newInstance(values);
		}
		catch (ClassNotFoundException e) { e.printStackTrace(); }
		catch (SecurityException e) { e.printStackTrace(); }
		catch (NoSuchMethodException e) { e.printStackTrace(); }
		catch (IllegalArgumentException e) { e.printStackTrace(); }
		catch (InstantiationException e) { e.printStackTrace(); }
		catch (IllegalAccessException e) { e.printStackTrace(); }
		catch (InvocationTargetException e) { e.printStackTrace(); }
		
		return instance;
	}
	
	static public Object newInstance(Class<?> cls, Object... args) {
		Object instance = null;
		
		try {
			int argsSize = args.length / 2;
			Class[] types = new Class[argsSize];
			Object[] values = new Object[argsSize];
			
			for (int i = 0; i < args.length; i++) {
				if (i % 2 == 0) {
					types[i/2] = (Class) args[i];
				} else {
					values[(i - 1) / 2] = args[i];
				}
			}
			
			Constructor constructor = cls.getConstructor(types);
			instance = constructor.newInstance(values);
		}
		catch (SecurityException e) { e.printStackTrace(); }
		catch (NoSuchMethodException e) { e.printStackTrace(); }
		catch (IllegalArgumentException e) { e.printStackTrace(); }
		catch (InstantiationException e) { e.printStackTrace(); }
		catch (IllegalAccessException e) { e.printStackTrace(); }
		catch (InvocationTargetException e) { e.printStackTrace(); }
		
		return instance;
	}
	
	/* actions for methods */
	
	static public Object call(Object obj, String methodName, Object... args) {
		Object result = null;
		
		try {
			Class<? extends Object> cls = obj.getClass();
			int argsSize = args.length / 2;
			
			Class[] types = new Class[argsSize];
			Object[] values = new Object[argsSize];
			
			for (int i = 0; i < args.length; i++) {
				if (i % 2 == 0) {
					types[i/2] = (Class) args[i];
				} else {
					values[(i - 1) / 2] = args[i];
				}
			}

			Method method = cls.getDeclaredMethod(methodName, types);		// getMethod()
			method.setAccessible(true);
			result = method.invoke(obj, values);
		}
		catch (SecurityException e) { e.printStackTrace(); }
		catch (NoSuchMethodException e) { e.printStackTrace(); }
		catch (IllegalArgumentException e) { e.printStackTrace(); }
		catch (IllegalAccessException e) { e.printStackTrace(); }
		catch (InvocationTargetException e) { e.printStackTrace(); }
		
		return result;
	}

	/* actions for fields */
	
	// the value of the field should not be null value,
	// for that you can treat the null result as the field not exists.
	static public Object getField(Object obj, String fieldName) {
		Object result = null;
		
		try {
			Class cls = obj.getClass();
			
			Field field = cls.getDeclaredField(fieldName);
			field.setAccessible(true);
			result = field.get(obj);
		}
		catch (SecurityException e) { e.printStackTrace(); }
		catch (NoSuchFieldException e) { e.printStackTrace(); }
		catch (IllegalAccessException e) { e.printStackTrace(); }
		
		return result;
	}
	
	static public boolean setField(Object obj, String fieldName, Object value) {
		boolean result = false;
		
		try {
			Class cls = obj.getClass();

			Field field = cls.getDeclaredField(fieldName);		// getField()
			field.setAccessible(true);
			field.set(obj, value);
			
			result = true;
		}
		catch (SecurityException e) { e.printStackTrace(); }
		catch (NoSuchFieldException e) { e.printStackTrace(); }
		catch (IllegalAccessException e) { e.printStackTrace(); }
		
		return result;
	}
}