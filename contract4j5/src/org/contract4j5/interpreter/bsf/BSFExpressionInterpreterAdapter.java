package org.contract4j5.interpreter.bsf;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.contract4j5.context.TestContext;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.interpreter.ExpressionInterpreterHelper;
import org.contract4j5.interpreter.TestResult;

public class BSFExpressionInterpreterAdapter extends
		ExpressionInterpreterHelper {

	private BSFEngine  bsfEngine;
	private BSFManager bsfManager;
	private String     scriptingEngineName;
	private String     testPseudoScriptFileName;

	public BSFEngine  getBSFEngine()  { return bsfEngine;  }
	public BSFManager getBSFManager() { return bsfManager; }
	
	public BSFExpressionInterpreterAdapter(String whichScriptingEngine) throws BSFException {
		super();
		this.scriptingEngineName = whichScriptingEngine;
		this.bsfManager = new BSFManager();
		this.bsfEngine  = this.bsfManager.loadScriptingEngine(whichScriptingEngine);
		this.testPseudoScriptFileName = "test." + whichScriptingEngine;
	}

	@Override
	protected Object doDetermineOldValue(String exprStr, TestContext context) {
		try {
			return bsfEngine.eval("determine old value", 0, 0, exprStr);
		} catch (BSFException e) {
			throw new TestSpecificationError("BSF Engine failed to evaluate the expression \""+exprStr+"\".", e);
		}
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

	@Override
	protected TestResult doTest(String testExpression, TestContext context) {
		try {
			Object o = bsfManager.eval(scriptingEngineName, testPseudoScriptFileName, 0, 0, testExpression);
			if (!(o instanceof Boolean)) {
				String ostr = o != null ? o.getClass().getName() : "null object";
				return new TestResult (false, "Test returned \""+ostr+"\", instead of boolean for test expression \""+testExpression+"\".");
			}
			return new TestResult ((Boolean) o);
		} catch (BSFException e) {
			String msg = "BSF evaluation of test expression \""+testExpression+"\" failed.";
			return new TestResult (false, msg, new TestSpecificationError(msg, e));
		}
	}

}
