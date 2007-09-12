package org.contract4j5.interpreter.bsf.jruby.test;

import org.apache.bsf.BSFException;
import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.instance.Instance;
import org.contract4j5.interpreter.bsf.jruby.JRubyBSFExpressionInterpreter;
import org.contract4j5.util.SystemUtils;

import junit.framework.TestCase;

public class JRubyBSFExpressionInterpreterTest extends TestCase {
	private JRubyBSFExpressionInterpreter interpreter;
	private TestContext context;
	private static final String testExpression = "\"abc\".equals(\"abcd\")";
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (!SystemUtils.isJRuby())
			return;
		interpreter = new JRubyBSFExpressionInterpreter();
		context = new TestContextImpl(testExpression, "abc", new Instance("abc", String.class, "abc"), null, new Instance[0], null, "NoFile", -1);
	}
	
	public void testJRubyInterpreterCanValidateTestExpressions() throws BSFException {
		if (!SystemUtils.isJRuby())
			return;
		assertTrue(interpreter.validateTestExpression(testExpression, context).isPassed());
	}
	
	public void testJRubyInterpreterCanExecuteTestExpressions() throws BSFException {
		if (!SystemUtils.isJRuby())
			return;
		assertFalse(interpreter.invokeTest(testExpression, context).isPassed());
	}
}
