/**
 * 
 */
package org.contract4j5.aspects.constructor.test;

public class Validator {
	public static boolean called = false;
	public static void setCalled(boolean b) { called = b; }
	public static boolean valid(String s) { called = true; return true;}
}