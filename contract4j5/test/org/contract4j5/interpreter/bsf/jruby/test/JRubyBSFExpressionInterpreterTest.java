package org.contract4j5.interpreter.bsf.jruby.test;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.instance.Instance;
import org.contract4j5.interpreter.TestResult;
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
		if (!SystemUtils.isJRuby())	return;
		interpreter = new JRubyBSFExpressionInterpreter();
		context = new TestContextImpl(testExpression, "abc", new Instance("abc", String.class, "abc"), null, new Instance[0], null, "NoFile", -1);
	}
	
	public void testJRubyInterpreterCanValidateTestExpressions() throws BSFException {
		if (!SystemUtils.isJRuby())	return;
		assertTrue(interpreter.validateTestExpression(context).isPassed());
	}
	
	public void testJRubyInterpreterCanExecuteTestExpressions() throws BSFException {
		if (!SystemUtils.isJRuby())	return;
		assertFalse(interpreter.invokeTest(context).isPassed());
	}
	
	public void testJRubyIsReallyInvoked1() {
		if (!SystemUtils.isJRuby())	return;
		String expression = "class Foo\ndef to_s\n'hello!'\nend\nend\nFoo.new.to_s.eql?('hello!')\n";
		context = new TestContextImpl(expression, "foo", new Instance("foo", String.class, "foo"), null, new Instance[0], null, "NoFile", -1);
		TestResult result = interpreter.invokeTest(context);
		assertTrue(result.toString(), result.isPassed());
		assertEquals("", result.getMessage());
	}
	
	public void testJRubyIsReallyInvoked2() {
		if (!SystemUtils.isJRuby())	return;
		String expression = "global_variables\n";
		context = new TestContextImpl(expression, "foo", new Instance("foo", String.class, "foo"), null, new Instance[0], null, "NoFile", -1);
		TestResult result = interpreter.invokeTest(context);
		assertFalse(result.toString(), result.isPassed());
		assertTrue(result.toString(), result.getMessage().contains("Test returned \"org.jruby.RubyArray\""));
	}
}
