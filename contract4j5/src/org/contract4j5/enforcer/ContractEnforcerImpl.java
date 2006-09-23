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
import org.contract4j5.controller.Contract4J;
import org.contract4j5.errors.ContractError;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.instance.Instance;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.interpreter.jexl.JexlExpressionInterpreter;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.Severity;

/**
 * Class that implements {@link ContractEnforcer}. Most of the interesting work is
 * done by the {@link ExpressionInterpreter} used by this class.
 * 
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class ContractEnforcerImpl implements ContractEnforcer {

	private ExpressionInterpreter expressionInterpreter = null;
	
	public ExpressionInterpreter getExpressionInterpreter() {
		if (expressionInterpreter == null)
			expressionInterpreter = new JexlExpressionInterpreter();
		return expressionInterpreter;
	}
	public void setExpressionInterpreter(ExpressionInterpreter expressionInterpreter) {
		this.expressionInterpreter = expressionInterpreter;
	}
	
	private boolean includeStackTrace = false;
	
	public void    setIncludeStackTrace(boolean onOff) { includeStackTrace = onOff; }
	public boolean getIncludeStackTrace()              { return includeStackTrace; }
	
	private boolean warnedOnce = false;
	
	protected Reporter getReporter() {
		return Contract4J.getInstance().getReporter();
	}
	
	public void invokeTest (
			String      testExpression, 
			String      testPrefix, 
			String      extraMessage, 
			TestContext context) throws ContractError {
		ExpressionInterpreter interpreter = getExpressionInterpreter();
		if (interpreter == null) {
			if (warnedOnce == false) {
				getReporter().report (Severity.FATAL, this.getClass(), "No ExpressionInterpreter is defined for the ContractEnforcer!");
				warnedOnce = true;
			}
			return;
		}
		getReporter().report(Severity.DEBUG, this.getClass(), "Invoking \""+testPrefix+"\" test: \""+testExpression+"\".");
		TestResult testResult = interpreter.invokeTest(testExpression, context);
		if (testResult.isPassed() == false) {
			reportFailureAndThrowContractError(testExpression, testPrefix, extraMessage, context, testResult);
		}
	}

	public void fail(
			String testExpression, 
			String testPrefix, 
			String extraMessage, 
			TestContext context, 
			Throwable optionalThrowable) throws ContractError {
		getReporter().report(Severity.DEBUG, this.getClass(), "fail() called!");
		TestResult testResult = new TestResult(false, "", optionalThrowable);
		reportFailureAndThrowContractError(testExpression, testPrefix, extraMessage, context, testResult);
	}

	protected void reportFailureAndThrowContractError(String testExpression, String testPrefix, String extraMessage, TestContext context, TestResult testResult) throws ContractError {
		String msg = makeFailureMessage(testExpression, testPrefix, extraMessage, context, testResult);
		reportContractFailure(msg, testResult.getFailureCause());
		throw makeContractError(msg, testResult.getFailureCause());
	}
	
	protected void reportContractFailure (String message, Throwable throwable) {
		String report = makeStackDumpMessage(message, throwable);
		getReporter().report (Severity.FATAL, this.getClass(), report);
	}
	
	protected String makeStackDumpMessage(String message, Throwable throwable) {
		StringBuilder buff = new StringBuilder(1024); // arbitrary size
		buff.append(message);
		if (throwable != null) {
			if (throwable.getCause() != null) {
				throwable = throwable.getCause();
			}
			while (throwable != null) {
				buff.append("\n");
				buff.append(throwable.toString());
				if (throwable.getMessage() != null) {
					buff.append("\n  Message: \"");
					buff.append(throwable.getMessage());
					buff.append("\", ");
				}
				appendStackTrace(throwable, buff);
				throwable = throwable.getCause();
			}
		}
		String report = buff.toString();
		return report;
	}
	
	protected void appendStackTrace(Throwable throwable, StringBuilder buff) {
		if (getIncludeStackTrace()) {
			buff.append("\n  Stack Trace:\n");
			StackTraceElement[] trace = throwable.getStackTrace();
			for (int i = 0; i < trace.length; i++) {
				buff.append("    " + trace[i].toString() + "\n");
			}
		}
	}

	protected ContractError makeContractError(String message, Throwable throwable) {
		if (throwable instanceof TestSpecificationError)
			return new TestSpecificationError(message, throwable);
		return new ContractError(message, throwable);
	}

	protected String makeFailureMessage(
			String testExpression, 
			String testPrefix, 
			String extraMessage, 
			TestContext context, 
			TestResult testResult) {
		if (empty(testExpression)) {
			testExpression = "<empty test expression>";
		}
		if (empty(testPrefix)) {
			testPrefix = "<unknown test>";
		}
		String fn = context.getFileName();
		if (empty(fn)) {
			fn = "<unknown file>";
		}
		StringBuffer msg = new StringBuffer(256);
		msg.append("*** Contract Failure ");
		msg.append("("+fn+":"+context.getLineNumber()+"): ");				
		if (testResult.isFailureCauseATestSpecificationFailure()) {
			msg.append("Test specification error, ");
		}
		msg.append(testPrefix).append(" test \"").append(testExpression);
		Instance thiz = context.getInstance();
		String name = thiz != null ? thiz.getItemName() : "<unknown>";
		if (name.length() == 0) {
			name = context.getField().getItemName();
		}
		msg.append("\" for \"").append(name).append("\" failed. ");
		if (!empty(extraMessage)) {
			msg.append(" ").append(extraMessage);
		}
		if (testResult != null) {
			if(!empty(testResult.getMessage())) {
				msg.append(" (").append(testResult.getMessage()).append(")");
			}
			msg.append(" [").append(testResult.getFailureCauseMessage()).append("]");
		}
		return msg.toString();
	}
	
	protected boolean empty (String s) {
		return (s == null || s.length() == 0);
	}
	
	/**
	 * Constructor.
	 * @param expressionInterpreter
	 * @param includeStackTrace
	 */
	public ContractEnforcerImpl(
			ExpressionInterpreter expressionInterpreter,
			boolean includeStackTrace) {
		setExpressionInterpreter(expressionInterpreter);
		setIncludeStackTrace(includeStackTrace);
	}

	/**
	 * Default Constructor. By default, don't include the stack trace in error
	 * messages and set the expression interpreter to null (not recommended!).
	 */
	public ContractEnforcerImpl() {
		setExpressionInterpreter(null);
		setIncludeStackTrace(false);
	}
}
