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
package org.contract4j5.interpreter.bsf.jruby;

import java.util.HashMap;
import java.util.Map;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.contract4j5.context.TestContext;
import org.contract4j5.interpreter.bsf.BSFExpressionInterpreterAdapter;

/**
 * A JRuby subclass of {@link BSFExpressionInterpreterAdapter} that registers the 
 * engine with the bean scripting framework, which we do with a static initializer. 
 * @deprecated
 */
public class JRubyBSFExpressionInterpreter extends BSFExpressionInterpreterAdapter {

	static {
		BSFManager.registerScriptingEngine(
				"jruby", 
				"org.jruby.javasupport.bsf.JRubyEngine", 
				new String[] { "rb" }
			);
	}
	
	/**
	 * Handle some differences between JRuby's behavior and the default behaviors 
	 * supported by the other engines in use:
	 * <ol>
	 *   <li>All the registered beans are referenced using "$beanName", so we put the "$" back in the
	 * expression, before any "c4j" prefixes.</li> 
	 *   <li>Replace "null" with Ruby's "nil".</li>
	 *   <li>Replace "equals\s*(" with "eql?(". (It tries to match only methods, not variables named "equals"!)</li>
	 *   <li>Replace "compareTo\s*(" with "<=>(". (It tries to match only methods, not variables named "compareTo"!)</li>
	 * </ol>
	 * @see org.contract4j5.interpreter.bsf.BSFExpressionInterpreterAdapter#evaluateScript(java.lang.String, org.contract4j5.context.TestContext)
	 */
	protected Object evaluateScript(String testExpression, TestContext context) throws BSFException {
		String expr1 = testExpression.replaceAll("c4j", "\\$c4j");
		String expr2 = expr1.replaceAll("null", "nil");
		String expr3 = expr2.replaceAll("equals\\s*\\(", "eql\\?\\(");
		String expr  = expr3.replaceAll("compareTo\\s*\\(", "\\<=\\>\\(");
		return super.evaluateScript(expr, context);
	}

	public JRubyBSFExpressionInterpreter() throws BSFException {
		this(false, new HashMap<String,String>());
	}
	
	public JRubyBSFExpressionInterpreter(
			boolean treatEmptyTestExpressionAsValid) throws BSFException {
		this(treatEmptyTestExpressionAsValid, new HashMap<String,String>());
	}
	
	public JRubyBSFExpressionInterpreter(
			Boolean treatEmptyTestExpressionAsValid, 
			Map<String, String> optionalKeywordSubstitutions) throws BSFException {
		super("jruby", treatEmptyTestExpressionAsValid, optionalKeywordSubstitutions);
		setAllowUnrecognizedKeywords(true);
	}

	public static void main(String args[]) {
		try {
			new JRubyBSFExpressionInterpreter();
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}
}
