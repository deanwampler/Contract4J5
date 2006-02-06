package org.contract4j5.interpreter.jexl;

import java.util.Map;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;
import org.contract4j5.TestContext;
import org.contract4j5.interpreter.ExpressionInterpreterHelper;
import org.contract4j5.interpreter.TestResult;

public class JexlExpressionInterpreter extends ExpressionInterpreterHelper {

	private JexlContext jexlContext = null;
	
	/* (non-Javadoc)
	 * @see org.contract4j5.interpreter.ExpressionInterpreterHelper#doDetermineOldValue(java.lang.String, org.contract4j5.TestContext)
	 */
	@Override
	protected Object doDetermineOldValue (String exprStr, TestContext context) {
		Expression expr;
		try {
			expr = ExpressionFactory.createExpression (exprStr);
		} catch (Throwable th) {
			return new TestResult (false, "Failed to create the Jexl expression for \""+exprStr+"\".", th);
		}
		try {
			Object o = expr.evaluate(jexlContext);
			return o;
		} catch (Throwable th2) {
			return new TestResult (false, "Failed to evaluate the Jexl expression for \""+exprStr+"\".", th2);
		}
	}
	
	/**
	 * Do the test. Begin by creating an expression object and substituting the special 
	 * keywords, then evaluate.
	 * Note that in "$old(..)" expressions, we replace anything other than "$old($this)" 
	 * and "$old($target)" with "c4jOldThis.string", where "string" is the argument in
	 * the "$old(..)". This assumes that the old instance was captured (e.g., thru cloning)
	 * and the "string" is an accessible field in the instance.
	 * The first substitution handles, e.g., "$old($this)", "$old($this.foo)", 
	 * "$old($this.foo.bar)", but not "$old($this.foo.getBar())", etc., even though the
	 * latter is legal. Instead, use "$old($this.foo).getBar().
	 * @note If the item name isn't empty and the target is not null, both correspond to a field. 
	 * We look for "bare" field references (without $this or $target) and replace them with
	 * "c4jTarget".
	 * TODO would it be better to use "c4jThis.field" instead?
	 * @see org.contract4j5.interpreter.ExpressionInterpreterHelper#doTest(java.lang.String, org.contract4j5.TestContext)
	 */
	@Override
	protected TestResult doTest(
			String      testExpression, 
			TestContext context) {
		Expression expr;
		try {
			expr = ExpressionFactory.createExpression (testExpression);
		} catch (Throwable th) {
			return new TestResult (false, "Failed to create a Jexl Expression object.", th);
		}
		try {
			Object o = expr.evaluate(jexlContext);
			if (!(o instanceof Boolean)) {
				String ostr = o != null ? o.getClass().getName() : "null object";
				return new TestResult (false, "Test returned \""+ostr+"\", instead of boolean.");
			}
			return new TestResult ((Boolean) o);
		} catch (Exception e) {
			return new TestResult (false, "Failed to evaluate the Jexl expression.", e);
		}
	}
	
	/**
	 * Save the change by adding the new symbol name and object to the {@link #jexlContext} map.
	 * @see org.contract4j5.interpreter.ExpressionInterpreterHelper#recordContextChange(java.lang.String, java.lang.Object)
	 */
	@Override
	protected void doRecordContextChange(String newSymbolName, Object newObject) {
		Map<String, Object> map = jexlContext.getVars();
		map.put (newSymbolName, newObject);
	}
	
	/**
	 * Remove the change by removing the symbol name and object from the {@link #jexlContext} map.
	 * @see org.contract4j5.interpreter.ExpressionInterpreterHelper#removeContextChange(java.lang.String, java.lang.Object)
	 */
	@Override
	protected void doRemoveContextChange(String newSymbolName, Object newObject) {
		Map<String, Object> map = jexlContext.getVars();
		map.remove(newSymbolName);
	}
	
	public JexlExpressionInterpreter() {
		super();
		jexlContext = JexlHelper.createContext();
	}
}
