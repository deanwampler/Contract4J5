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
import org.contract4j5.instance.Instance;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.interpreter.groovy.GroovyExpressionInterpreter;
import org.contract4j5.reporter.Reporter;
import org.contract4j5.reporter.Severity;
import org.contract4j5.utils.StringUtils;

/**
 * Class that implements {@link ContractEnforcer}. Most of the interesting work is
 * done by the {@link ExpressionInterpreter} used by this class.
 * 
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public abstract class ContractEnforcerHelper implements ContractEnforcer {

	private ExpressionInterpreter expressionInterpreter = null;
	
	public ExpressionInterpreter getExpressionInterpreter() {
		if (expressionInterpreter == null) {
			expressionInterpreter = new GroovyExpressionInterpreter();
		}
		return expressionInterpreter;
	}
	public void setExpressionInterpreter(ExpressionInterpreter expressionInterpreter) {
		this.expressionInterpreter = expressionInterpreter;
	}
	
	private boolean reportError = true;

	public void    setReportErrors(boolean onOff) { reportError = onOff; }
	public boolean getReportErrors()              { return reportError; }

	private Severity errorReportingSeverity = Severity.FATAL;
	
	public void     setErrorReportingSeverityLevel(Severity severity) { errorReportingSeverity = severity; }
	public Severity getErrorReportingSeverityLevel()                  { return errorReportingSeverity; }
	
	private boolean includeStackTrace = false;
	
	public void    setIncludeStackTrace(boolean onOff) { includeStackTrace = onOff; }
	public boolean getIncludeStackTrace()              { return includeStackTrace; }
	
	private boolean  warnedOnce = false;
	private Reporter reporter;
	
	protected Reporter getReporter() {
		return reporter;
	}
	
	public void invokeTest (
			String      testPrefix, 
			String      extraMessage, 
			TestContext context) throws ContractError {
		ExpressionInterpreter interpreter = getExpressionInterpreter();
		if (interpreter == null) {
			if (warnedOnce == false) {
				getReporter().report (getErrorReportingSeverityLevel(), this.getClass(), "No ExpressionInterpreter is defined for the ContractEnforcer!");
				warnedOnce = true;
			}
			return;
		}
		getReporter().report(Severity.DEBUG, this.getClass(), "Invoking \""+testPrefix+"\" test: \""+context.getActualTestExpression()+"\".");
		TestResult testResult = interpreter.invokeTest(context);
		if (testResult.isPassed() == false) {
			handleFailure(context.getActualTestExpression(), testPrefix, extraMessage, context, testResult);
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
		handleFailure(testExpression, testPrefix, extraMessage, context, testResult);
	}

	public void handleFailure(String testExpression, String testPrefix, String extraMessage, TestContext context, TestResult testResult) throws ContractError {
		String msg = makeFailureMessage(testExpression, testPrefix, extraMessage, context, testResult);
		reportContractFailure(msg, testResult.getFailureCause());
		finishFailureHandling(testResult, msg);
	}

	/**
	 * Override this method to complete failure handling, <i>e.g.,</i> to throw a 
	 * {@link ContractError}, it will have to subclass {@link ContractError}, which
	 * is unchecked.
	 * @param testResult
	 * @param msg
	 */
	protected abstract void finishFailureHandling(TestResult testResult, String msg) throws ContractError;
	
	protected void reportContractFailure (String message, Throwable throwable) {
		if (getReportErrors() == false)
			return;
		String report = makeStackDumpMessage(message, throwable);
		getReporter().report (getErrorReportingSeverityLevel(), this.getClass(), report);
	}
	
	protected String makeStackDumpMessage(String message, Throwable throwable) {
		StringBuilder buff = new StringBuilder(1024); // arbitrary size
		buff.append(message);
		if (throwable != null) {
			Throwable t = throwable;
			if (t.getCause() != null) {
				t = t.getCause();
			}
			String newline = StringUtils.newline();
			while (t != null) {
				buff.append(newline);
				buff.append(t.toString());
				if (t.getMessage() != null) {
					buff.append(newline);
					buff.append("  Message: \"");
					buff.append(t.getMessage());
					buff.append("\", ");
				}
				appendStackTrace(t, buff);
				t = t.getCause();
			}
		}
		String report = buff.toString();
		return report;
	}
	
	protected void appendStackTrace(Throwable throwable, StringBuilder buff) {
		if (getIncludeStackTrace()) {
			String newline = StringUtils.newline();
			buff.append(newline);
			buff.append("  Stack Trace:");
			buff.append(newline);
			StackTraceElement[] trace = throwable.getStackTrace();
			for (int i = 0; i < trace.length; i++) {
				buff.append("    ");
				buff.append(trace[i].toString());
				buff.append(newline);
			}
		}
	}

	/**
	 * Instantiates the appropriate subclass of {@link ContractError}. Override if
	 * you introduce new custom subclasses.
	 * @param message used to construct the ContractError.
	 * @param throwable that originally caused the failure.
	 * @return newly constructed ContractError.
	 */
	protected ContractError makeContractError(String message, Throwable throwable) {
		if (throwable instanceof TestSpecificationError) {
			// Hack. We'd like to prepend message to throwable.getMessage(), but
			// there is no way to change it!
			return new TestSpecificationError(message, throwable);
		}
		return new ContractError(message, throwable);
	}

	protected String makeFailureMessage(
			String testExpression, 
			String testPrefix, 
			String extraMessage, 
			TestContext context, 
			TestResult testResult) {
		String expression = StringUtils.empty(testExpression) ? "<empty test expression>" : testExpression;
		String prefix = StringUtils.empty(testPrefix) ? "<unknown test>" : testPrefix;
		String fn = context != null ? context.getFileName() : null;
		if (StringUtils.empty(fn)) {
			fn = "<unknown file>";
		}
		StringBuffer msg = new StringBuffer(256);
		msg.append("*** Contract Failure ");
		String lineNumber = context != null ? Integer.toString(context.getLineNumber()) : "<unknown>";
		msg.append("("+fn+":"+lineNumber +"): ");				
		if (testResult.isFailureCauseATestSpecificationFailure()) {
			msg.append("Test specification error, ");
		}
		msg.append(prefix).append(" test \"").append(expression);
		Instance thiz = context != null ? context.getInstance() : null;
		String name = thiz != null ? thiz.getItemName() : "";
		if (name.length() == 0) {
			name = "<unknown>";
			if (context != null && context.getField() != null) {
				name = context.getField().getItemName();
			}
		}
		msg.append("\" for \"").append(name).append("\" failed. ");
		if (!StringUtils.empty(extraMessage)) {
			msg.append(" ").append(extraMessage);
		}
		if(!StringUtils.empty(testResult.getMessage())) {
			msg.append(" (").append(testResult.getMessage()).append(")");
		}
		msg.append(" [").append(testResult.getFailureCauseMessage()).append("]");
		return msg.toString();
	}
	
	/**
	 * Constructor.
	 * @param expressionInterpreter
	 * @param includeStackTrace
	 */
	public ContractEnforcerHelper(
			ExpressionInterpreter expressionInterpreter,
			boolean includeStackTrace) {
		setExpressionInterpreter(expressionInterpreter);
		setIncludeStackTrace(includeStackTrace);
	}

	/**
	 * Default Constructor. By default, don't include the stack trace in error
	 * messages and set the expression interpreter to null (not recommended!).
	 */
	public ContractEnforcerHelper() {
		setExpressionInterpreter(null);
		setIncludeStackTrace(false);
	}
}
