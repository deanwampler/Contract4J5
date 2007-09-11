package org.contract4j5.interpreter.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;

import java.util.HashMap;
import java.util.Map;

import org.contract4j5.context.TestContext;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.interpreter.ExpressionInterpreterHelper;
import org.contract4j5.interpreter.TestResult;

public class GroovyExpressionInterpreter extends ExpressionInterpreterHelper {

	private Binding binding;
	private GroovyShell shell;

	public GroovyExpressionInterpreter(boolean treatEmptyTestExpressionAsValid, Map<String, String> optionalKeywordSubstitutions) {
		super("groovy", treatEmptyTestExpressionAsValid, optionalKeywordSubstitutions);
		binding = new Binding();
		shell   = new GroovyShell(binding);
	}
	
	public GroovyExpressionInterpreter() {
		this(false, new HashMap<String, String>());
	}
	
	@Override
	protected Object doDetermineOldValue(String exprStr, TestContext context) {
		try {
			return shell.evaluate(exprStr);
		} catch (Exception e) {
			TestResult result = makeExceptionThrownTestResult(exprStr, context, e);
			throw new TestSpecificationError(result.getFailureCauseMessage());
		}
	}

	@Override
	protected Object doGetObjectInContext(String name) {
		try {
			return binding.getVariable(name);
		} catch (MissingPropertyException mpe) {
			return null;
		}
	}

	@Override
	protected void doRecordContextChange(String newSymbolName, Object newObject) {
		binding.setVariable(newSymbolName, newObject);
	}

	@Override
	protected void doRemoveContextChange(String oldSymbolName) {
		binding.setVariable(oldSymbolName, null);
	}

	@Override
	protected TestResult doTest(String testExpression, TestContext context) {
		try {
			Object value = shell.evaluate(testExpression);
			if (value instanceof Boolean) 
				return new TestResult(((Boolean) value).booleanValue());
			return new TestResult(false, didNotReturnBooleanErrorMessage(testExpression, value));
		} catch (Exception e) {
			return makeExceptionThrownTestResult(testExpression, context, e);
		}
	}

	@Override
	protected boolean isLikelyTestSpecificationError(
			Throwable throwable) {
		// TODO what Groovy exceptions should we observe here?
		return false;
	}
}
