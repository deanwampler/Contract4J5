package org.contract4j5.enforcer.defaultimpl;

import org.contract4j5.enforcer.ContractEnforcerHelper;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.TestResult;

public class DefaultContractEnforcer extends ContractEnforcerHelper {
	
	protected void finishFailureHandling(TestResult testResult, String msg) {
		throw makeContractError(msg, testResult.getFailureCause());
	}

	/**
	 * Constructor.
	 * @param expressionInterpreter
	 * @param includeStackTrace
	 */
	public DefaultContractEnforcer(
			ExpressionInterpreter expressionInterpreter,
			boolean includeStackTrace) {
		super(expressionInterpreter, includeStackTrace);
	}

	/**
	 * Default Constructor. By default, don't include the stack trace in error
	 * messages and set the expression interpreter to null (not recommended!).
	 */
	public DefaultContractEnforcer() {
		super();
	}
}
