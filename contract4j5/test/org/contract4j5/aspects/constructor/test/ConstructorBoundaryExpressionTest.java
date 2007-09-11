package org.contract4j5.aspects.constructor.test;

import java.util.ArrayList;

import org.contract4j5.errors.ContractError;
import org.contract4j5.util.SystemUtils;

import junit.framework.TestCase;

/**
 * You can reference a class' own static method if you prefix with $this. However,
 * this doesn't appear to work with JRuby.
 * @author deanwampler
 *
 */
public class ConstructorBoundaryExpressionTest extends TestCase {
	public void testCanUseDollarThisDotClassStaticMethodInPreconditionsToFailTest() {
		if (SystemUtils.isJRuby())
			return;
		ArrayList<String> arrayList = new ArrayList<String>();
		try {
			new ConstructorBoundaries(arrayList);
			fail();
		} catch (ContractError ce) {}
	}
	public void testCanUseDollarThisDotClassStaticMethodInPreconditionsToPassTest() {
		if (SystemUtils.isJRuby())
			return;
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("1");
		arrayList.add("2");
		arrayList.add("3");
		new ConstructorBoundaries(arrayList);
	}
}
