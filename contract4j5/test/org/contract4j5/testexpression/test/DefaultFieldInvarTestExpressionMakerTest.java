package org.contract4j5.testexpression.test;

import junit.framework.TestCase;

import org.contract4j5.Instance;
import org.contract4j5.TestContext;
import org.contract4j5.TestContextImpl;
import org.contract4j5.testexpression.DefaultFieldInvarTestExpressionMaker;

public class DefaultFieldInvarTestExpressionMakerTest extends TestCase {
	DefaultFieldInvarTestExpressionMaker maker = null;
	TestContext context = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		maker = new DefaultFieldInvarTestExpressionMaker();
		context = new TestContextImpl();
	}

	/*
	 * Test method for 'org.contract4j5.testExpression.DefaultFieldInvarTestExpressionMaker.makeDefaultTestExpression(TestContext)'
	 */
	public void testMakeDefaultTestExpression() {
		assertEquals ("$target != null", maker.makeDefaultTestExpression(context));
		Instance i = new Instance("1", Integer.TYPE, 1);
		context.setField(i);
		// Treats it like the primitive that it is!
		assertEquals ("", maker.makeDefaultTestExpression(context));
		i = new Instance("foo", String.class, new String ("foo"));
		context.setField(i);
		assertEquals ("$target != null", maker.makeDefaultTestExpression(context));
	}

	/*
	 * Test method for 'org.contract4j5.testExpression.DefaultFieldInvarTestExpressionMaker.makeDefaultTestExpressionIfEmpty(String, TestContext)'
	 */
	public void testMakeDefaultTestExpressionIfEmpty() {
		assertEquals ("foo", maker.makeDefaultTestExpressionIfEmpty("foo", context));
		Instance i = new Instance("1", Integer.TYPE, 1);
		context.setField(i);
		assertEquals ("foo", maker.makeDefaultTestExpressionIfEmpty("foo", context));
		i = new Instance("foo", String.class, new String ("foo"));
		context.setField(i);
		assertEquals ("foo", maker.makeDefaultTestExpressionIfEmpty("foo", context));
	}

}
