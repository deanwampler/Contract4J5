package org.contract4j5.interpreter.bsf.groovy.test;

import junit.framework.TestCase;

import org.apache.bsf.BSFException;
import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.interpreter.bsf.groovy.GroovyBSFExpressionInterpreter;

public class GroovyBSFExpressionInterpreterTest extends TestCase {
	private GroovyBSFExpressionInterpreter interpreter;
	private TestContext context;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		interpreter = new GroovyBSFExpressionInterpreter();
		context = new TestContextImpl("1+1 == 2", null, null, null, null, null, "NoFile", -1);
	}
	
	public void testGroovyInterpreterCanValidateTestExpressions() throws BSFException {
		assertTrue(interpreter.validateTestExpression(context).isPassed());
	}
	
	public void testGroovyInterpreterCanExecuteTestExpressions() throws BSFException {
		assertTrue(interpreter.invokeTest(context).isPassed());
	}
}
