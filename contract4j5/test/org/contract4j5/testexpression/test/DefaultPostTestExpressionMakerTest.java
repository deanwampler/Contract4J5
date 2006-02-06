package org.contract4j5.testexpression.test;

import junit.framework.TestCase;

import org.contract4j5.Instance;
import org.contract4j5.TestContext;
import org.contract4j5.TestContextImpl;
import org.contract4j5.testexpression.DefaultPostTestExpressionMaker;

public class DefaultPostTestExpressionMakerTest extends TestCase {
	DefaultPostTestExpressionMaker maker = null;
	TestContext context = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		maker = new DefaultPostTestExpressionMaker();
		context = new TestContextImpl();
	}

	/*
	 * Test method for 'org.contract4j5.testExpression.DefaultPostTestExpressionMaker.makeDefaultTestExpression(TestContext)'
	 */
	public void testMakeDefaultTestExpression() {
		assertEquals ("$return != null", maker.makeDefaultTestExpression(context));
		context.setMethodResult(new Instance("1", Integer.class, 1));
		// Still thinks it's not a primitive!
		assertEquals ("$return != null", maker.makeDefaultTestExpression(context));
		context.setMethodResult(new Instance("foo", String.class, new String ("foo")));
		assertEquals ("$return != null", maker.makeDefaultTestExpression(context));
	}

	/*
	 * Test method for 'org.contract4j5.testExpression.DefaultPostTestExpressionMaker.makeDefaultTestExpressionIfEmpty(String, TestContext)'
	 */
	public void testMakeDefaultTestExpressionIfEmpty() {
		assertEquals ("foo", maker.makeDefaultTestExpressionIfEmpty("foo", context));
		context.setMethodResult(new Instance("1", Integer.class, 1));
		assertEquals ("foo", maker.makeDefaultTestExpressionIfEmpty("foo", context));
		context.setMethodResult(new Instance("foo", String.class, new String ("foo")));
		assertEquals ("foo", maker.makeDefaultTestExpressionIfEmpty("foo", context));
	}

}
