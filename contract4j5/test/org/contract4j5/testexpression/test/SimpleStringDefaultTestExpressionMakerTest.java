package org.contract4j5.testexpression.test;

import junit.framework.TestCase;

import org.contract4j5.TestContext;
import org.contract4j5.TestContextImpl;
import org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker;

public class SimpleStringDefaultTestExpressionMakerTest extends TestCase {
	public static class Foo {
		private String name;
		public String getName() { return name; }
		public void   setName(String n) { name = n; }
		
		public String doIt(int i) {
			return i == 0 ? null : Integer.toString(i);
		}
		public Foo (String name) { setName(name); }
	}
	
	SimpleStringDefaultTestExpressionMaker maker = null;
	TestContext context = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		maker = new SimpleStringDefaultTestExpressionMaker("default");
		context = new TestContextImpl();
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker.makeDefaultTestExpression(TestContext)'
	 */
	public void testMakeDefaultTestExpression() {
		assertEquals ("default", maker.makeDefaultTestExpression(context));
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker.getExpression()'
	 */
	public void testGetExpression() {
		assertEquals ("default", maker.getExpression());
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker.setExpression(String)'
	 */
	public void testSetExpression() {
		maker.setExpression("foo");
		assertEquals ("foo", maker.getExpression());
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker.SimpleStringDefaultTestExpressionMaker()'
	 */
	public void testSimpleStringDefaultTestExpressionMaker() {
		maker = new SimpleStringDefaultTestExpressionMaker();
		assertEquals ("", maker.getExpression());
	}

	/*
	 * Test method for 'org.contract4j5.testexpression.SimpleStringDefaultTestExpressionMaker.SimpleStringDefaultTestExpressionMaker(String)'
	 */
	public void testSimpleStringDefaultTestExpressionMakerString() {
		assertEquals ("default", maker.getExpression());
	}
}
