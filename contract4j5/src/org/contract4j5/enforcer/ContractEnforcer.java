/*
 * Copyright 2005, 2006 Dean Wampler. All rights reserved.
 * http://www.aspectprogramming.com
 *
 * Licensed under the Eclipse Public License - v 1.0; you may not use this
 * software except in compliance with the License. You may obtain a copy of the 
 * License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * A copy is also included with this distribution. See the "LICENSE" file.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */

package org.contract4j5.enforcer;

import org.contract4j5.context.TestContext;
import org.contract4j5.errors.ContractError;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.reporter.Severity;

/**
 * Interface for the component that invokes tests and handles failures.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public interface ContractEnforcer {
	/**
	 * Perform the test, given the test express and throw an exception if it
	 * fails. Also perform any appropriate reporting, etc.
	 * @param testExpression that implements the test, which must be valid, based
	 * on the constraints of the {@link ExpressionInterpreter}. 
	 * @param testPrefix for the corresponding test, <i>e.g.,</i> "pre"
	 * @param extraMessage optional message printed with an error report
	 * @param context containing context information about the method, etc.
	 * @throws ContractError if the test fails and if the test specification is
	 * in error, then it throws the {@link TestSpecificationError} subclass.
	 */
	void invokeTest (
			String      testExpression, 
			String      testPrefix, 
			String      extraMessage,
			TestContext context) throws ContractError;
	
	/**
	 * Like {@link #invokeTest}, but simply fails. It will always throw a
	 * {@link ContractError}.
	 */
	void fail (
			String      testExpression, 
			String      testPrefix, 
			String      extraMessage,
			TestContext context,
			Throwable   optionalThrowable) throws ContractError;
	
	/**
	 * Turn on or off reporting of error messages. Defaults to on.
	 * @param onOff
	 */
	void setReportErrors(boolean onOff);
	
	/**
	 * Return whether or not reporting of error messages is enabled. Defaults to true.
	 */
	boolean getReportErrors();
	
	/**
	 * Turn on or off inclusion of stack traces in error messages. Ignored if
	 * {@link #getReportErrors()} is false.
	 * @param onOff enables inclusion if <code>true</code> or disables it if <code>false</code>
	 */
	void setIncludeStackTrace(boolean onOff);

	/**
	 * @return boolean whether inclusion of stack traces is enabled (<code>true</code>)
	 *         or disabled (<code>false</code>).
	 */
	boolean getIncludeStackTrace();
	
	/**
	 * Set the severity level to use when reporting the error. Defaults to FATAL.
	 * @param Severity level.
	 */
	void setErrorReportingSeverityLevel(Severity severity);
	
	/**
	 * Get the severity level to use when reporting the error. Defaults to FATAL.
	 */
	Severity getErrorReportingSeverityLevel();
	
	/**
	 * Set the ExpressionInterpreter that parses and evaluates the test 
	 * expressions in the annotations.
	 * @param expressionInterpreter that if null results in no contract tests being executed.
	 */
	void setExpressionInterpreter(ExpressionInterpreter expressionInterpreter);

	/**
	 * Get the ExpressionInterpreter that parses and evaluates the test 
	 * expressions in the annotations.
	 * @return ExpressionInterpreter that if null results in no contract tests being executed.
	 */
	ExpressionInterpreter getExpressionInterpreter();

	public void handleFailure(String testExpression, String testPrefix, String extraMessage,
			TestContext context, TestResult testResult) throws ContractError;
}
