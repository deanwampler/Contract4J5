/*
 * Copyright 2005 Dean Wampler. All rights reserved.
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
import org.contract4j5.util.reporter.Severity;
import org.contract4j5.util.reporter.WriterReporter;

/**
 * Class that implements {@link ContractEnforcer}. Most of the interesting work is
 * done by the {@link ExpressionInterpreter} property used by this class.
 * 
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class ContractEnforcerImpl implements ContractEnforcer {

	private Reporter reporter;
	
	/* (non-Javadoc)
	 * @see org.contract4j5.ContractEnforcer#getReporter()
	 */
	public Reporter getReporter() {
		if (this.reporter == null) {
			this.reporter = new WriterReporter();
		}
		return reporter;
	}

	/* (non-Javadoc)
	 * @see org.contract4j5.ContractEnforcer#setReporter(org.contract4j5.util.reporter.Reporter)
	 */
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}

	private ExpressionInterpreter expressionInterpreter = null;
	
	public ExpressionInterpreter getExpressionInterpreter() {
		return expressionInterpreter;
	}
	
	public void setExpressionInterpreter(ExpressionInterpreter expressionInterpreter) {
		this.expressionInterpreter = expressionInterpreter;
	}
	
	private boolean includeStackTrace;
	
	public void setIncludeStackTrace(boolean onOff) {
		includeStackTrace = onOff;
	}
	
	public boolean getIncludeStackTrace() {
		return includeStackTrace;
	}
	
	private boolean warnedOnce = false;
	
	public void invokeTest (
			String      testExpression, 
			String      testPrefix, 
			String      extraMessage, 
			TestContext context) {
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
			String msg = makeFailureMessage(testExpression, testPrefix, extraMessage, context, testResult);
			handleFailure(msg);
		}
	}

	/**
	 * Format a standard error message used when a test fails.
	 * @param testExpression
	 * @param testPrefix
	 * @param extraMessage
	 * @param context
	 * @param testResult
	 * @return
	 */
	public String makeFailureMessage(
			String testExpression, 
			String testPrefix, 
			String extraMessage, 
			TestContext context, 
			TestResult testResult) {
		StringBuffer msg = new StringBuffer(256);
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
		msg.append(testPrefix).append(" test \"").append(testExpression);
		Instance thiz = context.getInstance();
		String name = thiz != null ? thiz.getItemName() : "<unknown>";
		if (name.length() == 0) {
			name = context.getField().getItemName();
		}
		msg.append("\" for \"").append(name).append("\" failed. ");
		msg.append("("+fn+":"+context.getLineNumber()+")");				
		if (!empty(extraMessage)) {
			msg.append(" ").append(extraMessage);
		}
		if (testResult != null) {
			if(!empty(testResult.getMessage())) {
				msg.append(" (").append(testResult.getMessage()).append(")");
			}
			if (testResult.getFailureCause() != null) {
				msg.append(" [").append(testResult.getFailureCause().toString()).append("]");
			}
		}
		return msg.toString();
	}
	
	protected boolean empty (String s) {
		return (s == null || s.length() == 0);
	}
	
	/* (non-Javadoc)
	 * @see org.contract4j5.ContractEnforcer#handleFailure()
	 */
	public void handleFailure() throws ContractError {
		handleFailure("<no message>", null);
	}

	/* (non-Javadoc)
	 * @see org.contract4j5.ContractEnforcer#handleFailure(java.lang.String)
	 */
	public void handleFailure (String message) throws ContractError {
		handleFailure(message, null);
	}

	/* (non-Javadoc)
	 * @see org.contract4j5.ContractEnforcer#handleFailure(java.lang.String, Throwable)
	 */
	public void handleFailure (String message, Throwable throwable) throws ContractError {
		StringBuilder buff = new StringBuilder(1024); // arbitrary size
		buff.append("*** Contract Failure: ");
		buff.append(message);
		if (throwable != null) {
			buff.append(" Throwable: ");
			buff.append(throwable.toString());
			String msg = throwable.getMessage();
			if (msg != null) {
				buff.append("\n  Message: \"" + msg + "\", ");
			}
			Throwable cause = throwable.getCause();
			if (cause != null) {
				buff.append("\n  Cause: \"" + cause.toString() + "\", ");
			}
			if (getIncludeStackTrace()) {
				buff.append("\n  Stack Trace:\n");
				StackTraceElement[] trace = throwable.getStackTrace();
				for (int i = 0; i < trace.length; i++) {
					buff.append("    " + trace[i].toString() + "\n");
				}
			}
		}
		getReporter().report (Severity.FATAL, this.getClass(), buff.toString());

		ContractError ce;
		if (throwable != null) {
			ce = new ContractError(message, throwable);
		} else {
			ce = new ContractError(message);
		}
		throw ce;
	}

	/**
	 * Constructor.
	 * 
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
	 * messages and set the interpreter attributes to null.
	 */
	public ContractEnforcerImpl() {
		setExpressionInterpreter(null);
		setIncludeStackTrace(false);
	}
}
