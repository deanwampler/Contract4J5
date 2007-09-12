package org.contract4j5.interpreter.bsf.jexl.test;

import org.apache.bsf.BSFException;
import org.contract4j5.context.TestContext;
import org.contract4j5.context.TestContextImpl;
import org.contract4j5.interpreter.bsf.jexl.JexlBSFExpressionInterpreter;

import junit.framework.TestCase;

public class JexlBSFExpressionInterpreterTest extends TestCase {
	private JexlBSFExpressionInterpreter interpreter;
	private TestContext context;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		interpreter = new JexlBSFExpressionInterpreter();
		context = new TestContextImpl("1+1 == 2", null, null, null, null, null, "NoFile", -1);
	}
	
	public void testJexlInterpreterCanValidateTestExpressions() throws BSFException {
		assertTrue(interpreter.validateTestExpression("1+1 == 2", context).isPassed());
	}
	
	public void testJexlInterpreterCanExecuteTestExpressions() throws BSFException {
		assertTrue(interpreter.invokeTest("1+1 == 2", context).isPassed());
	}
}
