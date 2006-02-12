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

package org.contract4j5;

import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.util.reporter.Reporter;

/**
 * Interface for the component that invokes tests and handles failures.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public interface ContractEnforcer {
	/**
	 * Perform the test, given the test express and handle failure appropriately.
	 * @param testExpression that implements the test, which must be valid, based
	 * on the constraints of the {@link ExpressionInterpreter}. 
	 * @param testPrefix for the corresponding test, <i>e.g.,</i> "pre"
	 * @param extraMessage optional message printed with an error report
	 * @param context containing context information about the method, etc.
	 */
	void invokeTest (
			String      testExpression, 
			String      testPrefix, 
			String      extraMessage,
			TestContext context);
	
	/**
	 * Format a standard error message used when a test fails.
	 * @param testExpression the expression that failed (optional)
	 * @param testPrefix either "Pre", "Post", etc. (optional)
	 * @param extraMessage additional context-specific information (optional)
	 * @param context the TestContext object for this test invocation
	 * @param testResult a result object with failure information (optional)
	 * @return the formatted string
	 * @note the optional items can be empty or null.
	 */
	String makeFailureMessage(
			String testExpression, 
			String testPrefix, 
			String extraMessage, 
			TestContext context, 
			TestResult testResult);
	
	/**
	 * Handle a test failure by reporting a message and throwing a
	 * {@link ContractError}, which is unchecked, with an error message
	 * and an optional input {@link Throwable} from which additional information
	 * is reported. The normal requirement in DbC is that the program is stopped
	 * when a test fails. Hence, assume that this method will not return.
	 * This is an important principle of <i>Design by Contract</i>. 
	 * It is a program correctness tool for catching logic errors during development; 
	 * not a tool for checking runtime conditions. While clients of this class could 
	 * catch the thrown ContractError and attempt recovery, doing so leads to quality
	 * degradation over time because fixing contract failures will be seen as less 
	 * urgent and will be deferred, thereby reducing quality. Hence, use alternative
	 * means to test true runtime conditions <i>e.g.</i>, invalid user input,
	 * memory exhaustion, <i>etc.</> and save contract tests for finding logic
	 * errors during development
	 * @param message describing the failure
	 * @param throwable from which additional information is reported.
	 * @throws ContractError
	 */
	void handleFailure(String message, Throwable throwable) throws ContractError;

	/**
	 * Handle a test failure without an accompanying {@link Throwable}.
	 * @param message describing the failure
	 * @throws ContractError
	 * @see #handleFailure (String, Throwable)
	 */
	void handleFailure(String message) throws ContractError;

	/**
	 * Handle a test failure without a descriptive message or an accompanying {@link Throwable}.
	 * @throws ContractError
	 * @see #handleFailure (String, Throwable)
	 */
	void handleFailure() throws ContractError;

	/**
	 * Turn on or off inclusion of stack traces in error messages.
	 * @param onOff enables inclusion if <code>true</code> or disables it if <code>false</code>
	 */
	void setIncludeStackTrace(boolean onOff);

	/**
	 * @return boolean whether inclusion of stack traces is enabled (<code>true</code>)
	 *         or disabled (<code>false</code>).
	 */
	boolean getIncludeStackTrace();
	
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

	/**
	 * @return Returns the reporter used by the implementation to report/log messages
	 */
	Reporter getReporter();
	
	/**
	 * @param reporter The reporter to be used by the implementation to report/log messages
	 */
	void setReporter(Reporter reporter);
}
