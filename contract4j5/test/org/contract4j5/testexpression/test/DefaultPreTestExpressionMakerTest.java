package org.contract4j5.testexpression.test;

import junit.framework.TestCase;

import org.contract4j5.Instance;
import org.contract4j5.TestContext;
import org.contract4j5.TestContextImpl;
import org.contract4j5.testexpression.DefaultPreTestExpressionMaker;

public class DefaultPreTestExpressionMakerTest extends TestCase {
	DefaultPreTestExpressionMaker maker = null;
	TestContext context = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		maker = new DefaultPreTestExpressionMaker();
		context = new TestContextImpl();
		Instance[] args = new Instance[] {
				new Instance ("arg0", String.class,  new String("arg0")), 
				new Instance ("one",  Integer.class, new Integer(1)), 
				new Instance ("twof", Float.class,   new Float(2f)) 
			}; 
		context.setMethodArgs(args);
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.DefaultPreTestExpressionMaker.makeDefaultTestExpression(TestContext)'
	 */
	public void testMakeDefaultTestExpression() {
		assertEquals ("$args[0] != null && $args[1] != null && $args[2] != null", 
				maker.makeDefaultTestExpression(context));
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.DefaultPreTestExpressionMaker.makeDefaultTestExpressionIfEmpty(String, TestContext)'
	 */
	public void testMakeDefaultTestExpressionIfEmpty() {
		assertEquals ("foo", maker.makeDefaultTestExpressionIfEmpty("foo", context));
	}

}
