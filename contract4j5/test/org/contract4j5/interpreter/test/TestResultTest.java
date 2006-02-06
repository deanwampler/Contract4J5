package org.contract4j5.interpreter.test;

import junit.framework.TestCase;

import org.contract4j5.ContractError;
import org.contract4j5.interpreter.TestResult;

public class TestResultTest extends TestCase {
	TestResult result = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		result = new TestResult(true, "");
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.isPassed()'
	 */
	public void testIsPassed() {
		assertTrue (result.isPassed());
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.setPassed(boolean)'
	 */
	public void testSetPassed() {
		result.setPassed(false);
		assertFalse (result.isPassed());
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.getMessage()'
	 */
	public void testGetMessage() {
		assertEquals ("", result.getMessage());
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.setMessage(String)'
	 */
	public void testSetMessage() {
		result.setMessage("foo");
		assertEquals ("foo", result.getMessage());
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.getFailureCause()'
	 */
	public void testGetFailureCause() {
		assertNull (result.getFailureCause());
		TestResult r2 = new TestResult (false, "bad", new ContractError());
		assertNotNull (r2.getFailureCause());
		assertEquals  (ContractError.class, r2.getFailureCause().getClass());
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.toString()'
	 */
	public void testToString() {
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.equals(Object)'
	 */
	public void testEqualsObject() {
		TestResult r2 = new TestResult();
		assertEquals (result, r2);
		r2 = new TestResult(false, "");
		assertFalse (result.equals(r2));
		r2 = new TestResult(true, "foo");
		assertFalse (result.equals(r2));
		r2 = new TestResult(false, "foo");
		assertFalse (result.equals(r2));
		assertFalse (result.equals(null));
		assertFalse (result.equals(new String("foo)")));
	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.TestResult()'
	 */
	public void testTestResult() {

	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.TestResult(boolean)'
	 */
	public void testTestResultBoolean() {

	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.TestResult(boolean, String)'
	 */
	public void testTestResultBooleanString() {

	}

	/*
	 * Test method for 'org.contract4j5.interpreter.TestResult.TestResult(boolean, String, Throwable)'
	 */
	public void testTestResultBooleanStringThrowable() {
	}

}
