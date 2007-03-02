package org.contract4j5.performance.test;

import org.contract4j5.context.TestContext;
import org.contract4j5.interpreter.ExpressionInterpreterHelper;
import org.contract4j5.interpreter.TestResult;

public class NoBSFExpressionInterpreter extends ExpressionInterpreterHelper {

	@Override
	protected Object doDetermineOldValue(String exprStr, TestContext context) {
		return null;
	}

	@Override
	protected void doRecordContextChange(String newSymbolName, Object newObject) {
	}

	@Override
	protected void doRemoveContextChange(String oldSymbolName) {
	}

	@Override
	protected TestResult doTest(String testExpression, TestContext context) {
		return new TestResult();
	}

}
