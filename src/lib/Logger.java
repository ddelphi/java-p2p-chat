package lib;


public class Logger {
	private static boolean canDisplayLog = true;
	private static boolean canDisplayInfo = true;
	private static boolean canDisplayWarn = true;
	private static boolean canDisplayError = true;
	private static boolean canDisplayBit = true;
	private static String mInfoPrefix = "    ";
	private static String mLogPrefix = "    ";
	private static String mWarnPrefix = "[!] ";
	private static String mErrorPrefix = "[X] ";
	private static String mBitPrefix = "----";
	
	/* getter and setter */
	
	public static void setLogState(boolean flag) { canDisplayLog = flag; }
	public static void setInfoState(boolean flag) { canDisplayLog = flag; }
	public static void setWarnState(boolean flag) { canDisplayWarn = flag; }
	public static void setErrorState(boolean flag) { canDisplayError = flag; }
	public static void setBitState(boolean flag) { canDisplayBit = flag; }
	public static void setBitPrefix(String prefix) { mBitPrefix = prefix; }
	
	/* log actions */
	
	public static void log(Object... args) {
		if (!canDisplayLog) { return; }
		print(mLogPrefix, args);
	}
	
	public static void error(Object... args) {
		if (!canDisplayError) { return; }
		print(mErrorPrefix, args);
	}
	
	public static void warn(Object... args) {
		if (!canDisplayWarn) { return; }
		print(mWarnPrefix, args);
	}
	
	/*
		@Description
			bit method is for placing crumbs in the source code temporally,
			to let you clarify the code path.
			Then you can use find action to delete them before publishing.  
	*/
	public static void bit(Object... args) {
		if (!canDisplayBit) { return; }
		print(mBitPrefix, args);	
	}
	
	public static void info(Object... args) {
		if (!canDisplayInfo) { return; }
		print(mInfoPrefix, args);	
	}
	
	/* private actions */
	
	private static void print(Object prefix, Object[] args) {
		String res = String.valueOf(prefix);
		
		for (Object arg : args) {
			res += " " + arg;
		}
		System.out.println(res);
	}
	
}

