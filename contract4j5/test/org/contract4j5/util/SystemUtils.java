package org.contract4j5.util;

import org.contract4j5.controller.Contract4J;

public class SystemUtils {
	
	public static String getScriptingEngineName() {
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
