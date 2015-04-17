package lib;


/*
	@version 0.2
	
	@Description
	  @2015-04-08
	  change the at (assertEquals) code,
	  make it testable for null value
*/
public class TestTool {
	
	private static int totalCount = 0;
	private static int rightCount = 0;
	private static int errorCount = 0;
	
	/* log actions */
	
	public static void log(Object... args) {
		String prefix = "    ";
		print(prefix, args);
	}
	
	public static void error(Object... args) {
		String prefix = "[X] ";
		print(prefix, args);
	}
	
	public static void warn(Object... args) {
		String prefix = "[!] ";
		print(prefix, args);
	}
	
	/* assert actions */
	
	public static void at(String msg, Object one, Object two){
		log("-----> " + msg);
		at(one, two);
	}
	
	public static void at(Object one, Object two) {
		// make two element being testable
		if (one == null) {
			one = "null";
		}
		if (two == null) {
			two = "null";
		}
		
		String o = one.toString();
		String t = two.toString();
		if (o.equals(t)) {
			assertLog("[TRUE] ", o + " || " + t);
			rightCount += 1;
		} else {
			assertLog("[FALSE] ", o + " || " + t);
			errorCount += 1;
		}
		totalCount += 1;
	}
	
	/**/
	
	public static void printResult() {
		String template = "\n"
				+ "******************************\n"
				+ "       the result\n"
				+ "******************************\n"
				+ "error:%s  right:%s  total:%s";
		log(String.format(template, errorCount, rightCount, totalCount));
	}

	/* private actions */
	
	private static void assertLog(String prefix, Object... args) {
		print(prefix, args);
	}
	
	private static void print(Object prefix, Object[] args) {
		String res = String.valueOf(prefix);
		
		for (Object arg : args) {
			res += " " + arg;
		}
		System.out.println(res);
	}
	
}

