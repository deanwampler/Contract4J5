/*
 * Copyright 2005, 2006 Dean Wampler. All rights reserved.
 * http://www.contract4j.org
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
package org.contract4j5.interpreter.bsf;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.contract4j5.context.TestContext;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.interpreter.ExpressionInterpreterHelper;
import org.contract4j5.interpreter.TestResult;
import org.contract4j5.reporter.Severity;

public class BSFExpressionInterpreterAdapter extends
		ExpressionInterpreterHelper {

	private BSFEngine  bsfEngine;
	private BSFManager bsfManager;
	private String     scriptingEngineName;

	public BSFEngine  getBSFEngine()  { return bsfEngine;  }
	public BSFManager getBSFManager() { return bsfManager; }
	public String     getScriptingEngineName() { return scriptingEngineName; }
	
	public BSFExpressionInterpreterAdapter(String whichScriptingEngine) throws BSFException {
		super();
		init(whichScriptingEngine);
	}

	public BSFExpressionInterpreterAdapter(
			String whichScriptingEngine,
			boolean treatEmptyTestExpressionAsValid) throws BSFException {
		super(treatEmptyTestExpressionAsValid, new HashMap<String, String>());
		init(whichScriptingEngine);
	}

	public BSFExpressionInterpreterAdapter(
			String whichScriptingEngine,
			boolean treatEmptyTestExpressionAsValid, 
			Map<String, String> optionalKeywordSubstitutions) throws BSFException {
		super(treatEmptyTestExpressionAsValid, optionalKeywordSubstitutions);
		init(whichScriptingEngine);
	}
	
	private void init(String whichScriptingEngine) throws BSFException {
		this.scriptingEngineName = whichScriptingEngine;
		this.bsfManager = new BSFManager();
		this.bsfEngine  = this.bsfManager.loadScriptingEngine(whichScriptingEngine);
	}

	@Override
	protected Object doDetermineOldValue(String exprStr, TestContext context) {
		try {
			return evaluateScript(exprStr, context);
//			return bsfEngine.eval("determine old value", 0, 0, exprStr);
		} catch (BSFException e) {
			throw new TestSpecificationError("BSF Engine failed to evaluate the expression \""+exprStr+"\".", e);
		}
	}

	@Override
	protected TestResult doTest(String testExpression, TestContext context) {
		TestResult result = null;
		try {
			Object o = evaluateScript(testExpression, context);
			if (!(o instanceof Boolean)) {
				String ostr = o != null ? o.getClass().getName() : "null object";
				result = new TestResult (false, "Test returned \""+ostr+"\", instead of boolean for test expression \""+testExpression+"\".");
			} else {
				result = new TestResult ((Boolean) o);
			}
		} catch (BSFException e) {
			String msg = "BSF evaluation of test expression \""+testExpression+"\" failed: " + makeBSFExceptionMessage(e);
			Throwable cause  = e.getCause();
			Throwable target = e.getTargetException();
			if (cause instanceof NullPointerException || target instanceof NullPointerException) 
				result = new TestResult (false, msg, new TestSpecificationError(msg, e));
			else if (target != null)
				result = new TestResult (false, msg, target);
			else if (cause != null)
				result = new TestResult (false, msg, cause);
			result = new TestResult (false, msg);
		}
		return result;
	}
	
	private String makeBSFExceptionMessage(BSFException e) {
		StringBuffer buff = new StringBuffer();
		switch (e.getReason()) {
		case BSFException.REASON_INVALID_ARGUMENT:
			buff.append("BSFException.REASON_INVALID_ARGUMENT");
			break;
		case BSFException.REASON_IO_ERROR:
			buff.append("BSFException.REASON_IO_ERROR");
			break;
		case BSFException.REASON_UNKNOWN_LANGUAGE:
			buff.append("BSFException.REASON_UNKNOWN_LANGUAGE");
			break;
		case BSFException.REASON_EXECUTION_ERROR:
			buff.append("BSFException.REASON_EXECUTION_ERROR");
			break;
		case BSFException.REASON_UNSUPPORTED_FEATURE:
			buff.append("BSFException.REASON_UNSUPPORTED_FEATURE");
			break;
		case BSFException.REASON_OTHER_ERROR:
			buff.append("BSFException.REASON_OTHER_ERROR");
			break;
		default:
			buff.append("Unknown BSFException reason code \""+e.getReason()+"\"");
		}
		Throwable targetException = e.getTargetException();
		if (targetException != null) {
			buff.append(": target exception = ");
			buff.append(makeExceptionMessage(targetException));
			if (targetException.getCause() != null) {
				buff.append(", target exception cause = ");
				buff.append(makeExceptionMessage(targetException.getCause()));
			}
			buff.append(".");
		}
		return buff.toString();
	}
	
	// Big hack: When using JRuby, we have discovered that the true exception cause 
	// is likely a JRuby-specific exception with its own nested exception object, not 
	// accessable through the standard "getCause()" method. This nested exception is
	// the one with the really interesting error message! To get at it without hard-coding
	// JRuby dependencies here, we use reflection.
	private String makeExceptionMessage(Throwable exception) {
		if (exception == null)
			return "";
		String message = exception.toString();
		if (exception.getClass().getSimpleName().equals("RaiseException")) {
			try {
				Method method = exception.getClass().getDeclaredMethod("getException", new Class[0]);
				Object object = method.invoke(exception, new Object[0]);
				message += ": nested exception = " + (object == null ? "<null>" : object.toString());
			} catch (Exception e) {
				getReporter().report(Severity.WARN, this.getClass(), "Calling 'getException()' on what appears to be a JRuby 'RaiseException' failed: "+e.toString());
			}
		}
		return message;
	}
	
	@Override
	protected void doRecordContextChange(String newSymbolName, Object newObject) {
		try {
			bsfManager.declareBean(newSymbolName, newObject, 
					newObject != null ? newObject.getClass() : null);
		} catch (BSFException e) {
			throw new TestSpecificationError("BSF Manager failed to declare bean with name \""+newSymbolName+"\", and value \"" + newObject + "\".", e);
		}
	}

	@Override
	protected void doRemoveContextChange(String oldSymbolName) {
		try {
			bsfManager.undeclareBean(oldSymbolName);
		} catch (BSFException e) {
			throw new TestSpecificationError("BSF Manager failed to undeclare bean with name \""+oldSymbolName+"\".", e);
		}
	}

	protected Object evaluateScript(String testExpression, TestContext context) throws BSFException {
		return bsfManager.eval(scriptingEngineName, getSourceName(context), 0, 0, testExpression);
	}

	private String getSourceName(TestContext context) {
		return context.getInstance() != null ?
				context.getInstance().getClazz().getSimpleName()+"."+scriptingEngineName : null;
	}

}
