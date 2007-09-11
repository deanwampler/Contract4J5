/**
 * 
 */
package org.contract4j5.aspects.constructor.test;

import org.contract4j5.aspects.constructor.test.ConstructorBoundaryGroovyExpressionsWithObjectReferencesTest.Color;

class ColorValidator {
	public boolean called = false;
	public boolean valid(Color color) { called = true; return true; }
}