package org.contract4j5.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.contract4j5.TestContext;
import org.contract4j5.util.reporter.NullReporter;
import org.contract4j5.util.reporter.Reporter;

/**
 * Uses the "Null Object Pattern" to define an interpreter that does nothing. It
 * effectively disables all tests because {@link #invokeTest(String, TestContext)}
 * always succeeds. This object is primarily useful for eliminating the need for 
 * clients to always test for null.
 * @author Dean Wampler
 */
public class NullExpressionInterpreter implements ExpressionInterpreter {
	
	public boolean getTreatEmptyTestExpressionAsValidTest() { return false; }
	public void setTreatEmptyTestExpressionAsValidTest(boolean emptyOK) {}

	public Map<String, String> getOptionalKeywordSubstitutions() { return null; }
	public void setOptionalKeywordSubstitutions(
			Map<String, String> optionalKeywordSubstitutions) {}

	public Map<String, Object> determineOldValues(String testExpression,
			TestContext context) { 
		return new HashMap<String, Object>(); 
	}

	public TestResult validateTestExpression(String testExpression, TestContext context) {
		return new TestResult(true);
	}
	
	public TestResult invokeTest(String testExpression, TestContext context) {
		return new TestResult(true);
	}
	
	private Reporter reporter = null;
	public Reporter getReporter() {
		if (reporter == null) {
			reporter = new NullReporter();
		}
		return reporter;
	}
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
}
