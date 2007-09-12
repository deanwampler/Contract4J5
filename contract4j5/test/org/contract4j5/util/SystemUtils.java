package org.contract4j5.util;

import org.contract4j5.controller.Contract4J;

public class SystemUtils {
	
	/**
	 * Get the scripting engine name by first looking for the System property that might exist. If not
	 * found, then query Contract4J.  The latter is actually more definitive, but if Contract4J hasn't
	 * been initialized yet, then querying it will implicitly trigger the default initialization process,
	 * which is probably not desirable!
	 */
	public static String getScriptingEngineName() {
		String name = System.getProperty("interpreter");
		if (name != null)
			return name;
		return Contract4J.getInstance().getContractEnforcer().getExpressionInterpreter().getScriptingEngineName();
	}
	
	public static boolean isJexl() {
		return getScriptingEngineName().contains("jexl");
	}

	public static boolean isGroovy() {
		return getScriptingEngineName().contains("groovy");
	}

	public static boolean isJRuby() {
		return getScriptingEngineName().contains("jruby");
	}

}
