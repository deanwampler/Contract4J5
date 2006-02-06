package org.contract4j5.testexpression.test;

import junit.framework.TestCase;

import org.contract4j5.Instance;
import org.contract4j5.TestContext;
import org.contract4j5.TestContextImpl;
import org.contract4j5.testexpression.DefaultTestExpressionMakerHelper;

public class DefaultTestExpressionMakerHelperTest extends TestCase {
	public static class DefaultTestExpressionMakerStub extends DefaultTestExpressionMakerHelper {
		String expr = "stub";
		public String makeDefaultTestExpression(TestContext context) {
			return expr;
		}	
	}
	
	DefaultTestExpressionMakerStub stub = null;
	TestContext context = null;
	Instance[] args = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		stub = new DefaultTestExpressionMakerStub();
		context = new TestContextImpl();
		args = new Instance[] {
				new Instance ("arg0", String.class,  new String("arg0")), 
				new Instance ("one",  Integer.class, new Integer(1)), 
				new Instance ("twof", Float.class,   new Float(2f)) 
			}; 
		context.setMethodArgs(args);
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.DefaultTestExpressionMakerHelper.makeDefaultTestExpression(TestContext)'
	 */
	public void testMakeDefaultTestExpression() {
		assertEquals ("stub", stub.makeDefaultTestExpression(context));
	}


	/*
	 * Test method for 'org.contract4j5.testexpression.DefaultTestExpressionMakerHelper.makeDefaultTestExpressionIfEmpty(String, TestContext)'
	 */
	public void testMakeDefaultTestExpressionIfEmpty() {
		assertEquals ("foo",  stub.makeDefaultTestExpressionIfEmpty("foo", context));
		assertEquals ("stub", stub.makeDefaultTestExpressionIfEmpty("",    context));
		assertEquals ("stub", stub.makeDefaultTestExpressionIfEmpty(null,  context));
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.DefaultTestExpressionMakerHelper.makeArgsNotNullExpression(TestContext)'
	 */
	public void testMakeArgsNotNullExpression() {
		assertEquals ("$args[0] != null && $args[1] != null && $args[2] != null", 
				stub.makeArgsNotNullExpression(context));

		context.setMethodArgs(args);
		// Doesn't matter that they we used primitives; they are still detected as 
		// "not primitive" in the bowels....
		assertEquals ("$args[0] != null && $args[1] != null && $args[2] != null", 
				stub.makeArgsNotNullExpression(context));
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.DefaultTestExpressionMakerHelper.isNotPrimitive(Class)'
	 */
	public void testIsNotPrimitive() {
		assertTrue  (stub.isNotPrimitive(null));
		assertTrue  (stub.isNotPrimitive(String.class));
		assertFalse (stub.isNotPrimitive(Integer.TYPE));
	}

}
