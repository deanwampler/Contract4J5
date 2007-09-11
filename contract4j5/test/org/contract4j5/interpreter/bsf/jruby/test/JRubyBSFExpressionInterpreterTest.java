package org.contract4j5.interpreter.bsf.jruby.test;

import org.apache.bsf.BSFException;
import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.instance.Instance;
import org.contract4j5.interpreter.bsf.jruby.JRubyBSFExpressionInterpreter;

import junit.framework.TestCase;

public class JRubyBSFExpressionInterpreterTest extends TestCase {
	private JRubyBSFExpressionInterpreter interpreter;
	private TestContext context;
	private static final String testExpression = "\"abc\".equals(\"abcd\")";
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		interpreter = new JRubyBSFExpressionInterpreter();
		context = new TestContextImpl(testExpression, "abc", new Instance("abc", String.class, "abc"), null, new Instance[0], null, "NoFile", -1);
	}
	
	public void testJRubyInterpreterCanValidateTestExpressions() throws BSFException {
		assertTrue(interpreter.validateTestExpression(testExpression, context).isPassed());
	}
	
	public void testJRubyInterpreterCanExecuteTestExpressions() throws BSFException {
		assertFalse(interpreter.invokeTest(testExpression, context).isPassed());
	}
}
