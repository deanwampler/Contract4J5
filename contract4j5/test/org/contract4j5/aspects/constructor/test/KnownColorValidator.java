/**
 * 
 */
package org.contract4j5.aspects.constructor.test;

class KnownColorValidator {
	public boolean called = false;
	public boolean valid(KnownColor color) { called = true; return true; }
}