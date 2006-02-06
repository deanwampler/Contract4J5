package org.contract4j5;

import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.NullExpressionInterpreter;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.util.reporter.NullReporter;
import org.contract4j5.util.reporter.Reporter;

/**
 * Uses the "Null Object Pattern" to define a contract enforcer that does nothing,
 * effectively disabling all tests, although it is more transparent to users to disable
 * the tests with the {@link Contract4J#isEnabled(TestType)} flags instead. Rather, this
 * object is primarily useful for eliminating the need for clients to always test for null.
 * @author Dean Wampler
 */
public class NullContractEnforcer implements ContractEnforcer {

	public void invokeTest(String testExpression, String testPrefix,
			String extraMessage, TestContext context) {}
	
	public String makeFailureMessage(String testExpression, String testPrefix, String extraMessage, TestContext context, TestResult testResult) {
		return extraMessage;
	}
	public void handleFailure(String message, Throwable throwable)
			throws ContractError {}
	public void handleFailure(String message) throws ContractError {}
	public void handleFailure() throws ContractError {}

	public void setIncludeStackTrace(boolean onOff) {}
	public boolean getIncludeStackTrace() { return false; }

	private NullExpressionInterpreter nullExpressionInterpreter = 
		new NullExpressionInterpreter();
	public void setExpressionInterpreter(
			ExpressionInterpreter expressionInterpreter) {}
	public ExpressionInterpreter getExpressionInterpreter() {
		return nullExpressionInterpreter;
	}

	Reporter nullReporter = new NullReporter();
	public void setReporter(Reporter reporter) {}
	public Reporter getReporter() {
		return nullReporter;
	}
	

}
