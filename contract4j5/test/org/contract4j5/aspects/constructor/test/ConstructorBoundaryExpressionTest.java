package org.contract4j5.aspects.constructor.test;

import java.util.ArrayList;

import org.contract4j5.errors.ContractError;

import junit.framework.TestCase;

public class ConstructorBoundaryExpressionTest extends TestCase {
	public void testCanUseClassStaticMethodInPreconditionsToFailTest() {
		ArrayList<String> arrayList = new ArrayList<String>();
		try {
			new ConstructorBoundaries(arrayList);
			fail();
		} catch (ContractError ce) {}
	}
	public void testCanUseClassStaticMethodInPreconditionsToPassTest() {
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("1");
		arrayList.add("2");
		arrayList.add("3");
		new ConstructorBoundaries(arrayList);
	}
}
