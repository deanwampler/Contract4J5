/*
 * Copyright 2005 2006 Dean Wampler. All rights reserved.
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

package org.contract4j5.interpreter.jexl;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;
import org.contract4j5.context.TestContext;
import org.contract4j5.errors.TestSpecificationError;
import org.contract4j5.interpreter.ExpressionInterpreterHelper;
import org.contract4j5.interpreter.TestResult;

/**
 * Wrapper around the Jexl interpreter. You might consider using the BSF wrapper.
 * However, I have discovered that BSF adds <em>significant</em> overhead, perhaps
 * increasing the time for script evaluation by a factor of 3 or so! Too bad, 
 * since it makes supporting new interpreters easy.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class JexlExpressionInterpreter extends ExpressionInterpreterHelper {

	private JexlContext jexlContext = null;
	
	/* (non-Javadoc)
	 * @see org.contract4j5.interpreter.ExpressionInterpreterHelper#doDetermineOldValue(java.lang.String, org.contract4j5.TestContext)
	 */
	@Override
	protected Object doDetermineOldValue (String exprStr, TestContext context2) {
		Expression expr;
		try {
			expr = getOrParseExpression(exprStr);
		} catch (Throwable th) {
			throw new TestSpecificationError("Failed to create the Jexl expression for \""+exprStr+"\".", th);
		}
		try {
			Object o = expr.evaluate(jexlContext);
			return o;
		} catch (Throwable th2) {
			throw new TestSpecificationError("Failed to evaluate the Jexl expression for \""+exprStr+"\".", th2);
		}
	}
	
	/**
	 * Do the test. The input test expression already has all required subsitutions
	 * made.
	 * @see org.contract4j5.interpreter.ExpressionInterpreterHelper#doTest(java.lang.String, org.contract4j5.context.TestContext)
	 */
	@Override
	protected TestResult doTest(
			String      testExpression, 
			TestContext context) {
		Expression expr;
		try {
			expr = getOrParseExpression(testExpression);
		} catch (Throwable th) {
			String msg = "Failed to create a Jexl Expression object.";
			return new TestResult (false, msg, new TestSpecificationError(msg, th));
		}
		try {
			Object o = expr.evaluate(jexlContext);
			if (!(o instanceof Boolean)) {
				String msg = didNotReturnBooleanErrorMessage(testExpression, o);
				return new TestResult (false, msg);
			}
			return new TestResult ((Boolean) o);
		} catch (Exception e) {
			return new TestResult (false, "Failed to evaluate the Jexl expression.", e);
		}
	}
	
	/**
	 * Save the change by adding the new symbol name and object to the {@link #jexlContext} map.
	 * @see org.contract4j5.interpreter.ExpressionInterpreterHelper#recordContextChange(java.lang.String, java.lang.Object, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doRecordContextChange(String newSymbolName, Object newObject) {
		Map<String, Object> map = jexlContext.getVars();
		map.put (newSymbolName, newObject);
	}
	
	/**
	 * Remove the change by removing the symbol name and object from the {@link #jexlContext} map.
	 * @see org.contract4j5.interpreter.ExpressionInterpreterHelper#removeContextChange(java.lang.String, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doRemoveContextChange(String oldSymbolName) {
		Map<String, Object> map = jexlContext.getVars();
		map.remove(oldSymbolName);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected Object doGetObjectInContext(String name) {
		Map<String, Object> map = jexlContext.getVars();
		return map.get(name);
	}

	public JexlExpressionInterpreter() {
		this(false, new HashMap<String,String>());
	}

	public JexlExpressionInterpreter(
			boolean treatEmptyTestExpressionAsValid, Map<String, String> optionalKeywordSubstitutions) {
		super("jexl", treatEmptyTestExpressionAsValid, optionalKeywordSubstitutions);
		jexlContext = JexlHelper.createContext();
	}

	@Override
	protected boolean isLikelyTestSpecificationError(
			Throwable throwable) {
		// TODO what Jexl exceptions should we observe here?
		return false;
	}

	private SortedMap<String,Expression> expressionCache = new TreeMap<String,Expression>();

	private Expression getOrParseExpression(String exprStr) throws Throwable {
		Expression script = expressionCache.get(exprStr);
		if (script != null) return script;
		script = ExpressionFactory.createExpression (exprStr);
		expressionCache.put(exprStr, script);
		return script;
	}

}
