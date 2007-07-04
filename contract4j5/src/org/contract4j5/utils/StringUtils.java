package org.contract4j5.utils;

public class StringUtils {
	public static String newline() {
		String newline = System.getProperty("line.separator");
		if (newline == null)
			newline = "\n";
		return newline;
	}
	
	public static boolean empty (String s) {
		return (s == null || s.length() == 0);
	}

}
