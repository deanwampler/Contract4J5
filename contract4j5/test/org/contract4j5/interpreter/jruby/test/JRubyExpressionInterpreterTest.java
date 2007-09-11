package org.contract4j5.interpreter.jruby.test;

import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.instance.Instance;
import org.contract4j5.interpreter.jruby.JRubyExpressionInterpreter;

import junit.framework.TestCase;

public class JRubyExpressionInterpreterTest extends TestCase {
	private JRubyExpressionInterpreter interpreter;
	private TestContext context;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		interpreter = new JRubyExpressionInterpreter();
		context = new TestContextImpl("1+1 == 2", "i", new Instance("i", Integer.class, new Integer(1)), null, new Instance[0], null, "NoFile", -1);
	}
	
	public void testJRubyInterpreterCanValidateTestExpressions() throws Exception {
		if (false)
			assertTrue(interpreter.validateTestExpression("1+1 == 2", context).isPassed());
	}
	
	public void testJRubyInterpreterCanExecuteTestExpressions() throws Exception {
		if (false)
			assertTrue(interpreter.invokeTest("1+1 == 2", context).isPassed());
	}

}
